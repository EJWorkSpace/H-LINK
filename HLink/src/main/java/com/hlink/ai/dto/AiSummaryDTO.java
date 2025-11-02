package com.hlink.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiSummaryDTO {
    private String title;
    private String summary;
    private String tags;
    private String deadline; // 문자열로 받은 뒤, 필요 시 LocalDate로 변환
}
