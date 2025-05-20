package org.example.Presentation;
import org.example.Business.ClientBLL;
import org.example.Model.Client;
import org.example.DataAccess.ClientDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;

/**
 * GUI class for managing clients.
 * Allows users to add, update, delete, and view client information in a table interface.
 */
public class ClientUI extends JFrame {
    private JTextField idField, nameField, addressField, emailField, ageField;
    private JTable table;
    private DefaultTableModel model;
    //private ClientDAO clientDAO = new ClientDAO();
    private ClientBLL clientBLL = new ClientBLL();
    /**
     * Constructs the ClientUI window and initializes the user interface components.
     */
    public ClientUI() {
        setTitle("Client Management");
        setSize(800, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel();
        idField = new JTextField(5);
        nameField = new JTextField(10);
        addressField = new JTextField(10);
        emailField = new JTextField(10);
        ageField = new JTextField(5);
        idField.setEditable(false);

        inputPanel.add(new JLabel("ID"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Address"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Email"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Age"));
        inputPanel.add(ageField);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton backButton = new JButton("Back");
        JButton exitButton = new JButton("Exit");
        JButton refreshFieldsButton = new JButton("Clear Fields");
        refreshFieldsButton.addActionListener(e -> clearTextFields());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshFieldsButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        buttonPanel.add(exitButton);

        List<Client> clients = clientBLL.findAllClients().stream().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).sorted((c1, c2) -> c1.getEmail().compareTo(c2.getEmail())).toList();
        model = clientBLL.getTableModel(clients);
        table = new JTable(model);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                idField.setText(model.getValueAt(row, 0).toString());
                nameField.setText(model.getValueAt(row, 1).toString());
                addressField.setText(model.getValueAt(row, 2).toString());
                emailField.setText(model.getValueAt(row, 3).toString());
                ageField.setText(model.getValueAt(row, 4).toString());
            }
        });

        addButton.addActionListener(e -> {
            Client c = new Client(
                    0,
                    nameField.getText(),
                    addressField.getText(),
                    emailField.getText(),
                    Integer.parseInt(ageField.getText())
            );
            clientBLL.insertClient(c);//clientDAO.insert(c);
            refreshTable();
        });

        updateButton.addActionListener(e -> {
            Client c = new Client(
                    Integer.parseInt(idField.getText()),
                    nameField.getText(),
                    addressField.getText(),
                    emailField.getText(),
                    Integer.parseInt(ageField.getText())
            );
            clientBLL.updateClient(c);//clientDAO.update(c);
            refreshTable();
        });

        deleteButton.addActionListener(e -> {
            int id = Integer.parseInt(idField.getText());
            clientBLL.deleteClient(id);//clientDAO.delete(id);
            refreshTable();
        });

        backButton.addActionListener(e -> dispose());
        exitButton.addActionListener(e -> System.exit(0));

        add(inputPanel, "North");
        add(new JScrollPane(table), "Center");
        add(buttonPanel, "South");
        setVisible(true);
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearTextFields() {
        idField.setText("");
        nameField.setText("");
        addressField.setText("");
        emailField.setText("");
        ageField.setText("");
    }

    /**
     * Refreshes the client table by reloading data from the database.
     */
    private void refreshTable() {
        model.setRowCount(0);
        List<Client> clients = clientBLL.findAllClients().stream().sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).sorted((c1, c2) -> c1.getEmail().compareTo(c2.getEmail())).toList();
        DefaultTableModel refreshModel = clientBLL.getTableModel(clients);
        table.setModel(refreshModel);
        model = refreshModel;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
