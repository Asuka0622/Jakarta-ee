/*
 * ============================================================================
 *  【当前已注释停用】ParamsServlet —— 接收请求参数与处理中文编码
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  为什么停用：
 *    本模板默认只让「最基础的示例」HelloServlet 生效，
 *    其余进阶示例整文件注释保留，需要时再启用。
 *
 *  这个类演示了什么：
 *    GET 与 POST 两种方式取参数的区别、取多个同名参数、
 *    遍历全部参数、中文乱码的成因与正确解法、输出转义防 XSS。
 *
 *  怎么启用（在 IDEA 里操作）：
 *    1. 从下面第一行「// package ...」开始，一直选中到文件最后一行
 *    2. 按 Ctrl + 斜杠（注释切换快捷键），行首的双斜杠会被一次性去掉
 *    3. 重新打包部署后访问：http://localhost:8080/demo1_war_exploded/params
 *
 *  依赖关系：
 *    index.html 里「在线试试参数传递」那一节的两个表单就提交到本 Servlet，
 *    启用本文件之后，那两个表单才能正常工作；
 *    在此之前提交会得到 404。
 *
 *  注意：本文件目前全部是注释，不会编译出任何 class，也不会注册任何 URL。
 * ============================================================================
 */
// package com.example.demo;

// import jakarta.servlet.ServletException;
// import jakarta.servlet.annotation.WebServlet;
// import jakarta.servlet.http.HttpServlet;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import java.io.IOException;
// import java.io.PrintWriter;
// import java.util.Arrays;
// import java.util.Map;

// /**
//  * ============================================================================
//  * ParamsServlet —— 接收请求参数 & 解决中文乱码
//  * ----------------------------------------------------------------------------
//  * 【GET 与 POST 的参数有什么区别】
//  *   GET  ：参数拼在 URL 后面，如 /params?name=张三&age=18
//  *          优点：能收藏、能分享、便于调试
//  *          缺点：地址栏可见（不适合密码），长度有上限（浏览器/服务器限制）
//  *   POST ：参数放在请求体（body）里，地址栏看不到
//  *          适合提交敏感数据、上传文件、数据量大或需要改数据的场景
//  *
//  * 【如何取值】
//  *   req.getParameter("name")     → 取单个值，不存在时返回 null
//  *   req.getParameterValues("x")  → 取多个值（如多选框），返回 String[]
//  *   req.getParameterMap()        → 取全部参数，返回 Map<String, String[]>
//  *
//  *   ⚠️ 一个关键点：参数值永远是「字符串」。
//  *      需要数字要自己转换：Integer.parseInt(req.getParameter("age"))
//  *      转换前必须判空，否则 null 会抛出 NumberFormatException。
//  *
//  * 【中文乱码的成因与解决】
//  *   乱码只发生在「浏览器用什么编码发」和「服务端用什么编码解」不一致时。
//  *   解决方式分两半：
//  *     - 请求体（POST）：在读取参数**之前**调用 req.setCharacterEncoding("UTF-8")。
//  *       不同容器/版本的默认编码不一致（老版本是 ISO-8859-1，必然乱码），
//  *       所以显式设置是最稳妥的做法。
//  *       ⚠️ 顺序很重要：一旦调用过 getParameter()，编码就已经确定，
//  *          之后再 setCharacterEncoding 是无效的。
//  *     - 响应内容：resp.setContentType("text/html;charset=UTF-8")
//  *   另外，浏览器端页面的 <meta charset="UTF-8"> 和表单所在页面的编码也要对齐。
//  * ============================================================================
//  */
// @WebServlet("/params")
// public class ParamsServlet extends HttpServlet {

//     /**
//      * 处理 GET 请求（地址栏直接访问、GET 表单提交）。
//      */
//     @Override
//     protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//             throws ServletException, IOException {
//         handle(req, resp);
//     }

//     /**
//      * 处理 POST 请求（POST 表单提交）。
//      *
//      * 这里**没有**把请求转给 doGet，而是各自调用同一个 handle 方法，
//      * 只是为了在页面里能看出到底是哪种方式提交的。
//      */
//     @Override
//     protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//             throws ServletException, IOException {
//         handle(req, resp);
//     }

//     /**
//      * GET / POST 共用的处理逻辑。
//      */
//     private void handle(HttpServletRequest req, HttpServletResponse resp)
//             throws ServletException, IOException {

//  ① 设置请求体编码。必须放在任何 getParameter() 之前，否则不生效。
//         req.setCharacterEncoding("UTF-8");

//  ② 设置响应编码。同样要在拿 Writer 之前调用。
//         resp.setContentType("text/html;charset=UTF-8");

//  ③ 取参数。此时编码已经确定，中文能正确解析。
//         String name = req.getParameter("name");

//  ④ 演示「同名多值」：getParameterValues 返回数组。
//     单个值也能用，返回长度为 1 的数组；参数不存在时返回 null。
//         String[] hobbies = req.getParameterValues("hobby");

//  ⑤ 一口气拿到所有参数。注意值是 String[]，因为同一个名字可以有多个值。
//         Map<String, String[]> allParams = req.getParameterMap();

//         try (PrintWriter out = resp.getWriter()) {
//             out.println("<!DOCTYPE html>");
//             out.println("<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">");
//             out.println("<title>请求参数演示</title>");
//             out.println("<style>");
//             out.println("  body{font-family:'Microsoft YaHei',sans-serif;line-height:1.8;"
//                     + "padding:30px;max-width:760px;margin:0 auto;color:#1f2937}");
//             out.println("  table{border-collapse:collapse;width:100%;margin:12px 0}");
//             out.println("  th,td{border:1px solid #e5e7eb;padding:8px 12px;text-align:left;font-size:14px}");
//             out.println("  th{background:#f9fafb}");
//             out.println("  .tag{display:inline-block;padding:2px 8px;border-radius:4px;"
//                     + "font-size:13px;color:#fff}");
//             out.println("  .get{background:#10b981}.post{background:#f59e0b}");
//             out.println("  .warn{background:#fef2f2;border:1px solid #fecaca;border-radius:6px;"
//                     + "padding:12px;color:#b91c1c;font-size:14px}");
//             out.println("  code{background:#f3f4f6;padding:2px 5px;border-radius:4px;"
//                     + "font-family:Consolas,monospace;font-size:13px;color:#be123c}");
//             out.println("</style></head><body>");

//             out.println("<h1>请求参数演示</h1>");

//  显示本次用的是 GET 还是 POST
//             String method = req.getMethod();
//             String cls = "GET".equals(method) ? "get" : "post";
//             out.println("<p>请求方式：<span class=\"tag " + cls + "\">" + method + "</span>");

//             if ("GET".equals(method)) {
//                 out.println("　（参数在地址栏里，注意看浏览器的 URL）</p>");
//             } else {
//                 out.println("　（参数在请求体中，地址栏是干净的）</p>");
//             }

//  演示 1：取单个参数（含判空处理）
//             out.println("<h2>1. 单个参数 name</h2>");
//             if (name == null) {
//                 out.println("<p class=\"warn\">没有收到名为 <code>name</code> 的参数，"
//                         + "<code>getParameter()</code> 返回了 null。<br>"
//                         + "请回到<a href=\"index.html\">首页</a>通过表单提交，"
//                         + "或手动在地址栏加上 <code>?name=张三</code>。</p>");
//             } else {
//                 out.println("<p>收到：<b>" + escape(name) + "</b>　"
//                         + "（长度 " + name.length() + " 个字符，"
//                         + "中文能正常显示说明编码设置正确）</p>");
//             }

//  演示 2：取多个同名参数
//             out.println("<h2>2. 多个同名参数 hobby</h2>");
//             if (hobbies == null) {
//                 out.println("<p>未收到 <code>hobby</code>。它用来演示"
//                         + "<code>getParameterValues()</code>——"
//                         + "多选框提交时会得到多个同名值。例如地址栏追加 "
//                         + "<code>&amp;hobby=读书&amp;hobby=游泳</code> 再看结果。</p>");
//             } else {
//                 out.println("<p>共收到 " + hobbies.length + " 个值：</p><ul>");
//                 for (String h : hobbies) {
//                     out.println("  <li>" + escape(h) + "</li>");
//                 }
//                 out.println("</ul>");
//             }

//  演示 3：遍历全部参数
//             out.println("<h2>3. 全部参数（getParameterMap）</h2>");
//             if (allParams.isEmpty()) {
//                 out.println("<p>本次请求没有任何参数。</p>");
//             } else {
//                 out.println("<table>");
//                 out.println("  <tr><th>参数名</th><th>值</th></tr>");
//                 for (Map.Entry<String, String[]> e : allParams.entrySet()) {
//                     out.println("  <tr><td>" + escape(e.getKey()) + "</td><td>"
//                             + escape(Arrays.toString(e.getValue())) + "</td></tr>");
//                 }
//                 out.println("</table>");
//             }

//             out.println("<p><a href=\"index.html\">返回首页</a></p>");
//             out.println("</body></html>");
//         }
//     }

//     /**
//      * 把 HTML 特殊字符转义后再输出。
//      *
//      * 为什么必须转义：如果用户输入 <script>alert(1)</script>，
//      * 直接拼进 HTML 就会被浏览器当代码执行（XSS 跨站脚本攻击）。
//      * 转义后它们只会显示成普通文字。
//      *
//      * ⚠️ 教学重点：所有「用户能控制的内容」在输出到 HTML 前都要转义。
//      *    真实项目里推荐用 JSTL 的 <c:out> 或模板引擎自带的转义，
//      *    这里手写一份是为了让你看清原理。
//      */
//     private String escape(String s) {
//         if (s == null) {
//             return "";
//         }
//         return s.replace("&", "&amp;")
//                 .replace("<", "&lt;")
//                 .replace(">", "&gt;")
//                 .replace("\"", "&quot;")
//                 .replace("'", "&#39;");
//     }
// }
