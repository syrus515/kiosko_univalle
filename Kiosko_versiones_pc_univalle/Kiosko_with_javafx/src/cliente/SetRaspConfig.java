package cliente;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Carlos Andres
 */
public interface SetRaspConfig {
    
    public void setCommand(String command, int setPoint);
    public String getCommand();
    public void setUserTanita(String id, String genero, String edad, String estatura, String tipoActividad);
    //public String getUserTanita();
}
