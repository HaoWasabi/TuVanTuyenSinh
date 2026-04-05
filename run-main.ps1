$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

# Robust launcher: try known scripts first, then fallback to direct java call.
$runMainJava = Join-Path $projectRoot 'run-main-java.ps1'
$runJava = Join-Path $projectRoot 'run-java.ps1'

if (Test-Path $runMainJava) {
	& $runMainJava @args
	exit $LASTEXITCODE
}

if (Test-Path $runJava) {
	& $runJava @args
	exit $LASTEXITCODE
}

$mainClass = if ($args.Count -gt 0 -and $args[0]) { $args[0] } else { 'com.tuyensinh.Main' }
$argFile = Join-Path $projectRoot '.java-main.args'

if (-not (Test-Path $argFile)) {
	throw "Khong tim thay run-main-java.ps1, run-java.ps1 hoac .java-main.args de chay chuong trinh."
}

Write-Host "Running main class: $mainClass"
java "@$argFile"
exit $LASTEXITCODE
