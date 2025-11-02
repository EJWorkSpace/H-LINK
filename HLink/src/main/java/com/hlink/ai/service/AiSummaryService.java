package com.hlink.ai.service;

import com.hlink.ai.core.AiSummarizer;
import com.hlink.ai.entity.AiSummary;
import com.hlink.ai.repository.AiSummaryRepository;
import com.hlink.notice.entity.Notice;
import com.hlink.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiSummaryService {

    private final NoticeRepository noticeRepository;
    private final AiSummaryRepository aiSummaryRepository;

    /**
     * ✅ (1) 새 트랜잭션으로 요약 실행 — Notice 저장 후 rollback 방지용
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void summarizeNoticeByIdTxNew(Long noticeId) {
        summarizeNoticeById(noticeId);
    }

    /**
     * ✅ (2) 단일 공지 요약 — 자동 호출 및 수동 테스트용
     */
    public void summarizeNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElse(null);
        if (notice == null) return;

        System.out.println("🧠 [AI] 요약 시작 → " + notice.getTitle());

        var result = AiSummarizer.summarize(notice.getTitle(), notice.getSummary());

        AiSummary summary = AiSummary.builder()
                .notice(notice)
                .summary(result.summary())
                .tags(result.tags())
                .deadline(parseDeadline(result.deadline()))
                .model("gemini-2.5-flash")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        aiSummaryRepository.save(summary);

        System.out.println("✅ [AI] 요약 완료 → noticeId=" + noticeId);
    }

    /**
     * ✅ (3) 아직 요약 안 된 모든 공지 일괄 처리 (스케줄러/관리용)
     */
    @Transactional
    public int summarizeNewNotices() {
        List<Notice> unsummarized = noticeRepository.findAll().stream()
                .filter(n -> !aiSummaryRepository.findByNoticeId(n.getId()).isPresent())
                .toList();

        int count = 0;
        for (Notice notice : unsummarized) {
            summarizeNoticeById(notice.getId());
            count++;
        }

        System.out.println("💡 새 공지 요약 처리 완료 (" + count + "건)");
        return count;
    }

    /**
     * ✅ (4) 문자열 형태의 날짜 → LocalDateTime 변환
     */
    private LocalDateTime parseDeadline(String str) {
        try {
            if (str == null || str.isBlank()) return null;
            // 예: "2025-11-05 16:00" or "2025-11-05T16:00"
            return LocalDateTime.parse(str.trim().replace(" ", "T"));
        } catch (Exception e) {
            return null;
        }
    }
}
