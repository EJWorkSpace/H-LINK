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

-- 테스트용 데이터
INSERT INTO notices (title, link, date, category, summary, tags)
VALUES
('테스트 공지 1', 'https://www.hallym.ac.kr/bbs/test/1', NOW(), '학사공지', '테스트용 공지입니다.', '테스트,학사'),
('테스트 공지 2', 'https://www.hallym.ac.kr/bbs/test/2', NOW(), '장학/등록공지', '장학 테스트 공지입니다.', '장학금,등록');

INSERT INTO favorites (notice_id, user_id)
VALUES (1, 1);
