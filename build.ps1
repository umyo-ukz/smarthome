$ErrorActionPreference = "Stop"
$PSNativeCommandUseErrorActionPreference = $true
Set-Location $PSScriptRoot

foreach ($folder in "out", "dist") {
    if (Test-Path $folder) { Remove-Item $folder -Recurse -Force }
}

$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d out/classes $sources
jar --create --file out/jar/SmartHome.jar --main-class smarthome.Main -C out/classes .
jpackage --type app-image --name SmartHome --input out/jar --main-jar SmartHome.jar --add-modules java.desktop --dest dist

Write-Host "Built dist/SmartHome/SmartHome.exe"
