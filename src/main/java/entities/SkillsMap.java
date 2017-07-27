/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dpattas
 */
@Entity
@Table(name = "SKILLS_MAP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SkillsMap.findAll", query = "SELECT s FROM SkillsMap s")
    , @NamedQuery(name = "SkillsMap.findById", query = "SELECT s FROM SkillsMap s WHERE s.id = :id")
    , @NamedQuery(name = "SkillsMap.findBySkillLevel", query = "SELECT s FROM SkillsMap s WHERE s.skillLevel = :skillLevel")
    , @NamedQuery(name = "SkillsMap.findByStudentId", query = "SELECT s FROM SkillsMap s WHERE s.studentId = :studentId")})
public class SkillsMap implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SKILL_LEVEL")
    private int skillLevel;
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Category categoryId;
    @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Student studentId;

    public SkillsMap() {
    }

    public SkillsMap(Long id) {
        this.id = id;
    }

    public SkillsMap(Long id, int skillLevel) {
        this.id = id;
        this.skillLevel = skillLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
    }

    public Student getStudentId() {
        return studentId;
    }

    public void setStudentId(Student studentId) {
        this.studentId = studentId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SkillsMap)) {
            return false;
        }
        SkillsMap other = (SkillsMap) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.SkillsMap[ id=" + id + " ]";
    }

}
