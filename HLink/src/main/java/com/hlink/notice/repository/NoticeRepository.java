package com.hlink.notice.repository;

import com.hlink.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    boolean existsByTitleAndLink(String title, String link);

    // ✅ AI 요약 테이블(ai_summaries)과 join
    @Query("""
    	    SELECT n.id, n.title, n.category, n.link, n.date,
    	           COALESCE(a.summary, n.summary),
    	           COALESCE(a.tags, n.tags),
    	           COALESCE(a.deadline, n.deadline)
    	    FROM Notice n
    	    LEFT JOIN AiSummary a ON a.notice.id = n.id
    	    ORDER BY n.date DESC
    	""")
    	List<Object[]> findAllWithAiSummary();
}
