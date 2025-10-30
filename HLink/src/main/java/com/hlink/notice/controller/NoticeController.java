package com.hlink.notice.controller;

import com.hlink.notice.dto.NoticeDTO;
import com.hlink.notice.entity.Notice;
import com.hlink.notice.service.AiService;
import com.hlink.notice.service.CrawlAiFacade;
import com.hlink.notice.service.NoticeService;
import com.hlink.notice.service.NoticeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;
    private final AiService aiService;
    private final NoticeSyncService noticeSyncService; // ✅ DB 동기화용
    private final CrawlAiFacade crawlAiFacade;         // ✅ 크롤링+AI 실시간 조합용

    /**
     * ✅ 1️⃣ DB에 저장된 공지 목록 보기
     * - summary / tags가 DB에 있으면 그대로 출력
     */
    @GetMapping
    public String listFromDb(Model model) {
        List<Notice> notices = noticeService.findAll();

        // 날짜 최신순 정렬
        notices.sort(Comparator.comparing(Notice::getDate,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        model.addAttribute("notices", notices);
        model.addAttribute("source", "db");
        return "notices/list";
    }

    /**
     * ✅ 2️⃣ 최신 공지 크롤링 + AI 요약/태그를 합쳐 화면에만 표시 (DB 저장 X)
     * 예: /notices/live?limit=10
     */
    @GetMapping("/live")
    public String liveAi(@RequestParam(defaultValue = "10") int limit, Model model) {
        List<NoticeDTO> notices = crawlAiFacade.crawlWithAi(limit);
        model.addAttribute("notices", notices);
        model.addAttribute("source", "live");
        return "notices/list";
    }

    /**
     * ✅ 3️⃣ 공지 DB 동기화 (크롤링 결과를 저장)
     */
    @PostMapping("/sync")
    public String syncNotices(RedirectAttributes ra) {
        noticeSyncService.syncNotices();
        ra.addFlashAttribute("toast", "공지 DB 동기화 완료 ✅");
        return "redirect:/notices";
    }

    /**
     * ✅ 4️⃣ 단건 요약 실행 (공지 상세에서 버튼 클릭 시)
     */
    @PostMapping("/{id}/analyze")
    public String analyzeOne(@PathVariable Long id, RedirectAttributes ra) {
        noticeService.analyzeAndSave(id);
        ra.addFlashAttribute("toast", "공지 " + id + " 요약 완료 🤖");
        return "redirect:/notices";
    }

    /**
     * ✅ 5️⃣ 일괄 요약 실행 (상단 버튼)
     */
    @PostMapping("/analyze-all")
    public String analyzeAll(@RequestParam(defaultValue = "20") int limit, RedirectAttributes ra) {
        int processed = noticeService.analyzeAll(limit);
        ra.addFlashAttribute("toast", processed + "건 일괄 요약 완료 🚀");
        return "redirect:/notices";
    }
}
