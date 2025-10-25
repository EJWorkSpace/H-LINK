# 🌐 H-Link

> **AI 기반 한림대학교 통합 공지 플랫폼**  
> 공지 확인 피로도를 줄이는, 지능형 공지 요약·정리 서비스

**🗓 기간**: 2025년 10월 25일 ~  
**🏫 과정**: 한림대학교 소프트웨어학부 전시회 출품 프로젝트  

---

## ✨ 프로젝트 개요

**H-Link**는 한림대학교의 학사·장학·SW학부 등  
여러 게시판에 흩어진 공지사항을 **한 곳에 통합**하고,  
AI를 통해 내용을 **자동 요약·태그화·마감 표시(D-Day)** 하는 서비스입니다.

> 학교 홈페이지를 매번 들어가지 않아도,  
> “한눈에 핵심만 보는” 공지 확인 경험을 제공합니다.

### 📌 주요 특징
- 여러 게시판 공지 통합  
- AI 핵심 요약 및 키워드 태그  
- 마감일 자동 인식 및 D-Day 표시  
- 공지 즐겨찾기 및 필터링  
- 카테고리별 컬러 테마 UI  

---

## 👥 팀 소개

**팀명**: E&J  
**구성**: 2인의 풀스택 웹 개발자  

| [<img src="https://avatars.githubusercontent.com/wldms-04" width="100px;" style="border-radius:50%;">](https://github.com/wldms-04) | [<img src="https://avatars.githubusercontent.com/Chae0227" width="100px;" style="border-radius:50%;">](https://github.com/Chae0227) |
|:--:|:--:|
| **최지은 (팀장)** <br> 빅데이터 | **김은채** <br> 콘텐츠IT |

---

## 🛠 기술 스택

| 영역 | 기술 |
|------|------|
| **Frontend** | HTML5, CSS3, JavaScript, Thymeleaf, Bootstrap |
| **Backend** | Spring Boot 3.x, Java 17, Spring MVC, Spring Data JPA |
| **Database** | MySQL |
| **AI 기능** | Google Gemini API |
| **크롤링/자동화** | Jsoup, Spring Scheduler |
| **협업** | Git + GitHub (Fork → Pull Request → Merge) |
| **IDE** | Spring Tool Suite 4 |
| **Build** | Maven |

---

## 📌 주요 기능 요약

### 📰 공지 통합  
- 학사·장학·SW학부 게시판 크롤링  
- 최신순 정렬 + 카테고리 필터링  

### 🧠 AI 요약 & 자동 태그  
- Gemini API로 공지 핵심 요약 (2~3줄)  
- 주요 키워드 자동 태그 (#장학금 #휴학 #SW)  

### ⏰ D-Day 자동 인식  
- ‘~일까지’, ‘마감’ 등 문구 자동 추출 → D-3, D-1 표시  
- 마감 임박 공지 강조 표시  

### ⭐ 즐겨찾기  
- 중요 공지를 북마크하여 별도 페이지에서 관리  

### 🎨 카테고리별 색상 테마  
- 학사: 파랑 / 장학: 주황 / SW학부: 보라  
- 정보 구분 및 시각적 완성도 향상  

---

## 🤝 협업 규칙 (Collaboration Guide)

### 💬 Message Format
| Message Type | Description | Example |
|:-------------:|:------------:|:------------:|
| **Issue** | `<수정사항>` 형식으로 작성 | `<수정사항> AI 요약 기능 오류 수정` |
| **Commit** | `#이슈번호 [키워드] 수정내용` | `#3 [Feat] AI 요약 기능 추가` |
| **Pull Request** | `#이슈번호 [키워드] 수정내용` | `#3 [Fix] 크롤링 오류 해결` |

---

### 🔑 Keyword Type
| 태그 | 설명 |
|:----:|:----:|
| ✨ Feat | 새로운 기능 추가 |
| 🐛 Fix | 버그 수정 |
| 🚑 HOTFIX | 치명적 버그 수정 |
| 📁 Build | 빌드 관련 파일 수정 |
| 🎨 Design | CSS/UI 디자인 변경 |
| 📄 Docs | 문서(README 등) 수정 |
| 📝 Test | 테스트 코드 추가/수정 |
| ♻️ Rename | 파일/폴더명 수정 |
| 🔥 Remove | 파일/폴더 삭제 |

---

### 🌿 Branch Convention

#### 🧩 구조
- **main** : 배포용 (항상 안정 상태 유지)  
- **develop** : 통합 개발 브랜치  
- **feature/** : 기능 개발 단위 (예: `feature/ai-summary`)  

#### 🔁 Flow
```plaintext
Main Branch
▲
└── Develop Branch ── 테스트 완료 후 병합
▲
└── Feature Branch ── 기능 단위 작업
└── 완료 후 병합
```


#### ⚙️ RuleSet

Reviewer(상대 팀원) 1인 이상 승인 시 PR 머지 가능

PR에 커밋 추가 시, 이전 승인 무효 → 재검토 필요

main 브랜치는 팀장만 머지 가능


---

## 🌱 브랜치 전략 예시

| 브랜치명 | 설명 |
|-----------|--------|
| `main` | 배포용 |
| `develop` | 개발 통합 |
| `feature/crawling` | 공지 크롤링 기능 개발 |
| `feature/ai-summary` | AI 요약 기능 개발 |
| `feature/ui-update` | 프론트 디자인 업데이트 |

---

## 📦 프로젝트 구조
```plaintext
com.hlink.notice
├── controller
│ ├── NoticeController.java
│ └── ApiController.java
│
├── service
│ ├── NoticeService.java
│ ├── AiService.java
│ └── CrawlService.java
│
├── repository
│ └── NoticeRepository.java
│
├── entity
│ └── Notice.java
│
├── scheduler
│ └── NoticeScheduler.java
│
├── config
│ └── WebConfig.java
│
└── HLinkApplication.java
```


---

## 📜 License
MIT License © 2025 Team E&J  

---

## 🪴 슬로건
> “학교 공지를 한 눈에 — **H-Link** 🌐”  
> 한림의 모든 정보를 하나의 링크로 연결하다.
