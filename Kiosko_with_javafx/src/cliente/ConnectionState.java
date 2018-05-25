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
public class ConnectionState {
    private boolean bluetoothConnected=false;
    private boolean tcpConnected=false;
    private int connection=-1;

    public synchronized void blueSetState(boolean state){
        bluetoothConnected=state;
    }
    
    public synchronized boolean blueReadState(){
        return bluetoothConnected;
    }
    
    public synchronized void tcpSetState(boolean state){
        tcpConnected=state;
    }
    
    public synchronized boolean tcpReadState(){
        return tcpConnected;
    }
    
    public synchronized void connectByBluetooth(){
        connection=0;
    }
    
    public synchronized void connectByTCP(){
        connection=1;
    }
    
    public synchronized void reset(){
        connection=-1;
    }
    
    public synchronized int getConnection(){
        return connection;
    }
}

