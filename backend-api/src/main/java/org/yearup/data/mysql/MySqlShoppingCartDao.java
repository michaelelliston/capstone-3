package org.yearup.data.mysql;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {

        String sql = "SELECT * FROM shopping_cart JOIN products USING (product_id) WHERE user_id = ?;";

        ShoppingCart shoppingCart = new ShoppingCart();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                    shoppingCartItem.setProduct(mapRow(resultSet));
                    shoppingCartItem.setQuantity(resultSet.getInt("quantity"));

                    shoppingCart.add(shoppingCartItem);
                }
                return shoppingCart;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Adds a product to the user's cart. Returns the user's ShoppingCart object, so the frontend can successfully update the number of products in the cart.
    @Override
    public ShoppingCart addProductToCart(Product product, int userId) {

        String sql = "SELECT * FROM shopping_cart WHERE user_id = ? AND product_id = ?;";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, product.getProductId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Checks if a record exists already before attempting to update it.
                if (resultSet.next()) {
                    sql = "UPDATE shopping_cart SET quantity = ? WHERE product_id = ? AND user_id = ?;";

                    try (PreparedStatement quantityStatement = connection.prepareStatement(sql)) {

                        quantityStatement.setInt(1, resultSet.getInt("quantity") + 1);
                        quantityStatement.setInt(2, product.getProductId());
                        quantityStatement.setInt(3, userId);

                        int rowsUpdated = quantityStatement.executeUpdate();

                        if (rowsUpdated != 1) {
                            throw new RuntimeException();
                        }
                    }

                } else {
                    // Adds a product to the shopping cart if none of its type exists already.
                    sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, ?);";

                    try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {

                        insertStatement.setInt(1, userId);
                        insertStatement.setInt(2, product.getProductId());
                        insertStatement.setInt(3, 1);

                        int rowsUpdated = insertStatement.executeUpdate();

                        if (rowsUpdated != 1) {
                            throw new RuntimeException();
                        }
                    }
                }
            }
            return getByUserId(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateProductInCart(Product product, int productId, int userId) {

        String sql = "UPDATE shopping_cart SET product_id = ? WHERE product_id = ? AND user_id = ?;";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, product.getProductId());
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, userId);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated != 1) {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeProductInCart(int productId, int userId) {

        String sql = "SELECT * FROM shopping_cart WHERE product_id = ? AND user_id = ?;";

        try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("quantity") <= 1) {
                    sql = "DELETE FROM shopping_cart WHERE product_id = ? AND user_id = ?;";

                    preparedStatement.setInt(1, productId);
                    preparedStatement.setInt(2, userId);

                    int rowsUpdated = preparedStatement.executeUpdate(sql);
                    if (rowsUpdated != 1) {
                        throw new SQLException();
                    }
                } else {
                    sql = "UPDATE shopping_cart SET quantity = ? WHERE product_id = ? AND user_id = ?;";

                    preparedStatement.setInt(1, resultSet.getInt("quantity") - 1);
                    preparedStatement.setInt(2, productId);
                    preparedStatement.setInt(3, userId);

                    int rowsUpdated = preparedStatement.executeUpdate(sql);
                    if (rowsUpdated != 1) {
                        throw new SQLException();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShoppingCart emptyCart(int userId) {

        String sql = "DELETE FROM shopping_cart WHERE user_id = ?;";

        try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            preparedStatement.executeUpdate();

            return getByUserId(userId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static Product mapRow(ResultSet row) throws SQLException {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String subCategory = row.getString("subcategory");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, subCategory, stock, isFeatured, imageUrl);
    }
}
