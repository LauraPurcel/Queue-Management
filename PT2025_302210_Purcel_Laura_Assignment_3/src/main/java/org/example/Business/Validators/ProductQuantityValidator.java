package org.example.Business.Validators;

import org.example.Model.Product;

public class ProductQuantityValidator implements Validator<Product> {
    private final int maxQuantity;

    public ProductQuantityValidator(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    @Override
    public void validate(Product product) {
        int quantity = product.getQuantity();
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (quantity > maxQuantity) {
            throw new IllegalArgumentException("Quantity exceeds the maximum allowed value of " + maxQuantity);
        }
    }
}
