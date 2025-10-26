// ✅ 남겨둘 컨트롤러 (DB 기반)
package com.hlink.notice.controller;

import com.hlink.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class NoticesPageController {
    private final NoticeRepository noticeRepository;

    @GetMapping({"/", "/notices"})
    public String noticesPage(Model model) {
        model.addAttribute("pageTitle", "공지");
        model.addAttribute("activeTab", "notices");
        model.addAttribute("notices", noticeRepository.findAll());
        return "notices";
    }
}
