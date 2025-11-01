package com.hlink.notice.service;

import com.hlink.notice.entity.Favorite;
import com.hlink.notice.entity.Notice;
import com.hlink.notice.repository.FavoriteRepository;
import com.hlink.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final NoticeRepository noticeRepository;

    private static final Long USER_ID = 1L; // 임시 고정

    public boolean toggle(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + noticeId));

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndNotice(USER_ID, notice);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false; // removed
        } else {
            Favorite fav = Favorite.builder()
                    .userId(USER_ID)
                    .notice(notice)
                    .build();
            favoriteRepository.save(fav);
            return true; // added
        }
    }

    public List<Favorite> list() {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(USER_ID);
    }

    public Set<Long> idSet() {
        return list().stream()
                .map(f -> f.getNotice().getId())
                .collect(Collectors.toSet());
    }

    public boolean isStarred(Long noticeId) {
        return favoriteRepository.existsByUserIdAndNotice_Id(USER_ID, noticeId);
    }

    public List<Notice> favoriteNotices() {
        return list().stream()
                .map(Favorite::getNotice)
                .toList();
    }
}