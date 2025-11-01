package com.hlink.notice.repository;

import java.util.List;
import com.hlink.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
	boolean existsByTitleAndLink(String title, String link);
	List<Notice> findTop100BySummaryIsNullOrderByDateDesc();
	List<Notice> findByTagsContaining(String tag);
}