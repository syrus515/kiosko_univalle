/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        System.out.println("HOLA");
    }    
    
}
