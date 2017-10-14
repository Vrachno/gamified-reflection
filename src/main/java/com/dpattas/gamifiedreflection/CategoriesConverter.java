/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dpattas.gamifiedreflection;

import entities.Category;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dpattas
 */
@FacesConverter("categoriesConverter")
public class CategoriesConverter implements Converter {

    @PersistenceContext
    EntityManager em;
    
    private Category category;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value.equals("")) {
            return null;
        } else {
            category = (Category) em.createNamedQuery("Category.findByTitle").setParameter("title", value).getSingleResult();
            return category;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        //category = (Category) value;
        return value.toString();
    }
    
}
