# 个人博客平台 — 分阶段开发步骤细化

> 基于《Springboot博客平台开发规划.md》第八点细化，将每个阶段拆解到具体文件级粒度  
> 适用工具：IntelliJ IDEA · MySQL 8.0 · DBeaver · Postman

---

## 目录

- [前置准备：开发环境搭建](#前置准备开发环境搭建)
- [第一阶段：项目初始化与基础框架（2-3天）](#第一阶段项目初始化与基础框架2-3天)
- [第二阶段：认证系统（2天）](#第二阶段认证系统2天)
- [第三阶段：文章管理（3-4天）](#第三阶段文章管理3-4天)
- [第四阶段：前台展示页（3-4天）](#第四阶段前台展示页3-4天)
- [第五阶段：Giscus 评论集成（半天）](#第五阶段giscus-评论集成半天)
- [第六阶段：辅助功能（2天）](#第六阶段辅助功能2天)
- [第七阶段：Docker + CI/CD 部署（2天）](#第七阶段docker--ci-cd部署2天)
- [第八阶段：优化与收尾（1-2天）](#第八阶段优化与收尾1-2天)
- [附录：工具使用指南](#附录工具使用指南)
- [开发策略建议](#开发策略建议)

---

## 前置准备：开发环境搭建

### □ IDEA 配置

| # | 操作 | 说明 |
|---|------|------|
| 0.1 | 安装插件：Lombok、Spring Boot Helper、.env files support | IDEA 插件市场安装 |
| 0.2 | 配置 JDK 17：File → Project Structure → SDK → 选 JDK 17 | 确保 JDK 17 已安装 |
| 0.3 | 配置 Maven：File → Settings → Build → Build Tools → Maven | 国内用户建议配阿里云镜像 |
| 0.4 | 开启 Annotation Processing：Settings → Build → Compiler → Annotation Processors → Enable | 否则 Lombok 不生效 |
| 0.5 | 安装建议插件：GitToolBox（git 信息展示）、MyBatis（JPA 也有提示帮助） | — |

### □ MySQL 初始化

```sql
-- 用 DBeaver 或 IDEA Database 工具执行
CREATE DATABASE blog
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
```

> **DBeaver 连接配置：** 新建连接 → MySQL  
> 主机 `localhost:3306` → 数据库 `blog`  
> 驱动属性中设置 `allowPublicKeyRetrieval=true` + `useSSL=false`

### □ Postman 准备

| 操作 | 说明 |
|------|------|
| 创建工作区 `BlogPlatform` | Postman 左侧 Workspaces → New |
| 新建 Collection `Blog API` | 后续每写完一组接口就往里加请求 |
| 新建环境变量 `blog-dev` | 定义 `{{base_url}}` = `http://localhost:8080`、`{{token}}` = 空 |
| 登录后自动保存 token | 在 Login 请求的 Tests 标签中写脚本（见附录） |

### □ 项目结构预览

```
blog-platform/
├── blog-server/              # Spring Boot 后端
│   ├── src/main/java/com/blog/
│   │   ├── BlogApplication.java
│   │   ├── config/              # 配置类（Security、CORS、WebMvc、OpenAPI）
│   │   ├── controller/          # REST 控制器
│   │   ├── service/             # 业务逻辑层
│   │   ├── repository/          # JPA 数据访问层
│   │   ├── entity/              # JPA 实体
│   │   ├── dto/                 # 请求 DTO（含校验注解）
│   │   ├── vo/                  # 响应 VO
│   │   ├── common/              # Result、异常、常量
│   │   ├── security/            # JWT + Spring Security
│   │   └── enums/               # 枚举类
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── application-prod.yml
├── blog-client/              # Vue 3 前端
│   └── src/
│       ├── api/              # API 请求封装
│       ├── assets/           # 静态资源
│       ├── components/       # 公共组件
│       ├── layouts/          # 布局组件
│       ├── router/           # 路由配置
│       ├── stores/           # Pinia 状态管理
│       ├── views/
│       │   ├── admin/        # 后台管理页面
│       │   └── public/       # 前台展示页面
│       ├── utils/            # 工具函数（含 axios 封装）
│       └── styles/           # 全局样式
├── docker-compose.yml        # Docker 编排
├── .github/workflows/deploy.yml
└── deploy/
    └── nginx/                # Nginx 配置
```

---

## 第一阶段：项目初始化与基础框架（2-3天）

**目标：** 搭建 monorepo 骨架，两端都能启动并连通

### Day 1 — 后端项目脚手架

| # | 具体操作 | 关键文件 |
|---|---------|---------|
| 1.1 | 前往 [start.spring.io](https://start.spring.io) 生成项目，选 Java 17、Spring Boot 3.x，勾选 Web、JPA、MySQL、Security、Validation、Lombok | `blog-server/pom.xml` |
| 1.2 | 手动添加 jjwt (0.12.5)、commonmark (0.22.0)、rome (2.1.0)、springdoc (2.6.0) 到 pom.xml | `blog-server/pom.xml` |
| 1.3 | 创建完整包结构（在 `com.blog` 下建 10 个 package） | `config/`, `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `vo/`, `common/`, `security/`, `enums/` |
| 1.4 | 编写 `Result<T>` — 链式调用风格：`Result.success(data)` / `Result.error(code, msg)`，包含 `code`, `message`, `data`, `timestamp` 四个字段 | `common/Result.java` |
| 1.5 | 编写 `GlobalExceptionHandler` — 覆盖 `MethodArgumentNotValidException`、`EntityNotFoundException`、`AccessDeniedException`、`BadCredentialsException`、通用 `Exception` | `common/GlobalExceptionHandler.java` |
| 1.6 | 编写 `PageVO<T>` — `records`, `total`, `page`, `pageSize`, `totalPages`，构造方法接受 Spring 的 `Page<T>` 做自动转换 | `vo/PageVO.java` |
| 1.7 | 配置 `application.yml` + `application-dev.yml` + `application-prod.yml` 三套环境 | `resources/application*.yml` |
| 1.8 | 创建 `BlogApplication.java` 启动类，添加 `@EnableJpaAuditing`，跑一次验证 Spring Boot 能正常启动 | — |

> **验收：** `mvn spring-boot:run` 启动成功，访问 `http://localhost:8080/swagger-ui.html` 能看到 Swagger 页面（空 API 列表）

### Day 2 — 前端项目脚手架

| # | 具体操作 | 关键文件 |
|---|---------|---------|
| 1.9 | `npm create vite@latest blog-client -- --template vue` | `blog-client/package.json` |
| 1.10 | 安装全部依赖：`vue-router@4 pinia axios element-plus @element-plus/icons-vue dayjs nprogress` | `package.json` |
| 1.11 | 配置 Vite 代理：`server.proxy` 将 `/api` 转发到 `http://localhost:8080`（解决跨域） | `vite.config.js` |
| 1.12 | 封装 Axios：`request.js` — 拦截器逻辑完整写好（baseURL 读环境变量、请求拦截注入 token、响应拦截提取 `response.data`、401 处理） | `utils/request.js` |
| 1.13 | 创建 `.env.development` + `.env.production`，放入 `VITE_API_BASE_URL=/api` | 根目录 |
| 1.14 | 清理默认模板代码，保留干净入口 `main.js`（只保留 import + createApp + use(router) + use(pinia) + mount） | `src/main.js` |
| 1.15 | 添加 Element Plus 按需引入（使用 `unplugin-vue-components` + `unplugin-auto-import`） | `vite.config.js` |
| 1.16 | 编写 `.gitignore`（覆盖 Java 和 Node 的标准忽略模式） | 项目根目录 |

> **验收：** `npm run dev` 启动成功，浏览器打开能看到空白 Vue 页面，控制台无报错

### Day 3 — 前后端连通 + Git

| # | 具体操作 | 关键文件 |
|---|---------|---------|
| 1.17 | 后端写一个测试 Controller `GET /api/ping` 返回 `Result.success("pong")` | `controller/PingController.java` |
| 1.18 | 前端在 `App.vue` 的 `onMounted` 中调用 `/api/ping`，控制台打印结果 | `App.vue` |
| 1.19 | 用 Postman 测试 `GET http://localhost:8080/api/ping`，确认返回 `{"code":200,"data":"pong"}` | — |
| 1.20 | `git init` → `git add .` → `git commit -m "feat: project init"` | — |

> **验收：** 前端 F12 看到 ping 请求成功返回 `{code: 200, message: "success", data: "pong"}`

---

## 第二阶段：认证系统（2天）

**目标：** 管理员登录 + JWT 签发 + 前后端认证闭环

### Day 4 — 后端认证

| # | 核心代码逻辑 | 文件 |
|---|------------|------|
| 2.1 | `User` 实体 — `id`, `username`, `password`, `nickname`, `email`, `avatar`, `role`（ADMIN/VISITOR），注意 `@Enumerated(EnumType.STRING)` 序列化 | `entity/User.java` |
| 2.2 | `UserRole` 枚举 — `ADMIN`, `VISITOR` | `enums/UserRole.java` |
| 2.3 | `UserRepository extends JpaRepository` + `Optional<User> findByUsername(String)` | `repository/UserRepository.java` |
| 2.4 | `JwtTokenProvider` — 核心三方法：`generateToken`（HS512 签名，放入 username + role）、`getUsernameFromToken`、`validateToken`。密钥和过期时间从配置读取 | `security/JwtTokenProvider.java` |
| 2.5 | `JwtAuthenticationFilter extends OncePerRequestFilter` — 从 `Authorization` 头提取 Bearer token → 验证 → 构建 `UsernamePasswordAuthenticationToken` → 写入 `SecurityContextHolder` | `security/JwtAuthenticationFilter.java` |
| 2.6 | `SecurityConfig` — `@Bean SecurityFilterChain`：禁用 CSRF、`STATELESS` session、`permitAll` 公开路径列表、`authenticated` 管理路径、注册 Filter | `config/SecurityConfig.java` |
| 2.7 | `AuthController` — `POST /api/auth/login` 接收 `LoginRequest(username, password)`，返回 `Result.success(jwtResponse)` | `controller/AuthController.java` |
| 2.8 | `AuthService` — `login()` 方法：调用 `AuthenticationManager` → 成功生成 JWT → 返回带 token 的响应 | `service/AuthService.java` |
| 2.9 | `LoginRequest` DTO — `@NotBlank` 校验 | `dto/LoginRequest.java` |
| 2.10 | `JwtResponse` DTO — `token`, `tokenType`, `expiresIn` | `dto/JwtResponse.java` |
| 2.11 | `DataInitializer implements CommandLineRunner` — 首次启动检测 admin 用户，不存在则创建（BCrypt 加密，密码从配置读取） | `config/DataInitializer.java` |
| 2.12 | 密码加密：`SecurityConfig` 中暴露 `@Bean PasswordEncoder`（BCryptPasswordEncoder） | — |

> **验收：** 用 Postman 测试 `POST /api/auth/login` 返回 JWT token。用 DBeaver 查看 `blog_user` 表确认管理员已创建

### Day 5 — 前端认证

| # | 核心逻辑 | 文件 |
|---|---------|------|
| 2.13 | `LoginPage.vue` — Element Plus `<el-form>`，用户名+密码输入框，提交按钮调用 `authStore.login()`，成功后跳转 | `views/admin/LoginPage.vue` |
| 2.14 | `authStore` — Pinia store：`state: token`（从 localStorage 读取）、`isLoggedIn`（getter: !!token）、`login()`（调 API 存 token）、`logout()`（清 token 跳 /login） | `stores/auth.js` |
| 2.15 | `router/index.js` — 定义 `/login` 和 `/admin` 路由 | `router/index.js` |
| 2.16 | 路由守卫 `router.beforeEach` — `to.meta.requiresAuth && !authStore.isLoggedIn` → 跳 `/login?redirect=xxx` | `router/index.js` |
| 2.17 | Axios 请求拦截器：从 localStorage 取 token，存在则加 `Authorization: Bearer xxx` Header | `utils/request.js` |
| 2.18 | Axios 响应拦截器：`status === 401` → 清除 token → 跳 `/login` | `utils/request.js` |

> **验收：** 前端访问 `/login` 能正常显示登录表单，用 admin 账号能登录成功，localStorage 出现 token，再访问需认证页面不会跳回登录页

---

## 第三阶段：文章管理（3-4天）

**目标：** 完成文章 CRUD + 分类标签管理，后端 Swagger 可测，前端后台可用

### Day 6 — 后端实体 + Repository

| # | 要点 | 文件 |
|---|------|------|
| 3.1 | `Article` 实体 — 字段：`id`, `title`, `summary`(VARCHAR 500), `content`(`@Lob @Column(columnDefinition = "LONGTEXT")`), `coverImage`, `status`(DRAFT/PUBLISHED), `category`(`@ManyToOne`), `tags`(`@ManyToMany`), `isTop`(Boolean, default false), `viewCount`(Integer, default 0), `createdAt/updatedAt`(`@CreatedDate/@LastModifiedDate`), `publishedAt` | `entity/Article.java` |
| 3.2 | `ArticleStatus` 枚举 — `DRAFT`, `PUBLISHED` | `enums/ArticleStatus.java` |
| 3.3 | `Category` 实体 — `id`, `name`(唯一), `slug`(唯一), `description`, `sortOrder` | `entity/Category.java` |
| 3.4 | `Tag` 实体 — `id`, `name`(唯一), `slug`(唯一) | `entity/Tag.java` |
| 3.5 | `ArticleRepository` — 关键查询：`findByStatusOrderByCreatedAtDesc`、`findByCategory_Slug`、`findByTags_Slug`、`findByStatusAndPublishedAtBetween`、`countByStatus`、`findTop5ByStatusOrderByCreatedAtDesc`（侧边栏用） | `repository/ArticleRepository.java` |
| 3.6 | `CategoryRepository` — `findBySlug` | `repository/CategoryRepository.java` |
| 3.7 | `TagRepository` — 同上 | `repository/TagRepository.java` |
| 3.8 | 在 DBeaver 中执行全文索引 SQL：`CREATE FULLTEXT INDEX ft_article_title_content ON article(title, content) WITH PARSER ngram;` | DBeaver |

> **验收：** JPA 启动后自动建表，用 DBeaver 检查 `article`、`category`、`tag`、`article_tag` 四张表结构正确，确认 `cover_image`、`is_top`、`view_count`、`summary` 字段存在

### Day 7 — 后端 Service + Controller

| # | 要点 | 文件 |
|---|------|------|
| 3.9 | `ArticleService` — CRUD + 分页查询（`Pageable` 参数）+ 按分类/标签筛选 + Markdown→HTML 转换（注入 CommonMark 的 `Parser` + `HtmlRenderer`）。发布时自动设 `publishedAt` | `service/ArticleService.java` |
| 3.10 | `CategoryService` + `TagService` — CRUD，删除时检查是否有文章引用 | `service/CategoryService.java`, `service/TagService.java` |
| 3.11 | `ArticleController`（管理端）— `@RequestMapping("/api/admin/articles")`，Restful CRUD：列表/详情/新建/更新/删除/切换状态 | `controller/ArticleController.java` |
| 3.12 | `CategoryController` + `TagController`（管理端） | `controller/CategoryController.java`, `controller/TagController.java` |
| 3.13 | DTO 类 — `ArticleCreateRequest`（`@NotBlank title`、`@NotNull content`、`status`、`categoryId`、`tagIds`、`coverImage`、`summary`、`isTop`）、`ArticleUpdateRequest`、`ArticleVO`（响应体） | `dto/*.java` |

> **验收：** Swagger UI 上所有 `/api/admin/articles` 接口可调用，用 JWT token 认证后能创建、查询、编辑、删除文章

### Day 8 — 前端后台布局 + 文章管理页

| # | 要点 | 文件 |
|---|------|------|
| 3.14 | `AdminLayout.vue` — 左右布局：左侧固定宽度的 `AdminSidebar`（`<el-menu>`），右侧 `router-view` + 顶部面包屑 | `layouts/AdminLayout.vue` |
| 3.15 | `AdminSidebar.vue` — `<el-menu>`，菜单项分组：内容管理（文章/分类/标签）、系统（友链/关于/设置）、仪表盘 | `components/admin/AdminSidebar.vue` |
| 3.16 | `ArticleList.vue` — `<el-table>` 展示文章列表，支持分页、按状态筛选、搜索标题、编辑/删除按钮 | `views/admin/ArticleList.vue` |
| 3.17 | `ArticleEditor.vue` — 两个模式：新建/编辑（通过路由有无 `:id`）。集成 `mavon-editor`（`v-model`）、标题输入、分类下拉、标签多选、封面图 URL 输入、置顶开关、发布/存草稿按钮 | `views/admin/ArticleEditor.vue` |
| 3.18 | 对应的 API 封装：`api/article.js` — `getList`, `getById`, `create`, `update`, `deleteById`, `updateStatus` | `api/article.js` |

> **验收：** 在后台能创建一篇文章（写标题 + Markdown 内容 + 选分类标签），发布后能显示在文章列表

### Day 9 — 前端分类标签管理

| # | 要点 | 文件 |
|---|------|------|
| 3.19 | `CategoryList.vue` — `<el-table>` + `<el-dialog>` 实现新增/编辑/删除 | `views/admin/CategoryList.vue` |
| 3.20 | `TagList.vue` — 同上 | `views/admin/TagList.vue` |
| 3.21 | API 封装：`api/category.js`, `api/tag.js` | `api/*.js` |
| 3.22 | 完善后台路由配置，子路由全部配置完成 | `router/index.js` |

> **验收：** 能在后台管理界面完成分类和标签的增删改查

---

## 第四阶段：前台展示页（3-4天）

**目标：** 博客前台全部可见，文章列表、详情、搜索、归档完整可用

### Day 10 — 后端公开 API

| # | 要点 | 文件 |
|---|------|------|
| 4.1 | `PublicArticleController` — `GET /api/articles`（分页，只返回 PUBLISHED，置顶在前）、`GET /api/articles/{id}`（+1 阅读数）、`GET /api/articles/archive`（按年月分组）、`GET /api/articles/top`（置顶文章） | `controller/PublicArticleController.java` |
| 4.2 | `PublicCategoryController` — `GET /api/categories`（含每分类文章数）、`GET /api/categories/{slug}/articles`（分页） | `controller/PublicCategoryController.java` |
| 4.3 | `PublicTagController` — 同上 | `controller/PublicTagController.java` |
| 4.4 | `SearchController` — `GET /api/articles/search?q=xxx`，MySQL `MATCH...AGAINST` 全文检索（nativeQuery + ngram 解析器支持中文） | `controller/SearchController.java` |
| 4.5 | `RssController` — 使用 Rome 库构建 `SyndFeed`，输出 `application/rss+xml; charset=utf-8` | `controller/RssController.java` |
| 4.6 | `SiteInfoController` — `GET /api/site-info` 返回博客标题、描述等（从 `site_setting` 表读取） | `controller/SiteInfoController.java` |

> **验收：** 浏览器访问 `http://localhost:8080/api/articles` 能看到已发布文章的 JSON 列表，`http://localhost:8080/api/rss` 返回 RSS XML

### Day 11 — 前台公共组件

| # | 要点 | 文件 |
|---|------|------|
| 4.7 | `AppHeader.vue` — Logo/站点名（左侧），导航菜单（首页/归档/关于/友链），搜索框（右侧，回车跳转搜索页） | `components/AppHeader.vue` |
| 4.8 | `AppFooter.vue` — 版权信息 `© 2026 YourName`，RSS 订阅图标 | `components/AppFooter.vue` |
| 4.9 | `Sidebar.vue` — 三个区块：分类列表（带文章数 badge）、标签云（随机大小颜色）、最新文章列表（5 篇） | `components/Sidebar.vue` |
| 4.10 | `ArticleCard.vue` — 文章卡片：封面图（左/上，无封面用默认占位图）、标题、摘要（前 150 字）、发布时间、标签 chips，点击跳详情 | `components/ArticleCard.vue` |
| 4.11 | `Pagination.vue` — 封装 Element Plus `<el-pagination>` | `components/Pagination.vue` |
| 4.12 | `BreadCrumb.vue` — 面包屑导航 | `components/BreadCrumb.vue` |
| 4.13 | 全局样式：`styles/global.css` — CSS 变量（`--primary-color` 等）、基础重置、字体栈 | `styles/global.css` |

> **验收：** 首页能看到布局完整的页面结构（顶栏 + 侧边栏 + 内容区 + 底部）

### Day 12 — 前台核心页面

| # | 要点 | 文件 |
|---|------|------|
| 4.14 | `HomePage.vue` — 置顶文章（`/api/articles/top`）+ 分页文章列表（`ArticleCard` 循环） | `views/public/HomePage.vue` |
| 4.15 | `ArticleDetail.vue` — 使用 `markdown-it` + `highlight.js` 渲染 Markdown，生成 TOC 目录导航，显示分类/标签/发布时间/阅读数，底部嵌入 GiscusComments | `views/public/ArticleDetail.vue` |
| 4.16 | `Archives.vue` — 时间线样式，按"2026年6月 (3篇)" → 文章列表渲染 | `views/public/Archives.vue` |
| 4.17 | `CategoryArticles.vue` + `TagArticles.vue` — 根据 slug 请求文章列表 | `views/public/*.vue` |
| 4.18 | `SearchPage.vue` — 搜索输入框 + 结果列表，关键词高亮 | `views/public/SearchPage.vue` |
| 4.19 | API 封装：`api/public.js` — 文章列表、详情、归档、筛选、搜索、站点信息 | `api/public.js` |

> **验收：** 完整的浏览体验：首页 → 点文章 → 看详情 → 看分类/标签下的文章列表 → 搜索文章 → 归档页

---

## 第五阶段：Giscus 评论集成（半天）

> **前置条件：** 博客项目已推送到 GitHub 仓库

| # | 具体操作 |
|---|---------|
| 5.1 | 在博客项目 GitHub 仓库 Settings → General → Features → 启用 **Discussions** |
| 5.2 | 安装 [Giscus App](https://github.com/apps/giscus) 到该仓库 |
| 5.3 | 访问 [giscus.app](https://giscus.app) → 输入仓库名 → 选择 `specific` mapping → 获取 `data-repo-id`、`data-category-id`、`data-category` |
| 5.4 | 将配置写入 `.env` 和 `.env.production`：`VITE_GISCUS_REPO`, `VITE_GISCUS_REPO_ID`, `VITE_GISCUS_CATEGORY`, `VITE_GISCUS_CATEGORY_ID` |
| 5.5 | 完善 `GiscusComments.vue` — 使用 `data-mapping="specific"` + `data-term="article-{id}"`，每篇文章一个 discussion。监听 `articleId` prop 变化重新加载 | `components/GiscusComments.vue` |
| 5.6 | 在 `ArticleDetail.vue` 文章内容下方嵌入 `<GiscusComments :article-id="article.id" />` | — |

> **验收：** 打开一篇已发布文章，底部能看到 Giscus 评论区，能发评论并同步到 GitHub Discussions

---

## 第六阶段：辅助功能（2天）

**目标：** 友链、关于、站点设置、仪表盘 — 前后端完整闭环

### Day 13 — 后端辅助 API

| # | 要点 | 文件 |
|---|------|------|
| 6.1 | `FriendLink` 实体 — `id`, `name`, `url`, `logo`, `description`, `sortOrder`, `isVisible`(Boolean), `createdAt` | `entity/FriendLink.java` |
| 6.2 | `FriendLinkRepository` — `findByIsVisibleTrueOrderBySortOrderAsc` | `repository/FriendLinkRepository.java` |
| 6.3 | `FriendLinkService` + Controller（管理端 CRUD + 公开查询 `GET /api/friend-links`） | `service/FriendLinkService.java`, `controller/FriendLinkController.java` |
| 6.4 | `AboutInfo` 实体 — `id`, `content`(LONGTEXT, Markdown), `updatedAt` | `entity/AboutInfo.java` |
| 6.5 | `AboutController` — `GET /api/about`(公开) + `PUT /api/admin/about`(管理端更新) | `controller/AboutController.java` |
| 6.6 | `SiteSetting` 实体 — `id`, `configKey`(唯一), `configValue`(TEXT) | `entity/SiteSetting.java` |
| 6.7 | `SiteSettingService` — `getAllSettings()` 返回 Map，`bulkUpdate(Map)` 批量更新 | `service/SiteSettingService.java` |
| 6.8 | `SiteSettingController` — `GET /api/admin/site-settings`(全部) + `POST /api/admin/site-settings`(批量更新) + `GET /api/site-info`(公开) | `controller/SiteSettingController.java` |
| 6.9 | `DashboardController` — `GET /api/admin/dashboard` 聚合返回：文章总数/已发布/草稿数、分类数、标签数、近7日发布趋势、总访问量 | `controller/DashboardController.java` |
| 6.10 | `DashboardService` — 聚合查询逻辑 | `service/DashboardService.java` |

> **验收：** 所有新增接口在 Swagger 上可调。用 DBeaver 插入几条测试数据后确认仪表盘数据正确

### Day 14 — 前端辅助页面

| # | 要点 | 文件 |
|---|------|------|
| 6.11 | `LinkList.vue` — 友链表格 + 新增/编辑对话框 | `views/admin/LinkList.vue` |
| 6.12 | `AboutEdit.vue` — 简单的 Markdown 编辑器（mavon-editor 或 textarea + 预览） | `views/admin/AboutEdit.vue` |
| 6.13 | `SiteSettings.vue` — 动态表单：从后端 GET 配置 key-value → 渲染表单项 → 保存时批量 POST | `views/admin/SiteSettings.vue` |
| 6.14 | `DashboardPage.vue` — 统计卡片（`<el-statistic>`）+ 近7日发布趋势（表格或简单图表） | `views/admin/DashboardPage.vue` |
| 6.15 | `FriendLinks.vue`（前台）— 友链卡片网格展示 | `views/public/FriendLinks.vue` |
| 6.16 | `AboutPage.vue`（前台）— Markdown 渲染展示 | `views/public/AboutPage.vue` |
| 6.17 | API 封装：`api/admin.js` — 仪表盘、友链、关于、站点设置 | `api/admin.js` |

> **验收：** 后台仪表盘能看到统计数据；友链、关于、设置功能完整可用；前台友链页和关于页正常展示

---

## 第七阶段：Docker + CI/CD 部署（2天）

### Day 15 — Docker 化

| # | 要点 | 文件 |
|---|------|------|
| 7.1 | `blog-server/Dockerfile` — 多阶段构建：`maven:3.9-eclipse-temurin-17` 编译 → `eclipse-temurin:17-jre-alpine` 运行，添加 HEALTHCHECK | `blog-server/Dockerfile` |
| 7.2 | `blog-client/Dockerfile` — 多阶段构建：`node:20-alpine` 编译 → `nginx:alpine` 运行 | `blog-client/Dockerfile` |
| 7.3 | `docker-compose.yml` — 4 个 service：<br>**mysql**(8.0)：volume 持久化 + charset utf8mb4<br>**backend**：build ./blog-server，depends_on mysql (condition: service_healthy)<br>**frontend**：build ./blog-client，端口 80<br>**nginx**：nginx:alpine，挂载配置，端口 80 | `docker-compose.yml` |
| 7.4 | `nginx.conf` — `/` 代理到 frontend:80，`/api/` 和 `/rss` 代理到 backend:8080，添加 gzip 和安全头 | `deploy/nginx/nginx.conf` |
| 7.5 | `.env.example` — 列出所有环境变量模板（MYSQL_ROOT_PASSWORD、JWT_SECRET、ADMIN_PASSWORD、VITE_GISCUS 系列），**注意 .env 加入 .gitignore** | 根目录 |

> **验收：** `docker-compose up -d` 后，浏览器访问 `http://localhost` 能看到完整博客

### Day 16 — CI/CD

| # | 要点 | 文件 |
|---|------|------|
| 7.6 | `.github/workflows/deploy.yml` — `on: push to main`，steps：checkout → Maven build → npm build → SCP 到服务器 → SSH 执行 `docker-compose down && up -d --build` | `.github/workflows/deploy.yml` |
| 7.7 | GitHub Secrets 中添加：`SERVER_HOST`, `SERVER_USER`, `SSH_PRIVATE_KEY`, `MYSQL_ROOT_PASSWORD`, `JWT_SECRET`, `ADMIN_PASSWORD`, `VITE_GISCUS_*` | GitHub 仓库 Settings |
| 7.8 | 服务器初始化脚本 `deploy/init-server.sh` — 安装 Docker、Docker Compose、开启防火墙 80/443 端口 | `deploy/init-server.sh` |

> **验收：** 推送 main 分支后，GitHub Actions 自动运行并部署成功；访问服务器 IP 能看到博客

---

## 第八阶段：优化与收尾（1-2天）

| # | 具体改进 | 实现方式 | 优先级 |
|---|---------|---------|--------|
| 8.1 | 响应式适配 | 前台页面添加 CSS media queries，移动端侧边栏折叠为底部或隐藏 | ⭐⭐⭐ |
| 8.2 | 页面过渡动画 | Vue `<Transition>` 包裹 `router-view`，添加 fade/slide 动画 | ⭐⭐ |
| 8.3 | 加载骨架屏 | Element Plus `<el-skeleton>` 组件，在 API 返回前显示 | ⭐⭐ |
| 8.4 | 后端缓存 | Spring Cache + Caffeine：`@Cacheable("articles")` 标注热门查询，`@CacheEvict` 在写操作后清除 | ⭐⭐⭐ |
| 8.5 | 接口限流 | `Bucket4j` 或自定义 `RateLimiter` 过滤器，限制 `/api/articles/**` 每秒请求数 | ⭐ |
| 8.6 | SEO 优化 | Vue 页面添加 `<meta>` 标签 + Open Graph（`og:title`、`og:description`、`og:image`） | ⭐⭐ |
| 8.7 | Nginx Gzip + 安全头 | `nginx.conf` 添加 `gzip on`；`add_header X-Frame-Options "SAMEORIGIN"` 等 | ⭐⭐ |
| 8.8 | Actuator 健康检查 | 添加 `spring-boot-starter-actuator`，Docker HEALTHCHECK 依赖 `/actuator/health` | ⭐ |
| 8.9 | README.md | 项目介绍、技术栈徽章、本地启动步骤（5步以内）、Docker 部署命令、功能截图 | ⭐⭐⭐ |
| 8.10 | 简历整理 | 从 README 提炼项目描述、技术亮点、个人贡献 | ⭐⭐ |

---

## 附录：工具使用指南

### 附录 A：DBeaver 配合开发的使用技巧

| 场景 | 操作 |
|------|------|
| 查看 JPA 自动建的表 | 连接 blog 数据库 → Tables → 右键表 → View DDL |
| 执行全文索引 SQL | 右键数据库 → Tools → Execute Script |
| 插入测试数据 | 双击表 → Data 标签页下方 + → 填入值 → Save |
| 验证查询 SQL | 右键数据库 → SQL Editor → 写 SELECT 执行 |

### 附录 B：Postman 自动保存 Token 脚本

在 Login 请求的 Tests 标签写入：

```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.data.token);
}
```

后续请求在 Header 中添加 `Authorization: Bearer {{token}}`，自动生效。

### 附录 C：排查顺序

```
Postman 测 API（后端问题？）
  → DBeaver 查数据库（数据问题？）
  → 浏览器 F12 Network/Console（前端问题？）
  → IDEA 断点 Debug（代码逻辑问题？）
```

---

## 开发策略建议

### 推荐路线：后端先写一步，前端紧随其后

每个功能域遵循相同节奏：

```
后端写 API（Swagger 验证通过）→ 前端调 API（页面渲染完成）→ 下一个功能
```

### 为什么后端优先？

| 原因 | 说明 |
|------|------|
| **契约先行** | API 的请求/响应结构（`Result<T>` 包装、分页格式、错误码）必须后端先确定，前端才有对接依据 |
| **自带验证工具** | Swagger/SpringDoc OpenAPI 让后端写完即可调试，不依赖前端界面 |
| **精准定位 bug** | 前后端分步开发时，发现问题可以直接确定是后端还是前端的问题 |

### 各阶段执行顺序

| 阶段 | 开发顺序 | 推荐测试方式 |
|------|---------|-------------|
| **第一阶段** | 两端基础脚手架 **并行搭建** | 前端 ping 后端 |
| **第二阶段** | 后端认证 API → 前端登录页 | Postman 测登录 → 前端调通 |
| **第三阶段** | 后端文章/分类/标签 API → 前端后台 CRUD 页面 | Swagger 验证 → 前端调通 |
| **第四阶段** | 后端公开 API + 搜索 + RSS → 前台所有展示页 | 浏览器直接访问 API → 前端页面 |
| **第五阶段** | 纯前端 Giscus 组件（无需后端） | 浏览器直接看评论区 |
| **第六阶段** | 后端辅助 API → 前端辅助管理页 | Swagger → Postman → 前端 |
| **第七/八阶段** | Docker + CI/CD + 优化（两端并行） | docker-compose 整体测试 |

### 唯一例外：布局骨架可提前

第一阶段中，布局类组件（`AppHeader`、`AppFooter`、`AdminSidebar` 等）不依赖任何 API，**可以和后端同步开发**，快速看到页面效果。

---

> 本文件基于《Springboot博客平台开发规划.md》第八点"分阶段开发步骤"细化生成。
