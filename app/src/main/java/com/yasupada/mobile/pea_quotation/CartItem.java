package com.yasupada.mobile.pea_quotation;

public class CartItem {

    private boolean checked;
    private String name;
    private double price;
    private int quantity;

    public CartItem(boolean check, String name, double price) {
        this.checked = check;
        this.name = name ;
        this.price = price;
        this.quantity = 1; // Default quantity is 1
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isSelected() {
        if(this.checked) {
            return true;
        }else{
            return false;
        }

    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSelected(boolean isChecked) {
        this.checked = isChecked;
    }
}