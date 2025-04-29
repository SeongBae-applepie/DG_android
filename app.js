const express = require('express');
const cors = require('cors');
const authRoutes = require('./auth'); // 경로가 맞는지 확인
const emailAuthRouter = require('./email_auth');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());
// 이렇게 경로 분리
app.use('/api', authRoutes);
app.use('/api/m/', emailAuthRouter);

app.get('/get', (req, res) => {
    res.json({ message: 'GET 요청 성공!' });
});


const PORT = process.env.PORT || 3000;
const SERVER_URL = process.env.SERVER_URL;
app.listen(PORT, () => {
  console.log(`Server running on ${SERVER_URL}:${PORT}`);
});


// ... --- ...