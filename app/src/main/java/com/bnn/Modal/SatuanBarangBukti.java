package com.bnn.Modal;

/**
 * Created by ferdinandprasetyo on 11/9/17.
 */

public class SatuanBarangBukti {

    private String kode, nama;

    public SatuanBarangBukti() {
    }

    public SatuanBarangBukti(String kode, String nama) {
        this.kode = kode;
        this.nama = nama;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    @Override
    public String toString() {
        return this.nama;
    }
}
