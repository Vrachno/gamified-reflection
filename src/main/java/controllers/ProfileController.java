/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.AppUser;
import entities.Category;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.bean.ViewScoped;
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
public class ProfileController implements Serializable{

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    Auxilliary aux;

    private AppUser student;
    private HorizontalBarChartModel barModel;
    private LineChartModel lineModel;
    private boolean activitiesPending;
    private List<Category> categories;

    public ProfileController() {
    }

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        categories = em.createNamedQuery("Category.findAll").getResultList();
        student = em.find(AppUser.class, request.getUserPrincipal().getName());
        student.setSkillsMapList(em.createNamedQuery("SkillsMap.findByStudentEmail").setParameter("studentEmail", student).getResultList());
        setBarModel(aux.createBarModel(student, barModel));
        setLineModel(aux.createLineModel(student, lineModel));
        setActivitiesPending(!em.createNamedQuery("ActivitiesMap.findNotLoggedByStudent").setParameter("studentEmail", student).setParameter("logged", false).getResultList().isEmpty());
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
        
}
