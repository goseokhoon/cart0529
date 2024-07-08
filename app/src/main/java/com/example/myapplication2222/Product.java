package com.example.myapplication2222;

public class Product {

    private String id; // 상품 ID
    private String name; // 상품 이름
    private double price; // 상품 가격
    private int quantity; // 상품 수량

    public Product(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
