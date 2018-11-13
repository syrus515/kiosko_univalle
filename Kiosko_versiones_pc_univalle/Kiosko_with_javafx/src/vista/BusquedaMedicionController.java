/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import BD.Medicion;
import BD.MedicionPersonalizada;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author Miguel Askar
 */
public class BusquedaMedicionController implements Initializable {

    @FXML
    private DatePicker desdeReproduccion;
    @FXML
    private DatePicker hastaReproduccion;
    @FXML
    private TableView<MedicionPersonalizada> tablaReproduccion;
    @FXML
    private Button reproducir;
        
    @FXML
    private TableColumn<MedicionPersonalizada, LocalDate> columnaFecha;
    @FXML
    private TableColumn<MedicionPersonalizada, String> columnaDetalles;
    @FXML
    private TableColumn<MedicionPersonalizada, Integer> columnaIntervalo;
    @FXML
    private TableColumn<MedicionPersonalizada, Integer> ColumnaMuestra;
    @FXML
    private TableColumn<MedicionPersonalizada, Integer> ColumnaExamen;
    
    private String usuario;
    
    private Kiosko programaPrincipal;
    
    private Stage thisStage;
    @FXML
    private TableColumn<Medicion, LocalDate> columnaFechaSimple;
    @FXML
    private TableColumn<Medicion, String> columnaDetallesSimple;
    @FXML
    private Button cargarSimple;
    @FXML
    private TableView<Medicion> tablaMedicionSimple;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnaDetalles.setCellValueFactory(new PropertyValueFactory<>("Detalles"));
        columnaIntervalo.setCellValueFactory(new PropertyValueFactory<>("intervalo"));
        ColumnaMuestra.setCellValueFactory(new PropertyValueFactory<>("DuracionMuestra"));
        ColumnaExamen.setCellValueFactory(new PropertyValueFactory<>("DuracionExamen"));        
        
        columnaDetallesSimple.setCellValueFactory(new PropertyValueFactory<>("detalles"));
        columnaFechaSimple.setCellValueFactory(new PropertyValueFactory<>("fecha"));
    }  
    
    public void setProgramaPrincipal(Kiosko programaPrincipal) 
    {
        this.programaPrincipal = programaPrincipal;        
    }
    
    public void llenarTabla()
    {
        //Código para llenar las mediciones simples
       usuario= programaPrincipal.getUsuarioABuscar();
       
       EntityManager em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();        
       Query queryMedicionFindAll = em.createNativeQuery("SELECT * from medicion m WHERE identificacion= '" + usuario +"'", Medicion.class);
       List<Medicion> listMedicion = queryMedicionFindAll.getResultList();
       tablaMedicionSimple.setItems(FXCollections.observableArrayList(listMedicion));
       
       
       //Código para llenar las mediciones personalizadas       
       String consulta= "SELECT DISTINCT medicion_personalizada.id , medicion_personalizada.intervalo, medicion_personalizada.duracionMuestra, medicion_personalizada.duracionExamen,"
               + "medicion_personalizada.fecha, medicion_personalizada.detalles " +
        "FROM medicion_personalizada"+
        " INNER JOIN medicion ON medicion.idPersonalizada=medicion_personalizada.id AND medicion_personalizada.id!=1" ;
       queryMedicionFindAll = em.createNativeQuery(consulta, MedicionPersonalizada.class);
       //Query queryMedicionFindAll = em.createNativeQuery("SELECT * from medicion_personalizada m WHERE identificacion= '" + usuario +"'", MedicionPersonalizada.class);
       List<MedicionPersonalizada> listMedicionPersonalizada = queryMedicionFindAll.getResultList();
       tablaReproduccion.setItems(FXCollections.observableArrayList(listMedicionPersonalizada)); 
    }
    
    @FXML
    public void cerrarYReproducir()
    {
        int id= tablaMedicionSimple.getSelectionModel().getSelectedItem().getId();       
        EntityManager em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();        
        Query queryReproducir= em.createNamedQuery("Medicion.findById", Medicion.class);
        queryReproducir.setParameter("id", id);
        Medicion medicion= (Medicion) queryReproducir.getSingleResult();        
        programaPrincipal.setMedicionReproducir(medicion);
        
        System.out.println("Sí funcionó!!!!" + id);
        this.thisStage.close();
    }
    
    @FXML
    public void presentarGraficos()
    {
        int idPersonalizado= tablaReproduccion.getSelectionModel().getSelectedItem().getId();
        this.programaPrincipal.mostrarVentanaCharts(idPersonalizado);
    }
    
    public void setStage(Stage stage)
    {
        this.thisStage= stage;
    }
    
}
