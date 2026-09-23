<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Jakarta-ee-template-plugin Changelog

## [1.3.2] - 2026-09-23

### Added
- 新建项目向导新增「应用名」输入框，可自定义上下文路径
- 应用名自动跟随项目名预填，支持字母/数字/-/_ 校验

### Fixed
- 修复 HTML/JSP 中文乱码：web.xml 增加 mime-mapping（html/css/js 带 charset=UTF-8）与 jsp-config 全局兜底
- 所有 HTML 模板增加双重编码声明（meta charset + meta http-equiv Content-Type）

## [Unreleased]
