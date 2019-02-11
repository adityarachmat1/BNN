package com.bnn.Modal;

/**
 * Created by Ramdan Tri Kusumawijaya on 11/5/17.
 */

public class JumlahPeserta {

    private String Nama, Jenis;

    public JumlahPeserta() {
    }

    public JumlahPeserta(String nama, String jenis) {
        this.Nama = nama;
        this.Jenis = jenis;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        this.Nama = nama;
    }

    public String getJenis() {
        return Jenis;
    }

    public void setJenis(String jenis) {
        this.Jenis = jenis;
    }
}
