@echo off
chcp 65001 >nul
title JDK17 + Maven + Tomcat 一键安装

echo ============================================
echo   JDK 17 + Maven + Tomcat 一键安装配置
echo ============================================
echo.
echo   随包内置（全离线，无需联网）：
echo     jdk-17.0.14_windows-x64_bin.zip          免安装版 JDK 17
echo     apache-maven-3.8.8-bin.zip               免安装版 Maven 3.8.8
echo     apache-tomcat-11.0.25-windows-x64.zip    免安装版 Tomcat 11.0.25
echo     apache-tomcat-11.0.25.exe                Tomcat 官方安装器（可选，需管理员）
echo.
echo   默认安装目录：D:\dev-tools
echo   自定义目录用法：install-dev-tools.bat  D:\my\tools
echo.
echo   同时会把 IDEA 默认浏览器设为电脑的系统默认浏览器
echo   （需先关闭 IDEA，否则 IDEA 退出时会覆盖该设置）
echo.

REM 把第一个参数（安装目录）透传给 PowerShell 脚本
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0install-dev-tools.ps1" %1

echo.
pause
