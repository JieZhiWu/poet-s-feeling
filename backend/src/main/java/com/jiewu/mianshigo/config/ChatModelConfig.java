package com.jiewu.mianshigo.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatModel 配置类
 * 提供 ChatLanguageModel 的 Bean 配置
 */
@Configuration
public class ChatModelConfig {

    @Value("${langchain4j.dashscope.api-key}")
    private String apiKey;

    @Value("${langchain4j.dashscope.chat-model.model-name:qwen-turbo}")
    private String modelName;

    @Value("${langchain4j.dashscope.chat-model.temperature:0.7}")
    private Double temperature;

    @Value("${langchain4j.dashscope.chat-model.max-tokens:2000}")
    private Integer maxTokens;

    /**
     * 提供 ChatModel 的实现
     * 使用 OpenAI 兼容的接口连接月之暗面 API
     */
    @Bean
    @ConditionalOnMissingBean(ChatModel.class)
    public ChatModel chatModel() {
        // 验证配置
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API Key 不能为空，请检查配置文件中的 langchain4j.dashscope.api-key");
        }
        
        // 使用 OpenAI 兼容的接口连接硅基流动
        return OpenAiChatModel.builder()
                .apiKey(apiKey.trim())
                .modelName(modelName)
                .temperature(temperature)
                .maxTokens(maxTokens)
                // 硅基流动的 API 基础 URL
                .baseUrl("https://api.siliconflow.cn/v1")
                .logRequests(true)  // 启用请求日志
                .logResponses(true) // 启用响应日志
                .build();
    }
}
