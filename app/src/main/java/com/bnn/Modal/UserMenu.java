package com.bnn.Modal;

/**
 * Created by ferdinandprasetyo on 11/23/17.
 */

public class UserMenu {
    private int menuId;
    private String menuTitle;

    public UserMenu(int menuId, String menuTitle) {
        this.menuId = menuId;
        this.menuTitle = menuTitle;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }
}
