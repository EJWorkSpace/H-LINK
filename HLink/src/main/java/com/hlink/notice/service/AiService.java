package com.hlink.notice.service;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentRequest;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.generativeai.GenerativeModel;

@Service
public class AiService {

    private final String apiKey;

    public AiService() {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .load();
        this.apiKey = dotenv.get("GEMINI_API_KEY");

        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new RuntimeException("❌ GEMINI_API_KEY not found in .env file! (.env 위치: " + System.getProperty("user.dir") + ")");
        } else {
            System.out.println("✅ GEMINI_API_KEY loaded successfully!");
        }
    }

    public String summarize(String text) {
        try (VertexAI vertexAI = new VertexAI("hlink-project", "asia-northeast3")) { // 프로젝트명/리전 직접 입력 필요
            GenerativeModel model = new GenerativeModel("gemini-1.5-flash", vertexAI);

            // 요청 내용 구성
            Content prompt = Content.newBuilder()
                    .setRole("user")
                    .addParts(com.google.cloud.vertexai.api.Part.newBuilder()
                            .setText("요약해줘: " + text)
                            .build())
                    .build();

            // 모델 호출
            GenerateContentResponse response = model.generateContent(prompt);

            return response.getCandidates(0).getContent().getParts(0).getText();

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ 요약 중 오류 발생: " + e.getMessage();
        }
    }

	public List<String> extractTags(String title, String fetchContent) {
		// TODO Auto-generated method stub
		return null;
	}
}