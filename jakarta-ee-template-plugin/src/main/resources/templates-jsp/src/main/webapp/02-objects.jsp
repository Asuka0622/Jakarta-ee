<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // 演示九大内置对象中最常用的 6 个
    // 1. out        —— 输出流，往页面写内容
    // 2. request    —— 请求对象，读请求参数、请求头
    // 3. response   —— 响应对象，设状态码、响应头
    // 4. session    —— 会话对象，同一用户多次请求共享数据
    // 5. application —— 全局对象，所有用户共享（就是 ServletContext）
    // 6. pageContext —— 页面上下文，能拿到其他 8 个对象
%>
<%
    // 统计访问次数（application 域，所有用户共享）
    Integer appCount = (Integer) application.getAttribute("appCount");
    if (appCount == null) appCount = 0;
    appCount++;
    application.setAttribute("appCount", appCount);

    // 会话级访问次数（session 域，同一个浏览器共享）
    Integer sessCount = (Integer) session.getAttribute("sessCount");
    if (sessCount == null) sessCount = 0;
    sessCount++;
    session.setAttribute("sessCount", sessCount);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>02 · 九大内置对象</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #4f46e5; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; }
        table { width: 100%; border-collapse: collapse; margin: 16px 0; }
        th, td { padding: 10px 14px; text-align: left; border-bottom: 1px solid #e5e7eb; }
        th { background: #f9fafb; font-weight: 500; color: #374151; width: 140px; }
        tr:hover { background: #f9fafb; }
        .back { display: inline-block; margin-top: 20px; color: #6366f1; text-decoration: none; }
        .tip { background: #fef3c7; border-left: 4px solid #f59e0b; padding: 12px 16px;
               border-radius: 6px; margin: 16px 0; font-size: 14px; }
        .badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
        .b-req { background: #dbeafe; color: #1e40af; }
        .b-sess { background: #fce7f3; color: #be185d; }
        .b-app { background: #d1fae5; color: #047857; }
    </style>
</head>
<body>
    <a href="index.jsp" class="back">← 返回首页</a>
    <h1>02 · 九大内置对象</h1>
    <p>JSP 有 9 个不用声明就能直接用的对象，下面是最常用的 6 个的演示</p>

    <div class="tip">
        💡 <b>作用域从小到大：</b>page → request → session → application<br>
        作用域越大，共享范围越广，但也要越小心线程安全问题。
    </div>

    <h3>request 对象 <span class="badge b-req">请求域</span></h3>
    <table>
        <tr><th>请求方式</th><td><%= request.getMethod() %></td></tr>
        <tr><th>请求 URL</th><td><%= request.getRequestURL() %></td></tr>
        <tr><th>客户端 IP</th><td><%= request.getRemoteAddr() %></td>
        <tr><th>User-Agent</th><td><%= request.getHeader("User-Agent") %></td></tr>
        <tr><th>内容类型</th><td><%= request.getContentType() %></td></tr>
    </table>

    <h3>session 对象 <span class="badge b-sess">会话域</span></h3>
    <table>
        <tr><th>会话 ID</th><td><%= session.getId() %></td></tr>
        <tr><th>创建时间</th><td><%= new java.util.Date(session.getCreationTime()) %></td></tr>
        <tr><th>最后访问时间</th><td><%= new java.util.Date(session.getLastAccessedTime()) %></td></tr>
        <tr><th>本次会话访问次数</th><td><b style="color:#be185d;"><%= sessCount %></b>（刷新页面会 +1）</td></tr>
        <tr><th>是否新会话</th><td><%= session.isNew() ? "是" : "否" %></td></tr>
    </table>

    <h3>application 对象 <span class="badge b-app">全局域</span></h3>
    <table>
        <tr><th>服务器信息</th><td><%= application.getServerInfo() %></td></tr>
        <tr><th>全局访问次数</th><td><b style="color:#047857;"><%= appCount %></b>（所有用户共用，重启服务器重置）</td></tr>
        <tr><th>真实路径</th><td><%= application.getRealPath("/") %></td></tr>
    </table>

    <h3>其他内置对象（简要）</h3>
    <table>
        <tr><th>out</th><td>JspWriter，向页面输出内容（<%= %> 底层就是它）</td></tr>
        <tr><th>response</th><td>HttpServletResponse，设响应头、状态码、重定向等</td></tr>
        <tr><th>pageContext</th><td>PageContext，能获取其他 8 个对象，也能操作 page 域</td></tr>
        <tr><th>page</th><td>当前 JSP 对象本身（就是 this），几乎不用</td></tr>
        <tr><th>config</th><td>ServletConfig，读 web.xml 里的初始化参数</td></tr>
        <tr><th>exception</th><td>Throwable，只在错误页（isErrorPage=true）中可用</td></tr>
    </table>

    <a href="index.jsp" class="back">← 返回首页</a>
</body>
</html>
