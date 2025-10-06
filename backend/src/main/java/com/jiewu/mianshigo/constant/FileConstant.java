package com.jiewu.mianshigo.constant;

/**
 * 文件常量

 */
public interface FileConstant {

    /**
     * 阿里云OSS访问地址
     * 格式：https://存储桶名称.endpoint
     * 例如：https://jw-sky-take-out.oss-cn-beijing.aliyuncs.com
     */
    String OSS_HOST = "https://jw-sky-take-out.oss-cn-beijing.aliyuncs.com";

    /**
     * COS 访问地址（已弃用，改用OSS）
     * @deprecated 请使用 OSS_HOST
     */
    @Deprecated
    String COS_HOST = "https://mianshigo-files-1234567890.cos.ap-beijing.myqcloud.com";
}
