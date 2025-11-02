package com.hlink.notice.controller;

import com.hlink.notice.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // POST 요청으로 텍스트 요약
    @PostMapping("/summary")
    public String summarize(@RequestBody String text) {
        return aiService.summarize(text);
    }
}