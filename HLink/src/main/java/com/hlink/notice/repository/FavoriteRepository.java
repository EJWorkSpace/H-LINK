package com.hlink.notice.repository;

import com.hlink.notice.entity.Favorite;
import com.hlink.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Favorite> findByUserIdAndNotice(Long userId, Notice notice);

    boolean existsByUserIdAndNotice_Id(Long userId, Long noticeId);
}
