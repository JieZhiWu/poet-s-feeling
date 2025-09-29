package com.jiewu.mianshigo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiewu.mianshigo.common.ErrorCode;
import com.jiewu.mianshigo.exception.BusinessException;
import com.jiewu.mianshigo.exception.ThrowUtils;
import com.jiewu.mianshigo.mapper.PoemAnalysisMapper;
import com.jiewu.mianshigo.model.dto.poem.PoemAnalysisResponse;
import com.jiewu.mianshigo.model.dto.poem.PoemResponse;
import com.jiewu.mianshigo.model.entity.Poem;
import com.jiewu.mianshigo.model.entity.PoemAnalysis;
import com.jiewu.mianshigo.service.PoemAnalysisService;
import com.jiewu.mianshigo.service.PoemService;
import com.jiewu.mianshigo.service.ai.PoemAnalysisAiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 诗歌解读服务实现
 */
@Service
@Slf4j
public class PoemAnalysisServiceImpl extends ServiceImpl<PoemAnalysisMapper, PoemAnalysis> implements PoemAnalysisService {

    @Resource
    private PoemAnalysisAiService poemAnalysisAiService;

    @Resource
    private PoemService poemService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public PoemAnalysisResponse generateAnalysis(Long poemId) {
        // 参数校验
        ThrowUtils.throwIf(poemId == null || poemId <= 0, ErrorCode.PARAMS_ERROR, "诗歌ID无效");

        // 获取诗歌信息
        Poem poem = poemService.getById(poemId);
        ThrowUtils.throwIf(poem == null, ErrorCode.NOT_FOUND_ERROR, "诗歌不存在");

        try {
            // 构建诗歌 JSON 用于 AI 分析
            String poemJson = poem.getAiResponse();
            if (StringUtils.isBlank(poemJson)) {
                // 如果没有 AI 原始响应，构建基本的诗歌信息
                PoemResponse basicPoemResponse = new PoemResponse();
                basicPoemResponse.setTitle(poem.getTitle());
                basicPoemResponse.setPoem(poem.getPoem());
                poemJson = objectMapper.writeValueAsString(basicPoemResponse);
            }

            // 调用 AI 服务生成解读
            PoemAnalysisResponse analysisResponse = poemAnalysisAiService.analyzePoem(poemJson);
            
            // 验证生成结果
            validateAnalysisResponse(analysisResponse);
            
            return analysisResponse;
        } catch (Exception e) {
            log.error("生成诗歌解读失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务调用失败，请稍后重试");
        }
    }

    @Override
    public PoemAnalysis createAnalysis(Long poemId) {
        // 参数校验
        ThrowUtils.throwIf(poemId == null || poemId <= 0, ErrorCode.PARAMS_ERROR, "诗歌ID无效");

        // 检查是否已存在解读报告
        PoemAnalysis existingAnalysis = this.lambdaQuery()
                .eq(PoemAnalysis::getPoemId, poemId)
                .one();
        if (existingAnalysis != null) {
            return existingAnalysis;
        }

        // 生成解读报告
        PoemAnalysisResponse analysisResponse = generateAnalysis(poemId);

        // 构建解读实体
        PoemAnalysis poemAnalysis = new PoemAnalysis();
        poemAnalysis.setPoemId(poemId);
        poemAnalysis.setAnalysis(analysisResponse.getAnalysis());
        poemAnalysis.setStyle(analysisResponse.getStyle());
        poemAnalysis.setInspiration(analysisResponse.getInspiration());
        poemAnalysis.setModelName("qwen-turbo");
        poemAnalysis.setCreateTime(new Date());
        poemAnalysis.setUpdateTime(new Date());

        try {
            // 将 relatedThemes 转为 JSON 字符串
            poemAnalysis.setRelatedThemes(objectMapper.writeValueAsString(analysisResponse.getRelatedThemes()));
            poemAnalysis.setAiResponse(objectMapper.writeValueAsString(analysisResponse));
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据处理失败");
        }

        // 保存到数据库
        boolean saveResult = this.save(poemAnalysis);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "解读报告保存失败");

        return poemAnalysis;
    }

    /**
     * 验证解读报告生成结果
     */
    private void validateAnalysisResponse(PoemAnalysisResponse analysisResponse) {
        if (analysisResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 返回结果为空");
        }
        
        if (StringUtils.isBlank(analysisResponse.getAnalysis())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "诗歌解读不能为空");
        }
        
        if (StringUtils.isBlank(analysisResponse.getStyle())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "风格总结不能为空");
        }
        
        if (StringUtils.isBlank(analysisResponse.getInspiration())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "灵感建议不能为空");
        }
        
        if (analysisResponse.getRelatedThemes() == null || analysisResponse.getRelatedThemes().size() < 3 || analysisResponse.getRelatedThemes().size() > 6) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "相关主题数量应为3-6个");
        }
    }
}
