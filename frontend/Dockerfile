# 构建阶段
FROM node:14 AS build

# 设置工作目录
WORKDIR /app

# 设置npm镜像源为淘宝镜像
RUN npm config set registry https://registry.npmmirror.com

# 复制package.json和package-lock.json
COPY package*.json ./

# 清除npm缓存并强制安装依赖
RUN npm cache clean --force && \
    npm install --force

# 复制源代码
COPY . .

# 构建应用
RUN npm run build

# 生产阶段
FROM nginx:alpine

# 复制构建产物到Nginx目录
COPY --from=build /app/dist /usr/share/nginx/html/interview

# 复制Nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80
