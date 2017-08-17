/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.SkillsMap;
import entities.AppUser;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;

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

    /**
     * Creates a new instance of indexController
     */
    public StudentsController() {
    }

    @PostConstruct
    public void init() {
        
        students = em.createNamedQuery("AppUser.findByUserRole").setParameter("userRole", 1).getResultList();
        for (AppUser student : students) {
            student.setSkillsMapList(em.createNamedQuery("SkillsMap.findByStudentEmail").setParameter("studentEmail", student).getResultList());
            aux.createBarModel(student, barModel);
        }

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
    }   
    

    public HorizontalBarChartModel getBarModel() {
        return barModel;
    }



}
