package com.hlink.notice.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 1000)
    private String link;

    @Column(nullable = false)
    private java.time.LocalDateTime date;

    @Column(nullable = false, length = 100)
    private String category;

    private java.time.LocalDateTime deadline;

    @Column(columnDefinition = "TEXT")
    private String summary;

    // 쉼표로 저장 (예: "장학,모집")
    private String tags;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = java.time.LocalDateTime.now();
    }
}
