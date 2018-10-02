/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BD;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Miguel Askar
 */
@Embeddable
public class HistorialAfinamientoPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "tipoid")
    private String tipoid;
    @Basic(optional = false)
    @Column(name = "identificacion")
    private String identificacion;
    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    public HistorialAfinamientoPK() {
    }

    public HistorialAfinamientoPK(String tipoid, String identificacion, Date fecha) {
        this.tipoid = tipoid;
        this.identificacion = identificacion;
        this.fecha = fecha;
    }

    public String getTipoid() {
        return tipoid;
    }

    public void setTipoid(String tipoid) {
        this.tipoid = tipoid;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tipoid != null ? tipoid.hashCode() : 0);
        hash += (identificacion != null ? identificacion.hashCode() : 0);
        hash += (fecha != null ? fecha.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HistorialAfinamientoPK)) {
            return false;
        }
        HistorialAfinamientoPK other = (HistorialAfinamientoPK) object;
        if ((this.tipoid == null && other.tipoid != null) || (this.tipoid != null && !this.tipoid.equals(other.tipoid))) {
            return false;
        }
        if ((this.identificacion == null && other.identificacion != null) || (this.identificacion != null && !this.identificacion.equals(other.identificacion))) {
            return false;
        }
        if ((this.fecha == null && other.fecha != null) || (this.fecha != null && !this.fecha.equals(other.fecha))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.HistorialAfinamientoPK[ tipoid=" + tipoid + ", identificacion=" + identificacion + ", fecha=" + fecha + " ]";
    }
    
}
