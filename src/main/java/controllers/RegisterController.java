/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.AppUser;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
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
@ManagedBean(name = "registerController")
@ViewScoped
public class RegisterController implements Serializable{

    private AppUser student;
    private String password;

    @PersistenceContext
    EntityManager em;
    @EJB
    private TransactionsController transactions;
    @EJB
    private Auxilliary aux;

    public RegisterController() {
    }

    @PostConstruct
    public void init() {
        student = new AppUser();
    }

    public AppUser getStudent() {
        return student;
    }

    public void setStudent(AppUser student) {
        this.student = student;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void register() throws IOException, NoSuchAlgorithmException{
        student.setPassword(aux.hash256(password));
        student.setUserRole((short) 1);
        
        transactions.addUser(student);
        
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.html");
    }
    
    
}
