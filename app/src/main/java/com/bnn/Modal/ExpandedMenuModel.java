package com.bnn.Modal;


public class ExpandedMenuModel {

    private int menuId;
    private String menuName = "";
    private int menuIconImg = -1; // menu icon resource id
    private int visibility = 0;

    public ExpandedMenuModel(int menuId, String menuName, int menuIconImg) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuIconImg = menuIconImg;
    }

    public ExpandedMenuModel(int menuId, String menuName, int menuIconImg, int visibility) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuIconImg = menuIconImg;
        this.visibility = visibility;
    }

    public int getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getMenuIconImg() {
        return menuIconImg;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}
