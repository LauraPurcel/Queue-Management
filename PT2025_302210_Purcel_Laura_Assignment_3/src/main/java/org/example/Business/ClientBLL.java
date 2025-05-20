package org.example.Business;

import org.example.DataAccess.ClientDAO;
import org.example.Model.Client;
import org.example.Business.Validators.EmailValidator;
import org.example.Business.Validators.ClientAgeValidator;
import org.example.Business.Validators.Validator;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
/**
 * The ClientBLL (Business Logic Layer) class handles the core logic for managing clients.
 * It validates client data before performing operations through the ClientDAO.
 */
public class ClientBLL {
    private final List<Validator<Client>> validators;
    private final ClientDAO clientDAO;

    /**
     * Constructs a new ClientBLL instance.
     * Initializes validators for client email and age, and sets up the data access object.
     */
    public ClientBLL() {
        validators = new ArrayList<>();
        validators.add(new EmailValidator());
        validators.add(new ClientAgeValidator());
        clientDAO = new ClientDAO();
    }

    /**
     * Inserts a new client into the database after validation.
     *
     * @param client The Client object to be inserted.
     * @return The inserted Client object.
     * @throws IllegalArgumentException if validation fails.
     */
    public Client insertClient(Client client) {
        for (Validator<Client> v : validators) {
            v.validate(client);
        }
        return clientDAO.insert(client);
    }

    /**
     * Updates an existing client in the database after validation.
     *
     * @param client The Client object with updated data.
     * @return The updated Client object.
     * @throws IllegalArgumentException if validation fails.
     */
    public Client updateClient(Client client) {
        for (Validator<Client> v : validators) {
            v.validate(client);
        }
        return clientDAO.update(client);
    }

    /**
     * Deletes a client from the database by ID.
     *
     * @param id The ID of the client to be deleted.
     */
    public void deleteClient(int id) {
        clientDAO.delete(id);
    }

    /**
     * Retrieves all clients from the database.
     *
     * @return A list of all Client objects.
     */
    public List<Client> findAllClients() {
        return clientDAO.findAll();
    }

    /**
     * Creates a DefaultTableModel representation for a list of clients.
     * This model is used for displaying clients in a JTable.
     *
     * @param clients The list of Client objects.
     * @return A DefaultTableModel for the given clients.
     */
    public DefaultTableModel getTableModel(List<Client> clients) {
        return clientDAO.createTable(clients);
    }
}
