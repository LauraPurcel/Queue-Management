package org.example.Presentation;

import org.example.Model.Bill;
import org.example.Model.Log;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * The main window of the application.
 * Provides access to Clients, Products, and Orders UIs,
 * and displays the history of orders (bills) in a table.
 */
public class MainUI extends JFrame {

    private JTable logTable;
    /**
     * Constructs the main user interface window.
     * Initializes buttons for navigating to Clients, Products, and Orders interfaces.
     * Also displays the order log (bill history) in a table in the center of the window.
     */
    public MainUI() {
        setTitle("Main UI");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setVisible(true);
        JButton clientButton = new JButton("Clients");
        JButton productButton = new JButton("Products");
        JButton orderButton = new JButton("Orders");

        clientButton.addActionListener(e -> new ClientUI());
        productButton.addActionListener(e -> new ProductUI());
        orderButton.addActionListener(e -> new OrderUI(this));

        JPanel topPanel = new JPanel();
        topPanel.add(clientButton);
        topPanel.add(productButton);
        topPanel.add(orderButton);

        add(topPanel, BorderLayout.NORTH);

        logTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(logTable);
        add(scrollPane, BorderLayout.CENTER);

        populateLogTable();
        setVisible(true);
    }

    /**
     * Populates the provided JTable with data from the bill log.
     * Each row represents an order previously placed in the system.
     *
     */
    void populateLogTable() {
        Log log = new Log();
        List<Bill> billList = log.findAll();

        String[] columnNames = {"Order ID", "Client ID", "Product ID", "Quantity", "Total Price", "Timestamp"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Bill bill : billList) {
            Object[] row = {
                    bill.orderId(),
                    bill.clientId(),
                    bill.productId(),
                    bill.quantity(),
                    bill.totalPrice(),
                    bill.timestamp()
            };
            model.addRow(row);
        }
        logTable.setModel(model);
    }

    /**
     * The entry point of the application.
     * Launches the main UI window.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new MainUI();
    }
}
