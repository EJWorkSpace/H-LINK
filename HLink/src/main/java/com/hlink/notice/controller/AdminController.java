// com.hlink.notice.controller.AdminController
package com.hlink.notice.controller;

import com.hlink.notice.service.NoticeSyncService;
import com.hlink.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final NoticeSyncService syncService;
    private final NoticeService noticeService;

    // 🧹 신규 공지 크롤링 → DB 저장
    @PostMapping("/sync")
    public String sync() {
        syncService.syncNotices();
        return "redirect:/notices";
    }

    // 🤖 요약이 비어있는 상위 100개만 AI 분석
    @PostMapping("/analyze-missing")
    public String analyzeMissing() {
        noticeService.analyzeMissing(100);
        return "redirect:/notices";
    }
}