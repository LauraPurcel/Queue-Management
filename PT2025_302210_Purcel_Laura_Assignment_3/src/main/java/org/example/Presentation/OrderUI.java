package org.example.Presentation;

import org.example.Business.ClientBLL;
import org.example.Business.OrderBLL;
import org.example.Business.ProductBLL;
import org.example.Model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * GUI class for managing orders.
 * Allows the user to:
 * - Select a client and a product
 * - Enter quantity
 * - Place an order
 * - View existing orders
 */

public class OrderUI extends JFrame {

    private JTable clientTable, productTable, orderTable;
    private JTextField quantityField;
    private DefaultTableModel clientModel, productModel, orderModel;

    private ClientBLL clientBLL = new ClientBLL();
    private ProductBLL productBLL = new ProductBLL();
    private OrderBLL orderBLL = new OrderBLL();
    private MainUI mainUI;

    public OrderUI(MainUI mainUI) {
        this.mainUI = mainUI;
        setTitle("Order Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        quantityField = new JTextField(5);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);

        JButton placeOrderButton = new JButton("Place Order");
        JButton backButton = new JButton("Back");
        JButton exitButton = new JButton("Exit");

        inputPanel.add(placeOrderButton);
        inputPanel.add(backButton);
        inputPanel.add(exitButton);

        add(inputPanel, BorderLayout.NORTH);

        JPanel selectionPanel = new JPanel(new GridLayout(1, 2));

        clientModel = new DefaultTableModel();
        clientTable = new JTable(clientModel);
        selectionPanel.add(new JScrollPane(clientTable));

        productModel = new DefaultTableModel();
        productTable = new JTable(productModel);
        selectionPanel.add(new JScrollPane(productTable));

        add(selectionPanel, BorderLayout.CENTER);
        orderModel = new DefaultTableModel();
        orderTable = new JTable(orderModel);
        add(new JScrollPane(orderTable), BorderLayout.SOUTH);

        refreshClients();
        refreshProducts();
        refreshOrders();

        placeOrderButton.addActionListener(e -> placeOrder());
        backButton.addActionListener(e -> dispose());
        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    /**
     * Loads and displays all clients from the database.
     */
    private void refreshClients() {
        clientModel.setRowCount(0);
        List<Client> clients = clientBLL.findAllClients().stream().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).sorted((c1, c2) -> c1.getEmail().compareTo(c2.getEmail())).toList();
        DefaultTableModel refreshModel = clientBLL.getTableModel(clients);
        clientTable.setModel(refreshModel);
        clientModel = refreshModel;
    }

    /**
     * Loads and displays all products from the database.
     */
    private void refreshProducts() {
        productModel.setRowCount(0);
        List<Product> products = productBLL.findAllProducts().stream().filter(p1 -> p1.getQuantity() > 0).sorted((p1, p2) -> Integer.compare(p1.getId(), p2.getId())).toList();
        DefaultTableModel refreshModel = productBLL.getTableModel(products);
        productTable.setModel(refreshModel);
        productModel = refreshModel;
    }

    /**
     * Loads and displays all orders from the database.
     */
    private void refreshOrders() {
        orderModel.setRowCount(0);
        List<Order> orders = orderBLL.findAllOrders().stream().sorted((o2, o1) -> o1.getOrderDate().compareTo(o2.getOrderDate())).toList();
        DefaultTableModel refreshModel = orderBLL.getOrderTable(orders);
        orderTable.setModel(refreshModel);
        orderModel = refreshModel;
    }

    /**
     * Handles the logic for placing an order.
     * - Checks if a client and product are selected
     * - Validates the quantity
     * - Checks stock availability
     * - Updates product quantity in the database
     * - Inserts a new order
     * - Logs the bill in the Log table
     */
    private void placeOrder() {
        int clientRow = clientTable.getSelectedRow();
        int productRow = productTable.getSelectedRow();

        if (clientRow == -1 || productRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client and a product.");
            return;
        }

        try {
            int clientId = (int) clientModel.getValueAt(clientRow, 0);
            int productId = (int) productModel.getValueAt(productRow, 0);
            int availableQty = (int) productModel.getValueAt(productRow, 3);
            double price = (double) productModel.getValueAt(productRow, 1);
            int quantity = Integer.parseInt(quantityField.getText());
            if (quantity > availableQty) {
                JOptionPane.showMessageDialog(this, "Insufficient stock for selected product.");
                return;
            }
            productBLL.updateProduct(new Product(productId, price,
                    (String) productModel.getValueAt(productRow, 2),
                    availableQty - quantity));
            Order newOrder = new Order(0, clientId, productId, LocalDateTime.now(), quantity);
            orderBLL.insert(newOrder);

            double totalPrice = quantity * price;
            Bill bill = new Bill(
                    (int) orderModel.getValueAt(orderModel.getRowCount() - 1, 0),
                    clientId, productId, quantity, totalPrice, newOrder.getOrderDate());

            Log log = new Log();
            log.insertBill(bill);
            if (mainUI != null) {
                mainUI.populateLogTable();
            }
            JOptionPane.showMessageDialog(this, "Order placed successfully!");

            refreshProducts();
            refreshOrders();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity entered.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error placing order: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
