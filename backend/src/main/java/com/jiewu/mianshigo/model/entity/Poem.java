package com.jiewu.mianshigo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 诗歌表
 */
@TableName(value = "poem")
@Data
public class Poem implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户原始输入
     */
    @TableField("input_text")
    private String inputText;

    /**
     * 诗歌标题
     */
    private String title;

    /**
     * 关键词数组（JSON格式）
     */
    private String keywords;

    /**
     * 诗歌正文
     */
    private String poem;

    /**
     * 推荐作品（JSON格式）
     */
    private String recommendations;

    /**
     * AI 模型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * AI 原始返回（JSON格式）
     */
    @TableField("ai_response")
    private String aiResponse;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
