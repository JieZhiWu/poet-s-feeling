package com.jiewu.mianshigo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiewu.mianshigo.model.dto.poem.PoemGenerateRequest;
import com.jiewu.mianshigo.model.dto.poem.PoemResponse;
import com.jiewu.mianshigo.model.entity.Poem;

/**
 * 诗歌服务
 */
public interface PoemService extends IService<Poem> {

    /**
     * 生成诗歌
     *
     * @param poemGenerateRequest 诗歌生成请求
     * @return 生成的诗歌响应
     */
    PoemResponse generatePoem(PoemGenerateRequest poemGenerateRequest);

    /**
     * 根据用户输入文本生成诗歌并保存到数据库
     *
     * @param userId 用户ID
     * @param inputText 用户输入文本
     * @return 保存的诗歌实体
     */
    Poem createPoem(Long userId, String inputText);
}
