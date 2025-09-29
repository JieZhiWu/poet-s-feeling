package com.jiewu.mianshigo.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 诗歌解读报告表
 */
@TableName(value = "poem_analysis")
@Data
public class PoemAnalysis implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联 poem.id
     */
    @TableField("poem_id")
    private Long poemId;

    /**
     * 诗歌解读
     */
    private String analysis;

    /**
     * 风格总结
     */
    private String style;

    /**
     * 灵感建议
     */
    private String inspiration;

    /**
     * 相关主题（JSON格式）
     */
    @TableField("related_themes")
    private String relatedThemes;

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
