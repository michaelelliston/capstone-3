package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    Product addProductToCart(Product product);
    ShoppingCart updateProductInCart(int productId,Product product);
    void deleteProductInCart(int productId);
    // add additional method signatures here
}
