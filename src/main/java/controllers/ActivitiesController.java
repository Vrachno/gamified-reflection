/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import entities.ActivitiesMap;
import entities.Activity;
import entities.AppUser;
import entities.Category;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
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
@ManagedBean(name = "activitiesController")
@ViewScoped
public class ActivitiesController implements Serializable {

    @PersistenceContext
    EntityManager em;
    @EJB
    private TransactionsController transactions;

        
    private ScheduleModel scheduleModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private List<Category> categories;
    private List<Activity> activities;
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
        activities = em.createNamedQuery("Activity.findAll").getResultList();
        // List<ActivitiesMap> uniqueActivitiesMaps = new ArrayList<>();
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
//            for (Activity activity : category.getActivityList()) {
//                activity.setActivitiesMapList(em.createNamedQuery("ActivitiesMap.findByActivity").setParameter("activity", activity).getResultList());
//               // uniqueActivitiesMaps.add(activity.getActivitiesMapList().get(0));
//                for (int i = 0; i < activity.getActivitiesMapList().size(); i++) {
//                    boolean found = false;
//                    for (int j = 0; j <= i && i>0; j++) {
//                        String title1 = activity.getActivitiesMapList().get(i).getActivity().getTitle();
//                        String title2 = activity.getActivitiesMapList().get(j).getActivity().getTitle();
//                        if (title1.equals(title2)
//                                && (activity.getActivitiesMapList().get(i).getDateEnabled() != null && activity.getActivitiesMapList().get(i).getDateEnabled().equals(activity.getActivitiesMapList().get(j).getDateEnabled()))) {
//                            found = true;
//                            break;
//                        }
//                        //uniqueActivitiesMaps.add(activity.getActivitiesMapList().get(i));
//                    }
//                    if (!found && activity.getActivitiesMapList().get(i).getDateEnabled() != null ) {
//                        scheduleModel.addEvent(new DefaultScheduleEvent(activity.getActivitiesMapList().get(i).getActivity().getTitle(),
//                                activity.getActivitiesMapList().get(i).getDateEnabled(),
//                                activity.getActivitiesMapList().get(i).getDateDisabled(), true));
//                    }
//                }
//            }
//      }


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

//    public void toggleEnabled(Activity activity) {
//        transactions.toggleEnabled(activity);
//        updateActivitiesList(activity.getCategoryId());
//        init();
//    }

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
    }

    public void onDateSelect(SelectEvent e) {
        Date date = (Date) e.getObject();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 7);
        event = new DefaultScheduleEvent(selectedActivity.getTitle(), date, c.getTime());
        addEvent();
    }

}
