
package klinik;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApp {
    public static void main(String[] args) {
        // Menjalankan aplikasi GUI pada Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Mengatur tampilan aplikasi agar mengikuti tampilan sistem operasi
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            // Membuat dan menampilkan jendela utama aplikasi klinik
            ClinicFrame frame = new ClinicFrame();
            frame.setVisible(true);
        });
    }
}
