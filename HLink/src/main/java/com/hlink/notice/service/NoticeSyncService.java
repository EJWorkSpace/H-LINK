package com.hlink.notice.service;

import com.hlink.notice.crawler.NoticeCrawler;
import com.hlink.notice.dto.NoticeDTO;
import com.hlink.notice.entity.Notice;
import com.hlink.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeSyncService {

    private final NoticeCrawler crawler;
    private final NoticeRepository noticeRepository;
    private final AiService aiService; // ✅ AI 요약/태그용

    @Transactional
    public void syncNotices() {
        List<NoticeDTO> crawled = crawler.crawlAll();
        System.out.println("🔍 크롤링 결과: " + crawled.size() + "건");

        int savedCount = 0;

        for (NoticeDTO dto : crawled) {
            // 중복(title + link) 방지
            if (noticeRepository.existsByTitleAndLink(dto.getTitle(), dto.getLink())) {
                continue;
            }

            // 날짜 null-safe
            LocalDateTime safeDate = (dto.getDate() != null) ? dto.getDate() : LocalDateTime.now();
            LocalDateTime safeDeadline = dto.getDeadline(); // null 허용

            // ✅ 요약 확보 (dto에 없으면 AI로 생성)
            String summary = dto.getSummary();
            if (summary == null || summary.isBlank()) {
                try {
                    summary = aiService.summarize(dto.getTitle());
                } catch (Exception e) {
                    summary = "[요약 실패] " + dto.getTitle();
                }
            }

            // ✅ 태그는 List<String>으로 맞춤
            List<String> tagList = dto.getTags();
            if (tagList == null || tagList.isEmpty()) {
                try {
                    List<String> aiTags = aiService.extractTags(dto.getTitle(), summary);
                    tagList = (aiTags != null) ? aiTags : Collections.emptyList();
                } catch (Exception ignore) {
                    tagList = Collections.emptyList();
                }
            }

            Notice notice = Notice.builder()
                    .title(dto.getTitle())
                    .category(dto.getCategory())
                    .link(dto.getLink())
                    .summary(summary)
                    .tags(tagList)          // ✅ List<String>으로 저장
                    .date(safeDate)
                    .deadline(safeDeadline)
                    .build();

            noticeRepository.save(notice);
            savedCount++;
        }

        System.out.println("💾 신규 공지 " + savedCount + "건 저장 완료 (중복은 건너뜀)");
    }
}