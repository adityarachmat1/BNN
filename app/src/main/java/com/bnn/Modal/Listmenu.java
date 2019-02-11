package com.bnn.Modal;

/**
 * Created by USER on 10/27/2017.
 */

public class Listmenu {

    String noid,tanggal, lkn, jenisbarang, lokasi, pelaksana, arrayStringBarbuk, arrayStringTersangka;

    public Listmenu (String tanggal,String lkn, String jenisbarang, String nomerid) {
        this.noid = nomerid;
        this.tanggal = tanggal;
        this.lkn = lkn;
        this.jenisbarang = jenisbarang;
    }

    public Listmenu (String tanggal,String lkn, String jenisbarang, String nomerid, String lokasi, String pelaksana) {
        this.noid = nomerid;
        this.tanggal = tanggal;
        this.lkn = lkn;
        this.jenisbarang = jenisbarang;
        this.lokasi = lokasi;
        this.pelaksana = pelaksana;
    }

    public Listmenu (String tanggal,String lkn, String jenisbarang, String nomerid, String lokasi,
                     String pelaksana, String arrayStringBarbuk, String arrayStringTersangka) {
        this.noid = nomerid;
        this.tanggal = tanggal;
        this.lkn = lkn;
        this.jenisbarang = jenisbarang;
        this.lokasi = lokasi;
        this.pelaksana = pelaksana;
        this.arrayStringBarbuk = arrayStringBarbuk;
        this.arrayStringTersangka = arrayStringTersangka;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getNoid() {
        return noid;
    }

    public void setNoid(String noid) {
        this.noid = noid;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getLkn() {
        return lkn;
    }

    public void setLkn(String lkn) {
        this.lkn = lkn;
    }

    public String getJenisbarang() {
        return jenisbarang;
    }

    public void setJenisbarang(String jenisbarang) {
        this.jenisbarang = jenisbarang;
    }

    public String getPelaksana() {
        return pelaksana;
    }

    public void setPelaksana(String pelaksana) {
        this.pelaksana = pelaksana;
    }

    public String getArrayStringBarbuk() {
        return arrayStringBarbuk;
    }

    public void setArrayStringBarbuk(String arrayStringBarbuk) {
        this.arrayStringBarbuk = arrayStringBarbuk;
    }

    public String getArrayStringTersangka() {
        return arrayStringTersangka;
    }

    public void setArrayStringTersangka(String arrayStringTersangka) {
        this.arrayStringTersangka = arrayStringTersangka;
    }
}
