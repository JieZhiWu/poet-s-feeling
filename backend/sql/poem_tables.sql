-- 诗歌模块数据库表创建脚本
USE mianshigo;

-- 诗歌表
CREATE TABLE IF NOT EXISTS `poem` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userId` BIGINT NULL COMMENT '用户id',
  `input_text` LONGTEXT NOT NULL COMMENT '用户原始输入',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '诗歌标题',
  `keywords` JSON DEFAULT NULL COMMENT '关键词数组',
  `poem` LONGTEXT DEFAULT NULL COMMENT '诗歌正文',
  `recommendations` JSON DEFAULT NULL COMMENT '推荐作品',
  `model_name` VARCHAR(128) DEFAULT NULL COMMENT 'AI 模型',
  `prompt` TEXT DEFAULT NULL COMMENT '提示词',
  `ai_response` JSON DEFAULT NULL COMMENT 'AI 原始返回',
  `status` VARCHAR(32) NOT NULL DEFAULT 'done' COMMENT '状态',
  `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`userId`),
  INDEX `idx_create_time` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诗歌记录';

-- 诗歌解读表
CREATE TABLE IF NOT EXISTS `poem_analysis` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `poem_id` BIGINT NOT NULL COMMENT '关联 poem.id',
  `analysis` LONGTEXT NOT NULL COMMENT '诗歌解读',
  `style` VARCHAR(255) DEFAULT NULL COMMENT '风格总结',
  `inspiration` TEXT DEFAULT NULL COMMENT '灵感建议',
  `related_themes` JSON DEFAULT NULL COMMENT '相关主题',
  `model_name` VARCHAR(128) DEFAULT NULL COMMENT 'AI 模型',
  `prompt` TEXT DEFAULT NULL COMMENT '提示词',
  `ai_response` JSON DEFAULT NULL COMMENT 'AI 原始返回',
  `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_poem_id` (`poem_id`),
  INDEX `idx_create_time` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='诗歌解读报告';
