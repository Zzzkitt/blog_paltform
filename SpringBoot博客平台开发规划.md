# 个人博客平台 — 全栈开发规划文档

## 一、项目概述

### 技术栈
| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.x + JDK 17 |
| ORM | Spring Data JPA (Hibernate) |
| 数据库 | MySQL 8.x |
| 安全认证 | Spring Security + JWT |
| 前端框架 | Vue 3 + Vite |
| UI 组件库 | Element Plus |
| 状态管理 | Pinia |
| 前端路由 | Vue Router 4 |
| 评论系统 | Giscus（第三方，基于 GitHub Discussions） |
| 部署 | Docker + Docker Compose + CI/CD（GitHub Actions） |

### 项目结构（Monorepo）
```
blog-platform/
├── blog-server/          # Spring Boot 后端
│   ├── src/main/java/com/blog/
│   │   ├── BlogApplication.java
│   │   ├── config/              # 配置类
│   │   ├── controller/          # REST 控制器
│   │   ├── service/             # 业务逻辑层
│   │   ├── repository/          # JPA 数据访问层
│   │   ├── entity/              # JPA 实体
│   │   ├── dto/                 # 数据传输对象
│   │   ├── vo/                  # 视图对象（响应封装）
│   │   ├── common/              # 公共工具（Result, 异常, 常量）
│   │   ├── security/            # Spring Security + JWT 相关
│   │   └── enums/               # 枚举类
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── application-prod.yml
├── blog-client/          # Vue 3 前端
│   ├── src/
│   │   ├── api/                # API 请求封装
│   │   ├── assets/             # 静态资源
│   │   ├── components/         # 公共组件
│   │   ├── layouts/            # 布局组件
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── views/              # 页面组件
│   │   │   ├── admin/          # 后台管理页面
│   │   │   └── public/         # 前台展示页面
│   │   ├── utils/              # 工具函数（含 axios 封装）
│   │   └── styles/             # 全局样式
│   └── index.html
├── docker-compose.yml    # Docker 编排
├── .github/workflows/    # CI/CD 流水线
└── deploy/               # 部署脚本和配置
    └── nginx/            # Nginx 配置
```

---

## 二、数据库设计

### ER 图（表结构）

#### 1. 用户表 `blog_user`
```sql
CREATE TABLE blog_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(50)  NOT NULL,
    email       VARCHAR(100),
    avatar      VARCHAR(255),
    role        VARCHAR(20)  NOT NULL DEFAULT 'ADMIN',  -- ADMIN / VISITOR
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME
);
```

#### 2. 文章表 `article`
```sql
CREATE TABLE article (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(200)   NOT NULL,
    summary       VARCHAR(500),
    content       LONGTEXT       NOT NULL,
    cover_image   VARCHAR(500),
    status        VARCHAR(20)    NOT NULL DEFAULT 'DRAFT',  -- DRAFT / PUBLISHED
    category_id   BIGINT,
    is_top        TINYINT(1)     DEFAULT 0,
    view_count    INT            DEFAULT 0,
    created_at    DATETIME       NOT NULL,
    updated_at    DATETIME,
    published_at  DATETIME,
    INDEX idx_status_published (status, published_at),
    FULLTEXT INDEX ft_title_content (title, content) WITH PARSER ngram  -- MySQL 全文检索
);
```

#### 3. 分类表 `category`
```sql
CREATE TABLE category (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    slug        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(200),
    sort_order  INT          DEFAULT 0,
    created_at  DATETIME     NOT NULL
);
```

#### 4. 标签表 `tag`
```sql
CREATE TABLE tag (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50) NOT NULL UNIQUE,
    slug       VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME    NOT NULL
);
```

#### 5. 文章-标签关联表 `article_tag`
```sql
CREATE TABLE article_tag (
    article_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    PRIMARY KEY (article_id, tag_id),
    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);
```

#### 6. 友链表 `friend_link`
```sql
CREATE TABLE friend_link (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    url         VARCHAR(500) NOT NULL,
    logo        VARCHAR(500),
    description VARCHAR(200),
    sort_order  INT DEFAULT 0,
    is_visible  TINYINT(1) DEFAULT 1,
    created_at  DATETIME NOT NULL
);
```

#### 7. 关于页面表 `about`
```sql
CREATE TABLE about_info (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    content     LONGTEXT NOT NULL,     -- Markdown 内容
    updated_at  DATETIME
);
```

#### 8. 网站设置表 `site_setting`
```sql
CREATE TABLE site_setting (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(100) NOT NULL UNIQUE,  -- 如: site_title, site_description, giscus_repo
    value TEXT         NOT NULL
);
```

---

## 三、后端 API 设计

### 基础响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 公开接口（无需认证）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 管理员登录，返回 JWT Token |
| GET | /api/articles | 分页获取已发布文章列表 |
| GET | /api/articles/{id} | 获取文章详情（公开） |
| GET | /api/articles/archive | 获取文章归档（按年月分组） |
| GET | /api/articles/search?q=xxx | 搜索文章 |
| GET | /api/categories | 获取分类列表（含文章数） |
| GET | /api/tags | 获取标签列表（含文章数） |
| GET | /api/categories/{slug}/articles | 按分类获取文章 |
| GET | /api/tags/{slug}/articles | 按标签获取文章 |
| GET | /api/friend-links | 获取友链列表 |
| GET | /api/about | 获取关于页面内容 |
| GET | /api/site-info | 获取站点基本信息（标题、描述等） |
| GET | /api/rss | 获取 RSS Feed（返回 XML） |

### 管理接口（需 JWT 认证，Header: `Authorization: Bearer <token>`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/admin/articles | 新建文章 |
| PUT | /api/admin/articles/{id} | 编辑文章 |
| DELETE | /api/admin/articles/{id} | 删除文章 |
| PATCH | /api/admin/articles/{id}/status | 修改文章状态（发布/草稿） |
| GET | /api/admin/articles | 分页获取所有文章（含草稿） |
| GET | /api/admin/articles/{id} | 获取文章详情（编辑用，含未发布） |
| POST | /api/admin/categories | 新增分类 |
| PUT | /api/admin/categories/{id} | 编辑分类 |
| DELETE | /api/admin/categories/{id} | 删除分类 |
| POST | /api/admin/tags | 新增标签 |
| PUT | /api/admin/tags/{id} | 编辑标签 |
| DELETE | /api/admin/tags/{id} | 删除标签 |
| POST | /api/admin/friend-links | 新增友链 |
| PUT | /api/admin/friend-links/{id} | 编辑友链 |
| DELETE | /api/admin/friend-links/{id} | 删除友链 |
| PUT | /api/admin/about | 更新关于页面 |
| GET | /api/admin/dashboard | 获取仪表盘统计数据 |
| POST | /api/admin/site-settings | 更新站点设置 |

### API 基础路径配置
所有接口统一前缀 `/api`，在 `application.yml` 中配置 `server.servlet.context-path`。

---

## 四、后端关键实现细节

### 4.1 Spring Boot 依赖（pom.xml 核心依赖）

```xml
<!-- Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Rome (RSS 生成) -->
<dependency>
    <groupId>com.rometools</groupId>
    <artifactId>rome</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- Markdown 转 HTML -->
<dependency>
    <groupId>org.commonmark</groupId>
    <artifactId>commonmark</artifactId>
    <version>0.22.0</version>
</dependency>

<!-- SpringDoc OpenAPI (API 文档) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

### 4.2 application.yml 配置
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: ${MYSQL_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update          # 开发阶段用 update，生产用 validate 或手动迁移
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true

jwt:
  secret: ${JWT_SECRET}          # 至少256位密钥
  expiration: 86400000           # 24小时，单位毫秒

# 初始化管理员账号（首次启动时写入数据库）
blog:
  admin:
    username: admin
    password: ${ADMIN_PASSWORD}
```

### 4.3 JWT 认证流程

1. **JwtTokenProvider** — 工具类，负责生成 Token、解析 Token、验证 Token
   - `generateToken(String username)`: 生成 JWT，subject 为用户名
   - `getUsernameFromToken(String token)`: 从 Token 提取用户名
   - `validateToken(String token)`: 验证 Token 是否有效（签名、过期时间）

2. **JwtAuthenticationFilter** — 继承 `OncePerRequestFilter`
   - 从请求头 `Authorization` 提取 Bearer Token
   - 验证 Token 有效性
   - 若有效，构建 `UsernamePasswordAuthenticationToken` 写入 `SecurityContextHolder`

3. **SecurityConfig** — 继承 `SecurityFilterChain`
   - 禁用 CSRF（前后端分离不需要）
   - 设置无状态 Session（`SessionCreationPolicy.STATELESS`）
   - 配置公开接口无需认证（`.requestMatchers("/api/auth/**", "/api/articles/**", "/api/categories/**", "/api/tags/**", "/api/friend-links/**", "/api/about/**", "/api/site-info/**", "/api/rss").permitAll()`）
   - 管理接口需认证（`.requestMatchers("/api/admin/**").authenticated()`）
   - 注册 `JwtAuthenticationFilter`

4. **前端处理**
   - 登录成功后，Token 存储在 `localStorage`
   - Axios 请求拦截器自动在 Header 添加 `Authorization: Bearer <token>`
   - Axios 响应拦截器检测 401 状态码，跳转到登录页

### 4.4 统一响应与异常处理

- **`Result<T>`** 统一响应类：`code`, `message`, `data`, `timestamp`
- **`GlobalExceptionHandler`** — `@RestControllerAdvice`
  - 处理 `MethodArgumentNotValidException`（参数校验失败）
  - 处理 `AccessDeniedException`（权限不足）
  - 处理 `EntityNotFoundException`（资源不存在）
  - 处理通用异常

### 4.5 分页

Spring Data JPA 的 `Pageable` + 自定义 `PageVO` 返回格式：
```json
{
  "records": [...],
  "total": 100,
  "page": 1,
  "pageSize": 10,
  "totalPages": 10
}
```

### 4.6 搜索实现

采用 MySQL 全文检索（ngram 解析器，支持中文）：
```java
@Query(value = "SELECT * FROM article WHERE MATCH(title, content) AGAINST(?1 IN BOOLEAN MODE) AND status = 'PUBLISHED'",
       nativeQuery = true)
Page<Article> searchByKeyword(String keyword, Pageable pageable);
```

### 4.7 RSS 生成

使用 Rome 库，在 `RssController` 中生成 RSS 2.0 Feed：
- 读取已发布文章列表
- 构建 `SyndFeed` 对象（`SyndFeedImpl`）
- 设置 title, link, description, entries
- 返回 `application/rss+xml` 格式

### 4.8 仪表盘统计（Dashboard）

聚合查询：
- 文章总数、已发布数、草稿数
- 分类数量、标签数量
- 最近7天/30天文章发布趋势
- 总访问量（视图中累加）

---

## 五、前端详细设计

### 5.1 依赖（package.json 核心依赖）
```json
{
  "dependencies": {
    "vue": "^3.4",
    "vue-router": "^4.3",
    "pinia": "^2.1",
    "axios": "^1.7",
    "element-plus": "^2.7",
    "@element-plus/icons-vue": "^2.3",
    "mavon-editor": "^3.0",         // Markdown 编辑器（后台）
    "markdown-it": "^14.1",         // Markdown 渲染（前台）
    "highlight.js": "^11.10",       // 代码高亮
    "dayjs": "^1.11",
    "nprogress": "^0.2"
  },
  "devDependencies": {
    "vite": "^5.4",
    "eslint": "^9.0",
    "prettier": "^3.3"
  }
}
```

### 5.2 路由设计

```javascript
const routes = [
  // 前台展示
  { path: '/',             name: 'Home',        component: () => import('@/views/public/HomePage.vue') },
  { path: '/articles/:id', name: 'ArticleDetail',component: () => import('@/views/public/ArticleDetail.vue') },
  { path: '/category/:slug', name: 'CategoryArticles', component: () => import('@/views/public/CategoryArticles.vue') },
  { path: '/tag/:slug',   name: 'TagArticles',  component: () => import('@/views/public/TagArticles.vue') },
  { path: '/archives',    name: 'Archives',     component: () => import('@/views/public/Archives.vue') },
  { path: '/about',       name: 'About',        component: () => import('@/views/public/AboutPage.vue') },
  { path: '/links',       name: 'FriendLinks',  component: () => import('@/views/public/FriendLinks.vue') },
  { path: '/search',      name: 'Search',       component: () => import('@/views/public/SearchPage.vue') },

  // 后台管理（嵌套路由）
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '',            name: 'Dashboard',     component: () => import('@/views/admin/DashboardPage.vue') },
      { path: 'articles',    name: 'ArticleList',   component: () => import('@/views/admin/ArticleList.vue') },
      { path: 'articles/edit',    name: 'ArticleEditor', component: () => import('@/views/admin/ArticleEditor.vue') },
      { path: 'articles/edit/:id', name: 'ArticleEdit', component: () => import('@/views/admin/ArticleEditor.vue') },
      { path: 'categories',  name: 'CategoryList',  component: () => import('@/views/admin/CategoryList.vue') },
      { path: 'tags',        name: 'TagList',       component: () => import('@/views/admin/TagList.vue') },
      { path: 'links',       name: 'LinkList',      component: () => import('@/views/admin/LinkList.vue') },
      { path: 'about',       name: 'AboutEdit',     component: () => import('@/views/admin/AboutEdit.vue') },
      { path: 'settings',    name: 'SiteSettings',  component: () => import('@/views/admin/SiteSettings.vue') },
    ],
  },

  // 登录
  { path: '/login',       name: 'Login',       component: () => import('@/views/admin/LoginPage.vue') },
]
```

### 5.3 全局路由守卫
```javascript
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})
```

### 5.4 组件划分

**公共组件：**
- `AppHeader.vue` — 前台顶部导航栏（logo、菜单、搜索框）
- `AppFooter.vue` — 前台底部
- `Sidebar.vue` — 前台侧边栏（个人简介、标签云、分类列表、最新文章）
- `ArticleCard.vue` — 文章卡片（列表项，含标题、摘要、封面、日期、标签）
- `Pagination.vue` — 分页组件
- `CommentSection.vue` — Giscus 评论区嵌入
- `BreadCrumb.vue` — 面包屑导航

**后台布局：**
- `AdminLayout.vue` — 侧边栏菜单 + 顶部导航栏 + 内容区
- `AdminSidebar.vue` — 菜单导航（仪表盘、文章管理、分类、标签、友链、关于、设置）

**后台页面：**
- `LoginPage.vue` — 登录表单
- `DashboardPage.vue` — 统计概览（文章数、分类数、访问量等卡片）
- `ArticleList.vue` — 文章表格（分页、搜索过滤、批量操作）
- `ArticleEditor.vue` — Markdown 编辑器（mavon-editor，含发布/存草稿按钮）
- `CategoryList.vue` — 分类 CRUD（表格 + 对话框）
- `TagList.vue` — 标签 CRUD（表格 + 对话框）
- `LinkList.vue` — 友链 CRUD
- `AboutEdit.vue` — 关于页面 Markdown 编辑器
- `SiteSettings.vue` — 站点标题、描述、Giscus 仓库配置等

### 5.5 Axios 封装

```javascript
// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000
})

// 请求拦截器：自动添加 Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  res => res.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    ElMessage.error(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)
```

### 5.6 环境变量（.env）

```env
VITE_API_BASE_URL = /api
VITE_APP_TITLE = My Blog
VITE_GISCUS_REPO = owner/repo
VITE_GISCUS_CATEGORY = Announcements
```

---

## 六、Giscus 评论集成

Giscus 是基于 GitHub Discussions 的评论系统，无需后端支持。

### 配置步骤
1. 在 GitHub 上为博客仓库启用 Discussions
2. 安装 [Giscus App](https://github.com/apps/giscus) 到该仓库
3. 在 [giscus.app](https://giscus.app) 配置并获取 `data-repo`、`data-repo-id`、`data-category`、`data-category-id`

### 前端组件实现

```vue
<!-- src/components/GiscusComments.vue -->
<template>
  <div class="giscus-wrapper" ref="wrapper"></div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'

const props = defineProps({ articleId: { type: Number, required: true } })
const wrapper = ref(null)

onMounted(() => {
  loadGiscus()
})

const loadGiscus = () => {
  // 清除旧评论
  wrapper.value.innerHTML = ''

  const script = document.createElement('script')
  script.src = 'https://giscus.app/client.js'
  script.setAttribute('data-repo', import.meta.env.VITE_GISCUS_REPO)
  script.setAttribute('data-repo-id', import.meta.env.VITE_GISCUS_REPO_ID)
  script.setAttribute('data-category', import.meta.env.VITE_GISCUS_CATEGORY)
  script.setAttribute('data-category-id', import.meta.env.VITE_GISCUS_CATEGORY_ID)
  script.setAttribute('data-mapping', 'specific')
  script.setAttribute('data-term', `article-${props.articleId}`)  // 每篇文章一个 discussion
  script.setAttribute('data-reactions-enabled', '1')
  script.setAttribute('data-emit-metadata', '0')
  script.setAttribute('data-theme', 'light')
  script.setAttribute('crossorigin', 'anonymous')
  script.async = true
  wrapper.value.appendChild(script)
}
</script>
```

关键点：使用 `data-mapping="specific"` + `data-term="article-{id}"`，每篇文章对应一个 GitHub Discussion，文章 ID 改变时自动加载对应评论。

---

## 七、CI/CD 部署方案

### 7.1 Docker Compose 配置

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: blog
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - blog_network
    restart: always

  backend:
    build: ./blog-server
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/blog
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      ADMIN_PASSWORD: ${ADMIN_PASSWORD}
    ports:
      - "8080:8080"
    networks:
      - blog_network
    restart: always

  frontend:
    build: ./blog-client
    depends_on:
      - backend
    ports:
      - "3000:80"
    networks:
      - blog_network
    restart: always

  nginx:
    image: nginx:alpine
    volumes:
      - ./deploy/nginx/nginx.conf:/etc/nginx/nginx.conf
    ports:
      - "80:80"
    depends_on:
      - frontend
      - backend
    networks:
      - blog_network
    restart: always

volumes:
  mysql_data:

networks:
  blog_network:
```

### 7.2 Nginx 配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        proxy_pass http://frontend:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # API 反向代理
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 后端其他路径
    location /rss {
        proxy_pass http://backend:8080;
    }
}
```

### 7.3 GitHub Actions 工作流

```yaml
# .github/workflows/deploy.yml
name: Deploy Blog

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Build backend
        run: cd blog-server && ./mvnw clean package -DskipTests

      - name: Build frontend
        run: cd blog-client && npm install && npm run build

      - name: Deploy to server
        uses: appleboy/scp-action@v0.1.7
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          source: "./"
          target: "~/blog/"

      - name: Docker deploy
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            cd ~/blog
            docker-compose pull
            docker-compose down
            docker-compose up -d --build
```

---

## 八、分阶段开发步骤

### 第一阶段：项目初始化与基础框架（2-3天）

- [ ] 创建 Monorepo 目录结构：`blog-server/` + `blog-client/`
- [ ] **后端**：通过 Spring Initializr 生成 Spring Boot 3.x 项目
  - 引入 Web、JPA、MySQL、Security、Validation 依赖
  - 配置 `application.yml`（数据源、JPA、JWT）
  - 创建包结构：`config`、`controller`、`service`、`repository`、`entity`、`dto`、`vo`、`common`、`security`
  - 编写 `Result` 统一响应类
  - 编写 `GlobalExceptionHandler` 全局异常处理
- [ ] **前端**：`npm create vite@latest blog-client -- --template vue` 初始化项目
  - 安装 Vue Router、Pinia、Axios、Element Plus、dayjs
  - 配置 Vite 代理解决跨域（`vite.config.js` 中 proxy `/api`）
  - 封装 Axios 请求工具
  - 启动页 Hello World 测试前后端连通
- [ ] 初始化 Git 仓库 + `.gitignore`（排除 `node_modules`、`target`、`.env`）

### 第二阶段：认证系统（2天）

- [ ] **后端**
  - 编写 `User` 实体 + `UserRepository`
  - 编写 `JwtTokenProvider`（生成/解析/验证 Token）
  - 编写 `JwtAuthenticationFilter`
  - 编写 `SecurityConfig`（Spring Security 安全配置）
  - 编写 `AuthController`（`POST /api/auth/login`）
  - 数据库初始化 `DataInitializer`（`CommandLineRunner`，首次启动创建管理员账号）
- [ ] **前端**
  - 创建 `LoginPage.vue`（登录表单，Element Plus 表单组件）
  - 创建 `authStore`（Pinia store，存储 token 和登录状态）
  - 路由守卫 `router.beforeEach`（未登录跳转登录页）
  - 请求拦截器自动添加 Token
  - 响应拦截器处理 401 跳转

### 第三阶段：文章管理（3-4天）

- [ ] **后端**
  - 编写实体：`Article`、`Category`、`Tag` + 对应的 Repository
  - 编写 `ArticleService`（含分页、按分类/标签筛选、按状态筛选）
  - 编写 `CategoryService`、`TagService`
  - 编写管理端 Controller：`ArticleController`（CRUD + 状态切换）、`CategoryController`、`TagController`
  - 文章内容格式：接收 Markdown，存储 Markdown，暴露时同时返回 markdown 和 html（由后端 commonmark 转换）
  - 分类和标签的 slug 自动生成（中文拼音或英文）
- [ ] **前端**
  - 创建 `AdminLayout.vue`（侧边栏菜单 + 内容区布局）
  - 创建 `ArticleList.vue`（Element Plus 表格 + 分页 + 搜索 + 状态筛选）
  - 创建 `ArticleEditor.vue`（集成 mavon-editor，标题、封面、分类、标签选择器）
  - 创建 `CategoryList.vue`（表格 + 新增/编辑对话框）
  - 创建 `TagList.vue`（表格 + 新增/编辑对话框）

### 第四阶段：前台展示页（3-4天）

- [ ] **后端**
  - 编写公开 Controller：文章列表、详情（+ 阅读数递增）、归档、按分类/标签筛选
  - 编写 `SearchController`（MySQL 全文检索）
  - `RssController`（Rome 生成 RSS Feed）
  - `SiteInfoController`（站点基本信息）
- [ ] **前端**
  - 创建 `AppHeader.vue`（导航 + 搜索框）
  - 创建 `AppFooter.vue`
  - 创建 `Sidebar.vue`（分类、标签云、最新文章）
  - 创建 `HomePage.vue`（文章卡片列表 + 置顶文章）
  - 创建 `ArticleDetail.vue`（Markdown 渲染 + 代码高亮 + 目录导航）
  - 创建 `Archives.vue`（按年月归档时间线）
  - 创建 `CategoryArticles.vue`、`TagArticles.vue`
  - 创建 `SearchPage.vue`
  - 集成 `GiscusComments.vue` 到文章详情页

### 第五阶段：Giscus 评论集成（半天）

- [ ] 在 GitHub 仓库启用 Discussions
- [ ] 安装 Giscus App
- [ ] 配置 `GiscusComments.vue` 组件
- [ ] 在 `ArticleDetail.vue` 中嵌入评论区

### 第六阶段：辅助功能（2天）

- [ ] **后端**
  - `FriendLink` 实体 + CRUD API
  - `AboutInfo` 实体 + 更新 API
  - `SiteSetting` 实体 + 更新 API
  - `DashboardController`（统计数据聚合查询）
- [ ] **前端**
  - 创建 `LinkList.vue`（友链管理）
  - 创建 `AboutEdit.vue`（关于页面编辑器）
  - 创建 `SiteSettings.vue`（站点设置表单）
  - 创建 `FriendLinks.vue`（前台友链展示页）
  - 创建 `AboutPage.vue`（前台关于页面）
  - 创建 `DashboardPage.vue`（统计卡片 + 图表）

### 第七阶段：Docker + CI/CD 部署（2天）

- [ ] 编写 `blog-server/Dockerfile`
  ```dockerfile
  FROM eclipse-temurin:17-jre-alpine
  ARG JAR_FILE=target/*.jar
  COPY ${JAR_FILE} app.jar
  ENTRYPOINT ["java", "-jar", "/app.jar"]
  ```
- [ ] 编写 `blog-client/Dockerfile`（多阶段构建：Node 构建 → Nginx 运行）
  ```dockerfile
  FROM node:20-alpine AS builder
  WORKDIR /app
  COPY package*.json ./
  RUN npm install
  COPY . .
  RUN npm run build

  FROM nginx:alpine
  COPY --from=builder /app/dist /usr/share/nginx/html
  EXPOSE 80
  CMD ["nginx", "-g", "daemon off;"]
  ```
- [ ] 编写 `docker-compose.yml`（mysql + backend + frontend + nginx）
- [ ] 编写 `.env.example`（列出所有环境变量模板）
- [ ] 编写 Nginx 配置
- [ ] 配置 GitHub Actions（SSH 部署到服务器或云主机）
- [ ] 服务器初始化脚本（Docker 安装、防火墙设置）

### 第八阶段：优化与收尾（1-2天）

- [ ] 前端：响应式适配移动端
- [ ] 前端：页面过渡动画、加载骨架屏
- [ ] 后端：接口数据缓存（Spring Cache + Caffeine）
- [ ] 后端：接口限流保护
- [ ] SEO 优化：页面 meta 标签、Open Graph 协议
- [ ] Nginx Gzip 压缩
- [ ] 编写 README.md（项目介绍、技术栈、启动方式、部署说明）
- [ ] 截图项目页面，整理简历描述

---

## 九、简历项目描述建议

```
个人博客平台 | Spring Boot + Vue 3 + MySQL + Docker

核心技术：Spring Boot 3、Spring Data JPA、Spring Security、JWT、Vue 3、Element Plus、Pinia、MySQL、Docker

项目描述：
- 基于前后端分离架构，开发了包含文章发布、分类标签管理、全文检索、RSS订阅等功能的个人博客系统
- 后端采用 Spring Boot 3 + JPA 实现 RESTful API，使用 Spring Security + JWT 实现无状态认证
- 前端采用 Vue 3 + Element Plus 构建响应式管理后台和前台展示页面
- 集成 Giscus（基于 GitHub Discussions）实现零成本评论系统
- 使用 Markdown 编辑器实现富文本写作体验，支持代码高亮和文章预览
- 通过 Docker Compose 编排多容器部署，配置 GitHub Actions 实现 CI/CD 自动化发布
```

---

## 十、推荐学习资源

- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Data JPA 参考](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Vue 3 官方文档](https://vuejs.org/guide/introduction.html)
- [Element Plus 文档](https://element-plus.org/zh-CN/component/overview.html)
- [Giscus 配置](https://giscus.app/zh-CN)
