package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                    Product p = new Product(resultSet.getInt("product_id"), resultSet.getString("name"),
                            resultSet.getBigDecimal("price"), resultSet.getInt("category_id"),
                            resultSet.getString("description"), resultSet.getString("subcategory"),
                            resultSet.getInt("stock"), resultSet.getBoolean("featured"),
                            resultSet.getString("image_url"));

                    ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                    shoppingCartItem.setProduct(p);
                    shoppingCartItem.setQuantity(resultSet.getInt("quantity"));
                    shoppingCartItem.setDiscountPercent(BigDecimal.valueOf(0));

                    shoppingCart.add(shoppingCartItem);
                }
                return shoppingCart;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addProductToCart(Product product) {

    }

    @Override
    public void updateProductInCart(int productId, Product product) {

    }

    @Override
    public void deleteProductInCart(int productId) {

    }
}
