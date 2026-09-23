# Jakarta EE Web 教学模板

一个可以直接打包部署的 Jakarta EE Web 项目骨架，所有文件都写了详细中文注释，适合边看边改来学习 Servlet。

**项目名：`demo1`　上下文路径：`/demo1_war_exploded`　访问地址：<http://localhost:8080/demo1_war_exploded/>**

---

## 零、在 IDEA 里跑起来（社区版 / 旗舰版都适用）

### 用 IDEA 社区版（Community）

社区版**没有**「Tomcat Server」运行配置 —— 那是旗舰版独有功能。
所以社区版走项目自带的 cargo 插件，效果一样。有两种「一键」方式：

**方式 A：点 IDEA 右上角的绿色三角（推荐）**

项目根目录的 `.run/StartTomcat.run.xml` 里已经配好了一个共享运行配置。
用 IDEA 打开本项目后，右上角运行配置下拉框里会出现 **「启动 Tomcat（demo1）」**，
点绿色三角（或按 `Shift + F10`）即可启动。
停止点运行窗口左侧的红色方块（或 `Ctrl + F2`）。

> 如果下拉框里没有这一项，检查是否把项目当成了 Maven 项目打开
> （右侧应能看到 Maven 面板）。也可以在 `.run/StartTomcat.run.xml`
> 上右键选择 `Run`。

**方式 B：双击项目根目录的 `start-tomcat.bat`**

不用打开 IDEA，直接双击即可，效果与 `mvn clean package cargo:run` 相同。
（该脚本用的是系统 `PATH` 里的 `mvn`。）

**方式 C：手动敲命令**

1. 打开 IDEA 下方的 **Terminal**，或在右侧打开 **Maven** 面板
2. 执行这一条命令（Maven 面板则依次双击 `clean`、`package`，再双击 `Plugins → cargo → cargo:run`）：

   ```bash
   mvn clean package cargo:run
   ```

3. 等控制台出现这一行，说明启动成功：

   ```
   [INFO] Tomcat 11.0.25 started on port [8080]
   ```

4. 浏览器打开 <http://localhost:8080/demo1_war_exploded/> 即可看到页面
5. 停止服务：在跑命令的那个窗口按 `Ctrl + C`

> **启动失败怎么办？**
> 先用 IDEA 菜单 **Tools → 检查 Tomcat / cargo 运行环境**（插件提供的体检功能）：
> 它会检查 pom、Tomcat 目录、cargo 依赖、启动端口四项，缺什么直接列出来并给修复命令。
>
> 最常见的两个原因：
>
> - 报 `NoClassDefFoundError: ...DefaultJvmLauncher` —— 本地 Maven 仓库里的 cargo 依赖没下全。
> - 报 `Port number 8080 ... is in use` —— 8080 已被别的程序占用，通常是**残留的 Tomcat 进程**
>   或本机 Tomcat 的 **Windows 服务**。这时 cargo 的 Tomcat 根本起不来，浏览器连上的是那个旧的
>   Tomcat，它里面没有本项目，所以**怎么访问都是 404**。先把它关掉再重跑：
>   `netstat -ano | findstr :8080`，拿最后一列的 PID 到任务管理器结束；
>   或者把 `pom.xml` 里 cargo 插件配置中的 `<cargo.servlet.port>8080</cargo.servlet.port>`
>   改成 `8081`（更省事的写法：启动命令后加 `-Dcargo.servlet.port=8081`），
>   访问地址随之改成 <http://localhost:8081/demo1_war_exploded/>。

### 用 IDEA 旗舰版（Ultimate）

1. `Run` → `Edit Configurations` → `+` → `Tomcat Server` → `Local`
2. `Application server` 选本机 Tomcat 安装目录
3. 切到 `Deployment` 页签 → `+` → `Artifact` → 选 **`demo1:war exploded`**
4. `Application context` 填 `/demo1_war_exploded`
5. 点运行，IDEA 会自动打开浏览器

> 两种方式部署后的访问地址**完全一样**，都是
> <http://localhost:8080/demo1_war_exploded/>
> 因为 `pom.xml` 里 cargo 的上下文路径就是照着 IDEA 的默认命名配的。

---

## 一、这份模板是「基础版」

默认只让最基础的示例生效，其余进阶内容全部整段注释保留，需要时再启用。

| 内容 | 当前状态 | 如何启用 |
| --- | --- | --- |
| HelloServlet | 生效 | 直接访问 `/hello` |
| LifecycleServlet | 整文件注释 | 取消该文件的注释，见下 |
| ParamsServlet | 整文件注释 | 取消该文件的注释，见下 |
| ForwardRedirectServlet | 整文件注释 | 取消该文件的注释，见下 |
| pom.xml 里的插件配置 | 属于基础，保持启用 | 不能注释：注释后 `mvn package` 会失败，原因见 pom.xml 里的说明 |
| pom.xml 里的 cargo 插件 | 生效 | 用来一条命令启动 Tomcat；删掉不影响 `mvn package` |
| start-tomcat.bat 与 .run 运行配置 | 生效 | 一键启动用；删掉不影响构建，只是要自己敲命令 |
| web.xml 里的会话配置、全局参数 | 已注释 | 去掉对应段落外层的注释 |
| index.html 的样式与全部内容 | 已注释（页面只留一个 Hello World 标题） | 去掉对应段落外层的注释 |
| 本文件第五、六、七节 | 已注释 | 见本文件末尾 |

### 怎么启用那三个 Servlet

1. 在 IDEA 里打开对应的 `.java` 文件
2. 从第一行 `// package ...` 开始，一直选中到文件最后一行
3. 按 `Ctrl + 斜杠`，行首的双斜杠会被一次性去掉
4. 重新打包部署

每个文件顶部都写了它演示的知识点和启用后的访问地址。

---

## 二、快速开始

### 1. 环境要求

| 组件 | 版本要求 | 说明 |
| --- | --- | --- |
| JDK | 17 或更高 | 本项目按 `release 17` 编译，Tomcat 11 最低要求 17 |
| Maven | 3.6+ | 用来编译打包；IDEA 自带也行 |
| Tomcat | 10.1+ 或 11.x | 必须用 Tomcat 10 及以上，原因见第五节的第 1 条 |

### 2. 打包

在项目根目录执行：

```bash
mvn clean package
```

成功后在 `target/` 下得到 `demo1.war`。

> 只在 IDEA 里跑的话，也可以直接用 Maven 面板双击 `clean` 和 `package`。

### 3. 一条命令启动 Tomcat（开发时最省事）

项目已经在 `pom.xml` 里配好了 cargo 插件，只要本机装了 Tomcat，一条命令就能完成「打包 + 启动 + 部署」：

```bash
mvn clean package cargo:run
```

启动成功后会看到这样的日志：

```
[INFO] Tomcat 11.0.25 started on port [8080]
[INFO] The cargo:run mojo will wait until a TERM signal is received...
```

然后浏览器打开 <http://localhost:8080/demo1_war_exploded/> 即可。

**停止**：回到执行命令的那个窗口按 `Ctrl + C`，Tomcat 会一起关掉。

两个要留意的地方：

**一、Tomcat 路径**

`pom.xml` 顶部有个属性指向本机 Tomcat 安装目录：

```xml
<tomcat.home>本机Tomcat安装目录</tomcat.home>
```

生成项目时插件会自动探测本机 Tomcat 并把这个值填好。**探测不到时它会是一句提示文字**，
这时需要你改成实际路径；换电脑后也一样要改。

不想改文件可以临时在命令行覆盖：

```bash
mvn clean package cargo:run -Dtomcat.home=D:/你的/tomcat目录
```

懒得找路径：在 IDEA 里执行菜单 **Tools → 检查 Tomcat / cargo 运行环境**，
它会指出路径问题，并帮你选目录、自动写回 `pom.xml`。

**二、端口**

默认 8080。端口写在 `pom.xml` 里 cargo 插件的配置中：

```xml
<plugin>
    <groupId>org.codehaus.cargo</groupId>
    <artifactId>cargo-maven3-plugin</artifactId>
    <configuration>
        <!-- 省略 container、configuration 等其它配置 -->
        <properties>
            <cargo.servlet.port>8080</cargo.servlet.port>   <!-- 改这一行 -->
        </properties>
    </configuration>
</plugin>
```

**改法一（推荐）：改 `pom.xml`。** 改成 8081 后重新执行 `mvn clean package cargo:run`，
访问地址随之变成 <http://localhost:8081/demo1_war_exploded/>。

好处是三种启动方式（IDEA 右上角绿色三角、双击 `start-tomcat.bat`、命令行）会自动保持一致，
不会出现「某个入口还是旧端口」的情况。

**改法二：不改文件，临时覆盖。**

```bash
mvn clean package cargo:run -Dcargo.servlet.port=8081
```

在 IDEA 里则是改运行配置：`Run` → `Edit Configurations` → 选中「启动 Tomcat（demo1）」，
在 **Goals** 输入框里追加 `-Dcargo.servlet.port=8081`，整行变成
`clean package cargo:run -Dcargo.servlet.port=8081`。

**同时跑两个项目时，还得改第二个端口。** cargo 除了 8080 还会占用一个
`cargo.rmi.port`（默认 8205），只改 8080 会启动失败并报
`Port number 8205 ... is in use`，所以两个都要改：

```bash
mvn clean package cargo:run -Dcargo.servlet.port=8081 -Dcargo.rmi.port=8206
```

**旗舰版用户注意**：IDEA 旗舰版的 `Tomcat Server` 运行配置有它自己的 `HTTP port`
（`Run` → `Edit Configurations` → `Tomcat Server` → `Server` 页签）。
那是 IDEA 启动 Tomcat 时用的，和 `pom.xml` 里的 cargo 配置**互不影响**，
两种方式都用的话两处都要改，否则总有一种方式地址对不上。
（社区版没有 `Tomcat Server` 运行配置，只需要改 `pom.xml`。）

**8080 被谁占着？** 在命令行执行：

```
netstat -ano | findstr :8080
```

最后一列是占用者的进程 PID，到任务管理器里结束它即可 —— 多半是上一次没关干净的 Tomcat。
不想关它就按上面的办法换个端口。

> Cargo 会在 `target/cargo/config` 下另建一套独立的 Tomcat 配置来运行，
> **不会**改动你 Tomcat 安装目录里的任何文件，也不会把 war 拷进它的 webapps。
> 所以可以放心，跑完不会把你的 Tomcat 环境弄乱。
>
> 不想依赖本机 Tomcat 的话，`pom.xml` 里注释掉的那段 `zipUrlInstaller` 写法
> 可以让 Cargo 自动下载一个 Tomcat，真正做到「换台机器就能跑」。

### 4. 部署到 Tomcat

**方式一：拷贝 war 包（最简单）**

1. 把 `target/demo1.war` 复制到 Tomcat 的 `webapps/` 目录
2. 启动 Tomcat：`bin/startup.bat`（Windows）或 `bin/startup.sh`（Linux/Mac）
3. Tomcat 会自动解压部署

> ⚠️ 这种方式下，上下文路径取自 war 文件名，所以访问地址是
> <http://localhost:8080/demo1/>，与下面表格里的 `/demo1_war_exploded` 不同。
> 想让手动部署也用同一个地址，复制过去时把 war 改名成 `demo1_war_exploded.war` 即可。

**方式二：IDEA 配置 Tomcat（方便调试）**

1. `Run` → `Edit Configurations` → `+` → `Tomcat Server` → `Local`
2. `Application server` 选择本机 Tomcat 目录
3. 切到 `Deployment` 页签 → `+` → `Artifact` → 选择 `demo1:war exploded`
4. `Application context` 填 `/demo1_war_exploded`
5. 点运行，IDEA 会自动打开浏览器

> 用 `war exploded` 的好处是改了 JSP/HTML 无需重新打包即可生效。

---

## 三、访问地址

假设 Tomcat 端口是默认的 8080，上下文路径是 `/demo1_war_exploded`：
（用 cargo 启动、或在 IDEA 里用 `demo1:war exploded` 部署，都是这个路径）

| 地址 | 对应文件 | 当前是否可用 |
| --- | --- | --- |
| <http://localhost:8080/demo1_war_exploded/> | `webapp/index.html` | 可用（欢迎页） |
| <http://localhost:8080/demo1_war_exploded/hello> | `HelloServlet.java` | 可用 |
| <http://localhost:8080/demo1_war_exploded/lifecycle> | `LifecycleServlet.java` | 需先启用该文件 |
| <http://localhost:8080/demo1_war_exploded/params> | `ParamsServlet.java` | 需先启用该文件 |
| <http://localhost:8080/demo1_war_exploded/forward-redirect> | `ForwardRedirectServlet.java` | 需先启用该文件 |

> 地址里 `/demo1_war_exploded` 这一段叫**上下文路径（context path）**，
> 可以理解成「这个应用在服务器上的门牌号」。它的来源分两种情况：
>
> · 用 cargo 或 IDEA 部署：由配置固定为 `/demo1_war_exploded`
> · 手动把 war 丢进 `webapps`：等于 war 文件名，也就是 `/demo1`
>
> 想改的话改对应配置即可，代码不用动
> （代码里所有跳转都用了 `req.getContextPath()`，不会写死）。

想手动测试带参数的情况，可以直接在地址栏拼参数（需先启用 ParamsServlet）：

```
http://localhost:8080/demo1_war_exploded/params?name=张三&hobby=读书&hobby=游泳
```

---

## 四、目录结构

```
demo1/
├── pom.xml                     Maven 项目描述：依赖、插件、打包方式
├── README.md                   本文件
└── src/
    └── main/
        ├── java/
        │   └── com/example/demo/
        │       ├── HelloServlet.java              最基础的 Servlet（生效）
        │       ├── LifecycleServlet.java          生命周期演示（已注释）
        │       ├── ParamsServlet.java             请求参数与编码（已注释）
        │       └── ForwardRedirectServlet.java    转发 vs 重定向（已注释）
        └── webapp/                                Web 根目录（war 包的内容）
            ├── index.html                         首页（静态资源，可直接访问）
            └── WEB-INF/                           受保护目录，浏览器无法直接访问
                └── web.xml                        部署描述文件
```

### 几个必须记住的约定

| 路径 | 作用 | 能否被浏览器直接访问 |
| --- | --- | --- |
| `src/main/java` | Java 源码（Servlet 等） | 否，是编译产物 |
| `src/main/webapp` | Web 根目录，对应网站的 `/` | 是 |
| `src/main/webapp/WEB-INF` | 配置与受保护资源 | **否**，只能服务端转发访问 |
| `target/` | 编译打包输出目录 | 否，可随时删除重建 |

`WEB-INF` 是安全边界：放在里面的文件浏览器永远访问不到，所以配置文件和内部页面放这里最稳妥。

---

<!--
  ============================================================================
   【进阶内容 · 已注释保留】
   下面第五、六、七节在 Markdown 里被整段注释掉了，不会在页面上显示。
   需要时把本行下面紧跟的注释标记去掉即可恢复。

   ⚠️ 注意：Markdown 注释（就是这种尖括号感叹号形式）内部同样不允许出现连续
      两个减号，所以下面表格的分隔行用的是单个减号，恢复时不要改回去。
  ============================================================================

## 五、常见问题排查

### 1. 启动报错 / 访问 404 / 提示 ClassNotFoundException: jakarta.servlet.*

最常见的原因是用错了 Tomcat 版本。Jakarta EE 9 起包名从 javax 改成了 jakarta：

| Tomcat 版本 | Servlet 规范 | 包名前缀 |
| - | - | - |
| Tomcat 9 及以下 | Servlet 4.0 | javax.servlet |
| Tomcat 10.1 | Servlet 6.0 | jakarta.servlet |
| Tomcat 11.x | Servlet 6.1 | jakarta.servlet |

本项目用的是 jakarta 包名，所以必须用 Tomcat 10 以上。用 Tomcat 9 部署必然失败。

### 2. 页面中文乱码

按顺序检查三处编码，任何一处不一致都会乱码：

1. 项目源码编码是 UTF-8，pom.xml 里已配 project.build.sourceEncoding
2. 请求参数编码，Servlet 里调用 req.setCharacterEncoding("UTF-8")，且必须在取参数之前
3. 响应编码，resp.setContentType("text/html;charset=UTF-8")

另外 HTML 页面里的 meta charset 声明也要写，并且要放在 title 之前。

### 3. 访问 /hello 返回 404

- 确认地址拼写正确，且带上了上下文路径：/demo1_war_exploded/hello
- 确认 web.xml 里 metadata-complete 的值为 false，否则容器不会扫描 WebServlet 注解
- 改完 Java 代码要重新打包并重新部署才会生效

### 4. 想改用 web.xml 注册 Servlet

同一个 URL 不能既用注解注册、又用 web.xml 注册，会冲突。两种方式选一种：

- 用注解：删掉 web.xml 里的 servlet / servlet-mapping 配置（本模板默认）
- 用 web.xml：把 Java 类上的 WebServlet 注解删掉，改用 web.xml 里的写法

若彻底删除 web.xml，pom.xml 里 war 插件的 failOnMissingWebXml 已经设为 false，
所以打包不会因此失败，可以直接删。

### 5. mvn package 报「编码 GBK 的不可映射字符」

说明 Maven 没读到 UTF-8 配置。确认 pom.xml 的属性区里有：

    project.build.sourceEncoding 的值为 UTF-8

并确认源文件本身确实是 UTF-8 编码（用记事本另存为时注意别选成 ANSI）。

***

## 六、建议的学习顺序

1. 先看 index.html 和 web.xml，理解「访问根路径为什么打开这个页面」
2. 读 HelloServlet，搞清 doGet() 的参数 req 和 resp 分别代表什么
3. 启用 LifecycleServlet，反复刷新 /lifecycle，亲眼确认 init 只执行一次、实例只有一个
4. 启用 ParamsServlet，用 /params 分别走 GET 和 POST，观察地址栏差异，再故意制造一次中文乱码并修好它
5. 启用 ForwardRedirectServlet，点击两个按钮对比地址栏变化
6. 动手改造：自己加一个 /time Servlet，返回当前服务器时间

***

## 七、下一步可以加什么

- JSP 与 JSTL：把响应 HTML 从 Java 里搬到页面，实现真正的 MVC
- 过滤器 Filter：统一处理编码、登录校验、请求日志
- 监听器 Listener：应用启动时加载缓存、初始化连接池
- 数据库：加 mysql-connector-j 依赖，用 JDBC 或连接池访问数据
- REST 接口：Jakarta RESTful Web Services 写 JSON 接口

这些都能在现有结构上直接扩展，不需要推翻重来。

  ============================================================================
   上面的进阶内容到此结束
  ============================================================================
-->
