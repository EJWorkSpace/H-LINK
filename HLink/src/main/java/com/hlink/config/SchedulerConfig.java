package com.hlink.config;

import com.hlink.ai.service.AiSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final AiSummaryService aiSummaryService;

    /**
     * 🧩 10분마다 요약 안 된 공지 자동 요약
     */
    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다
    public void backfillSummaries() {
        try {
            int n = aiSummaryService.summarizeNewNotices();
            if (n > 0) System.out.println("🧩 백필 요약 " + n + "건 처리 완료");
        } catch (Exception e) {
            System.err.println("⚠️ 백필 요약 실패: " + e.getMessage());
        }
    }
}
