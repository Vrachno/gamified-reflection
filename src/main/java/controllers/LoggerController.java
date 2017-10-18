/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.ActivitiesMap;
import entities.AppUser;
import entities.SkillsMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author dpattas
 */
@ManagedBean(name = "loggerController")
@ViewScoped
public class LoggerController implements Serializable {

    @PersistenceContext
    EntityManager em;
    @EJB
    private TransactionsController transactions;
    @EJB
    private Auxilliary aux;

    private List<ActivitiesMap> activities;
    private AppUser student;

    public LoggerController() {
    }

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        student = em.find(AppUser.class, request.getUserPrincipal().getName());
        List<ActivitiesMap> allActivities = em.createNamedQuery("ActivitiesMap.findNotLoggedByStudent").setParameter("studentEmail", student).setParameter("logged", false).getResultList();
        Calendar cCurrent = Calendar.getInstance();
        cCurrent.setTime(new Date());
        activities = new ArrayList<>();
        for (ActivitiesMap activity : allActivities) {
             Calendar cEnabled = Calendar.getInstance();
                cEnabled.setTime(activity.getDateEnabled());
                Calendar cDisabled = Calendar.getInstance();
                cDisabled.setTime(activity.getDateDisabled());
                cDisabled.add(Calendar.DATE, 1);
                if (activity.getEnabled() && !cCurrent.getTime().before(cEnabled.getTime()) && !cCurrent.getTime().after(cDisabled.getTime())) {
                activities.add(activity);
            }
        }
        aux.setStudentScores(null);
    }

    public AppUser getStudent() {
        return student;
    }

    public void setStudent(AppUser student) {
        this.student = student;
    }

    public List<ActivitiesMap> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivitiesMap> activities) {
        this.activities = activities;
    }

    public Auxilliary getAux() {
        return aux;
    }

    public void setAux(Auxilliary aux) {
        this.aux = aux;
    }
    
    public void log(ActivitiesMap activity, int level) {
        activity.setAnswer(level);
        activity.setLogged(true);
        activity.setDateAnswered(new Date());
        SkillsMap skillsMap = (SkillsMap) em.createNamedQuery("SkillsMap.findByCategoryIdAndStudentEmail")
                .setParameter("categoryId", activity.getActivity().getCategoryId())
                .setParameter("studentEmail", student).getSingleResult();
        skillsMap.setSkillLevel(skillsMap.getSkillLevel() + level);
        activity.setNewSkillLevel(skillsMap.getSkillLevel());
        transactions.saveActivitiesMap(activity);
        transactions.saveSkillsMap(skillsMap);
        aux.setStudentScores(null);
        init();
    }

}
