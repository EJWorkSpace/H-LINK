// View 요청 처리

package com.hlink.notice.controller;

import com.hlink.notice.dto.NoticeDTO;
import com.hlink.notice.service.NoticeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

//✅ 공지 페이지
@Controller
public class NoticeController {
 private final NoticeService noticeService;

 public NoticeController(NoticeService noticeService) {
     this.noticeService = noticeService;
 }

 @GetMapping("/notices")
 public String listNotices(Model model) {
     List<NoticeDTO> notices = noticeService.getAllNotices();
     model.addAttribute("notices", notices);
     model.addAttribute("pageTitle", "한림대 공지");
     model.addAttribute("activeTab", "notices"); // 💡 layout용
     return "notices";
 }
}
