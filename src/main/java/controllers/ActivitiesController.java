/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.Activity;
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
@ManagedBean(name = "activitiesController")
@ViewScoped
public class ActivitiesController implements Serializable {

    @PersistenceContext
    EntityManager em;
    @EJB
    private TransactionsController transactions;

    private List<Category> categories;
    private Category selectedCategory;
    private List<Activity> categoryActivities;
    private Activity selectedActivity;
    private String newActivityTitle;

    /**
     * Creates a new instance of ActivitiesController
     */
    public ActivitiesController() {
    }

    @PostConstruct
    public void init() {

        categories = em.createNamedQuery("Category.findAll").getResultList();
        for (Category category : categories) {
            category.setActivityList(em.createNamedQuery("Activity.findByCategoryId").setParameter("categoryId", category).getResultList());
        }

    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
        setCategoryActivities(selectedCategory.getActivityList());
    }

    public List<Activity> getCategoryActivities() {
        return categoryActivities;
    }

    public void setCategoryActivities(List<Activity> categoryActivities) {
        this.categoryActivities = categoryActivities;
    }
    
    public Activity getSelectedActivity() {
        return selectedActivity;
    }

    public void setSelectedActivity(Activity selectedActivity) {
        this.selectedActivity = selectedActivity;
    }
    
    public String getNewActivityTitle() {
        return newActivityTitle;
    }

    public void setNewActivityTitle(String newActivityTitle) {
        this.newActivityTitle = newActivityTitle;
    }

    public void addActivity(String title, Category category) {
        Activity newActivity = new Activity();
        newActivity.setTitle(title);
        newActivity.setCategoryId(category);
        transactions.addActivity(newActivity);
        setNewActivityTitle(null);
        updateActivitiesList(category);
        init();
    }

    public void onRowEdit(RowEditEvent event) {
        transactions.saveActivity((Activity) event.getObject());
    }

    public void deleteActivity(Activity activity) {

        transactions.deleteActivity(activity);
        updateActivitiesList(activity.getCategoryId());
        init();
    }

    public void toggleEnabled(Activity activity) {
        transactions.toggleEnabled(activity);
        updateActivitiesList(activity.getCategoryId());
        init();
    }

    public void updateActivitiesList(Category category) {
        
        selectedCategory.setActivityList(em.createNamedQuery("Activity.findByCategoryId").setParameter("categoryId", category).getResultList());
        setCategoryActivities(selectedCategory.getActivityList());
        
    }
    
    public void setEnabled(Activity activity) {
        
    }

}
