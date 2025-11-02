package com.hlink.ai.controller;

import com.hlink.ai.service.AiSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiSummaryService aiSummaryService;

    // ✅ 전체 공지 중 아직 요약 안 된 항목 자동 요약 + DB 저장
    @PostMapping("/summarize")
    public String summarizeAll() {
        int count = aiSummaryService.summarizeNewNotices();
        return "✅ AI 요약 완료 (" + count + "건)";
    }

    // ✅ 개별 공지 ID로 요약하기 (테스트용)
    @PostMapping("/summarize/{noticeId}")
    public String summarizeSingle(@PathVariable Long noticeId) {
        aiSummaryService.summarizeNoticeById(noticeId);
        return "✅ 공지 ID " + noticeId + " 요약 완료";
    }
}
