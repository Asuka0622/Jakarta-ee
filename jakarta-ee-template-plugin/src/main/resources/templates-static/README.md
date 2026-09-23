# 静态 HTML 模板

一个最基础的纯静态网页模板，只有 HTML + CSS + JS，不需要任何服务器端环境。

## 文件结构

```
my-site/
├── index.html          主页面（导航栏 + 英雄区 + 特性卡片 + 页脚）
├── css/
│   └── style.css       全局样式（含响应式）
├── js/
│   └── script.js       交互脚本（点击计数 + 平滑滚动）
└── README.md           本文件
```

## 怎么打开

**方式一：直接打开**
双击 `index.html`，用浏览器打开即可。

**方式二：本地服务器（推荐）**
有些浏览器对本地文件有限制（比如 fetch、字体等），起个本地服务更稳：

```bash
# 方式 A：Python 自带
python -m http.server 8000

# 方式 B：Node.js 的 http-server
npx http-server -p 8000
```

然后访问 `http://localhost:8000`。

## 可以怎么改

- **改文字**：直接编辑 `index.html` 里的标题、段落、按钮文字
- **改颜色**：在 `css/style.css` 顶部或对应模块里找颜色变量/值修改
- **加页面**：复制 `index.html` 改个名字，比如 `about.html`，然后在导航栏加链接
- **加图片**：新建 `images/` 文件夹，把图片放进去，用 `<img src="images/xxx.png">` 引用

## 技术点

- HTML5 语义化标签（`nav` / `section` / `footer` 等）
- CSS Grid + Flexbox 布局
- 原生 JavaScript（无任何框架依赖）
- 响应式设计（手机 / 平板 / 桌面自适应）
