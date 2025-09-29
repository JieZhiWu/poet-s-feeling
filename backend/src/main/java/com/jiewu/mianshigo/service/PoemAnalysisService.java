package com.jiewu.mianshigo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiewu.mianshigo.model.dto.poem.PoemAnalysisResponse;
import com.jiewu.mianshigo.model.entity.PoemAnalysis;

/**
 * 诗歌解读服务
 */
public interface PoemAnalysisService extends IService<PoemAnalysis> {

    /**
     * 生成诗歌解读报告
     *
     * @param poemId 诗歌ID
     * @return 解读报告响应
     */
    PoemAnalysisResponse generateAnalysis(Long poemId);

    /**
     * 根据诗歌ID生成解读报告并保存到数据库
     *
     * @param poemId 诗歌ID
     * @return 保存的解读报告实体
     */
    PoemAnalysis createAnalysis(Long poemId);
}
