<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%
    // 准备一些测试数据，放到 request 域里，JSTL 就能读
    List<String> fruits = new ArrayList<>();
    fruits.add("苹果");
    fruits.add("香蕉");
    fruits.add("橘子");
    fruits.add("葡萄");
    fruits.add("西瓜");
    request.setAttribute("fruits", fruits);

    request.setAttribute("score", 85);
    request.setAttribute("user", "小明");
    request.setAttribute("isLogin", true);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>07 · JSTL 标签库</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #4f46e5; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; }
        .demo { background: #fff; border: 1px solid #e5e7eb; border-radius: 10px;
                padding: 20px 24px; margin: 20px 0; }
        .demo h3 { color: #374151; margin-bottom: 10px; }
        .code { background: #1f2937; color: #e5e7eb; padding: 12px 16px; border-radius: 6px;
                font-family: Consolas, monospace; font-size: 13px; margin: 10px 0; white-space: pre-wrap; }
        .result { background: #f0fdf4; border: 1px solid #22c55e;
                  border-radius: 6px; padding: 12px 16px; margin-top: 10px; }
        .back { display: inline-block; margin-top: 20px; color: #6366f1; text-decoration: none; }
        .fruit-list { list-style: none; padding: 0; }
        .fruit-list li { padding: 6px 12px; margin: 4px 0; background: #f3f4f6;
                         border-radius: 4px; display: flex; justify-content: space-between; }
        .tag { display: inline-block; background: #ede9fe; color: #6d28d9;
               padding: 2px 8px; border-radius: 4px; font-size: 12px; }
    </style>
</head>
<body>
    <a href="index.jsp" class="back">← 返回首页</a>
    <h1>07 · JSTL 标签库</h1>
    <p>JSTL（JSP Standard Tag Library）是 JSP 标准标签库，用标签替代脚本，页面更干净</p>

    <p style="background:#fef3c7; padding:10px 14px; border-radius:6px; font-size:14px;">
        ⚠️ 使用前必须先引入 JSTL 依赖（pom.xml 里已加好），并在页面顶部加：<br>
        <code>&lt;%@ taglib prefix="c" uri="jakarta.tags.core" %&gt;</code>
    </p>

    <!-- c:forEach 循环 -->
    <div class="demo">
        <h3>① c:forEach <span class="tag">循环</span></h3>
        <p>遍历集合或数组，替代 Java 的 for 循环</p>
        <div class="code">&lt;c:forEach items="${fruits}" var="fruit" varStatus="vs"&gt;
    ${vs.count}. ${fruit}
&lt;/c:forEach&gt;</div>
        <p><b>效果：</b></p>
        <div class="result">
            <ul class="fruit-list">
                <c:forEach items="${fruits}" var="fruit" varStatus="vs">
                    <li><span>${vs.count}. ${fruit}</span><span style="color:#9ca3af;">索引 ${vs.index}</span></li>
                </c:forEach>
            </ul>
        </div>
    </div>

    <!-- c:if 条件判断 -->
    <div class="demo">
        <h3>② c:if <span class="tag">条件判断</span></h3>
        <p>满足条件才显示内容，相当于 if 语句（JSTL 没有 else，用 c:choose）</p>
        <div class="code">&lt;c:if test="${isLogin}"&gt;
    欢迎回来，${user}！
&lt;/c:if&gt;</div>
        <p><b>效果：</b></p>
        <div class="result">
            <c:if test="${isLogin}">
                👋 欢迎回来，<b>${user}</b>！
            </c:if>
            <c:if test="${!isLogin}">
                请先登录
            </c:if>
        </div>
    </div>

    <!-- c:choose 多分支 -->
    <div class="demo">
        <h3>③ c:choose / c:when / c:otherwise <span class="tag">多分支</span></h3>
        <p>相当于 if - else if - else</p>
        <div class="code">&lt;c:choose&gt;
    &lt;c:when test="${score >= 90}"&gt;优秀&lt;/c:when&gt;
    &lt;c:when test="${score >= 80}"&gt;良好&lt;/c:when&gt;
    &lt;c:when test="${score >= 60}"&gt;及格&lt;/c:when&gt;
    &lt;c:otherwise&gt;不及格&lt;/c:otherwise&gt;
&lt;/c:choose&gt;</div>
        <p><b>效果（分数 = ${score}）：</b></p>
        <div class="result">
            <c:choose>
                <c:when test="${score >= 90}">🏆 优秀</c:when>
                <c:when test="${score >= 80}">👍 良好</c:when>
                <c:when test="${score >= 60}">✅ 及格</c:when>
                <c:otherwise">❌ 不及格</c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- c:set 设置变量 -->
    <div class="demo">
        <h3>④ c:set <span class="tag">设置变量</span></h3>
        <p>在指定作用域里设置变量</p>
        <div class="code">&lt;c:set var="message" value="Hello JSTL!" scope="page"/&gt;
${message}</div>
        <c:set var="message" value="Hello JSTL!" scope="page"/>
        <div class="result">输出：<b>${message}</b></div>
    </div>

    <a href="index.jsp" class="back">← 返回首页</a>
</body>
</html>
