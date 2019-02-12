package smartcard;

import java.awt.event.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class Prueba{
	
    public static void main(String[] args) throws Exception {
    	SmartCardManager ACOS3_card = new SmartCardManager();
    	ACOS3_card.SelectCypher("DES");
    	String str="Prueba 2 smartcardio exitosa|";
    	String str2="Tercer string escrito exitosamente|";
        
    	int ret=ACOS3_card.ListReaders();
    	if(ret>=0) 
        {
    		ACOS3_card.SetTerminalKey("33333333"); //Configura la clave del Host. Debe ser de 8/16 caracteres, sino devuelve valor menor a 0 
    		ACOS3_card.SetCardKey("22222222");//Configura la clave de la tarjeta. Debe ser de 8/16 caracteres, sino devuelve valor menor a 0 
        }        
    	ACOS3_card.FormatCard((byte)0x00, (byte)0x01, (byte)0xFF, (byte)0xFF);//Dar formato a la tarjeta. Arg 1 y 2: ID archivo registro datos; Arg 3 y 4: Tama�o de memoria asignado
    	if(ACOS3_card.MutualAuth("222222222")==0)//Realiza proceso de autenticacion entre el Host y la tarjeta. Se ingresa la cardkey como argumento
    	{
    	ACOS3_card.WriteFile(str, ACOS3_card.SELECT_FILE_DATA, 0);//Metodo para escribir en archivo. Arg 1: String a escribir. Arg 2: file ID(2 bytes)
    															  //Arg 3: offset dentro del archivo para escribir nueva cadena 
    														
    	//ACOS3_card.WriteFile("cuarta entrada 4|", ACOS3_card.SELECT_FILE_DATA, 0x70);
    	//ACOS3_card.WriteFile("quinta entrada entrada 5|", ACOS3_card.SELECT_FILE_DATA, 0xE0);
    	//ACOS3_card.WriteFile("sexta entrada entrada 6|", ACOS3_card.SELECT_FILE_DATA, 0x5F10);

														
    	String readText=ACOS3_card.readFile(ACOS3_card.SELECT_FILE_DATA, 0x00);//Metodo para leer de archivo. Arg 1: file ID (2 bytes); Arg 2: offset dentro de archivo a leer
   
		
    	System.out.println(readText);
    	readText=ACOS3_card.readFile(ACOS3_card.SELECT_FILE_DATA, 0x70);
    	System.out.println(readText);
    	readText=ACOS3_card.readFile(ACOS3_card.SELECT_FILE_DATA, 0xE0);
    	System.out.println(readText);
    	readText=ACOS3_card.readFile(ACOS3_card.SELECT_FILE_DATA, 0x5F10);
    	System.out.println(readText);
    	}else {
    		System.out.println("Autenticacion fallida");
    	}
    }

}

		