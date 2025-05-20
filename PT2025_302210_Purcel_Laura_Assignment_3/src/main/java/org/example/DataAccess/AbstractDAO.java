package org.example.DataAccess;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.example.Connection.ConnectionFactory;

import javax.swing.table.DefaultTableModel;

/**
 * Generic abstract DAO class for performing basic database operations on any type T.
 * Uses reflection to dynamically generate SQL queries and manipulate object fields.
 *
 * @param <T> the type of objects handled by this DAO
 */
public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
    private final Class<T> type;

    /**
     * Constructor that uses reflection to determine the actual class of T at runtime.
     */
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Creates a SELECT SQL query with a WHERE clause based on a given field.
     *
     * @param field the name of the field to use in the WHERE clause
     * @return the generated SQL query string
     */
    private String createSelectQuery(String field) {
        return "SELECT * FROM `" + type.getSimpleName() + "` WHERE " + field + " =?";
    }

    /**
     * Retrieves all records of type T from the database by selecting all IDs and loading each object using findById.
     *
     * @return a list of all T objects from the database
     */
    public List<T> findAll() {
        List<T> resultList = new ArrayList<>();
        String query = "SELECT id FROM `" + type.getSimpleName() + '`';

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                T obj = findById(id);
                if (obj != null) {
                    resultList.add(obj);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "findAll using findById failed: " + e.getMessage(), e);
        }
        return resultList;
    }

    /**
     * Finds and returns a single object of type T by its ID.
     *
     * @param id the primary key of the desired object
     * @return the object of type T, or null if not found
     */
    public T findById(int id) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(createSelectQuery("id"))) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<T> list = createObjects(resultSet);
                return list.isEmpty() ? null : list.get(0);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        }
        return null;
    }

    /**
     * Creates a list of objects of type T from a ResultSet using reflection.
     *
     * @param resultSet the ResultSet containing data from a SQL query
     * @return a list of instantiated and populated T objects
     */
    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<T>();
        Constructor[] ctors = type.getDeclaredConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }
        try {
            while (resultSet.next()) {
                ctor.setAccessible(true);
                T instance = (T) ctor.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    String fieldName = field.getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (InstantiationException | IllegalAccessException | SecurityException |
                 IllegalArgumentException | InvocationTargetException | SQLException |
                 IntrospectionException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Creates an SQL INSERT query string for the type T, excluding the ID field.
     *
     * @return the INSERT SQL query
     */
    public String createInsertQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `").append(type.getSimpleName()).append("` (");

        Field[] fields = type.getDeclaredFields();
        for (int i = 1; i < fields.length; i++) {
            sb.append(fields[i].getName());
            if (i < fields.length - 1) sb.append(", ");
        }

        sb.append(") VALUES (");
        for (int i = 1; i < fields.length; i++) {
            sb.append("?");
            if (i < fields.length - 1) sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Inserts a new object of type T into the database.
     *
     * @param t the object to insert
     * @return the inserted object
     */
    public T insert(T t) {
        String query = createInsertQuery();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            Field[] fields = type.getDeclaredFields();
            for (int i = 1; i < fields.length; i++) {
                fields[i].setAccessible(true);
                statement.setObject(i, fields[i].get(t));
            }
            statement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, "Insert failed: " + e.getMessage(), e);
        }
        return t;
    }

    /**
     * Updates an existing object of type T in the database based on its ID.
     *
     * @param t the object to update
     * @return the updated object
     */
    public T update(T t) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE `").append(type.getSimpleName()).append("` SET ");

        Field[] fields = type.getDeclaredFields();
        for (int i = 1; i < fields.length; i++) {
            sb.append(fields[i].getName()).append(" = ?");
            if (i < fields.length - 1) sb.append(", ");
        }
        sb.append(" WHERE id = ?");

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sb.toString())) {

            for (int i = 1; i < fields.length; i++) {
                fields[i].setAccessible(true);
                statement.setObject(i, fields[i].get(t));
            }

            Field idField = fields[0];
            idField.setAccessible(true);
            statement.setObject(fields.length, idField.get(t));

            statement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, "Update failed: " + e.getMessage(), e);
        }
        return t;
    }

    /**
     * Deletes an object from the database based on its ID.
     *
     * @param id the ID of the object to delete
     */
    public void delete(int id) {
        String query = "DELETE FROM `" + type.getSimpleName() + "` WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Delete failed: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a DefaultTableModel from a list of objects of type T for use in JTable components.
     *
     * @param listOfT the list of T objects to convert to table rows
     * @return a DefaultTableModel representing the data
     */
    public DefaultTableModel createTable(List<T> listOfT) {
        DefaultTableModel model = new DefaultTableModel();
        for (Field field : type.getDeclaredFields()) {
            model.addColumn(field.getName());
        }
        for (T t : listOfT) {
            Object[] row = new Object[type.getDeclaredFields().length];
            int i = 0;
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    row[i++] = field.get(t);
                } catch (IllegalAccessException e) {
                    LOGGER.log(Level.WARNING, "Reflection error reading field: " + field.getName(), e);
                }
            }
            model.addRow(row);
        }
        return model;
    }
}
