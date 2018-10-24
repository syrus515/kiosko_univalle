/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BD;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Miguel Askar
 */
@Entity
@Table(name = "medicion_personalizada")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MedicionPersonalizada.findAll", query = "SELECT m FROM MedicionPersonalizada m")
    , @NamedQuery(name = "MedicionPersonalizada.findById", query = "SELECT m FROM MedicionPersonalizada m WHERE m.id = :id")
    , @NamedQuery(name = "MedicionPersonalizada.findByIntervalo", query = "SELECT m FROM MedicionPersonalizada m WHERE m.intervalo = :intervalo")
    , @NamedQuery(name = "MedicionPersonalizada.findByDuracionMuestra", query = "SELECT m FROM MedicionPersonalizada m WHERE m.duracionMuestra = :duracionMuestra")
    , @NamedQuery(name = "MedicionPersonalizada.findByDuracionExamen", query = "SELECT m FROM MedicionPersonalizada m WHERE m.duracionExamen = :duracionExamen")
    , @NamedQuery(name = "MedicionPersonalizada.findByFecha", query = "SELECT m FROM MedicionPersonalizada m WHERE m.fecha = :fecha")
    , @NamedQuery(name = "MedicionPersonalizada.findByDetalles", query = "SELECT m FROM MedicionPersonalizada m WHERE m.detalles = :detalles")})
public class MedicionPersonalizada implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "intervalo")
    private int intervalo;
    @Basic(optional = false)
    @Column(name = "duracionMuestra")
    private int duracionMuestra;
    @Basic(optional = false)
    @Column(name = "duracionExamen")
    private int duracionExamen;
    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @Column(name = "detalles")
    private String detalles;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPersonalizada")
    private Collection<Medicion> medicionCollection;

    public MedicionPersonalizada() {
    }

    public MedicionPersonalizada(Integer id) {
        this.id = id;
    }

    public MedicionPersonalizada(Integer id, int intervalo, int duracionMuestra, int duracionExamen, Date fecha, String detalles) {
        this.id = id;
        this.intervalo = intervalo;
        this.duracionMuestra = duracionMuestra;
        this.duracionExamen = duracionExamen;
        this.fecha = fecha;
        this.detalles = detalles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(int intervalo) {
        this.intervalo = intervalo;
    }

    public int getDuracionMuestra() {
        return duracionMuestra;
    }

    public void setDuracionMuestra(int duracionMuestra) {
        this.duracionMuestra = duracionMuestra;
    }

    public int getDuracionExamen() {
        return duracionExamen;
    }

    public void setDuracionExamen(int duracionExamen) {
        this.duracionExamen = duracionExamen;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    @XmlTransient
    public Collection<Medicion> getMedicionCollection() {
        return medicionCollection;
    }

    public void setMedicionCollection(Collection<Medicion> medicionCollection) {
        this.medicionCollection = medicionCollection;
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
        if (!(object instanceof MedicionPersonalizada)) {
            return false;
        }
        MedicionPersonalizada other = (MedicionPersonalizada) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.MedicionPersonalizada[ id=" + id + " ]";
    }
    
}
