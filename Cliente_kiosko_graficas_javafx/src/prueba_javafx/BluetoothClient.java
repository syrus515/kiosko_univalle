package prueba_javafx;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.util.Vector;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.*;
import javax.bluetooth.*;
import javax.swing.JOptionPane;
/**
* A simple SPP client that connects with an SPP server
*/
public class BluetoothClient extends Thread implements DiscoveryListener{

//object used for waiting
private static Object lock=new Object();

//vector containing the devices discovered
private static Vector vecDevices=new Vector();

private static String connectionURL=null;

LocalDevice localDevice;
DiscoveryAgent agent;
StreamConnection streamConnection;
InputStream inStream;
BufferedReader entrada;
OutputStream outStream;
PrintWriter salida;
private Ecg1Signal ecg1Signal;
private Ecg2Signal ecg2Signal;
private Spo2Signal spo2Signal;
private RespSignal respSignal;
private StaticParameters staticParameter;
CommandsToRaspberry commands;
Monitor monitor;
ConnectionState connectionState;
AdminDevice admin;
static int indexRaspberry=-1;

public BluetoothClient(Ecg1Signal ecg1Signal, 
            Ecg2Signal ecg2Signal, Spo2Signal spo2Signal, RespSignal respSignal, StaticParameters staticParameter, 
            CommandsToRaspberry commands, Monitor monitor, ConnectionState connectionState, AdminDevice admin) throws IOException{
    this.ecg1Signal=ecg1Signal;
    this.ecg2Signal=ecg2Signal;
    this.spo2Signal=spo2Signal;
    this.respSignal=respSignal;    
    this.staticParameter=staticParameter;
    this.commands=commands;
    this.monitor=monitor;
    this.connectionState=connectionState;
    this.admin=admin;
    connectBluetooth();
    start();
}

@Override
    public void run() {
        
        String linea = null;
        String[] dato;
        boolean HandShake=false;
        boolean authFlag=false;
        boolean reqFlag=false;
        String authen="KUV!req!aut";
        String request="KUV!req!data";
        String lastCommand="";
        try {
            salida.write("Test String from SPP Client\r\n");
            salida.flush();
            writeSocket("KUV!Sec-WebSocket-Key: OI2Jzq7EZ4+dTkLaa3GLdw==");
            while( ( linea=entrada.readLine() ) != null ) {
                if(!connectionState.blueReadState()) break;
                dato = linea.split("!");
                if((linea.contains("XrNNNoSO/+Yc8Pz/yub6XLMEML8=")) && !authFlag){
                    HandShake = true;
                    writeSocket(authen);
                    System.out.println("Handshake OK");
                }if(HandShake){
//                    String comando=commands.getCommand();
//                    if(!lastCommand.equals(comando)){
//                        writeSocket(comando);
//                        lastCommand=comando;
//                    }
                    
                    
                    
                    if(dato[0].equals("KUV")){
                        if (dato[1].equals("msg")){
                            if(dato[2].equals("ok")){
                                if(dato[3].equals("type")){
                                    if(dato[4].equals("aut")){
                                        authFlag=true;
                                        System.out.println("Verify AUT OK");
                                        writeSocket(request);
                                       
                                    }
                                }
                            }
                        }else if (dato[1].equals("onda")){
 
                            if(dato[2].equals("ecg1")){
//      
                                ecg1Signal.getWave(dato[3]);
                                monitor.putData();
                            }else if(dato[2].equals("ecg2")){
//                    
                                ecg2Signal.getWave(dato[3]);
                                monitor.putData();
                            }else if(dato[2].equals("spo2")){
//                                System.out.println("spo2 recibido");
                                spo2Signal.getWave(dato[3]);
                                monitor.putData();
                            }else if(dato[2].equals("resp")){
                                respSignal.getWave(dato[3]);
                                monitor.putData();
                            }
                        }else if(dato[1].equals("estaticos")){
                           // writeSocket(this,request);
                            if(dato[2].equals("hr")){
                                staticParameter.getHr(dato[3]);
                            }if(dato[4].equals("rr")){
                                staticParameter.getResp(dato[5]);
                            }if(dato[6].equals("spo2oxi")){
                                staticParameter.getSpo2Oxi(dato[7]);
                            }if(dato[8].equals("spo2hr")){
                                staticParameter.getSpo2Hr(dato[9]);
                            }if(dato[10].equals("pr")){
                                staticParameter.getPresR(dato[11]);
                            }if(dato[12].equals("pd")){
                                staticParameter.getPresDias(dato[13]);
                            }if(dato[14].equals("pm")){
                                staticParameter.getPresMed(dato[15]);
                            }if(dato[16].equals("ps")){
                                staticParameter.getPresSist(dato[17]);
                            }if(dato[18].equals("temp")){
                                staticParameter.getTemp(dato[19]);
//                                 System.out.println("ESTATICOS RECIBIDO");
                            }    
                        }else if(dato[1].equals("tanita")){ //"KUV!tanita!peso!%s!fat!%s!agua!%s!musculo!%s"
                            System.out.println(linea);
                              if(dato[2].equals("peso")){
                                staticParameter.getWeight(dato[3]);
                            }if(dato[4].equals("fat")){
                                staticParameter.getBodyFat(dato[5]);
                            }if(dato[6].equals("agua")){
                                staticParameter.getWaterPercent(dato[7]);
                            }if(dato[8].equals("musculo")){
                                staticParameter.getMuscleMass(dato[9]);
                            }   
                        }else if (dato[1].equals("msg")){
                            if(dato[2].equals("ok")){
                                if(dato[3].equals("type")){
                                    if(dato[4].equals("logout")){
                                        authFlag=false;
                                        closeStreams();
                                        break;
                                    }
                                }
                            }
                        }
                    
                
                    }
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            closeStreams();
            connectionState.blueSetState(false);
            admin.dispositivoDesconectado();
                //System.exit(-1);
        }
    }
    public void connectBluetooth() throws IOException{

    localDevice = LocalDevice.getLocalDevice();
    agent = localDevice.getDiscoveryAgent();
    System.out.println("Address: "+localDevice.getBluetoothAddress());
    System.out.println("Name: "+localDevice.getFriendlyName());
    System.out.println("Starting device inquiry...");
    agent.startInquiry(DiscoveryAgent.GIAC, this);
    try {
        synchronized(lock){
            lock.wait();
        }
    }
    catch (InterruptedException e) {
        e.printStackTrace();
    }
        System.out.println("Device Inquiry Completed. ");

    //print all devices in vecDevices
    int deviceCount=vecDevices.size();


    if(deviceCount <= 0){
        System.out.println("No Devices Found .");
        //System.exit(0);
    }
    else{
        //print bluetooth device addresses and names in the format [ No. address (name) ]
        System.out.println("Bluetooth Devices: ");
        for (int i = 0; i <deviceCount; i++) {
            RemoteDevice remoteDevice=(RemoteDevice)vecDevices.elementAt(i);
            System.out.println((i+1)+". "+remoteDevice.getBluetoothAddress()+" ("+remoteDevice.getFriendlyName(true)+")");
            if(remoteDevice.getFriendlyName(true).equals("raspberrypi"))
                indexRaspberry=i;
            else if(remoteDevice.getFriendlyName(true).equals("raspberrypi #2"))
                indexRaspberry=i;
           
        }
    }
    
//    System.out.print("Choose Device index: ");
//    BufferedReader bReader=new BufferedReader(new InputStreamReader(System.in));

//    String chosenIndex=bReader.readLine();
//    int index=Integer.parseInt(chosenIndex.trim());
    //check for spp service
      try{
     if(indexRaspberry!=-1){     
    RemoteDevice remoteDevice=(RemoteDevice)vecDevices.elementAt(indexRaspberry);
    
    UUID[] uuidSet = new UUID[1];
    uuidSet[0]=new UUID("27012f0c68af4fbf8dbe6bbaf7aa432a", false);

    System.out.println("\nSearching for service...");
    agent.searchServices(null,uuidSet,remoteDevice,this);
     }
         }catch(java.lang.NullPointerException e){
         errorFatal(e, "Dispositivo no existe.");
     }catch(java.lang.ArrayIndexOutOfBoundsException e){
         errorFatal(e, "Dispositivo no existe.");
     }
    try {
        synchronized(lock){
            lock.wait();
        }
    }
    catch (InterruptedException e) {
        e.printStackTrace();
    }
    if(indexRaspberry!=-1){ 
     if(connectionURL==null){
        System.out.println("Device does not support Simple SPP Service.");
        //System.exit(0);
    }else{
     
   
         streamConnection=(StreamConnection)Connector.open(connectionURL);
         inStream=streamConnection.openInputStream();
         entrada=new BufferedReader(new InputStreamReader(inStream));
         outStream=streamConnection.openOutputStream();
         salida=new PrintWriter((outStream)); 
         connectionState.blueSetState(true);
     }
    }
    
}
    public  void writeSocket( String mensaje) {
        try {

            salida.println(new String(mensaje.getBytes(), "windows-1252"));
            salida.flush();

        } catch (Exception e) {
            connectionState.blueSetState(false);
            admin.dispositivoDesconectado();
        }
    }

     public void closeStreams(){
        if (entrada !=null) {
            try {
                entrada.close();
            } catch (IOException e1) {} // No se hace nada con la excepciÃ³n
            }
        if (salida !=null) {
            salida.close();
            }
        if (streamConnection !=null) {
            try {
                streamConnection.close();
            } catch (IOException e1) {} // No se hace nada con la excepciÃ³n
            }
    }
//methods of DiscoveryListener
public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
    //add the device to the vector
    if(!vecDevices.contains(btDevice)){
        vecDevices.addElement(btDevice);
    }
}

//implement this method since services are not being discovered
public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
    if(servRecord!=null && servRecord.length>0){
        connectionURL=servRecord[0].getConnectionURL(0,false);
    }
    synchronized(lock){
        lock.notify();
    }
}

//implement this method since services are not being discovered
public void serviceSearchCompleted(int transID, int respCode) {
    synchronized(lock){
        lock.notify();
    }
}


public void inquiryCompleted(int discType) {
    synchronized(lock){
        lock.notify();
    }

}//end method

private static void errorFatal(Exception excepcion, String mensajeError) {
        excepcion.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fatal."+ System.getProperty("line.separator") +
                mensajeError, "InformaciÃ³n para el usuario", JOptionPane.WARNING_MESSAGE);
        System.exit(-1);
    }
}