/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.Category;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author dpattas
 */
@ManagedBean(name = "categoriesController")
@ViewScoped
public class CategoriesController implements Serializable{

    @PersistenceContext
    EntityManager em;
    @EJB
    private TransactionsController transactions;
    
    private List<Category> categories;
    private String newCategoryTitle;

    /**
     * Creates a new instance of categoriesController
     */
    public CategoriesController() {
    }

    @PostConstruct
    public void init() {

        categories = em.createNamedQuery("Category.findAll").getResultList();
        
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getNewCategoryTitle() {
        return newCategoryTitle;
    }

    public void setNewCategoryTitle(String newCategoryTitle) {
        this.newCategoryTitle = newCategoryTitle;
    }
    
    public void onRowEdit (RowEditEvent event){
        transactions.saveCategory((Category) event.getObject());
}
    
    public void deleteCategory(Category category) {
        
        transactions.deleteCategory(category);
        init();
    }

    public void addCategory(String title) {
        Category newCategory = new Category();
        newCategory.setTitle(title);
        transactions.addCategory(newCategory);
        init();
    }

}
