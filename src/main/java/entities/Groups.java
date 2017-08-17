/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dpattas
 */
@Entity
@Table(name = "GROUPS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Groups.findAll", query = "SELECT g FROM Groups g")
    , @NamedQuery(name = "Groups.findByEmail", query = "SELECT g FROM Groups g WHERE g.groupsPK.email = :email")
    , @NamedQuery(name = "Groups.findByGroupName", query = "SELECT g FROM Groups g WHERE g.groupsPK.groupName = :groupName")})
public class Groups implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GroupsPK groupsPK;
    @JoinColumn(name = "EMAIL", referencedColumnName = "EMAIL", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private AppUser appUser;

    public Groups() {
    }

    public Groups(GroupsPK groupsPK) {
        this.groupsPK = groupsPK;
    }

    public Groups(String email, String groupName) {
        this.groupsPK = new GroupsPK(email, groupName);
    }

    public GroupsPK getGroupsPK() {
        return groupsPK;
    }

    public void setGroupsPK(GroupsPK groupsPK) {
        this.groupsPK = groupsPK;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupsPK != null ? groupsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Groups)) {
            return false;
        }
        Groups other = (Groups) object;
        if ((this.groupsPK == null && other.groupsPK != null) || (this.groupsPK != null && !this.groupsPK.equals(other.groupsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Groups[ groupsPK=" + groupsPK + " ]";
    }
    
}
