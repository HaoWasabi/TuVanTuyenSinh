$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

$mainClass = if ($args.Count -gt 0 -and $args[0]) { $args[0] } else { 'com.tuyensinh.Main' }

Write-Host "Running main class: $mainClass"

# Tai su dung script run chinh da co san
& "$projectRoot\run-main-java.ps1" $mainClass
