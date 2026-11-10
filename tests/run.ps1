param([string]$JdkPath=$env:JAVA_HOME)
$ErrorActionPreference='Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
 New-Item -ItemType Directory -Force build/core-tests | Out-Null
 & "$JdkPath\bin\javac.exe" -encoding UTF-8 -d build/core-tests src/br/com/saulo/rumoaotaf/Core.java tests/CoreTest.java
 if($LASTEXITCODE -ne 0){throw 'Falha ao compilar testes de dominio.'}
 & "$JdkPath\bin\java.exe" -cp build/core-tests CoreTest
 if($LASTEXITCODE -ne 0){throw 'Falha em testes de dominio.'}
 & "$JdkPath\bin\javac.exe" -encoding UTF-8 -d build/core-tests src/br/com/saulo/rumoaotaf/Planner.java tests/PlannerTest.java
 if($LASTEXITCODE -ne 0){throw 'Falha ao compilar o motor.'}
 & "$JdkPath\bin\java.exe" -cp build/core-tests PlannerTest
 if($LASTEXITCODE -ne 0){throw 'Falha no motor.'}
 & node --test tests/data.test.cjs
 if($LASTEXITCODE -ne 0){throw 'Falha no conteudo.'}
} finally {Pop-Location}
