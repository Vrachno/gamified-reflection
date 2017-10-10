/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author dpattas
 */
@Entity
@Table(name = "ACTIVITY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Activity.findAll", query = "SELECT a FROM Activity a")
    , @NamedQuery(name = "Activity.findById", query = "SELECT a FROM Activity a WHERE a.id = :id")
    , @NamedQuery(name = "Activity.findByCategoryId", query = "SELECT a FROM Activity a WHERE a.categoryId = :categoryId")})

public class Activity implements Serializable {

    @Column(name = "ENABLED")
    private Boolean enabled = false;
    @OneToMany(mappedBy = "activity")
    private List<ActivitiesMap> activitiesMapList;

    @Size(max = 255)
    @Column(name = "TITLE")
    private String title;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Category categoryId;

    public Activity() {
    }

    public Activity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Category categoryId) {
        this.categoryId = categoryId;
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
        if (!(object instanceof Activity)) {
            return false;
        }
        Activity other = (Activity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Activity[ id=" + id + " ]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @XmlTransient
    public List<ActivitiesMap> getActivitiesMapList() {
        return activitiesMapList;
    }

    public void setActivitiesMapList(List<ActivitiesMap> activitiesMapList) {
        this.activitiesMapList = activitiesMapList;
    }
    
}
