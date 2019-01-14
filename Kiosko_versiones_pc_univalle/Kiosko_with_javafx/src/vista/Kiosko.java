package vista;
import BD.Medicion;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JFrame;
import org.controlsfx.dialog.Dialogs;
public class Kiosko extends Application {
    
    private Stage ventanaPrincipal; //Stage principal para poder derivar ventanas    
    private Stage primaryStage;
    private BorderPane rootLayout;
    Stage ventanaWebCam = null;
    Stage ventanaHuella = null;
    CapturaHuella capturaHuella = null;
    WebCam webCam = null;
    String usuario=null, nombre=null, rol=null;    
    private String usuarioABuscar;
    private Medicion medicionReproducir;
    

    public void setMedicionReproducir(Medicion medicion)
    {
        this.medicionReproducir= medicion;    
    }
    
    public Medicion getMedicionReproducir()
    {
        return this.medicionReproducir;
    }
    
    public void setUsuarioABuscar(String usuario)
    {
        this.usuarioABuscar= usuario;    
    }
    
    public String getUsuarioABuscar()
    {
        return this.usuarioABuscar;
    }
   
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Autenticación de usuario");
        initRootLayout();
    }
    
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Kiosko.class.getResource("/vista/Autenticar.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            //scene.getStylesheets().add("/vista/DarkTheme.css");
            
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            
            AutenticarController controller = loader.getController();
            controller.setProgramaPrincipal(this);
            primaryStage.show();
            
        } catch (IOException ex) {
            System.out.println("Error en el sistema: " + ex.toString());
        }
    }
    
    public void mostrarMenu(String usuario, String nombre, String rol) {
        try {
            this.usuario = usuario;
            this.nombre = nombre;
            this.rol = rol;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Kiosko.class.getResource("/vista/Menu.fxml"));
            AnchorPane menu = (AnchorPane) loader.load();
            ventanaPrincipal = new Stage();
            ventanaPrincipal.initModality(Modality.APPLICATION_MODAL);
            ventanaPrincipal.setTitle("Menú principal");
            
            Scene scene = new Scene(menu);
            scene.getStylesheets().add("/vista/menu.css");
            ventanaPrincipal.setScene(scene);
            MenuController controller = loader.getController();           
            
            controller.setProgramaPrincipal(this, usuario, nombre, rol);
            controller.iniciarAdmin();
            controller.restringirBotones();
            
            ventanaPrincipal.show();
            ventanaPrincipal.setResizable(false);
            Rectangle2D ventanaPrimariaLimites = Screen.getPrimary().getVisualBounds();
            ventanaPrincipal.setX(ventanaPrimariaLimites.getMinX());
            ventanaPrincipal.setY(ventanaPrimariaLimites.getMinY());
            ventanaPrincipal.setWidth(ventanaPrimariaLimites.getWidth());
            ventanaPrincipal.setHeight(ventanaPrimariaLimites.getHeight());
            primaryStage.close();
            primaryStage = ventanaPrincipal;
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override public void handle(WindowEvent event) {
                //event.consume(); //Consumar el evento
                controller.cerrarPrograma();
                System.exit(0);
            }  
        });
        } catch (Exception ex) {
            System.out.println("Error del sistema: " + ex.toString());
        }
    }
    
     
    public void mostrarVentanaReproduccion() //Método para mostrar la ventana donde se buscan las mediciones almacenadas del paciente
    {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Kiosko.class.getResource("/vista/BusquedaMedicion.fxml"));
            VBox menu = (VBox) loader.load();
            Stage ventana = new Stage();
            ventana.initOwner(ventanaPrincipal);
            ventana.setTitle("Busqueda mediciones");
            
            
            Scene scene = new Scene(menu);
            scene.getStylesheets().add("/vista/menu.css");
            ventana.setScene(scene);
            BusquedaMedicionController controller = loader.getController();
            
            controller.setProgramaPrincipal(this);
            controller.setStage(ventana);
            controller.llenarTabla();
            ventana.show();
            ventana.setResizable(false);
            Rectangle2D ventanaPrimariaLimites = new Rectangle2D(640, 300, 640, 453);
            ventana.setX(ventanaPrimariaLimites.getMinX());
            ventana.setY(ventanaPrimariaLimites.getMinY());
            ventana.setWidth(ventanaPrimariaLimites.getWidth());
            ventana.setHeight(ventanaPrimariaLimites.getHeight());
            //primaryStage.close();
            
            //controller.setProgramaPrincipal(this);
            ventana.setOnCloseRequest(new EventHandler<WindowEvent>(){            
            @Override public void handle(WindowEvent event) {
                //event.consume(); //Consumar el evento
                ventana.close();
                
            }  
        });
        } catch (Exception ex) {
            System.out.println("Error del sistema: " + ex.toString());
        }      

    }
    
    public void mostrarVentanaCharts(int idPersonalizado) //Método para mostrar la ventana donde van las gráficas de las mediciones personalizadas.
    {
        try {            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Kiosko.class.getResource("/vista/graficasEstadisticas.fxml"));
            VBox menu = (VBox) loader.load();
            Stage ventana = new Stage();
            ventana.initOwner(ventanaPrincipal);
            ventana.setTitle("Resultados medición personalizada");
            
            
            Scene scene = new Scene(menu);
            scene.getStylesheets().add("/vista/menu.css");
            ventana.setScene(scene);
            GraficasEstadisticasController controller = loader.getController();
            
            controller.setProgramaPrincipal(this);
            controller.setStage(ventana);
                
            controller.setIdPersonalizado(idPersonalizado);
            controller.obtenerMediciones();
            controller.dibujarCharts();
            ventana.show();
            ventana.setResizable(false);
            Rectangle2D ventanaPrimariaLimites = new Rectangle2D(10, 10, 1293, 1000);
            ventana.setX(ventanaPrimariaLimites.getMinX());
            ventana.setY(ventanaPrimariaLimites.getMinY());
            ventana.setWidth(ventanaPrimariaLimites.getWidth());
            ventana.setHeight(ventanaPrimariaLimites.getHeight());
            //primaryStage.close();
            
            //controller.setProgramaPrincipal(this);
            ventana.setOnCloseRequest(new EventHandler<WindowEvent>(){            
            @Override public void handle(WindowEvent event) {
                //event.consume(); //Consumar el evento
                ventana.close();
                
            }  
        });
        } catch (Exception ex) {       
            ex.printStackTrace();
            System.out.println("Error del sistema: " + ex.toString());
        }  
    }
    
    
    public void mostrarVentanaFoto(MenuController refController) {
        if (refController.openFoto) {
        try {
            refController.openFoto = false;
            if (ventanaWebCam==null){
                ventanaWebCam = new Stage ();
                webCam = new WebCam(primaryStage, ventanaWebCam, refController);
                Scene scene = new Scene(webCam);                
                ventanaWebCam.setScene(scene);
//                ventanaWebCam.setHeight(350);
//                ventanaWebCam.setWidth(300);
//                ventanaWebCam.centerOnScreen();
//                ventanaWebCam.setResizable(false);
                ventanaWebCam.show();
            } else {
                ventanaWebCam.show();
                webCam.startWebCamCamera();
//                webCam.startWebCamStream();
            }
        } catch (Exception ex) {
            
        }
        } else {
            Dialogs.create()
                .title("Registro asistencia")
                .masthead("La ventana ya esta abierta.")
                .showInformation();
        }
    }
    
    //public void mostrarVentanaHuella(PacienteController refController, ResultSet rs) {
    public void mostrarVentanaHuella(MenuController refController) {
        if (refController.openHuella) {
            try {
                refController.openHuella = false;
//              if (ventanaHuella==null){
                ventanaHuella = new Stage ();
                //capturaHuella = new CapturaHuella(primaryStage, ventanaHuella, refController, rs);
                capturaHuella = new CapturaHuella(primaryStage, ventanaHuella, refController);
                Scene scene = new Scene(capturaHuella);                
                ventanaHuella.setScene(scene);
                ventanaHuella.show();
//              } else {ventanaHuella.show(); }
            } catch (Exception ex) { }
        } else {
            Dialogs.create()
                .title("Registro asistencia")
                .masthead("La ventana ya esta abierta.")
                .showInformation();
        }
    }
    
    public void cerrarVentanaPrincipal(){
        primaryStage.close();
//        if (webCam!=null)
//            webCam.disposeWebCamCamera();
    }
    
    public static void main(String[] args) {
        launch(args);
    }   

}