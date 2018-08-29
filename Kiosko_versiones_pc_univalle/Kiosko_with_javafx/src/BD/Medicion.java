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
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "medicion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Medicion.findAll", query = "SELECT m FROM Medicion m")
    , @NamedQuery(name = "Medicion.findById", query = "SELECT m FROM Medicion m WHERE m.id = :id")
    , @NamedQuery(name = "Medicion.findByTipo", query = "SELECT m FROM Medicion m WHERE m.tipo = :tipo")
    , @NamedQuery(name = "Medicion.findByIntervalo", query = "SELECT m FROM Medicion m WHERE m.intervalo = :intervalo")
    , @NamedQuery(name = "Medicion.findByDuracionMuestra", query = "SELECT m FROM Medicion m WHERE m.duracionMuestra = :duracionMuestra")
    , @NamedQuery(name = "Medicion.findByDuracionExamen", query = "SELECT m FROM Medicion m WHERE m.duracionExamen = :duracionExamen")
    , @NamedQuery(name = "Medicion.findByDetalles", query = "SELECT m FROM Medicion m WHERE m.detalles = :detalles")
    , @NamedQuery(name = "Medicion.findByFecha", query = "SELECT m FROM Medicion m WHERE m.fecha = :fecha")})
public class Medicion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "tipo")
    private String tipo;
    @Basic(optional = false)
    @Column(name = "intervalo")
    private int intervalo;
    @Basic(optional = false)
    @Column(name = "DuracionMuestra")
    private int duracionMuestra;
    @Basic(optional = false)
    @Column(name = "DuracionExamen")
    private int duracionExamen;
    @Column(name = "Detalles")
    private String detalles;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @Lob
    @Column(name = "ondaSPO2")
    private String ondaSPO2;
    @Basic(optional = false)
    @Lob
    @Column(name = "ondaECG1")
    private String ondaECG1;
    @Basic(optional = false)
    @Lob
    @Column(name = "ondaECG2")
    private String ondaECG2;
    @Basic(optional = false)
    @Lob
    @Column(name = "ondaRESP")
    private String ondaRESP;
    @Basic(optional = false)
    @Lob
    @Column(name = "presionSistolica")
    private String presionSistolica;
    @Basic(optional = false)
    @Lob
    @Column(name = "presionDiastolica")
    private String presionDiastolica;
    @Basic(optional = false)
    @Lob
    @Column(name = "pulso")
    private String pulso;
    @Basic(optional = false)
    @Lob
    @Column(name = "med")
    private String med;
    @Basic(optional = false)
    @Lob
    @Column(name = "ECG")
    private String ecg;
    @Basic(optional = false)
    @Lob
    @Column(name = "SPO2")
    private String spo2;
    @Basic(optional = false)
    @Lob
    @Column(name = "HR")
    private String hr;
    @Basic(optional = false)
    @Lob
    @Column(name = "RESP")
    private String resp;
    @JoinColumns({
    @JoinColumn(name = "identificacion", referencedColumnName = "identificacion", nullable = false),
    @JoinColumn(name = "identificacion", referencedColumnName = "identificacion", insertable = false, updatable = false)
  })
    
    /*@JoinColumn(name = "identificacion", referencedColumnName = "identificacion", updatable=false)
    @ManyToOne(optional = false)*/
    private Pacientes identificacion;
    
    
    /*@JoinColumn(name = "tipoid", referencedColumnName = "tipoid", updatable=false)
    @ManyToOne(optional = false)*/
    @JoinColumns({
    @JoinColumn(name = "tipoid", referencedColumnName = "tipoid", nullable = false),
    @JoinColumn(name = "tipoid", referencedColumnName = "tipoid", insertable = false, updatable = false)
  })
    private Pacientes tipoid;

    public Medicion() {
    }

    public Medicion(Integer id) {
        this.id = id;
    }

    public Medicion(Integer id, String tipo, int intervalo, int duracionMuestra, int duracionExamen, String ondaSPO2, String ondaECG1, String ondaECG2, String ondaRESP, String presionSistolica, String presionDiastolica, String pulso, String med, String ecg, String spo2, String hr, String resp) {
        this.id = id;
        this.tipo = tipo;
        this.intervalo = intervalo;
        this.duracionMuestra = duracionMuestra;
        this.duracionExamen = duracionExamen;
        this.ondaSPO2 = ondaSPO2;
        this.ondaECG1 = ondaECG1;
        this.ondaECG2 = ondaECG2;
        this.ondaRESP = ondaRESP;
        this.presionSistolica = presionSistolica;
        this.presionDiastolica = presionDiastolica;
        this.pulso = pulso;
        this.med = med;
        this.ecg = ecg;
        this.spo2 = spo2;
        this.hr = hr;
        this.resp = resp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getOndaSPO2() {
        return ondaSPO2;
    }

    public void setOndaSPO2(String ondaSPO2) {
        this.ondaSPO2 = ondaSPO2;
    }

    public String getOndaECG1() {
        return ondaECG1;
    }

    public void setOndaECG1(String ondaECG1) {
        this.ondaECG1 = ondaECG1;
    }

    public String getOndaECG2() {
        return ondaECG2;
    }

    public void setOndaECG2(String ondaECG2) {
        this.ondaECG2 = ondaECG2;
    }

    public String getOndaRESP() {
        return ondaRESP;
    }

    public void setOndaRESP(String ondaRESP) {
        this.ondaRESP = ondaRESP;
    }

    public String getPresionSistolica() {
        return presionSistolica;
    }

    public void setPresionSistolica(String presionSistolica) {
        this.presionSistolica = presionSistolica;
    }

    public String getPresionDiastolica() {
        return presionDiastolica;
    }

    public void setPresionDiastolica(String presionDiastolica) {
        this.presionDiastolica = presionDiastolica;
    }

    public String getPulso() {
        return pulso;
    }

    public void setPulso(String pulso) {
        this.pulso = pulso;
    }

    public String getMed() {
        return med;
    }

    public void setMed(String med) {
        this.med = med;
    }

    public String getEcg() {
        return ecg;
    }

    public void setEcg(String ecg) {
        this.ecg = ecg;
    }

    public String getSpo2() {
        return spo2;
    }

    public void setSpo2(String spo2) {
        this.spo2 = spo2;
    }

    public String getHr() {
        return hr;
    }

    public void setHr(String hr) {
        this.hr = hr;
    }

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public Pacientes getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(Pacientes identificacion) {
        this.identificacion = identificacion;
    }

    public Pacientes getTipoid() {
        return tipoid;
    }

    public void setTipoid(Pacientes tipoid) {
        this.tipoid = tipoid;
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
        if (!(object instanceof Medicion)) {
            return false;
        }
        Medicion other = (Medicion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.Medicion[ id=" + id + " ]";
    }
    
}
