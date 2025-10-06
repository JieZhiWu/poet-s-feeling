package com.jiewu.mianshigo.manager;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.jiewu.mianshigo.config.AliOssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * 阿里云OSS对象存储操作
 */
@Component
@Slf4j
public class AliOssManager {

    @Resource
    private OSS ossClient;

    @Resource
    private AliOssConfig aliOssConfig;

    /**
     * 上传对象
     *
     * @param key 唯一键（文件路径）
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(aliOssConfig.getBucketName(), key, file);
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            log.info("文件上传成功，Key: {}, ETag: {}", key, result.getETag());
            return result;
        } catch (Exception e) {
            log.error("文件上传失败，Key: {}, 错误信息: {}", key, e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 上传对象
     *
     * @param key 唯一键（文件路径）
     * @param localFilePath 本地文件路径
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        return putObject(key, new File(localFilePath));
    }

    /**
     * 删除对象
     *
     * @param key 唯一键（文件路径）
     */
    public void deleteObject(String key) {
        try {
            ossClient.deleteObject(aliOssConfig.getBucketName(), key);
            log.info("文件删除成功，Key: {}", key);
        } catch (Exception e) {
            log.error("文件删除失败，Key: {}, 错误信息: {}", key, e.getMessage(), e);
            throw new RuntimeException("文件删除失败", e);
        }
    }

    /**
     * 检查对象是否存在
     *
     * @param key 唯一键（文件路径）
     * @return 是否存在
     */
    public boolean doesObjectExist(String key) {
        try {
            return ossClient.doesObjectExist(aliOssConfig.getBucketName(), key);
        } catch (Exception e) {
            log.error("检查文件是否存在失败，Key: {}, 错误信息: {}", key, e.getMessage(), e);
            return false;
        }
    }
}


