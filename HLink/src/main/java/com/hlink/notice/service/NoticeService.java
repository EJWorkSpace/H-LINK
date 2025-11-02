package com.hlink.notice.service;

import com.hlink.notice.crawler.NoticeCrawler;
import com.hlink.notice.dto.NoticeDTO;
import com.hlink.notice.entity.Notice;
import com.hlink.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeCrawler crawler;
    private final NoticeRepository noticeRepository;
    private final AiService aiService;

    /**
     * ✅ 1️⃣ 크롤링으로 공지 가져오기 (DB 저장 X)
     */
    public List<NoticeDTO> getAllNotices() {
        return crawler.crawlAll();
    }

    /**
     * ✅ 2️⃣ 신규 공지 크롤링 후 DB 저장 (중복 제외)
     */
    @Transactional
    public int syncToDatabase() {
        List<NoticeDTO> crawled = crawler.crawlAll();
        int savedCount = 0;

        for (NoticeDTO dto : crawled) {
            boolean exists = noticeRepository.existsByTitleAndLink(dto.getTitle(), dto.getLink());
            if (!exists) {
                Notice n = Notice.builder()
                        .title(dto.getTitle())
                        .link(dto.getLink())
                        .date(dto.getDate())
                        .category(dto.getCategory())
                        .deadline(dto.getDeadline())
                        .summary(null)
                        .tags(null)
                        .aiUpdatedAt(null)
                        .build();
                noticeRepository.save(n);
                savedCount++;
            }
        }

        System.out.println("💾 신규 공지 " + savedCount + "건 저장 완료 (중복은 건너뜀)");
        return savedCount;
    }

    /**
     * ✅ 3️⃣ 개별 공지에 Gemini AI 요약 및 태그 적용
     */
    @Transactional
    public Notice analyzeAndSave(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + id));

        String summary = aiService.summarize(notice.getTitle());
        List<String> tags = aiService.extractTags(notice.getTitle(), fetchContent(notice));

        notice.setSummary(summary);
        notice.setTags(tags); // ✅ setTagList → setTags 로 변경
        notice.setAiUpdatedAt(LocalDateTime.now()); // ✅ 분석 시각 기록

        noticeRepository.save(notice);
        System.out.println("🤖 [" + id + "] AI 요약 및 태그 적용 완료");
        return notice;
    }

    /**
     * ✅ 4️⃣ 모든 공지에 대해 AI 분석 일괄 적용 (최대 limit개)
     */
    @Transactional
    public int analyzeAll(int limit) {
        List<Notice> all = noticeRepository.findAll();
        int count = 0;

        for (Notice notice : all) {
            if (limit > 0 && count >= limit) break;

            String summary = aiService.summarize(notice.getTitle());
            List<String> tags = aiService.extractTags(notice.getTitle(), fetchContent(notice));

            notice.setSummary(summary);
            notice.setTags(tags); // ✅ 수정된 필드명
            notice.setAiUpdatedAt(LocalDateTime.now()); // ✅ 분석 시간 저장
            noticeRepository.save(notice);

            count++;

            // 너무 빠른 호출 방지 (Gemini API 제한 대비)
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {}
        }

        System.out.println("✅ 총 " + count + "개 공지에 AI 요약/태그 적용 완료");
        return count;
    }
 // com.hlink.notice.service.NoticeService 에 메서드 추가
    public int analyzeMissing(int limit) {
        var targets = noticeRepository.findTop100BySummaryIsNullOrderByDateDesc(); // 메서드명 정확!
        int count = 0;
        for (var notice : targets) {
            if (limit > 0 && count >= limit) break;
            var summary = aiService.summarize(notice.getTitle());
            var tags = aiService.extractTags(notice.getTitle(), summary);
            notice.setSummary(summary);
            notice.setTags(tags);              // Notice에 List<String> 필드면 join해서 저장
            notice.setAiUpdatedAt(LocalDateTime.now());
            noticeRepository.save(notice);
            count++;
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }
        return count;
    }

    /**
     * ✅ 5️⃣ 공지 내용 가져오기 (실제 본문을 크롤링하거나 DB에 추가 가능)
     * 현재는 제목 기반 간단한 placeholder로 대체
     */
    private String fetchContent(Notice notice) {
        return notice.getTitle() + " 관련 공지입니다. " +
               "상세 내용은 " + notice.getLink() + " 페이지를 참고하세요.";
    }

    /**
     * ✅ 6️⃣ 전체 공지 조회 (템플릿 전달용)
     */
    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }
}