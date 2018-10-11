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
@Table(name = "historial_afinamiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HistorialAfinamiento.findAll", query = "SELECT h FROM HistorialAfinamiento h")
    , @NamedQuery(name = "HistorialAfinamiento.findById", query = "SELECT h FROM HistorialAfinamiento h WHERE h.id = :id")
    , @NamedQuery(name = "HistorialAfinamiento.findByTipoIdentificacion", query = "SELECT h FROM HistorialAfinamiento h WHERE h.tipoIdentificacion = :tipoIdentificacion")
    , @NamedQuery(name = "HistorialAfinamiento.findByIdentificacion", query = "SELECT h FROM HistorialAfinamiento h WHERE h.identificacion = :identificacion")
    , @NamedQuery(name = "HistorialAfinamiento.findByFecha", query = "SELECT h FROM HistorialAfinamiento h WHERE h.fecha = :fecha")
    , @NamedQuery(name = "HistorialAfinamiento.findByPeso", query = "SELECT h FROM HistorialAfinamiento h WHERE h.peso = :peso")
    , @NamedQuery(name = "HistorialAfinamiento.findByBrazo", query = "SELECT h FROM HistorialAfinamiento h WHERE h.brazo = :brazo")
    , @NamedQuery(name = "HistorialAfinamiento.findByPosicion", query = "SELECT h FROM HistorialAfinamiento h WHERE h.posicion = :posicion")
    , @NamedQuery(name = "HistorialAfinamiento.findByJornada", query = "SELECT h FROM HistorialAfinamiento h WHERE h.jornada = :jornada")
    , @NamedQuery(name = "HistorialAfinamiento.findByEstadoInicial", query = "SELECT h FROM HistorialAfinamiento h WHERE h.estadoInicial = :estadoInicial")
    , @NamedQuery(name = "HistorialAfinamiento.findByPresDiastolica", query = "SELECT h FROM HistorialAfinamiento h WHERE h.presDiastolica = :presDiastolica")
    , @NamedQuery(name = "HistorialAfinamiento.findByPresSistolica", query = "SELECT h FROM HistorialAfinamiento h WHERE h.presSistolica = :presSistolica")
    , @NamedQuery(name = "HistorialAfinamiento.findByDetalles", query = "SELECT h FROM HistorialAfinamiento h WHERE h.detalles = :detalles")
    , @NamedQuery(name = "HistorialAfinamiento.findByGrasaCorporal", query = "SELECT h FROM HistorialAfinamiento h WHERE h.grasaCorporal = :grasaCorporal")
    , @NamedQuery(name = "HistorialAfinamiento.findByPorcentajeAgua", query = "SELECT h FROM HistorialAfinamiento h WHERE h.porcentajeAgua = :porcentajeAgua")
    , @NamedQuery(name = "HistorialAfinamiento.findByMasaMuscular", query = "SELECT h FROM HistorialAfinamiento h WHERE h.masaMuscular = :masaMuscular")
    , @NamedQuery(name = "HistorialAfinamiento.findByImc", query = "SELECT h FROM HistorialAfinamiento h WHERE h.imc = :imc")})
public class HistorialAfinamiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "tipoIdentificacion")
    private String tipoIdentificacion;
    @Basic(optional = false)
    @Column(name = "identificacion")
    private String identificacion;
    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @Column(name = "peso")
    private double peso;
    @Basic(optional = false)
    @Column(name = "brazo")
    private String brazo;
    @Basic(optional = false)
    @Column(name = "posicion")
    private String posicion;
    @Basic(optional = false)
    @Column(name = "jornada")
    private String jornada;
    @Basic(optional = false)
    @Column(name = "estadoInicial")
    private String estadoInicial;
    @Basic(optional = false)
    @Column(name = "presDiastolica")
    private int presDiastolica;
    @Basic(optional = false)
    @Column(name = "presSistolica")
    private int presSistolica;
    @Basic(optional = false)
    @Column(name = "Detalles")
    private String detalles;
    @Basic(optional = false)
    @Column(name = "GrasaCorporal")
    private float grasaCorporal;
    @Basic(optional = false)
    @Column(name = "PorcentajeAgua")
    private float porcentajeAgua;
    @Basic(optional = false)
    @Column(name = "MasaMuscular")
    private float masaMuscular;
    @Basic(optional = false)
    @Column(name = "IMC")
    private float imc;

    public HistorialAfinamiento() {
    }

    public HistorialAfinamiento(Integer id) {
        this.id = id;
    }

    public HistorialAfinamiento(Integer id, String tipoIdentificacion, String identificacion, Date fecha, double peso, String brazo, String posicion, String jornada, String estadoInicial, int presDiastolica, int presSistolica, String detalles, float grasaCorporal, float porcentajeAgua, float masaMuscular, float imc) {
        this.id = id;
        this.tipoIdentificacion = tipoIdentificacion;
        this.identificacion = identificacion;
        this.fecha = fecha;
        this.peso = peso;
        this.brazo = brazo;
        this.posicion = posicion;
        this.jornada = jornada;
        this.estadoInicial = estadoInicial;
        this.presDiastolica = presDiastolica;
        this.presSistolica = presSistolica;
        this.detalles = detalles;
        this.grasaCorporal = grasaCorporal;
        this.porcentajeAgua = porcentajeAgua;
        this.masaMuscular = masaMuscular;
        this.imc = imc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
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

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
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
        return estadoInicial;
    }

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public int getPresDiastolica() {
        return presDiastolica;
    }

    public void setPresDiastolica(int presDiastolica) {
        this.presDiastolica = presDiastolica;
    }

    public int getPresSistolica() {
        return presSistolica;
    }

    public void setPresSistolica(int presSistolica) {
        this.presSistolica = presSistolica;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public float getGrasaCorporal() {
        return grasaCorporal;
    }

    public void setGrasaCorporal(float grasaCorporal) {
        this.grasaCorporal = grasaCorporal;
    }

    public float getPorcentajeAgua() {
        return porcentajeAgua;
    }

    public void setPorcentajeAgua(float porcentajeAgua) {
        this.porcentajeAgua = porcentajeAgua;
    }

    public float getMasaMuscular() {
        return masaMuscular;
    }

    public void setMasaMuscular(float masaMuscular) {
        this.masaMuscular = masaMuscular;
    }

    public float getImc() {
        return imc;
    }

    public void setImc(float imc) {
        this.imc = imc;
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
        if (!(object instanceof HistorialAfinamiento)) {
            return false;
        }
        HistorialAfinamiento other = (HistorialAfinamiento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.HistorialAfinamiento[ id=" + id + " ]";
    }
    
}
