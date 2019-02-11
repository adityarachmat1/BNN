package com.bnn.Modal;

/**
 * Created by ferdinandprasetyo on 11/5/17.
 */

public class BarangBukti {

    private String kasusIdBarbuk, jenisBarang, jumlah, satuan, idBarang;

    public BarangBukti() {
    }

    public BarangBukti(String jenisBarang, String jumlah, String satuan) {
        this.jenisBarang = jenisBarang;
        this.jumlah = jumlah;
        this.satuan = satuan;
    }

    public BarangBukti(String jenisBarang, String jumlah, String satuan, String kasusIdBarbuk) {
        this.jenisBarang = jenisBarang;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.kasusIdBarbuk = kasusIdBarbuk;
    }

    public String getKasusIdBarbuk() {
        return kasusIdBarbuk;
    }

    public void setKasusIdBarbuk(String kasusIdBarbuk) {
        this.kasusIdBarbuk = kasusIdBarbuk;
    }

    public String getJenisBarang() {
        return jenisBarang;
    }

    public void setJenisBarang(String jenisBarang) {
        this.jenisBarang = jenisBarang;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
    }
}
