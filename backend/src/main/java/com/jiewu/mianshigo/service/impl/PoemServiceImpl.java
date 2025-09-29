package com.jiewu.mianshigo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiewu.mianshigo.common.ErrorCode;
import com.jiewu.mianshigo.exception.BusinessException;
import com.jiewu.mianshigo.exception.ThrowUtils;
import com.jiewu.mianshigo.mapper.PoemMapper;
import com.jiewu.mianshigo.model.dto.poem.PoemGenerateRequest;
import com.jiewu.mianshigo.model.dto.poem.PoemResponse;
import com.jiewu.mianshigo.model.entity.Poem;
import com.jiewu.mianshigo.service.PoemService;
import com.jiewu.mianshigo.service.ai.PoemGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 诗歌服务实现
 */
@Service
@Slf4j
public class PoemServiceImpl extends ServiceImpl<PoemMapper, Poem> implements PoemService {

    @Resource
    private PoemGeneratorService poemGeneratorService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public PoemResponse generatePoem(PoemGenerateRequest poemGenerateRequest) {
        // 参数校验
           if (poemGenerateRequest == null || StringUtils.isBlank(poemGenerateRequest.getInputText())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户输入文本不能为空");
        }

        String inputText = poemGenerateRequest.getInputText().trim();
        if (inputText.length() > 2000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入文本过长，请控制在2000字符以内");
        }

        try {
            // 调用 AI 服务生成诗歌
            PoemResponse poemResponse = poemGeneratorService.generatePoem(inputText);
            
            // 验证生成结果
            validatePoemResponse(poemResponse);
            
            return poemResponse;
        } catch (Exception e) {
            log.error("生成诗歌失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务调用失败，请稍后重试");
        }
    }

    @Override
    public Poem createPoem(Long userId, String inputText) {
        // 参数校验
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID无效");
        ThrowUtils.throwIf(StringUtils.isBlank(inputText), ErrorCode.PARAMS_ERROR, "输入文本不能为空");

        // 生成诗歌
        PoemGenerateRequest request = new PoemGenerateRequest();
        request.setUserId(userId);
        request.setInputText(inputText);
        
        PoemResponse poemResponse = generatePoem(request);

        // 构建诗歌实体
        Poem poem = new Poem();
        poem.setUserId(userId);
        poem.setInputText(inputText);
        poem.setTitle(poemResponse.getTitle());
        poem.setPoem(poemResponse.getPoem());
        poem.setModelName("qwen-turbo");
        poem.setStatus("done");
        poem.setCreateTime(new Date());
        poem.setUpdateTime(new Date());

        try {
            // 将 keywords 和 recommendations 转为 JSON 字符串
            poem.setKeywords(objectMapper.writeValueAsString(poemResponse.getKeywords()));
            poem.setRecommendations(objectMapper.writeValueAsString(poemResponse.getRecommendations()));
            poem.setAiResponse(objectMapper.writeValueAsString(poemResponse));
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据处理失败");
        }

        // 保存到数据库
        boolean saveResult = this.save(poem);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "诗歌保存失败");

        return poem;
    }

    /**
     * 验证诗歌生成结果
     */
    private void validatePoemResponse(PoemResponse poemResponse) {
        if (poemResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 返回结果为空");
        }
        
        if (StringUtils.isBlank(poemResponse.getTitle())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "诗歌标题不能为空");
        }
        
        if (StringUtils.isBlank(poemResponse.getPoem())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "诗歌内容不能为空");
        }
        
        if (poemResponse.getKeywords() == null || poemResponse.getKeywords().size() < 3 || poemResponse.getKeywords().size() > 6) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "关键词数量应为3-6个");
        }
        
        if (poemResponse.getRecommendations() == null || poemResponse.getRecommendations().size() < 2 || poemResponse.getRecommendations().size() > 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "推荐作品数量应为2-3个");
        }
    }
}
