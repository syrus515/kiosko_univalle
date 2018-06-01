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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
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
@Table(catalog = "kiosko", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pacientes.findAll", query = "SELECT p FROM Pacientes p"),
    @NamedQuery(name = "Pacientes.findByTipoid", query = "SELECT p FROM Pacientes p WHERE p.pacientesPK.tipoid = :tipoid"),
    @NamedQuery(name = "Pacientes.findByIdentificacion", query = "SELECT p FROM Pacientes p WHERE p.pacientesPK.identificacion = :identificacion"),
    @NamedQuery(name = "Pacientes.findByNombre1", query = "SELECT p FROM Pacientes p WHERE p.nombre1 = :nombre1"),
    @NamedQuery(name = "Pacientes.findByNombre2", query = "SELECT p FROM Pacientes p WHERE p.nombre2 = :nombre2"),
    @NamedQuery(name = "Pacientes.findByApellido1", query = "SELECT p FROM Pacientes p WHERE p.apellido1 = :apellido1"),
    @NamedQuery(name = "Pacientes.findByApellido2", query = "SELECT p FROM Pacientes p WHERE p.apellido2 = :apellido2"),
    @NamedQuery(name = "Pacientes.findByDireccion", query = "SELECT p FROM Pacientes p WHERE p.direccion = :direccion"),
    @NamedQuery(name = "Pacientes.findByTelFijo", query = "SELECT p FROM Pacientes p WHERE p.telFijo = :telFijo"),
    @NamedQuery(name = "Pacientes.findByCelular", query = "SELECT p FROM Pacientes p WHERE p.celular = :celular"),
    @NamedQuery(name = "Pacientes.findByAdministradora", query = "SELECT p FROM Pacientes p WHERE p.administradora = :administradora"),
    @NamedQuery(name = "Pacientes.findByTipousuario", query = "SELECT p FROM Pacientes p WHERE p.tipousuario = :tipousuario"),
    @NamedQuery(name = "Pacientes.findByGenero", query = "SELECT p FROM Pacientes p WHERE p.genero = :genero"),
    @NamedQuery(name = "Pacientes.findByFNacimiento", query = "SELECT p FROM Pacientes p WHERE p.fNacimiento = :fNacimiento"),
    @NamedQuery(name = "Pacientes.findByDepartamento", query = "SELECT p FROM Pacientes p WHERE p.departamento = :departamento"),
    @NamedQuery(name = "Pacientes.findByMunicipio", query = "SELECT p FROM Pacientes p WHERE p.municipio = :municipio"),
    @NamedQuery(name = "Pacientes.findByZona", query = "SELECT p FROM Pacientes p WHERE p.zona = :zona"),
    @NamedQuery(name = "Pacientes.findByEstado", query = "SELECT p FROM Pacientes p WHERE p.estado = :estado"),
    @NamedQuery(name = "Pacientes.findByFModificacion", query = "SELECT p FROM Pacientes p WHERE p.fModificacion = :fModificacion"),
    @NamedQuery(name = "Pacientes.findByFCreacion", query = "SELECT p FROM Pacientes p WHERE p.fCreacion = :fCreacion")})
public class Pacientes implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PacientesPK pacientesPK;
    @Column(length = 30)
    private String nombre1;
    @Column(length = 30)
    private String nombre2;
    @Column(length = 30)
    private String apellido1;
    @Column(length = 30)
    private String apellido2;
    @Column(length = 100)
    private String direccion;
    @Column(name = "tel_fijo", length = 20)
    private String telFijo;
    @Column(length = 20)
    private String celular;
    @Basic(optional = false)
    @Column(nullable = false, length = 6)
    private String administradora;
    private Integer tipousuario;
    @Column(length = 1)
    private String genero;
    @Column(name = "f_nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fNacimiento;
    @Basic(optional = false)
    @Column(nullable = false, length = 2)
    private String departamento;
    @Basic(optional = false)
    @Column(nullable = false, length = 5)
    private String municipio;
    @Column(length = 1)
    private String zona;
    @Lob
    private byte[] foto;
    @Lob
    private byte[] huella;
    @Lob
    private byte[] huella2;
    @Column(length = 1)
    private String estado;
    @Column(name = "f_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fModificacion;
    @Column(name = "f_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fCreacion;

    public Pacientes() {
    }

    public Pacientes(PacientesPK pacientesPK) {
        this.pacientesPK = pacientesPK;
    }

    public Pacientes(PacientesPK pacientesPK, String administradora, String departamento, String municipio) {
        this.pacientesPK = pacientesPK;
        this.administradora = administradora;
        this.departamento = departamento;
        this.municipio = municipio;
    }

    public Pacientes(String tipoid, String identificacion) {
        this.pacientesPK = new PacientesPK(tipoid, identificacion);
    }

    public PacientesPK getPacientesPK() {
        return pacientesPK;
    }

    public void setPacientesPK(PacientesPK pacientesPK) {
        this.pacientesPK = pacientesPK;
    }

    public String getNombre1() {
        return nombre1;
    }

    public void setNombre1(String nombre1) {
        this.nombre1 = nombre1;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelFijo() {
        return telFijo;
    }

    public void setTelFijo(String telFijo) {
        this.telFijo = telFijo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getAdministradora() {
        return administradora;
    }

    public void setAdministradora(String administradora) {
        this.administradora = administradora;
    }

    public Integer getTipousuario() {
        return tipousuario;
    }

    public void setTipousuario(Integer tipousuario) {
        this.tipousuario = tipousuario;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Date getFNacimiento() {
        return fNacimiento;
    }

    public void setFNacimiento(Date fNacimiento) {
        this.fNacimiento = fNacimiento;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public byte[] getHuella() {
        return huella;
    }

    public void setHuella(byte[] huella) {
        this.huella = huella;
    }

    public byte[] getHuella2() {
        return huella2;
    }

    public void setHuella2(byte[] huella2) {
        this.huella2 = huella2;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFModificacion() {
        return fModificacion;
    }

    public void setFModificacion(Date fModificacion) {
        this.fModificacion = fModificacion;
    }

    public Date getFCreacion() {
        return fCreacion;
    }

    public void setFCreacion(Date fCreacion) {
        this.fCreacion = fCreacion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pacientesPK != null ? pacientesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pacientes)) {
            return false;
        }
        Pacientes other = (Pacientes) object;
        if ((this.pacientesPK == null && other.pacientesPK != null) || (this.pacientesPK != null && !this.pacientesPK.equals(other.pacientesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.Pacientes[ pacientesPK=" + pacientesPK + " ]";
    }
    
}
