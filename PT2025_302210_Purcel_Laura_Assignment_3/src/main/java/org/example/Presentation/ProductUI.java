package org.example.Presentation;

import org.example.Business.ProductBLL;
import org.example.Model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
/**
 * ProductUI is the graphical user interface for managing products.
 * It allows users to add, update, delete, and view product records.
 */
public class ProductUI extends JFrame {
    private JTextField idField, nameField, priceField, quantityField;
    private JTable table;
    private DefaultTableModel tableModel;
    private final ProductBLL productBLL;

    public ProductUI() {
        super("Product Management");
        productBLL = new ProductBLL();

        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        idField = new JTextField(10);
        nameField = new JTextField(10);
        priceField = new JTextField(10);
        quantityField = new JTextField(10);
        idField.setEditable(false);

        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton backButton = new JButton("Back");
        JButton clearButton = new JButton("Clear Fields");

        clearButton.addActionListener(e -> clearTextFields());
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        List<Product> products = productBLL.findAllProducts().stream().sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).toList();
        tableModel = productBLL.getTableModel(products);
        table = new JTable(tableModel);

        DefaultTableCellRenderer topAlign = new DefaultTableCellRenderer();
        topAlign.setVerticalAlignment(SwingConstants.TOP);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(topAlign);
        }

        table.getSelectionModel().addListSelectionListener(e -> loadSelectedProduct());

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();
        setVisible(true);
    }

    /**
     * Refreshes the product table by retrieving and displaying the updated list of products.
     * The products are sorted alphabetically by name.
     */

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Product> products = productBLL.findAllProducts().stream().sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).toList();
        DefaultTableModel refreshed = productBLL.getTableModel(products);
        table.setModel(refreshed);
        tableModel = refreshed;
    }

    /**
     * Clears all the text fields in the input panel.
     * Useful for resetting the form after adding or updating a product.
     */

    private void clearTextFields() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
    }

    /**
     * Loads the product data from the selected row in the table into the input fields.
     * Allows the user to view or edit the selected product.
     */

    private void loadSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            priceField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            quantityField.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
    }

    /**
     * Creates a new product from the input fields and inserts it into the database.
     * Then refreshes the table to display the new product.
     * Assumes that the input values are valid and properly formatted.
     */

    private void addProduct() {
        Product product = new Product(
                0,
                Double.parseDouble(priceField.getText()),
                nameField.getText(),
                Integer.parseInt(quantityField.getText())
        );
        productBLL.insertProduct(product);
        refreshTable();
    }

    /**
     * Updates the selected product in the database using the data from the input fields.
     * Then refreshes the table to reflect the changes.
     * Assumes that the input values are valid and properly formatted.
     */
    private void updateProduct() {
        Product product = new Product(
                Integer.parseInt(idField.getText()),
                Double.parseDouble(priceField.getText()),
                nameField.getText(),
                Integer.parseInt(quantityField.getText())
        );
        productBLL.updateProduct(product);
        refreshTable();
    }

    /**
     * Deletes the currently selected product from the database based on its ID.
     * Then refreshes the table to remove the deleted product.
     */

    private void deleteProduct() {
        int id = Integer.parseInt(idField.getText());
        productBLL.deleteProduct(id);
        refreshTable();
    }
}
