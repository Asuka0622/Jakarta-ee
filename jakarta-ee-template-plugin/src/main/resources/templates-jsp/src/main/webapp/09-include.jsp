<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // 设置一个 request 域变量，用来验证静态 include 和动态 include 的区别
    request.setAttribute("pageName", "09-include.jsp");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>09 · 页面包含</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; }
        h1 { color: #4f46e5; border-bottom: 2px solid #e5e7eb; padding-bottom: 8px; }
        .demo { background: #fff; border: 1px solid #e5e7eb; border-radius: 10px;
                padding: 20px 24px; margin: 20px 0; }
        .demo h3 { color: #374151; margin-bottom: 10px; }
        .code { background: #1f2937; color: #e5e7eb; padding: 12px 16px; border-radius: 6px;
                font-family: Consolas, monospace; font-size: 13px; margin: 10px 0; white-space: pre-wrap; }
        .include-result { border: 2px dashed #6366f1; border-radius: 8px;
                          padding: 16px; margin: 10px 0; background: #eef2ff; }
        .back { display: inline-block; margin-top: 20px; color: #6366f1; text-decoration: none; }
        .vs { display: flex; gap: 20px; flex-wrap: wrap; }
        .vs > div { flex: 1; min-width: 280px; }
        .tag { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
        .t-static { background: #fef3c7; color: #92400e; }
        .t-dynamic { background: #dbeafe; color: #1e40af; }
    </style>
</head>
<body>
    <a href="index.jsp" class="back">← 返回首页</a>
    <h1>09 · 页面包含</h1>
    <p>JSP 有两种包含页面的方式：静态 include 和动态 include</p>

    <div class="vs">
        <div class="demo">
            <h3>静态 include <span class="tag t-static">指令</span></h3>
            <div class="code"><%@ include file="common/header.jspf" %></div>
            <p>编译时合并，两个文件变成一个 Servlet</p>
            <p><b>效果：</b></p>
            <div class="include-result">
                <%@ include file="common/header.jspf" %>
                <p style="font-size:13px; color:#6b7280; margin-top:10px;">
                    上面这行是用静态 include 包含进来的
                </p>
            </div>
        </div>

        <div class="demo">
            <h3>动态 include <span class="tag t-dynamic">动作</span></h3>
            <div class="code">&lt;jsp:include page="common/footer.jspf" /&gt;</div>
            <p>运行时调用，两个独立的 Servlet，结果拼在一起</p>
            <p><b>效果：</b></p>
            <div class="include-result" style="border-color:#3b82f6; background:#eff6ff;">
                <jsp:include page="common/footer.jspf" />
                <p style="font-size:13px; color:#6b7280; margin-top:10px;">
                    上面这行是用动态 include 包含进来的
                </p>
            </div>
        </div>
    </div>

    <div class="demo">
        <h3>区别对比</h3>
        <table style="width:100%; border-collapse:collapse;">
            <tr style="background:#f9fafb;">
                <th style="padding:10px; text-align:left; border-bottom:1px solid #e5e7eb;">特性</th>
                <th style="padding:10px; text-align:left; border-bottom:1px solid #e5e7eb;">静态 include</th>
                <th style="padding:10px; text-align:left; border-bottom:1px solid #e5e7eb;">动态 include</th>
            </tr>
            <tr>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">语法</td>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;"><%@ include file="..." %></td>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">&lt;jsp:include page="..." /&gt;</td>
            </tr>
            <tr>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">时机</td>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">编译时合并</td>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">运行时调用</td>
            </tr>
            <tr>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">Servlet 数量</td>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">1 个（合并后）</td>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">2 个（各自独立）</td>
            </tr>
            <tr>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">变量共享</td>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">共享（同一个 pageContext）</td>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">不共享（request 可以传参）</td>
            </tr>
            <tr>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">性能</td>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">快（一次编译）</td>
                <td style="padding:10px; border-bottom:1px solid #e5e7eb;">稍慢（运行时调用）</td>
            </tr>
            <tr>
                <td style="padding:10px;">适用场景</td>
                <td style="padding:10px;">静态内容（导航栏、页脚等不常变的）</td>
                <td style="padding:10px;">动态内容（需要传参数、经常修改的）</td>
            </tr>
        </table>
    </div>

    <a href="index.jsp" class="back">← 返回首页</a>
</body>
</html>
