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
@Table(name = "Historial_parametros")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Historial_parametros.findAll", query = "SELECT hp FROM Historial_parametros hp"),
    @NamedQuery(name = "Historial_parametros.findByTipoid", query = "SELECT hp FROM Historial_parametros hp WHERE hp.historial_parametrosPK.tipoid = :tipoid"),
    @NamedQuery(name = "Historial_parametros.findByIdentificacion", query = "SELECT hp FROM Historial_parametros hp WHERE hp.historial_parametrosPK.identificacion = :identificacion"),
    @NamedQuery(name = "Historial_parametros.findByFecha", query = "SELECT hp FROM Historial_parametros hp WHERE hp.fecha = :fecha"),
    @NamedQuery(name = "Historial_parametros.findByHora", query = "SELECT hp FROM Historial_parametros hp WHERE hp.hora = :hora"),
    @NamedQuery(name = "Historial_parametros.findByHr_ecg", query = "SELECT hp FROM Historial_parametros hp WHERE hp.hr_ecg = :hr_ecg"),
    @NamedQuery(name = "Historial_parametros.findByHr_spo2", query = "SELECT hp FROM Historial_parametros hp WHERE hp.hr_spo2 = :hr_spo2"),
    @NamedQuery(name = "Historial_parametros.findBySpo2oxi", query = "SELECT hp FROM Historial_parametros hp WHERE hp.spo2oxi = :spo2oxi"),
    @NamedQuery(name = "Historial_parametros.findByFrec_resp", query = "SELECT hp FROM Historial_parametros hp WHERE hp.frec_resp = :frec_resp"),
    @NamedQuery(name = "Historial_parametros.findByPres_Sist", query = "SELECT hp FROM Historial_parametros hp WHERE hp.pres_Sist = :pres_Sist"),
    @NamedQuery(name = "Historial_parametros.findByPres_diast", query = "SELECT hp FROM Historial_parametros hp WHERE hp.pres_diast = :pres_diast"),
    @NamedQuery(name = "Historial_parametros.findByPres_media", query = "SELECT hp FROM Historial_parametros hp WHERE hp.pres_media = :pres_media"),
    @NamedQuery(name = "Historial_parametros.findByPres_rate", query = "SELECT hp FROM Historial_parametros hp WHERE hp.pres_rate = :pres_rate"),
    @NamedQuery(name = "Historial_parametros.findByTemperatura", query = "SELECT hp FROM Historial_parametros hp WHERE hp.temperatura = :temperatura"),
    @NamedQuery(name = "Historial_parametros.findByPeso", query = "SELECT hp FROM Historial_parametros hp WHERE hp.peso = :peso"),
    @NamedQuery(name = "Historial_parametros.findByPorc_agua", query = "SELECT hp FROM Historial_parametros hp WHERE hp.porc_agua = :porc_agua"),
    @NamedQuery(name = "Historial_parametros.findByGrasa_corporal", query = "SELECT hp FROM Historial_parametros hp WHERE hp.grasa_corporal = :grasa_corporal"),
    @NamedQuery(name = "Historial_parametros.findByMasa_muscular", query = "SELECT hp FROM Historial_parametros hp WHERE hp.masa_muscular = :masa_muscular"),
    @NamedQuery(name = "Historial_parametros.findByIMC", query = "SELECT hp FROM Historial_parametros hp WHERE hp.imc = :imc")})
public class Historial_parametros implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected Historial_parametrosPK historial_parametrosPK;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "hora")
    private Double hora;
    @Column(name = "hr_ecg")    
    private Integer hr_ecg;
    @Column(name = "hr_spo2")
    private Integer hr_spo2;
    @Column(name = "spo2oxi")
    private Integer spo2oxi;
    @Column(name = "frec_resp")
    private Integer frec_resp;
    @Column(name = "pres_Sist")
    private Integer pres_Sist;
    @Column(name = "pres_diast")
    private Integer pres_diast;
    @Column(name = "pres_media")
    private Integer pres_media;
    @Column(name = "pres_rate")
    private Integer pres_rate;
    @Column(name = "temperatura")
    private Integer temperatura;    
    @Column(name = "peso")
    private Double peso;
    @Column(name = "peso")
    private Double porc_agua;
    @Column(name = "porc_agua")
    private Double grasa_corporal;
    @Column(name = "masa_muscular")
    private Double masa_muscular;
    @Column(name = "imc")
    private Double imc;
    
    public Historial_parametros() {
    }

    public Historial_parametros(Historial_parametrosPK historial_parametrosPK) {
        this.historial_parametrosPK = historial_parametrosPK;
    }

    public Historial_parametros(String tipoid, String identificacion) {
        this.historial_parametrosPK = new Historial_parametrosPK(tipoid, identificacion);
    }

    public Historial_parametrosPK getHistorial_parametrosPK() {
        return historial_parametrosPK;
    }

    public void setHistorial_parametrosPK(Historial_parametrosPK historial_parametrosPK) {
        this.historial_parametrosPK = historial_parametrosPK;
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

    public Integer getHr_ecg() {
        return hr_ecg;
    }

    public void setHr_ecg(Integer hr_ecg) {
        this.hr_ecg = hr_ecg;
    }
    
    public Integer getHr_spo2() {
        return hr_spo2;
    }

    public void setHr_spo2(Integer hr_spo2) {
        this.hr_spo2 = hr_spo2;
    }
    
    public Integer getSpo2oxi() {
        return spo2oxi;
    }

    public void setSpo2oxi(Integer spo2oxi) {
        this.spo2oxi = spo2oxi;
    }
    
    public Integer getFrec_resp() {
        return frec_resp;
    }
    
    public void setFrec_resp(Integer frec_resp) {
        this.frec_resp = frec_resp;
    }
    
    public Integer getPres_Sist() {
        return pres_Sist;
    }
    
    public void setPres_Sist(Integer pres_Sist) {
        this.pres_Sist = pres_Sist;
    }
    
    public Integer getPres_diast() {
        return pres_diast;
    }
    
    public void setPres_diast(Integer pres_diast) {
        this.pres_diast = pres_diast;
    }
    
    public Integer getPres_media() {
        return pres_media;
    }
    
    public void setPres_media(Integer pres_media) {
        this.pres_media = pres_media;
    }
    
    public Integer getPres_rate() {
        return pres_rate;
    }
    
    public void setPres_rate(Integer pres_rate) {
        this.pres_rate = pres_rate;
    }
    
    public Integer getTemperatura() {
        return temperatura;
    }
    
    public void setTemperatura(Integer temperatura) {
        this.temperatura = temperatura;
    }
    
    public Double getPeso() {
        return peso;
    }
    
    public void setPeso(double peso) {
        this.peso = peso;
    }

    public Double getPorc_agua() {
        return porc_agua;
    }
    
    public void setPorc_agua(double porc_agua) {
        this.porc_agua = porc_agua;
    }
    
    public Double getGrasa_corporal() {
        return grasa_corporal;
    }
    
    public void setGrasa_corporal(double grasa_corporal) {
        this.grasa_corporal = grasa_corporal;
    }
    
    public Double getMasa_muscular() {
        return masa_muscular;
    }
    
    public void setMasa_muscular(double masa_muscular) {
        this.masa_muscular = masa_muscular;
    }
    
    public Double getIMC() {
        return 	imc;
    }
    
    public void setIMC(double imc) {
        this.imc = imc;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (historial_parametrosPK != null ? historial_parametrosPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Historial_parametros)) {
            return false;
        }
        Historial_parametros other = (Historial_parametros) object;
        if ((this.historial_parametrosPK == null && other.historial_parametrosPK != null) || (this.historial_parametrosPK != null && !this.historial_parametrosPK.equals(other.historial_parametrosPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.Historial_parametros[ historial_parametrosPK=" + historial_parametrosPK + " ]";
    }   
}