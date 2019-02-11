package com.bnn.Modal;

/**
 * Created by USER on 10/27/2017.
 */

public class ExpandedSubmenu {

    String id;
    private String menuName = "";
    private int menuIconImg = -1, menuId; // menu icon resource id

    public ExpandedSubmenu(String id, String menuName) {
        this.id = id;
        this.menuName = menuName;
    }

    public ExpandedSubmenu(int menuId, String menuName) {
        this.menuId = menuId;
        this.menuName = menuName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}
