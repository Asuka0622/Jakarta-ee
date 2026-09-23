<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.bean.User" %>
<%
    // 准备一些数据放在不同作用域里，演示 EL 表达式怎么取值
    pageContext.setAttribute("p1", "page域的值");
    request.setAttribute("r1", "request域的值");
    session.setAttribute("s1", "session域的值");
    application.setAttribute("a1", "application域的值");

    // 放一个 JavaBean
    User user = new User("张三", 20, "zhangsan@example.com");
    request.setAttribute("user", user);

    // 放一个数组
    String[] colors = {"红", "橙", "黄", "绿", "青", "蓝", "紫"};
    request.setAttribute("colors", colors);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>08 · EL 表达式</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #4f46e5; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; }
        .demo { background: #fff; border: 1px solid #e5e7eb; border-radius: 10px;
                padding: 20px 24px; margin: 20px 0; }
        .demo h3 { color: #374151; margin-bottom: 10px; }
        table { width: 100%; border-collapse: collapse; margin: 10px 0; }
        th, td { padding: 8px 12px; text-align: left; border-bottom: 1px solid #e5e7eb; }
        th { background: #f9fafb; font-weight: 500; color: #374151; width: 45%; }
        code { background: #f3f4f6; padding: 2px 6px; border-radius: 4px; font-size: 13px;
               font-family: Consolas, monospace; }
        .back { display: inline-block; margin-top: 20px; color: #6366f1; text-decoration: none; }
        .tag { display: inline-block; background: #dcfce7; color: #166534;
               padding: 2px 8px; border-radius: 4px; font-size: 12px; }
    </style>
</head>
<body>
    <a href="index.jsp" class="back">← 返回首页</a>
    <h1>08 · EL 表达式</h1>
    <p>EL（Expression Language）表达式语言：用 <code>${ }</code> 简化取值和运算</p>

    <div class="demo">
        <h3>① 取四大作用域的值 <span class="tag">最常用</span></h3>
        <p>按 page → request → session → application 顺序查找，找到就返回</p>
        <table>
            <tr><th>EL 表达式</th><th>结果</th></tr>
            <tr><td><code>${'$'}{p1}</code></td><td>${p1}</td></tr>
            <tr><td><code>${'$'}{r1}</code></td><td>${r1}</td></tr>
            <tr><td><code>${'$'}{s1}</code></td><td>${s1}</td></tr>
            <tr><td><code>${'$'}{a1}</code></td><td>${a1}</td></tr>
        </table>
        <p style="font-size:13px; color:#6b7280;">
            💡 想指定作用域？用 <code>${'$'}{pageScope.p1}</code>、<code>${'$'}{requestScope.r1}</code>、
            <code>${'$'}{sessionScope.s1}</code>、<code>${'$'}{applicationScope.a1}</code>
        </p>
    </div>

    <div class="demo">
        <h3>② 取 JavaBean 属性</h3>
        <p>可以直接用「.属性名」取 getter 的值，不用写 get</p>
        <table>
            <tr><th>EL 表达式</th><th>结果</th></tr>
            <tr><td><code>${'$'}{user.username}</code></td><td>${user.username}</td></tr>
            <tr><td><code>${'$'}{user.age}</code></td><td>${user.age}</td></tr>
            <tr><td><code>${'$'}{user.email}</code></td><td>${user.email}</td></tr>
            <tr><td><code>${'$'}{user.adult}</code>（boolean 用 is 开头）</td><td>${user.adult}</td></tr>
        </table>
    </div>

    <div class="demo">
        <h3>③ 取数组 / List 元素</h3>
        <p>用下标 [索引] 取值</p>
        <table>
            <tr><th>EL 表达式</th><th>结果</th></tr>
            <tr><td><code>${'$'}{colors[0]}</code></td><td>${colors[0]}</td></tr>
            <tr><td><code>${'$'}{colors[2]}</code></td><td>${colors[2]}</td></tr>
            <tr><td><code>${'$'}{colors[6]}</code></td><td>${colors[6]}</td></tr>
            <tr><td><code>${'$'}{colors[100]}</code>（越界不会报错，返回空）</td><td>[${colors[100]}]</td></tr>
        </table>
    </div>

    <div class="demo">
        <h3>④ EL 运算符</h3>
        <p>支持算术、比较、逻辑运算</p>
        <table>
            <tr><th>类型</th><th>表达式</th><th>结果</th></tr>
            <tr><td>算术</td><td><code>${'$'}{10 + 20 * 2}</code></td><td>${10 + 20 * 2}</td></tr>
            <tr><td>比较</td><td><code>${'$'}{10 > 5}</code></td><td>${10 > 5}</td></tr>
            <tr><td>逻辑</td><td><code>${'$'}{true and false}</code></td><td>${true and false}</td></tr>
            <tr><td>空判断</td><td><code>${'$'}{empty user}</code></td><td>${empty user}</td></tr>
            <tr><td>三元</td><td><code>${'$'}{user.age >= 18 ? '成年' : '未成年'}</code></td><td>${user.age >= 18 ? '成年' : '未成年'}</td></tr>
        </table>
    </div>

    <div class="demo" style="background: #fef2f2; border-color: #fecaca;">
        <h3 style="color: #991b1b;">⚠️ EL 表达式取值为 null 时</h3>
        <p>EL 表达式的一大优点：<b>取到 null 不报错，显示空字符串</b></p>
        <p>对比：<code>&lt;%= request.getAttribute("不存在") %&gt;</code> 会输出 "null"（丑）</p>
        <p>而 <code>${'$'}{不存在的变量}</code> 输出：[${不存在的变量}]（空白，更友好）</p>
    </div>

    <a href="index.jsp" class="back">← 返回首页</a>
</body>
</html>
