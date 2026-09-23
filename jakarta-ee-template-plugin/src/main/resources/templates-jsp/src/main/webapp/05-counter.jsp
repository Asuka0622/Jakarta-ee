<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // ============================================
    //  访问计数器 —— 演示三种作用域的区别
    // ============================================

    // 1. page 域：只在当前页面有效，刷新就重置（几乎不用）
    int pageCount = 1; // 每次请求都重新声明

    // 2. session 域：同一个浏览器（同一个用户）共享
    Integer sessionCount = (Integer) session.getAttribute("sessionCount");
    if (sessionCount == null) sessionCount = 0;
    sessionCount++;
    session.setAttribute("sessionCount", sessionCount);

    // 3. application 域：所有用户共享（整个应用只有一份）
    Integer appCount = (Integer) application.getAttribute("appCount");
    if (appCount == null) appCount = 0;
    appCount++;
    application.setAttribute("appCount", appCount);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>05 · 访问计数器</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #4f46e5; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; }
        .counter-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                         gap: 20px; margin: 24px 0; }
        .counter-card { background: #fff; border: 1px solid #e5e7eb; border-radius: 10px;
                        padding: 24px; text-align: center; transition: transform 0.2s; }
        .counter-card:hover { transform: translateY(-3px); box-shadow: 0 8px 20px rgba(0,0,0,0.08); }
        .counter-card .icon { font-size: 36px; margin-bottom: 10px; }
        .counter-card .label { font-size: 14px; color: #6b7280; margin-bottom: 6px; }
        .counter-card .num { font-size: 36px; font-weight: bold; }
        .c-page .num { color: #6b7280; }
        .c-session .num { color: #ec4899; }
        .c-app .num { color: #10b981; }
        .tip-box { background: #eff6ff; border-left: 4px solid #3b82f6;
                   padding: 16px 20px; border-radius: 6px; margin: 20px 0; }
        .tip-box h4 { color: #1e40af; margin-bottom: 8px; }
        .tip-box ul { padding-left: 20px; color: #374151; }
        .tip-box li { margin: 4px 0; }
        .back { display: inline-block; margin-top: 20px; color: #6366f1; text-decoration: none; }
        .refresh-btn { display: inline-block; background: #4f46e5; color: white; padding: 10px 20px;
                       border-radius: 6px; text-decoration: none; margin-right: 10px; }
        .reset-btn { display: inline-block; background: #ef4444; color: white; padding: 10px 20px;
                     border-radius: 6px; text-decoration: none; }
    </style>
</head>
<body>
    <a href="index.jsp" class="back">← 返回首页</a>
    <h1>05 · 访问计数器</h1>
    <p>三种作用域的计数器，刷新页面对比一下变化</p>

    <div class="counter-grid">
        <div class="counter-card c-page">
            <div class="icon">📄</div>
            <div class="label">page 域</div>
            <div class="num"><%= pageCount %></div>
            <p style="font-size:12px; color:#9ca3af; margin-top:8px;">每次请求都是 1</p>
        </div>
        <div class="counter-card c-session">
            <div class="icon">👤</div>
            <div class="label">session 域</div>
            <div class="num"><%= sessionCount %></div>
            <p style="font-size:12px; color:#9ca3af; margin-top:8px;">同一浏览器共享</p>
        </div>
        <div class="counter-card c-app">
            <div class="icon">🌐</div>
            <div class="label">application 域</div>
            <div class="num"><%= appCount %></div>
            <p style="font-size:12px; color:#9ca3af; margin-top:8px;">所有用户共享</p>
        </div>
    </div>

    <div style="margin: 16px 0;">
        <a href="05-counter.jsp" class="refresh-btn">🔄 刷新页面</a>
    </div>

    <div class="tip-box">
        <h4>📚 四大作用域对比</h4>
        <ul>
            <li><b>page 域</b>：只在当前 JSP 页面有效，刷新就没了（几乎不用）</li>
            <li><b>request 域</b>：一次请求内有效，转发（forward）后还能拿到</li>
            <li><b>session 域</b>：同一个浏览器多次请求都能拿到，关闭浏览器失效</li>
            <li><b>application 域</b>：整个应用所有用户共享，服务器重启失效</li>
        </ul>
    </div>

    <div class="tip-box" style="background: #fef2f2; border-left-color: #ef4444;">
        <h4 style="color: #991b1b;">⚠️ 注意事项</h4>
        <ul>
            <li>application 域是所有用户共享的，并发访问时要考虑线程安全</li>
            <li>真实项目的访问量统计一般存在数据库或 Redis，不用 application</li>
            <li>session 不要存太多数据，服务器内存会爆</li>
        </ul>
    </div>

    <a href="index.jsp" class="back">← 返回首页</a>
</body>
</html>
