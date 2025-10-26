package com.hlink;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HLinkApplication {

    public static void main(String[] args) {
        // 1️⃣ .env 로드
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 2️⃣ System Properties에 주입
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));

        SpringApplication.run(HLinkApplication.class, args);
    }
}
