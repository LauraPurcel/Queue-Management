package org.example.Model;
/**
 * Represents a product that can be ordered by clients.
 */
public class Product {
    private int id;
    private double price;
    private String name;
    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    public Product(){

    }

    public Product(int id, double price, String name, int quantity) {
        super();
        this.id = id;
        this.price = price;
        this.name = name;
        this.quantity = quantity;
    }

    public Product(int id, String name, double price, int quantity) {
        super();
        this.id = id;
        this.price = price;
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
