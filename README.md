# Klinik SehatKita — Sistem Antrean & Administrasi Klinik

Aplikasi desktop berbasis **Java Swing** untuk manajemen pendaftaran pasien, antrean poliklinik (*FIFO Queue*), perhitungan tagihan otomatis (Reguler & BPJS), pencetakan struk `.txt`, serta penyimpanan data otomatis (*Auto-Save Multithreading*).

Dibuat untuk memenuhi **Final Project Mata Kuliah Pemrograman Berorientasi Objek (PBO)** — Informatika, Universitas Sebelas Maret (UNS).

## Anggota Kelompok
| Nama | NIM | Peran |
| :--- | :--- | :--- |
| Alexander Alvin Novandi | L0125002 | Pengembangan Antarmuka dan Integrasi Aplikasi |
| Na'ila Tsabita Farha | L0125026 | Pengembangan Fitur dan Logika Aplikasi |
| Ayu Atikah Azhar | L0125127 | Pengembangan Struktur Program dan Pengelolaan Data |

## Implementasi Materi Perkuliahan (Week 1–14)
* **OOP Pillars (Week 1–7):** `abstract class Person`, `interface Billable`, Inheritance (`Patient` → `RegulerPatient` & `BPJSPatient`), Encapsulation (`private` fields + Getter/Setter), dan Polymorphism (`calculateTotalBill()` & overloaded `searchPatient()`).
* **String & Collections (Week 9–10):** `StringBuilder` (cetak struk), `StringBuffer` (log thread-safe), `ArrayList`, `HashSet`, `Queue` (`LinkedList`), `HashMap`, serta `Collections.sort/min/max`.
* **Exception, Java I/O & Multithreading (Week 11–14):** Custom `InvalidPatientDataException`, `assert`, `ObjectOutputStream` (`.ser`), `BufferedWriter` (`.txt`), serta background `Runnable` thread untuk jam real-time dan *synchronized auto-save*.


## Cara Menjalankan Aplikasi

Pastikan **JDK** sudah terpasang, lalu pilih salah satu cara berikut untuk menjalankan aplikasi.

### Opsi 1. Melalui Terminal

Jalankan perintah berikut dari root folder repository:

```bash
# 1. Compile source code
mkdir -p out
javac -d out src/klinik/*.java

# 2. Jalankan aplikasi (dengan assertion aktif)
java -ea -cp out klinik.MainApp
```

### Opsi 2. Melalui File Launcher

Aplikasi juga dapat dijalankan secara langsung melalui file launcher yang tersedia di folder utama repository:

- **Windows:** `Aplikasi_KlinikSehat.bat`
- **Linux:** `Aplikasi_KlinikSehat.sh`
- **macOS:** `Apilkasi_KlinikSehat.command`
  
Pastikan JDK sudah terpasang sebelum menjalankan file launcher.
