/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author dpattas
 */
@Entity
@Table(name = "APP_USER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AppUser.findAll", query = "SELECT u FROM AppUser u")
    , @NamedQuery(name = "AppUser.findByEmail", query = "SELECT u FROM AppUser u WHERE u.email = :email")
    , @NamedQuery(name = "AppUser.findByFirstName", query = "SELECT u FROM AppUser u WHERE u.firstName = :firstName")
    , @NamedQuery(name = "AppUser.findByLastName", query = "SELECT u FROM AppUser u WHERE u.lastName = :lastName")
    , @NamedQuery(name = "AppUser.findByUserRole", query = "SELECT u FROM AppUser u WHERE u.userRole = :userRole")
    , @NamedQuery(name = "AppUser.findByPassword", query = "SELECT u FROM AppUser u WHERE u.password = :password")})
public class AppUser implements Serializable {

    @OneToMany(mappedBy = "studentEmail")
    private List<ActivitiesMap> activitiesMapList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appUser")
    private List<Groups> groupsList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studentEmail")
    private List<SkillsMap> skillsMapList;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "EMAIL")
    private String email;
    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "USER_ROLE")
    private Short userRole;
    @Size(max = 255)
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "DATE_REGISTERED")
    @Temporal(TemporalType.DATE)
    private Date dateRegistered;
    @OneToMany(mappedBy = "studentEmail")
    private List<SkillsMap> skills;

    public AppUser() {
    }

    public AppUser(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (email != null ? email.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppUser)) {
            return false;
        }
        AppUser other = (AppUser) object;
        if ((this.email == null && other.email != null) || (this.email != null && !this.email.equals(other.email))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.AppUser[ email=" + email + " ]";
    }

    @XmlTransient
    public List<SkillsMap> getSkillsMapList() {
        return skillsMapList;
    }

    public void setSkillsMapList(List<SkillsMap> skillsMapList) {
        this.skillsMapList = skillsMapList;
    }

    public Short getUserRole() {
        return userRole;
    }

    public void setUserRole(Short userRole) {
        this.userRole = userRole;
    }

    @XmlTransient
    public List<Groups> getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(List<Groups> groupsList) {
        this.groupsList = groupsList;
    }

    @XmlTransient
    public List<ActivitiesMap> getActivitiesMapList() {
        return activitiesMapList;
    }

    public void setActivitiesMapList(List<ActivitiesMap> activitiesMapList) {
        this.activitiesMapList = activitiesMapList;
    }

    public Date getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(Date dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

}
