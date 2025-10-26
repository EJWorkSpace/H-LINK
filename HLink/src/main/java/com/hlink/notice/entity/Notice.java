// DB 테이블 매핑
package com.hlink.notice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;
    private LocalDate date;
    private LocalDate deadline;

    @Column(length = 500)
    private String summary;

    private String link;

    // Getter/Setter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}
