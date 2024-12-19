#### 后端

+ 在Windows上安装`JAVA17.0.12` , 配置环境变量

+ 在Windows上安装`Maven3.9.9` , 配置环境变量 , 并在IDEA内配置相关目录
+ 在CentOS上安装`RabbitMQ` `Redis` , 修改`yml`文件
+ 配置邮件发送机制`SMTP`

最终后端配置的`yml`文件(`application-dev.yml`)如下
```yaml
# 开发环境配置
springdoc:
  paths-to-match: /api/**
  swagger-ui:
    operations-sorter: alpha
spring:
  mail:
    host: smtp.163.com
    username: 18216327750@163.com
    password: UJxfMSAa6E6VXiL7
    port: 465
    protocol: smtp
    properties:
      mail:
        smtp:
          ssl.enable: true
          socketFactory.port: 465
          socketFactory.class: javax.net.ssl.SSLSocketFactory
          auth: true
          starttls.enable: true
          stattls.enable: true
  rabbitmq:
    addresses: niit01
    username: admin
    password: admin
    virtual-host: /
  datasource:
    url: jdbc:mysql://niit01:3306/bank_db
    username: root
    password: "000000"
    driver-class-name: com.mysql.cj.jdbc.Driver
  security:
    jwt:
      key: 'abcdefghijklmn'
      expire: 72
      limit:
        base: 10
        upgrade: 300
        frequency: 30
    filter:
      order: -100
  web:
    verify:
      mail-limit: 60
    flow:
      period: 3
      limit: 50
      block: 30
    cors:
      origin: '*'
      credentials: false
      methods: '*'
    resources:
      add-mappings: false
  mvc:
    static-path-pattern: /static/**
  servlet:
    multipart:
  # Max file size，默认1M
      max-file-size: 2048MB
  # Max request size，默认10M
      max-request-size: 2048MB
  data:
    redis:
      host: niit01
      port: 6379
      password: 123456
  logging:
    level:
      root: DEBUG
```



#### 前端

+ 进入`my-project-frontend`目录 , 打开`pakage.json`文件 , 并按IDEA弹窗安装`nodejs`



#### 运行

+ 进入后端目录 , 使用`mvn spring-boot:run`运行后端
+ 进入前端目录 , 使用`npm run dev`运行前端
