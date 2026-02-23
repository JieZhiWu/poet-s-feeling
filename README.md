# 芥雾浮心 - 诗歌创作与阅读平台

## 📖 项目简介

**芥雾浮心** 是一个诗歌阅读、AI智能创作的现代化Web应用平台。项目名称寓意"借虚假之物，愿一丝暖意浮于心间"，旨在为用户提供一个诗歌阅读和记录空间。



### 该项目现已下线。由于过去AI使用的干扰，个人将项目前后端代码重写并增添新功能。若关心其网页内容，以下是过去部分的页面截图。


#### 主页：

![image-20251019193052418](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019193052418.png)

---

![image-20251019193218873](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019193218873.png)

---

![image-20251019194142886](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019194142886.png)

---

##### 当页面发生调整时，内容显示动态调整![image-20251019193946077](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019193946077.png)

---

---

#### AI功能：文本转诗歌![image-20251019195047234](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019195047234.png)

---

![image-20251019195733664](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019195733664.png)

![image-20251019195823969](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019195823969.png)

---

![image-20251019195941641](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019195941641.png)

---

![image-20251019200025020](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019200025020.png)

---

#### 诗歌发布交流平台：![image-20251019200302873](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019200302873.png)

<img src="https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019200531632.png" alt="image-20251019200531632"  />

---

---

#### 诗人/诗歌展示

![image-20251019200922700](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019200922700.png)

---

![image-20251019201012473](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019201012473.png)

![image-20251019201114979](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019201114979.png)

---

![image-20251019201225595](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019201225595.png)

![image-20251019201330455](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019201330455.png)

---

#### 诗歌大全页面：![image-20251019201409899](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019201409899.png)

---

#### 管理页面（若用户不是管理员，将不显示该导航栏）

![image-20251019201758949](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019201758949.png)

---

#### 个人信息页：（BitSet 阅读签到记录）

![image-20251019201921643](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019201921643.png)

---

#### 搜索：（es模糊搜索）

![image-20251019202449081](https://poem-s-feeling.oss-cn-beijing.aliyuncs.com/image-20251019202449081.png)



### 🎯 核心价值

- **诗歌知识库**：精选中外经典诗人及其代表作品，提供诗歌在线阅读平台
- **AI创作辅助**：基于大语言模型的智能诗歌生成与解读功能（《风之札记》模块）

## 🌟 核心功能模块

### 1. 诗歌知识库管理
- **诗人库**：收录中外知名诗人，包括北岛、顾城、佩阿索、泰戈尔等
- **诗歌库**：每位诗人对应其经典作品，包含诗歌正文、创作背景、风格分析
- **标签分类**：支持按流派（朦胧派、浪漫主义）、主题（爱情、哲理）、风格分类检索
- **搜索功能**：基于Elasticsearch的全文搜索，支持模糊匹配和智能推荐

### 2. AI诗歌生成《风的信笺》

用户输入日常随笔或情感文字，AI自动生成现代诗：
- **智能创作**：根据用户输入生成包含标题、关键词、诗歌正文的完整作品
- **风格推荐**：推荐与生成诗歌风格相似的经典作品
- **深度解读**：提供AI生成的诗歌分析、风格总结、写作灵感建议
- **历史记录**：保存用户的创作历史，支持回溯和二次编辑

**技术实现**：
- 集成月之暗面Kimi大语言模型
- 使用LangChain4j框架进行AI服务编排
- 精心设计的提示词工程（Prompt Engineering）

### 3. 用户系统
- **多端登录**：支持账号密码登录登录
- **权限管理**：用户/管理员角色区分，明确权限控制
- **个人中心**：用户资料管理、创作历史查看

### 4. 内容管理系统（CMS）

管理员后台功能：
- 诗人/诗歌的增删改查
- 用户管理与权限分配
- 文件(头像)上传管理（支持阿里云OSS）

---

## 💻 技术架构

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| **Spring Boot** | 2.7.2 | 核心框架 |
| **Java** | 17 | 开发语言 |
| **MyBatis Plus** | 3.5.2 | ORM框架，简化数据库操作 |
| **MySQL** | 8.x | 主数据库 |
| **Redis** | - | 缓存、Session管理 |
| **Redisson** | 3.21.0 | 分布式锁、限流 |
| **Elasticsearch** | 7.x | 全文搜索引擎 |
| **LangChain4j** | 1,4.0 | AI服务框架 |
| **Kimi** | - | 大语言模型API |
| **Knife4j** | 4.4.0 | API文档（Swagger增强版） |
| **Druid** | 1.2.23 | 数据库连接池 |
| **对象存储**      | -      | 阿里云OSS                |

### 前端技术栈

| 技术 | 用途 |
|------|------|
| **Next.js** | React服务端渲染框架 |
| **React** | 前端框架 |
| **TypeScript** | 类型安全的开发语言 |
| **Ant Design** | 企业级UI组件库 |
| **Ant Design Pro Components** | 高级业务组件 |
| **Axios** | HTTP客户端 |

### 架构设计亮点

#### 1. 前后端分离架构

- **后端**：提供RESTful API，使用Knife4j生成在线文档
- **前端**：Next.js SSR渲染，提升SEO和首屏加载速度
- **跨域处理**：统一的CORS配置和请求拦截器

#### 2. 多级缓存架构

- **Redis缓存**：热点数据缓存，降低数据库压力
- **Redisson分布式锁**：防止缓存击穿
- **JD HotKey**：热点数据本地缓存（集成hotkey-client）

#### 3. 搜索引擎集成

- Elasticsearch全文索引
- 同步机制：MySQL数据变更自动同步到ES
- 复杂查询优化：支持模糊搜索、分词匹配

#### 4. AI服务集成

```java
// AI服务调用流程
用户输入文本 
  → PoemController 接收请求
  → PoemService 业务逻辑
  → PoemGeneratorService (LangChain4j)
  → 调用AI模型API
  → 解析JSON响应
  → 存储到MySQL
  → 返回前端渲染
```

#### 5. 安全机制

- **AOP拦截器**：权限校验、日志记录
- **Session管理**：Spring Session + Redis分布式会话
- **参数校验**：使用Hibernate Validator
- **SQL注入防护**：MyBatis Plus预编译

---

## 📊 数据库设计

### 核心表结构

#### 用户表（user）

存储用户基本信息、登录凭证、权限角色

#### 诗人表（question_bank）

| 字段 | 说明 |
|------|------|
| id | 诗人ID |
| title | 诗人名称 |
| description | 诗人简介 |
| picture | 诗人头像 |

#### 诗歌表（question）

| 字段 | 说明 |
|------|------|
| id | 诗歌ID |
| title | 诗歌标题 |
| content | 诗歌解读/背景 |
| answer | 诗歌正文 |
| tags | 标签（JSON数组） |

#### 诗人-诗歌关联表（question_bank_question）

多对多关系，一个诗人可对应多首诗歌

#### AI诗歌表（poem）

| 字段 | 说明 |
|------|------|
| id | 诗歌ID |
| user_id | 创作用户 |
| title | AI生成标题 |
| poem | 诗歌正文 |
| keywords | 关键词（JSON） |
| recommendations | 推荐作品（JSON） |
| ai_response | 完整AI响应（JSON） |

#### 诗歌解读表（poem_analysis）

存储AI生成的诗歌分析报告

---

## 🎨 项目特色与创新点

### 1. 《风之札记》AI创作模块 ⭐

- **创新点**：将日常随笔转化为现代诗，降低诗歌创作门槛
- **技术亮点**：
  - 精心设计的两段式提示词（System Message + User Message）
  - 结构化输出：AI返回JSON格式，便于解析和存储
  - 数据持久化：完整保存AI原始响应，支持后续优化
- **用户价值**：非专业人士也能体验诗歌创作乐趣

### 2. 优雅的UI设计

- **响应式布局**：完美适配PC、平板、手机
- **暗色主题**：护眼且高级感十足
- **动画效果**：Ant Design动效，流畅的交互体验
- **Markdown渲染**：诗歌展示支持富文本格式

### 3. 性能优化

- **分页加载**：大数 据量下的流畅体验
- **懒加载**：图片按需加载

### 4. 可扩展性

- **微服务预留**：代码结构清晰，易于拆分
- **多数据源支持**：Druid连接池，支持主从分离
- **多云存储**：同时支持阿里云OSS和腾讯云COS

---

##  未来规划

### 功能扩展

- [ ] 诗歌社区：用户可发布原创作品，互相点评交友
- [ ] 诗歌朗诵：集成TTS语音合成，AI朗诵诗歌
- [ ] 个性化推荐：基于用户阅读历史推荐诗歌
- [ ] 移动端APP：React Native开发移动应用
- [ ] 多语言支持：英文界面，面向国际用户

### 技术优化

- [ ] 微服务改造：拆分为用户服务、诗歌服务、AI服务
- [ ] 消息队列：RabbitMQ异步处理AI生成任务
- [ ] 监控告警：Prometheus + Grafana监控
- [ ] 自动化测试：单元测试覆盖率80%+
- [ ] CI/CD流水线：GitHub Actions自动部署

