package org.example.Business.Validators;

import org.example.Model.Product;

public class ProductPriceValidator implements Validator<Product>{
    private final double maxPrice;

    public ProductPriceValidator(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public void validate(Product product) {
        double price = product.getPrice();
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }
        if (price > maxPrice) {
            throw new IllegalArgumentException("Price exceeds the maximum allowed value of " + maxPrice);
        }
    }
}
