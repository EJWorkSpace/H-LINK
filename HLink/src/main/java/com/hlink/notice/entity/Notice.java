package com.hlink.notice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Notice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)       // 제목은 필수
    private String title;

    private String category;

    @Column(nullable = false)       // 링크는 필수
    private String link;

    @Column(nullable = false)       // 게시일은 필수(크롤링 시 null이면 now 로 대체 권장)
    private LocalDateTime date;

    private LocalDateTime deadline; // 마감일은 옵션

    // ✅ AI 요약문
    @Column(length = 1500)
    private String summary;

    // ✅ 태그: 다대일 별도 테이블로 보관 (notice_tags)
    @ElementCollection
    @CollectionTable(name = "notice_tags", joinColumns = @JoinColumn(name = "notice_id"))
    @Column(name = "tag")
    private List<String> tags;

    // ✅ 마지막 AI 분석 시각
    private LocalDateTime aiUpdatedAt;
}