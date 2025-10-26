package com.hlink.notice.crawler;

import com.hlink.notice.dto.NoticeDTO;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URL;
import java.util.*;

@Component
public class NoticeCrawler {

    private static final Map<String, String> FEEDS = Map.of(
        "학사공지", "https://www.hallym.ac.kr/bbs/hallym/157/rssList.do?row=50",
        "장학/등록공지", "https://www.hallym.ac.kr/bbs/hallym/156/rssList.do?row=50",
        "일반공지", "https://www.hallym.ac.kr/bbs/hallym/155/rssList.do?row=50",
        "채용공지", "https://www.hallym.ac.kr/bbs/hallym/151/rssList.do?row=50",
        "SW중심대학사업단", "https://www.hallym.ac.kr/bbs/hlsw/335/rssList.do?row=50"
    );

    public List<NoticeDTO> crawlAll() {
        List<NoticeDTO> allNotices = new ArrayList<>();

        FEEDS.forEach((category, url) -> {
            System.out.println("🔍 [" + category + "] RSS 크롤링 시작: " + url);
            allNotices.addAll(fetchRss(category, url));
        });

        System.out.println("✅ 총 " + allNotices.size() + "개 공지 수집 완료");
        return allNotices;
    }

    private List<NoticeDTO> fetchRss(String category, String feedUrl) {
        List<NoticeDTO> notices = new ArrayList<>();

        try {
            URL url = new URL(feedUrl);
            Scanner scanner = new Scanner(url.openStream());
            StringBuilder xmlBuilder = new StringBuilder();
            while (scanner.hasNext()) {
                xmlBuilder.append(scanner.nextLine());
            }
            scanner.close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlBuilder.toString())));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element item = (Element) nodeList.item(i);

                String title = getTagValue("title", item);
                if (title != null) title = title.replaceAll("}$", "").trim();
                String link = "https://www.hallym.ac.kr" + getTagValue("link", item);
                String date = getTagValue("pubDate", item);
                String author = getTagValue("author", item);
                String description = getTagValue("description", item);

                notices.add(new NoticeDTO(
                        null, title, category, date, author, description, List.of(), link
                ));
            }
            System.out.println("✅ [" + category + "] " + notices.size() + "개 항목 수집 완료");

        } catch (Exception e) {
            System.err.println("❌ [" + category + "] 크롤링 실패: " + e.getMessage());
        }

        return notices;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0 && nodeList.item(0).getFirstChild() != null) {
            return nodeList.item(0).getFirstChild().getNodeValue();
        }
        return "";
    }
}
