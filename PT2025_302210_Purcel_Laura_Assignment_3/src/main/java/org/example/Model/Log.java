package org.example.Model;

import org.example.Connection.ConnectionFactory;

import java.lang.reflect.RecordComponent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that manages logging of bills into the database.
 * It allows insertion of a Bill, finding a Bill by order ID,
 * and retrieving all Bill entries.
 */
public class Log {

    private static final Logger LOGGER = Logger.getLogger(Log.class.getName());

    /**
     * Inserts a new Bill into the "Log" table of the database.
     * It uses reflection to build the SQL insert query dynamically based on
     * the fields of the Bill record.
     *
     * @param bill the Bill object to be inserted into the database
     */
    public void insertBill(Bill bill) {
        StringBuilder query = new StringBuilder("INSERT INTO `Log` (");

        RecordComponent[] components = Bill.class.getRecordComponents();
        for (int i = 0; i < components.length; i++) {
            query.append(components[i].getName());
            if (i < components.length - 1)
                query.append(", ");
        }

        query.append(") VALUES (");
        for (int i = 0; i < components.length; i++) {
            query.append("?");
            if (i < components.length - 1)
                query.append(", ");
        }
        query.append(")");

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query.toString())) {

            statement.setInt(1, bill.orderId());
            statement.setInt(2, bill.clientId());
            statement.setInt(3, bill.productId());
            statement.setInt(4, bill.quantity());
            statement.setDouble(5, bill.totalPrice());
            statement.setTimestamp(6, Timestamp.valueOf(bill.timestamp()));

            statement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to insert bill: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all Bill entries from the "Log" table.
     *
     * @return a list containing all Bill objects found in the database
     */
    public List<Bill> findAll() {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM `Log`";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Bill bill = new Bill(
                        resultSet.getInt("orderId"),
                        resultSet.getInt("clientId"),
                        resultSet.getInt("productId"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("totalPrice"),
                        resultSet.getTimestamp("timestamp").toLocalDateTime()
                );
                bills.add(bill);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve all bills: " + e.getMessage(), e);
        }

        return bills;
    }
}
