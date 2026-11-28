package klinik;

import java.io.Serializable;

// Exception khusus untuk menangani data pasien yang tidak valid
class InvalidPatientDataException extends Exception {
    public InvalidPatientDataException(String message) {
        super(message);
    }
}

// Interface untuk mengatur perhitungan tagihan pasien
interface Billable {
    double ADMIN_FEE = 15000.0;

    double calculateTotalBill();

    // Mengubah angka menjadi format mata uang Rupiah
    default String getCurrencyFormat(double amount) {
        return String.format("Rp %,.0f", amount);
    }
}

// Abstract class Person dengan penerapan encapsulation
abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private int age;

    public Person(String id, String name, int age) {
        setId(id);
        setName(name);
        setAge(age);
    }

    // Method untuk menentukan kategori pasien
    public abstract String getPatientCategory();

    // Getter dan Setter untuk ID pasien
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "ID pasien tidak boleh kosong"
            );
        }
        this.id = id.trim();
    }

    // Getter dan Setter untuk nama pasien
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Nama pasien tidak boleh kosong"
            );
        }
        this.name = name.trim();
    }

    // Getter dan Setter untuk umur pasien
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException(
                "Umur harus lebih dari 0"
            );
        }
        this.age = age;
    }
}

// Class Patient yang mewarisi Person dan menerapkan Billable serta Comparable
abstract class Patient extends Person implements Billable, Comparable<Patient> {
    private static final long serialVersionUID = 1L;

    // Menghitung jumlah seluruh pasien yang dibuat
    private static int totalPatientsCreated = 0;

    // Atribut pasien dibuat private untuk menerapkan encapsulation
    private String complaint;
    private double baseConsultationFee;

    public Patient(String id, String name, int age,
                   String complaint, double baseConsultationFee) {
        super(id, name, age);

        setComplaint(complaint);
        setBaseConsultationFee(baseConsultationFee);

        totalPatientsCreated++;
    }

    // Mengambil jumlah pasien yang sudah dibuat
    public static int getTotalPatientsCreated() {
        return totalPatientsCreated;
    }

    // Getter dan Setter untuk keluhan pasien
    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        if (complaint == null || complaint.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Keluhan tidak boleh kosong"
            );
        }
        this.complaint = complaint.trim();
    }

    // Getter dan Setter untuk tarif konsultasi
    public double getBaseConsultationFee() {
        return baseConsultationFee;
    }

    public void setBaseConsultationFee(double fee) {
        if (!Double.isFinite(fee) || fee < 0) {
            throw new IllegalArgumentException(
                "Tarif konsultasi tidak valid"
            );
        }
        this.baseConsultationFee = fee;
    }

    // Membuat struk pembayaran menggunakan StringBuilder
    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("       STRUK PEMBAYARAN KLINIK          \n");
        sb.append("========================================\n");
        sb.append("ID Pasien   : ").append(getId()).append("\n");
        sb.append("Nama Pasien : ").append(getName()).append("\n");
        sb.append("Umur        : ").append(getAge()).append(" Tahun\n");
        sb.append("Kategori    : ").append(getPatientCategory()).append("\n");
        sb.append("Keluhan     : ").append(getComplaint()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Total Biaya : ")
          .append(getCurrencyFormat(calculateTotalBill()))
          .append("\n");
        sb.append("========================================\n");

        return sb.toString();
    }

    // Membandingkan total tagihan antar pasien
    @Override
    public int compareTo(Patient other) {
        return Double.compare(
            this.calculateTotalBill(),
            other.calculateTotalBill()
        );
    }

    // Menampilkan informasi pasien dalam bentuk String
    @Override
    public String toString() {
        return String.format(
            "[%s] %s (%d thn) - %s | %s | Tagihan: %s",
            getId(),
            getName(),
            getAge(),
            getPatientCategory(),
            getComplaint(),
            getCurrencyFormat(calculateTotalBill())
        );
    }
}

// Class untuk pasien reguler yang mewarisi Patient
class RegulerPatient extends Patient {
    private static final long serialVersionUID = 1L;

    public RegulerPatient(String id, String name, int age,
                          String complaint, double baseFee) {
        super(id, name, age, complaint, baseFee);
    }

    // Menentukan kategori pasien reguler
    @Override
    public String getPatientCategory() {
        return "Pasien Reguler (Umum)";
    }

    // Menghitung tagihan pasien reguler ditambah biaya administrasi
    @Override
    public double calculateTotalBill() {
        return getBaseConsultationFee() + ADMIN_FEE;
    }
}

// Class untuk pasien BPJS yang mewarisi Patient
class BPJSPatient extends Patient {
    private static final long serialVersionUID = 1L;

    private String bpjsNumber;

    public BPJSPatient(String id, String name, int age,
                       String complaint, double baseFee,
                       String bpjsNumber) {
        super(id, name, age, complaint, baseFee);
        setBpjsNumber(bpjsNumber);
    }

    // Getter untuk nomor BPJS
    public String getBpjsNumber() {
        return bpjsNumber;
    }

    // Setter untuk nomor BPJS sekaligus memeriksa formatnya
    public void setBpjsNumber(String bpjsNumber) {
        if (bpjsNumber == null ||
            !bpjsNumber.matches("\\d{13}")) {
            throw new IllegalArgumentException(
                "Nomor BPJS harus terdiri dari 13 digit angka"
            );
        }

        this.bpjsNumber = bpjsNumber;
    }

    // Menampilkan kategori pasien beserta nomor BPJS
    @Override
    public String getPatientCategory() {
        return "Pasien BPJS (No: " + bpjsNumber + ")";
    }

    // Menghitung tagihan pasien BPJS sebesar 20% dari tarif konsultasi
    @Override
    public double calculateTotalBill() {
        return getBaseConsultationFee() * 0.20;
    }
} 