param(
 [string]$SdkPath=$env:ANDROID_SDK_ROOT,
 [string]$JdkPath=$env:JAVA_HOME,
 [string]$PrivatePath=(Join-Path $PSScriptRoot '..\private'),
 [switch]$TestApk
)
$ErrorActionPreference='Stop'
if(!$SdkPath){$SdkPath=$env:ANDROID_HOME}
if(!$SdkPath -or !$JdkPath){throw 'Informe -SdkPath e -JdkPath ou configure ANDROID_SDK_ROOT e JAVA_HOME.'}
$env:JAVA_HOME=$JdkPath
$env:PATH="$JdkPath\bin;$env:PATH"
$bt=Join-Path $SdkPath 'build-tools\36.0.0'
$android=Join-Path $SdkPath 'platforms\android-36\android.jar'
function Run([string]$exe,[string[]]$argv){ & $exe @argv; if($LASTEXITCODE -ne 0){throw "Falha: $exe ($LASTEXITCODE)"} }
Push-Location $PSScriptRoot
try {
 New-Item -ItemType Directory -Force build,build/gen,build/classes,build/dex,$PrivatePath | Out-Null
 Run "$bt\aapt2.exe" @('compile','--dir','res','-o','build/resources.zip')
 Run "$bt\aapt2.exe" @('link','-o','build/resources.apk','-I',$android,'--manifest','AndroidManifest.xml','-A','assets','--java','build/gen','build/resources.zip')
 $sources=@(Get-ChildItem -LiteralPath src,build/gen -Filter '*.java' -Recurse | ForEach-Object FullName)
 Run "$JdkPath\bin\javac.exe" (@('--release','8','-encoding','UTF-8','-classpath',$android,'-d','build/classes')+$sources)
 Run "$JdkPath\bin\jar.exe" @('cf','build/classes.jar','-C','build/classes','.')
 Run "$bt\d8.bat" @('--release','--lib',$android,'--min-api','26','--output','build/dex','build/classes.jar')
 Copy-Item -LiteralPath 'build/resources.apk' -Destination 'build/unsigned.apk' -Force
 Run 'python' @('-c',"import zipfile; z=zipfile.ZipFile('build/unsigned.apk','a',zipfile.ZIP_DEFLATED); z.write('build/dex/classes.dex','classes.dex'); z.close()")
 $passwordFile=Join-Path $PrivatePath 'password.txt'
 if(!(Test-Path -LiteralPath $passwordFile)){
  $bytes=New-Object byte[] 36; [System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
  [IO.File]::WriteAllText($passwordFile,[Convert]::ToBase64String($bytes))
 }
 $env:RUMO_STORE_PASSWORD=[IO.File]::ReadAllText($passwordFile)
 $keystore=Join-Path $PrivatePath 'rumo-release.jks'
 if(!(Test-Path -LiteralPath $keystore)){
  Run "$JdkPath\bin\keytool.exe" @('-genkeypair','-keystore',$keystore,'-alias','rumo','-keyalg','RSA','-keysize','3072','-validity','10000','-dname','CN=Rumo ao TAF, OU=Personal, O=Local, C=BR','-storepass:env','RUMO_STORE_PASSWORD','-keypass:env','RUMO_STORE_PASSWORD','-storetype','PKCS12')
 }
 Run "$bt\zipalign.exe" @('-f','-p','4','build/unsigned.apk','build/aligned.apk')
 Run "$bt\apksigner.bat" @('sign','--ks',$keystore,'--ks-key-alias','rumo','--ks-pass','env:RUMO_STORE_PASSWORD','--out','build/Rumo-1.3.0.apk','build/aligned.apk')
 Run "$bt\apksigner.bat" @('verify','--verbose','build/Rumo-1.3.0.apk')
 if($TestApk){
  New-Item -ItemType Directory -Force build/testclasses,build/testdex | Out-Null
  Run "$bt\aapt2.exe" @('link','-o','build/testresources.apk','-I',$android,'--manifest','tests/AndroidManifest.xml')
  $testSources=@(Get-ChildItem -LiteralPath tests/android -Filter '*.java' -Recurse | ForEach-Object FullName)
  Run "$JdkPath\bin\javac.exe" (@('--release','8','-encoding','UTF-8','-classpath',"$android;build/classes",'-d','build/testclasses')+$testSources)
  Run "$JdkPath\bin\jar.exe" @('cf','build/tests.jar','-C','build/testclasses','.')
  Run "$bt\d8.bat" @('--release','--lib',$android,'--classpath','build/classes.jar','--min-api','26','--output','build/testdex','build/tests.jar')
  Copy-Item -LiteralPath 'build/testresources.apk' -Destination 'build/testunsigned.apk' -Force
  Run 'python' @('-c',"import zipfile; z=zipfile.ZipFile('build/testunsigned.apk','a',zipfile.ZIP_DEFLATED); z.write('build/testdex/classes.dex','classes.dex'); z.close()")
  Run "$bt\zipalign.exe" @('-f','4','build/testunsigned.apk','build/testaligned.apk')
  Run "$bt\apksigner.bat" @('sign','--ks',$keystore,'--ks-key-alias','rumo','--ks-pass','env:RUMO_STORE_PASSWORD','--out','build/tests.apk','build/testaligned.apk')
 }
 Write-Output 'APK assinado: build/Rumo-1.3.0.apk'
} finally {Remove-Item Env:RUMO_STORE_PASSWORD -ErrorAction SilentlyContinue; Pop-Location}
