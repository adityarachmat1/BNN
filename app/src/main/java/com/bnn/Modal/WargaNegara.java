package com.bnn.Modal;

/**
 * Created by ferdinandprasetyo on 11/15/17.
 */

public class WargaNegara {
    private String kode, wargaNegara;

    public WargaNegara() {
    }

    public WargaNegara(String kode, String wargaNegara) {
        this.kode = kode;
        this.wargaNegara = wargaNegara;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getWargaNegara() {
        return wargaNegara;
    }

    public void setWargaNegara(String wargaNegara) {
        this.wargaNegara = wargaNegara;
    }

    @Override
    public String toString() {
        return this.wargaNegara;
    }
}
