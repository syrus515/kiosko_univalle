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
public class Historial_parametrosPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "tipoid")
    private String tipoid;
    @Basic(optional = false)
    @Column(name = "identificacion")
    private String identificacion;

    public Historial_parametrosPK() {
    }

    public Historial_parametrosPK(String tipoid, String identificacion) {
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
        if (!(object instanceof Historial_parametrosPK)) {
            return false;
        }
        Historial_parametrosPK other = (Historial_parametrosPK) object;
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
        return "BD.AntecedentesfamiliaresPK[ tipoid=" + tipoid + ", identificacion=" + identificacion + " ]";
    }
    
}
