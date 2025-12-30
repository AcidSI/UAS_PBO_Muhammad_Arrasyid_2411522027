import java.sql.Connection;
import java.sql.DriverManager;

public class Koneksi {
    private static Connection connect;

    // Method statis agar bisa dipanggil langsung tanpa membuat objek baru
    public static Connection getKoneksi() {
        if (connect == null) {
            try {
                // Konfigurasi URL Database: localhost port 3306, nama db: db_mapala_unand
                String url = "jdbc:mysql://localhost:3306/db_mapala_unand";
                String user = "root"; // Default user database
                String password = ""; // Password kosong (default XAMPP/Laragon)

                // Melakukan koneksi ke Driver MySQL
                connect = DriverManager.getConnection(url, user, password);
                System.out.println("Status: Terhubung ke Database.");
                
            } catch (Exception e) {
                // Menangkap error jika server database mati atau url salah
                System.out.println("Koneksi Gagal: " + e.getMessage());
            }
        }
        return connect;
    }
}