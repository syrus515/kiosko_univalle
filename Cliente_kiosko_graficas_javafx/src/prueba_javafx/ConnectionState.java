package prueba_javafx;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Carlos Andres
 */
public class ConnectionState {
    private boolean bluetoothConnected=false;
    private boolean tcpConnected=false;
    private int connection=-1;
 /**
 * Permite establecer bandera de estado de conexion bluetooth
 *
 * @param state if true: conexion bluetooth exitosa, if false: bluetooth desconectado 
 */
    public synchronized void blueSetState(boolean state){
        bluetoothConnected=state;
    }
 /**
 * Retorna estado de conexion bluetooth
 *
 * @return if true: conexion bluetooth exitosa, if false: bluetooth desconectado 
 */   
    public synchronized boolean blueReadState(){
        return bluetoothConnected;
    }
 /**
 * Permite establecer bandera de estado de conexion TCP/IP
 *
 * @param state if true: conexion tcp/ip exitosa, if false: tcp/ip desconectado 
 */
    public synchronized void tcpSetState(boolean state){
        tcpConnected=state;
    }
 /**
 * Retorna estado de conexion tcp/ip
 *
 * @return if true: conexion tcp/ip exitosa, if false: tcp/ip desconectado 
 */   
    public synchronized boolean tcpReadState(){
        return tcpConnected;
    }
 /**
 * Bandera para establecer prioridad de conexion por bluetooth
 *
 */    
    public synchronized void connectByBluetooth(){
        connection=0;
    }
 /**
 * Bandera para establecer prioridad de conexion por tcp/ip
 *
 */     
    public synchronized void connectByTCP(){
        connection=1;
    }
 /**
 * Reset prioridad de coneccion
 *
 */     
    public synchronized void reset(){
        connection=-1;
    }
  /**
 * Bandera para establecer prioridad de conexion por bluetooth
 *
 * @return tipo de conexion con prieoridad. Si es cero hay prioridad para bluetooth, si es 1 hay prioridad para tcp, 
 * y si es -1 no hay prioridad.
 */    
    public synchronized int getConnection(){
        return connection;
    }
}

