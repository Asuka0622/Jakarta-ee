<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // ===== JSP 声明（declaration）=====
    // 用 <%! ... %> 包裹，定义的是类的成员变量/方法，
    // 只在 JSP 首次被编译成 Servlet 时初始化一次。
%>
<%!
    // 声明一个成员变量（注意：JSP 是单例多线程，成员变量会被所有用户共享！）
    int visitCount = 0;

    // 声明一个方法
    String sayHello(String name) {
        return "你好，" + name + "！";
    }
%>
<%
    // ===== JSP 脚本（scriptlet）=====
    // 用 <% ... %> 包裹，写普通 Java 代码，
    // 每次请求都会执行（放在 _jspService() 方法里）。
    visitCount++; // 每次访问 +1
    String user = "同学";
    int a = 10, b = 20;
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>01 · Hello JSP - 基础语法</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #4f46e5; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; }
        .demo { background: #f9fafb; padding: 16px 20px; border-radius: 8px; margin: 16px 0; }
        .demo h3 { color: #374151; margin-bottom: 8px; }
        .code { background: #1f2937; color: #e5e7eb; padding: 12px 16px; border-radius: 6px;
                font-family: Consolas, monospace; font-size: 13px; margin: 10px 0; white-space: pre-wrap; }
        .back { display: inline-block; margin-top: 20px; color: #6366f1; text-decoration: none; }
        .back:hover { text-decoration: underline; }
        .tag { display: inline-block; background: #dbeafe; color: #1e40af; padding: 2px 8px;
               border-radius: 4px; font-size: 12px; margin-right: 6px; }
    </style>
</head>
<body>
    <a href="index.jsp" class="back">← 返回首页</a>
    <h1>01 · Hello JSP</h1>
    <p>JSP 四大基础语法：表达式、脚本、声明、注释</p>

    <!-- ===== JSP 表达式（expression）===== -->
    <!-- 用 <%= ... %> 包裹，等价于 out.print(...)，
         注意后面不写分号！ -->
    <div class="demo">
        <h3>① JSP 表达式 <span class="tag">&lt;%= %&gt;</span></h3>
        <p>向页面输出一个值，等价于 out.print()</p>
        <div class="code">当前时间：<%= new java.util.Date() %>
a + b = <%= a + b %>
sayHello() = <%= sayHello(user) %></div>
        <p><b>效果：</b></p>
        <div class="code">当前时间：<%= new java.util.Date() %>
a + b = <%= a + b %>
sayHello() = <%= sayHello(user) %></div>
    </div>

    <!-- ===== JSP 脚本（scriptlet）===== -->
    <div class="demo">
        <h3>② JSP 脚本 <span class="tag">&lt;% %&gt;</span></h3>
        <p>写任意 Java 代码，可以用 for / if / while 等控制页面输出</p>
        <div class="code">&lt;%
    for (int i = 1; i &lt;= 5; i++) {
%&gt;
    &lt;p&gt;第 &lt;%= i %&gt; 行&lt;/p&gt;
&lt;%
    }
%&gt;</div>
        <p><b>效果：</b></p>
        <%
            for (int i = 1; i <= 5; i++) {
        %>
            <p style="margin:4px 0;">第 <%= i %> 行</p>
        <%
            }
        %>
    </div>

    <!-- ===== JSP 声明（declaration）===== -->
    <div class="demo">
        <h3>③ JSP 声明 <span class="tag">&lt;%! %&gt;</span></h3>
        <p>定义成员变量或方法，只在首次编译时初始化一次</p>
        <p>⚠️ 注意：成员变量会被所有用户共享（单例多线程），不要用来存用户数据！</p>
        <p>当前页面累计访问次数（刷新看看会不会涨）：<b style="color:#ef4444;"><%= visitCount %></b></p>
    </div>

    <!-- ===== JSP 注释 ===== -->
    <div class="demo">
        <h3>④ JSP 注释 <span class="tag">&lt;%-- --%&gt;</span></h3>
        <p>JSP 注释在服务端就被去掉了，浏览器查看源码看不到</p>
        <p>HTML 注释（&lt;!-- --&gt;）浏览器能看到，JSP 注释浏览器看不到</p>
        <%-- 这是 JSP 注释，右键查看源码是看不到我的 --%>
        <!-- 这是 HTML 注释，右键查看源码能看到 -->
    </div>

    <a href="index.jsp" class="back">← 返回首页</a>
</body>
</html>
