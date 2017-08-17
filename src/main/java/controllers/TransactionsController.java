/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.Activity;
import entities.Category;
import entities.SkillsMap;
import entities.AppUser;
import entities.Groups;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dpattas
 */
@Stateless
public class TransactionsController {

    @PersistenceContext
    private EntityManager em;

    public void saveCategory(Category category) {
        em.merge(category);
    }

    public void addCategory(Category category) {
        em.persist(category);
        List<AppUser> students = em.createNamedQuery("AppUser.findByUserRole").setParameter("userRole", 1).getResultList();
        for (AppUser student : students) {
            SkillsMap newSkill = new SkillsMap();
            newSkill.setStudentEmail(student);
            newSkill.setCategoryId(category);
            em.merge(newSkill);
 
            }
    }

    public void deleteCategory(Category category) {
        em.remove(em.merge(category));
    }

    public void addActivity(Activity activity) {
        em.persist(activity);
    }

    public void saveActivity(Activity activity) {
        em.merge(activity);
    }

    public void deleteActivity(Activity activity) {
        em.remove(em.merge(activity));
    }
    
    public void addUser(AppUser user) {
        em.persist(new Groups(user.getEmail(), "STUDENT"));
        em.persist(user);
        List<Category> categories = em.createNamedQuery("Category.findAll").getResultList();
        for (Category category : categories) {
            SkillsMap skillsMap = new SkillsMap();
            skillsMap.setStudentEmail(user);
            skillsMap.setCategoryId(category);
           em.persist(skillsMap);   
        }

    }
}
