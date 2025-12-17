package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    ShoppingCart addProductToCart(Product product, int userId);
    void updateProductInCart(Product product, int productId, int userId);
    void deleteProductInCart(int productId, int userId);
    // add additional method signatures here
}
