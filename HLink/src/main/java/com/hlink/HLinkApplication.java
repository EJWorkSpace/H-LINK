package com.hlink;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

@SpringBootApplication
public class HLinkApplication {

    public static void main(String[] args) {
        // 1️⃣ .env 로드
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 2️⃣ System Properties에 주입
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASS", dotenv.get("DB_PASS"));

        // 3️⃣ Spring Boot 실행
        SpringApplication.run(HLinkApplication.class, args);
    }

    // 4️⃣ Thymeleaf Resolver 추가 (layout 깨짐 방지)
    @Bean
    public SpringResourceTemplateResolver thymeleafTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false); // 💡 캐시 비활성화로 최신 파일 즉시 반영
        return resolver;
    }
}
