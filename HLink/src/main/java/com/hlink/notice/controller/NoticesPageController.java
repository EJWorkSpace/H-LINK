// 파일: com/hlink/notice/controller/NoticesPageController.java
package com.hlink.notice.controller;

import com.hlink.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui") // ✅ prefix 추가
public class NoticesPageController {

    private final NoticeService noticeService;

    @GetMapping("/notices") // 최종 URL: /ui/notices
    public String noticesPage(Model model) {
        model.addAttribute("notices", noticeService.findAll());
        return "notices/list";
    }
}
