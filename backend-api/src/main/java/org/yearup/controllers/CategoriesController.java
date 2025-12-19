package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

/**
 * Handles operations regarding Category by listening to specific paths on the front-end
 * including Getting all or specific categories,
 * allowing Admins to Post new categories, and Put or Delete existing categories.
 */
@RestController
@RequestMapping("categories")
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;

    /**
     * @param categoryDao is injected as a Bean from MySqlCategoryDao
     * @param productDao is injected as a Bean from MySqlProductDao
     */
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    /**
     * Runs when requesting a get at the mapped path
     * @return a list of all Category objects from the database.
     */
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Category> getAll()
    {
        return this.categoryDao.getAllCategories();
    }

    /**
     * @param id is obtained from the URL path
     * @return the specific category with a matching id if one is found, otherwise returns Response Status 404.
     */
    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    @ResponseStatus(value = HttpStatus.OK)
    public Category getById(@PathVariable int id)
    {
        Category category = this.categoryDao.getById(id);

        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return category;
    }

    /**
     * @param categoryId is obtained from the URL path
     * @return a list of Product objects from the database that have a matching id if any are found,
     * otherwise returns Response Status 404.
     */
    @RequestMapping(path = "{categoryId}/products", method = RequestMethod.GET)
    @PreAuthorize("permitAll()")
    @ResponseStatus(value = HttpStatus.OK)
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        if (this.categoryDao.getById(categoryId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return this.productDao.listByCategoryId(categoryId);
    }

    /**
     * Requires the user to be an Admin.
     * @param category is obtained from the request body
     * @return the category that is created if successful.
     */
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category)
    {
        return this.categoryDao.create(category);
    }

    /**
     * Requires the user to be an Admin.
     * @param id is obtained from the URL path
     * @param category is obtained from the request body.
     * Updates the Category with the corresponding id if successful.
     */
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        if (this.categoryDao.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        this.categoryDao.update(id, category);
    }

    /**
     * Requires the user to be an Admin.
     * @param id is obtained from the URL path.
     * Deletes the Category with the corresponding id if successful.
     */
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int id)
    {
        if (this.categoryDao.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        this.categoryDao.delete(id);
    }
}
