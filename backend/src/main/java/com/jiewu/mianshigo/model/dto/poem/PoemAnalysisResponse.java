package com.jiewu.mianshigo.model.dto.poem;

import lombok.Data;

import java.util.List;

/**
 * 诗歌解读响应 DTO
 * 用于 AI 第二次调用的结构化输出
 */
@Data
public class PoemAnalysisResponse {

    /**
     * 诗歌解读（200-600字）
     */
    private String analysis;

    /**
     * 风格总结
     */
    private String style;

    /**
     * 写作灵感建议（50-200字）
     */
    private String inspiration;

    /**
     * 相关主题（3-6个）
     */
    private List<String> relatedThemes;
}
