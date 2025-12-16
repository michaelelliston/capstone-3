package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    void addProductToCart(Product product);
    void updateProductInCart(int productId,Product product);
    void deleteProductInCart(int productId);
    // add additional method signatures here
}
