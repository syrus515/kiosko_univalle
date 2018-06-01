package vista;

import BD.ConexionDBs;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class AutenticarController {
    @FXML
    private TextField usuario;
    @FXML
    private PasswordField clave;
    
    private Kiosko programaPrincipal;
    Alert popup;
    
    private static SecretKey key;       
    private static Cipher cipher;  
    private static final String algoritmo = "AES";
    private static final int keysize = 16; 
    
    //Método
    /******************************************************************************************** 
    * Programa para actualizar una tabla del servidor con la misma tabla del programa local.
    * @param conexión1 conexión de la base de datos 1 local.
    * @param tabla la tabla que será actualizada.
    * @return 0 si el usuario no esta en el sistema y 1 si esta y su identificación es correcta.
    * @throws dispara SQLException.
    ********************************************************************************************/ 
    /**
     * Called when the user clicks ok.
     */
    @FXML
    public void AutenticarUsuario() {
        String mensaje;
        try {
            ConexionDBs conexiones = new ConexionDBs();
            String val = conexiones.conectar();
            if (val.equals("OK")) {
                Statement stmt1 = conexiones.db1.createStatement();
                addKey("Fergon");
                String palabra = encriptar(clave.getText());
                mensaje = "Select * From usuarios Where usuario = '" + usuario.getText() + "' and contrasena = '" + palabra + "'";
                ResultSet rs1 = stmt1.executeQuery(mensaje);
                rs1.last();
                int resultado = rs1.getRow();
                if (resultado > 0){
                    rs1.first();
                    String usuario = rs1.getString("usuario");
                    String nombre = rs1.getString("nombre");
                    String rol = rs1.getString("rol");
                    // Abrir la nueva ventana y cerrar la ventana de autenticar.
                    // programaPrincipal.mostrarVentanaRegPacientes(usuario, nombre, rol);
                    programaPrincipal.mostrarMenu(usuario, nombre, rol);
                } else {
                    popup = new Alert(AlertType.ERROR);
                    popup.setTitle("Usuario");
                    popup.setHeaderText(null);
                    popup.setContentText("No existe");
                    popup.showAndWait();
                }
                if(rs1!=null) rs1.close();
                conexiones.desconectar();
            } else {
                popup = new Alert(AlertType.ERROR);
                popup.setTitle("Error conexión");
                popup.setHeaderText(null);
                popup.setContentText("Error en la conexión de la DB.");
                popup.showAndWait();
            }
        } catch (Exception ex) {
            popup = new Alert(AlertType.ERROR);
            popup.setTitle("Error DB");
            popup.setHeaderText(null);
            popup.setContentText("Error en la consulta de la DB.");
            popup.showAndWait();
        }
    }
    @FXML
    public void cerrarVentana() {
        programaPrincipal.cerrarVentanaPrincipal();
    }
    
    public void setProgramaPrincipal(Kiosko programaPrincipal) {
        this.programaPrincipal = programaPrincipal;
    }
    
    public void addKey( String value ){
        byte[] valuebytes = value.getBytes();            
        key = new SecretKeySpec( Arrays.copyOf( valuebytes, keysize ) , algoritmo );      
    }
    
    public String encriptar(String palabra) {
        String resultado = "";
        try {
            cipher = Cipher.getInstance( algoritmo );             
            cipher.init( Cipher.ENCRYPT_MODE, key );             
            byte[] textobytes = palabra.getBytes();
            byte[] cipherbytes = cipher.doFinal( textobytes );
            resultado = DatatypeConverter.printBase64Binary(cipherbytes);
            
        } catch (NoSuchAlgorithmException ex) {
            System.err.println( ex.getMessage() );
        } catch (NoSuchPaddingException ex) {
            System.err.println( ex.getMessage() );
        } catch (InvalidKeyException ex) {
            System.err.println( ex.getMessage() );
        } catch (IllegalBlockSizeException ex) {
            System.err.println( ex.getMessage() );
        } catch (BadPaddingException ex) {
            System.err.println( ex.getMessage() );
        }
        return resultado;
    }
    
}