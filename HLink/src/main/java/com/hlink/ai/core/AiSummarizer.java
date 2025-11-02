package com.hlink.ai.core;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AiSummarizer {

	private static final String MODEL = "gemini-2.5-flash";

    public static AiSummaryResult summarize(String title, String content) {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String apiKey = dotenv.get("GEMINI_API_KEY");

            if (apiKey == null || apiKey.isBlank()) {
                return new AiSummaryResult("(API 키 누락)", null, null);
            }

            // ✅ 요청 본문 구성
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();

            JSONObject textPart = new JSONObject();
            textPart.put("text", String.format("""
너는 한림대학교 공지 요약 및 분석 AI야.
다음 내용을 읽고 아래 형식으로 정확히 출력해.

요약: (2~3줄 요약)
태그: [태그1, 태그2, 태그3]
마감일: YYYY-MM-DD 혹은 없음

제목: %s
내용:
%s
""", title, content));

            JSONObject contentItem = new JSONObject();
            contentItem.put("role", "user");
            contentItem.put("parts", new JSONArray().put(textPart));
            contents.put(contentItem);

            requestBody.put("contents", contents);

            // ✅ API 호출
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent?key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.toString().getBytes("UTF-8"));
            }

            // ✅ 응답 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            JSONObject responseJson = new JSONObject(sb.toString());
            String text = responseJson
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            // ✅ 파싱
            String summary = extract(text, "요약[:：]");
            String tags = extract(text, "태그[:：]");
            String deadline = extract(text, "마감일[:：]");

            return new AiSummaryResult(summary, tags, deadline);

        } catch (Exception e) {
            e.printStackTrace();
            return new AiSummaryResult("(요약 실패)", null, null);
        }
    }

    private static String extract(String text, String key) {
        String[] lines = text.split("\n");
        for (String l : lines) {
            if (l.trim().startsWith("요약") && key.contains("요약")) return l.replaceAll("요약[:：]", "").trim();
            if (l.trim().startsWith("태그") && key.contains("태그")) return l.replaceAll("태그[:：]", "").trim();
            if (l.trim().startsWith("마감일") && key.contains("마감일")) return l.replaceAll("마감일[:：]", "").trim();
        }
        return null;
    }

    public record AiSummaryResult(String summary, String tags, String deadline) {}
}
