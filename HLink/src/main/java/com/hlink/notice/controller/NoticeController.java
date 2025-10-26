// View 요청 처리

package com.hlink.notice.controller;

import com.hlink.notice.dto.NoticeDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class NoticeController {

    @GetMapping("/notices")
    public String listNotices(Model model) {
        // 임시 데이터 (크롤링/DB 전까지 프론트 테스트용)
        List<NoticeDTO> demo = List.of(
                new NoticeDTO(1L, "[학사] 2학기 수강정정 안내", "학사", "2025-10-20", "2025-10-30",
                        "수강정정 일정 및 유의사항 안내", List.of("수강","정정"), "#"),
                new NoticeDTO(2L, "[장학] 외부장학금 신청 공고", "장학", "2025-10-19", "2025-10-28",
                        "신청 자격 및 제출서류 공지", List.of("장학금","서류"), "#"),
                new NoticeDTO(3L, "[SW학부] 캡스톤 공지", "SW학부", "2025-10-18", "2025-11-05",
                        "팀 구성 및 주제 제출 일정", List.of("캡스톤","팀"), "#")
        );
        model.addAttribute("notices", demo);
        model.addAttribute("pageTitle", "공지 목록");
        return "notices";
    }
}
