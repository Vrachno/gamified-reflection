/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author dpattas
 */
@Stateless
public class AuthCheckBean {

    @PostConstruct
    public void init()  {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();    
        if (request.getUserPrincipal() == null) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("login.html");
            } catch (IOException ex) {
                Logger.getLogger(AuthCheckBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
