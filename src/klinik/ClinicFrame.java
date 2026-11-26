package klinik;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

// Bab 1 & 2 (OOP, Class & Object) + Bab 4 (Inheritance): ClinicFrame mewarisi JFrame
public class ClinicFrame extends JFrame {

    // Bab 5 (Encapsulation): Semua atribut dibikin private biar aksesnya terkontrol di dalam class
    // Bab 2 (Class & Object): Instansiasi objek ClinicManager & komponen Swing
    private final ClinicManager manager = new ClinicManager();

    // Bab 13 (Java GUI): Komponen input form & tampilan
    private final JTextField txtName = new JTextField();
    private final JTextField txtAge = new JTextField();
    // Bab 10 (Generics): Pakai tipe parameter <String> pada JComboBox
    private final JComboBox<String> cmbPoli = new JComboBox<>(new String[]{
            "Poli Umum", "Poli Gigi", "Poli Anak", "Poli Mata"
    });
    private final JTextField txtComplaint = new JTextField();
    private final JCheckBox chkBpjs = new JCheckBox("Gunakan BPJS Kesehatan (Subsidi 80%)");
    private final JTextField txtBpjsNo = new JTextField();
    private final JTextField txtSearch = new JTextField(10);

    // Label peringatan merah di bawah tiap kolom
    private final JLabel lblNameWarn = new JLabel(" ");
    private final JLabel lblAgeWarn = new JLabel(" ");
    private final JLabel lblBpjsWarn = new JLabel(" ");

    // Border normal vs border merah terang 
    private final Border normalBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 185, 190), 1),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)
    );

    private final Border errorBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED, 2),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
    );

    // Bab 13 (Java GUI): Custom ImageIcon untuk pop-up Berhasil (Centang Hijau) & Gagal (Tanda Seru)
    private final ImageIcon successIcon = createSuccessIcon();
    private final ImageIcon warningIcon = createWarningIcon();

    private final JTextArea areaQueue = new JTextArea();
    private final JTextArea areaHistory = new JTextArea();
    private final JTextArea areaLog = new JTextArea(5, 40);
    private final JLabel lblClockAndThread = new JLabel("Memuat jam sistem...");

    // Bab 3 (Java Variables): Variabel primitif boolean dengan modifier volatile agar aman dibaca antar-thread
    private volatile boolean runningThread = true;

    // Bab 2 (Class & Object): Constructor untuk inisialisasi awal frame saat objek dibuat
    public ClinicFrame() {
        setTitle("Sistem Antrean & Administrasi Klinik SehatKita");
        setSize(1140, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Bab 13 (Java GUI): BorderLayout sebagai layout utama frame
        setLayout(new BorderLayout(10, 10));

        initComponents();
        setupFocusAndLiveValidation();
        loadInitialData();
    }

    // Bab 13 (Java GUI): Gambar custom icon centang hijau pakai Graphics2D
    private ImageIcon createSuccessIcon() {
        int size = 44; // Bab 3 (Java Variables): Variabel lokal primitif int
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

    // Bab 13 (Java GUI): Gambar custom icon tanda seru merah pakai Graphics2D
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

        // 1. NORTH PANEL: Header & Live Thread Status
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

        // 2. WEST PANEL: Form pendaftaran pasien pakai GridLayout (Bab 13)
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

        // Toggle aktif/nonaktif kolom BPJS saat checkbox dicentang
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

        // 3. CENTER PANEL: Split tampilan Antrean (Queue FIFO) & Riwayat (ArrayList)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        areaQueue.setEditable(false);
        areaQueue.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaQueue.setBackground(new Color(244, 251, 247));
        JScrollPane scrollQueue = new JScrollPane(areaQueue);
        scrollQueue.setBorder(new TitledBorder("Antrean Tunggu Saat Ini (Queue - FIFO)"));

        areaHistory.setEditable(false);
        areaHistory.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaHistory.setBackground(new Color(244, 251, 247));
        areaHistory.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JScrollPane scrollHistory = new JScrollPane(areaHistory);
        scrollHistory.setBorder(new TitledBorder("Riwayat Seluruh Pasien (ArrayList)"));

        centerPanel.add(scrollQueue);
        centerPanel.add(scrollHistory);
        add(centerPanel, BorderLayout.CENTER);

        // 4. SOUTH PANEL: Tombol aksi (FlowLayout) + Log aktivitas thread-safe
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

        buttonPanel.add(lblSearch);
        buttonPanel.add(txtSearch);
        buttonPanel.add(btnSearch);

        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        // Bab 9 (String): Log di dalam manager memakai StringBuffer yang thread-safe
        scrollLog.setBorder(new TitledBorder("Log Aktivitas Thread-Safe (StringBuffer)"));

        southContainer.add(buttonPanel, BorderLayout.NORTH);
        southContainer.add(scrollLog, BorderLayout.CENTER);
        add(southContainer, BorderLayout.SOUTH);

        // Bab 7 (Abstraction) & Bab 13 (Event Handling): Implementasi interface ActionListener
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegisterPatient();
            }
        });
    }

    // Validasi real-time saat mengetik maupun saat kursor pindah (focusLost)
    private void setupFocusAndLiveValidation() {
        // 1. Nama Lengkap: cek huruf kapital pakai method String.toUpperCase() (Bab 9)
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

        // Bab 7 (Abstraction): Implementasi method abstrak dari interface DocumentListener
        txtName.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateName.run(); }
            public void removeUpdate(DocumentEvent e) { validateName.run(); }
            public void changedUpdate(DocumentEvent e) { validateName.run(); }
        });

        // 2. Umur: cek format angka saja pakai regex String.matches() (Bab 9)
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

        // 3. Nomor BPJS: wajib 13 digit angka kalau checkbox BPJS dicentang
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
        // Bab 11 (Exceptions): Blok try-catch-finally untuk menangani custom exception validasi
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
        } finally {
            System.out.println("Proses percobaan registrasi pasien selesai.");
        }
    }

    // Bab 12 (Java IO) & Bab 11 (Exceptions): Baca ulang data dari file serialisasi saat aplikasi dibuka
    private void loadInitialData() {
        try {
            manager.loadDatabaseFromFile();
        } catch (Exception ex) {
            manager.logActivity("Memulai dengan database baru.");
        }

        refreshDisplay();
    }

    private void refreshDisplay() {
        // Bab 9 (String): Pakai StringBuilder supaya penyusunan daftar antrean lebih efisien
        StringBuilder qText = new StringBuilder();
        int pos = 1;

        // Bab 10 (Generics & Collections): Iterasi elemen dari Queue antrean
        for (Patient p : manager.getWaitingQueue()) {
            qText.append(pos++).append(". ").append(p.toString()).append("\n");
        }

        areaQueue.setText(
                qText.length() == 0
                        ? "(Tidak ada pasien dalam antrean)"
                        : qText.toString());

        StringBuilder hText = new StringBuilder();

        // Bab 10 (Generics & Collections): Iterasi elemen dari ArrayList riwayat
        for (Patient p : manager.getPatientHistory()) {
            hText.append("• ").append(p.toString()).append("\n");
        }

        areaHistory.setText(
                hText.length() == 0
                        ? "(Belum ada riwayat pasien)"
                        : hText.toString());

        areaLog.setText(manager.getActivityLog());
    }
}