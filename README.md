# 差旅报销项目

本仓库包含前端（Vite + Vue 3 + Element Plus）和后端（Spring Boot + MySQL）的差旅报销示例实现，数据库结构以 `database/travel_reimbursement_schema.sql` 为准，已按控件数据与业务表解耦。

## 目录结构

- `project-backend`: Spring Boot 后端 API
- `vetech-frontend`: Vue 3 前端页面
- `database`: 数据库脚本与文档

## 后端启动

1. 创建数据库并导入 SQL：

```shell
mysql -u root -p vetech_project < database/travel_reimbursement_schema.sql
```

2. 修改 `project-backend/src/main/resources/application.yaml` 中的数据库账号密码。
3. 启动服务：

```shell
mvn -f project-backend/pom.xml spring-boot:run
```

默认端口：`http://localhost:8080`

## 前端启动

```shell
npm --prefix vetech-frontend install
npm --prefix vetech-frontend run dev
```

默认访问：`http://localhost:5173`

## API 摘要

- `GET /api/lookups` 基础数据
- `GET /api/reimbursements` 报销单列表
- `GET /api/reimbursements/{id}` 报销单详情
- `POST /api/reimbursements` 新建报销单
- `PUT /api/reimbursements/{id}` 更新报销单
- `POST /api/reimbursements/{id}/submit` 提交
- `POST /api/reimbursements/{id}/void` 作废

## 鉴权占位

后端提供简单鉴权占位配置：

- `app.auth.enabled: false` 默认关闭
- 开启后需要在请求头中传 `X-Auth-Token`
