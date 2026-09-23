# JSP 教学模板

一个以 JSP 为核心的 Java Web 教学项目，包含 9 个循序渐进的 JSP 示例页面。

## 快速开始

### 前置条件
- JDK 17+
- Maven 3.6+
- Tomcat 11+

### 启动方式

**方式一：Maven Cargo 一键启动（推荐）**
```bash
mvn clean package cargo:run
```
然后访问：http://localhost:8080/demo1_war_exploded/

**方式二：手动打包 + 部署**
```bash
mvn clean package
# 把 target/demo1.war 复制到 Tomcat 的 webapps 目录
# 启动 Tomcat，访问 http://localhost:8080/demo1/
```

## 项目结构

```
demo1/
├── pom.xml                         Maven 配置（Servlet + JSP + JSTL 依赖）
├── README.md                       本文件
├── start-tomcat.bat                Windows 一键启动脚本
├── .run/
│   └── StartTomcat.run.xml         IDEA 运行配置
└── src/
    └── main/
        ├── java/
        │   └── com/example/bean/
        │       └── User.java       JavaBean 示例
        └── webapp/
            ├── index.jsp           首页（示例导航）
            ├── 01-hello.jsp        JSP 基础语法
            ├── 02-objects.jsp      九大内置对象
            ├── 03-directive.jsp    三大指令
            ├── 04-form.jsp         表单提交
            ├── 05-counter.jsp      访问计数器（三种作用域）
            ├── 06-javabean.jsp     JavaBean
            ├── 07-jstl.jsp         JSTL 标签库
            ├── 08-el.jsp           EL 表达式
            ├── 09-include.jsp      页面包含
            ├── common/
            │   ├── header.jspf     公共头部（静态 include 用）
            │   └── footer.jspf     公共页脚（动态 include 用）
            └── WEB-INF/
                └── web.xml         部署描述文件
```

## 学习顺序

按编号从 01 学到 09，每个页面都有代码示例 + 运行效果 + 注释讲解：

1. **01-hello.jsp** — JSP 四大语法：表达式、脚本、声明、注释
2. **02-objects.jsp** — 九大内置对象：request / response / session / application 等
3. **03-directive.jsp** — 三大指令：page / include / taglib
4. **04-form.jsp** — 表单提交：GET / POST 参数接收
5. **05-counter.jsp** — 访问计数器：page / session / application 作用域
6. **06-javabean.jsp** — JavaBean：封装数据 + getter/setter
7. **07-jstl.jsp** — JSTL 标签库：forEach / if / choose / set
8. **08-el.jsp** — EL 表达式：${ } 取值与运算
9. **09-include.jsp** — 页面包含：静态 include vs 动态 include

## 技术栈

- **Jakarta Servlet 6.0**
- **Jakarta JSP 3.1**
- **JSTL 3.0**（Jakarta 版）
- **Tomcat 11.x**
- **Java 17**
- **Maven 3.x**

## 常见问题

**Q: 访问 JSP 出现 500 错误？**
A: 看 Tomcat 控制台的错误信息，常见原因：
- 语法写错了（比如 `<%=` 后面多写了分号）
- JSTL 依赖没加全（需要 jstl-api + glassfish 的实现）
- 导入的 Java 类没编译成功

**Q: JSP 和 Servlet 有什么区别？**
A: JSP 本质上就是 Servlet，JSP 会被 Tomcat 编译成 Servlet 再执行。
JSP 擅长写页面（HTML 多 Java 少），Servlet 擅长写逻辑（Java 多 HTML 少）。

**Q: JSP 还在使用吗？**
A: 企业新项目一般用前后端分离了，但 JSP 仍然是学习 Java Web 的绝佳入门，
因为它能让你直观理解「服务端渲染」的原理，很多老系统也还在用。
