package com.hlink.notice.service;

import com.hlink.notice.crawler.NoticeCrawler;
import com.hlink.notice.dto.NoticeDTO;
import com.hlink.notice.entity.Notice;
import com.hlink.notice.repository.NoticeRepository;
import com.hlink.ai.service.AiSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class NoticeSyncService {

    private final NoticeCrawler crawler;
    private final NoticeRepository noticeRepository;
    private final AiSummaryService aiSummaryService;

    @Transactional
    public void syncNotices() {
        var crawled = crawler.crawlAll();
        System.out.println("🔍 크롤링 결과: " + crawled.size() + "건");

        int savedCount = 0;

        for (var dto : crawled) {
            if (noticeRepository.existsByTitleAndLink(dto.getTitle(), dto.getLink())) continue;

            var notice = Notice.builder()
                    .title(dto.getTitle())
                    .category(dto.getCategory())
                    .link(dto.getLink())
                    .summary(dto.getSummary())
                    .tags(dto.getTags() != null ? String.join(",", dto.getTags()) : null)
                    .date(parseDate(dto.getDate()))
                    .deadline(parseDate(dto.getDeadline()))
                    .build();

         // ✅ 1) 공지 저장
            notice = noticeRepository.save(notice);
            savedCount++;

            // ✅ 람다에서 쓰기 위해 복사
            final var savedNotice = notice;

            // ✅ 2) 저장 직후 즉시 AI 요약 (트랜잭션 영향 無)
            new Thread(() -> {
                try {
                    aiSummaryService.summarizeNoticeById(savedNotice.getId());
                } catch (Exception e) {
                    System.err.println("⚠️ AI 요약 실패 noticeId=" + savedNotice.getId() + " : " + e.getMessage());
                }
            }).start();
        }

        System.out.println("💾 신규 공지 " + savedCount + "건 저장 & AI 요약 즉시 시작");
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;

        try {
            return LocalDateTime.parse(dateStr);
        } catch (Exception ignored) {}

        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
            return ZonedDateTime.parse(dateStr, fmt).toLocalDateTime();
        } catch (Exception e) {
            System.err.println("⚠️ 날짜 파싱 실패 → " + dateStr);
            return null;
        }
    }
}
