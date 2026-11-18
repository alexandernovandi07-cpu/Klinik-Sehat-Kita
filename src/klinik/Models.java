package klinik;

import java.io.Serializable;

// WEEK 11: Custom Checked Exception
class InvalidPatientDataException extends Exception {
    public InvalidPatientDataException(String message) {
        super(message);
    }
}

// WEEK 7: Interface
interface Billable {
    double ADMIN_FEE = 15000.0;

    double calculateTotalBill();

    default String getCurrencyFormat(double amount) {
        return String.format("Rp %,.0f", amount);
    }
}

// WEEK 7 & WEEK 5: Abstract Class + Encapsulation
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

    public abstract String getPatientCategory();

    // Getter dan Setter ID
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

    // Getter dan Setter Nama
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

    // Getter dan Setter Umur
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

// WEEK 4: Inheritance & Comparable
abstract class Patient extends Person implements Billable, Comparable<Patient> {
    private static final long serialVersionUID = 1L;

    private static int totalPatientsCreated = 0;

    // Encapsulation
    private String complaint;
    private double baseConsultationFee;

    public Patient(String id, String name, int age,
                   String complaint, double baseConsultationFee) {
        super(id, name, age);

        setComplaint(complaint);
        setBaseConsultationFee(baseConsultationFee);

        totalPatientsCreated++;
    }

    public static int getTotalPatientsCreated() {
        return totalPatientsCreated;
    }

    // Getter dan Setter Keluhan
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

    // Getter dan Setter Tarif Konsultasi
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

    // WEEK 9: StringBuilder
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

    // WEEK 10: Comparable
    @Override
    public int compareTo(Patient other) {
        return Double.compare(
            this.calculateTotalBill(),
            other.calculateTotalBill()
        );
    }

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
