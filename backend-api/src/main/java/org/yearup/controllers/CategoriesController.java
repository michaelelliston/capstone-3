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

@RestController
@RequestMapping("categories")
@CrossOrigin
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
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Category> getAll()
    {
        return this.categoryDao.getAllCategories();
    }

    // Runs when requesting a Get at the mapped path, and passes in the id from the path url.
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

    // Runs when requesting a Get at the mapped path, and passes in the id from the path url.
    @RequestMapping(path = "{categoryId}/products", method = RequestMethod.GET)
    @PreAuthorize("permitAll()")
    @ResponseStatus(value = HttpStatus.OK)
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        return this.productDao.listByCategoryId(categoryId);
    }

    // Runs when requesting a Post at the mapped path, and passes the request's body in as a Category object.
    // Requires the user to be an admin.
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category)
    {
        return this.categoryDao.create(category);
    }

    // Runs when requesting a Put at the mapped path, and passes the path variable and request body into the method.
    // Requires the user to be an admin.
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        this.categoryDao.update(id, category);
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int id)
    {
        this.categoryDao.delete(id);
    }
}
