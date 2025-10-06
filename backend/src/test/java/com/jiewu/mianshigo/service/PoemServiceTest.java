package com.jiewu.mianshigo.service;

import com.jiewu.mianshigo.model.dto.poem.PoemAnalysisResponse;
import com.jiewu.mianshigo.model.dto.poem.PoemGenerateRequest;
import com.jiewu.mianshigo.model.dto.poem.PoemResponse;
import com.jiewu.mianshigo.model.entity.Poem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 诗歌服务测试
 */
@SpringBootTest
public class PoemServiceTest {

    @Resource
    private PoemService poemService;

    @Resource
    private PoemAnalysisService poemAnalysisService;

    /**
     * 测试诗歌生成
     */
    @Test
    public void testGeneratePoem() {
        // 创建测试请求
        PoemGenerateRequest request = new PoemGenerateRequest();
        request.setUserId(1L);
        request.setInputText("今天下班很晚，回家路上只有昏黄的路灯，我觉得有点孤单，但也看到一只小猫在等我，心里好像亮了一点点。");

        try {
            // 测试生成诗歌
            PoemResponse response = poemService.generatePoem(request);
            
            System.out.println("Generated Poem:");
            System.out.println("Title: " + response.getTitle());
            System.out.println("Keywords: " + response.getKeywords());
            System.out.println("Poem: " + response.getPoem());
            System.out.println("Recommendations: " + response.getRecommendations());
            
            // 测试保存诗歌
            Poem poem = poemService.createPoem(1L, request.getInputText());
            System.out.println("Saved Poem ID: " + poem.getId());
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试诗歌解读生成
     */
    @Test
    public void testGenerateAnalysis() {
        try {
            // 假设已有诗歌 ID 为 1
            Long poemId = 1L;
            
            // 测试生成解读
            PoemAnalysisResponse analysisResponse = poemAnalysisService.generateAnalysis(poemId);
            
            System.out.println("Generated Analysis:");
            System.out.println("Analysis: " + analysisResponse.getAnalysis());
            System.out.println("Style: " + analysisResponse.getStyle());
            System.out.println("Inspiration: " + analysisResponse.getInspiration());
            System.out.println("Related Themes: " + analysisResponse.getRelatedThemes());
            
        } catch (Exception e) {
            System.err.println("Analysis test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
