# AI API 故障排查指南

## 问题症状
```
dev.langchain4j.exception.AuthenticationException: {"error":{"message":"Invalid Authentication","type":"invalid_authentication_error"}}
```

## 可能原因及解决方案

### 1. API Key 无效或过期

**检查步骤：**
1. 确认 API Key 是否正确复制（没有多余空格）
2. 登录月之暗面控制台验证 API Key 状态
3. 确认 API Key 没有过期

**解决方案：**
```yaml
langchain4j:
  dashscope:
    api-key: ${MOONSHOT_API_KEY:你的有效API_KEY}
```

### 2. 模型名称错误

**硅基流动平台支持的 Kimi 模型：**
- `moonshotai/Kimi-K2-Instruct-0905`
- `moonshotai/moonshot-v1-8k`
- `moonshotai/moonshot-v1-32k`
- `moonshotai/moonshot-v1-128k`

**当前配置：**
```yaml
chat-model:
  model-name: moonshotai/Kimi-K2-Instruct-0905  # 硅基流动平台的模型名称格式
```

### 3. API 基础 URL 配置

**硅基流动正确的 API 地址：**
```
https://api.siliconflow.cn/v1
```

### 4. 网络连接问题

**检查方法：**
```powershell
# PowerShell 测试硅基流动 API
Invoke-RestMethod -Uri "https://api.siliconflow.cn/v1/chat/completions" `
  -Method POST `
  -Headers @{"Authorization"="Bearer 你的API_KEY"; "Content-Type"="application/json"} `
  -Body '{"model":"moonshotai/Kimi-K2-Instruct-0905","messages":[{"role":"user","content":"Hello"}],"max_tokens":50}'
```

### 5. 配置验证

**应用启动时会自动输出配置检查信息：**
```
=== AI API 配置检查 ===
API Key: sk-wdeamlu***
Model Name: moonshotai/Kimi-K2-Instruct-0905
Base URL: https://api.siliconflow.cn/v1
✅ API Key 格式正确
========================
```

## 常见解决步骤

### 步骤 1：验证 API Key
1. 登录 [硅基流动控制台](https://siliconflow.cn/)
2. 检查 API Key 状态和余额
3. 如有必要，重新生成 API Key

### 步骤 2：更新配置文件
```yaml
langchain4j:
  dashscope:
    # 使用环境变量或直接配置
    api-key: ${SILICONFLOW_API_KEY:sk-你的新API_KEY}
    chat-model:
      model-name: moonshotai/Kimi-K2-Instruct-0905
      temperature: 0.7
      max-tokens: 2000
```

### 步骤 3：重启应用
```bash
mvn spring-boot:run
```

### 步骤 4：测试接口
```bash
# 测试诗歌生成接口
curl -X POST http://localhost:8101/api/poem/generate \
  -H "Content-Type: application/json" \
  -d '{"inputText": "测试文本"}'
```

## 环境变量配置

**推荐方式：使用环境变量**
```bash
# Windows
set MOONSHOT_API_KEY=sk-你的API_KEY

# Linux/Mac
export MOONSHOT_API_KEY=sk-你的API_KEY
```

## 联系支持

如果问题仍然存在：
1. 检查月之暗面官方文档
2. 确认账户状态和配额
3. 联系月之暗面技术支持

## 日志分析

**关键日志信息：**
- 启动时的 API 配置检查
- 请求和响应日志（已启用）
- 错误堆栈信息

**调试模式：**
```yaml
logging:
  level:
    dev.langchain4j: DEBUG
    com.jiewu.mianshigo: DEBUG
```
