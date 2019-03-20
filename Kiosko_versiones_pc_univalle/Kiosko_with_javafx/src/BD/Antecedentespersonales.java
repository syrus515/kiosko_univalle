/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BD;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Miguel Askar
 */
@Entity
@Table(name = "antecedentespersonales")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Antecedentespersonales.findAll", query = "SELECT a FROM Antecedentespersonales a")
    , @NamedQuery(name = "Antecedentespersonales.findByTipoid", query = "SELECT a FROM Antecedentespersonales a WHERE a.antecedentespersonalesPK.tipoid = :tipoid")
    , @NamedQuery(name = "Antecedentespersonales.findByIdentificacion", query = "SELECT a FROM Antecedentespersonales a WHERE a.antecedentespersonalesPK.identificacion = :identificacion")
    , @NamedQuery(name = "Antecedentespersonales.findByDiabetes", query = "SELECT a FROM Antecedentespersonales a WHERE a.diabetes = :diabetes")
    , @NamedQuery(name = "Antecedentespersonales.findByHipertension", query = "SELECT a FROM Antecedentespersonales a WHERE a.hipertension = :hipertension")
    , @NamedQuery(name = "Antecedentespersonales.findByInfartos", query = "SELECT a FROM Antecedentespersonales a WHERE a.infartos = :infartos")
    , @NamedQuery(name = "Antecedentespersonales.findByMedicamentospermanentes1", query = "SELECT a FROM Antecedentespersonales a WHERE a.medicamentospermanentes1 = :medicamentospermanentes1")
    , @NamedQuery(name = "Antecedentespersonales.findByMedicamentospermanentes2", query = "SELECT a FROM Antecedentespersonales a WHERE a.medicamentospermanentes2 = :medicamentospermanentes2")
    , @NamedQuery(name = "Antecedentespersonales.findByMedicamentospermanentes3", query = "SELECT a FROM Antecedentespersonales a WHERE a.medicamentospermanentes3 = :medicamentospermanentes3")
    , @NamedQuery(name = "Antecedentespersonales.findByMedicamentospermanentes4", query = "SELECT a FROM Antecedentespersonales a WHERE a.medicamentospermanentes4 = :medicamentospermanentes4")
    , @NamedQuery(name = "Antecedentespersonales.findByMedicamentospermanentes5", query = "SELECT a FROM Antecedentespersonales a WHERE a.medicamentospermanentes5 = :medicamentospermanentes5")
    , @NamedQuery(name = "Antecedentespersonales.findByActividadfisica", query = "SELECT a FROM Antecedentespersonales a WHERE a.actividadfisica = :actividadfisica")
    , @NamedQuery(name = "Antecedentespersonales.findByCosumelicor", query = "SELECT a FROM Antecedentespersonales a WHERE a.cosumelicor = :cosumelicor")
    , @NamedQuery(name = "Antecedentespersonales.findByOtrassustancias1", query = "SELECT a FROM Antecedentespersonales a WHERE a.otrassustancias1 = :otrassustancias1")
    , @NamedQuery(name = "Antecedentespersonales.findByOtrassustancias2", query = "SELECT a FROM Antecedentespersonales a WHERE a.otrassustancias2 = :otrassustancias2")
    , @NamedQuery(name = "Antecedentespersonales.findByOtrassustancias3", query = "SELECT a FROM Antecedentespersonales a WHERE a.otrassustancias3 = :otrassustancias3")
    , @NamedQuery(name = "Antecedentespersonales.findByOtrassustancias4", query = "SELECT a FROM Antecedentespersonales a WHERE a.otrassustancias4 = :otrassustancias4")
    , @NamedQuery(name = "Antecedentespersonales.findByOtrassustancias5", query = "SELECT a FROM Antecedentespersonales a WHERE a.otrassustancias5 = :otrassustancias5")
    , @NamedQuery(name = "Antecedentespersonales.findByFumadias", query = "SELECT a FROM Antecedentespersonales a WHERE a.fumadias = :fumadias")
    , @NamedQuery(name = "Antecedentespersonales.findByConviveconfumadores", query = "SELECT a FROM Antecedentespersonales a WHERE a.conviveconfumadores = :conviveconfumadores")})
public class Antecedentespersonales implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AntecedentespersonalesPK antecedentespersonalesPK;
    @Column(name = "diabetes")
    private String diabetes;
    @Column(name = "hipertension")
    private String hipertension;
    @Column(name = "infartos")
    private String infartos;
    @Basic(optional = false)
    @Column(name = "medicamentospermanentes1")
    private String medicamentospermanentes1;
    @Basic(optional = false)
    @Column(name = "medicamentospermanentes2")
    private String medicamentospermanentes2;
    @Basic(optional = false)
    @Column(name = "medicamentospermanentes3")
    private String medicamentospermanentes3;
    @Basic(optional = false)
    @Column(name = "medicamentospermanentes4")
    private String medicamentospermanentes4;
    @Basic(optional = false)
    @Column(name = "medicamentospermanentes5")
    private String medicamentospermanentes5;
    @Column(name = "actividadfisica")
    private String actividadfisica;
    @Column(name = "cosumelicor")
    private String cosumelicor;
    @Column(name = "otrassustancias1")
    private String otrassustancias1;
    @Column(name = "otrassustancias2")
    private String otrassustancias2;
    @Column(name = "otrassustancias3")
    private String otrassustancias3;
    @Column(name = "otrassustancias4")
    private String otrassustancias4;
    @Column(name = "otrassustancias5")
    private String otrassustancias5;
    @Column(name = "fumadias")
    private String fumadias;
    @Column(name = "conviveconfumadores")
    private String conviveconfumadores;

    public Antecedentespersonales() {
    }

    public Antecedentespersonales(AntecedentespersonalesPK antecedentespersonalesPK) {
        this.antecedentespersonalesPK = antecedentespersonalesPK;
    }

    public Antecedentespersonales(AntecedentespersonalesPK antecedentespersonalesPK, String medicamentospermanentes1, String medicamentospermanentes2, String medicamentospermanentes3, String medicamentospermanentes4, String medicamentospermanentes5) {
        this.antecedentespersonalesPK = antecedentespersonalesPK;
        this.medicamentospermanentes1 = medicamentospermanentes1;
        this.medicamentospermanentes2 = medicamentospermanentes2;
        this.medicamentospermanentes3 = medicamentospermanentes3;
        this.medicamentospermanentes4 = medicamentospermanentes4;
        this.medicamentospermanentes5 = medicamentospermanentes5;
    }

    public Antecedentespersonales(String tipoid, String identificacion) {
        this.antecedentespersonalesPK = new AntecedentespersonalesPK(tipoid, identificacion);
    }

    public AntecedentespersonalesPK getAntecedentespersonalesPK() {
        return antecedentespersonalesPK;
    }

    public void setAntecedentespersonalesPK(AntecedentespersonalesPK antecedentespersonalesPK) {
        this.antecedentespersonalesPK = antecedentespersonalesPK;
    }

    public String getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(String diabetes) {
        this.diabetes = diabetes;
    }

    public String getHipertension() {
        return hipertension;
    }

    public void setHipertension(String hipertension) {
        this.hipertension = hipertension;
    }

    public String getInfartos() {
        return infartos;
    }

    public void setInfartos(String infartos) {
        this.infartos = infartos;
    }

    public String getMedicamentospermanentes1() {
        return medicamentospermanentes1;
    }

    public void setMedicamentospermanentes1(String medicamentospermanentes1) {
        this.medicamentospermanentes1 = medicamentospermanentes1;
    }

    public String getMedicamentospermanentes2() {
        return medicamentospermanentes2;
    }

    public void setMedicamentospermanentes2(String medicamentospermanentes2) {
        this.medicamentospermanentes2 = medicamentospermanentes2;
    }

    public String getMedicamentospermanentes3() {
        return medicamentospermanentes3;
    }

    public void setMedicamentospermanentes3(String medicamentospermanentes3) {
        this.medicamentospermanentes3 = medicamentospermanentes3;
    }

    public String getMedicamentospermanentes4() {
        return medicamentospermanentes4;
    }

    public void setMedicamentospermanentes4(String medicamentospermanentes4) {
        this.medicamentospermanentes4 = medicamentospermanentes4;
    }

    public String getMedicamentospermanentes5() {
        return medicamentospermanentes5;
    }

    public void setMedicamentospermanentes5(String medicamentospermanentes5) {
        this.medicamentospermanentes5 = medicamentospermanentes5;
    }

    public String getActividadfisica() {
        return actividadfisica;
    }

    public void setActividadfisica(String actividadfisica) {
        this.actividadfisica = actividadfisica;
    }

    public String getCosumelicor() {
        return cosumelicor;
    }

    public void setCosumelicor(String cosumelicor) {
        this.cosumelicor = cosumelicor;
    }

    public String getOtrassustancias1() {
        return otrassustancias1;
    }

    public void setOtrassustancias1(String otrassustancias1) {
        this.otrassustancias1 = otrassustancias1;
    }

    public String getOtrassustancias2() {
        return otrassustancias2;
    }

    public void setOtrassustancias2(String otrassustancias2) {
        this.otrassustancias2 = otrassustancias2;
    }

    public String getOtrassustancias3() {
        return otrassustancias3;
    }

    public void setOtrassustancias3(String otrassustancias3) {
        this.otrassustancias3 = otrassustancias3;
    }

    public String getOtrassustancias4() {
        return otrassustancias4;
    }

    public void setOtrassustancias4(String otrassustancias4) {
        this.otrassustancias4 = otrassustancias4;
    }

    public String getOtrassustancias5() {
        return otrassustancias5;
    }

    public void setOtrassustancias5(String otrassustancias5) {
        this.otrassustancias5 = otrassustancias5;
    }

    public String getFumadias() {
        return fumadias;
    }

    public void setFumadias(String fumadias) {
        this.fumadias = fumadias;
    }

    public String getConviveconfumadores() {
        return conviveconfumadores;
    }

    public void setConviveconfumadores(String conviveconfumadores) {
        this.conviveconfumadores = conviveconfumadores;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (antecedentespersonalesPK != null ? antecedentespersonalesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Antecedentespersonales)) {
            return false;
        }
        Antecedentespersonales other = (Antecedentespersonales) object;
        if ((this.antecedentespersonalesPK == null && other.antecedentespersonalesPK != null) || (this.antecedentespersonalesPK != null && !this.antecedentespersonalesPK.equals(other.antecedentespersonalesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BD.Antecedentespersonales[ antecedentespersonalesPK=" + antecedentespersonalesPK + " ]";
    }
    
}
