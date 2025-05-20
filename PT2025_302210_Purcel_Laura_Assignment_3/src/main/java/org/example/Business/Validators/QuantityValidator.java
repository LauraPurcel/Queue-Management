package org.example.Business.Validators;

import org.example.DataAccess.ProductDAO;
import org.example.Model.Order;
import org.example.Model.Product;

public class QuantityValidator implements Validator<Order> {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    public void validate(Order order) {
        Product product = productDAO.findById(order.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product with ID " + order.getProductId() + " not found.");
        }
        if (order.getQuantity() > product.getQuantity()) {
            throw new IllegalArgumentException("Ordered quantity exceeds available stock.");
        }
    }
}
