/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BD;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Familia
 */
@Embeddable
public class PacientesPK implements Serializable {

    @Basic(optional = false)
    @Column(nullable = false, length = 2)
    private String tipoid;
    @Basic(optional = false)
    @Column(nullable = false, length = 20)
    private String identificacion;

    public PacientesPK() {
    }

    public PacientesPK(String tipoid, String identificacion) {
        this.tipoid = tipoid;
        this.identificacion = identificacion;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tipoid != null ? tipoid.hashCode() : 0);
        hash += (identificacion != null ? identificacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PacientesPK)) {
            return false;
        }
        PacientesPK other = (PacientesPK) object;
        if ((this.tipoid == null && other.tipoid != null) || (this.tipoid != null && !this.tipoid.equals(other.tipoid))) {
            return false;
        }
        if ((this.identificacion == null && other.identificacion != null) || (this.identificacion != null && !this.identificacion.equals(other.identificacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.PacientesPK[ tipoid=" + tipoid + ", identificacion=" + identificacion + " ]";
    }
    
}
