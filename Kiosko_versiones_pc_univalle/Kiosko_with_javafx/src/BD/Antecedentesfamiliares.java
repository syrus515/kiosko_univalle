/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BD;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Familia
 */
@Entity
@Table(name = "antecedentesfamiliares")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Antecedentesfamiliares.findAll", query = "SELECT a FROM Antecedentesfamiliares a"),
    @NamedQuery(name = "Antecedentesfamiliares.findByTipoid", query = "SELECT a FROM Antecedentesfamiliares a WHERE a.antecedentesfamiliaresPK.tipoid = :tipoid"),
    @NamedQuery(name = "Antecedentesfamiliares.findByIdentificacion", query = "SELECT a FROM Antecedentesfamiliares a WHERE a.antecedentesfamiliaresPK.identificacion = :identificacion"),
    @NamedQuery(name = "Antecedentesfamiliares.findByDiabetes", query = "SELECT a FROM Antecedentesfamiliares a WHERE a.diabetes = :diabetes"),
    @NamedQuery(name = "Antecedentesfamiliares.findByHipertension", query = "SELECT a FROM Antecedentesfamiliares a WHERE a.hipertension = :hipertension"),
    @NamedQuery(name = "Antecedentesfamiliares.findByInfartos", query = "SELECT a FROM Antecedentesfamiliares a WHERE a.infartos = :infartos"),
    @NamedQuery(name = "Antecedentesfamiliares.findByAcv", query = "SELECT a FROM Antecedentesfamiliares a WHERE a.acv = :acv")})
public class Antecedentesfamiliares implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AntecedentesfamiliaresPK antecedentesfamiliaresPK;
    @Column(name = "diabetes")
    private Integer diabetes;
    @Column(name = "hipertension")
    private Integer hipertension;
    @Column(name = "infartos")
    private Integer infartos;
    @Column(name = "acv")
    private Integer acv;

    public Antecedentesfamiliares() {
    }

    public Antecedentesfamiliares(AntecedentesfamiliaresPK antecedentesfamiliaresPK) {
        this.antecedentesfamiliaresPK = antecedentesfamiliaresPK;
    }

    public Antecedentesfamiliares(String tipoid, String identificacion) {
        this.antecedentesfamiliaresPK = new AntecedentesfamiliaresPK(tipoid, identificacion);
    }

    public AntecedentesfamiliaresPK getAntecedentesfamiliaresPK() {
        return antecedentesfamiliaresPK;
    }

    public void setAntecedentesfamiliaresPK(AntecedentesfamiliaresPK antecedentesfamiliaresPK) {
        this.antecedentesfamiliaresPK = antecedentesfamiliaresPK;
    }

    public Integer getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(Integer diabetes) {
        this.diabetes = diabetes;
    }

    public Integer getHipertension() {
        return hipertension;
    }

    public void setHipertension(Integer hipertension) {
        this.hipertension = hipertension;
    }

    public Integer getInfartos() {
        return infartos;
    }

    public void setInfartos(Integer infartos) {
        this.infartos = infartos;
    }

    public Integer getAcv() {
        return acv;
    }

    public void setAcv(Integer acv) {
        this.acv = acv;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (antecedentesfamiliaresPK != null ? antecedentesfamiliaresPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Antecedentesfamiliares)) {
            return false;
        }
        Antecedentesfamiliares other = (Antecedentesfamiliares) object;
        if ((this.antecedentesfamiliaresPK == null && other.antecedentesfamiliaresPK != null) || (this.antecedentesfamiliaresPK != null && !this.antecedentesfamiliaresPK.equals(other.antecedentesfamiliaresPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.Antecedentesfamiliares[ antecedentesfamiliaresPK=" + antecedentesfamiliaresPK + " ]";
    }
    
}
