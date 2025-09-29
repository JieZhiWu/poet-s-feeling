package com.jiewu.mianshigo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API 配置测试类
 * 用于验证 API 配置是否正确
 */
@Configuration
@Slf4j
public class ApiTestConfig {

    @Value("${langchain4j.dashscope.api-key}")
    private String apiKey;

    @Value("${langchain4j.dashscope.chat-model.model-name}")
    private String modelName;

    /**
     * 应用启动时检查 API 配置
     */
    @Bean
    public ApplicationRunner checkApiConfig() {
        return args -> {
            log.info("=== AI API 配置检查 ===");
            log.info("API Key: {}", apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "***" : "未配置");
            log.info("Model Name: {}", modelName);
            log.info("Base URL: https://api.siliconflow.cn/v1");
            
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("❌ API Key 未配置或为空！");
                log.error("请在 application.yml 中正确配置 langchain4j.dashscope.api-key");
            } else if (apiKey.startsWith("sk-")) {
                log.info("✅ API Key 格式正确");
            } else {
                log.warn("⚠️ API Key 格式可能不正确，应该以 'sk-' 开头");
            }
            log.info("========================");
        };
    }
}
