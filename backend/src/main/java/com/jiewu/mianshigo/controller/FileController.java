package com.jiewu.mianshigo.controller;

import cn.hutool.core.io.file.FileNameUtil;
import com.jiewu.mianshigo.common.BaseResponse;
import com.jiewu.mianshigo.common.ErrorCode;
import com.jiewu.mianshigo.common.ResultUtils;
import com.jiewu.mianshigo.constant.FileConstant;
import com.jiewu.mianshigo.exception.BusinessException;
import com.jiewu.mianshigo.manager.CosManager;
import com.jiewu.mianshigo.manager.AliOssManager;
import com.jiewu.mianshigo.config.AliOssConfig;
import com.jiewu.mianshigo.model.entity.User;
import com.jiewu.mianshigo.model.enums.FileUploadBizEnum;
import com.jiewu.mianshigo.service.UserService;
import com.jiewu.mianshigo.model.dto.file.UploadFileRequest;

import java.io.File;
import java.util.Arrays;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件接口

 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private AliOssManager aliOssManager;

    @Resource
    private AliOssConfig aliOssConfig;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        log.info("文件上传请求开始，业务类型: {}", uploadFileRequest != null ? uploadFileRequest.getBiz() : "null");
        
        if (uploadFileRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传参数不能为空");
        }
        
        String biz = uploadFileRequest.getBiz();
        if (biz == null || biz.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "业务类型不能为空");
        }
        
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "业务类型不正确，支持的业务类型：" + FileUploadBizEnum.getValues());
        }
        
        // 校验文件
        validFile(multipartFile, fileUploadBizEnum);
        User loginUser = userService.getLoginUser(request);
        
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String originalFilename = multipartFile.getOriginalFilename();
        String filename = uuid + "-" + originalFilename;
        // OSS对象名称不能以 "/" 开头，所以去掉开头的 "/"
        String filepath = String.format("%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        
        log.info("开始上传文件，用户ID: {}, 业务类型: {}, 文件名: {}, 文件大小: {} bytes", 
                loginUser.getId(), biz, originalFilename, multipartFile.getSize());
        
        File tempFile = null;
        try {
            // 创建临时文件
            tempFile = File.createTempFile("upload_", "_" + originalFilename);
            multipartFile.transferTo(tempFile);
            
            String fileUrl;
            
            try {
                // 检查OSS配置是否有效
                if (aliOssManager != null && !"YOUR_ACCESS_KEY_ID".equals(aliOssConfig.getAccessKeyId())) {
                    // 尝试上传到阿里云OSS
                    aliOssManager.putObject(filepath, tempFile);
                    // 动态生成OSS访问URL，格式：https://bucket-name.endpoint/object-name
                    fileUrl = String.format("https://%s.%s/%s", 
                            aliOssConfig.getBucketName(), 
                            aliOssConfig.getEndpoint(), 
                            filepath);
                    log.info("文件上传到OSS成功，访问地址: {}", fileUrl);
                } else {
                    throw new RuntimeException("OSS未配置或配置无效，使用本地存储");
                }
            } catch (Exception ossException) {
                log.warn("OSS上传失败，使用本地存储备用方案: {}", ossException.getMessage());
                
                // 备用方案：本地存储
                String uploadDir = "uploads";
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                String localFilePath = uploadDir + File.separator + filepath.replace("/", File.separator);
                File localFile = new File(localFilePath);
                localFile.getParentFile().mkdirs();
                
                // 复制文件到本地目录
                java.nio.file.Files.copy(tempFile.toPath(), localFile.toPath(), 
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                // 返回本地访问地址（需要配置静态资源映射）
                fileUrl = "http://localhost:8101/uploads/" + filepath;
                log.info("文件本地存储成功，访问地址: {}", fileUrl);
            }
            
            return ResultUtils.success(fileUrl);
            
        } catch (Exception e) {
            log.error("文件上传失败，用户ID: {}, 文件路径: {}, 错误信息: {}", 
                    loginUser.getId(), filepath, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败：" + e.getMessage());
        } finally {
            // 清理临时文件
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    log.warn("临时文件删除失败: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileNameUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        final long TWO_M = 2 * ONE_M;
        
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            // 头像文件校验
            if (fileSize > TWO_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像文件大小不能超过 2MB");
            }
            if (fileSize == 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
            }
            if (!Arrays.asList("jpeg", "jpg", "png", "webp").contains(fileSuffix.toLowerCase())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像只支持 JPEG、JPG、PNG、WebP 格式");
            }
        } else {
            // 其他文件类型的通用校验
            if (fileSize > 10 * ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 10MB");
            }
            if (fileSize == 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
            }
        }
        
        // 检查文件名
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名不能为空");
        }
        
        log.info("文件校验通过，文件名: {}, 大小: {} bytes, 类型: {}", 
                originalFilename, fileSize, fileSuffix);
    }
}
