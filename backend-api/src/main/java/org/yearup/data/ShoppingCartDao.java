package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    ShoppingCart addProductToCart(Product product, int userId);
    void updateProductInCart(Product product, int productId, int userId);
    void removeProductInCart(int productId, int userId);
    ShoppingCart emptyCart(int userId);
}
