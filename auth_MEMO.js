const express = require('express');
const bcrypt = require('bcryptjs');
const validator = require('validator');
const db = require('./db_MEMO');
const jwt = require('jsonwebtoken');
const Redis = require('ioredis');
const { OAuth2Client } = require('google-auth-library');
const redis = new Redis();
const router = express.Router();

const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

// ✅ 회원가입
router.post('/signup', async (req, res) => {
  const { email, password } = req.body;
  console.log("post-signup 호출");

  try {
    if (!email || !password || !validator.isEmail(email) || !validator.isLength(password, { min: 6 })) {
      return res.status(400).json({ message: '유효한 이메일과 6자 이상 비밀번호를 입력해주세요.' });
    }

    const verified = await redis.get(`verify:${email}`);
    if (verified !== 'verified') {
      return res.status(403).json({ message: '이메일 인증을 먼저 완료해주세요.' });
    }

    const [existing] = await db.query('SELECT * FROM users WHERE email = ?', [email]);
    if (existing.length > 0) {
      return res.status(409).json({ message: '이미 등록된 이메일입니다.' });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    await db.query('INSERT INTO users (email, password, provider) VALUES (?, ?, ?)', [email, hashedPassword, 'email']);
    await redis.del(`verify:${email}`);

    return res.status(201).json({ message: '회원가입이 완료되었습니다.' });
  } catch (err) {
    console.error('회원가입 오류:', err.message);
    return res.status(500).json({ message: '서버 오류가 발생했습니다.' });
  }
});

// ✅ 이메일 로그인
router.post('/login', async (req, res) => {
  const { email, password } = req.body;

  try {
    if (!email || !password || !validator.isEmail(email) || !validator.isLength(password, { min: 6 })) {
      return res.status(400).json({ message: '유효한 이메일과 6자 이상 비밀번호를 입력해주세요.' });
    }

    const [users] = await db.query('SELECT * FROM users WHERE email = ?', [email]);
    const user = users[0];
    if (!user) {
      return res.status(401).json({ message: '등록되지 않은 사용자입니다.' });
    }

    if (user.provider !== 'email') {
      return res.status(403).json({ message: '이메일/비밀번호 로그인은 이 계정에 사용할 수 없습니다.' });
    }

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({ message: '비밀번호가 일치하지 않습니다.' });
    }

    const accessToken = jwt.sign(
      { id: user.id, email: user.email, role: user.role || 'user' },
      process.env.JWT_SECRET,
      { expiresIn: '15m' }
    );

    const refreshToken = jwt.sign(
      { id: user.id },
      process.env.JWT_SECRET,
      { expiresIn: '7d' }
    );

    await db.query('UPDATE users SET refresh_token = ? WHERE id = ?', [refreshToken, user.id]);

    await db.query('INSERT INTO login_logs (user_id, email, login_time, ip_address) VALUES (?, ?, ?, ?)', [
      user.id,
      user.email,
      new Date(),
      req.ip
    ]);

    return res.status(200).json({ accessToken, refreshToken });
  } catch (err) {
    console.error('로그인 오류:', err.message);
    return res.status(500).json({ message: '서버 오류가 발생했습니다.' });
  }
});

// router.post('/google-login', async (req, res) => {
//   const { idToken } = req.body;

//   if (!idToken) {
//     return res.status(400).json({ message: 'Google ID Token이 필요합니다.' });
//   }

//   try {
//     const ticket = await client.verifyIdToken({
//       idToken,
//       audience: process.env.GOOGLE_CLIENT_ID
//     });

//     const payload = ticket.getPayload();
//     const email = payload.email;
//     const name = payload.name;
//     const picture = payload.picture;
//     const googleId = payload.sub;

//     const [users] = await db.query('SELECT * FROM users WHERE email = ?', [email]);
//     let user = users[0];

//     // 이메일 중복인데 다른 로그인 방식으로 가입된 경우
//     if (user && user.provider !== 'google') {
//       return res.status(409).json({
//         message: '이미 다른 방식으로 가입된 이메일입니다.\n이메일 로그인 또는 비밀번호 재설정을 이용해 주세요.'
//       });
//     }

//     // 신규 사용자: 자동 회원가입
//     if (!user) {
//       const [result] = await db.query(
//         `INSERT INTO users 
//           (email, provider, google_id, name, picture, role, is_verified, created_at, updated_at) 
//          VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())`,
//         [email, 'google', googleId, name, picture, 'user', 1] // is_verified = 1
//       );
//       user = { id: result.insertId, email, role: 'user' };
//     }

//     // access token 생성
//     const accessToken = jwt.sign(
//       { id: user.id, email: user.email, role: user.role || 'user' },
//       process.env.JWT_SECRET,
//       { expiresIn: '15m' }
//     );

//     // refresh token 생성
//     const refreshToken = jwt.sign(
//       { id: user.id },
//       process.env.JWT_SECRET,
//       { expiresIn: '7d' }
//     );

//     // refresh token 저장
//     await db.query('UPDATE users SET refresh_token = ?, last_login = NOW() WHERE id = ?', [
//       refreshToken,
//       user.id,
//     ]);

//     return res.status(200).json({ accessToken, refreshToken });
//   } catch (err) {
//     console.error('[Google Login] 오류:', err.message);
//     return res.status(401).json({
//       message: 'Google 로그인 인증에 실패했습니다. 다시 시도해주세요.',
//     });
//   }
// });


// ✅ OAuth2 Google 로그인 (code 기반)
router.post('/google-login', async (req, res) => {
  const { code } = req.body;
  if (!code) return res.status(400).json({ message: 'Google 인증 코드(code)가 필요합니다.' });
  console.log('🔐 받은 Google 인증 코드:', code); // ✅ 인증 코드 로그 추가
  try {
    const tokenRes = await axios.post('https://oauth2.googleapis.com/token', null, {
      params: {
        code,
        client_id: process.env.GOOGLE_CLIENT_ID,
        client_secret: process.env.GOOGLE_CLIENT_SECRET,
        redirect_uri: process.env.REDIRECT_URI,
        grant_type: 'authorization_code',
      },
    });

    const idToken = tokenRes.data.id_token;
    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });

    const payload = ticket.getPayload();
    const email = payload.email;
    const name = payload.name;
    const picture = payload.picture;
    const googleId = payload.sub;

    const [users] = await db.query('SELECT * FROM users WHERE email = ?', [email]);
    let user = users[0];

    if (user && user.provider !== 'google') {
      return res.status(409).json({ message: '이미 이메일 로그인으로 가입된 계정입니다.' });
    }

    if (!user) {
      const [result] = await db.query(
        `INSERT INTO users (email, provider, google_id, name, picture, role, is_verified, created_at, updated_at)
         VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())`,
        [email, 'google', googleId, name, picture, 'user', 1]
      );
      user = { id: result.insertId, email, role: 'user' };
    }

    const accessToken = jwt.sign(
      { id: user.id, email: user.email, role: user.role || 'user' },
      process.env.JWT_SECRET,
      { expiresIn: '15m' }
    );

    const refreshToken = jwt.sign(
      { id: user.id },
      process.env.JWT_SECRET,
      { expiresIn: '7d' }
    );

    await db.query('UPDATE users SET refresh_token = ?, last_login = NOW() WHERE id = ?', [
      refreshToken,
      user.id,
    ]);

    return res.status(200).json({ accessToken, refreshToken });
  } catch (err) {
    console.error('[OAuth2 Google Login Error]', err.message);
    return res.status(500).json({ message: 'Google 로그인 처리 중 오류가 발생했습니다.' });
  }
});


// ✅ 토큰 갱신
router.post('/refresh', async (req, res) => {
  const { refreshToken } = req.body;

  if (!refreshToken) return res.status(401).json({ message: 'Refresh Token이 필요합니다.' });

  try {
    const decoded = jwt.verify(refreshToken, process.env.JWT_SECRET);
    const [users] = await db.query('SELECT * FROM users WHERE id = ?', [decoded.id]);
    const user = users[0];

    if (!user || user.refresh_token !== refreshToken) {
      return res.status(403).json({ message: '유효하지 않은 Refresh Token입니다.' });
    }

    const newAccessToken = jwt.sign(
      { id: user.id, email: user.email, role: user.role },
      process.env.JWT_SECRET,
      { expiresIn: '15m' }
    );

    return res.status(200).json({ accessToken: newAccessToken });
  } catch (err) {
    return res.status(403).json({ message: '토큰이 만료되었거나 유효하지 않습니다.' });
  }
});

// ✅ 프로필 조회
router.get('/profile', verifyToken, async (req, res) => {
  try {
    const [users] = await db.query('SELECT id, email, role, name, picture, created_at FROM users WHERE id = ?', [req.user.id]);
    const user = users[0];
    if (!user) return res.status(404).json({ message: '사용자를 찾을 수 없습니다.' });
    return res.status(200).json(user);
  } catch (err) {
    console.error('프로필 조회 오류:', err.message);
    return res.status(500).json({ message: '서버 오류가 발생했습니다.' });
  }
});

// ✅ 로그아웃
router.post('/logout', verifyToken, async (req, res) => {
  try {
    await db.query('UPDATE users SET refresh_token = NULL WHERE id = ?', [req.user.id]);
    return res.status(200).json({ message: '로그아웃 되었습니다.' });
  } catch (err) {
    console.error('로그아웃 오류:', err.message);
    return res.status(500).json({ message: '서버 오류가 발생했습니다.' });
  }
});

// ✅ 이메일 중복 확인
router.post('/check-email', async (req, res) => {
  const { email } = req.body;

  if (!email) {
    return res.status(400).json({ message: '이메일을 입력하세요.' });
  }

  try {
    const [rows] = await db.query('SELECT id FROM users WHERE email = ?', [email]);
    return res.json({ isDuplicate: rows.length > 0 });
  } catch (err) {
    console.error('이메일 중복 확인 오류:', err.message);
    return res.status(500).json({ message: '서버 오류' });
  }
});

// ✅ 비밀번호 재설정
router.post('/reset-password', async (req, res) => {
  const { email, newPassword } = req.body;
  console.log(`🔐 Reset Password 요청: email=${email}, newPassword=${newPassword}`);

  try {
    if (!email || !newPassword || !validator.isEmail(email) || !validator.isLength(newPassword, { min: 6 })) {
      return res.status(400).json({ message: '유효한 이메일과 6자 이상 새 비밀번호를 입력해주세요.' });
    }

    const verified = await redis.get(`verify:${email}`);
    if (verified !== 'verified') {
      return res.status(403).json({ message: '이메일 인증이 필요합니다.' });
    }

    const [users] = await db.query('SELECT id FROM users WHERE email = ?', [email]);
    const user = users[0];
    if (!user) {
      return res.status(404).json({ message: '등록되지 않은 이메일입니다.' });
    }

    const hashedPassword = await bcrypt.hash(newPassword, 10);
    await db.query('UPDATE users SET password = ?, refresh_token = NULL WHERE email = ?', [hashedPassword, email]);

    await redis.del(`verify:${email}`);

    return res.status(200).json({ message: '비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요.' });
  } catch (err) {
    console.error('비밀번호 재설정 오류:', err.message);
    return res.status(500).json({ message: '서버 오류가 발생했습니다.' });
  }
});

// ✅ 토큰 인증 미들웨어
function verifyToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.status(401).json({ message: '토큰이 필요합니다.' });

  jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
    if (err) return res.status(403).json({ message: '유효하지 않은 토큰입니다.' });
    req.user = user;
    next();
  });
}

module.exports = router;