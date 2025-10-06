package com.jiewu.mianshigo.service.ai;

import com.jiewu.mianshigo.model.dto.poem.PoemResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * 诗歌生成 AI 服务接口
 * 使用 LangChain4j 的工厂类方式创建实现
 */
public interface PoemGeneratorService {

    @SystemMessage(fromResource = "/prompts/poem-generator-system.txt")
    @UserMessage("请你根据以下内容生成一首现代诗，并严格以 JSON 格式返回（不要额外文字）：\n\n用户输入：\n{{userText}}")
    PoemResponse generatePoem(String userText);
}
