/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.ActivitiesMap;
import entities.Activity;
import entities.Category;
import entities.SkillsMap;
import entities.AppUser;
import entities.Groups;
import java.util.Date;
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

    public void addActivitiesMaps(Activity activity, Date dateEnabled, Date dateDisabled) {
        List<AppUser> students = em.createNamedQuery("AppUser.findByUserRole").setParameter("userRole", 1).getResultList();
        if (!students.isEmpty()) {
            for (AppUser student : students) {
                ActivitiesMap newActivitiesMap = new ActivitiesMap();
                newActivitiesMap.setStudentEmail(student);
                newActivitiesMap.setActivity(activity);
                newActivitiesMap.setDateEnabled(dateEnabled);
                newActivitiesMap.setDateDisabled(dateDisabled);
                em.merge(newActivitiesMap);
            }
        }
        ActivitiesMap newActivitiesMap = new ActivitiesMap();
        newActivitiesMap.setActivity(activity);
        AppUser admin = (AppUser) em.createNamedQuery("AppUser.findByUserRole").setParameter("userRole", 0).getSingleResult();
        newActivitiesMap.setStudentEmail(admin);
        newActivitiesMap.setDateEnabled(dateEnabled);
        newActivitiesMap.setDateDisabled(dateDisabled);
        em.merge(newActivitiesMap);
    }
    
    public void removeActivitiesMaps(List<ActivitiesMap> activitiesMaps) {

        for (ActivitiesMap studentActivityMap : activitiesMaps) {
            studentActivityMap.setEnabled(false);
            em.merge(studentActivityMap);
        }
    }
    
    public void editActivitiesMaps (List<ActivitiesMap> activitiesMaps, Date startDate, Date endDate) {
        for (ActivitiesMap studentActivityMap : activitiesMaps) {
            studentActivityMap.setDateEnabled(startDate);
            studentActivityMap.setDateDisabled(endDate);
            em.merge(studentActivityMap);
        }
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
        AppUser admin = (AppUser) em.createNamedQuery("AppUser.findByUserRole").setParameter("userRole", 0).getSingleResult();
        List<ActivitiesMap> nullActivitiesMapList = em.createNamedQuery("ActivitiesMap.findByStudentEmail").setParameter("studentEmail", admin).getResultList();
        for (ActivitiesMap nullActivitiesMap : nullActivitiesMapList) {
            ActivitiesMap activitiesMap = new ActivitiesMap();
            activitiesMap.setActivity(nullActivitiesMap.getActivity());
            activitiesMap.setDateEnabled(nullActivitiesMap.getDateEnabled());
            activitiesMap.setDateDisabled(nullActivitiesMap.getDateDisabled());
            activitiesMap.setStudentEmail(user);
            em.persist(activitiesMap);
        }

    }

    public void saveUser(AppUser user) {
        em.merge(user);
    }

    public void saveActivitiesMap(ActivitiesMap activitiesMap) {
        em.merge(activitiesMap);
    }

    public void saveSkillsMap(SkillsMap skillsMap) {
        em.merge(skillsMap);
    }
    
    public void toggleActive (AppUser user) {
        user.setActive(!user.getActive());
        em.merge(user);
    }
    
    public void deleteUser (AppUser user) {
        em.remove(em.merge(user));
    }
}
