/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.ActivitiesMap;
import entities.AppUser;
import entities.Category;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;

/**
 *
 * @author dpattas
 */
@ManagedBean(name = "profileController")
@ViewScoped
public class ProfileController implements Serializable {

    @PersistenceContext
    private EntityManager em;
    @EJB
    private TransactionsController transactions;
    @EJB
    Auxilliary aux;

    private AppUser student;
    private HorizontalBarChartModel barModel;
    private LineChartModel lineModel;
    private boolean activitiesPending;
    private List<Category> categories;
    private boolean editing;
    private String level;
    private int progress;
    private Category selectedCategory;
    private List<SelectItem> categoriesList;

    public ProfileController() {
    }

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        categories = em.createNamedQuery("Category.findAll").getResultList();
        categoriesList = new ArrayList<>();
        for (Category category : categories) {
            categoriesList.add(new SelectItem(category, category.getTitle()));
        }
        selectedCategory = new Category();
        student = em.find(AppUser.class, request.getUserPrincipal().getName());
        student.setSkillsMapList(em.createNamedQuery("SkillsMap.findByStudentEmail").setParameter("studentEmail", student).getResultList());
        setBarModel(aux.createBarModel(student, barModel));
        setLineModel(aux.createLineModel(student, lineModel));
        List<ActivitiesMap> allActivities = em.createNamedQuery("ActivitiesMap.findNotLoggedByStudent").setParameter("studentEmail", student).setParameter("logged", false).getResultList();
        if (!allActivities.isEmpty()) {
            List<ActivitiesMap> currentActivities = new ArrayList<>();
            Date currentDate = new Date();
            for (ActivitiesMap activity : allActivities) {
                if (!currentDate.before(activity.getDateEnabled()) && !currentDate.after(activity.getDateDisabled())) {
                    currentActivities.add(activity);
                }
            }
            setActivitiesPending(!currentActivities.isEmpty());
        }
        //aux.setAllLevels();
        aux.setStudentsOverallScores();
        aux.setGraphicImage();
        //aux.setStudent();
    }

    public AppUser getStudent() {
        return student;
    }

    public void setStudent(AppUser student) {
        this.student = student;
    }

    public HorizontalBarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(HorizontalBarChartModel barModel) {
        this.barModel = barModel;
    }

    public LineChartModel getLineModel() {
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }

    public boolean getActivitiesPending() {
        return activitiesPending;
    }

    public void setActivitiesPending(boolean activitiesPending) {
        this.activitiesPending = activitiesPending;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public boolean getEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public void changeNickname() {
        transactions.saveUser(student);
        setEditing(false);
        init();
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
        init();
        //aux.setSelectedCategory(selectedCategory);
    }

    public List<SelectItem> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(List<SelectItem> categoriesList) {
        this.categoriesList = categoriesList;
    }

}
