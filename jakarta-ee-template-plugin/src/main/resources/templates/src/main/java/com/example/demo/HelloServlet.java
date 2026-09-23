package com.example.demo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * ============================================================================
 * HelloServlet —— 最基础的 Servlet 示例
 * ----------------------------------------------------------------------------
 * 【Servlet 是什么】
 *   运行在服务器端的 Java 小程序，负责「接收 HTTP 请求 → 处理 → 返回响应」。
 *   浏览器不能直接 new 它，而是由 Tomcat 这样的 Servlet 容器负责创建和调用。
 *
 * 【访问入口】
 *   本类注册的地址是 /hello，部署后完整地址为：
 *       http://localhost:8080/demo1_war_exploded/hello
 *   其中 /demo1_war_exploded 是「上下文路径（context path）」，
 *   它由部署方式决定（cargo 与 IDEA 都固定成这一段，见 pom.xml 与 README），
 *   所以代码里**不要**写死这个前缀，只用相对于它的 /hello 即可。
 *
 * 【生命周期（划重点）】
 *   1. 类加载 + 实例化：容器创建唯一一个 Servlet 实例（单例！）
 *   2. init()           ：初始化，只调用一次
 *   3. service()        ：每次请求都调用，内部再根据请求方法分发到 doGet/doPost
 *   4. destroy()        ：应用停止时调用一次，用来释放资源
 *   ⚠️ 因为是单例多线程，Servlet 里**不要**定义可变的成员变量来存请求数据，
 *      否则多个用户会互相干扰（想了解细节看 LifecycleServlet）。
 *
 * 【@WebServlet 注解】
 *   value 就是访问路径，必须以 / 开头。
 *   不需要在 web.xml 里再注册一遍，容器启动时会扫描到这个注解
 *   （前提是 web.xml 里 metadata-complete 为 false，见该文件注释）。
 * ============================================================================
 */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    /**
     * 处理 GET 请求。
     * 什么时候触发：直接在浏览器地址栏输入地址、点超链接、GET 表单提交。
     *
     * @param req  请求对象。可以从中取出参数、请求头、Cookie、会话等
     * @param resp 响应对象。用它设置状态码、响应头，并写入返回给浏览器的内容
     *
     * 注意方法签名是固定的，不要改参数类型，否则容器找不到该方法，
     * 会退化成父类默认实现，表现为 HTTP 405（方法不允许）。
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 告诉浏览器「我返回的是 HTML，字符集是 UTF-8」。
        // 这一步必须在拿 Writer 之前做，否则中文字符集设置不生效。
        resp.setContentType("text/html;charset=UTF-8");

        // 从「全局初始化参数」里取值（定义在 web.xml 的 <context-param> 中）。
        // getServletContext() 拿到的是整个应用的上下文，所有 Servlet 共享。
        // 参数不存在时返回 null，所以这里给个兜底默认值。
        String appName = getServletContext().getInitParameter("appName");
        if (appName == null) {
            appName = "Jakarta EE 教学模板";
        }

        // try-with-resources：块结束时自动调用 out.close()，
        // 即使中间抛异常也会关闭，避免连接泄漏。
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"zh-CN\">");
            out.println("<head>");
            out.println("  <meta charset=\"UTF-8\">");
            out.println("  <title>Hello Servlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("  <h1>Hello from Jakarta Servlet!</h1>");

            // 演示读取 web.xml 里的 context-param
            out.println("  <p>当前应用名称（来自 web.xml 的 context-param）：" + appName + "</p>");

            // 【进阶 · 已注释】读取「被转发进来」时携带的 request 域数据。
            //
            // 背景：ForwardRedirectServlet 在转发前会调用
            //       req.setAttribute("fromForward", "...")
            //       转发用的是同一个 request 对象，所以目标 Servlet 能取到；
            //       换成重定向就取不到了（重定向是两次独立请求）。
            //
            // 之所以先注释掉，是因为 ForwardRedirectServlet 当前整文件处于注释停用状态，
            // 没有它来放数据，这里永远取到 null。等启用那个 Servlet 后取消注释即可。
            //
            // Object forwarded = req.getAttribute("fromForward");
            // if (forwarded != null) {
            //     out.println("  <p style=\"color:#1e40af\">收到转发携带的数据：" + forwarded + "</p>");
            // }

            // 演示获取客户端的常见信息
            out.println("  <ul>");
            out.println("    <li>请求方法：" + req.getMethod() + "</li>");
            out.println("    <li>请求路径：" + req.getRequestURI() + "</li>");
            out.println("    <li>客户端 IP：" + req.getRemoteAddr() + "</li>");
            out.println("  </ul>");

            out.println("  <p><a href=\"index.html\">返回首页</a></p>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // =========================================================================
    // 【进阶 · 已注释】doPost：处理 POST 请求
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // 如果不重写这个方法，向本 Servlet 发 POST 请求会得到 405 错误。
    // 下面采用一个「偷懒但常见」的写法：直接把 POST 转交给 doGet 处理，
    // 这样 GET 和 POST 的响应内容完全一致。
    //
    // 真实项目里通常不会这么写，因为：
    //   1. 表单重复提交：用户按 F5 刷新会重复提交一次数据
    //   2. POST 一般用于「新增/修改」，处理完应该重定向，而不是直接输出页面
    // 关于第 2 点，详见 ForwardRedirectServlet 里对重定向的说明。
    //
    // 启用方法：去掉下面每一行开头的双斜杠即可
    //           （在 IDEA 里选中整段后按 Ctrl + 斜杠 可批量切换注释）。
    //
    // @Override
    // protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    //         throws ServletException, IOException {
    //     doGet(req, resp);
    // }
    // =========================================================================
}
