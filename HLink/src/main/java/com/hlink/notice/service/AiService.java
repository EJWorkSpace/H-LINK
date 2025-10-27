package com.hlink.notice.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class AiService {
	private final String API_KEY;
	
	public AiService() {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		this.API_KEY = dotenv.get("GEMINI_API_KEY");
		
		if (API_KEY == null || API_KEY.isBlank()) {
			System.out.println("⚠️  [AiService] GEMINI_API_KEY not found in .env file!");
		} else {
			System.out.println("✅ [AiService] GEMINI_API_KEY loaded successfully!");
		}
	}
	public void testPrintKey() {
		System.out.println("🔑 Current key prefix: " + (API_KEY != null ? API_KEY.substring(0, 10) + "..." : "null"));
	}
	}