/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.AppUser;
import java.io.IOException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author dpattas
 */
@ManagedBean(name = "loginController")
@ViewScoped
public class LoginController implements Serializable {

    @PersistenceContext
    private EntityManager em;
    @EJB
    private Auxilliary aux;

    private String username;
    private String password;
    private AppUser loggedInUser;

    /**
     * Creates a new instance of LoginController
     */
    public LoginController() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        loggedInUser = em.find(AppUser.class, username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AppUser getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(AppUser loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void login() throws IOException, ServletException {
        //FacesContext.getCurrentInstance().getExternalContext().redirect("http://localhost:8080/GamifiedReflection/teacher/students.html");
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(this.username, this.password);
            if (loggedInUser.getUserRole() == 0) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("http://localhost:8080/GamifiedReflection/teacher/students.html");
            } else {
                FacesContext.getCurrentInstance().getExternalContext().redirect("http://localhost:8080/GamifiedReflection/student/index.html");
            }
        } catch (ServletException e) {

            context.addMessage("loginError", new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Login failed."));

        }
    }

    public void logout() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.logout();
            FacesContext.getCurrentInstance().getExternalContext().redirect("http://localhost:8080/GamifiedReflection/login.html");
        } catch (ServletException e) {

            context.addMessage(null, new FacesMessage("Logout failed."));
        }
    }

}
