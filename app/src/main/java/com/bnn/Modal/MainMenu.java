package com.bnn.Modal;


public class MainMenu {
    String id, nama;
    int img, menuId;

    public MainMenu(String id, String nama, int img) {
        this.id = id;
        this.nama = nama;
        this.img = img;
    }

    public MainMenu(int menuId, String nama, int img) {
        this.menuId = menuId;
        this.nama = nama;
        this.img = img;
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

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}
