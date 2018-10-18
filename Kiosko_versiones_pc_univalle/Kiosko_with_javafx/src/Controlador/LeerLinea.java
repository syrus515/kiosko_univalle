/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Miguel Askar
 */
public class LeerLinea extends Thread
{
    private String linea;
    private BufferedReader lector;
   
    
    public LeerLinea(BufferedReader lector)            
    {
        this.lector= lector;
    }
    
    @Override
    public void run()
    {
        this.linea= "vacio"; //Si pasa el tiempo y readline no sirve, la línea sea leída como vacío.
        try {
            this.linea= lector.readLine();
        } catch (IOException ex) {
            System.out.println("Logré capturar la excepcion");
        }        
    }
    
    public void setLinea(String linea) {
        this.linea = linea;
    }

    public void setLector(BufferedReader lector) {
        this.lector = lector;
    }

    public String getLinea() {
        return linea;
    }

    public BufferedReader getLector() {
        return lector;
    }
    
}
