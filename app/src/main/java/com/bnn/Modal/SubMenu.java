package com.bnn.Modal;

/**
 * Created by USER on 10/25/2017.
 */

public class SubMenu {

    String id, namakategory;
    int menuId;


    public SubMenu (String id, String nama) {
        this.id = id;
        this.namakategory = nama;
    }

    public SubMenu (int menuId, String nama) {
        this.menuId = menuId;
        this.namakategory = nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getnamaKategory() {
        return namakategory;
    }

    public void setNamaKategory(String nama) {
        this.namakategory = nama;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}

