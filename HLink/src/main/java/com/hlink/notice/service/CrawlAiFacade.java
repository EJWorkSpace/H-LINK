package com.hlink.notice.service;

import com.hlink.notice.crawler.NoticeCrawler;
import com.hlink.notice.dto.NoticeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrawlAiFacade {

    private final NoticeCrawler crawler;
    private final AiService aiService;

    /**
     * 크롤링한 공지에 AI 요약/태그를 얹어서 반환 (저장 X, 화면용)
     */
    public List<NoticeDTO> crawlWithAi(int limit) {
        List<NoticeDTO> raw = crawler.crawlAll();
        List<NoticeDTO> out = new ArrayList<>();

        int count = 0;
        for (NoticeDTO dto : raw) {
            if (limit > 0 && count >= limit) break;

            // 요약
            String summary;
            try {
                summary = aiService.summarize(dto.getTitle());
            } catch (Exception e) {
                summary = "[요약 실패] " + dto.getTitle();
            }
            dto.setSummary(summary);

            // 태그
            try {
                List<String> tags = aiService.extractTags(dto.getTitle(), summary);
                dto.setTags(tags);
            } catch (Exception ignore) {
                dto.setTags(null);
            }

            out.add(dto);
            count++;

            // 너무 빠른 호출 방지 (옵션)
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }
        return out;
    }
}