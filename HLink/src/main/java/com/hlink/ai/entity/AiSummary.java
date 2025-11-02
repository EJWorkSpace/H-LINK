package com.hlink.ai.entity;

import com.hlink.notice.entity.Notice;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ✅ DB의 notice_id FK와 연결 (1:1 관계) */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    /** AI 요약문 */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    /** AI가 추출한 태그 */
    @Column(length = 255)
    private String tags;

    /** AI가 추출한 마감일 */
    private LocalDateTime deadline;

    /** 사용된 모델명 (예: gemini-2.5-flash) */
    @Column(length = 100)
    private String model;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
