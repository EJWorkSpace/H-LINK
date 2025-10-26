package com.hlink.notice.service;

import com.hlink.notice.crawler.NoticeCrawler;
import com.hlink.notice.dto.NoticeDTO;
import com.hlink.notice.entity.Notice;
import com.hlink.notice.repository.NoticeRepository;
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

    @Transactional
    public void syncNotices() {
        List<NoticeDTO> crawled = crawler.crawlAll();
        System.out.println("🔍 크롤링 결과: " + crawled.size() + "건");

        int savedCount = 0;
        for (NoticeDTO dto : crawled) {
            // 중복 여부 확인 (title + link 조합으로 판별)
            boolean exists = noticeRepository.existsByTitleAndLink(dto.getTitle(), dto.getLink());
            if (exists) continue; // 이미 존재하면 skip

            Notice notice = Notice.builder()
                    .title(dto.getTitle())
                    .category(dto.getCategory())
                    .link(dto.getLink())
                    .summary(dto.getSummary())
                    .tags(dto.getTags() != null ? String.join(",", dto.getTags()) : null)
                    .date(parseDate(dto.getDate()))
                    .deadline(parseDate(dto.getDeadline()))
                    .build();

            noticeRepository.save(notice);
            savedCount++;
        }

        System.out.println("💾 신규 공지 " + savedCount + "건 저장 완료 (중복은 건너뜀)");
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return LocalDateTime.now();
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            return ZonedDateTime.parse(dateStr, fmt).toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
