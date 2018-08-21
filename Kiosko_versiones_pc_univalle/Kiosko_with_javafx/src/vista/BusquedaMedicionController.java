/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import BD.Medicion;
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
    private TableView<Medicion> tablaReproduccion;
    @FXML
    private Button reproducir;
        
    @FXML
    private TableColumn<Medicion, LocalDate> columnaFecha;
    @FXML
    private TableColumn<Medicion, String> columnaDetalles;
    @FXML
    private TableColumn<Medicion, Integer> columnaIntervalo;
    @FXML
    private TableColumn<Medicion, Integer> ColumnaMuestra;
    @FXML
    private TableColumn<Medicion, Integer> ColumnaExamen;
    
    private String usuario;
    
    private Kiosko programaPrincipal;
    
    private Stage thisStage;

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
    }  
    
    public void setProgramaPrincipal(Kiosko programaPrincipal) 
    {
        this.programaPrincipal = programaPrincipal;        
    }
    
    public void llenarTabla()
    {
       usuario= programaPrincipal.getUsuarioABuscar();
       
       EntityManager em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
       Query queryMedicionFindAll = em.createNativeQuery("SELECT * from medicion m WHERE identificacion= '" + usuario +"'", Medicion.class);
       List<Medicion> listMedicion = queryMedicionFindAll.getResultList();
       tablaReproduccion.setItems(FXCollections.observableArrayList(listMedicion));        
    }
    
    public void cerrarYReproducir()
    {
        int id= tablaReproduccion.getSelectionModel().getSelectedItem().getId();       
        EntityManager em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();        
        Query queryReproducir= em.createNamedQuery("Medicion.findById", Medicion.class);
        queryReproducir.setParameter("id", id);
        Medicion medicion= (Medicion) queryReproducir.getSingleResult();        
        programaPrincipal.setMedicionReproducir(medicion);
        
        this.thisStage.close();
    }
    
    public void setStage(Stage stage)
    {
        this.thisStage= stage;
    }
    
}
