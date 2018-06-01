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
public class CommandsToRaspberry implements SetRaspConfig{
    String estado="default";
    String setNumber;
    String leads="5";
    String nibpMode="manual";
    String nibpState="stop";
    String nibpTest="notest";
    String configCommand="";//"KUV!config!leadsEcg!"+leads+"!nibp!"+nibpMode+";"+nibpState+";"+nibpTest;
    
    @Override
    public void setCommand(String command, int setPoint) {
        estado=command;
        setNumber=String.valueOf(setPoint);
        //Funcion de reset de la tarjeta, para configurar valores por defecto. setPoint=0
        if(estado.equals("resetBoard"))
            configCommand="KUV!config!SetGeneral!reset";
        //Configura el tipo de paciente. Adulto: setPoint=0, Pediatrico: setPoint=1, Neonato: setPoint=2
        else if(estado.equals("tipoPaciente"))
            configCommand="KUV!config!SetGeneral!patientType!"+setNumber;
        //Configura filtro para visualizacion de la señal ecg. Filtro para diagnostico: setPoint=0, monitor: setPoint=1, operacion: setPoint=2
        else if(estado.equals("filtroEcg"))
            configCommand="KUV!config!SetEcg!filter!"+setNumber;
        //Configura ganancia para la señal ecg del canal 1. setPoint puede ser 0,1,2,3 ó 4
        else if(estado.equals("gananciaEcg1"))
            configCommand="KUV!config!SetEcg!gain!0!"+setNumber;
        //Configura ganancia para la señal ecg del canal 2. setPoint puede ser 0,1,2,3 ó 4
        else if(estado.equals("gananciaEcg2"))
            configCommand="KUV!config!SetEcg!gain!1!"+setNumber;
        //Configura la deriva que aparecera en el canal 1. setPoint= 1 al 7
        else if(estado.equals("channel1Lead"))
            configCommand="KUV!config!SetEcg!channelLead!0!"+setNumber;
        //Configura la deriva que aparecera en el canal 1. setPoint= 1 al 7
        else if(estado.equals("channel2Lead"))
            configCommand="KUV!config!SetEcg!channelLead!1!"+setNumber;
        //Configuracion de 5 derivaciones para el ECG. setPoint=0
        else if(estado.equals("5leads")){
            configCommand="KUV!config!SetEcg!numberLeads!1";
            //Configuracion de 3 derivaciones para el ECG. setPoint=0
        }else if(estado.equals("3leads")){
            configCommand="KUV!config!SetEcg!numberLeads!0";
        //Configura ganancia para la señal de respiracion. setPoint puede ser 0,1,2,3 ó 4
        }else if(estado.equals("gananciaResp"))
            configCommand="KUV!config!SetResp!gain!"+setNumber;
        //Configura el tiempo de calculo de la alarma de apnea. si no se quiere alarma, setPoint=0. El tiempo de calculo, setPoint= 1 al 7
        else if(estado.equals("delayApneaAlarm"))
            configCommand="KUV!config!SetResp!delayAlarm!"+setNumber;
        //
        else if(estado.equals("tempMode"))
            configCommand="KUV!config!SetTemp!mode!"+setNumber;
        
        else if(estado.equals("senseSpo2"))
            configCommand="KUV!config!SetSpo2!sense!"+setNumber;
        //Configura la toma de presion de forma manual. setPoint=0
        else if(estado.equals("manualPressure")){
            configCommand="KUV!config!SetNIBP!mode!0";
        //Configura la toma de presion de forma automatica, cada tantos minutos de acuerdo a setPoint (valores del 1 al 12)
        }else if(estado.equals("continousPressure")){
            configCommand="KUV!config!SetNIBP!mode!"+setNumber;
        //comando para comenzar la toma de presion, sea modo manual o automatico. setPoint=0
        }else if(estado.equals("startPressure")){
            configCommand="KUV!config!SetNIBP!state!start";
        //comando para detener la toma de presion. setPoint=0
        }else if(estado.equals("stopPressure")){
           configCommand="KUV!config!SetNIBP!state!stop";
        //comando para calibrar. setPoint=0
        }else if(estado.equals("calibratePressure"))
           configCommand="KUV!config!SetNIBP!calibrate";
        //comando de reset para el tensiometro. configuraciones por defecto. setPoint=0
        else if(estado.equals("resetPressure"))
           configCommand="KUV!config!SetNIBP!reset";
        //Preconfigura la presion inicial en mmHg para la medicion de paciente adulto. setPiont= 60 a 240. Valor por defecto 160.
        else if(estado.equals("presettingPressureAdult"))
           configCommand="KUV!config!SetNIBP!pressetPressure!0!"+setNumber;
        //Preconfigura la presion inicial en mmHg para la medicion de paciente pediatrico. setPiont= 60 a 240. Valor por defecto 120.
        else if(estado.equals("presettingPressurePediat"))
           configCommand="KUV!config!SetNIBP!presetPressure!1!"+setNumber;
        //Preconfigura la presion inicial en mmHg para la medicion de paciente neonato. setPiont= 60 a 240. Valor por defecto 70.
        else if(estado.equals("presettingPressureNeonato"))
           configCommand="KUV!config!SetNIBP!presetPressure!2!"+setNumber;
        //comando para pedir ultimo valor medido de presion arterial. setPoint=0
        else if(estado.equals("getLastPressure"))
           configCommand="KUV!config!SetNIBP!getResult";
        else if(estado.equals(""))
            configCommand="";
           
        
    }

    @Override
    public String getCommand() {
        return configCommand;
    }

    @Override
    public void setUserTanita(String id, String genero, String edad, String estatura, String tipoActividad) {
        configCommand="KUV!config!SetUserTanita!ID!"+id+"!genero!"+genero+"!edad!"+edad+"!estatura!"+estatura+"!actividad!"+tipoActividad;
    }

//    @Override
//    public String getUserTanita() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
    
}
