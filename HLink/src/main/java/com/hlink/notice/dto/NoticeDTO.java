// 데이터 전달 전용

package com.hlink.notice.dto;

import java.util.List;

public class NoticeDTO {
    private Long id;
    private String title;
    private String category;
    private String date;
    private String deadline;
    private String summary;
    private List<String> tags;
    private String link;

    // 생성자
    public NoticeDTO(Long id, String title, String category, String date, String deadline, String summary, List<String> tags, String link) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.date = date;
        this.deadline = deadline;
        this.summary = summary;
        this.tags = tags;
        this.link = link;
    }

    // Getter/Setter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getDeadline() { return deadline; }
    public String getSummary() { return summary; }
    public List<String> getTags() { return tags; }
    public String getLink() { return link; }
}
