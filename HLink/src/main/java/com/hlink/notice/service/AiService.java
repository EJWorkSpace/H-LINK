package com.hlink.notice.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
public class AiService {

    private final String API_KEY;
    private final HttpClient http = HttpClient.newHttpClient();
    // 모델은 가볍고 빠른 1.5-flash 사용 (요금/쿼터 상황 따라 변경 가능)
    private static final String MODEL = "gemini-1.5-flash";

    public AiService() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.API_KEY = dotenv.get("GEMINI_API_KEY");
        if (API_KEY == null || API_KEY.isBlank()) {
            System.out.println("⚠️ [AiService] GEMINI_API_KEY not found. Check .env");
        } else {
            System.out.println("✅ [AiService] GEMINI_API_KEY loaded");
        }
    }

    public String summarize(String title, String content) {
        String base = (content != null && !content.isBlank()) ? content : "";
        String prompt = """
                다음 공지사항을 3줄 이내 한국어로 핵심만 요약해 주세요.
                - 군더더기/광고 문구 제외
                - 날짜/신청기한/대상/혜택이 있으면 포함
                제목: %s
                본문:
                %s
                """.formatted(nullToEmpty(title), base);

        return callGemini(prompt);
    }

    public List<String> extractTags(String title, String content) {
        String text = (nullToEmpty(title) + " " + nullToEmpty(content)).trim();
        String prompt = """
                다음 텍스트에서 한국어 핵심 태그를 5개 선별해 주세요.
                - 한 단어 또는 짧은 구(최대 8자)
                - 공백없이 쉼표(,)로만 구분
                - 예시 출력: 공모전,장학금,모집,채용,마감임박

                텍스트:
                %s
                """.formatted(text);

        String raw = callGemini(prompt);
        // "AI, 공지, 일정, 접수, 안내" → ["AI","공지","일정","접수","안내"]
        String[] toks = raw.replaceAll("[\\r\\n]", " ").split("\\s*,\\s*");
        return Arrays.stream(toks)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .limit(5)
                .toList();
    }

    private String callGemini(String prompt) {
        try {
            JSONObject body = new JSONObject()
                    .put("contents", new JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new JSONArray()
                                            .put(new JSONObject().put("text", prompt)))));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent?key=" + API_KEY))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 400) {
                System.out.println("❌ [AiService] Gemini error: " + resp.statusCode() + " " + resp.body());
                return "";
            }

            JSONObject json = new JSONObject(resp.body());
            return json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .optString("text", "")
                    .trim();

        } catch (Exception e) {
            System.out.println("❌ [AiService] callGemini error: " + e.getMessage());
            return "";
        }
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}