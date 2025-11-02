-- H-Link database schema
-- Created: 2025-10-26
-- Author: Team H-Link

DROP DATABASE IF EXISTS hlink;
CREATE DATABASE hlink CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE hlink;

-- 공지사항 테이블
CREATE TABLE notices (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(500) NOT NULL,
  link VARCHAR(1000) NOT NULL,
  date DATETIME NOT NULL,
  category VARCHAR(100) NOT NULL,
  deadline DATETIME NULL,
  summary TEXT NULL,
  tags VARCHAR(255) NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 즐겨찾기 테이블
CREATE TABLE favorites (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  notice_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_fav_notice FOREIGN KEY (notice_id) REFERENCES notices(id) ON DELETE CASCADE
);

-- AI 요약 및 분석 결과 테이블
CREATE TABLE ai_summaries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  notice_id BIGINT NOT NULL,       -- 연결된 공지 ID
  summary TEXT NOT NULL,           -- AI 요약문
  tags VARCHAR(255) NULL,          -- AI가 추출한 태그
  deadline DATETIME NULL,          -- AI가 추출한 마감일
  model VARCHAR(100) NULL,         -- 사용된 모델명 (예: gemini-2.5-flash)
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_summary_notice FOREIGN KEY (notice_id)
    REFERENCES notices(id) ON DELETE CASCADE
);
