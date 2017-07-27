/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.Student;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    }
    
    public void setStudentSkills(Student student) {
        student.setSkillsMapList(em.createNamedQuery("SkillsMap.findByStudentId").setParameter("studentId", student).getResultList());
    }
    

}
