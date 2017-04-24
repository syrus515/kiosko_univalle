package prueba_javafx;


import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;



/*
 * Esta clase es el administrador del cliente. Estan definidos los metodos de conexion y desconexion,
 * asi como los metodos para enviar comandos al dispositivo de medicion.
 * @author Carlos Andres
 */
public class AdminDevice {
    
    ThreadCliente tcpCliente=null;
    BluetoothClient bluetoothCliente=null;
    Ecg1Signal ecg1Signal;
    Ecg2Signal ecg2Signal;
    Spo2Signal spo2Signal;
    RespSignal respSignal;
    StaticParameters staticParameters;
    CommandsToRaspberry commands;
    Monitor monitor;
    ConnectionState connectionState;
    ThreadConsumer consumidor=null;
    Thread hiloConsumidor=null;
    
    private ConcurrentLinkedQueue<Integer> queueECG1 = new ConcurrentLinkedQueue<Integer>();
    private ConcurrentLinkedQueue<Integer> queueECG2 = new ConcurrentLinkedQueue<Integer>();
    private ConcurrentLinkedQueue<Integer> queueSpo2 = new ConcurrentLinkedQueue<Integer>();
    private ConcurrentLinkedQueue<Integer> queueResp = new ConcurrentLinkedQueue<Integer>();
    
    
    public AdminDevice(){
        ecg1Signal=new Ecg1Signal(queueECG1);
        ecg2Signal=new Ecg2Signal(queueECG2);
        spo2Signal=new Spo2Signal(queueSpo2);
        respSignal=new RespSignal(queueResp);
        staticParameters=new StaticParameters();
        commands=new CommandsToRaspberry();
        connectionState=new ConnectionState(); 
    }
    
    public void ConectarBluetooth(){
        connectionState.tcpSetState(false);
        connectionState.connectByBluetooth();
        monitor=new Monitor();
        try {
            bluetoothCliente=new BluetoothClient(ecg1Signal,ecg2Signal,spo2Signal,respSignal,staticParameters,commands,monitor,connectionState,this);
            connectionState.reset();
            if(connectionState.blueReadState()){
//                consumidor=new ThreadConsumer(ecg1Signal, ecg2Signal, spo2Signal, respSignal, staticParameters,monitor,connectionState);
//                hiloConsumidor = new Thread(consumidor, "Consumer1");
//                hiloConsumidor.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(AdminDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void ConectarTcp(){
        connectionState.blueSetState(false);
        connectionState.connectByTCP();
        monitor=new Monitor();
        try {
            tcpCliente=new ThreadCliente(ecg1Signal,ecg2Signal,spo2Signal,respSignal,staticParameters,commands,monitor,connectionState,this);
            connectionState.reset();
            if(connectionState.tcpReadState()){
                //consumidor=new ThreadConsumer(ecg1Signal, ecg2Signal, spo2Signal, respSignal, staticParameters,monitor,connectionState);
               // hiloConsumidor = new Thread(consumidor, "Consumer2");
                //hiloConsumidor.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(AdminDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    public void dispositivoDesconectado(){
        if (hiloConsumidor != null) {
            try {
                consumidor.stopThread();
                hiloConsumidor.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(AdminDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
            consumidor=null;
            hiloConsumidor=null;
            monitor=null;
            System.out.println("Thread successfully stopped.");
        }
    }
    public void desconectarCliente(){
        if(tcpCliente!=null){
            dispositivoDesconectado();
            connectionState.tcpSetState(false);
            tcpCliente.closeStreams();
            tcpCliente=null;
        }
        if(bluetoothCliente!=null){
            dispositivoDesconectado();
            connectionState.blueSetState(false);
            bluetoothCliente.closeStreams();
            bluetoothCliente=null;
        }
    }
    /**
 * Envia comandos de configuracion u ordenes de medicion al dispositivo a traves
 * de TCP/IP 
 *
 * @param  command  String correspondiente al comando a enviar. Para ver todos los comandos, ir a seccion ver tambien.
 * @param  setPoint Int para algunos comandos de configuracion. 
 * @see    CommandsToRaspberry
 */
    public void enviarComando(String command, int setPoint){
        commands.setCommand(command,setPoint);
        String comando=commands.getCommand();
        if(connectionState.tcpReadState() && !connectionState.blueReadState()){
            if(!comando.equals("")){
                tcpCliente.writeSocket(comando);
                commands.setCommand("",0);
            }
        }else if(connectionState.blueReadState() && !connectionState.tcpReadState()){
            if(!comando.equals("")){
                bluetoothCliente.writeSocket(comando);
                commands.setCommand("",0);
            }
        }
    }
        /**
 * Envia orden de medicion a la bascula Tanita, ingresando todos los valores requeridos.
 *
 * @param  ID  Numero en string de identificacion de usuario. Puede asignar valor fijo entre 20 y 256.
 * @param  genero sexo del paciente. Asignar "masculino" o "femenino".
 * @param  edad Edad del paciente en formato string.
 * @param  estatura Estatura en centimetros del paciente en formato string.
 * @param  tipoActividad Actividad fisica del paciente. Asignar "sedentario", "regular" o "deportista" segun sea el caso.
 * @see    CommandsToRaspberry
 */
    public void solicitarTanita(String ID, String genero, String edad, String estatura, String tipoActividad){
        commands.setUserTanita(ID,genero,edad,estatura,tipoActividad);
        String comando=commands.getCommand();
                if(connectionState.tcpReadState() && !connectionState.blueReadState()){
            if(!comando.equals("")){
                tcpCliente.writeSocket(comando);
                commands.setCommand("",0);
            }
        }else if(connectionState.blueReadState() && !connectionState.tcpReadState()){
            if(!comando.equals("")){
                bluetoothCliente.writeSocket(comando);
                commands.setCommand("",0);
            }
        }
    }
}
