package com.jiewu.mianshigo.model.dto.poem;

import lombok.Data;

import java.io.Serializable;

/**
 * 诗歌生成请求 DTO
 */
@Data
public class PoemGenerateRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户输入的文本
     */
    private String inputText;

    private static final long serialVersionUID = 1L;
}
