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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

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
    @Basic(optional = false)
    @Lob
    @Column(name = "pulso")
    private String pulso;
    @Basic(optional = false)
    @Lob
    @Column(name = "pulsoMinutes")
    private String pulsoMinutes;
    @Basic(optional = false)
    @Lob
    @Column(name = "eCG")
    private String eCG;
    @Basic(optional = false)
    @Lob
    @Column(name = "eCGMinutes")
    private String eCGMinutes;
    @Basic(optional = false)
    @Lob
    @Column(name = "sPO2")
    private String sPO2;
    @Basic(optional = false)
    @Lob
    @Column(name = "sPO2Minutes")
    private String sPO2Minutes;
    @Basic(optional = false)
    @Lob
    @Column(name = "heartRate")
    private String heartRate;
    @Basic(optional = false)
    @Lob
    @Column(name = "heartRateMinutes")
    private String heartRateMinutes;
    @Basic(optional = false)
    @Lob
    @Column(name = "rESP")
    private String rESP;
    @Basic(optional = false)
    @Lob
    @Column(name = "rESPMinutes")
    private String rESPMinutes;

    public MedicionPersonalizada() {
    }

    public MedicionPersonalizada(Integer id) {
        this.id = id;
    }

    public MedicionPersonalizada(Integer id, int intervalo, int duracionMuestra, int duracionExamen, Date fecha, String detalles, String pulso, String pulsoMinutes, String eCG, String eCGMinutes, String sPO2, String sPO2Minutes, String heartRate, String heartRateMinutes, String rESP, String rESPMinutes) {
        this.id = id;
        this.intervalo = intervalo;
        this.duracionMuestra = duracionMuestra;
        this.duracionExamen = duracionExamen;
        this.fecha = fecha;
        this.detalles = detalles;
        this.pulso = pulso;
        this.pulsoMinutes = pulsoMinutes;
        this.eCG = eCG;
        this.eCGMinutes = eCGMinutes;
        this.sPO2 = sPO2;
        this.sPO2Minutes = sPO2Minutes;
        this.heartRate = heartRate;
        this.heartRateMinutes = heartRateMinutes;
        this.rESP = rESP;
        this.rESPMinutes = rESPMinutes;
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

    public String getPulso() {
        return pulso;
    }

    public void setPulso(String pulso) {
        this.pulso = pulso;
    }

    public String getPulsoMinutes() {
        return pulsoMinutes;
    }

    public void setPulsoMinutes(String pulsoMinutes) {
        this.pulsoMinutes = pulsoMinutes;
    }

    public String getECG() {
        return eCG;
    }

    public void setECG(String eCG) {
        this.eCG = eCG;
    }

    public String getECGMinutes() {
        return eCGMinutes;
    }

    public void setECGMinutes(String eCGMinutes) {
        this.eCGMinutes = eCGMinutes;
    }

    public String getSPO2() {
        return sPO2;
    }

    public void setSPO2(String sPO2) {
        this.sPO2 = sPO2;
    }

    public String getSPO2Minutes() {
        return sPO2Minutes;
    }

    public void setSPO2Minutes(String sPO2Minutes) {
        this.sPO2Minutes = sPO2Minutes;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getHeartRateMinutes() {
        return heartRateMinutes;
    }

    public void setHeartRateMinutes(String heartRateMinutes) {
        this.heartRateMinutes = heartRateMinutes;
    }

    public String getRESP() {
        return rESP;
    }

    public void setRESP(String rESP) {
        this.rESP = rESP;
    }

    public String getRESPMinutes() {
        return rESPMinutes;
    }

    public void setRESPMinutes(String rESPMinutes) {
        this.rESPMinutes = rESPMinutes;
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
