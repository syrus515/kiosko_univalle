

import java.awt.event.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Prueba{
	
	static ACOS3_SM sm_acos3;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	//static JacspcscLoader jacs = new JacspcscLoader();
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//JacspcscLoader jacs = new JacspcscLoader();
		sm_acos3= new ACOS3_SM();
//		
		int ret=sm_acos3.ListReaders();   //Busca el lector de smartcard. Si no lo encuentra devielve un valor menor a 0
		if(ret>=0)
			if(sm_acos3.ConnectACOS3()==0) {  //Realiza la conexion con el lector.
				sm_acos3.SetTerminalKey("3333333333333333"); //Configura la clave del Host. Debe ser de 16 caracteres, sino devuelve valor menor a 0 
				sm_acos3.SetCardKey("2222222222222222");//Configura la clave de la tarjeta. Debe ser de 16 caracteres, sino devuelve valor menor a 0 
			}
		//sm_acos3.FormatCard("00", "01", "FF", "FF");
		sm_acos3.MutualAuth("2222222222222222");//Realiza proceso de autenticacion entre el Host y la tarjeta. Se ingresa la cardkey como argumento
		sm_acos3.TextToWrite("Carlos Andres Gomez");//Establece el texto que se va escribir en el archivo interno de la tarjeta. Va primero que metodo WriteFile
		sm_acos3.WriteFile("00", "01", "00", "00", "70");//Metodo para escribir en archivo. Los dos primeros argumentos son un File ID, 
														//los siguientes 2 corresponden al offset de lectura o escritura y el ultimo corresponde al numero de caracteres.
														//los numerosson hexadecimales pero se ingresan como string
		sm_acos3.ReadFile("00", "01", "00", "00", "70");//Metodo para leer de archivo. Los dos primeros argumentos son un File ID, 
														//los siguientes 2 corresponden al offset de lectura o escritura y el ultimo corresponde al numero de caracteres.
														//los numerosson hexadecimales pero se ingresan como string
		
		System.out.println(sm_acos3.TextToRead()); //Se utiliza despues de ReadFile para obtener el texto leido del archivo
		
		
	}

}

		


