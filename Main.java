import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; // Manipulasi Date (Poin C.2.e)
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    // Scanner untuk input user
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Exception Handling (Poin C.2.f) untuk mencegah error fatal
        try {
            while (true) {
                System.out.println("\n=== SISTEM PEMINJAMAN MAPALA UNAND ===");
                System.out.println("1. Lihat Daftar Alat");
                System.out.println("2. Tambah Peminjaman");
                System.out.println("3. Kembalikan Alat");
                System.out.println("4. Hapus/Batal Peminjaman");
                System.out.println("5. Keluar");
                System.out.print("Pilih menu: ");
                
                int menu = scanner.nextInt();
                scanner.nextLine(); 

                if (menu == 1) {
                    tampilkanAlat();
                } else if (menu == 2) {
                    tambahPeminjaman();
                } else if (menu == 3) {
                    kembalikanAlat();
                } else if (menu == 4) {
                    batalkanPeminjaman(); 
                } else if (menu == 5) {
                    System.out.println("Program selesai.");
                    break;
                } else {
                    System.out.println("Menu tidak valid.");
                }
            }
        } catch (Exception e) {
            System.out.println("Terjadi Error pada sistem: " + e.getMessage());
        }
    }

    // Method Menampilkan Data (Read - CRUD)
    public static void tampilkanAlat() {
        System.out.println("\n--- DAFTAR ALAT GUNUNG ---");
        try {
            Connection conn = Koneksi.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM alat");

            while (rs.next()) {
                System.out.println(rs.getInt("id_alat") + ". " + 
                                   rs.getString("nama_alat") + 
                                   " (" + rs.getString("jenis") + ") - Stok: " + 
                                   rs.getInt("stok") + " | Sewa: Rp" + 
                                   rs.getDouble("harga_sewa") + "/hari");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method CREATE: Menambah data peminjaman dengan Jumlah Barang & Update Stok
    public static void tambahPeminjaman() {
        try {
            System.out.println("\n--- FORM PEMINJAMAN ---");
            System.out.print("Nama Peminjam: ");
            String nama = scanner.nextLine();
            
            tampilkanAlat(); // Tampilkan daftar alat
            System.out.print("Pilih ID Alat: ");
            int idAlat = scanner.nextInt();
            
            Connection conn = Koneksi.getKoneksi();
            
            // 1. Cek Harga dan Stok barang dulu
            PreparedStatement pStmt = conn.prepareStatement("SELECT harga_sewa, stok, nama_alat FROM alat WHERE id_alat = ?");
            pStmt.setInt(1, idAlat);
            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
                double harga = rs.getDouble("harga_sewa");
                int stokTersedia = rs.getInt("stok");
                String namaAlat = rs.getString("nama_alat");

                // Input Jumlah Barang
                System.out.print("Jumlah yang dipinjam (" + namaAlat + "): ");
                int jumlah = scanner.nextInt();

                // VALIDASI: Cek apakah stok cukup?
                if (jumlah > stokTersedia) {
                    System.out.println("❌ Gagal! Stok tidak cukup. Sisa stok hanya: " + stokTersedia);
                    return; // Stop proses jika stok kurang
                }

                System.out.print("Lama Pinjam (hari): ");
                int lama = scanner.nextInt();

                // RUMUS: Harga x Lama x Jumlah
                double total = harga * lama * jumlah; 

                // 2. Simpan ke Tabel Peminjaman
                String sqlSimpan = "INSERT INTO peminjaman (nama_peminjam, id_alat, tgl_pinjam, total_biaya, status, jumlah) VALUES (?, ?, NOW(), ?, 'Dipinjam', ?)";
                PreparedStatement simpan = conn.prepareStatement(sqlSimpan);
                simpan.setString(1, nama);
                simpan.setInt(2, idAlat);
                simpan.setDouble(3, total);
                simpan.setInt(4, jumlah); // Masukkan jumlah ke database
                simpan.executeUpdate();

                // 3. Update (Kurangi) Stok di Tabel Alat
                String sqlKurangiStok = "UPDATE alat SET stok = stok - ? WHERE id_alat = ?";
                PreparedStatement updateStok = conn.prepareStatement(sqlKurangiStok);
                updateStok.setInt(1, jumlah);
                updateStok.setInt(2, idAlat);
                updateStok.executeUpdate();

                System.out.println("✅ Peminjaman Berhasil!");
                System.out.println("Total Biaya: Rp" + total + " (untuk " + jumlah + " barang)");
                System.out.println("Stok " + namaAlat + " telah dikurangi.");
            } else {
                System.out.println("ID Alat tidak ditemukan.");
            }

        } catch (SQLException e) {
            System.out.println("Gagal transaksi: " + e.getMessage());
        }
    }

    // Method UPDATE: Mengembalikan alat, Hitung Denda, dan BALIKIN STOK
    public static void kembalikanAlat() {
        try {
            System.out.println("\n--- PENGEMBALIAN ALAT ---");
            System.out.print("Masukkan ID Peminjaman: ");
            int idPinjam = scanner.nextInt();

            Connection conn = Koneksi.getKoneksi();
            
            // 1. Ambil data peminjaman (termasuk JUMLAH dan ID_ALAT)
            String sqlCek = "SELECT p.total_biaya, p.jumlah, p.id_alat, p.status, a.harga_sewa, a.nama_alat " +
                            "FROM peminjaman p " +
                            "JOIN alat a ON p.id_alat = a.id_alat WHERE p.id_pinjam = ?";
            
            PreparedStatement pst = conn.prepareStatement(sqlCek);
            pst.setInt(1, idPinjam);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                
                // Cek apakah sudah pernah dikembalikan?
                if ("Kembali".equalsIgnoreCase(status)) {
                    System.out.println("❌ Barang ini sudah dikembalikan sebelumnya!");
                    return;
                }

                double biayaAwal = rs.getDouble("total_biaya");
                double hargaSewa = rs.getDouble("harga_sewa");
                int jumlahPinjam = rs.getInt("jumlah"); // Ambil jumlah barang yg dipinjam
                int idAlat = rs.getInt("id_alat");
                String namaAlat = rs.getString("nama_alat");

                System.out.println("Barang: " + namaAlat + " (Jumlah: " + jumlahPinjam + ")");
                System.out.print("Keterlambatan (hari, 0 jika tepat waktu): ");
                int telat = scanner.nextInt();

                // Hitung Denda
                double denda = 0;
                if (telat > 0) {
                    if (hargaSewa <= 10000) denda = 2000 * telat * jumlahPinjam;
                    else if (hargaSewa <= 25000) denda = 3000 * telat * jumlahPinjam;
                    else denda = 5000 * telat * jumlahPinjam;
                }

                double totalBayar = biayaAwal + denda;

                // 2. Update Status Peminjaman
                String sqlUpdate = "UPDATE peminjaman SET tgl_kembali = NOW(), status = 'Kembali', total_biaya = ? WHERE id_pinjam = ?";
                PreparedStatement up = conn.prepareStatement(sqlUpdate);
                up.setDouble(1, totalBayar);
                up.setInt(2, idPinjam);
                up.executeUpdate();

                // 3. UPDATE STOK: Balikin barang ke gudang (Stok + Jumlah)
                String sqlBalikinStok = "UPDATE alat SET stok = stok + ? WHERE id_alat = ?";
                PreparedStatement balikin = conn.prepareStatement(sqlBalikinStok);
                balikin.setInt(1, jumlahPinjam); // Jumlah yang dikembalikan
                balikin.setInt(2, idAlat);       // Ke alat yang mana
                balikin.executeUpdate();

                System.out.println("✅ Pengembalian Selesai.");
                System.out.println("Stok " + namaAlat + " telah bertambah " + jumlahPinjam + " unit.");
                System.out.println("Total Bayar (Termasuk Denda): Rp" + totalBayar);
            } else {
                System.out.println("ID Peminjaman tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    } // Method Tambahan: Menghitung Denda (Logic if-else bertingkat)
    public static double hitungDenda(double hargaSewa, int hariTerlambat) {
        double tarifDenda = 0;

        // Logika sesuai permintaan:
        if (hargaSewa <= 10000) {
            tarifDenda = 2000;
        } else if (hargaSewa > 10000 && hargaSewa <= 25000) {
            tarifDenda = 3000;
        } else if (hargaSewa >= 26000) { // 26000 ke atas
            tarifDenda = 5000;
        }

        return tarifDenda * hariTerlambat;
    }
    // Method DELETE: Menghapus data peminjaman (Cancel Transaksi)
    public static void batalkanPeminjaman() {
        try {
            System.out.println("\n--- BATALKAN / HAPUS PEMINJAMAN ---");
            System.out.print("Masukkan ID Peminjaman yang akan dihapus: ");
            int idPinjam = scanner.nextInt();

            Connection conn = Koneksi.getKoneksi();

            // 1. Cek dulu data transaksinya sebelum dihapus
            String sqlCek = "SELECT * FROM peminjaman WHERE id_pinjam = ?";
            PreparedStatement pstCek = conn.prepareStatement(sqlCek);
            pstCek.setInt(1, idPinjam);
            ResultSet rs = pstCek.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                int idAlat = rs.getInt("id_alat");
                int jumlah = rs.getInt("jumlah");

                // LOGIKA PENTING:
                // Jika barang statusnya masih 'Dipinjam', berarti stoknya sedang di luar.
                // Kalau transaksinya dihapus/batal, stok harus balik ke gudang dulu.
                if ("Dipinjam".equalsIgnoreCase(status)) {
                    String sqlBalikinStok = "UPDATE alat SET stok = stok + ? WHERE id_alat = ?";
                    PreparedStatement pstStok = conn.prepareStatement(sqlBalikinStok);
                    pstStok.setInt(1, jumlah);
                    pstStok.setInt(2, idAlat);
                    pstStok.executeUpdate();
                    System.out.println("ℹ️ Info: Stok barang telah dikembalikan ke gudang.");
                }

                // 2. Hapus Data Peminjaman (DELETE)
                String sqlDelete = "DELETE FROM peminjaman WHERE id_pinjam = ?";
                PreparedStatement pstDel = conn.prepareStatement(sqlDelete);
                pstDel.setInt(1, idPinjam);
                pstDel.executeUpdate();

                System.out.println("✅ Data Peminjaman ID " + idPinjam + " berhasil dihapus permanen.");
            } else {
                System.out.println("ID Peminjaman tidak ditemukan.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}