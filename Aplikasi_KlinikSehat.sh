#!/usr/bin/env bash
# ============================================================
#  Aplikasi_KlinikSehat.sh  (Linux) 
#  Compile otomatis + menjalankan aplikasi Klinik SehatKita.
# ============================================================
JEDA=0.3

# Pindah ke folder tempat file ini berada (aman untuk spasi / dipindah)
cd "$(dirname "${BASH_SOURCE[0]:-$0}")" || exit 1

OS="$(uname -s)"
[ "$OS" = "Darwin" ] && NAMA_OS="macOS" || NAMA_OS="Linux"

ketik() {
  echo "$1"
  if [ "$JEDA" != "0" ]; then sleep "$JEDA"; fi
}

# Kalau dijalankan dengan klik dua kali tanpa terminal (umum di Linux),
# error ditampilkan lewat kotak dialog supaya tetap kelihatan.
dialog() {
  [ -t 1 ] && return 0
  if command -v zenity >/dev/null 2>&1; then
    zenity --error --title="Klinik SehatKita" --text="$1" >/dev/null 2>&1
  elif command -v kdialog >/dev/null 2>&1; then
    kdialog --error "$1" --title "Klinik SehatKita" >/dev/null 2>&1
  elif command -v xmessage >/dev/null 2>&1; then
    xmessage -center "$1" >/dev/null 2>&1
  fi
  return 0
}

gagal() {
  dialog "$1"
  echo
  if [ -t 0 ]; then read -r -p "Tekan Enter buat nutup jendela ini..." _; fi
  rm -f "$LOGFILE" 2>/dev/null
  exit 1
}

echo
ketik "      +-----+"
ketik "      |  +  |   KLINIK SEHATKITA"
ketik "      +-----+   Sistem Antrean dan Administrasi Klinik"
echo
ketik "Halo, selamat datang di $NAMA_OS! Bentar ya, dokter lagi cuci tangan dulu..."
ketik "Resepsionis lagi nyiapin map pasien..."

if [ ! -f "src/klinik/MainApp.java" ]; then
  echo
  ketik "[ERROR] Hmm, file src/klinik/MainApp.java gak ketemu nih."
  ketik "Kayaknya file ini nyasar, kayak pasien salah poli."
  ketik "Taruh di folder utama project (yang isinya ada folder src) ya."
  echo "Sekarang posisinya di: $(pwd)"
  gagal "File src/klinik/MainApp.java tidak ditemukan. Taruh file ini di folder utama project."
fi

ketik "Mengecek apakah komputer ini punya Java (JDK)..."

# Cari Java: JAVA_HOME dulu, lalu java_home (macOS), lalu PATH
JAVA_BIN=""
JAVAC_BIN=""
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVA_BIN="$JAVA_HOME/bin/java"
  JAVAC_BIN="$JAVA_HOME/bin/javac"
fi
if [ -z "$JAVAC_BIN" ] && [ -x /usr/libexec/java_home ]; then
  JH="$(/usr/libexec/java_home 2>/dev/null)"
  if [ -n "$JH" ] && [ -x "$JH/bin/javac" ] && [ -x "$JH/bin/java" ]; then
    JAVA_BIN="$JH/bin/java"
    JAVAC_BIN="$JH/bin/javac"
  fi
fi
if [ -z "$JAVAC_BIN" ] && command -v javac >/dev/null 2>&1 && command -v java >/dev/null 2>&1 \
   && javac -version >/dev/null 2>&1; then
  JAVA_BIN="java"
  JAVAC_BIN="javac"
fi

if [ -z "$JAVAC_BIN" ]; then
  echo
  ketik "[ERROR] Yah, JDK-nya belum ada nih. Dokternya belum datang."
  ketik "Komputer ini belum punya Java (JDK), atau belum terdaftar di PATH."
  if command -v java >/dev/null 2>&1 && java -version >/dev/null 2>&1; then
    echo "Java-nya sih ada, tapi cuma JRE (belum ada javac). Kita butuhnya JDK."
  fi
  echo
  ketik "Gampang kok, ikutin resep ini:"
  if [ "$OS" = "Darwin" ]; then
    ketik "  1. Pasang JDK 17 atau lebih baru (JDK 21 paling oke), pilih salah satu:"
    ketik "       - unduh di https://adoptium.net"
    ketik "       - atau lewat Homebrew: brew install --cask temurin"
  else
    ketik "  1. Pasang JDK 17 atau lebih baru (JDK 21 paling oke), contoh:"
    ketik "       - Ubuntu/Debian : sudo apt install default-jdk"
    ketik "       - Fedora        : sudo dnf install java-latest-openjdk-devel"
    ketik "       - Arch          : sudo pacman -S jdk-openjdk"
    ketik "       - atau unduh di https://adoptium.net"
  fi
  ketik "  2. Tutup jendela ini, lalu jalankan file ini lagi."
  gagal "JDK (Java Development Kit) belum terpasang. Pasang JDK 17+ dari https://adoptium.net lalu coba lagi."
fi

ketik "Java ketemu! Aman, dokternya udah standby."

# Di Linux, aplikasi GUI butuh layar grafis
if [ "$OS" = "Linux" ] && [ -z "$DISPLAY" ] && [ -z "$WAYLAND_DISPLAY" ]; then
  echo
  ketik "[ERROR] Waduh, gak ada layar grafis (DISPLAY kosong)."
  ketik "Aplikasi Swing butuh desktop. Jalankan dari sesi desktop, bukan SSH biasa."
  gagal "Tidak ada layar grafis. Jalankan dari sesi desktop."
fi

mkdir -p out
LOGFILE="$(mktemp "${TMPDIR:-/tmp}/klinik_compile.XXXXXX")"

echo
ketik "[1/2] Lagi meracik kode jadi obat yang siap diminum (compile)..."
ketik "      Mohon jangan ditinggal tidur, ini cuma sebentar kok."
if ! "$JAVAC_BIN" -encoding UTF-8 -d out src/klinik/*.java >"$LOGFILE" 2>&1; then
  echo
  ketik "[ERROR] Aduh, racikannya gagal (kode gagal dikompilasi). Kata javac begini:"
  echo "--------------------------------------------"
  cat "$LOGFILE"
  echo "--------------------------------------------"
  ketik "Cek nama file dan nomor barisnya ya. Biasanya biang masalahnya ada di situ."
  PESAN="$(head -n 15 "$LOGFILE")"
  gagal "Kode gagal dikompilasi:
$PESAN"
fi
rm -f "$LOGFILE"

ketik "      Racikan selesai tanpa efek samping."
echo
ketik "[2/2] Membuka pintu klinik... silakan antre yang rapi ya!"
ketik "      Psst, jendela terminal ini jangan ditutup dulu."
ketik "      Kalau ditutup, aplikasinya ikut kabur lewat pintu belakang."
echo

"$JAVA_BIN" -ea -cp out klinik.MainApp
KODE=$?
if [ "$KODE" -ne 0 ]; then
  echo
  ketik "[ERROR] Yah, aplikasinya tiba-tiba pingsan (kode $KODE)."
  ketik "Cek pesan error Java di atas ini, mungkin ada petunjuknya."
  gagal "Aplikasi berhenti dengan kode $KODE. Jalankan lewat terminal untuk melihat detail errornya."
fi

echo
ketik "Kliniknya sudah tutup. Makasih udah mampir, semoga sehat selalu!"
exit 0
