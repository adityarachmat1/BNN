package com.bnn.Modal;

/**
 * Created by ferdinandprasetyo on 11/9/17.
 */

public class Tersangka {
    private String id, nama, jekel, wargaNegara, tempatLahir, tanggalLahir, kodeNegara;

    public Tersangka() {
    }

    public Tersangka(String nama, String jekel) {
        this.nama = nama;
        this.jekel = jekel;
    }

    public Tersangka(String nama, String jekel, String kodeNegara, String tempatLahir) {
        this.nama = nama;
        this.jekel = jekel;
        this.kodeNegara = kodeNegara;
        this.tempatLahir = tempatLahir;
    }

    public Tersangka(String nama, String jekel, String kodeNegara, String tempatLahir, String tanggalLahir) {
        this.nama = nama;
        this.jekel = jekel;
        this.kodeNegara = kodeNegara;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
    }

    public Tersangka(String id, String nama, String jekel, String kodeNegara, String tempatLahir, String tanggalLahir) {
        this.id = id;
        this.nama = nama;
        this.jekel = jekel;
        this.kodeNegara = kodeNegara;
        this.tempatLahir = tempatLahir;
        this.tanggalLahir = tanggalLahir;
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

    public String getJekel() {
        return jekel;
    }

    public void setJekel(String jekel) {
        this.jekel = jekel;
    }

    public String getKodeNegara() {
        return kodeNegara;
    }

    public void setKodeNegara(String kodeNegara) {
        this.kodeNegara = kodeNegara;
    }

    public String getWargaNegara() {
        return wargaNegara;
    }

    public void setWargaNegara(String wargaNegara) {
        this.wargaNegara = wargaNegara;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
}
