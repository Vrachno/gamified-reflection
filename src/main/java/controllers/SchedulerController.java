/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.ActivitiesMap;
import entities.Activity;
import entities.Category;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author dpattas
 */
@ManagedBean(name = "schedulerController")
@ViewScoped
public class SchedulerController implements Serializable {
    
    @PersistenceContext
    EntityManager em;
    @EJB
    private TransactionsController transactions;

    private ScheduleModel model;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private List<Category> categories;
    private List<Activity> activities;
    private List<SelectItem> categoriesList;
    private List<SelectItem> activitiesList;
    private Category selectedCategory;
    private Activity selectedActivity;

    public SchedulerController() {
    }

    @PostConstruct
    public void init() {
        categories = em.createNamedQuery("Category.findAll").getResultList();
        categoriesList = new ArrayList<>();
        for (Category category : categories) {
            category.setActivityList(em.createNamedQuery("Activity.findByCategoryId").setParameter("categoryId", category).getResultList());
            categoriesList.add(new SelectItem(category, category.getTitle()));
        }
        selectedCategory = new Category();
    }

    public ScheduleModel getModel() {
        return model;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void addEvent() {
        if (event.getId() == null) {
            model.addEvent(event);
           // transactions.toggleEnabled(selectedActivity);
            List<ActivitiesMap> activitiesMapList = selectedActivity.getActivitiesMapList();
            for (ActivitiesMap activitiesMap : activitiesMapList) {
                activitiesMap.setDateEnabled(event.getStartDate());
                activitiesMap.setDateDisabled(event.getEndDate());
            }
        } else {
            model.updateEvent(event);
        }
        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent e) {
        event = (ScheduleEvent) e.getObject();
    }

    public void onDateSelect(SelectEvent e) {
        Date date = (Date) e.getObject();
        event = new DefaultScheduleEvent("", date, date);
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
    }

    public Activity getSelectedActivity() {
        return selectedActivity;
    }

    public void setSelectedActivity(Activity selectedActivity) {
        this.selectedActivity = selectedActivity;
    }

    public void setCategoryActivitiesList() {
        activities = selectedCategory.getActivityList();
        activitiesList = new ArrayList<>();
        if (activities != null) {
            for (Activity activity : activities) {
                activitiesList.add(new SelectItem(activity, activity.getTitle()));
            }
        }

    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<SelectItem> getCategoriesList() {

        return categoriesList;
    }

    public void setCategoriesList(List<SelectItem> categoriesList) {
        this.categoriesList = categoriesList;
    }

    public List<SelectItem> getActivitiesList() {

        return activitiesList;
    }

    public void setActivitiesList(List<SelectItem> activitiesList) {
        this.activitiesList = activitiesList;

    }

}
