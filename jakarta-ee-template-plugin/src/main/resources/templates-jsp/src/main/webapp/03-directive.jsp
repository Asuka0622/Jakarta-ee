<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%--
  JSP 三大指令：
    1. page       —— 页面级配置（导入包、设编码、错误页等）
    2. include    —— 静态包含另一个文件
    3. taglib     —— 引入标签库（如 JSTL）

  指令格式：<%@ 指令名 属性="值" %>
  注意：指令是给 JSP 引擎看的，在编译阶段生效，不是运行时执行。
--%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>03 · JSP 指令</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #4f46e5; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; }
        .demo { background: #f9fafb; padding: 16px 20px; border-radius: 8px; margin: 16px 0; }
        .demo h3 { color: #374151; margin-bottom: 8px; }
        .code { background: #1f2937; color: #e5e7eb; padding: 12px 16px; border-radius: 6px;
                font-family: Consolas, monospace; font-size: 13px; margin: 10px 0; white-space: pre-wrap; }
        .back { display: inline-block; margin-top: 20px; color: #6366f1; text-decoration: none; }
        .tag { display: inline-block; background: #fef3c7; color: #92400e; padding: 2px 8px;
               border-radius: 4px; font-size: 12px; margin-right: 6px; }
        .result { background: #ecfdf5; border: 1px solid #10b981; padding: 12px 16px;
                  border-radius: 6px; margin-top: 10px; }
    </style>
</head>
<body>
    <a href="index.jsp" class="back">← 返回首页</a>
    <h1>03 · JSP 三大指令</h1>

    <!-- ===== page 指令 ===== -->
    <div class="demo">
        <h3>① page 指令 <span class="tag">最常用</span></h3>
        <p>设置页面属性：编码、语言、导入包、错误页等</p>
        <div class="code"><%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %></div>

        <p><b>import 导入效果演示：</b></p>
        <%
            List<String> list = new ArrayList<>();
            list.add("苹果");
            list.add("香蕉");
            list.add("橘子");
        %>
        <div class="result">
            List 集合内容：<%= list %><br>
            集合大小：<%= list.size() %>
        </div>
        <p style="font-size:13px; color:#6b7280; margin-top:8px;">
            💡 没有 import 的话，使用 List 和 ArrayList 需要写全类名（java.util.List），
            用 import 之后就能直接写类名了，和 Java 一样。
        </p>
    </div>

    <!-- ===== include 指令 ===== -->
    <div class="demo">
        <h3>② include 指令 <span class="tag">静态包含</span></h3>
        <p>在编译阶段把另一个文件的内容原封不动地插进来</p>
        <div class="code"><%@ include file="common/header.jspf" %></div>
        <p><b>效果：</b></p>
        <%@ include file="common/header.jspf" %>
        <p style="font-size:13px; color:#6b7280; margin-top:8px;">
            💡 静态 include 是编译时合并，两个文件共享同一个 pageContext、request 等。
            如果被包含的文件经常改，建议用动态 include（&lt;jsp:include&gt;）。
        </p>
    </div>

    <!-- ===== taglib 指令 ===== -->
    <div class="demo">
        <h3>③ taglib 指令 <span class="tag">引入标签库</span></h3>
        <p>引入 JSTL 等标签库，前缀是你自己起的别名</p>
        <div class="code"><%@ taglib prefix="c" uri="jakarta.tags.core" %></div>
        <p>引入后就能用 &lt;c:forEach&gt;、&lt;c:if&gt; 等标签了，详见 07-jstl.jsp 示例。</p>
    </div>

    <a href="index.jsp" class="back">← 返回首页</a>
</body>
</html>
