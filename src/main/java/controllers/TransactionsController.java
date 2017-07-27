/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.Activity;
import entities.Category;
import entities.SkillsMap;
import entities.Student;
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
        List<Student> students = em.createNamedQuery("Student.findAll").getResultList();
        for (Student student : students) {
            SkillsMap newSkill = new SkillsMap();
            newSkill.setStudentId(student);
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
    
        public void deleteActivity(Activity activity) {
        em.remove(em.merge(activity));
    }
}
