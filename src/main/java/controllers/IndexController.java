/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.SkillsMap;
import entities.Student;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
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
@ManagedBean(name = "indexController")
@ViewScoped
public class IndexController implements Serializable {

    @PersistenceContext
    EntityManager em;

    private List<Student> students;
    private Student selectedStudent;
    private HorizontalBarChartModel barModel;

    /**
     * Creates a new instance of indexController
     */
    public IndexController() {
    }

    @PostConstruct
    public void init() {
        
        students = em.createNamedQuery("Student.findAll").getResultList();
        for (Student student : students) {
            student.setSkillsMapList(em.createNamedQuery("SkillsMap.findByStudentId").setParameter("studentId", student).getResultList());
        }
        barModel = new HorizontalBarChartModel();

    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Student getSelectedStudent() {
        return selectedStudent;
    }

    public void setSelectedStudent(Student selectedStudent) {
        this.selectedStudent = selectedStudent;
        createBarModel(selectedStudent);
    }   
    
    public void setStudentSkills(Student student) {
        student.setSkillsMapList(em.createNamedQuery("SkillsMap.findByStudentId").setParameter("studentId", student).getResultList());
    }

    public HorizontalBarChartModel getBarModel() {
        return barModel;
    }

    public void createBarModel(Student student) {

        barModel = new HorizontalBarChartModel();

        ChartSeries skills = new ChartSeries();
            for (SkillsMap skillsMap : student.getSkillsMapList()) {
                skills.set(skillsMap.getCategoryId().getTitle(), skillsMap.getSkillLevel());
            }

        barModel.addSeries(skills);

        barModel.setTitle("Student Skills");
        barModel.setStacked(true);

        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Level");
        xAxis.setMin(0);
        xAxis.setMax(200);

        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Skill");

    }



}
