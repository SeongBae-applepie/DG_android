const express = require('express');
const cors = require('cors');
const authRoutes = require('./auth'); // 경로가 맞는지 확인
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

// const authRoutes = require('./auth.js'); // 위 코드가 auth.js라면
app.use('/api', authRoutes);

const PORT = process.env.PORT || 3000;
const SERVER_URL = process.env.SERVER_URL;


app.get('/get', (req, res) => {
  res.json({ message: 'GET 요청 성공!' });
});

app.listen(PORT, () => {
  console.log(`Server running on ${SERVER_URL}:${PORT}`);
});