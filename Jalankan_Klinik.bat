@echo off
setlocal EnableExtensions DisableDelayedExpansion
title Klinik SehatKita

rem ============================================================
rem  Jalankan_Klinik.bat
rem  Compile otomatis + menjalankan aplikasi Klinik SehatKita.
rem  Letakkan file ini di folder utama project (sejajar dengan
rem  folder "src" dan "out"). Cukup klik dua kali.
rem ============================================================

rem --- Pindah ke folder tempat file BAT ini berada (aman untuk spasi / dipindah)
pushd "%~dp0" >nul 2>&1
if errorlevel 1 goto :err_folder

if not exist "src\klinik\MainApp.java" goto :err_src

rem --- Cari Java: utamakan JAVA_HOME, kalau tidak ada pakai PATH
set "JAVA_EXE="
set "JAVAC_EXE="
if not defined JAVA_HOME goto :cari_path
set "JH=%JAVA_HOME:"=%"
if not exist "%JH%\bin\javac.exe" goto :cari_path
if not exist "%JH%\bin\java.exe" goto :cari_path
set "JAVAC_EXE=%JH%\bin\javac.exe"
set "JAVA_EXE=%JH%\bin\java.exe"
goto :java_ketemu

:cari_path
where javac >nul 2>&1
if errorlevel 1 goto :err_java
where java >nul 2>&1
if errorlevel 1 goto :err_java
set "JAVAC_EXE=javac"
set "JAVA_EXE=java"

:java_ketemu
if not defined TEMP set "TEMP=%~dp0"
set "ARGFILE=%TEMP%\klinik_sources_%RANDOM%%RANDOM%.txt"
set "LOGFILE=%TEMP%\klinik_compile_%RANDOM%%RANDOM%.log"
if exist "%ARGFILE%" del "%ARGFILE%" >nul 2>&1

rem --- Kumpulkan semua file .java di src\klinik ke daftar (path dengan spasi aman)
setlocal EnableDelayedExpansion
for %%F in ("src\klinik\*.java") do (
    set "P=%%F"
    echo "!P:\=/!">>"%ARGFILE%"
)
endlocal

if not exist "out" mkdir "out"

echo ============================================
echo   Klinik SehatKita
echo ============================================
echo [1/2] Mengompilasi source code...
"%JAVAC_EXE%" -encoding UTF-8 -d "out" @"%ARGFILE%" >"%LOGFILE%" 2>&1
if errorlevel 1 goto :err_compile

del "%ARGFILE%" >nul 2>&1
del "%LOGFILE%" >nul 2>&1

echo [2/2] Menjalankan aplikasi...
echo.
echo Catatan: jangan tutup jendela hitam ini selama aplikasi dipakai.
echo.
"%JAVA_EXE%" -ea -cp "out" klinik.MainApp
set "KODE=%ERRORLEVEL%"
if not "%KODE%"=="0" goto :err_run
goto :selesai

:err_folder
echo.
echo [ERROR] Tidak bisa membuka folder tempat file BAT ini berada.
echo Pindahkan folder project ke lokasi lokal (misalnya Documents), lalu coba lagi.
goto :akhir_error

:err_src
echo.
echo [ERROR] File "src\klinik\MainApp.java" tidak ditemukan.
echo Pastikan Jalankan_Klinik.bat berada di folder utama project,
echo yaitu folder yang berisi folder "src".
echo Lokasi saat ini: %CD%
goto :akhir_error

:err_java
echo.
echo [ERROR] JDK (Java Development Kit) belum terpasang atau belum terdaftar di PATH.
echo.
where java >nul 2>&1
if not errorlevel 1 echo Java terdeteksi, tetapi hanya JRE (tanpa compiler "javac"). Aplikasi ini perlu JDK.
echo.
echo Cara memperbaiki:
echo   1. Unduh dan pasang JDK 17 atau lebih baru (disarankan JDK 21), misalnya dari
echo      https://adoptium.net
echo   2. Saat instalasi, aktifkan opsi "Add to PATH" dan/atau "Set JAVA_HOME".
echo   3. Tutup jendela ini, lalu klik dua kali Jalankan_Klinik.bat lagi.
goto :akhir_error

:err_compile
echo.
echo [ERROR] Proses compile gagal. Detail kesalahan:
echo --------------------------------------------
type "%LOGFILE%"
echo --------------------------------------------
echo Periksa pesan di atas (nama file dan nomor baris) untuk mencari penyebabnya.
del "%ARGFILE%" >nul 2>&1
del "%LOGFILE%" >nul 2>&1
goto :akhir_error

:err_run
echo.
echo [ERROR] Aplikasi berhenti dengan kode %KODE%.
echo Pesan error Java (jika ada) tampil di atas ini.
goto :akhir_error

:akhir_error
echo.
pause
popd >nul 2>&1
endlocal
exit /b 1

:selesai
popd >nul 2>&1
endlocal
exit /b 0