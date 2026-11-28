package klinik;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

// OOP, Class & Object, Inheritance: ClinicFrame mewarisi JFrame
public class ClinicFrame extends JFrame {

    // Encapsulation: Semua atribut dibikin private biar aksesnya terkontrol di dalam class
    // Class & Object: Instansiasi objek ClinicManager & komponen Swing
    private final ClinicManager manager = new ClinicManager();

    // Java GUI: Komponen input form & tampilan
    private final JTextField txtName = new JTextField();
    private final JTextField txtAge = new JTextField();
    // Generics: Pakai tipe parameter <String> pada JComboBox
    private final JComboBox<String> cmbPoli = new JComboBox<>(new String[]{
            "Poli Umum", "Poli Gigi", "Poli Anak", "Poli Mata"
    });
    private final JTextField txtComplaint = new JTextField();
    private final JCheckBox chkBpjs = new JCheckBox("Gunakan BPJS Kesehatan (Subsidi 80%)");
    private final JTextField txtBpjsNo = new JTextField();
    private final JTextField txtSearch = new JTextField(10);

    // Java GUI: Label peringatan merah di bawah tiap kolom
    private final JLabel lblNameWarn = new JLabel(" ");
    private final JLabel lblAgeWarn = new JLabel(" ");
    private final JLabel lblBpjsWarn = new JLabel(" ");

    // Java GUI: Border normal vs border merah terang
    private final Border normalBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 185, 190), 1),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)
    );

    private final Border errorBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED, 2),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
    );

    // Java GUI: Custom ImageIcon untuk pop-up Berhasil (Centang Hijau) & Gagal (Tanda Seru)
    private final ImageIcon successIcon = createSuccessIcon();
    private final ImageIcon warningIcon = createWarningIcon();

    private final JTextArea areaQueue = new JTextArea();
    private final JTextArea areaHistory = new JTextArea();
    private final JTextArea areaLog = new JTextArea(5, 40);
    private final JLabel lblClockAndThread = new JLabel("Memuat jam sistem...");

    // Class & Object (Constructor): Inisialisasi awal frame saat objek dibuat
    public ClinicFrame() {
        setTitle("Sistem Antrean & Administrasi Klinik SehatKita");
        setSize(1140, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Java GUI (Layout Manager): BorderLayout sebagai layout utama frame
        setLayout(new BorderLayout(10, 10));

        initComponents();
        setupFocusAndLiveValidation();
        loadInitialData();
        startBackgroundThread(); // Multithreading: Jalankan background thread
    }

    // Java GUI (Graphics2D): Gambar custom icon centang hijau pakai Graphics2D
    private ImageIcon createSuccessIcon() {
        int size = 44; 
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(22, 163, 74));
        g2.fillOval(2, 2, size - 4, size - 4);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(12, 23, 19, 30);
        g2.drawLine(19, 30, 32, 15);
        g2.dispose();
        return new ImageIcon(img);
    }

    // Java GUI (Graphics2D): Gambar custom icon tanda seru merah pakai Graphics2D
    private ImageIcon createWarningIcon() {
        int size = 44;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(220, 38, 38));
        g2.fillOval(2, 2, size - 4, size - 4);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(22, 12, 22, 25);
        g2.fillOval(19, 30, 6, 6);
        g2.dispose();
        return new ImageIcon(img);
    }

    private void initComponents() {
        Color clinicGreen = new Color(16, 124, 65);

        // Java GUI (NORTH PANEL): Header & Live Thread Status
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(clinicGreen);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblTitle = new JLabel("Antrean & Rekam Medis");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        lblClockAndThread.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblClockAndThread.setForeground(new Color(220, 248, 232));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblClockAndThread, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Java GUI (WEST PANEL - GridLayout): Form pendaftaran pasien pakai GridLayout
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 2, 2));
        formPanel.setBorder(new TitledBorder("Form Pendaftaran Pasien Baru"));
        formPanel.setPreferredSize(new Dimension(340, 0));

        txtName.setBorder(normalBorder);
        txtAge.setBorder(normalBorder);
        txtComplaint.setBorder(normalBorder);
        txtBpjsNo.setBorder(normalBorder);

        Font warnFont = new Font("SansSerif", Font.BOLD, 11);
        lblNameWarn.setForeground(Color.RED);
        lblNameWarn.setFont(warnFont);
        lblAgeWarn.setForeground(Color.RED);
        lblAgeWarn.setFont(warnFont);
        lblBpjsWarn.setForeground(Color.RED);
        lblBpjsWarn.setFont(warnFont);

        formPanel.add(new JLabel("Nama Lengkap Pasien (HURUF KAPITAL):"));
        formPanel.add(txtName);
        formPanel.add(lblNameWarn);

        formPanel.add(new JLabel("Umur (Tahun):"));
        formPanel.add(txtAge);
        formPanel.add(lblAgeWarn);

        formPanel.add(new JLabel("Pilih Poliklinik Tujuan:"));
        formPanel.add(cmbPoli);
        formPanel.add(new JLabel("Keluhan Medis:"));
        formPanel.add(txtComplaint);
        formPanel.add(chkBpjs);
        formPanel.add(new JLabel("Nomor Kartu BPJS (13 digit angka):"));

        txtBpjsNo.setEnabled(false);
        formPanel.add(txtBpjsNo);
        formPanel.add(lblBpjsWarn);

        JButton btnAdd = new JButton("1. Tambah ke Antrean");
        btnAdd.setBackground(clinicGreen);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setOpaque(true);
        btnAdd.setBorderPainted(false);
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.add(btnAdd);

        add(formPanel, BorderLayout.WEST);

        // Java GUI (Event Handling): Toggle aktif/nonaktif kolom BPJS saat checkbox dicentang
        chkBpjs.addActionListener(e -> {
            boolean selected = chkBpjs.isSelected();
            txtBpjsNo.setEnabled(selected);
            if (selected) {
                txtBpjsNo.requestFocus();
            } else {
                txtBpjsNo.setText("");
                txtBpjsNo.setBorder(normalBorder);
                lblBpjsWarn.setText(" ");
            }
        });

        // Java GUI (CENTER PANEL - GridLayout): Split tampilan Antrean (Queue FIFO) & Riwayat (ArrayList)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        areaQueue.setEditable(false);
        areaQueue.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaQueue.setBackground(new Color(244, 251, 247));
        JScrollPane scrollQueue = new JScrollPane(areaQueue);
        scrollQueue.setBorder(new TitledBorder("Antrean Tunggu Saat Ini"));

        areaHistory.setEditable(false);
        areaHistory.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaHistory.setBackground(new Color(244, 251, 247));
        areaHistory.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JScrollPane scrollHistory = new JScrollPane(areaHistory);
        scrollHistory.setBorder(new TitledBorder("Riwayat Seluruh Pasien"));

        // Inheritance & Polymorphism (Overriding): MouseAdapter mewarisi MouseListener
        areaHistory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int offset = areaHistory.viewToModel2D(e.getPoint());

                // Exception Handling: Tangkap BadLocationException kalau klik di area kosong
                try {
                    int line = areaHistory.getLineOfOffset(offset);
                    // Generics & Collections: Ambil List<Patient> dari ArrayList riwayat
                    List<Patient> patients = manager.getPatientHistory();

                    if (line >= 0 && line < patients.size()) {
                        showPatientDetail(patients.get(line));
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });

        centerPanel.add(scrollQueue);
        centerPanel.add(scrollHistory);
        add(centerPanel, BorderLayout.CENTER);

        // Java GUI (SOUTH PANEL - FlowLayout): Tombol aksi (FlowLayout) + Log aktivitas thread-safe
        JPanel southContainer = new JPanel(new BorderLayout(5, 5));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JButton btnCallNext = new JButton("2. Panggil Pasien & Cetak Struk (.txt)");
        JButton btnStats = new JButton("3. Statistik & Urutkan Tagihan");
        JButton btnSave = new JButton("4. Simpan DB (.ser)");
        JLabel lblSearch = new JLabel("Cari ID/Nama:");
        JButton btnSearch = new JButton("Cari");

        buttonPanel.add(btnCallNext);
        buttonPanel.add(btnStats);
        buttonPanel.add(btnSave);

        JButton btnReset = new JButton("Reset Data");
        buttonPanel.add(btnReset);

        btnReset.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Hapus seluruh riwayat dan antrean pasien?", "Konfirmasi Reset",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, warningIcon);

            if (confirm == JOptionPane.YES_OPTION) {
                manager.resetDatabase();
                refreshDisplay();
                JOptionPane.showMessageDialog(this, "Data berhasil direset menjadi 0!",
                        "Reset Berhasil", JOptionPane.INFORMATION_MESSAGE, successIcon);
            }
        });

        buttonPanel.add(lblSearch);
        buttonPanel.add(txtSearch);
        buttonPanel.add(btnSearch);

        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        // String (StringBuffer): Log di dalam manager memakai StringBuffer yang thread-safe
        scrollLog.setBorder(new TitledBorder("Log Aktivitas"));

        southContainer.add(buttonPanel, BorderLayout.NORTH);
        southContainer.add(scrollLog, BorderLayout.CENTER);
        add(southContainer, BorderLayout.SOUTH);

        // Java GUI (Event Handling): Listener tombol tambah pasien
        btnAdd.addActionListener(e -> handleRegisterPatient());

        btnCallNext.addActionListener(e -> handleCallNextPatient());
        // Generics & Collections: Menampilkan hasil Collections.sort, max, dan min
        btnStats.addActionListener(e -> JOptionPane.showMessageDialog(this,
                manager.getClinicStatistics(), "Statistik Klinik",
                JOptionPane.INFORMATION_MESSAGE, successIcon));

        btnSave.addActionListener(e -> {
            // Exception Handling & Java I/O (Serialization): Simpan objek ke file serialisasi .ser
            try {
                manager.saveDatabaseToFile();
                refreshDisplay();
                JOptionPane.showMessageDialog(this, "Database berhasil disimpan ke clinic_data.ser!",
                        "Penyimpanan Berhasil", JOptionPane.INFORMATION_MESSAGE, successIcon);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + ex.getMessage(),
                        "I/O Error", JOptionPane.ERROR_MESSAGE, warningIcon);
            }
        });

        btnSearch.addActionListener(e -> handleSearch());
    }

    // Java GUI (Event Handling): Validasi real-time saat mengetik maupun saat kursor pindah (focusLost)
    private void setupFocusAndLiveValidation() {
        // String: Nama Lengkap cek huruf kapital pakai method String.toUpperCase()
        Runnable validateName = () -> {
            String val = txtName.getText();
            if (!val.isEmpty() && !val.equals(val.toUpperCase())) {
                txtName.setBorder(errorBorder);
                lblNameWarn.setText("Peringatan: Nama ditulis dengan huruf KAPITAL!");
            } else {
                txtName.setBorder(normalBorder);
                lblNameWarn.setText(" ");
            }
        };

        txtName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { validateName.run(); }
        });

        // Abstraction (Interface): Implementasi method abstrak dari interface DocumentListener
        txtName.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateName.run(); }
            public void removeUpdate(DocumentEvent e) { validateName.run(); }
            public void changedUpdate(DocumentEvent e) { validateName.run(); }
        });

        // String (Regex): Umur cek format angka saja pakai regex String.matches()
        Runnable validateAge = () -> {
            String val = txtAge.getText().trim();
            if (!val.isEmpty() && !val.matches("\\d+")) {
                txtAge.setBorder(errorBorder);
                lblAgeWarn.setText("Peringatan: Umur harus dalam bentuk angka!");
            } else {
                txtAge.setBorder(normalBorder);
                lblAgeWarn.setText(" ");
            }
        };

        txtAge.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { validateAge.run(); }
        });

        txtAge.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateAge.run(); }
            public void removeUpdate(DocumentEvent e) { validateAge.run(); }
            public void changedUpdate(DocumentEvent e) { validateAge.run(); }
        });

        // String (Regex): Nomor BPJS wajib 13 digit angka kalau checkbox BPJS dicentang
        Runnable validateBpjs = () -> {
            if (!chkBpjs.isSelected()) {
                txtBpjsNo.setBorder(normalBorder);
                lblBpjsWarn.setText(" ");
                return;
            }

            String val = txtBpjsNo.getText().trim();
            if (!val.matches("\\d{13}")) {
                txtBpjsNo.setBorder(errorBorder);
                lblBpjsWarn.setText("Peringatan: Nomor BPJS harus 13 digit angka!");
            } else {
                txtBpjsNo.setBorder(normalBorder);
                lblBpjsWarn.setText(" ");
            }
        };

        txtBpjsNo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { validateBpjs.run(); }
        });

        txtBpjsNo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateBpjs.run(); }
            public void removeUpdate(DocumentEvent e) { validateBpjs.run(); }
            public void changedUpdate(DocumentEvent e) { validateBpjs.run(); }
        });
    }

    private void handleRegisterPatient() {
        // Exception Handling: Blok try-catch untuk menangani custom exception validasi
        try {
            String newPatientId = manager.registerPatient(
                    txtName.getText(),
                    txtAge.getText(),
                    (String) cmbPoli.getSelectedItem(),
                    txtComplaint.getText(),
                    chkBpjs.isSelected(),
                    txtBpjsNo.getText()
            );

            txtName.setText("");
            txtAge.setText("");
            txtComplaint.setText("");
            txtBpjsNo.setText("");
            chkBpjs.setSelected(false);
            txtBpjsNo.setEnabled(false);

            txtName.setBorder(normalBorder);
            txtAge.setBorder(normalBorder);
            txtBpjsNo.setBorder(normalBorder);

            lblNameWarn.setText(" ");
            lblAgeWarn.setText(" ");
            lblBpjsWarn.setText(" ");

            refreshDisplay();

            JOptionPane.showMessageDialog(this,
                    "Pasien berhasil masuk ke dalam antrean!\nID Pasien: " + newPatientId,
                    "Pendaftaran Berhasil", JOptionPane.INFORMATION_MESSAGE, successIcon);

        } catch (InvalidPatientDataException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validasi Input Gagal", JOptionPane.WARNING_MESSAGE, warningIcon);
        }
    }

    private void handleCallNextPatient() {
        Patient called = manager.callNextPatient();

        if (called == null) {
            JOptionPane.showMessageDialog(this, "Antrean saat ini kosong!",
                    "Info Antrean", JOptionPane.WARNING_MESSAGE, warningIcon);
            return;
        }

        // Encapsulation: Ambil ID pasien lewat getter getId()
        String fileName = "Struk_" + called.getId() + ".txt";

        // Java I/O & Exception Handling: Export struk pembayaran ke file .txt
        try {
            manager.exportReceiptToTxt(called, fileName);
            refreshDisplay();

            // Polymorphism (Dynamic Binding): generateReceipt() menyesuaikan tipe pasien
            JOptionPane.showMessageDialog(this,
                    called.generateReceipt() + "\nStruk telah diekspor ke file: " + fileName,
                    "Pasien Dipanggil & Struk Dicetak",
                    JOptionPane.INFORMATION_MESSAGE, successIcon);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak struk: " + ex.getMessage(),
                    "File I/O Error", JOptionPane.ERROR_MESSAGE, warningIcon);
        }
    }

    private void handleSearch() {
        // String: Rapikan input pencarian pakai trim() dan cek prefix startsWith()
        String query = txtSearch.getText().trim();

        if (query.isEmpty()) return;

        // Polymorphism (Method Overloading): searchPatient(String) untuk cari berdasarkan ID
        Patient byId = manager.searchPatient(
                query.startsWith("PSN-") ? query : "PSN-" + query);

        if (byId != null) {
            JOptionPane.showMessageDialog(this, "Ditemukan via ID:\n" + byId.generateReceipt(),
                    "Hasil Pencarian", JOptionPane.INFORMATION_MESSAGE, successIcon);
            return;
        }

        // Polymorphism (Method Overloading): searchPatient(String, int) untuk cari berdasarkan Nama
        List<Patient> byName = manager.searchPatient(query, 0);

        if (byName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pasien '" + query + "' tidak ditemukan.",
                    "Hasil Pencarian", JOptionPane.WARNING_MESSAGE, warningIcon);
        } else {
            // String (StringBuilder): Susun hasil pencarian pakai StringBuilder
            StringBuilder sb = new StringBuilder("Ditemukan " + byName.size() + " pasien:\n\n");

            for (Patient p : byName) {
                sb.append(p.toString()).append("\n");
            }

            JOptionPane.showMessageDialog(this, sb.toString(),
                    "Hasil Pencarian", JOptionPane.INFORMATION_MESSAGE, successIcon);
        }
    }

    // Java GUI & Polymorphism: Menampilkan detail pasien saat baris riwayat diklik
    private void showPatientDetail(Patient patient) {
        String jenisPasien;
        String nomorBpjs = "-";

        // Inheritance & Polymorphism: Cek subclass BPJSPatient & downcasting
        if (patient instanceof BPJSPatient) {
            BPJSPatient bpjsPatient = (BPJSPatient) patient;
            jenisPasien = "BPJS";

            // Encapsulation & String: Ambil no BPJS via getter lalu masking 9 digit awal
            String number = bpjsPatient.getBpjsNumber();
            nomorBpjs = "*********" + number.substring(9);
        } else {
            jenisPasien = "Reguler";
        }

        // Encapsulation: Akses seluruh data atribut lewat method getter
        // Polymorphism (Dynamic Binding): calculateTotalBill() hitung tagihan sesuai jenis pasien
        String detail =
                "ID Pasien       : " + patient.getId() + "\n" +
                "Nama            : " + patient.getName() + "\n" +
                "Umur            : " + patient.getAge() + " tahun\n" +
                "Jenis Pasien    : " + jenisPasien + "\n" +
                "Nomor BPJS      : " + nomorBpjs + "\n" +
                "Keluhan         : " + patient.getComplaint() + "\n" +
                "Total Tagihan   : " +
                patient.getCurrencyFormat(patient.calculateTotalBill());

        JOptionPane.showMessageDialog(this,
                detail,
                "Detail Pasien",
                JOptionPane.INFORMATION_MESSAGE,
                successIcon);
    }

    // Java I/O (Deserialization) & Exception Handling: Baca ulang data dari file serialisasi saat aplikasi dibuka
    private void loadInitialData() {
        try {
            manager.loadDatabaseFromFile();
        } catch (Exception ex) {
            manager.logActivity("Memulai dengan database baru.");
        }

        refreshDisplay();
    }

    private void refreshDisplay() {
        // String (StringBuilder): Pakai StringBuilder supaya penyusunan daftar antrean lebih efisien
        StringBuilder qText = new StringBuilder();
        int pos = 1;

        // Generics & Collections: Iterasi elemen dari Queue antrean
        for (Patient p : manager.getWaitingQueue()) {
            qText.append(pos++).append(". ").append(p.toString()).append("\n");
        }

        areaQueue.setText(
                qText.length() == 0
                        ? "(Tidak ada pasien dalam antrean)"
                        : qText.toString());

        StringBuilder hText = new StringBuilder();

        // Generics & Collections: Iterasi elemen dari ArrayList riwayat
        for (Patient p : manager.getPatientHistory()) {
            hText.append("• ").append(p.toString()).append("\n");
        }

        areaHistory.setText(
                hText.length() == 0
                        ? "(Belum ada riwayat pasien)"
                        : hText.toString());

        areaLog.setText(manager.getActivityLog());
    }

    // Multithreading: Background thread untuk jam real-time & auto-save tiap 30 detik
    private void startBackgroundThread() {
        // Abstraction (Interface) & Multithreading: Anonymous class mengimplementasikan interface Runnable
        Runnable backgroundTask = new Runnable() {
            private int secondsCounter = 0;
            private final SimpleDateFormat sdf =
                    new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000); // Multithreading: Jeda thread 1 detik
                        secondsCounter++;

                        String timeStr = sdf.format(new Date());

                        // Java I/O (Serialization): Auto-save database ke .ser setiap kelipatan 30 detik
                        if (secondsCounter % 30 == 0
                                && !manager.getPatientHistory().isEmpty()) {
                            manager.saveDatabaseToFile();
                            SwingUtilities.invokeLater(() -> refreshDisplay());
                        }

                        final int nextSave = 30 - (secondsCounter % 30);

                        // Multithreading & Java GUI: Update komponen GUI dari background thread lewat Event Dispatch Thread (EDT)
                        SwingUtilities.invokeLater(() ->
                                lblClockAndThread.setText(
                                        "Jam: " + timeStr
                                                + " | Auto-Save: Aktif ("
                                                + nextSave + "d)"
                                ));

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (IOException ioEx) {
                        System.err.println(
                                "Auto-save gagal: " + ioEx.getMessage());
                    }
                }
            }
        };

        Thread autoSaveThread = new Thread(
             backgroundTask, "Clinic-AutoSave-Thread");

        autoSaveThread.start();
    }
}