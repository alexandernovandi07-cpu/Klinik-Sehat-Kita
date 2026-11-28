@echo off
setlocal EnableExtensions DisableDelayedExpansion
title Klinik SehatKita

rem ============================================================
rem  Aplikasi_KlinikSehat.bat  (versi Windows)
rem  Compile otomatis + menjalankan aplikasi Klinik SehatKita.
rem ============================================================
set "JEDA=200"

rem --- Pindah ke folder tempat file BAT ini berada (aman untuk spasi / dipindah)
pushd "%~dp0" >nul 2>&1
if errorlevel 1 goto :err_folder

if not exist "src\klinik\MainApp.java" goto :err_src

echo.
call :ketik "      [ + ]   KLINIK SEHATKITA"
call :ketik "      Sistem Antrean dan Administrasi Klinik"
echo.
call :ketik "Halo, selamat datang! Bentar ya, dokter lagi cuci tangan dulu..."
call :ketik "Resepsionis lagi nyiapin map pasien..."
call :ketik "Mengecek apakah komputer ini punya Java (JDK)..."

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
call :ketik "Java ketemu! Aman, dokternya udah standby."
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

echo.
call :ketik "[1/2] Lagi meracik kode jadi obat yang siap diminum (compile)..."
call :ketik "      Mohon jangan ditinggal tidur, ini cuma sebentar kok."
"%JAVAC_EXE%" -encoding UTF-8 -d "out" @"%ARGFILE%" >"%LOGFILE%" 2>&1
if errorlevel 1 goto :err_compile

del "%ARGFILE%" >nul 2>&1
del "%LOGFILE%" >nul 2>&1

call :ketik "      Racikan selesai tanpa efek samping."
echo.
call :ketik "[2/2] Membuka pintu klinik... silakan antre yang rapi ya!"
call :ketik "      Psst, jendela hitam ini jangan ditutup dulu."
call :ketik "      Kalau ditutup, aplikasinya ikut kabur lewat pintu belakang."
echo.
"%JAVA_EXE%" -ea -cp "out" klinik.MainApp
set "KODE=%ERRORLEVEL%"
if not "%KODE%"=="0" goto :err_run
echo.
call :ketik "Kliniknya sudah tutup. Makasih udah mampir, semoga sehat selalu!"
goto :selesai

:err_folder
echo.
call :ketik "[ERROR] Waduh, folder ini gak bisa dibuka."
call :ketik "Coba pindahin project ke lokasi lokal (misalnya Documents),"
call :ketik "terus klik dua kali lagi ya."
goto :akhir_error

:err_src
echo.
call :ketik "[ERROR] Hmm, file src\klinik\MainApp.java gak ketemu nih."
call :ketik "Kayaknya file BAT ini nyasar, kayak pasien salah poli."
call :ketik "Taruh di folder utama project (yang isinya ada folder src) ya."
echo Sekarang posisinya di: %CD%
goto :akhir_error

:err_java
echo.
call :ketik "[ERROR] Yah, JDK-nya belum ada nih. Dokternya belum datang."
call :ketik "Komputer ini belum punya Java (JDK), atau belum didaftarin ke PATH."
echo.
where java >nul 2>&1
if not errorlevel 1 echo Java-nya sih ada, tapi cuma JRE (belum ada javac). Kita butuhnya JDK.
echo.
call :ketik "Gampang kok, ikutin resep ini:"
call :ketik "  1. Unduh dan pasang JDK 17 atau yang lebih baru (JDK 21 paling oke)"
call :ketik "     misalnya di https://adoptium.net"
call :ketik "  2. Pas instalasi, centang Add to PATH dan/atau Set JAVA_HOME."
call :ketik "  3. Tutup jendela ini, terus klik dua kali file ini lagi."
goto :akhir_error

:err_compile
echo.
call :ketik "[ERROR] Aduh, racikannya gagal (kode gagal dikompilasi). Kata javac begini:"
echo --------------------------------------------
type "%LOGFILE%"
echo --------------------------------------------
call :ketik "Cek nama file dan nomor barisnya ya. Biasanya biang masalahnya ada di situ."
del "%ARGFILE%" >nul 2>&1
del "%LOGFILE%" >nul 2>&1
goto :akhir_error

:err_run
echo.
call :ketik "[ERROR] Yah, aplikasinya tiba-tiba pingsan (kode %KODE%)."
call :ketik "Cek pesan error Java di atas ini, mungkin ada petunjuknya."
goto :akhir_error

:akhir_error
echo.
echo Tekan tombol apa aja buat nutup jendela ini...
pause >nul
popd >nul 2>&1
endlocal
exit /b 1

:selesai
popd >nul 2>&1
endlocal
exit /b 0

rem --- Menampilkan satu baris tulisan, lalu jeda sebentar biar muncul pelan-pelan
:ketik
echo(%~1
if "%JEDA%"=="0" goto :eof
powershell -NoProfile -Command "Start-Sleep -Milliseconds %JEDA%" >nul 2>&1
goto :eof
