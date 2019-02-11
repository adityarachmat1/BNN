package com.bnn.Modal;

/**
 * Created by USER on 10/27/2017.
 */

public class MenuPencegahan {

    String noid, tanggal, judul, jenisMedia, pelaksana;

    public MenuPencegahan(String tanggal, String judul, String jenisMedia, String nomerid) {
        this.noid = nomerid;
        this.tanggal = tanggal;
        this.judul = judul;
        this.jenisMedia = jenisMedia;
    }

    public MenuPencegahan(String tanggal, String judul, String jenisMedia, String nomerid, String pelaksana) {
        this.noid = nomerid;
        this.tanggal = tanggal;
        this.judul = judul;
        this.jenisMedia = jenisMedia;
        this.pelaksana = pelaksana;
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

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getJenisMedia() {
        return jenisMedia;
    }

    public void setJenisMedia(String jenisMedia) {
        this.jenisMedia = jenisMedia;
    }

    public String getPelaksana() {
        return pelaksana;
    }

    public void setPelaksana(String pelaksana) {
        this.pelaksana = pelaksana;
    }
}
