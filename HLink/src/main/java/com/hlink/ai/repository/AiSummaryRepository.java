package com.hlink.ai.repository;

import com.hlink.ai.entity.AiSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiSummaryRepository extends JpaRepository<AiSummary, Long> {

    Optional<AiSummary> findByNoticeId(Long noticeId);

    // ✅ 아직 요약 안 된 공지 ID 찾기 (엔티티 관계 기준으로 수정)
    @Query("SELECT n.id FROM Notice n WHERE n.id NOT IN (SELECT a.notice.id FROM AiSummary a)")
    List<Long> findUnsummarizedNoticeIds();
}
