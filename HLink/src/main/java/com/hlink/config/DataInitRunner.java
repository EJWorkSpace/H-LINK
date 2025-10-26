package com.hlink.config;

import com.hlink.notice.service.NoticeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitRunner implements CommandLineRunner {

    private final NoticeSyncService noticeSyncService;

    @Override
    public void run(String... args) {
        System.out.println("🛰 공지사항 DB 동기화 시작...");
        noticeSyncService.syncNotices();
        System.out.println("✅ DB 동기화 완료!");
    }
}
