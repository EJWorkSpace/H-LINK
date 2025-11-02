// com.hlink.ai.controller.AiRunController
package com.hlink.ai.controller;

import com.hlink.ai.service.AiSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiRunController {

  private final AiSummaryService aiSummaryService;

  // 수동 실행 트리거
  @GetMapping("/run")
  public String runAiSummarizer() {
      int count = aiSummaryService.summarizeNewNotices();
      return "✅ 요약 완료: " + count + "건";
  }
}
