package com.hlink.notice.controller;

import com.hlink.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class NoticesPageController {

    private final NoticeRepository noticeRepository;

    @GetMapping({"/", "/notices"})
    public String noticesPage(Model model) {
        List<Object[]> raw = noticeRepository.findAllWithAiSummary();
        List<Map<String, Object>> notices = new ArrayList<>();

        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("title", row[1]);
            map.put("category", row[2]);
            map.put("link", row[3]);
            map.put("date", row[4]);
            map.put("summary", row[5] != null ? row[5] : "요약 준비 중...");
            map.put("tags", row[6] != null ? List.of(row[6].toString().split(",")) : List.of());
            map.put("deadline", row[7]);
            notices.add(map);
        }

        model.addAttribute("pageTitle", "공지");
        model.addAttribute("activeTab", "notices");
        model.addAttribute("notices", notices);

        return "notices";
    }
}
