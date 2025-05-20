package org.example.Business;

import org.example.DataAccess.OrderDAO;
import org.example.DataAccess.ProductDAO;
import org.example.Model.Order;
import org.example.Model.Product;
import org.example.Business.Validators.QuantityValidator;
import org.example.Business.Validators.Validator;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
/**
 * The OrderBLL (Business Logic Layer) class handles the logic for placing and managing orders.
 * It validates orders, updates product stock levels, and delegates data operations to DAOs.
 */
public class OrderBLL {
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final List<Validator<Order>> validators;

    /**
     * Constructs a new OrderBLL instance.
     * Initializes DAOs for orders and products and sets up order validators.
     */
    public OrderBLL() {
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
        this.validators = new ArrayList<>();
        validators.add(new QuantityValidator());
    }

    /**
     * Inserts a new order into the database after validation.
     * Also updates the product's available quantity.
     *
     * @param order The Order object to insert.
     * @return The inserted Order object.
     * @throws IllegalArgumentException if the order fails validation.
     */
    public Order insert(Order order) {
//        for (Validator<Order> validator : validators) {
//            validator.validate(order);
//        }
        validators.forEach(v -> v.validate(order));
        Product product = productDAO.findById(order.getProductId());
        product.setQuantity(product.getQuantity() - order.getQuantity());
        productDAO.update(product);
        return orderDAO.insert(order);
    }

    /**
     * Retrieves all orders from the database.
     *
     * @return A list of all Order objects.
     */
    public List<Order> findAllOrders() {
        return orderDAO.findAll();
    }

    /**
     * Creates a DefaultTableModel from a list of orders for display in a JTable.
     *
     * @param orders The list of Order objects.
     * @return A DefaultTableModel suitable for displaying orders in a table.
     */
    public DefaultTableModel getOrderTable(List<Order> orders) {
        return orderDAO.createTable(orders);
    }
}

