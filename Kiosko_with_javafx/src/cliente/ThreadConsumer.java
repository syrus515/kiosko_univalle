package cliente;

import javafx.scene.paint.Color;
import vista.MenuController;

public class ThreadConsumer implements Runnable {

    MenuController mc;
    Ecg1Signal ecg1Signal;
    Ecg2Signal ecg2Signal;
    Spo2Signal spo2Signal;
    RespSignal respSignal;
    StaticParameters staticParameters;
    Monitor monitor;
    ConnectionState connectionState;
    private volatile boolean running = true;

    boolean banderaPintarSPO2 = true; // Determina si puede pintar o no en la interfaz el SPO2.
    boolean banderaPintarECG1 = true; // Determina si puede pintar o no en la interfaz el ECG1.
    boolean banderaPintarECG2 = true; // Determina si puede pintar o no en la interfaz el ECG2.
    boolean banderaPintarRESP = true; // Determina si puede pintar o no en la interfaz el RESP.

    public ThreadConsumer(Ecg1Signal ecg1Signal, Ecg2Signal ecg2Signal, Spo2Signal spo2Signal, RespSignal respSignal,
            StaticParameters staticParameters, Monitor monitor, ConnectionState connectionState, MenuController mc) {
        this.mc = mc;
        this.ecg1Signal = ecg1Signal;
        this.ecg2Signal = ecg2Signal;
        this.spo2Signal = spo2Signal;
        this.respSignal = respSignal;
        this.staticParameters = staticParameters;
        this.monitor = monitor;
        this.connectionState = connectionState;
    }

    @Override
    public synchronized void run() {
        boolean flag = false;
        double ecg1 = 0;
        double ecg2 = 0;
        int spo2 = 0;
        double resp = 0;
        byte[] buffer_in = null;

        // *****************************************************************
        int tiempoECG1 = 0;
        //double mitadInterfazECG1 = (mc.altoGC / 2);
        double mitadInterfazECG1 = 150;
        double valorY2ECG1 = mitadInterfazECG1;
        double valorYECG1 = mitadInterfazECG1;
        double valorX2ECG1 = 20;
        double valorXECG1 = 20;
        int altoOndaECG1 = 256;
        int reducirSenalECG1 = 10; // Valor a amplificar la senal SPO2
        // *****************************************************************
        int tiempoECG2 = 640;
        //double mitadInterfazECG2 = (mc.altoGC / 2);
        double mitadInterfazECG2 = 150;
        double valorY2ECG2 = mitadInterfazECG2;
        double valorYECG2 = mitadInterfazECG2;
        double valorX2ECG2 = 20 + 640;
        double valorXECG2 = 20 + 640;
        int altoOndaECG2 = 256;
        int reducirSenalECG2 = 10; // Valor a amplificar la senal SPO2
        // *****************************************************************
        int tiempoSPO2 = 0;
        //double mitadInterfazSPO2 = (mc.altoGC / 2);
        double mitadInterfazSPO2 = 460;
        double valorY2SPO2 = mitadInterfazSPO2;
        double valorYSPO2 = mitadInterfazSPO2;
        double valorX2SPO2 = 20;
        double valorXSPO2 = 20;
        int altoOndaSPO2 = 256;
        int reducirSenalSPO2 = 1; // Valor a amplificar la senal SPO2
        // *****************************************************************
        int tiempoRESP = 640;
        //double mitadInterfazRESP = (mc.altoGC / 2);
        double mitadInterfazRESP = 460;
        double valorY2RESP = mitadInterfazRESP;
        double valorYRESP = mitadInterfazRESP;
        double valorX2RESP = 20 + 640;
        double valorXRESP = 20 + 640;
        int altoOndaRESP = 256;
        int reducirSenalRESP = 1; // Valor a amplificar la senal SPO2
        // *****************************************************************

        while (running) {
            
            if (!ecg1Signal.isEmpty()) {
                ecg1 = ecg1Signal.readWave();
                if (ecg1 < 0 || ecg1 > 5000) {
                    ecg1 = 0;
                }
                banderaPintarECG1 = true;
            } else {
//                monitor.getData();
                banderaPintarECG1 = false;
            }
            if (!ecg2Signal.isEmpty()) {
                ecg2 = ecg2Signal.readWave();
                if (ecg2 < 0 || ecg2 > 5000) {
                    ecg2 = 0;
                }
                banderaPintarECG2 = true;
            } else {
//                monitor.getData();
                banderaPintarECG2 = false;
            }
            if (!respSignal.isEmpty()) {
                resp = respSignal.readWave();
                if (resp < 0 || resp > 256) {
                    resp = 0;
                }
                banderaPintarRESP = true;
            } else {
//                monitor.getData();
                banderaPintarRESP = false;
            }
            if (!spo2Signal.isEmpty()) {
                spo2 = spo2Signal.readWave();
                if (spo2 < 0 || spo2 > 256) {
                    spo2 = 0;
                }
                banderaPintarSPO2 = true;
            } else {
//                monitor.getData();
                banderaPintarSPO2 = false;
            }

            if (banderaPintarECG1) {
                // ***********************************************************
                if (tiempoECG1 > (640 - (mc.ptoInicial * 2))) {
                    tiempoECG1 = 0;
                    valorY2ECG1 = mitadInterfazECG1;
                    valorYECG1 = mitadInterfazECG1;
                    valorX2ECG1 = mc.ptoInicial;
                    valorXECG1 = mc.ptoInicial;
                    mc.graficarECG1();
                }

                // reducir el tamano de la señal a un valor menor (escalar).
                ecg1 = ecg1 / reducirSenalECG1;
                // invierte el valor pero que el canva en la parte superior inicia en la posicion (0,0) del ecg1.
                ecg1 = (altoOndaECG1 - ecg1) + 100;

                tiempoECG1++;
                valorX2ECG1 = valorXECG1;
                valorXECG1 = (mc.ptoInicial + tiempoECG1);
                valorY2ECG1 = valorYECG1;
                valorYECG1 = ecg1;
                try {
                    if ((valorYECG1 >= 5 && valorYECG1 <= 305) && (valorY2ECG1 >= 5 && valorY2ECG1 <= 305)) {
                        mc.gc.setStroke(Color.CYAN);
                        mc.gc.setLineWidth(2);
                        mc.gc.strokeLine(valorX2ECG1, valorY2ECG1, valorXECG1, valorYECG1);
                        mc.gc.setStroke(Color.BLACK);
                        mc.gc.setLineWidth(1);
                    }
                } catch (Exception e) {
                    System.out.println("Error JavaFX, parámetros");
                }
                // ***********************************************************                    
            }
            
            if (banderaPintarECG2) {
                // ***********************************************************
                if (tiempoECG2 > (1280 - (mc.ptoInicial * 2))) {
                    tiempoECG2 = 640;
                    valorY2ECG2 = mitadInterfazECG2;
                    valorYECG2 = mitadInterfazECG2;
                    valorX2ECG2 = mc.ptoInicial + 640;
                    valorXECG2 = mc.ptoInicial + 640;
                    mc.graficarECG2();
                }

                // reducir el tamano de la señal a un valor menor (escalar).
                ecg2 = ecg2 / reducirSenalECG2;
                // invierte el valor pero que el canva en la parte superior inicia en la posicion (0,0) del ecg2.
                ecg2 = (altoOndaECG2 - ecg2) + 100;

                tiempoECG2++;
                valorX2ECG2 = valorXECG2;
                valorXECG2 = (mc.ptoInicial + tiempoECG2);
                valorY2ECG2 = valorYECG2;
                valorYECG2 = ecg2;
                try {
                    if ((valorYECG2 >= 5 && valorYECG2 <= 305) && (valorY2ECG2 >= 5 && valorY2ECG2 <= 305)) {
                        mc.gc.setStroke(Color.CYAN);
                        mc.gc.setLineWidth(2);
                        mc.gc.strokeLine(valorX2ECG2, valorY2ECG2, valorXECG2, valorYECG2);
                        mc.gc.setStroke(Color.BLACK);
                        mc.gc.setLineWidth(1);
                    }
                } catch (Exception e) {
                    System.out.println("Error JavaFX, parámetros");
                }
                // ***********************************************************
            }

            if (banderaPintarSPO2) {
                // ***********************************************************
                if (tiempoSPO2 > (640 - (mc.ptoInicial * 2))) {
                    tiempoSPO2 = 0;
                    valorY2SPO2 = mitadInterfazSPO2;
                    valorYSPO2 = mitadInterfazSPO2;
                    valorX2SPO2 = mc.ptoInicial;
                    valorXSPO2 = mc.ptoInicial;
                    mc.graficarSPO2();
                }
                // invierte el valor pero que el canva en la parte superior inicia en la posicion (0,0) del SPO2.
                spo2 = altoOndaSPO2 - spo2;

                tiempoSPO2++;
                valorX2SPO2 = valorXSPO2;
                valorXSPO2 = (mc.ptoInicial + tiempoSPO2);
                valorY2SPO2 = valorYSPO2;
                valorYSPO2 = spo2 + 310;
                //mc.gc3.fillOval(tiempoSPO2, spo2, 1, 1);
                try {
                    if ((valorYSPO2 >= 310 && valorYSPO2 <= 610) && (valorY2SPO2 >= 310 && valorY2SPO2 <= 610)) {
                        mc.gc.setStroke(Color.ORANGE);
                        mc.gc.setLineWidth(2);
                        mc.gc.strokeLine(valorX2SPO2, valorY2SPO2, valorXSPO2, valorYSPO2);
                        mc.gc.setStroke(Color.BLACK);
                        mc.gc.setLineWidth(1);
                    }
                } catch (Exception e) {
                    System.out.println("Error JavaFX, parámetros");
                }
                // ***********************************************************
            }

            if (banderaPintarRESP) {
                // ***********************************************************
                if (tiempoRESP > (1280 - (mc.ptoInicial * 2))) {
                    tiempoRESP = 640;
                    valorY2RESP = mitadInterfazRESP;
                    valorYRESP = mitadInterfazRESP;
                    valorX2RESP = mc.ptoInicial + 640;
                    valorXRESP = mc.ptoInicial + 640;
                    mc.graficarRESP();
                }
                // invierte el valor pero que el canva en la parte superior inicia en la posicion (0,0) del RESP.
                resp = altoOndaRESP - resp;

                tiempoRESP++;
                valorX2RESP = valorXRESP;
                valorXRESP = (mc.ptoInicial + tiempoRESP);
                valorY2RESP = valorYRESP;
                valorYRESP = resp + 310;
                try {
                    if ((valorYSPO2 >= 310 && valorYSPO2 <= 610) && (valorY2SPO2 >= 310 && valorY2SPO2 <= 610)) {
                        mc.gc.setStroke(Color.YELLOW);
                        mc.gc.setLineWidth(2);
                        mc.gc.strokeLine(valorX2RESP, valorY2RESP, valorXRESP, valorYRESP);
                        mc.gc.setStroke(Color.BLACK);
                        mc.gc.setLineWidth(1);
                    }
                } catch (Exception e) {
                    System.out.println("Error JavaFX, parámetros");
                }
                // ***********************************************************
            }

//            System.out.println(" ParAmetros-> " + spo2 );
//            System.out.println("Sistole: " + staticParameters.readPresSist() +
//                    ", Diastole: " + staticParameters.readPresDias()+ 
//                    ", Media: " + staticParameters.readPresMed() + 
//                    ", HR: " + staticParameters.readSpo2Hr());            
//            System.out.println(" ParAmetros-> " + staticParameters.readHr() + ", "
//                    + staticParameters.readResp() + ", "
//                    + staticParameters.readSpo2Oxi() + ", "
//                    + staticParameters.readSpo2Hr() + ", "
//                    + staticParameters.readPresR() + ", "
//                    + staticParameters.readPresDias() + ", "
//                    + staticParameters.readPresMed() + ", "
//                    + staticParameters.readPresSist());

//            System.out.println(" ParAmetros-> Peso: " + staticParameters.readWeight() + ", "
//                    + ", Grasa: " + staticParameters.readBodyFat() + ", "
//                    + ", %Agua: " + staticParameters.readWaterPercent() + ", "
//                    + ", Masa Muscular: " + staticParameters.readMuscleMass() + ", ");
            try {
                
                mc.asignarStaticParameters(staticParameters.readHr(),
                    + staticParameters.readResp(),
                    + staticParameters.readSpo2Oxi(),
                    + staticParameters.readSpo2Hr(),
                    + staticParameters.readPresR(),
                    + staticParameters.readPresDias(),
                    + staticParameters.readPresMed(),
                    + staticParameters.readPresSist());
                
                mc.pintarPesa(staticParameters.readWeight(),
                        staticParameters.readBodyFat(),
                        staticParameters.readWaterPercent(),
                        staticParameters.readMuscleMass());
            
            } catch (Exception e) {
                System.out.println("Error JavaFX, parámetros");
            }
        }
    }

    public void stopThread() {
        running = false;
    }
}
