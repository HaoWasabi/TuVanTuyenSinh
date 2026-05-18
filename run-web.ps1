$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

# Chỉ định chuẩn xác class chứa Embedded Tomcat của bạn
$mainClass = 'com.tuyensinh.SpringWebMain'

Write-Host "Starting Embedded Tomcat Server..." -ForegroundColor Cyan
Write-Host "Running main class: $mainClass"

# Cấu hình tối ưu hệ thống Web
$webArgs = @(
    "-Dspring.thymeleaf.cache=false",               # Tắt cache Thymeleaf
    "-Dfile.encoding=UTF-8",                        # Khắc phục lỗi font Tiếng Việt
    "-Djava.awt.headless=true"                      # Khóa hoàn toàn giao diện Desktop UI
)

# --- BƯỚC VÁ LỖI HIBERNATE & JAXB SONG HÀNH ---
$fixDir = Join-Path $projectRoot "target\dependency"
if (-not (Test-Path $fixDir)) { New-Item -ItemType Directory -Force $fixDir | Out-Null }

# Đã sửa lại đường dẫn chuẩn xác và phiên bản JAXB (Jakarta 4.0.0+)
$dependencies = @{
    "jakarta.xml.bind-api-4.0.0.jar" = "https://repo1.maven.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api/4.0.0/jakarta.xml.bind-api-4.0.0.jar";
    "jaxb-runtime-4.0.0.jar"         = "https://repo1.maven.org/maven2/org/glassfish/jaxb/jaxb-runtime/4.0.0/jaxb-runtime-4.0.0.jar";
    "jaxb-core-4.0.0.jar"            = "https://repo1.maven.org/maven2/org/glassfish/jaxb/jaxb-core/4.0.0/jaxb-core-4.0.0.jar";
    "txw2-4.0.0.jar"                 = "https://repo1.maven.org/maven2/org/glassfish/jaxb/txw2/4.0.0/txw2-4.0.0.jar";
    "istack-commons-runtime-4.1.1.jar" = "https://repo1.maven.org/maven2/com/sun/istack/istack-commons-runtime/4.1.1/istack-commons-runtime-4.1.1.jar";
    "antlr4-runtime-4.13.0.jar"      = "https://repo1.maven.org/maven2/org/antlr/antlr4-runtime/4.13.0/antlr4-runtime-4.13.0.jar"
}

foreach ($fileName in $dependencies.Keys) {
    $targetPath = Join-Path $fixDir $fileName
    if (-not (Test-Path $targetPath)) {
        Write-Host "Downloading missing core runtime: $fileName..." -ForegroundColor Yellow
        Invoke-WebRequest -Uri $dependencies[$fileName] -OutFile $targetPath
    }
}

# 1. Kiểm tra script tùy chỉnh nếu có
$runMainJava = Join-Path $projectRoot 'run-main-java.ps1'
if (Test-Path $runMainJava) {
    & $runMainJava $mainClass
    exit $LASTEXITCODE
}

# 2. Xây dựng danh sách Classpath thông minh
$repo = Join-Path $env:USERPROFILE '.m2\repository'
$cpItems = New-Object 'System.Collections.Generic.List[string]'

# Luôn ưu tiên code đã compile của dự án hiện tại lên đầu
if (Test-Path 'target/classes') { $cpItems.Add((Resolve-Path 'target/classes').Path) }
if (Test-Path 'src/main/resources') { $cpItems.Add((Resolve-Path 'src/main/resources').Path) }

# Nạp toàn bộ các file JAR cứu trợ vừa tải vào Classpath
Get-ChildItem $fixDir -Filter "*.jar" | ForEach-Object {
    $cpItems.Add((Resolve-Path $_.FullName).Path)
}

# Quét và thanh lọc thư viện JAR từ kho .m2
if (Test-Path $repo) {
    Write-Host "Scanning .m2 libraries and filtering out conflicting modules..." -ForegroundColor Yellow
    
    # Danh sách đen: Loại bỏ các gói log cũ gây sập hệ thống xung đột với Tomcat nhúng
    $blackListPatterns = @('slf4j-jdk14', 'slf4j-log4j12', 'log4j-1\.2')

    $allJars = Get-ChildItem $repo -Recurse -Filter '*.jar' |
        Where-Object { $_.Name -notmatch '(-sources|-javadoc|tests?)\.jar$' }

    $cleanJars = $allJars | Where-Object {
        $isBlocked = $false
        foreach ($pattern in $blackListPatterns) {
            if ($_.Name -match $pattern) { $isBlocked = $true; break }
        }
        -not $isBlocked
    }

    # Gom nhóm lọc trùng phiên bản (Giữ lại file có ngày sửa đổi mới nhất từ kho .m2)
    $filteredJars = $cleanJars | Group-Object {
        if ($_.Name -match '^([a-zA-Z0-9\.\-_]+?)-(\d+\.)') { $Matches[1] } else { $_.Name }
    } | ForEach-Object {
        $_.Group | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    }

    foreach ($jar in $filteredJars) { $cpItems.Add($jar.FullName) }
}

$cp = [string]::Join(';', $cpItems)
$runArgsFile = Join-Path $projectRoot '.java-run.args'

# Ghi cấu hình tham số vào file args
$argsContent = @()
$argsContent += $webArgs
$argsContent += "-cp"
$argsContent += $cp
$argsContent += $mainClass

$argsContent | Set-Content -Path $runArgsFile -Encoding ASCII

# Khởi động Server
Write-Host "Launching Java Application..." -ForegroundColor Green
java "@$runArgsFile"
exit $LASTEXITCODE