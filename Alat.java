
// Ini adalah Class Model untuk memenuhi syarat Konsep Objek & Encapsulation
public class Alat {
    private int id;
    private String nama;
    private String jenis;
    private int stok;
    private double hargaSewa;

    // Constructor
    public Alat(int id, String nama, String jenis, int stok, double hargaSewa) {
        this.id = id;
        this.nama = nama;
        this.jenis = jenis;
        this.stok = stok;
        this.hargaSewa = hargaSewa;
    }

    // Getter methods (Encapsulation)
    public String getNama() { return nama; }
    public int getStok() { return stok; }
    public double getHarga() { return hargaSewa; }
}