# LangChain4j AI 服务配置修复说明

## 问题描述

原始错误信息：
```
No qualifying bean of type 'dev.langchain4j.model.chat.ChatModel' available
```

## 解决方案

### 1. 创建 ChatModel 配置类

创建了 `ChatModelConfig.java`，手动配置 ChatModel Bean：

```java
@Configuration
public class ChatModelConfig {
    
    @Bean
    @ConditionalOnMissingBean(ChatModel.class)
    public ChatModel chatModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .baseUrl("https://api.moonshot.cn/v1")  // 月之暗面 API
                .build();
    }
}
```

### 2. 修改 AI 服务接口

移除了 `@AiService` 注解，改为使用工厂方法创建：

**PoemGeneratorService.java:**
```java
public interface PoemGeneratorService {
    @SystemMessage(fromResource = "/prompts/poem-generator-system.txt")
    @UserMessage("...")
    PoemResponse generatePoem(String userText);
}
```

### 3. 创建系统提示词文件

将提示词从注解中移出，放到独立的文件中：
- `/resources/prompts/poem-generator-system.txt`
- `/resources/prompts/poem-analysis-system.txt`

### 4. 更新 LangChain4j 配置

使用 `AiServices.builder()` 方式创建 AI 服务实现：

```java
@Configuration
public class LangChain4jConfig {

    @Bean
    public PoemGeneratorService poemGeneratorService(ChatModel chatModel) {
        return AiServices.builder(PoemGeneratorService.class)
                .chatModel(chatModel)
                .build();
    }
}
```

### 5. 配置文件调整

配置支持月之暗面 API：

```yaml
langchain4j:
  dashscope:
    api-key: sk-wdeamlusdmuhsxauneukqonfjxublhcrgujjgonukmirrdeo
    chat-model:
      model-name: moonshot-v1-8k
      temperature: 0.7
      max-tokens: 2000
```

## 技术要点

1. **工厂方法模式**：使用 `AiServices.builder()` 而不是 `@AiService` 注解
2. **依赖注入**：手动配置 ChatModel Bean，确保 Spring 能够注入
3. **提示词管理**：使用 `@SystemMessage(fromResource = "...")` 外部化提示词
4. **兼容性**：使用 OpenAI 兼容接口支持多种模型提供商

## 验证步骤

1. 启动应用，确保没有 Bean 创建错误
2. 调用诗歌生成接口：`POST /api/poem/generate`
3. 调用解读报告接口：`POST /api/poem/{id}/analysis`

## 注意事项

- 确保 API Key 正确配置
- 月之暗面 API 的模型名格式为 `moonshot-v1-8k`
- 系统提示词文件路径必须以 `/` 开头（classpath 根路径）
