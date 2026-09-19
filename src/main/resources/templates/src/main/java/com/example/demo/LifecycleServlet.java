/*
 * ============================================================================
 *  【当前已注释停用】LifecycleServlet —— 观察 Servlet 生命周期与「单例」特性
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *  为什么停用：
 *    本模板默认只让「最基础的示例」HelloServlet 生效，
 *    其余进阶示例整文件注释保留，需要时再启用。
 *
 *  这个类演示了什么：
 *    init() 只在首次访问时执行一次；destroy() 在应用停止时执行；
 *    一个 Servlet 实例被所有请求线程共享（单例多线程），
 *    所以可变的成员变量不能用来存某个用户的数据。
 *
 *  怎么启用（在 IDEA 里操作）：
 *    1. 从下面第一行「// package ...」开始，一直选中到文件最后一行
 *    2. 按 Ctrl + 斜杠（注释切换快捷键），行首的双斜杠会被一次性去掉
 *    3. 重新打包部署后访问：http://localhost:8080/demo1_war_exploded/lifecycle
 *
 *  注意：本文件目前全部是注释，不会编译出任何 class，也不会注册任何 URL，
 *        所以直接访问 /lifecycle 会得到 404。
 * ============================================================================
 */
// package com.example.demo;

// import jakarta.servlet.ServletConfig;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.annotation.WebInitParam;
// import jakarta.servlet.annotation.WebServlet;
// import jakarta.servlet.http.HttpServlet;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import java.io.IOException;
// import java.io.PrintWriter;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.concurrent.atomic.AtomicInteger;

// /**
//  * ============================================================================
//  * LifecycleServlet —— 观察 Servlet 生命周期与「单例」特性
//  * ----------------------------------------------------------------------------
//  * 【怎么看效果】
//  *   1. 部署后第一次访问 /lifecycle：会看到「init() 执行时间」被打印出来
//  *   2. 反复刷新页面：init() 时间**不再变化**，请求计数一直累加
//  *      → 证明容器只创建了一个实例，init() 只执行了一次
//  *   3. 停止/重启 Tomcat：控制台会打印 destroy()，计数归零
//  *
//  * 【为什么说 Servlet 是「单例多线程」】
//  *   容器为每个 Servlet 只创建一个对象，但每个请求都由一个独立线程来处理。
//  *   多个用户同时访问时，它们会**共用同一个对象**。
//  *   因此：
//  *     ✅ 安全：只读的成员变量、无状态的局部变量
//  *     ❌ 危险：可变的成员变量保存「某个用户的数据」
//  *   本类为了演示计数器，故意用了成员变量，但选用了 AtomicInteger
//  *   保证多线程下的原子性。真实项目里更推荐把状态放进
//  *   HttpSession（每个用户独立）或数据库。
//  *
//  * 【@WebServlet 带初始化参数】
//  *   注解里可以用 initParams 给这个 Servlet 配参数，
//  *   作用等价于 web.xml 中 <servlet> 内部写的 <init-param>，
//  *   区别是它只属于当前 Servlet，不能用 getServletContext() 读到，
//  *   必须用 getServletConfig() 读。
//  * ============================================================================
//  */
// @WebServlet(
//         urlPatterns = "/lifecycle",
//         initParams = {
//                 @WebInitParam(name = "author", value = "教学模板"),
//                 @WebInitParam(name = "version", value = "1.0")
//         }
// )
// public class LifecycleServlet extends HttpServlet {

//     /** 容器创建实例的时间，用来证明 init() 只执行一次 */
//     private String initTime;

//     /**
//      * 请求计数器。
//      * 用 AtomicInteger 而不是普通 int 的原因：
//      * 多线程同时执行 count++ 会丢数据（读-改-写不是原子操作），
//      * AtomicInteger 内部用 CAS 保证线程安全。
//      */
//     private final AtomicInteger requestCount = new AtomicInteger(0);

//     /** 记录最后一次访问时间，同样是被多线程共享的可变状态 */
//     private volatile String lastAccessTime = "（还没有人访问过）";

//     private static final DateTimeFormatter FMT =
//             DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//     /**
//      * 初始化方法：容器创建实例后调用，且只调用一次。
//      *
//      * 适合做「一次性的准备工作」：读配置、建连接池、加载缓存等。
//      * 如果这里抛出 ServletException，Servlet 会初始化失败，
//      * 访问对应路径时容器会返回 500 错误。
//      *
//      * 注意：默认情况下，Servlet 在**第一次被访问时**才创建并 init，
//      * 所以「第一次访问比较慢」。若希望应用启动时就初始化，
//      * 可以用 @WebServlet(loadOnStartup = 1)，
//      * 数字越小优先级越高（等价于 web.xml 的 <load-on-startup>）。
//      */
//     @Override
//     public void init(ServletConfig config) throws ServletException {
//  ⚠️ 重写 init(ServletConfig) 时，必须调用 super.init(config)，
//  否则 getServletConfig() / getServletContext() 会返回 null，
//  后续按规范应该避免这种情况。
//         super.init(config);

//         this.initTime = LocalDateTime.now().format(FMT);

//         System.out.println("==============================================");
//         System.out.println("[LifecycleServlet] init() 执行，" +
//                 "整个应用生命周期内只会看到这一行一次");
//         System.out.println("[LifecycleServlet] 实例 hashcode = " + this.hashCode());
//         System.out.println("==============================================");
//     }

//     /**
//      * 处理 GET 请求，把实例状态与生命周期信息展示给浏览器。
//      */
//     @Override
//     protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//             throws ServletException, IOException {

//         resp.setContentType("text/html;charset=UTF-8");

//  每次请求计数 +1，并取出加完之后的值
//         int current = requestCount.incrementAndGet();
//         lastAccessTime = LocalDateTime.now().format(FMT);

//  读取本 Servlet 自己的初始化参数（来自注解里的 initParams）
//         String author = getServletConfig().getInitParameter("author");
//         String version = getServletConfig().getInitParameter("version");

//  this.hashCode() 每次刷新都相同 → 证明是同一个对象在处理所有请求
//         int hash = this.hashCode();

//         try (PrintWriter out = resp.getWriter()) {
//             out.println("<!DOCTYPE html>");
//             out.println("<html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">");
//             out.println("<title>Servlet 生命周期</title>");
//             out.println("<style>");
//             out.println("  body{font-family:'Microsoft YaHei',sans-serif;"
//                     + "line-height:1.8;padding:30px;max-width:760px;margin:0 auto;color:#1f2937}");
//             out.println("  table{border-collapse:collapse;width:100%;margin:12px 0}");
//             out.println("  th,td{border:1px solid #e5e7eb;padding:8px 12px;text-align:left;font-size:14px}");
//             out.println("  th{background:#f9fafb;width:200px}");
//             out.println("  .ok{background:#eff6ff;border:1px solid #bfdbfe;border-radius:6px;"
//                     + "padding:12px;color:#1e40af;font-size:14px}");
//             out.println("</style></head><body>");

//             out.println("<h1>Servlet 生命周期演示</h1>");

//             out.println("<table>");
//             out.println("  <tr><th>init() 执行时间</th><td>" + initTime + "</td></tr>");
//             out.println("  <tr><th>当前请求序号</th><td>第 " + current + " 次</td></tr>");
//             out.println("  <tr><th>最后一次访问</th><td>" + lastAccessTime + "</td></tr>");
//             out.println("  <tr><th>实例 hashcode</th><td>" + hash + "</td></tr>");
//             out.println("  <tr><th>init-param: author</th><td>" + author + "</td></tr>");
//             out.println("  <tr><th>init-param: version</th><td>" + version + "</td></tr>");
//             out.println("  <tr><th>处理本次请求的线程</th><td>"
//                     + Thread.currentThread().getName() + "</td></tr>");
//             out.println("</table>");

//             out.println("<p class=\"ok\">"
//                     + "<b>请反复刷新本页面。</b><br>"
//                     + "「init() 执行时间」和「实例 hashcode」始终不变，"
//                     + "但「请求序号」持续增长 —— "
//                     + "这就是 Servlet「单例、init 只执行一次、每个请求一个线程」的直观证明。"
//                     + "</p>");

//             out.println("<p><a href=\"index.html\">返回首页</a></p>");
//             out.println("</body></html>");
//         }
//     }

//     /**
//      * 销毁方法：应用停止（或容器关闭）时调用一次。
//      *
//      * 用来释放资源：关闭数据库连接、停止后台线程、写日志等。
//      * 注意它**不是**由浏览器请求触发的，单靠刷新页面永远看不到它的输出，
//      * 必须去停止 Tomcat，在控制台里查看。
//      */
//     @Override
//     public void destroy() {
//         System.out.println("==============================================");
//         System.out.println("[LifecycleServlet] destroy() 执行，"
//                 + "本次共处理 " + requestCount.get() + " 个请求");
//         System.out.println("==============================================");
//     }
// }
