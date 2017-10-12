/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.ActivitiesMap;
import entities.Activity;
import entities.AppUser;
import entities.Category;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

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
    private ScheduleModel scheduleModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private Category selectedCategory;
    private List<Activity> categoryActivities;
    private Activity selectedActivity;
    private String newActivityTitle;

    /**
     * Creates a new instance of categoriesController
     */
    public CategoriesController() {
    }

    @PostConstruct
    public void init() {

        categories = em.createNamedQuery("Category.findAll").getResultList();
        scheduleModel = new DefaultScheduleModel();
        AppUser admin = (AppUser) em.createNamedQuery("AppUser.findByUserRole").setParameter("userRole", 0).getSingleResult();
        List<ActivitiesMap> nullActivitiesMapList = em.createNamedQuery("ActivitiesMap.findByStudentEmail").setParameter("studentEmail", admin).getResultList();
        for (ActivitiesMap nullActivitiesMap : nullActivitiesMapList) {
            scheduleModel.addEvent(new DefaultScheduleEvent(nullActivitiesMap.getActivity().getTitle(),
                    nullActivitiesMap.getDateEnabled(),
                    nullActivitiesMap.getDateDisabled(), true));
        }
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

    public String getNewCategoryTitle() {
        return newCategoryTitle;
    }

    public void setNewCategoryTitle(String newCategoryTitle) {
        this.newCategoryTitle = newCategoryTitle;
    }
    
    public void onRowEditCategory (RowEditEvent event){
        transactions.saveCategory((Category) event.getObject());
}
    
    public void deleteCategory(Category category) {
        
        transactions.deleteCategory(category);
        init();
    }

    public void addCategory(String title) throws IOException {
        Category newCategory = new Category();
        newCategory.setTitle(title);
        transactions.addCategory(newCategory);
        FacesContext.getCurrentInstance().getExternalContext().redirect("activities.html");
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

    public void addActivity(String title, Category category) throws IOException {
        Activity newActivity = new Activity();
        newActivity.setTitle(title);
        newActivity.setCategoryId(category);
        transactions.addActivity(newActivity);
        setNewActivityTitle(null);
        updateActivitiesList(category);
        FacesContext.getCurrentInstance().getExternalContext().redirect("activities.html");
    }

    public void onRowEditActivity(RowEditEvent event) {
        transactions.saveActivity((Activity) event.getObject());
    }

    public void deleteActivity(Activity activity) {

        transactions.deleteActivity(activity);
        updateActivitiesList(activity.getCategoryId());
        init();
    }
    
        public void updateActivitiesList(Category category) {
        
        selectedCategory.setActivityList(em.createNamedQuery("Activity.findByCategoryId").setParameter("categoryId", category).getResultList());
        setCategoryActivities(selectedCategory.getActivityList());
        
    }
    public ScheduleModel getScheduleModel() {
        return scheduleModel;
    }

    public void setScheduleModel(ScheduleModel scheduleModel) {
        this.scheduleModel = scheduleModel;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void addEvent() {
        if (event.getId() == null) {
            scheduleModel.addEvent(event);
            transactions.addActivitiesMaps(selectedActivity, event.getStartDate(), event.getEndDate());
        } else {
            scheduleModel.updateEvent(event);
        }
        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent e) {
        event = (ScheduleEvent) e.getObject();
        ActivitiesMap activitiesMap = (ActivitiesMap) em.createNamedQuery("ActivitiesMap.findByDateEnabledAndTitle")
                .setParameter("dateEnabled", event.getStartDate())
                .setParameter("title", event.getTitle()).getResultList().get(0);
        transactions.removeActivitiesMaps(activitiesMap);
        init();
    }

    public void onDateSelect(SelectEvent e) {
        Date date = (Date) e.getObject();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 7);
        event = new DefaultScheduleEvent(selectedActivity.getTitle(), date, c.getTime());
        try {
            em.createNamedQuery("ActivitiesMap.findByDateEnabledAndTitle")
                    .setParameter("dateEnabled", event.getStartDate())
                    .setParameter("title", event.getTitle()).getSingleResult();
        } catch (NoResultException ex) {
            addEvent();
        } catch (NonUniqueResultException ex) {
            
        }
    }

}
