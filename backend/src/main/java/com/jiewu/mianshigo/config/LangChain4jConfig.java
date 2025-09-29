package com.jiewu.mianshigo.config;

import com.jiewu.mianshigo.service.ai.PoemAnalysisAiService;
import com.jiewu.mianshigo.service.ai.PoemGeneratorService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j AI 服务配置类
 * 使用工厂类方式创建 AI 服务实现
 */
@Configuration
public class LangChain4jConfig {

    /**
     * 配置诗歌生成 AI 服务
     * 使用 AiServices.builder 工厂方法创建实现
     */
    @Bean
    public PoemGeneratorService poemGeneratorService(ChatModel chatModel) {
        return AiServices.builder(PoemGeneratorService.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * 配置诗歌解读 AI 服务
     * 使用 AiServices.builder 工厂方法创建实现
     */
    @Bean
    public PoemAnalysisAiService poemAnalysisAiService(ChatModel chatModel) {
        return AiServices.builder(PoemAnalysisAiService.class)
                .chatModel(chatModel)
                .build();
    }
}
