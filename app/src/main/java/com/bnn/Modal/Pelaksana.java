package com.bnn.Modal;

/**
 * Created by ferdinandprasetyo on 11/9/17.
 */

public class Pelaksana {

    String id, nama, provinsi, kota;

    public Pelaksana() {
    }

    public Pelaksana(String id, String nama, String provinsi, String kota) {
        this.id = id;
        this.nama = nama;
        this.provinsi = provinsi;
        this.kota = kota;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
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

    @Override
    public String toString() {
        return this.nama;
    }
}
