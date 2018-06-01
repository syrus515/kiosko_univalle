package BD;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionDBs {
    public Connection db1;
    //Método
    /******************************************************************************************** 
    * Permite realizar la conexión a las dos DB para la actualización.
    * @return un String notificación si la conexión fue exitosa o no.
    ********************************************************************************************/ 
    public String conectar() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            db1 = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Kiosko",  "kiosko",  "kiosko");
        }
        catch (Exception error) {
            return "Error de conexión";
        }
        return "OK";
    }
    
    //Método
    /******************************************************************************************** 
    * Permite realizar la desconexión a las dos DB de la actualización.
    * @return un String notificación si la desconexión fue exitosa o no.
    ********************************************************************************************/ 
    public String desconectar(){
        try {
                db1.close();
        }
        catch (Exception error) {
            return "Error de desconexión";
        }
        return "OK";
    }
}