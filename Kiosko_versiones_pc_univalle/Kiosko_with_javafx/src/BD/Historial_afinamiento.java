package BD;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Familia
 */
@Entity
@Table(name = "Historial_afinamiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Historial_afinamiento.findAll", query = "SELECT ha FROM Historial_afinamiento ha"),
    @NamedQuery(name = "Historial_afinamiento.findByTipoid", query = "SELECT ha FROM Historial_afinamiento ha WHERE ha.historial_afinamientoPK.tipoid = :tipoid"),
    @NamedQuery(name = "Historial_afinamiento.findByIdentificacion", query = "SELECT ha FROM Historial_afinamiento ha WHERE ha.historial_afinamientoPK.identificacion = :identificacion"),
    @NamedQuery(name = "Historial_afinamiento.findByFecha", query = "SELECT ha FROM Historial_afinamiento ha WHERE ha.fecha = :fecha"),
    @NamedQuery(name = "Historial_afinamiento.findByHora", query = "SELECT ha FROM Historial_afinamiento ha WHERE ha.hora = :hora"),
    @NamedQuery(name = "Historial_afinamiento.findByPeso", query = "SELECT ha FROM Historial_afinamiento ha WHERE ha.peso = :peso"),
    @NamedQuery(name = "Historial_afinamiento.findByPresion", query = "SELECT ha FROM Historial_afinamiento ha WHERE ha.presion = :presion"),
    @NamedQuery(name = "Historial_afinamiento.findByBrazo", query = "SELECT ha FROM Historial_afinamiento ha WHERE ha.brazo = :brazo"),
    @NamedQuery(name = "Historial_afinamiento.findByPosicion", query = "SELECT ha FROM Historial_afinamiento ha WHERE ha.posicion = :posicion"),
    @NamedQuery(name = "Historial_afinamiento.findByJornada", query = "SELECT ha FROM Historial_afinamiento ha WHERE ha.jornada = :jornada"),
    @NamedQuery(name = "Historial_afinamiento.findByEstadoInicial", query = "SELECT ha FROM Historial_afinamiento ha WHERE ha.estadoInicial = :estadoInicial")})
public class Historial_afinamiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected Historial_afinamientoPK historial_afinamientoPK;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "hora")
    private Double hora;
    @Column(name = "peso")    
    private Double peso;
    @Column(name = "presion", length = 30)
    private String presion;
    @Column(name = "brazo", length = 30)
    private String brazo;
    @Column(name = "posicion", length = 30)
    private String posicion;
    @Column(name = "jornada", length = 30)
    private String jornada;
    @Column(name = "estadoInicial", length = 30)
    private String estadoInicial;
    
    public Historial_afinamiento() {
    }

    public Historial_afinamiento(Historial_afinamientoPK historial_afinamientoPK) {
        this.historial_afinamientoPK = historial_afinamientoPK;
    }

    public Historial_afinamiento(String tipoid, String identificacion) {
        this.historial_afinamientoPK = new Historial_afinamientoPK(tipoid, identificacion);
    }

    public Historial_afinamientoPK getHistorial_afinamientoPK() {
        return historial_afinamientoPK;
    }

    public void setAntecedentesfamiliaresPK(Historial_afinamientoPK historial_afinamientoPK) {
        this.historial_afinamientoPK = historial_afinamientoPK;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getHora() {
        return hora;
    }

    public void setHora(Double hora) {
        this.hora = hora;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getPresion() {
        return presion;
    }

    public void setPresion(String presion) {
        this.presion = presion;
    }
    
    public String getBrazo() {
        return brazo;
    }

    public void setBrazo(String brazo) {
        this.brazo = brazo;
    }
    
    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }
    
    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }
    
    public String getEstadoInicial() {
        return 	estadoInicial;
    }

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (historial_afinamientoPK != null ? historial_afinamientoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Historial_afinamiento)) {
            return false;
        }
        Historial_afinamiento other = (Historial_afinamiento) object;
        if ((this.historial_afinamientoPK == null && other.historial_afinamientoPK != null) || (this.historial_afinamientoPK != null && !this.historial_afinamientoPK.equals(other.historial_afinamientoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.Historial_afinamiento[ historial_afinamientoPK=" + historial_afinamientoPK + " ]";
    }
    
}
