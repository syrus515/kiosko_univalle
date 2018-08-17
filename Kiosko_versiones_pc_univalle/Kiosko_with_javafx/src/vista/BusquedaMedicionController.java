/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import BD.Medicion;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * FXML Controller class
 *
 * @author migma
 */
public class BusquedaMedicionController implements Initializable {

    @FXML
    private DatePicker desdeReproduccion;
    @FXML
    private DatePicker hastaReproduccion;
    @FXML
    private TableView<?> tablaReproduccion;
    @FXML
    private Button reproducir;
    
    private Kiosko programaPrincipal;
    
    private String usuario;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
       
    }   
    
    
    public void setProgramaPrincipal(Kiosko programaPrincipal) 
    {
        this.programaPrincipal = programaPrincipal;        
    }
    
    public void llenarTabla()
    {
       usuario= programaPrincipal.getUsuarioABuscar();
       
       EntityManager em = Persistence.createEntityManagerFactory("KioskoPU").createEntityManager();
       em.getTransaction().begin();
       //em.setProperty("id", usuario);
       List<String> list = em.createQuery("SELECT Detalles d, identificacion i from Medicion m WHERE identificacion= '" + usuario +"'", String.class).getResultList();
       //List<Medicion> list = em.createNamedQuery("Medicion.findAll", Medicion.class).getResultList();
       ObservableList listaAgregar=  FXCollections.observableArrayList();;
       
      for(int i=0; i<list.size(); i++)
      {
          listaAgregar.add(list.get(i));
      }
      
      tablaReproduccion.setItems(listaAgregar);      
       
       
    }
    
}
