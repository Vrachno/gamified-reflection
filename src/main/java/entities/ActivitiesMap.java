/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dpattas
 */
@Entity
@Table(name = "ACTIVITIES_MAP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ActivitiesMap.findAll", query = "SELECT a FROM ActivitiesMap a")
    , @NamedQuery(name = "ActivitiesMap.findById", query = "SELECT a FROM ActivitiesMap a WHERE a.id = :id")
    , @NamedQuery(name = "ActivitiesMap.findByLogged", query = "SELECT a FROM ActivitiesMap a WHERE a.logged = :logged")
    , @NamedQuery(name = "ActivitiesMap.findByAnswer", query = "SELECT a FROM ActivitiesMap a WHERE a.answer = :answer")
    , @NamedQuery(name = "ActivitiesMap.findByStudentEmail", query = "SELECT a FROM ActivitiesMap a WHERE a.studentEmail = :studentEmail")
    , @NamedQuery(name = "ActivitiesMap.findByActivity", query = "SELECT a FROM ActivitiesMap a WHERE a.activity = :activity")
    , @NamedQuery(name = "ActivitiesMap.findByDateAnswered", query = "SELECT a FROM ActivitiesMap a WHERE a.dateAnswered = :dateAnswered")
    , @NamedQuery(name = "ActivitiesMap.findNotLoggedByStudent", query = "SELECT a FROM ActivitiesMap a WHERE a.studentEmail = :studentEmail AND a.logged = :logged AND a.activity.enabled = true")})
public class ActivitiesMap implements Serializable {

    @Column(name = "NEW_SKILL_LEVEL")
    private Integer newSkillLevel;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "LOGGED")
    private Boolean logged = false;
    @Column(name = "ANSWER")
    private Integer answer;
    @Column(name = "DATE_ANSWERED")
    @Temporal(TemporalType.DATE)
    private Date dateAnswered;
    @JoinColumn(name = "ACTIVITY", referencedColumnName = "ID")
    @ManyToOne
    private Activity activity;
    @JoinColumn(name = "STUDENT_EMAIL", referencedColumnName = "EMAIL")
    @ManyToOne
    private AppUser studentEmail;

    public ActivitiesMap() {
    }

    public ActivitiesMap(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getLogged() {
        return logged;
    }

    public void setLogged(Boolean logged) {
        this.logged = logged;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(Integer answer) {
        this.answer = answer;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public AppUser getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(AppUser studentEmail) {
        this.studentEmail = studentEmail;
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
        if (!(object instanceof ActivitiesMap)) {
            return false;
        }
        ActivitiesMap other = (ActivitiesMap) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ActivitiesMap[ id=" + id + " ]";
    }

    public Date getDateAnswered() {
        return dateAnswered;
    }

    public void setDateAnswered(Date dateAnswered) {
        this.dateAnswered = dateAnswered;
    }

    public Integer getNewSkillLevel() {
        return newSkillLevel;
    }

    public void setNewSkillLevel(Integer newSkillLevel) {
        this.newSkillLevel = newSkillLevel;
    }
    
}
