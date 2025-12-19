package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

import java.security.Principal;

/**
 * Handles operations regarding the ShoppingCart by listening to specific paths, including
 * Getting a ShoppingCart by a user's id, adding a Product to a user's cart, updating an
 * existing product in a user's cart, removing a specific product from a user's cart, or
 * completely clearing a user's cart. Requires the user to be logged in.
 */
@RestController
@RequestMapping("cart")
@PreAuthorize("hasRole('ROLE_USER')")
@CrossOrigin
public class ShoppingCartController {
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    /**
     * @param shoppingCartDao is injected as a Bean from MySqlShoppingCartDao
     * @param userDao is injected as a bean from MySqlUserDao
     * @param productDao is injected as a Bean from MySqlProductDao
     */
    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    /**
     * Gets the authenticated user's ShoppingCart from the database
     * @param principal is obtained from an authenticated user making the request.
     * @return a ShoppingCart object that contains a HashMap of ShoppingCartItems, which each contain a Product object.
     */
    @GetMapping("")
    public ShoppingCart getCart(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            return this.shoppingCartDao.getByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    /**
     * Gets the authenticated user's ShoppingCart from the database after adding a Product to it. If successful, responds with Response Code 201 Created.
     * @param principal is obtained from an authenticated user making the request.
     * @param productId is obtained from the URL path.
     * @return the user's updated ShoppingCart object from the database.
     */
    @PostMapping("products/{productId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ShoppingCart addToCart(Principal principal, @PathVariable int productId) {

        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        Product p = this.productDao.getById(productId);

        return this.shoppingCartDao.addProductToCart(p, userId);
    }

    /**
     * Updates a product in the authenticated user's ShoppingCart within the database. If successful, responds with Response Code 204 No Content.
     * @param principal is obtained from an authenticated user making the request.
     * @param productId is obtained from the URL path.
     * @param product is obtained from the request body.
     */
    @PutMapping("products/{productId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateProductInCart(Principal principal, @PathVariable int productId, @RequestBody Product product) {

        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        this.shoppingCartDao.updateProductInCart(product, productId, userId);
    }

    /**
     * Removes a product in the authenticated user's ShoppingCart by deleting the record within the database.
     * If successful, responds with Response Code 204 No Content.
     * @param principal is obtained from an authenticated user making the request.
     * @param productId is obtained from the URL path.
     */
    @DeleteMapping("remove/{productId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeProductInCart(Principal principal, @PathVariable int productId) {

        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        this.shoppingCartDao.removeProductInCart(productId, userId);
    }

    /**
     * Deletes every record in the database's shopping cart table with a user id matching the current authenticated user's.
     * @param principal is obtained from an authenticated user making the request.
     * @return the empty ShoppingCart object.
     */
    @DeleteMapping("")
    @ResponseStatus(value = HttpStatus.OK)
    public ShoppingCart emptyCart(Principal principal) {

        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();

        return this.shoppingCartDao.emptyCart(userId);
    }
}