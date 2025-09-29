package com.jiewu.mianshigo.service.ai;

import com.jiewu.mianshigo.model.dto.poem.PoemAnalysisResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * 诗歌解读 AI 服务接口
 * 使用 LangChain4j 的工厂类方式创建实现
 */
public interface PoemAnalysisAiService {

    @SystemMessage(fromResource = "/prompts/poem-analysis-system.txt")
    @UserMessage("请你根据以下诗歌，生成一份附加解读报告（严格以 JSON 返回）：\n\n诗歌 JSON：\n{{poemJson}}")
    PoemAnalysisResponse analyzePoem(String poemJson);
}
