package com.bnn.Modal;

/**
 * Created by ferdinandprasetyo on 11/7/17.
 */

public class Peserta {

    private String nama, jenisKelamin;

    public Peserta() {
    }

    public Peserta(String nama, String jenisKelamin) {
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }
}
