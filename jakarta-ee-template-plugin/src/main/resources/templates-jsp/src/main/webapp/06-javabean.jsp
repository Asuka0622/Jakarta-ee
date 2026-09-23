<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.bean.User" %>
<%
    request.setCharacterEncoding("UTF-8");

    // 检查是否是表单提交过来的
    String action = request.getParameter("action");
    User user = null;

    if ("create".equals(action)) {
        // 方式一：用 Java 代码手动 new 对象 + set 属性
        user = new User();
        user.setUsername(request.getParameter("username"));

        String ageStr = request.getParameter("age");
        int age = 0;
        try {
            if (ageStr != null && !ageStr.isEmpty()) {
                age = Integer.parseInt(ageStr);
            }
        } catch (NumberFormatException e) {
            // 输入不是数字就用 0
        }
        user.setAge(age);
        user.setEmail(request.getParameter("email"));
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>06 · JavaBean</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; max-width: 720px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #4f46e5; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; }
        .form-box { background: #fff; border: 1px solid #e5e7eb; border-radius: 10px; padding: 24px; margin: 20px 0; }
        .form-row { margin-bottom: 16px; }
        label { display: block; margin-bottom: 6px; font-size: 14px; color: #374151; font-weight: 500; }
        input[type="text"], input[type="number"], input[type="email"] {
            width: 100%; padding: 8px 12px; border: 1px solid #d1d5db;
            border-radius: 6px; font-size: 14px; font-family: inherit;
        }
        button { background: #4f46e5; color: white; border: none; padding: 10px 24px;
                 border-radius: 6px; font-size: 14px; cursor: pointer; }
        button:hover { background: #4338ca; }
        .result-box { background: #f0fdf4; border: 1px solid #22c55e;
                      border-radius: 8px; padding: 16px 20px; margin: 16px 0; }
        .result-box h3 { color: #15803d; margin-bottom: 10px; }
        .result-box p { margin: 4px 0; }
        .back { display: inline-block; margin-top: 20px; color: #6366f1; text-decoration: none; }
        .method-card { background: #f9fafb; border-left: 3px solid #6366f1;
                       padding: 12px 16px; border-radius: 4px; margin: 10px 0; }
        .method-card h4 { color: #4f46e5; margin-bottom: 4px; }
        .tag { display: inline-block; background: #e0e7ff; color: #4338ca;
               padding: 2px 8px; border-radius: 4px; font-size: 12px; }
        .adult { color: #10b981; font-weight: bold; }
        .minor { color: #f59e0b; font-weight: bold; }
    </style>
</head>
<body>
    <a href="index.jsp" class="back">← 返回首页</a>
    <h1>06 · JavaBean</h1>
    <p>JavaBean 是一种符合特定规范的 Java 类，封装数据用</p>

    <div class="method-card">
        <h4>JavaBean 三要素</h4>
        <p>
            <span class="tag">私有属性</span>
            <span class="tag">getter / setter</span>
            <span class="tag">无参构造</span>
        </p>
        <p style="font-size:13px; color:#6b7280;">
            对应文件：<code>src/main/java/com/example/bean/User.java</code>
        </p>
    </div>

    <div class="form-box">
        <form method="post" action="06-javabean.jsp">
            <input type="hidden" name="action" value="create">
            <div class="form-row">
                <label>用户名</label>
                <input type="text" name="username" placeholder="请输入用户名"
                    value="<%= user != null ? user.getUsername() : "" %>">
            </div>
            <div class="form-row">
                <label>年龄</label>
                <input type="number" name="age" placeholder="请输入年龄"
                    value="<%= user != null ? user.getAge() : "" %>">
            </div>
            <div class="form-row">
                <label>邮箱</label>
                <input type="email" name="email" placeholder="请输入邮箱"
                    value="<%= user != null ? user.getEmail() : "" %>">
            </div>
            <button type="submit">创建 User 对象</button>
        </form>
    </div>

    <% if (user != null) { %>
    <div class="result-box">
        <h3>✅ JavaBean 创建成功</h3>
        <p><b>用户名：</b><%= user.getUsername() %></p>
        <p><b>年龄：</b><%= user.getAge() %> 岁
            （<%= user.isAdult() ? "<span class='adult'>已成年</span>" : "<span class='minor'>未成年</span>" %>）
        </p>
        <p><b>邮箱：</b><%= user.getEmail() %></p>
        <p><b>toString()：</b><%= user.toString() %></p>
    </div>

    <div class="method-card">
        <h4>等价的 jsp:useBean 写法（了解）</h4>
        <p style="font-size:13px; color:#6b7280;">
            JSP 提供了 &lt;jsp:useBean&gt;、&lt;jsp:setProperty&gt;、&lt;jsp:getProperty&gt;
            三个动作标签来操作 JavaBean，不用写 Java 代码。
            但现在更推荐用 EL 表达式 + JSTL，所以这里只做了解即可。
        </p>
    </div>
    <% } %>

    <a href="index.jsp" class="back">← 返回首页</a>
</body>
</html>
