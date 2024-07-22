package com.example.myapplication2222;

public class InventoryItem {
    private String name;
    private int price;
    private int stock;

    // 기본 생성자 필요 (Firestore에서 사용)
    public InventoryItem() {}

    public InventoryItem(String name, int price, int stock) {

        this.name = name;
        this.price = price;
        this.stock = stock;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setQuantity(int stock) {
        this.stock= stock;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
