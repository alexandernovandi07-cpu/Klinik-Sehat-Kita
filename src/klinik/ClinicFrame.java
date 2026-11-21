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
        loadInitialData();
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