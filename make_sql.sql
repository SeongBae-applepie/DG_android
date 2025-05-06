-- 1. 데이터베이스 생성
CREATE DATABASE MEMO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 2. 데이터베이스 사용
USE MEMO;

-- 3. 사용자 테이블 생성 (확장 필드 포함)
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'user',
    is_verified TINYINT(1) DEFAULT 0,
    refresh_token TEXT,
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);