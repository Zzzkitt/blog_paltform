# 个人博客平台

基于 Spring Boot 4 + Vue 3 的前后端分离个人博客系统。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 4.0.6 + JDK 17 |
| ORM | Spring Data JPA (Hibernate) |
| 数据库 | MySQL 8.0 |
| 安全认证 | Spring Security + JWT |
| API 文档 | SpringDoc OpenAPI 3.0 |
| 前端框架 | Vue 3 + Vite |
| UI 组件库 | Element Plus |
| 状态管理 | Pinia |
| Markdown | mavon-editor / markdown-it + highlight.js |
| 评论系统 | Giscus (GitHub Discussions) |
| 部署 | Docker + Docker Compose |

## 功能

- ✅ 文章发布与管理（Markdown 编辑器，代码高亮）
- ✅ 分类与标签管理
- ✅ JWT 用户认证（登录/登出）
- ✅ 前台展示（首页、文章详情、归档、搜索）
- ✅ 全文检索（MySQL ngram）
- ✅ RSS 订阅
- ✅ Giscus 评论系统
- ✅ 友链管理
- ✅ 关于页面
- ✅ 站点设置
- ✅ 后台仪表盘
- ✅ Docker 一键部署

## 本地开发

### 前置条件

- JDK 17+
- Node.js 20+
- MySQL 8.0

### 启动后端

```bash
cd blog
./mvnw spring-boot:run
# 默认使用 dev 环境，数据库密码 root
```

### 启动前端

```bash
cd blog-client
npm install
npm run dev
# 访问 http://localhost:5173
```

### 访问地址

| 地址 | 说明 |
|------|------|
| http://localhost:5173 | 前端开发服务器 |
| http://localhost:8080/swagger-ui.html | API 文档 |
| http://localhost:8080/api/auth/login | 登录接口 |

默认管理员账号：`admin` / `admin123`

## Docker 部署

```bash
# 1. 克隆项目
git clone https://github.com/Zzzkitt/blog_paltform.git
cd blog_paltform

# 2. 配置环境变量
cp .env.example .env
# 编辑 .env，填入 MYSQL_ROOT_PASSWORD 和 JWT_SECRET

# 3. 构建并启动
docker compose up -d --build

# 4. 访问 http://localhost
```

## 项目结构

```
blog-platform/
├── blog/                    # Spring Boot 后端
│   ├── src/main/java/       # Java 源码
│   └── src/main/resources/  # 配置文件
├── blog-client/             # Vue 3 前端
│   └── src/                 # 前端源码
├── deploy/                  # 部署配置
│   └── nginx/               # Nginx 配置
├── docker-compose.yml       # Docker 编排
└── .env.example             # 环境变量模板
```
## 说明

本项目在开发过程中借助了 AI 编程工具来辅助完成。
* **代码生成** ：部分业务逻辑、控制器、工具类代码由 AI 生成后进行修改和优化。
* **测试用例** ：在测试过程中遇到许多问题，例如数据库连接、事务管理等，AI 生成的测试用例无法直接运行，需要手动调整和修复；同时自己的测试用例出现问题也使用 AI 进行修复和优化。
* **项目文档** ：其结构和部分内容也由 AI 参与生成。