package com.hlink.notice.crawler;

import com.hlink.notice.dto.NoticeDTO;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    /** 모든 카테고리의 공지를 수집 */
    public List<NoticeDTO> crawlAll() {
        List<NoticeDTO> allNotices = new ArrayList<>();

        FEEDS.forEach((category, url) -> {
            System.out.println("🔍 [" + category + "] RSS 크롤링 시작: " + url);
            allNotices.addAll(fetchRss(category, url));
        });

        System.out.println("✅ 총 " + allNotices.size() + "개 공지 수집 완료");
        return allNotices;
    }

    /** RSS 피드에서 공지 항목을 추출 */
    private List<NoticeDTO> fetchRss(String category, String feedUrl) {
        List<NoticeDTO> notices = new ArrayList<>();

        try {
            URL url = new URL(feedUrl);
            Scanner scanner = new Scanner(url.openStream());
            StringBuilder xmlBuilder = new StringBuilder();
            while (scanner.hasNext()) xmlBuilder.append(scanner.nextLine());
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
                String dateStr = getTagValue("pubDate", item);
                String description = getTagValue("description", item);

                // pubDate → LocalDateTime 변환
                LocalDateTime date = parsePubDateToLocalDateTime(dateStr);

                // NoticeDTO 빌더로 객체 생성 (순서 실수 방지)
                NoticeDTO dto = NoticeDTO.builder()
                        .id(null)
                        .title(title)
                        .link(link)
                        .date(date)
                        .category(category)
                        .deadline(null) // RSS에는 마감일이 없음
                        .summary(description)
                        .tags(Collections.emptyList())
                        .build();

                notices.add(dto);
            }

            System.out.println("✅ [" + category + "] " + notices.size() + "개 항목 수집 완료");

        } catch (Exception e) {
            System.err.println("❌ [" + category + "] 크롤링 실패: " + e.getMessage());
        }

        return notices;
    }

    /** pubDate 문자열을 LocalDateTime으로 변환 */
    private LocalDateTime parsePubDateToLocalDateTime(String pubDate) {
        if (pubDate == null || pubDate.isBlank()) return null;
        try {
            return ZonedDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME)
                    .withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (Exception e) {
            return null; // 형식 안 맞으면 null
        }
    }

    /** XML 태그 값 추출 */
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0 && nodeList.item(0).getFirstChild() != null) {
            return nodeList.item(0).getFirstChild().getNodeValue();
        }
        return "";
    }
}