package klinik;

import java.io.*;
import java.util.*;

public class ClinicManager {
    // Interface
    private final List<Patient> patientHistory = new ArrayList<>();         // Riwayat pasien
    private final Set<String> registeredIds = new HashSet<>();              // ID unik tanpa ada yang sama
    private final Queue<Patient> waitingQueue = new LinkedList<>();         // Antrean FIFO
    private final Map<String, Double> polyTariffMap = new HashMap<>();      // Tarif per poli

    // StringBuffer untuk log aktivitas
    private final StringBuffer activityLog = new StringBuffer();

    private static final String DB_FILE = "clinic_data.ser";

    public ClinicManager() {
        // Autoboxing dan nilai double otomatis di dalam hashmap
        polyTariffMap.put("Poli Umum", 75000.0);
        polyTariffMap.put("Poli Gigi", 150000.0);
        polyTariffMap.put("Poli Anak", 120000.0);
        polyTariffMap.put("Poli Mata", 135000.0);
        logActivity("Sistem Klinik SehatKita diinisialisasi.");
    }

    // Menambah teks ke StringBuffer
    public void logActivity(String entry) {
        activityLog.append("• ").append(entry).append("\n");
    }

    public String getActivityLog() {
        return activityLog.toString();
    }

    public Map<String, Double> getPolyTariffMap() {
        return polyTariffMap;
    }

    // Custom checked exception dan synchronized
    public synchronized String registerPatient(String rawName, String rawAge,
                                               String poliName, String complaint,
                                               boolean isBpjs, String bpjsNo) throws InvalidPatientDataException {
        String name = rawName.trim();
        String ageInput = rawAge.trim();
        String cleanComplaint = complaint.trim();

        if (name.isEmpty() || ageInput.isEmpty() || cleanComplaint.isEmpty()) {
            throw new InvalidPatientDataException("Nama, Umur, dan Keluhan tidak boleh kosong!");
        }

        // Nama Lengkap wajib huruf kapital semua 
        if (!name.matches("[A-Z][A-Z .'-]*")) {
            throw new InvalidPatientDataException("Nama lengkap harus ditulis dengan huruf KAPITAL semua!");
        }

        // Umur wajib bentuk angka tanpa huruf/simbol
        if (!ageInput.matches("\\d+")) {
            throw new InvalidPatientDataException("Umur harus dalam bentuk angka (tidak boleh ada huruf atau simbol)!");
        }

        int age;
        try {
            // ubat string ke integer, lalu unboxing ke int biasa
            Integer boxedAge = Integer.valueOf(ageInput);
            age = boxedAge;
        } catch (NumberFormatException e) {
            throw new InvalidPatientDataException("Umur harus dalam bentuk angka yang valid!");
        }

        if (age <= 0 || age > 130) {
            throw new InvalidPatientDataException("Umur harus berada di antara 1 hingga 130 tahun!");
        }

        String id = generatePatientId();

        double baseFee = polyTariffMap.getOrDefault(poliName, 75000.0);
        String fullComplaint = poliName + " - " + cleanComplaint;

        // Upcasting 
        Patient newPatient;
        if (isBpjs) {
            if (bpjsNo == null || !bpjsNo.trim().matches("\\d{13}")) {
                throw new InvalidPatientDataException("Nomor BPJS harus terdiri dari 13 digit angka!");
            }
            newPatient = new BPJSPatient(id, name, age, fullComplaint, baseFee, bpjsNo.trim());
        } else {
            newPatient = new RegulerPatient(id, name, age, fullComplaint, baseFee);
        }

        registeredIds.add(id);
        patientHistory.add(newPatient);
        waitingQueue.add(newPatient);

        // Assertion untuk memastikan kondisi internal benar
        assert !waitingQueue.isEmpty() : "Internal Error: Antrean tidak boleh kosong setelah tambah pasien";

        logActivity("Pasien terdaftar ke antrean: " + newPatient.getName() + " (" + id + ")");
        return id;
    }

    // Membuat ID otomatis berdasarkan nomor terbesar pada data pasien tersimpan.
    private String generatePatientId() {
        int maxNumber = 0;

        for (Patient patient : patientHistory) {
            String existingId = patient.getId();

            if (existingId != null && existingId.matches("PSN-\\d+")) {
                try {
                    int number = Integer.parseInt(existingId.substring(4));
                    maxNumber = Math.max(maxNumber, number);
                } catch (NumberFormatException ignored) {
                    // Abaikan ID lama yang angka belakangnya terlalu besar.
                }
            }
        }

        int nextNumber = maxNumber + 1;
        String candidate = String.format("PSN-%03d", nextNumber);

        while (registeredIds.contains(candidate)) {
            nextNumber++;
            candidate = String.format("PSN-%03d", nextNumber);
        }

        return candidate;
    }

    // Mengambil pasien dari antrean FIFO
    public synchronized Patient callNextPatient() {
        Patient next = waitingQueue.poll();
        assert waitingQueue.size() >= 0 : "Ukuran queue tidak mungkin negatif";
        if (next != null) {
            logActivity("Memanggil pasien dari antrean: " + next.getName());
        }
        return next;
    }

    // Compile-time Polymorphism untuk mencari pasien berdasarkan ID
    public Patient searchPatient(String id) {
        for (Patient p : patientHistory) {
            if (p.getId().equalsIgnoreCase(id.trim())) {
                return p;
            }
        }
        return null;
    }

    // Compile-time Polymorphism untuk cari berdasarkan kata kunci nama & umur minimal
    public List<Patient> searchPatient(String nameKeyword, int minAge) {
        List<Patient> results = new ArrayList<>();
        String key = nameKeyword.trim().toLowerCase();
        for (Patient p : patientHistory) {
            if (p.getName().toLowerCase().contains(key) && p.getAge() >= minAge) {
                results.add(p);
            }
        }
        return results;
    }

    // Collections Utility Methods (sort, max, min)
    public synchronized String getClinicStatistics() {
        if (patientHistory.isEmpty()) {
            return "Belum ada data pasien untuk dianalisis.";
        }
        List<Patient> sortedCopy = new ArrayList<>(patientHistory);
        Collections.sort(sortedCopy);
        Patient minBillPatient = Collections.min(sortedCopy);
        Patient maxBillPatient = Collections.max(sortedCopy);

        double totalRevenue = 0;
        for (Patient p : patientHistory) {
            totalRevenue += p.calculateTotalBill();
        }

        return String.format(
            "Total Pasien Terdaftar : %d orang\n" +
            "Pasien Dalam Antrean   : %d orang\n" +
            "Total Pendapatan Klinik: Rp %,.0f\n" +
            "Tagihan Terendah       : %s (Rp %,.0f)\n" +
            "Tagihan Tertinggi      : %s (Rp %,.0f)",
            patientHistory.size(), waitingQueue.size(), totalRevenue,
            minBillPatient.getName(), minBillPatient.calculateTotalBill(),
            maxBillPatient.getName(), maxBillPatient.calculateTotalBill()
        );
    }

    // Serialization (ObjectOutputStream) with synchronized 
    public synchronized void saveDatabaseToFile() throws IOException {
        File file = new File(DB_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(new ArrayList<>(patientHistory));
            oos.writeObject(new ArrayList<>(waitingQueue)); // antrean ikut tersimpan
        }
        logActivity("Auto-Save / Penyimpanan database berhasil (" + file.length() + " bytes).");
    }

    // Deserialization (ObjectInputStream) & pengecekan file
    @SuppressWarnings("unchecked")
    public synchronized void loadDatabaseFromFile() throws IOException, ClassNotFoundException {
        File file = new File(DB_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Patient> loaded = (List<Patient>) ois.readObject();
            patientHistory.clear();
            registeredIds.clear();
            for (Patient p : loaded) {
                patientHistory.add(p);
                registeredIds.add(p.getId());
            }
            waitingQueue.clear();
            try {
                List<Patient> savedQueue = (List<Patient>) ois.readObject();
                waitingQueue.addAll(savedQueue);
            } catch (EOFException oldFormat) {
            }
            logActivity("Database berhasil dimuat: " + loaded.size() + " riwayat pasien.");
        }
    }

    // Character Stream (BufferedWriter)
    public void exportReceiptToTxt(Patient patient, String fileName) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(patient.generateReceipt());
            bw.newLine();
            bw.write("Dicetak otomatis oleh Sistem Klinik SehatKita.");
        }
        logActivity("Struk pasien " + patient.getId() + " diekspor ke " + fileName);
    }

    public synchronized List<Patient> getPatientHistory() { return new ArrayList<>(patientHistory); }
    public synchronized Queue<Patient> getWaitingQueue() { return new LinkedList<>(waitingQueue); }
    
    // Menghapus file database menggunakan File.delete()
    public synchronized void resetDatabase() {
    patientHistory.clear();
    registeredIds.clear();
    waitingQueue.clear();
    File file = new File(DB_FILE);
    if (file.exists()) {
        file.delete();
    }
    logActivity("Seluruh data antrean dan riwayat berhasil direset ke 0.");
    }
}
