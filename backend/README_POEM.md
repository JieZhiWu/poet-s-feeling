# 《风的信笺》模块使用说明

## 功能概述

《风的信笺》是一个基于 AI 的诗歌生成和解读模块，用户输入日记、随笔或零散文字，AI 将生成现代诗并提供深度解读。

## 主要功能

1. **诗歌生成**：根据用户输入的文本，生成包含标题、关键词、诗歌正文和推荐作品的完整诗歌
2. **诗歌解读**：对生成的诗歌进行深度分析，包括解读、风格总结、写作灵感建议和相关主题

## API 接口

### 1. 生成诗歌

**POST** `/api/poem/generate`

**请求参数：**
```json
{
  "userId": 123,
  "inputText": "今天下班很晚，回家路上只有昏黄的路灯，我觉得有点孤单，但也看到一只小猫在等我。"
}
```

**响应示例：**
```json
{
  "code": 0,
  "data": {
    "id": 1,
    "poemResponse": {
      "title": "路灯下的暖影",
      "keywords": ["孤单", "夜晚", "路灯", "小猫", "温暖"],
      "poem": "昏黄的灯把街道拉长\n寒风像旧日的信件\n你蜷成一团小小的等待\n在我脚步里，忽然有了方向\n心被一只小小的眼睛点亮",
      "recommendations": [
        "《面朝大海，春暖花开》 —— 海子",
        "《回答》 —— 北岛"
      ]
    }
  },
  "message": "ok"
}
```

### 2. 生成解读报告

**POST** `/api/poem/{id}/analysis`

**响应示例：**
```json
{
  "code": 0,
  "data": {
    "analysis": "这首诗用简练的意象描绘了夜归时的孤独与被温柔打断的瞬间。诗中'昏黄'与'寒风'构成了冷寂的背景，'小猫'成为情感的转折点，给叙事者带来短暂而真实的慰藉，象征着生活中意外出现的温柔与陪伴。",
    "style": "现代抒情，意象化短句，偏向画面感与情绪流动，句式短促，节奏感强。",
    "inspiration": "留心你日常中短暂的温暖瞬间：一杯热茶、街角的灯光、陌生人的微笑。把这些细节写成 1-2 行短句，捕捉瞬间情绪即可。",
    "relatedThemes": ["孤独", "陪伴", "城市夜归", "温暖瞬间"]
  },
  "message": "ok"
}
```

### 3. 获取诗歌详情

**GET** `/api/poem/{id}`

**响应格式：** 同生成诗歌接口的 poemResponse 部分

## 配置说明

### 1. 数据库配置

需要执行 `sql/poem_tables.sql` 创建相关表：
- `poem`：诗歌表
- `poem_analysis`：诗歌解读表

### 2. AI 配置

在 `application.yml` 中配置 DashScope API：

```yaml
langchain4j:
  dashscope:
    api-key: ${DASHSCOPE_API_KEY:your-api-key-here}
    chat-model:
      model-name: qwen-turbo
      temperature: 0.7
      max-tokens: 2000
```

**注意：** 需要设置环境变量 `DASHSCOPE_API_KEY` 或直接替换配置中的 API 密钥。

### 3. Java 版本要求

本模块需要 Java 11+ 支持（用于 LangChain4j 依赖）

## 技术实现

### 核心技术栈

- **LangChain4j**：AI 服务框架
- **阿里云通义千问**：大语言模型
- **Spring Boot**：应用框架
- **MyBatis Plus**：数据持久化
- **MySQL**：数据存储

### 项目结构

```
src/main/java/com/jiewu/mianshigo/
├── config/
│   └── LangChain4jConfig.java          # LangChain4j 配置
├── controller/
│   └── PoemController.java             # 诗歌控制器
├── service/
│   ├── ai/
│   │   ├── PoemGeneratorService.java   # AI 诗歌生成服务
│   │   └── PoemAnalysisAiService.java  # AI 诗歌解读服务
│   ├── PoemService.java                # 诗歌业务服务
│   ├── PoemAnalysisService.java        # 解读业务服务
│   └── impl/                           # 服务实现
├── model/
│   ├── entity/
│   │   ├── Poem.java                   # 诗歌实体
│   │   └── PoemAnalysis.java           # 解读实体
│   └── dto/poem/                       # 传输对象
└── mapper/                             # 数据访问层
```

### AI 提示词设计

严格按照设计文档中的两段式提示词：
1. **System Message**：定义 AI 角色和输出格式要求
2. **User Message**：具体的用户输入内容

### 数据存储

- 原始 AI 响应以 JSON 格式存储在 `ai_response` 字段
- 关键词、推荐作品、相关主题等数组以 JSON 格式存储
- 支持完整的 CRUD 操作和数据回溯

## 错误处理

- **参数校验**：输入文本长度、格式验证
- **AI 响应验证**：确保返回的 JSON 格式符合预期
- **权限控制**：用户只能访问自己的诗歌
- **异常捕获**：AI 服务调用失败的容错处理

## 使用建议

1. **文本输入**：建议 50-500 字的日记或随笔，内容越具体越好
2. **API 调用**：先调用生成接口，再调用解读接口
3. **性能考虑**：AI 调用有一定耗时，建议做好前端 loading 处理
4. **数据管理**：定期清理无效数据，控制存储成本

## 扩展功能

- **流式输出**：可配置流式响应提升用户体验
- **多模型支持**：可扩展支持其他 AI 模型
- **批量处理**：支持批量生成和解读
- **个性化**：基于用户历史数据优化推荐

## 故障排查

1. **AI 服务调用失败**：检查 API 密钥配置和网络连接
2. **JSON 解析错误**：查看 AI 原始响应，可能需要调整提示词
3. **数据库连接问题**：确认数据库配置和表结构
4. **权限相关错误**：确保用户已登录且有相应权限
