package org.yearup.data.mysql;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    // Implements the method from the CategoryDao interface.
    @Override
    public List<Category> getAllCategories() {
        // Creates an empty ArrayList and stores it in a variable of type List.
        List<Category> categories = new ArrayList<Category>();

        // Creates the String that will be sent to the MySQL database as a query.
        String sql = "SELECT * FROM categories;";

        // Establishes a connection to the database and executes the query String previously created, surrounded in
        // a try with resources block to auto close resources.
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet row = preparedStatement.executeQuery()) {
                // Points the cursor to the first record of the ResultSet, then begins the loop.
                while (row.next()) {
                    // Loops through the result set and creates a Category object for each record, before adding it to the categories ArrayList.
                    Category c = mapRow(row);
                    categories.add(c);
                }
            }
            // Assuming nothing goes wrong, returns an ArrayList of Category objects.
            return categories;
            // Throws an exception if an SQLException occurred during the try with resources block.
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Category getById(int categoryId) {

        String sql = "SELECT * FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Passes the categoryId from the parameter into the prepared statement.
            preparedStatement.setInt(1, categoryId);

            try (ResultSet row = preparedStatement.executeQuery()) {

                // Ensures there is a ResultSet row, while also pointing the cursor to the first record.
                if (row.next()) {
                    return mapRow(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Returns null if something went wrong, or if there was no result set obtained from the query.
        return null;
    }

    @Override
    public Category create(Category category) {

        String sql = "INSERT INTO categories (name, description) VALUES (?, ?);";

        try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());

            int rowsUpdated = preparedStatement.executeUpdate();

            ResultSet generatedKey = preparedStatement.getGeneratedKeys();
            generatedKey.next();
            int primaryKey = generatedKey.getInt(1);

            if (rowsUpdated != 1) {
                System.err.println("A problem occurred when updating a category.");
                throw new RuntimeException();
            }

            // Closes the ResultSet.
            generatedKey.close();

            return new Category(primaryKey, category.getName(), category.getDescription());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int categoryId, Category category) {

        if (categoryId != category.getCategoryId()) {
            System.err.println("ID mismatch when updating category.");
            throw new RuntimeException();
        } else {

            String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?;";

            try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, category.getName());
                preparedStatement.setString(2, category.getDescription());
                preparedStatement.setInt(3, categoryId);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated != 1) {
                    System.err.println("An error occurred when updating category.");
                    throw new RuntimeException();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void delete(int categoryId) {

        String sql = "DELETE FROM categories WHERE category_id = ?;";

        try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, categoryId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated != 1) {
                System.err.println("An error occurred when deleting a category.");
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
