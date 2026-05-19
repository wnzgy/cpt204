$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

chcp 65001 | Out-Null
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

New-Item -ItemType Directory -Force -Path "bin" | Out-Null

$javaFiles = Get-ChildItem -Path "src" -Recurse -File -Filter "*.java" | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d "bin" $javaFiles
java -cp "bin" cpt204.app.UrbanInspectionApp
