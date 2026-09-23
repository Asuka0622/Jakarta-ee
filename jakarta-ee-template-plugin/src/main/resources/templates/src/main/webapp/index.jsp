<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // 演示 JSP 脚本：获取当前服务器时间
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String now = sdf.format(new java.util.Date());

    // 演示 JSP 内置对象：request / session / application
    String serverInfo = application.getServerInfo();
    String sessionId = session.getId();
    String remoteAddr = request.getRemoteAddr();
%>
<!DOCTYPE html>
<!--
  index.jsp —— 动态欢迎页（JSP 版本）
  访问 http://localhost:8080/demo1_war_exploded/index.jsp 即可查看。
  JSP 会被 Tomcat 编译成 Servlet 再执行，所以能在页面里嵌入 Java 代码，
  动态生成内容（比如当前时间、用户 IP、会话 ID 等）。

  本页面和 index.html 同时存在，web.xml 的欢迎页列表把 index.html 放在前面，
  所以访问根路径时默认返回 index.html（静态更快）；
  想默认用 JSP，把 web.xml 里 <welcome-file>index.jsp</welcome-file> 挪到第一个即可。
-->
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hello JSP</title>
    <style>
        body { margin: 40px 20px; }
        h1 { text-align: center; font-size: 28px; }
        .info { max-width: 600px; margin: 30px auto; border: 1px solid #e5e7eb;
                border-radius: 8px; padding: 20px 24px; background: #f9fafb; }
        .info table { width: 100%; border-collapse: collapse; }
        .info th, .info td { padding: 8px 12px; text-align: left; border-bottom: 1px solid #e5e7eb; }
        .info th { width: 140px; color: #6b7280; font-weight: normal; }
        .tag { display: inline-block; padding: 2px 8px; border-radius: 4px;
               background: #dbeafe; color: #1e40af; font-size: 12px; }
    </style>
</head>
<body>

    <h1>Hello JSP <span class="tag">动态页面</span></h1>

    <div class="info">
        <table>
            <tr>
                <th>服务器时间</th>
                <td><%= now %></td>
            </tr>
            <tr>
                <th>服务器信息</th>
                <td><%= serverInfo %></td>
            </tr>
            <tr>
                <th>你的 IP</th>
                <td><%= remoteAddr %></td>
            </tr>
            <tr>
                <th>会话 ID</th>
                <td><%= sessionId %></td>
            </tr>
            <tr>
                <th>JSP 内置对象</th>
                <td>out, request, response, session, application, page, pageContext, config, exception</td>
            </tr>
        </table>
    </div>

    <p style="text-align:center; color:#6b7280; font-size:14px;">
        刷新页面，「服务器时间」会变化——这就是 JSP 与静态 HTML 的区别。
    </p>

</body>
</html>
