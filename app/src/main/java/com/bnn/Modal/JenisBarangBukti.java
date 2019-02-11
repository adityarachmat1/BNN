package com.bnn.Modal;

/**
 * Created by ferdinandprasetyo on 11/9/17.
 */

public class JenisBarangBukti {

    String id, nama, namaJenis;

    public JenisBarangBukti() {
    }

    public JenisBarangBukti(String id, String nama, String namaJenis) {
        this.id = id;
        this.nama = nama;
        this.namaJenis = namaJenis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNamaJenis() {
        return namaJenis;
    }

    public void setNamaJenis(String namaJenis) {
        this.namaJenis = namaJenis;
    }

    @Override
    public String toString() {
        return this.nama;
    }
}
