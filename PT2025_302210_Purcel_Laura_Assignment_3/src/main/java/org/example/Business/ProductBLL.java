package org.example.Business;

import org.example.Business.Validators.ProductPriceValidator;
import org.example.Business.Validators.ProductQuantityValidator;
import org.example.Business.Validators.Validator;
import org.example.DataAccess.ProductDAO;
import org.example.Model.Product;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * The ProductBLL (Business Logic Layer) class handles operations related to product management.
 * It includes validation logic and delegates database operations to the ProductDAO.
 */
public class ProductBLL {
    private final List<Validator<Product>> validators = new ArrayList<>();
    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Constructs a new ProductBLL instance.
     * Initializes the list of product validators, including price and quantity constraints.
     */
    public ProductBLL() {
        validators.add(new ProductPriceValidator(10000.0));
        validators.add(new ProductQuantityValidator(1000));
    }

    /**
     * Inserts a new product into the database after validation.
     *
     * @param product The Product object to be inserted.
     * @return The inserted Product object.
     * @throws IllegalArgumentException if validation fails.
     */
    public Product insertProduct(Product product) {
//        for (Validator<Product> v : validators) {
//            v.validate(product);
//        }
        validators.forEach(v -> v.validate(product));
        return productDAO.insert(product);
    }

    /**
     * Updates an existing product in the database after validation.
     *
     * @param product The Product object with updated fields.
     * @return The updated Product object.
     * @throws IllegalArgumentException if validation fails.
     */
    public Product updateProduct(Product product) {
//        for (Validator<Product> v : validators) {
//            v.validate(product);
//        }
        validators.forEach(v -> v.validate(product));
        return productDAO.update(product);
    }

    /**
     * Deletes a product from the database by ID.
     *
     * @param id The ID of the product to delete.
     */
    public void deleteProduct(int id) {
        productDAO.delete(id);
    }

    /**
     * Retrieves all products from the database.
     *
     * @return A list of all Product objects.
     */
    public List<Product> findAllProducts() {
        return productDAO.findAll();
    }

    /**
     * Creates a DefaultTableModel representation for a list of products.
     * Used for displaying product data in a JTable.
     *
     * @param products The list of Product objects.
     * @return A DefaultTableModel based on the given products.
     */
    public DefaultTableModel getTableModel(List<Product> products) {
        return productDAO.createTable(products);
    }
}
