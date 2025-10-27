// 데이터 전달 전용

package com.hlink.notice.dto;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDTO {
    private Long id;
    private String title;
    private String link;
    private LocalDateTime date;
    private String category;
    private LocalDateTime deadline;
    private String summary;
    private List<String> tags;

    // 생성자
    public NoticeDTO(Long id, String title, String category, LocalDateTime date, LocalDateTime deadline, String summary, List<String> tags, String link) {
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
    public LocalDateTime getDate() { return date; }
    public LocalDateTime getDeadline() { return deadline; }
    public String getSummary() { return summary; }
    public List<String> getTags() { return tags; }
    public String getLink() { return link; }
}
