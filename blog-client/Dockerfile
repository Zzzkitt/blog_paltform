# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app

ARG VITE_API_BASE_URL
ARG VITE_APP_TITLE
ARG VITE_GISCUS_REPO
ARG VITE_GISCUS_REPO_ID
ARG VITE_GISCUS_CATEGORY
ARG VITE_GISCUS_CATEGORY_ID

ENV VITE_API_BASE_URL=$VITE_API_BASE_URL \
    VITE_APP_TITLE=$VITE_APP_TITLE \
    VITE_GISCUS_REPO=$VITE_GISCUS_REPO \
    VITE_GISCUS_REPO_ID=$VITE_GISCUS_REPO_ID \
    VITE_GISCUS_CATEGORY=$VITE_GISCUS_CATEGORY \
    VITE_GISCUS_CATEGORY_ID=$VITE_GISCUS_CATEGORY_ID

COPY blog-client/package.json blog-client/package-lock.json ./
RUN npm ci
COPY blog-client/ .
RUN npm run build

# 运行阶段
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY deploy/nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost/ || exit 1
CMD ["nginx", "-g", "daemon off;"]
