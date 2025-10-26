// 비지니스 로직

package com.hlink.notice.service;

import com.hlink.notice.crawler.NoticeCrawler;
import com.hlink.notice.dto.NoticeDTO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NoticeService {

    private final NoticeCrawler crawler;

    public NoticeService(NoticeCrawler crawler) {
        this.crawler = crawler;
    }

    public List<NoticeDTO> getAllNotices() {
        return crawler.crawlAll();
    }
}
