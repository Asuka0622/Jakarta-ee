// script.js —— 简单的交互示例
// 演示了：事件监听、计数器、DOM 操作

// 等待页面加载完成
document.addEventListener('DOMContentLoaded', function () {
    var btn = document.getElementById('clickBtn');
    var countDisplay = document.getElementById('clickCount');
    var count = 0;

    // 点击按钮，计数 +1
    btn.addEventListener('click', function () {
        count++;
        countDisplay.textContent = '点击次数：' + count;

        // 每点 5 次给个小提示
        if (count > 0 && count % 5 === 0) {
            btn.textContent = '太棒了！继续点';
            setTimeout(function () {
                btn.textContent = '点我试试';
            }, 800);
        }
    });

    // 平滑滚动：点击导航链接滚动到对应区域
    var links = document.querySelectorAll('.nav-links a');
    links.forEach(function (link) {
        link.addEventListener('click', function (e) {
            var targetId = this.getAttribute('href');
            if (targetId.startsWith('#')) {
                e.preventDefault();
                var target = document.querySelector(targetId);
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth' });
                }
            }
        });
    });

    // 在控制台输出一下，方便调试
    console.log('✅ 页面加载完成，脚本运行正常！');
});
