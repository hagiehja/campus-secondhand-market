# 校园二手交易系统

这是一个面向校园场景的二手交易系统课程设计项目，主题为“校园二手交易系统”。项目包含 Spring Boot 后端、Vue 前端、MySQL 数据库脚本、Redis 缓存、DeepSeek AI 助手、短信验证码登录和支付宝支付演示接口。

## 项目信息

- 学号：2415304249
- 姓名：黄钰宗
- 项目名称：校园二手交易系统
- 后端技术栈：Java 17、Spring Boot、MySQL 5.7、Redis、JDBC、LangChain4j
- 前端技术栈：Vue 3、Vite、HTML、CSS、JavaScript
- 数据库：MySQL 5.7
- 默认后端端口：8080
- 默认前端端口：5173

## 功能模块

- 用户登录、注册
- 手机号短信验证码登录
- 商品分类浏览
- 商品搜索与筛选
- 商品列表展示
- 商品下单
- 我的订单
- 模拟支付
- 支付宝支付页面接口
- AI 助手问答
- 校园二手交易数据统计
- ER 图与数据库设计文档

## 目录结构

```text
.
├── backend/                                      # Spring Boot 后端
│   ├── src/main/java/com/example/market/
│   │   ├── config/                              # 配置类
│   │   ├── service/                             # 业务接口
│   │   ├── service/impl/                        # 业务实现
│   │   └── web/                                 # Controller 和 DTO
│   ├── src/main/resources/application.yml       # 后端主配置
│   └── pom.xml                                  # Maven 配置
├── frontend/                                    # Vue 前端
│   ├── src/App.vue
│   ├── src/styles.css
│   ├── src/data/market.js
│   └── src/services/api.js
├── campus_secondhand_market.sql                 # 基础建库建表脚本
├── campus_secondhand_market_more_products.sql   # 更多商品数据
├── campus_secondhand_market_payment_addon.sql   # 支付相关 SQL
├── campus_secondhand_market_phone_login_addon.sql
├── campus_secondhand_market_real_product_images.sql
├── campus_secondhand_market_er.mmd              # ER 图 Mermaid 源文件
├── campus_secondhand_market_er.svg              # ER 图
└── campus_secondhand_market_database_design.md  # 数据库设计说明
```

## 数据库导入

MySQL 端口按当前开发环境配置为 `3307`。

```powershell
mysql -h 192.168.24.129 -P 3307 -uroot -p123456 < campus_secondhand_market.sql
mysql -h 192.168.24.129 -P 3307 -uroot -p123456 < campus_secondhand_market_more_products.sql
mysql -h 192.168.24.129 -P 3307 -uroot -p123456 < campus_secondhand_market_payment_addon.sql
mysql -h 192.168.24.129 -P 3307 -uroot -p123456 < campus_secondhand_market_phone_login_addon.sql
mysql -h 192.168.24.129 -P 3307 -uroot -p123456 < campus_secondhand_market_real_product_images.sql
```

后端默认连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.24.129:3307/campus_secondhand_market
    username: campus_user
    password: Campus@2415304249
```

## 后端启动

进入后端目录：

```powershell
cd backend
mvn spring-boot:run
```

启动成功后访问：

```text
http://127.0.0.1:8080
```

常用接口：

```text
GET  /api/products
GET  /api/categories
GET  /api/market/summary
POST /api/auth/login
POST /api/auth/register
POST /api/auth/sms-code
POST /api/auth/phone-login
POST /api/orders
GET  /api/orders
POST /api/orders/{orderId}/pay/mock
GET  /api/orders/{orderId}/pay/alipay
POST /api/agent/chat
```

## 前端启动

进入前端目录：

```powershell
cd frontend
npm install
npm run dev
```

浏览器访问：

```text
http://127.0.0.1:5173
```

前端默认 API 地址在 `frontend/.env.development` 中：

```text
VITE_API_BASE_URL=http://127.0.0.1:8080/api
```

## AI 助手配置

AI 助手通过后端接入 DeepSeek，配置项在：

```text
backend/src/main/resources/application.yml
```

推荐使用本地私有配置文件：

```text
backend/application-local.yml
```

示例：

```yaml
market:
  agent:
    api-key: "你的 DeepSeek API Key"
```

`backend/application-local.yml` 已加入 `.gitignore`，不会上传到 GitHub。

也可以使用环境变量：

```powershell
$env:DEEPSEEK_API_KEY="你的 DeepSeek API Key"
```

## 短信验证码配置

手机号验证码登录已完成后端接口和前端页面。真实短信发送使用阿里云短信服务，需要先在阿里云控制台开通短信服务、申请短信签名和验证码模板。

环境变量示例：

```powershell
$env:ALIYUN_SMS_ENABLED="true"
$env:ALIBABA_CLOUD_ACCESS_KEY_ID="你的AccessKeyId"
$env:ALIBABA_CLOUD_ACCESS_KEY_SECRET="你的AccessKeySecret"
$env:ALIYUN_SMS_SIGN_NAME="你的短信签名"
$env:ALIYUN_SMS_TEMPLATE_CODE="你的验证码模板Code"
```

如果未配置阿里云短信参数，点击“获取验证码”会返回“短信服务未启用”。

## 支付宝支付配置

系统已包含支付宝支付页面接口。默认配置为关闭真实支付：

```yaml
market:
  alipay:
    enabled: false
```

如需真实支付，需要准备：

- 支付宝开放平台应用 AppId
- 应用私钥
- 支付宝公钥
- notify-url 公网回调地址
- return-url 支付完成返回地址

课程演示可以先使用模拟支付接口：

```text
POST /api/orders/{orderId}/pay/mock
```

## 测试与构建

后端测试：

```powershell
cd backend
mvn test
```

前端内容检查：

```powershell
cd frontend
npm test
```

前端构建：

```powershell
cd frontend
npm run build
```

## 默认演示账号

```text
账号：buyer01
密码：123456
```

也可以使用注册功能创建新用户，或使用手机号验证码登录。手机号不存在时，系统会自动注册为买家账号。

## 注意事项

- 不要提交 `backend/application-local.yml`，里面可能包含 DeepSeek、短信、支付等私密配置。
- 上传 GitHub 前确认 `.gitignore` 已生效。
- MySQL 5.7 环境需要先导入 SQL 脚本。
- Redis 未启动时，部分缓存功能会降级，不影响主要接口演示。
- 真实短信和真实支付宝支付必须配置第三方平台参数后才能使用。
