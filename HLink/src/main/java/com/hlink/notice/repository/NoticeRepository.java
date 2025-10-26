// DB 접근 (DAO 역할)

package com.hlink.notice.repository;

import com.hlink.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 나중에 findByCategory, findByTitle 등 추가 가능
}
