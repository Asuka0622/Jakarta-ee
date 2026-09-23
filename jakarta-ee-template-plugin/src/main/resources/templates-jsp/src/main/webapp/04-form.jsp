<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setCharacterEncoding("UTF-8");

    // 读取表单参数（GET 和 POST 都能用 getParameter）
    String name = request.getParameter("name");
    String age  = request.getParameter("age");
    String gender = request.getParameter("gender");
    String[] hobbies = request.getParameterValues("hobby");
    String city = request.getParameter("city");
    String intro = request.getParameter("intro");

    // 判断用户是不是刚提交了表单
    boolean submitted = (name != null);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>04 · 表单提交</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; max-width: 720px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #4f46e5; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; }
        .form-box { background: #fff; border: 1px solid #e5e7eb; border-radius: 10px; padding: 24px; margin: 20px 0; }
        .form-row { margin-bottom: 16px; }
        label { display: block; margin-bottom: 6px; font-size: 14px; color: #374151; font-weight: 500; }
        input[type="text"], input[type="number"], select, textarea {
            width: 100%; padding: 8px 12px; border: 1px solid #d1d5db;
            border-radius: 6px; font-size: 14px; font-family: inherit;
        }
        textarea { resize: vertical; min-height: 80px; }
        .radio-group, .checkbox-group { display: flex; gap: 16px; flex-wrap: wrap; }
        .radio-group label, .checkbox-group label {
            display: inline-flex; align-items: center; gap: 6px;
            font-weight: normal; margin: 0;
        }
        button { background: #4f46e5; color: white; border: none; padding: 10px 24px;
                 border-radius: 6px; font-size: 14px; cursor: pointer; }
        button:hover { background: #4338ca; }
        .result-box { background: #ecfdf5; border: 1px solid #10b981;
                      border-radius: 8px; padding: 16px 20px; margin: 16px 0; }
        .result-box h3 { color: #047857; margin-bottom: 10px; }
        .result-box p { margin: 4px 0; }
        .back { display: inline-block; margin-top: 20px; color: #6366f1; text-decoration: none; }
        .method-tabs { display: flex; gap: 8px; margin-bottom: 16px; }
        .method-tabs .tab { padding: 6px 14px; border-radius: 6px; font-size: 13px;
                            background: #f3f4f6; cursor: pointer; text-decoration: none; color: #374151; }
        .method-tabs .tab.active { background: #4f46e5; color: white; }
    </style>
</head>
<body>
    <a href="index.jsp" class="back">← 返回首页</a>
    <h1>04 · 表单提交</h1>
    <p>JSP 接收表单参数：request.getParameter() 和 getParameterValues()</p>

    <div class="method-tabs">
        <a href="04-form.jsp?method=get" class="tab <%= "get".equals(request.getParameter("method")) || request.getParameter("method")==null ? "active" : "" %>">GET 方式</a>
        <a href="04-form.jsp?method=post" class="tab <%= "post".equals(request.getParameter("method")) ? "active" : "" %>">POST 方式</a>
    </div>

    <div class="form-box">
        <% if ("post".equals(request.getParameter("method"))) { %>
            <form method="post" action="04-form.jsp?method=post">
        <% } else { %>
            <form method="get" action="04-form.jsp?method=get">
        <% } %>
            <div class="form-row">
                <label>姓名</label>
                <input type="text" name="name" placeholder="请输入姓名" value="<%= name != null ? name : "" %>">
            </div>
            <div class="form-row">
                <label>年龄</label>
                <input type="number" name="age" placeholder="请输入年龄" value="<%= age != null ? age : "" %>">
            </div>
            <div class="form-row">
                <label>性别</label>
                <div class="radio-group">
                    <label><input type="radio" name="gender" value="男" <%= "男".equals(gender) ? "checked" : "" %>>男</label>
                    <label><input type="radio" name="gender" value="女" <%= "女".equals(gender) ? "checked" : "" %>>女</label>
                </div>
            </div>
            <div class="form-row">
                <label>爱好（多选）</label>
                <div class="checkbox-group">
                    <label><input type="checkbox" name="hobby" value="编程">编程</label>
                    <label><input type="checkbox" name="hobby" value="音乐">音乐</label>
                    <label><input type="checkbox" name="hobby" value="运动">运动</label>
                    <label><input type="checkbox" name="hobby" value="阅读">阅读</label>
                </div>
            </div>
            <div class="form-row">
                <label>城市</label>
                <select name="city">
                    <option value="">请选择</option>
                    <option value="北京" <%= "北京".equals(city) ? "selected" : "" %>>北京</option>
                    <option value="上海" <%= "上海".equals(city) ? "selected" : "" %>>上海</option>
                    <option value="广州" <%= "广州".equals(city) ? "selected" : "" %>>广州</option>
                    <option value="深圳" <%= "深圳".equals(city) ? "selected" : "" %>>深圳</option>
                </select>
            </div>
            <div class="form-row">
                <label>自我介绍</label>
                <textarea name="intro" placeholder="简单介绍一下自己..."><%= intro != null ? intro : "" %></textarea>
            </div>
            <button type="submit">提交</button>
        </form>
    </div>

    <% if (submitted) { %>
    <div class="result-box">
        <h3>✅ 收到你的提交啦！</h3>
        <p><b>提交方式：</b><%= request.getMethod() %></p>
        <p><b>姓名：</b><%= name %></p>
        <p><b>年龄：</b><%= age != null && !age.isEmpty() ? age + " 岁" : "未填写" %></p>
        <p><b>性别：</b><%= gender != null ? gender : "未选择" %></p>
        <p><b>城市：</b><%= city != null && !city.isEmpty() ? city : "未选择" %></p>
        <p><b>爱好：</b>
            <% if (hobbies != null && hobbies.length > 0) {
                   for (String h : hobbies) { out.print(h + " "); }
               } else { out.print("未选择"); } %>
        </p>
        <p><b>自我介绍：</b><br>
            <%= intro != null && !intro.isEmpty() ? intro.replace("\n", "<br>") : "未填写" %>
        </p>
    </div>
    <% } %>

    <p style="font-size:13px; color:#6b7280;">
        💡 GET 方式参数会出现在地址栏里，适合搜索、分页等；
        POST 方式参数在请求体里，地址栏看不到，适合登录、注册等敏感数据。
    </p>

    <a href="index.jsp" class="back">← 返回首页</a>
</body>
</html>
