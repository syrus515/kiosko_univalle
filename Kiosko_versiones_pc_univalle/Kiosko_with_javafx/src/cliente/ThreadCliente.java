package cliente;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Controlador.LeerLinea;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;

/**
 *
 * @author Carlos Andres
 */
public class ThreadCliente extends Thread {

    private static final int PUERTO = 9001;
    private Socket socketCliente;
    private static BufferedReader entrada;
    private PrintWriter salida;
    public PrintWriter salida2;
    Ecg1Signal ecg1Signal;
    Ecg2Signal ecg2Signal;
    Spo2Signal spo2Signal;
    RespSignal respSignal;
    StaticParameters staticParameter;
    CommandsToRaspberry commands;
    Monitor monitor;
    ConnectionState connectionState;
    AdminDevice admin;
    boolean leerSenales;
    boolean banderaIniciar= false;

    public ThreadCliente(Ecg1Signal ecg1Signal,
            Ecg2Signal ecg2Signal, Spo2Signal spo2Signal, RespSignal respSignal, StaticParameters staticParameter,
            CommandsToRaspberry commands, Monitor monitor, ConnectionState connectionState, AdminDevice admin, boolean leerSenales) throws IOException {
        this.ecg1Signal = ecg1Signal;
        this.ecg2Signal = ecg2Signal;
        this.spo2Signal = spo2Signal;
        this.respSignal = respSignal;
        this.staticParameter = staticParameter;
        this.commands = commands;
        this.monitor = monitor;
        this.connectionState = connectionState;
        this.admin = admin;
        arrancarCliente();
        if(banderaIniciar)
        {
            start(); // Se arranca el hilo.
        }
        
    }
    
    public void switchLectura()
    {
        leerSenales= !leerSenales;        
    }
    
    public boolean isSocketConnected()
    {        
        return socketCliente.isConnected();
    }

    private void arrancarCliente() {

        try {
            //socketCliente = new Socket("181.142.112.79", PUERTO); // puerto del servidor por omisiÃ³n
            //socketCliente = new Socket("192.168.43.110", PUERTO); // puerto del servidor por omisiÃ³n
            System.out.println("Arrancando el cliente."); 
            socketCliente = new Socket("169.254.212.48", PUERTO); // puerto del servidor por omisiÃ³n, 192.168.43.115 - 181.206.10.190            
            socketCliente.setKeepAlive(true);
            connectionState.tcpSetState(true);
            banderaIniciar= true; //Hubo conexión, por tanto el hilo puede iniciar.            
            
        } catch (java.lang.NumberFormatException e1) {
            // No se puede arrancar el cliente porque se introdujo un nÃºmero de puerto que no es entero.
            // Error irrecuperable: se sale del programa. No hace falta limpiar el socket, pues no llegÃ³ a
            // crearse.
            errorFatal(e1, "Numero de puerto invalido.");
        } catch (java.net.UnknownHostException e2) {
            // No se puede arrancar el cliente. Error irrecuperable: se sale del programa.
            // No hace falta limpiar el socket, pues no llegÃ³ a crearse.
            errorFatal(e2, "No se localiza el ordenador servidor con ese nombre.");
        } catch (java.lang.SecurityException e3) {
            // No se puede arrancar el cliente. Error irrecuperable: se sale del programa.
            // No hace falta limpiar el socket, pues no llegÃ³ a crearse.
            String mensaje = "Hay restricciones de seguridad en el servidor para conectarse por el "
                    + "puerto " + PUERTO;
            errorFatal(e3, mensaje);
        } catch (SocketException e) {
            //e.printStackTrace();
            
            //Cuadro de diálogo en caso de no haber conexión.
            Alert dialogoAlerta= new Alert(Alert.AlertType.CONFIRMATION);
            dialogoAlerta.setTitle("Falla en la conexión");
            dialogoAlerta.setHeaderText(null);
            dialogoAlerta.initStyle(StageStyle.UTILITY);
            dialogoAlerta.setContentText("No se encuentra el dispositivo\n ¿Desea continuar sin conexión?");
            Optional<ButtonType> result= dialogoAlerta.showAndWait();
            if(result.get()!=ButtonType.OK)
            {
                System.exit(0); //Se cierra el programa si así lo quiso el usuario.
            }
        }
        catch (IOException e4) {
            // No se puede arrancar el cliente. Error irrecuperable: se sale del programa.
            // No hace falta limpiar el socket, pues no llegÃ³ a crearse.
            String mensaje = "No se puede conectar con el puerto " + PUERTO + " de la maquina "
                    + "servidora. Asegurese de que el servidor esta¡ en marcha.";
            errorFatal(e4, mensaje);
        }
    }
    
    private static String lineaLeida= "vacio";
    
    final Runnable stuffToDo = new Thread() {
        @Override 
        public void run() { 
            try {
                lineaLeida=entrada.readLine();
            } catch (IOException ex) {
                lineaLeida= "vacio";
            }         
        }
      };
    
    
    public synchronized void run() {
        String linea = null;
        String[] dato;
        boolean HandShake = false;
        boolean authFlag = false;
        boolean reqFlag = false;
        String authen = "KUV!req!aut";
        String request = "KUV!req!data";
        int[] indRef = {0, 0, 0};
        String lastCommand = "";
        try {//aviable >0 y ready
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
            salida2 = new PrintWriter(new OutputStreamWriter(socketCliente.getOutputStream()));

            commands.setCommand("", 0);
            writeSocket("KUV!Sec-WebSocket-Key: OI2Jzq7EZ4+dTkLaa3GLdw==");            
            //LeerLinea lector;
            //while ((linea = entrada.readLine()) != null) {
            do{
            //while (true) {
                //System.out.println("Esto estoy imprimiendo de más: "); 
                
                //linea = entrada.readLine();
                //lector= new LeerLinea(entrada);            
                //lector.start();
                //double contador= 0;
                
                
                //------------------------------
                
                final ExecutorService executor = Executors.newSingleThreadExecutor();
                final Future future = executor.submit(stuffToDo);
                executor.shutdown(); // This does not cancel the already-scheduled task.
                
                future.get(2000, TimeUnit.MILLISECONDS);
                linea= lineaLeida;
                
                //------------------------------
                
                //linea= lector.getLinea();
                
                //salida.write("Probar conexion"); 
                //linea = entrada.readLine(); 
                
                if (!connectionState.tcpReadState()) {
                    break;
                }
                dato = linea.split("!");
                if ((linea.contains("XrNNNoSO/+Yc8Pz/yub6XLMEML8=")) && !authFlag) {
                    HandShake = true;
                    writeSocket(authen);
                    System.out.println("Handshake OK");
                }
                if (HandShake) {
//                    String comando=commands.getCommand();
//                    if(!comando.equals("")){
//                        writeSocket(comando);
//                        commands.setCommand("",0);
//                    }
                    if (dato[0].equals("KUV")) {
                        if (dato[1].equals("msg")) {
                            if (dato[2].equals("ok")) {
                                if (dato[3].equals("type")) {
                                    if (dato[4].equals("aut")) {
                                        authFlag = true;
                                        System.out.println("Verify AUT OK");
                                        writeSocket(request);

                                    }
                                }
                            }
                        } else if (dato[1].equals("onda") && leerSenales) {

                            if (dato[2].equals("ecg1")) {
//      
                                ecg1Signal.getWave(dato[3]);
//                                monitor.putData();
                            } else if (dato[2].equals("ecg2")) {
//                    
                                ecg2Signal.getWave(dato[3]);
//                                monitor.putData();
                            } else if (dato[2].equals("spo2")) {
//                                System.out.println("spo2 recibido");
                                spo2Signal.getWave(dato[3]);
//                                monitor.putData();
                            } else if (dato[2].equals("resp")) {
                                respSignal.getWave(dato[3]);
//                                monitor.putData();
                            }
                        } else if (dato[1].equals("estaticos")) {
                            // writeSocket(this,request);
                            if (dato[2].equals("hr")) {
                                staticParameter.getHr(dato[3]);
                            }
                            if (dato[4].equals("rr")) {
                                staticParameter.getResp(dato[5]);
                            }
                            if (dato[6].equals("spo2oxi")) {
                                staticParameter.getSpo2Oxi(dato[7]);
                            }
                            if (dato[8].equals("spo2hr")) {
                                staticParameter.getSpo2Hr(dato[9]);
                            }
                            if (dato[10].equals("pr")) {
                                staticParameter.getPresR(dato[11]);
                            }
                            if (dato[12].equals("pd")) {
                                staticParameter.getPresDias(dato[13]);
                            }
                            if (dato[14].equals("pm")) {
                                staticParameter.getPresMed(dato[15]);
                            }
                            if (dato[16].equals("ps")) {
                                staticParameter.getPresSist(dato[17]);
                            }
                            if (dato[18].equals("temp")) {
                                staticParameter.getTemp(dato[19]);
                                System.out.println("ESTATICOS RECIBIDO");
                            }
                        } else if (dato[1].equals("tanita")) { //"KUV!tanita!peso!%s!fat!%s!agua!%s!musculo!%s"
                            System.out.println(linea);
                            if (dato[2].equals("peso")) {
                                staticParameter.getWeight(dato[3]);
                            }
                            if (dato[4].equals("fat")) {
                                staticParameter.getBodyFat(dato[5]);
                            }
                            if (dato[6].equals("agua")) {
                                staticParameter.getWaterPercent(dato[7]);
                            }
                            if (dato[8].equals("musculo")) {
                                staticParameter.getMuscleMass(dato[9]);
                            }
                        } else if (dato[1].equals("msg")) {
                            if (dato[2].equals("ok")) {
                                if (dato[3].equals("type")) {
                                    if (dato[4].equals("logout")) {
                                        authFlag = false;
                                        closeStreams();
                                        break;
                                    }
                                }
                            }
                        }

                    }
                }
            }while(linea!="vacio");
            /*if(lector.isAlive())
            {
                lector.interrupt();
                lector= null;
            }*/
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Llega acá Socket");
        } catch (java.lang.NullPointerException e1) {
            System.out.println("Llega acá Null");
            e1.printStackTrace();
        } catch (IOException e1) {
            System.out.println("Llega acá IO");
            e1.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(ThreadCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            //admin.mc.mensajeDesconexion();
            System.out.println("Excepción de perdida, controlada");            
        }
            
            //admin.mc.mensajeDesconexion();
            
            //connectionState.tcpSetState(false);
            //closeStreams();
            System.out.println("Llega acá1");
            admin.desconectarDespuesDeCable();
            System.out.println("Llega acá2");
            //admin.dispositivoDesconectado();
            //System.exit(-1);
        
    }

    public void writeSocket(String mensaje) {
        try {
            this.salida.println(new String(mensaje.getBytes(), "windows-1252"));
            this.salida.flush();
        } catch (Exception e) {
            connectionState.tcpSetState(false);
            admin.dispositivoDesconectado();
        }
    }
    
    public void anularStreams()
    {
        if (socketCliente != null) {
            try {
                socketCliente.close();
            } catch (IOException e2) {
            }
            socketCliente = null;
        }
        System.out.println("Sin problema al cerrar el socket");
    }

    public void closeStreams() {
        if (entrada != null) {
            try {
                entrada.close();
            } catch (IOException e1) {
            }
            entrada = null;
        }
        System.out.println("Sin problema al cerrar la entrada");
        if (salida != null) {
            salida.close();
            salida = null;
        }
        System.out.println("Sin problema al cerrar la salida");
        if (socketCliente != null) {
            try {
                socketCliente.close();
            } catch (IOException e2) {
            }
            socketCliente = null;
        }
        System.out.println("Sin problema al cerrar el socket");
    }

    private static void errorFatal(Exception excepcion, String mensajeError) {
        excepcion.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error fatal." + System.getProperty("line.separator")
                + mensajeError, "InformaciÃ³n para el usuario", JOptionPane.WARNING_MESSAGE);
    }
}
