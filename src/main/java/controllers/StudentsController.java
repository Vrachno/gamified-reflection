/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.AppUser;
import entities.Category;
import entities.SkillsMap;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.chart.HorizontalBarChartModel;

import org.primefaces.model.chart.LineChartModel;

/**
 *
 * @author dpattas
 */
@ManagedBean(name = "studentsController")
@ViewScoped
public class StudentsController implements Serializable {

    @PersistenceContext
    EntityManager em;

    @EJB
    Auxilliary aux;

    private List<AppUser> students;
    private AppUser selectedStudent;
    private HorizontalBarChartModel barModel;
    private LineChartModel lineModel;
    private List<Category> categories;
    private Object[] sortedStudents;
    private Category selectedCategory;

    /**
     * Creates a new instance of indexController
     */
    public StudentsController() {
    }

    @PostConstruct
    public void init() {
        //selectedCategory = new Category();
        students = em.createNamedQuery("AppUser.findByUserRole").setParameter("userRole", 1).getResultList();
        categories = em.createNamedQuery("Category.findAll").getResultList();
        aux.setStudentsOverallScores(students);
        sortedStudents = (Object[]) aux.getStudentsOverallScores().entrySet().toArray();
    }

    public List<AppUser> getStudents() {
        return students;
    }

    public void setStudents(List<AppUser> students) {
        this.students = students;
    }

    public AppUser getSelectedStudent() {
        return selectedStudent;
    }

    public void setSelectedStudent(AppUser selectedStudent) {
        this.selectedStudent = selectedStudent;
        barModel = aux.createBarModel(selectedStudent, barModel);
        lineModel = aux.createLineModel(selectedStudent, lineModel);
    }

    public HorizontalBarChartModel getBarModel() {
        return barModel;
    }

    public LineChartModel getLineModel() {
        return lineModel;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public Object[] getSortedStudents() {
        return sortedStudents;
    }

    public void setSortedStudents(Object[] sortedStudents) {
        this.sortedStudents = sortedStudents;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
        aux.setStudentScores(selectedCategory);
        if (selectedCategory == null) {
            sortedStudents = (Object[]) aux.getStudentsOverallScores().entrySet().toArray();
        } else {
            sortedStudents = (Object[]) aux.getStudentsScores().entrySet().toArray();
        }
    }

}
