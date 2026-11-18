package klinik;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.Border;

public class ClinicFrame extends JFrame {
    private final ClinicManager manager = new ClinicManager();

    // GUI Components (Week 13)
    private final JTextField txtName = new JTextField();
    private final JTextField txtAge = new JTextField();
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

    // Border normal vs border merah terang (kompatibel dengan macOS)
    private final Border normalBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 185, 190), 1),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)
    );

    private final Border errorBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED, 2),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
    );

    // WEEK 13: Custom ImageIcon untuk pop-up Berhasil (Centang Hijau) & Gagal (Tanda Seru)
    private final ImageIcon successIcon = createSuccessIcon();
    private final ImageIcon warningIcon = createWarningIcon();

    private final JTextArea areaQueue = new JTextArea();
    private final JTextArea areaHistory = new JTextArea();
    private final JTextArea areaLog = new JTextArea(5, 40);
    private final JLabel lblClockAndThread = new JLabel("Memuat jam sistem...");

    private volatile boolean runningThread = true;

    public ClinicFrame() {
        setTitle("Sistem Antrean & Administrasi Klinik SehatKita");
        setSize(1140, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

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
}