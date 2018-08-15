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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author migma
 */
@Entity
@Table(name = "examenfisico")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Examenfisico.findAll", query = "SELECT e FROM Examenfisico e")
    , @NamedQuery(name = "Examenfisico.findByTipoid", query = "SELECT e FROM Examenfisico e WHERE e.examenfisicoPK.tipoid = :tipoid")
    , @NamedQuery(name = "Examenfisico.findByIdentificacion", query = "SELECT e FROM Examenfisico e WHERE e.examenfisicoPK.identificacion = :identificacion")
    , @NamedQuery(name = "Examenfisico.findByFechatoma", query = "SELECT e FROM Examenfisico e WHERE e.fechatoma = :fechatoma")
    , @NamedQuery(name = "Examenfisico.findByTallacm", query = "SELECT e FROM Examenfisico e WHERE e.tallacm = :tallacm")
    , @NamedQuery(name = "Examenfisico.findByPesogramo", query = "SELECT e FROM Examenfisico e WHERE e.pesogramo = :pesogramo")
    , @NamedQuery(name = "Examenfisico.findByImc", query = "SELECT e FROM Examenfisico e WHERE e.imc = :imc")
    , @NamedQuery(name = "Examenfisico.findByPorcentajegrasa", query = "SELECT e FROM Examenfisico e WHERE e.porcentajegrasa = :porcentajegrasa")
    , @NamedQuery(name = "Examenfisico.findByPorcentajeabdominal", query = "SELECT e FROM Examenfisico e WHERE e.porcentajeabdominal = :porcentajeabdominal")
    , @NamedQuery(name = "Examenfisico.findByTensionarterial", query = "SELECT e FROM Examenfisico e WHERE e.tensionarterial = :tensionarterial")
    , @NamedQuery(name = "Examenfisico.findByProcedimiento", query = "SELECT e FROM Examenfisico e WHERE e.procedimiento = :procedimiento")
    , @NamedQuery(name = "Examenfisico.findByDiagnostico", query = "SELECT e FROM Examenfisico e WHERE e.diagnostico = :diagnostico")
    , @NamedQuery(name = "Examenfisico.findByPrestador", query = "SELECT e FROM Examenfisico e WHERE e.prestador = :prestador")
    , @NamedQuery(name = "Examenfisico.findByFModificacion", query = "SELECT e FROM Examenfisico e WHERE e.fModificacion = :fModificacion")
    , @NamedQuery(name = "Examenfisico.findByFCreacion", query = "SELECT e FROM Examenfisico e WHERE e.fCreacion = :fCreacion")
    , @NamedQuery(name = "Examenfisico.findByBrazo", query = "SELECT e FROM Examenfisico e WHERE e.brazo = :brazo")
    , @NamedQuery(name = "Examenfisico.findByPosicion", query = "SELECT e FROM Examenfisico e WHERE e.posicion = :posicion")
    , @NamedQuery(name = "Examenfisico.findByDetalles", query = "SELECT e FROM Examenfisico e WHERE e.detalles = :detalles")})
public class Examenfisico implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExamenfisicoPK examenfisicoPK;
    @Basic(optional = false)
    @Column(name = "fechatoma")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechatoma;
    @Basic(optional = false)
    @Column(name = "tallacm")
    private int tallacm;
    @Basic(optional = false)
    @Column(name = "pesogramo")
    private int pesogramo;
    @Basic(optional = false)
    @Column(name = "imc")
    private double imc;
    @Basic(optional = false)
    @Column(name = "porcentajegrasa")
    private double porcentajegrasa;
    @Basic(optional = false)
    @Column(name = "porcentajeabdominal")
    private double porcentajeabdominal;
    @Basic(optional = false)
    @Column(name = "tensionarterial")
    private double tensionarterial;
    @Column(name = "procedimiento")
    private String procedimiento;
    @Column(name = "diagnostico")
    private String diagnostico;
    @Column(name = "prestador")
    private String prestador;
    @Column(name = "f_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fModificacion;
    @Column(name = "f_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fCreacion;
    @Column(name = "brazo")
    private Integer brazo;
    @Column(name = "posicion")
    private Integer posicion;
    @Column(name = "detalles")
    private String detalles;

    public Examenfisico() {
    }

    public Examenfisico(ExamenfisicoPK examenfisicoPK) {
        this.examenfisicoPK = examenfisicoPK;
    }

    public Examenfisico(ExamenfisicoPK examenfisicoPK, Date fechatoma, int tallacm, int pesogramo, double imc, double porcentajegrasa, double porcentajeabdominal, double tensionarterial) {
        this.examenfisicoPK = examenfisicoPK;
        this.fechatoma = fechatoma;
        this.tallacm = tallacm;
        this.pesogramo = pesogramo;
        this.imc = imc;
        this.porcentajegrasa = porcentajegrasa;
        this.porcentajeabdominal = porcentajeabdominal;
        this.tensionarterial = tensionarterial;
    }

    public Examenfisico(String tipoid, String identificacion) {
        this.examenfisicoPK = new ExamenfisicoPK(tipoid, identificacion);
    }

    public ExamenfisicoPK getExamenfisicoPK() {
        return examenfisicoPK;
    }

    public void setExamenfisicoPK(ExamenfisicoPK examenfisicoPK) {
        this.examenfisicoPK = examenfisicoPK;
    }

    public Date getFechatoma() {
        return fechatoma;
    }

    public void setFechatoma(Date fechatoma) {
        this.fechatoma = fechatoma;
    }

    public int getTallacm() {
        return tallacm;
    }

    public void setTallacm(int tallacm) {
        this.tallacm = tallacm;
    }

    public int getPesogramo() {
        return pesogramo;
    }

    public void setPesogramo(int pesogramo) {
        this.pesogramo = pesogramo;
    }

    public double getImc() {
        return imc;
    }

    public void setImc(double imc) {
        this.imc = imc;
    }

    public double getPorcentajegrasa() {
        return porcentajegrasa;
    }

    public void setPorcentajegrasa(double porcentajegrasa) {
        this.porcentajegrasa = porcentajegrasa;
    }

    public double getPorcentajeabdominal() {
        return porcentajeabdominal;
    }

    public void setPorcentajeabdominal(double porcentajeabdominal) {
        this.porcentajeabdominal = porcentajeabdominal;
    }

    public double getTensionarterial() {
        return tensionarterial;
    }

    public void setTensionarterial(double tensionarterial) {
        this.tensionarterial = tensionarterial;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getPrestador() {
        return prestador;
    }

    public void setPrestador(String prestador) {
        this.prestador = prestador;
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

    public Integer getBrazo() {
        return brazo;
    }

    public void setBrazo(Integer brazo) {
        this.brazo = brazo;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (examenfisicoPK != null ? examenfisicoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Examenfisico)) {
            return false;
        }
        Examenfisico other = (Examenfisico) object;
        if ((this.examenfisicoPK == null && other.examenfisicoPK != null) || (this.examenfisicoPK != null && !this.examenfisicoPK.equals(other.examenfisicoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.Examenfisico[ examenfisicoPK=" + examenfisicoPK + " ]";
    }
    
}
