/*
 * ============================================================================
 *  【当前已注释停用】ForwardRedirectServlet —— 转发与重定向的区别
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  为什么停用：
 *    本模板默认只让「最基础的示例」HelloServlet 生效，
 *    其余进阶示例整文件注释保留，需要时再启用。
 *
 *  这个类演示了什么：
 *    forward（转发）与 redirect（重定向）在请求次数、地址栏、
 *    request 域数据共享、能否跳外部站点上的差异，并给出可点击的对比页面。
 *
 *  怎么启用（在 IDEA 里操作）：
 *    1. 从下面第一行「// package ...」开始，一直选中到文件最后一行
 *    2. 按 Ctrl + 斜杠（注释切换快捷键），行首的双斜杠会被一次性去掉
 *    3. 重新打包部署后访问：http://localhost:8080/demo1_war_exploded/forward-redirect
 *
 *  关联说明：
 *    HelloServlet 里有一段「读取被转发进来时携带的 request 域数据」的代码，
 *    也是注释状态；启用本文件后，那段代码才有意义，可以一并启用。
 *
 *  注意：本文件目前全部是注释，不会编译出任何 class，也不会注册任何 URL。
 * ============================================================================
 */
// package com.example.demo;

// import jakarta.servlet.RequestDispatcher;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.annotation.WebServlet;
// import jakarta.servlet.http.HttpServlet;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import java.io.IOException;
// import java.io.PrintWriter;

// /**
//  * ============================================================================
//  * ForwardRedirectServlet —— 「转发」与「重定向」的区别
//  * ----------------------------------------------------------------------------
//  * 这两者都用来「跳转到另一个资源」，但机制完全不同，是面试和实战的高频考点。
//  *
//  * ┌────────────┬──────────────────────────┬──────────────────────────────┐
//  * │            │ 转发 forward              │ 重定向 redirect               │
//  * ├────────────┼──────────────────────────┼──────────────────────────────┤
//  * │ 谁在跳      │ 服务器内部跳              │ 服务器让浏览器再发一次请求      │
//  * │ 请求次数    │ 1 次                      │ 2 次                          │
//  * │ 地址栏      │ 不变                      │ 变成新地址                     │
//  * │ 能否共享    │ 能，同一个 request 对象    │ 不能，是新的 request           │
//  * │ request 数据│ （可用 setAttribute 传递）│ （数据会丢）                   │
//  * │ 能否跳外部站│ 不能，只能跳本应用内      │ 能，可跳任意 URL               │
//  * │ 典型用途    │ 请求处理后交给页面渲染     │ 表单提交成功后防止重复提交      │
//  * └────────────┴──────────────────────────┴──────────────────────────────┘
//  *
//  * 【本示例怎么用】
//  *   访问 /forward-redirect            → 页面提供两个按钮
//  *   访问 /forward-redirect?action=fwd → 执行转发，注意地址栏仍停留在原地址
//  *   访问 /forward-redirect?action=red → 执行重定向，地址栏会变成 /hello
//  *
//  * ⚠️ 两条铁律（踩坑最多的就是这两条）
//  *   1. 转发或重定向必须在「还没有向响应写入任何内容」之前调用。
//  *      一旦调用过 resp.getWriter() 并输出了内容，响应就已提交，
//  *      再跳转会抛出 IllegalStateException，或者跳转无效。
//  *   2. 重定向的地址要用完整路径。直接写 sendRedirect("/hello") 会跳到
//  *      服务器根路径的 /hello（即 http://localhost:8080/hello），漏掉应用名。
//  *      正确写法是 req.getContextPath() + "/hello"，
//  *      这里的 getContextPath() 就是 /demo1_war_exploded。
//  * ============================================================================
//  */
// @WebServlet("/forward-redirect")
// public class ForwardRedirectServlet extends HttpServlet {

//     @Override
//     protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//             throws ServletException, IOException {

//  依据参数决定演示哪种跳转
//         String action = req.getParameter("action");

//         if ("fwd".equals(action)) {
//             forwardDemo(req, resp);
//             return;
//         }
//         if ("red".equals(action)) {
//             redirectDemo(req, resp);
//             return;
//         }

//  没有指定 action 时，输出一个带按钮的说明页
//         showChooser(req, resp);
//     }

//     /**
//      * 演示「转发」：服务器内部把请求交给另一个 Servlet 继续处理。
//      *
//      * 关键点：
//      *   - 用同一个 request 对象，所以可以用 setAttribute 传数据过去，
//      *     目标 Servlet 用 getAttribute 取出来 —— 这是 MVC 里
//      *     「Servlet 查数据 → 转发给 JSP 渲染」的实现基础。
//      *   - 路径以 / 开头，表示相对于「应用根目录」（不是服务器根目录）。
//      *   - 浏览器完全不知道发生了跳转，地址栏保持 /forward-redirect 不变。
//      */
//     private void forwardDemo(HttpServletRequest req, HttpServletResponse resp)
//             throws ServletException, IOException {

//  把数据放进 request 域，目标 Servlet 能读到
//         req.setAttribute("fromForward", "这条消息由 ForwardRedirectServlet 放入 request 域");

//  取得转发器并转发。
//  ⚠️ 转发之前不能有任何输出，否则会抛 IllegalStateException。
//         RequestDispatcher dispatcher = req.getRequestDispatcher("/hello");
//         dispatcher.forward(req, resp);

//  转发之后不要继续写响应：此时响应已由目标 Servlet 提交，
//  下面这行代码即使执行也不会生效（实践中应当避免写在这里）。
//     }

//     /**
//      * 演示「重定向」：告诉浏览器「你要的东西在别处，请重新访问这个地址」。
//      *
//      * 本质是返回 HTTP 302 状态码 + 一个 Location 响应头，
//      * 浏览器收到后自动发起第二次请求。所以：
//      *   - 地址栏会变成新地址
//      *   - 原来的 request 域数据全部丢失
//      *   - 跳转地址必须是「客户端能访问到」的完整路径
//      */
//     private void redirectDemo(HttpServletRequest req, HttpServletResponse resp)
//             throws IOException {

//  getContextPath() 返回 "/demo1_war_exploded"，拼出的结果就是
//  "/demo1_war_exploded/hello"，浏览器才能正确定位。
//         String target = req.getContextPath() + "/hello";

//  同样要求：调用前不能有输出。
//         resp.sendRedirect(target);
//     }

//     /**
//      * 输出选择页：两个按钮分别触发上面两种跳转。
//      */
//     private void showChooser(HttpServletRequest req, HttpServletResponse resp)
//             throws IOException {

//         resp.setContentType("text/html;charset=UTF-8");

//         try (PrintWriter out = resp.getWriter()) {
//             out.println("<!DOCTYPE html>");
//             out.println("<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">");
//             out.println("<title>转发 vs 重定向</title>");
//             out.println("<style>");
//             out.println("  body{font-family:'Microsoft YaHei',sans-serif;line-height:1.8;"
//                     + "padding:30px;max-width:820px;margin:0 auto;color:#1f2937}");
//             out.println("  table{border-collapse:collapse;width:100%;margin:16px 0}");
//             out.println("  th,td{border:1px solid #e5e7eb;padding:8px 12px;font-size:14px;text-align:left}");
//             out.println("  th{background:#f9fafb}");
//             out.println("  a.btn{display:inline-block;margin:6px 10px 6px 0;padding:10px 20px;"
//                     + "border-radius:6px;text-decoration:none;color:#fff;font-size:14px}");
//             out.println("  .fwd{background:#3b82f6}.red{background:#f59e0b}");
//             out.println("  .hint{background:#eff6ff;border:1px solid #bfdbfe;border-radius:6px;"
//                     + "padding:12px;color:#1e40af;font-size:14px}");
//             out.println("</style></head><body>");

//             out.println("<h1>转发 vs 重定向</h1>");

//             out.println("<table>");
//             out.println("  <tr><th></th><th>转发 forward</th><th>重定向 redirect</th></tr>");
//             out.println("  <tr><th>请求次数</th><td>1 次</td><td>2 次</td></tr>");
//             out.println("  <tr><th>浏览器地址栏</th><td>不变</td><td>变成新地址</td></tr>");
//             out.println("  <tr><th>能否共享 request 数据</th><td>能</td><td>不能</td></tr>");
//             out.println("  <tr><th>能否跳到外部网站</th><td>不能</td><td>能</td></tr>");
//             out.println("  <tr><th>典型用途</th><td>交给页面渲染</td>"
//                     + "<td>提交成功后防重复提交</td></tr>");
//             out.println("</table>");

//             out.println("<p>"
//                     + "<a class=\"btn fwd\" href=\"forward-redirect?action=fwd\">试试转发 →</a>"
//                     + "<a class=\"btn red\" href=\"forward-redirect?action=red\">试试重定向 →</a>"
//                     + "</p>");

//             out.println("<p class=\"hint\">"
//                     + "点击后请盯住浏览器的地址栏：<br>"
//                     + "「转发」后地址栏仍然是 <b>/forward-redirect?action=fwd</b>，"
//                     + "但页面内容变成了 HelloServlet 的输出；<br>"
//                     + "「重定向」后地址栏会变成 <b>/demo1_war_exploded/hello</b>，"
//                     + "地址里已经看不到 action 参数了。"
//                     + "</p>");

//             out.println("<p><a href=\"index.html\">返回首页</a></p>");
//             out.println("</body></html>");
//         }
//     }
// }
