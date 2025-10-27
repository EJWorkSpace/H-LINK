package com.hlink.notice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "notices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 공지 제목 (크롤링 타이틀) */
    @Column(nullable = false, length = 500)
    private String title;

    /** 원문 링크(URL) */
    @Column(nullable = false, length = 1000)
    private String link;

    /** 공지 게시일(원문 기준) */
    @Column(nullable = false)
    private LocalDateTime date;

    /** 분류(학교/학사/장학/모집 등) */
    @Column(nullable = false, length = 100)
    private String category;

    /** 마감일(있을 때만) */
    private java.time.LocalDateTime deadline;

    /** ✅ Gemini 요약 결과 저장 */
    @Column(columnDefinition = "TEXT")
    private String summary;

    /** ✅ Gemini 태그 결과 저장 (쉼표 구분: 예) 장학,모집,마감임박) */
    @Column(length = 1000)
    private String tags;

    /** 생성 시각 */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 수정 시각 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* ===== 편의 메서드 (선택) ===== */

    /** 쉼표로 저장되는 tags를 리스트로 가져오기 */
    @Transient
    public List<String> getTagList() {
        if (tags == null || tags.isBlank()) return List.of();
        return Arrays.stream(tags.split("\\s*,\\s*"))
                .filter(s -> !s.isBlank())
                .toList();
    }

    /** 리스트 → 쉼표 문자열로 저장하기 */
    public void setTagList(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            this.tags = null;
        } else {
            this.tags = String.join(",", tagList);
        }
    }
}