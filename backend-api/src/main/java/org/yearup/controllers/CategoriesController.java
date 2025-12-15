package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
// add annotation to allow cross site origin requests
@RestController
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;


    // Autowires the dependency injections
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // Runs when requesting a Get at the mapped path.
    @RequestMapping(path = "/categories", method = RequestMethod.GET)
    public List<Category> getAll()
    {
        return this.categoryDao.getAllCategories();
    }

    // Runs when requesting a Get at the mapped path, and passes in the id from the path url.
    @RequestMapping(path = "/categories/{id}", method = RequestMethod.GET)
    public Category getById(@PathVariable int id)
    {
        return this.categoryDao.getById(id);
    }

    // Runs when requesting a Get at the mapped path, and passes in the id from the path url.
    @RequestMapping(path = "{categoryId}/products", method = RequestMethod.GET)
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        return this.productDao.listByCategoryId(categoryId);
    }

    // Runs when requesting a Post at the mapped path, and passes the request's body in as a Category object.
    // Requires the user to be an admin.
    @RequestMapping(path = "/categories", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category addCategory(@RequestBody Category category)
    {
        return this.categoryDao.create(category);
    }

    // Runs when requesting a Put at the mapped path, and passes the path variable and request body into the method.
    // Requires the user to be an admin.
    @RequestMapping(path = "/categories/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        this.categoryDao.update(id, category);
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @RequestMapping(path = "/categories/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable int id)
    {
        this.categoryDao.delete(id);
    }
}
