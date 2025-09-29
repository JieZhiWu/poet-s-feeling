package com.jiewu.mianshigo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiewu.mianshigo.annotation.AuthCheck;
import com.jiewu.mianshigo.common.BaseResponse;
import com.jiewu.mianshigo.common.ErrorCode;
import com.jiewu.mianshigo.common.ResultUtils;
import com.jiewu.mianshigo.constant.UserConstant;
import com.jiewu.mianshigo.exception.BusinessException;
import com.jiewu.mianshigo.exception.ThrowUtils;
import com.jiewu.mianshigo.model.dto.poem.PoemAnalysisResponse;
import com.jiewu.mianshigo.model.dto.poem.PoemGenerateRequest;
import com.jiewu.mianshigo.model.dto.poem.PoemResponse;
import com.jiewu.mianshigo.model.entity.Poem;
import com.jiewu.mianshigo.model.entity.PoemAnalysis;
import com.jiewu.mianshigo.model.entity.User;
import com.jiewu.mianshigo.service.PoemAnalysisService;
import com.jiewu.mianshigo.service.PoemService;
import com.jiewu.mianshigo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 诗歌接口
 */
@RestController
@RequestMapping("/poem")
@Slf4j
@Api(tags = "诗歌模块")
public class PoemController {

    @Resource
    private PoemService poemService;

    @Resource
    private PoemAnalysisService poemAnalysisService;

    @Resource
    private UserService userService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 生成诗歌
     *
     * @param poemGenerateRequest 诗歌生成请求
     * @param request HTTP请求
     * @return 生成的诗歌及诗歌ID
     */
    @PostMapping("/generate")
    @ApiOperation(value = "生成诗歌")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<PoemWithId> generatePoem(@RequestBody PoemGenerateRequest poemGenerateRequest,
                                                 HttpServletRequest request) {
        // 参数校验
        if (poemGenerateRequest == null || StringUtils.isBlank(poemGenerateRequest.getInputText())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户输入文本不能为空");
        }

        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();

        try {
            // 生成并保存诗歌
            Poem poem = poemService.createPoem(userId, poemGenerateRequest.getInputText());

            // 构建返回结果
            PoemResponse poemResponse = new PoemResponse();
            poemResponse.setTitle(poem.getTitle());
            poemResponse.setPoem(poem.getPoem());
            
            // 解析 JSON 字符串为 List
            if (StringUtils.isNotBlank(poem.getKeywords())) {
                List<String> keywords = objectMapper.readValue(poem.getKeywords(), new TypeReference<List<String>>() {});
                poemResponse.setKeywords(keywords);
            }
            
            if (StringUtils.isNotBlank(poem.getRecommendations())) {
                List<String> recommendations = objectMapper.readValue(poem.getRecommendations(), new TypeReference<List<String>>() {});
                poemResponse.setRecommendations(recommendations);
            }

            PoemWithId result = new PoemWithId();
            result.setId(poem.getId());
            result.setPoemResponse(poemResponse);

            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("生成诗歌失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "诗歌生成失败，请稍后重试");
        }
    }

    /**
     * 生成诗歌解读报告
     *
     * @param id 诗歌ID
     * @param request HTTP请求
     * @return 解读报告
     */
    @PostMapping("/{id}/analysis")
    @ApiOperation(value = "生成诗歌解读报告")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<PoemAnalysisResponse> generateAnalysis(@PathVariable Long id,
                                                               HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "诗歌ID无效");

        // 获取登录用户
        User loginUser = userService.getLoginUser(request);

        // 检查诗歌是否存在且属于当前用户
        Poem poem = poemService.getById(id);
        ThrowUtils.throwIf(poem == null, ErrorCode.NOT_FOUND_ERROR, "诗歌不存在");
        ThrowUtils.throwIf(!poem.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限访问该诗歌");

        try {
            // 生成并保存解读报告
            PoemAnalysis poemAnalysis = poemAnalysisService.createAnalysis(id);

            // 构建返回结果
            PoemAnalysisResponse analysisResponse = new PoemAnalysisResponse();
            analysisResponse.setAnalysis(poemAnalysis.getAnalysis());
            analysisResponse.setStyle(poemAnalysis.getStyle());
            analysisResponse.setInspiration(poemAnalysis.getInspiration());
            
            // 解析 JSON 字符串为 List
            if (StringUtils.isNotBlank(poemAnalysis.getRelatedThemes())) {
                List<String> relatedThemes = objectMapper.readValue(poemAnalysis.getRelatedThemes(), new TypeReference<List<String>>() {});
                analysisResponse.setRelatedThemes(relatedThemes);
            }

            return ResultUtils.success(analysisResponse);
        } catch (Exception e) {
            log.error("生成诗歌解读失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解读报告生成失败，请稍后重试");
        }
    }

    /**
     * 获取诗歌详情
     *
     * @param id 诗歌ID
     * @param request HTTP请求
     * @return 诗歌详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "获取诗歌详情")
    @AuthCheck(mustRole = UserConstant.DEFAULT_ROLE)
    public BaseResponse<PoemResponse> getPoemById(@PathVariable Long id,
                                                  HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "诗歌ID无效");

        // 获取登录用户
        User loginUser = userService.getLoginUser(request);

        // 获取诗歌信息
        Poem poem = poemService.getById(id);
        ThrowUtils.throwIf(poem == null, ErrorCode.NOT_FOUND_ERROR, "诗歌不存在");
        ThrowUtils.throwIf(!poem.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限访问该诗歌");

        try {
            // 构建返回结果
            PoemResponse poemResponse = new PoemResponse();
            poemResponse.setTitle(poem.getTitle());
            poemResponse.setPoem(poem.getPoem());
            
            // 解析 JSON 字符串为 List
            if (StringUtils.isNotBlank(poem.getKeywords())) {
                List<String> keywords = objectMapper.readValue(poem.getKeywords(), new TypeReference<List<String>>() {});
                poemResponse.setKeywords(keywords);
            }
            
            if (StringUtils.isNotBlank(poem.getRecommendations())) {
                List<String> recommendations = objectMapper.readValue(poem.getRecommendations(), new TypeReference<List<String>>() {});
                poemResponse.setRecommendations(recommendations);
            }

            return ResultUtils.success(poemResponse);
        } catch (Exception e) {
            log.error("获取诗歌详情失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取诗歌详情失败");
        }
    }

    /**
     * 诗歌响应包装类（包含ID）
     */
    public static class PoemWithId {
        private Long id;
        private PoemResponse poemResponse;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public PoemResponse getPoemResponse() {
            return poemResponse;
        }

        public void setPoemResponse(PoemResponse poemResponse) {
            this.poemResponse = poemResponse;
        }
    }
}
