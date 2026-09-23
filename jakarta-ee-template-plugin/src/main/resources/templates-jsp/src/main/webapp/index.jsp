<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // 获取当前服务器时间
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String now = sdf.format(new java.util.Date());
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JSP 教学模板</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, "Microsoft YaHei", sans-serif;
            background: #f0f4ff;
            color: #1f2937;
            line-height: 1.6;
        }
        .container { max-width: 960px; margin: 0 auto; padding: 40px 20px; }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            border-radius: 12px;
            margin-bottom: 32px;
        }
        .header h1 { font-size: 32px; margin-bottom: 8px; }
        .header .time { font-size: 14px; opacity: 0.9; }
        .badge {
            display: inline-block;
            background: rgba(255,255,255,0.2);
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            margin-bottom: 12px;
        }
        h2 { font-size: 22px; margin-bottom: 16px; color: #374151; }
        .grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 16px;
            margin-bottom: 32px;
        }
        .card {
            background: white;
            border-radius: 10px;
            padding: 24px;
            text-decoration: none;
            color: inherit;
            transition: transform 0.2s, box-shadow 0.2s;
            border: 1px solid #e5e7eb;
        }
        .card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 24px rgba(0,0,0,0.1);
        }
        .card .icon { font-size: 32px; margin-bottom: 10px; }
        .card h3 { font-size: 18px; margin-bottom: 6px; color: #4f46e5; }
        .card p { font-size: 14px; color: #6b7280; }
        .info-box {
            background: #fff;
            border-left: 4px solid #6366f1;
            padding: 16px 20px;
            border-radius: 6px;
            margin-bottom: 24px;
        }
        .info-box code {
            background: #f3f4f6;
            padding: 2px 6px;
            border-radius: 4px;
            font-size: 13px;
        }
        footer { text-align: center; color: #9ca3af; font-size: 13px; padding: 20px 0; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <span class="badge">JSP 教学模板</span>
            <h1>🎯 JSP 示例导航</h1>
            <p>从下面的例子开始，逐个了解 JSP 的核心用法</p>
            <p class="time">服务器时间：<%= now %></p>
        </div>

        <div class="info-box">
            <b>📌 提示：</b>
            每个示例都可以直接点击访问，建议配合 JSP 源码一起看。
            JSP 文件放在 <code>src/main/webapp/</code> 目录下。
        </div>

        <h2>基础语法</h2>
        <div class="grid">
            <a href="01-hello.jsp" class="card">
                <div class="icon">👋</div>
                <h3>01 · Hello JSP</h3>
                <p>JSP 基本语法：表达式、脚本、声明、注释</p>
            </a>
            <a href="02-objects.jsp" class="card">
                <div class="icon">📦</div>
                <h3>02 · 九大内置对象</h3>
                <p>out / request / response / session / application 等</p>
            </a>
            <a href="03-directive.jsp" class="card">
                <div class="icon">📝</div>
                <h3>03 · 指令</h3>
                <p>page / include / taglib 三大指令的用法</p>
            </a>
        </div>

        <h2>交互应用</h2>
        <div class="grid">
            <a href="04-form.jsp" class="card">
                <div class="icon">📋</div>
                <h3>04 · 表单提交</h3>
                <p>GET / POST 表单提交与参数接收</p>
            </a>
            <a href="05-counter.jsp" class="card">
                <div class="icon">🔢</div>
                <h3>05 · 访问计数器</h3>
                <p>session / application 域对象存数据</p>
            </a>
            <a href="06-javabean.jsp" class="card">
                <div class="icon">🫘</div>
                <h3>06 · JavaBean</h3>
                <p>useBean / getProperty / setProperty 标签</p>
            </a>
        </div>

        <h2>进阶用法</h2>
        <div class="grid">
            <a href="07-jstl.jsp" class="card">
                <div class="icon">🏷️</div>
                <h3>07 · JSTL 标签库</h3>
                <p>c:forEach / c:if / c:choose 等常用标签</p>
            </a>
            <a href="08-el.jsp" class="card">
                <div class="icon">⚡</div>
                <h3>08 · EL 表达式</h3>
                <p>${ } 表达式语言，简化页面取值</p>
            </a>
            <a href="09-include.jsp" class="card">
                <div class="icon">🧩</div>
                <h3>09 · 页面包含</h3>
                <p>静态 include 与动态 include 的区别</p>
            </a>
        </div>

        <footer>
            JSP 教学模板 · 部署路径：/demo1_war_exploded/
        </footer>
    </div>
</body>
</html>
