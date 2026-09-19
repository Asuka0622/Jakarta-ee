#Requires -Version 5.1
<#
.SYNOPSIS
  一键安装并配置 JDK 17 + Apache Maven + Apache Tomcat（Windows）
.DESCRIPTION
  - JDK 17：优先使用已安装的 JAVA_HOME；其次解压同目录下的 jdk*.zip；最后从 Adoptium 下载
  - Maven / Tomcat：解压同目录下的 apache-maven-*.zip / apache-tomcat-*.zip
  - 自动设置 JAVA_HOME / MAVEN_HOME / CATALINA_HOME 并追加到用户 PATH（去重）
#>

$ErrorActionPreference = "Stop"

# ===== 可配置项 =====
$InstallRoot = if ($args.Count -ge 1) { $args[0] } else { "D:\dev-tools" }
$JdkVersion  = "17"
# ===================

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

function Write-Step($msg)  { Write-Host "`n[STEP] $msg" -ForegroundColor Cyan }
function Write-Info($msg)  { Write-Host "  [INFO] $msg" -ForegroundColor Gray }
function Write-Ok($msg)    { Write-Host "  [ OK ] $msg" -ForegroundColor Green }
function Write-Warn($msg)  { Write-Host "  [WARN] $msg" -ForegroundColor Yellow }
function Write-Err($msg)   { Write-Host "  [FAIL] $msg" -ForegroundColor Red }

function Test-Jdk17($path) {
    if (-not $path) { return $false }
    $exe = Join-Path $path "bin\java.exe"
    if (-not (Test-Path $exe)) { return $false }
    $oldEAP = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    try {
        $out = & $exe -version 2>&1 | Out-String
    } finally {
        $ErrorActionPreference = $oldEAP
    }
    return ($out -match 'version "17\.')
}

# 安全执行 native 命令（用 Start-Process 捕获 stdout/stderr，避免 ErrorRecord 前缀）
function Invoke-Native($cmd, $argList) {
    $tmpOut = [System.IO.Path]::GetTempFileName()
    $tmpErr = [System.IO.Path]::GetTempFileName()
    try {
        Start-Process -FilePath $cmd -ArgumentList $argList -NoNewWindow -Wait `
            -RedirectStandardOutput $tmpOut -RedirectStandardError $tmpErr | Out-Null
        $out = if (Test-Path $tmpOut) { Get-Content $tmpOut -Raw -ErrorAction SilentlyContinue } else { "" }
        $err = if (Test-Path $tmpErr) { Get-Content $tmpErr -Raw -ErrorAction SilentlyContinue } else { "" }
        return "$out$err"
    } finally {
        Remove-Item $tmpOut, $tmpErr -Force -ErrorAction SilentlyContinue
    }
}

# 取 native 命令输出的第一行（自动处理编码）
function Get-NativeFirstLine($cmd, $argList) {
    $raw = Invoke-Native $cmd $argList
    $lines = $raw -split "`r?`n" | Where-Object { $_.Trim() -ne "" }
    if ($lines) { return $lines[0].Trim() }
    return ""
}

# 把 IDEA 的默认浏览器设为「本机默认浏览器」
# 原理：IDEA 的该设置保存在 <配置目录>\options\web-browsers.xml，
#       组件 WebBrowsersConfiguration 的 default 属性取值为
#       system / first / alternative（分别对应 系统默认 / 列表第一个 / 自定义路径）
function Set-IdeaDefaultBrowser($root) {
    if (-not (Test-Path $root)) {
        Write-Warn "未找到 JetBrains 配置目录，跳过 IDEA 浏览器设置"
        return $false
    }

    $dirs = @(Get-ChildItem $root -Directory -ErrorAction SilentlyContinue |
              Where-Object { $_.Name -like "IntelliJIdea*" -or $_.Name -like "IdeaIC*" })
    if ($dirs.Count -eq 0) {
        Write-Warn "未找到 IDEA 配置目录（IntelliJIdea*），跳过 IDEA 浏览器设置"
        return $false
    }

    # IDEA 运行时会把内存中的配置回写，此时改文件会被覆盖，必须等它退出
    if (@(Get-Process -Name "idea64", "idea" -ErrorAction SilentlyContinue).Count -gt 0) {
        Write-Warn "检测到 IDEA 正在运行，已跳过（IDEA 退出时会覆盖该文件）"
        Write-Warn "关闭 IDEA 后重新运行本脚本即可完成设置"
        return $false
    }

    $done = 0
    foreach ($d in $dirs) {
        $optDir = Join-Path $d.FullName "options"
        if (-not (Test-Path $optDir)) { continue }   # 该版本 IDEA 还没初始化过，跳过

        $file = Join-Path $optDir "web-browsers.xml"
        # 单个配置目录失败（权限/占用等）不应中断整脚本，跳过并继续
        try {
            $doc = New-Object System.Xml.XmlDocument
            if (Test-Path $file) {
                $doc.Load($file)
            }
            else {
                $doc.AppendChild($doc.CreateElement("application")) | Out-Null
            }

            $docRoot = $doc.DocumentElement
            if ($null -eq $docRoot -or $docRoot.Name -ne "application") {
                Write-Warn "结构异常，已跳过：$file"
                continue
            }

            $comp = $docRoot.SelectSingleNode("component[@name='WebBrowsersConfiguration']")
            if ($null -eq $comp) {
                $comp = $doc.CreateElement("component")
                $comp.SetAttribute("name", "WebBrowsersConfiguration")
                $docRoot.AppendChild($comp) | Out-Null
            }

            # system = 使用本机（系统）默认浏览器；只改这一项，其余浏览器列表原样保留
            $comp.SetAttribute("default", "system")

            # 与 IDEA 自身写出的格式保持一致：无 BOM、LF 换行、无 XML 声明
            $settings = New-Object System.Xml.XmlWriterSettings
            $settings.Indent = $true
            $settings.IndentChars = "  "
            $settings.OmitXmlDeclaration = $true
            $settings.NewLineChars = "`n"
            $settings.Encoding = New-Object System.Text.UTF8Encoding($false)

            $w = [System.Xml.XmlWriter]::Create($file, $settings)
            try { $doc.Save($w) } finally { $w.Close() }

            Write-Ok "已设置 $($d.Name)：默认浏览器 = 本机默认浏览器"
            $done++
        }
        catch {
            Write-Warn "写入失败，已跳过：$file"
            Write-Warn "  $($_.Exception.Message)"
        }
    }

    if ($done -eq 0) { Write-Warn "没有可更新的 IDEA 配置文件，跳过" }
    return ($done -gt 0)
}

# 在安装目录里查找已解压好的版本目录（重复运行时直接复用，
# 避免 Expand-Archive -Force 覆盖被占用的文件而失败）
function Find-InstalledDir($root, $pattern, $marker) {
    if (-not (Test-Path $root)) { return $null }
    $d = Get-ChildItem $root -Directory -Filter $pattern -ErrorAction SilentlyContinue |
         Where-Object { Test-Path (Join-Path $_.FullName $marker) } |
         Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if ($d) { return $d.FullName }
    return $null
}

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  JDK 17 + Maven + Tomcat 一键安装配置脚本" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan

# 创建安装根目录
if (-not (Test-Path $InstallRoot)) {
    New-Item -ItemType Directory -Path $InstallRoot -Force | Out-Null
    Write-Info "创建安装目录 $InstallRoot"
}

# ============================================================
# 1. JDK 17
# ============================================================
Write-Step "1/3  检查 / 安装 JDK $JdkVersion"

$jdkHome = $env:JAVA_HOME
$jdkInstalled = $false

# 1) 已设置 JAVA_HOME 且是 JDK 17
if (Test-Jdk17 $jdkHome) {
    Write-Ok "检测到现有 JDK 17：$jdkHome"
    $jdkInstalled = $true
}
else {
    if ($jdkHome) { Write-Warn "现有 JAVA_HOME 不是 JDK 17 或无效：$jdkHome" }

    # 2) 安装目录里已有解压好的 JDK 17
    $existingJdk = Find-InstalledDir $InstallRoot "jdk*" "bin\java.exe"
    if ($existingJdk -and (Test-Jdk17 $existingJdk)) {
        $jdkHome = $existingJdk
        Write-Ok "复用已安装的 JDK 17：$jdkHome"
        $jdkInstalled = $true
    }

    # 3) 同目录下有 jdk*.zip
    $jdkZip = Get-ChildItem -Path $ScriptDir -Filter "jdk*.zip" -ErrorAction SilentlyContinue |
              Sort-Object Length -Descending | Select-Object -First 1
    if (-not $jdkInstalled -and $jdkZip) {
        Write-Info "找到本地 JDK 压缩包：$($jdkZip.Name)"
        Expand-Archive -Path $jdkZip.FullName -DestinationPath $InstallRoot -Force
        $jdkDir = Get-ChildItem -Path $InstallRoot -Directory -Filter "jdk*" |
                  Sort-Object LastWriteTime -Descending | Select-Object -First 1
        $jdkHome = $jdkDir.FullName
        if (Test-Jdk17 $jdkHome) {
            Write-Ok "JDK 17 解压完成：$jdkHome"
            $jdkInstalled = $true
        }
    }

    # 4) 从 Adoptium 下载
    if (-not $jdkInstalled) {
        $url = "https://api.adoptium.net/v3/binary/latest/$JdkVersion/ga/windows/x64/jdk/hotspot/normal/eclipse"
        $tmpZip = Join-Path $InstallRoot "jdk${JdkVersion}-download.zip"
        Write-Info "正在从 Adoptium 下载 JDK $JdkVersion ..."
        try {
            Invoke-WebRequest -Uri $url -OutFile $tmpZip -UseBasicParsing
            if ((Get-Item $tmpZip).Length -lt 10MB) { throw "下载文件过小，可能失败" }
            Expand-Archive -Path $tmpZip -DestinationPath $InstallRoot -Force
            Remove-Item $tmpZip -Force -ErrorAction SilentlyContinue
            $jdkDir = Get-ChildItem -Path $InstallRoot -Directory -Filter "jdk*" |
                      Sort-Object LastWriteTime -Descending | Select-Object -First 1
            $jdkHome = $jdkDir.FullName
            if (Test-Jdk17 $jdkHome) {
                Write-Ok "JDK 17 下载并解压完成：$jdkHome"
                $jdkInstalled = $true
            }
        }
        catch {
            Write-Err "JDK 下载失败：$($_.Exception.Message)"
            Write-Warn "请手动下载 JDK 17 zip 放到脚本目录后重新运行"
        }
    }
}

if (-not $jdkInstalled) {
    Write-Err "无法获得可用的 JDK 17，终止脚本"
    exit 1
}

# ============================================================
# 2. Maven
# ============================================================
Write-Step "2/3  安装 Apache Maven"

$mavenHome = Find-InstalledDir $InstallRoot "apache-maven-*" "bin\mvn.cmd"
if ($mavenHome) {
    Write-Ok "复用已安装的 Maven：$mavenHome"
}
else {
    $mavenZip = Get-ChildItem -Path $ScriptDir -Filter "apache-maven-*.zip" -ErrorAction SilentlyContinue |
                Sort-Object Length -Descending | Select-Object -First 1
    if (-not $mavenZip) {
        Write-Err "未找到 apache-maven-*.zip，请将 Maven 压缩包放到脚本目录：$ScriptDir"
        exit 1
    }
    Write-Info "解压 $($mavenZip.Name) ..."
    Expand-Archive -Path $mavenZip.FullName -DestinationPath $InstallRoot -Force
    $mavenHome = Find-InstalledDir $InstallRoot "apache-maven-*" "bin\mvn.cmd"
    if ($mavenHome) {
        Write-Ok "Maven 安装完成：$mavenHome"
    }
    else {
        Write-Err "Maven 解压后未找到 bin\mvn.cmd"
        exit 1
    }
}

# ============================================================
# 3. Tomcat
# ============================================================
Write-Step "3/3  安装 Apache Tomcat"

$catalinaHome = Find-InstalledDir $InstallRoot "apache-tomcat-*" "bin\catalina.bat"
if ($catalinaHome) {
    Write-Ok "复用已安装的 Tomcat：$catalinaHome"
}
else {
    $tomcatZip = Get-ChildItem -Path $ScriptDir -Filter "apache-tomcat-*.zip" -ErrorAction SilentlyContinue |
                 Sort-Object Length -Descending | Select-Object -First 1
    if (-not $tomcatZip) {
        Write-Err "未找到 apache-tomcat-*.zip，请将 Tomcat 压缩包放到脚本目录：$ScriptDir"
        exit 1
    }
    Write-Info "解压 $($tomcatZip.Name) ..."
    Expand-Archive -Path $tomcatZip.FullName -DestinationPath $InstallRoot -Force
    $catalinaHome = Find-InstalledDir $InstallRoot "apache-tomcat-*" "bin\catalina.bat"
    if ($catalinaHome) {
        Write-Ok "Tomcat 安装完成：$catalinaHome"
    }
    else {
        Write-Err "Tomcat 解压后未找到 bin\catalina.bat"
        exit 1
    }
}

# ============================================================
# 4. 配置环境变量（用户级，无需管理员）
# ============================================================
Write-Step "配置环境变量"

[Environment]::SetEnvironmentVariable("JAVA_HOME",     $jdkHome,      "User")
[Environment]::SetEnvironmentVariable("MAVEN_HOME",    $mavenHome,    "User")
[Environment]::SetEnvironmentVariable("CATALINA_HOME", $catalinaHome, "User")
Write-Ok "JAVA_HOME     = $jdkHome"
Write-Ok "MAVEN_HOME    = $mavenHome"
Write-Ok "CATALINA_HOME = $catalinaHome"

# 更新用户 PATH（去重追加）
$userPath = [Environment]::GetEnvironmentVariable("Path", "User")
$entries  = @()
if ($userPath) { $entries = $userPath -split ";" | Where-Object { $_ -and $_.Trim() -ne "" } }

$toAdd = @("%JAVA_HOME%\bin", "%MAVEN_HOME%\bin", "%CATALINA_HOME%\bin")
$changed = $false
foreach ($e in $toAdd) {
    if ($entries -notcontains $e) {
        $entries += $e
        $changed = $true
        Write-Info "PATH 追加：$e"
    }
}
if ($changed) {
    [Environment]::SetEnvironmentVariable("Path", ($entries -join ";"), "User")
    Write-Ok "用户 PATH 已更新"
}
else {
    Write-Info "PATH 已包含所需条目，无需修改"
}

# ============================================================
# 5. 把 IDEA 默认浏览器设为本机默认浏览器
# ============================================================
Write-Step "配置 IDEA 默认浏览器"
$ideaBrowserSet = Set-IdeaDefaultBrowser (Join-Path $env:APPDATA "JetBrains")

# ============================================================
# 6. 当前会话临时生效 + 验证
# ============================================================
Write-Step "验证安装"

$env:JAVA_HOME     = $jdkHome
$env:MAVEN_HOME    = $mavenHome
$env:CATALINA_HOME = $catalinaHome
$env:Path = "$jdkHome\bin;$mavenHome\bin;$catalinaHome\bin;$env:Path"

Write-Host ""
Write-Host "  Java  ：" -NoNewline -ForegroundColor Cyan
Get-NativeFirstLine "java" @("-version")

Write-Host "  Maven ：" -NoNewline -ForegroundColor Cyan
Get-NativeFirstLine "mvn" @("-version")

Write-Host "  Tomcat：" -NoNewline -ForegroundColor Cyan
if (Test-Path "$catalinaHome\bin\catalina.bat") {
    $verFile = Get-Content "$catalinaHome\RELEASE-NOTES" -ErrorAction SilentlyContinue | Select-Object -First 1
    Write-Host "OK  ($catalinaHome)" -ForegroundColor Green
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  全部完成！" -ForegroundColor Green
Write-Host "  环境变量已写入用户级，新开命令行窗口自动生效" -ForegroundColor Yellow
Write-Host "  当前窗口已临时生效，可直接使用 java / mvn" -ForegroundColor Yellow
if ($ideaBrowserSet) {
    Write-Host "  IDEA 默认浏览器已设为「系统默认浏览器」" -ForegroundColor Yellow
}
else {
    Write-Host "  IDEA 默认浏览器未修改（关闭 IDEA 后重跑本脚本即可）" -ForegroundColor Yellow
}
Write-Host "============================================" -ForegroundColor Cyan
