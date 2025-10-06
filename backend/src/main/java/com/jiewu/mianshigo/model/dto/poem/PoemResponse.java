package com.jiewu.mianshigo.model.dto.poem;

import lombok.Data;

import java.util.List;

/**
 * 诗歌生成响应 DTO
 * 用于 AI 第一次调用的结构化输出
 */
@Data
public class PoemResponse {

    /**
     * 诗歌标题
     */
    private String title;

    /**
     * 关键词数组（3-6个）
     */
    private List<String> keywords;

    /**
     * 诗歌正文
     */
    private String poem;

    /**
     * 推荐作品（2-3个）
     */
    private List<String> recommendations;
}
