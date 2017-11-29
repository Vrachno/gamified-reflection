/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.AppUser;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dpattas
 */
@ManagedBean(name = "usersController")
@ViewScoped
public class UsersController implements Serializable {

    @PersistenceContext
    EntityManager em;
    @EJB
    private TransactionsController transactions;

    private List<AppUser> students;
    private List<AppUser> activeStudents;
    private List<AppUser> inactiveStudents;

    public UsersController() {
    }

    @PostConstruct
    public void init() {
        students = em.createNamedQuery("AppUser.findAllStudents").getResultList();
    }

    public List<AppUser> getStudents() {
        return students;
    }

    public void setStudents(List<AppUser> students) {
        this.students = students;
    }

    public List<AppUser> getActiveStudents() {
        return activeStudents;
    }

    public void setActiveStudents(List<AppUser> activeStudents) {
        this.activeStudents = activeStudents;
    }

    public List<AppUser> getInactiveStudents() {
        return inactiveStudents;
    }
    
    public void setInactiveStudents(List<AppUser> inactiveStudents) {
        this.inactiveStudents = inactiveStudents;
    }
    
    public void toggleActive(AppUser user) {
        transactions.toggleActive(user);
    }

    public void deleteUser(AppUser user) throws IOException {
        transactions.deleteUser(user);
        FacesContext.getCurrentInstance().getExternalContext().redirect("users.html");
    }

}
