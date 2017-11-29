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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.persistence.EntityManager;
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
public class ActivitiesController implements Serializable{

    @PersistenceContext
    EntityManager em;
    @EJB
    private TransactionsController transactions;
    @EJB
    private Auxilliary aux;
    
    private List<Category> categories;
    private String newCategoryTitle;
    private ScheduleModel scheduleModel;
    private DefaultScheduleEvent event = new DefaultScheduleEvent();
    private List<ActivitiesMap> selectedActivityMaps;
    private Category selectedCategory;
    private List<Activity> categoryActivities;
    private Activity selectedActivity;
    private String newActivityTitle;

    /**
     * Creates a new instance of categoriesController
     */
    public ActivitiesController() {
    }

    @PostConstruct
    public void init() {

        categories = em.createNamedQuery("Category.findAll").getResultList();
        scheduleModel = new DefaultScheduleModel();
        AppUser admin = (AppUser) em.createNamedQuery("AppUser.findByUserRole").setParameter("userRole", 0).getSingleResult();
        List<ActivitiesMap> nullActivitiesMapList = em.createNamedQuery("ActivitiesMap.findByStudentEmail").setParameter("studentEmail", admin).getResultList();
        for (ActivitiesMap nullActivitiesMap : nullActivitiesMapList) {
            if (nullActivitiesMap.getEnabled()) {
                DefaultScheduleEvent newEvent = new DefaultScheduleEvent(nullActivitiesMap.getActivity().getTitle(),
                        nullActivitiesMap.getDateEnabled(),
                        nullActivitiesMap.getDateDisabled(), nullActivitiesMap.getActivity().getCategoryId().getTitle().toLowerCase().replace(" ", "-"));
                newEvent.setAllDay(true);
                scheduleModel.addEvent(newEvent);
            }
        }
        for (Category category : categories) {
            category.setActivityList(em.createNamedQuery("Activity.findByCategoryId").setParameter("categoryId", category).getResultList());
        }
        selectedCategory = aux.getPreviousCategory();
        if (selectedCategory!=null) {
            selectedCategory.setActivityList(em.createNamedQuery("Activity.findByCategoryId").setParameter("categoryId", selectedCategory).getResultList());
            setCategoryActivities(selectedCategory.getActivityList());
        }
        selectedActivityMaps = new ArrayList<>();
        aux.setPreviousCategory(null);
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
    
    public void deleteCategory(Category category) throws IOException {

        transactions.deleteCategory(category);
        FacesContext.getCurrentInstance().getExternalContext().redirect("activities.html");
    }

    public void addCategory(String title) throws IOException {
        Category newCategory = new Category();
        newCategory.setTitle(title);
        transactions.addCategory(newCategory);
        aux.setPreviousCategory(newCategory);
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
        aux.setPreviousCategory(category);
    }

    public void onRowEditActivity(RowEditEvent event) {
        transactions.saveActivity((Activity) event.getObject());
    }

    public void deleteActivity(Activity activity) {
        transactions.deleteActivity(activity);
        updateActivitiesList(activity.getCategoryId());
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

    public DefaultScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(DefaultScheduleEvent event) {
        this.event = event;
    }

    public void addEvent() {
        boolean sameActivitiesOverlap = false;
        boolean editing = false;
        if (event.getId() != null) {
            editing = true;
            scheduleModel.deleteEvent(event);
        }
        for (ScheduleEvent checkedEvent : scheduleModel.getEvents()) {
            if (checkedEvent.getTitle().equals(event.getTitle())
                    && ((!event.getStartDate().before(checkedEvent.getStartDate()) && !event.getStartDate().after(checkedEvent.getEndDate()))
                    || (!event.getEndDate().before(checkedEvent.getStartDate()) && !event.getEndDate().after(checkedEvent.getEndDate()))
                    || (!event.getStartDate().after(checkedEvent.getStartDate()) && !event.getEndDate().before(checkedEvent.getEndDate())))) {
                sameActivitiesOverlap = true;
                break;
            }
        }
        if (!sameActivitiesOverlap) {
            scheduleModel.addEvent(event);
            if (!editing) {
                transactions.addActivitiesMaps(selectedActivity, event.getStartDate(), event.getEndDate());
            } else {
                transactions.editActivitiesMaps(selectedActivityMaps, event.getStartDate(), event.getEndDate());
            }
        }

        event = new DefaultScheduleEvent();

        init();
    }

public void onEventSelect(SelectEvent e) {
        event = (DefaultScheduleEvent) e.getObject();
        selectedActivityMaps = em.createNamedQuery("ActivitiesMap.findByDateEnabledAndTitle")
                .setParameter("dateEnabled", event.getStartDate())
                .setParameter("title", event.getTitle())
                .setParameter("enabled", true)
                .getResultList();
    }

    public void deleteEvent() {
        transactions.removeActivitiesMaps(selectedActivityMaps);
        scheduleModel.deleteEvent(event);
        selectedActivityMaps = new ArrayList<>();
    }

    public void onDateSelect(SelectEvent e) {
        Date startDate = (Date) e.getObject();
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DATE, 7);
        Date endDate = c.getTime();
        event = new DefaultScheduleEvent(selectedActivity.getTitle(), startDate, endDate, selectedActivity.getCategoryId().getTitle().toLowerCase().replace(" ", "-"));
        //event.setAllDay(true);
    }
    
    public void setStartDate(SelectEvent e) {
        Date startDate = (Date) e.getObject();
        event.setStartDate(startDate);
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DATE, 7);
        if (event.getEndDate().before(startDate)) {
            event.setEndDate(c.getTime());
        }
    }

    public void setEndDate(SelectEvent e) {
        Date startDate = (Date) e.getObject();
        event.setEndDate(startDate);
    }

    public String getStyles() {
        StringBuilder style = new StringBuilder();
        int factor = 0;
        Random rand = new Random();
        for (Category category : categories) {
            String className = category.getTitle().toLowerCase().replace(' ', '-');
            int hue = (int) (category.getId()*10 + factor);
            int saturation = (int) (rand.nextInt(50)) + 50;
            String value = hue + ", " + saturation + "%" + ", " + "80%";
            style.append(".")
                    .append(className)
                    .append(",\n.fc-agenda ")
                    .append(className)
                    .append(" .fc-event,\n")
                    .append(className)
                    .append(" a {\n    background-color: hsl(")
                    .append(value)
                    .append(");\n    border-color: hsl(")
                    .append(value)
                    .append(");\n    color: black !important;\n    font-weight: bold;\n}\n");
            
            factor+=80;
        }
        return style.toString();
    }

}
