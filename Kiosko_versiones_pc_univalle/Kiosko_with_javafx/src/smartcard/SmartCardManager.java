package smartcard;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.awt.event.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class SmartCardManager {
	
	private CardChannel cardChannel;
	TerminalFactory terminalFactory;
    List<CardTerminal> cardTerminals;
    CardTerminal cardTerminal;
    Card card;;
    
    static final String ENCODING = "windows-1252"; 
	int retCode, maxLen = 8;
	boolean connActive; 
	public static final int INVALID_SW1SW2 = -450;
	private static String algorithm = "DES";
	private String cipherAlgorithm="DES";
	boolean cbSM=false;
	static String VALIDCHARS = "ABCDEFabcdef0123456789";
	private String txtCardKey;
    private String txtTerminalkey;
    private String ReadText;
    private String WriteText;
    byte [] CRnd = new byte[8];
    byte [] TRnd = new byte[8];
    byte [] SeqNum = new byte[8];
    byte [] tempArray = new byte[16];
    byte [] tmpResult = new byte[16];
    byte [] RecvBuff = new byte[8];
    byte [] SessionKey = new byte[16];
	
	public static final byte[] SELECT_FILE_FF02 = new byte[]{(byte)0xFF,(byte)0x02};  
	public static final byte[] SELECT_FILE_FF03 = new byte[]{(byte)0xFF,(byte)0x03}; 
	public static final byte[] SELECT_FILE_FF04 = new byte[]{(byte)0xFF,(byte)0x04};
	public final byte[] SELECT_FILE_DATA = new byte[]{(byte)0x00,(byte)0x01};
	public static final  CommandAPDU SUBMIT_IC = new CommandAPDU(
			new byte[] {(byte)0x80, 0x20, 0x07, 0x00, 0x08, 0x41, 0x43, 0x4F, 0x53, 0x54, 0x45, 0x53, 0x54});
	public static final  CommandAPDU START_SESSION = new CommandAPDU(
			new byte[] {(byte)0x80, (byte)0x84, 0x00, 0x00, 0x08}); 
	public static final  CommandAPDU AUTHENTICATE = new CommandAPDU(
			new byte[] {(byte)0x80, (byte)0x82, 0x00, 0x00, 0x10}); 
	
	private static final int BLOCK_SIZE = 0x70;
	
	public SmartCardManager() {
		
	}
	
	public int ListReaders() throws Exception {
		try {
			terminalFactory = TerminalFactory.getDefault();
	        cardTerminals = terminalFactory.terminals().list();
	        System.out.println("Terminals: " + cardTerminals);
	        if (cardTerminals.isEmpty()) {
	            throw new Exception("No card terminals available");
	        }
	        cardTerminal = cardTerminals.get(0);
	        if (cardTerminal.isCardPresent() == false) {
	            System.out.println("*** Insert card");
	            if (cardTerminal.waitForCardPresent(20 * 1000) == false) {
	                throw new Exception("no card available");
	            }
	        }            
	        card = cardTerminal.connect("T=0");
	        cardChannel = card.getBasicChannel(); 
		}catch (Exception e) {
			e.printStackTrace();
            System.out.println( "Error: " + e.toString());
            return -1;
		}
		return 0;
	}

	
	public int SetTerminalKey(String txtTerminalkey) {
		if(getCipher().equals("3DES")) {
		if(txtTerminalkey.length()!=16) {
			System.out.print("Key should have 16 characters!!\n");
			return -1;
		}else {
			this.txtTerminalkey=txtTerminalkey;
			return 0;
		}
		}else {
			if(txtTerminalkey.length()!=8) {
				System.out.print("Key should have 8 characters!!\n");
				return -1;
			}else {
				this.txtTerminalkey=txtTerminalkey;
				return 0;
		}
		}
			
	}
	public int SetCardKey(String txtCardKey) {
		if(getCipher().equals("3DES")) {
		if(txtCardKey.length()!=16) {
			System.out.print("Key should have 16 characters!!\n");
			return -1;
		}else {
			this.txtCardKey=txtCardKey;
			return 0;
		}	
		}else {
			if(txtCardKey.length()!=8) {
				System.out.print("Key should have 8 characters!!\n");
				return -1;
			}else {
				this.txtCardKey=txtCardKey;
				return 0;
			}	
		}
	}
	
	public int FormatCard(byte tFileID1, byte tFileID2, byte tFileLen1, byte tFileLen2) throws Exception {
		byte[] tmpArray = new byte[31];
		byte[] buff = new byte[256];
		String tmpStr = "";
		retCode = submitIC();
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(4, 0, "Insert ACOS3 card on contact card reader.");
			return -1;
		}
		//select FF 02
		retCode = selectFile(SELECT_FILE_FF02);
		if (retCode != ACSModule.SCARD_S_SUCCESS)
			return -1;
		//3. Write to FF 02
	    //   This will create 1 binary file, no Option registers and
	    //   Security Option registers defined, Personalization bit
		if((getCipher().equals("3DES")))
			tmpArray[0] = (byte)0x02;
		else
			tmpArray[0] = (byte)0x00;
		
		tmpArray[1] = (byte)0x00;
		tmpArray[2] = (byte)0x03;
		tmpArray[3] = (byte)0x00;
		
		retCode = writeRecord((byte)0x00, (byte)0x04, tmpArray);			
		if(retCode != ACSModule.SCARD_S_SUCCESS)
			return -1;
		
		displayOut(0, 0, "File FF 02 is updated.");
		//4. perform a reset for changes in ACOS3 to take effect
		connActive = false;
		card.disconnect(true);
		cardTerminal = cardTerminals.get(0);
        if (cardTerminal.isCardPresent() == false) {
            System.out.println("*** Insert card");
            if (cardTerminal.waitForCardPresent(20 * 1000) == false) {
                throw new Exception("no card available");
            }
        }
        card = cardTerminal.connect("T=0");
        cardChannel = card.getBasicChannel();
        displayOut(3, 0, "Card reset is successful.");
        connActive = true;
        retCode = submitIC();
        if(retCode != ACSModule.SCARD_S_SUCCESS)
			return -1;
        
      //select FF03
		retCode = selectFile(SELECT_FILE_FF03);
		if(retCode != ACSModule.SCARD_S_SUCCESS)
			return -1;
		else
			displayOut(2, 0, "Select FF 03 Success.");
		
		int tmpInt = 0;
		
		//write to FF03
		if(!(getCipher().equals("3DES")))
		{
			//record 02 for card key
			tmpStr = this.txtCardKey;
			for (int i = 0; i < 8; i++)
			{
			    tmpInt = (int)tmpStr.charAt(i);
			    buff[i] = (byte) tmpInt;
			}				
			retCode = writeRecord((byte) 0x02,(byte)0x08, buff);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			else
				displayOut(2, 0, "Write Card Key Success.");
			
			//record 03 for terminal key
			tmpStr = this.txtTerminalkey;
			for (int i = 0; i < 8; i++)
			{
			    tmpInt = (int)tmpStr.charAt(i);
			    buff[i] = (byte) tmpInt;
			}				
			retCode = writeRecord((byte) 0x03,(byte)0x08, buff);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			else
				displayOut(2, 0, "Write Terminal Key Success.");
		}else {

			//record 02 of cardkey
			tmpStr = this.txtCardKey;
			for (int i = 0; i < 8; i++)
			{
			    tmpInt = (int)tmpStr.charAt(i);
			    buff[i] = (byte) tmpInt;
			}				
			retCode = writeRecord((byte) 0x02,(byte)0x08, buff);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			
			//record 12 for cardkey
			tmpStr = this.txtCardKey;
			for (int i = 0; i < 8; i++)
			{
			    tmpInt = (int)tmpStr.charAt(i+8);
			    buff[i] = (byte) tmpInt;
			}				
			retCode = writeRecord((byte) 0x0C,(byte)0x08, buff);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			else
				displayOut(2, 0, "Write Card Key Success.");
			
			//record 03 for terminal key
			tmpStr = this.txtTerminalkey;
			for (int i = 0; i < 8; i++)
			{
			    tmpInt = (int)tmpStr.charAt(i);
			    buff[i] = (byte) tmpInt;
			}				
			retCode = writeRecord((byte) 0x03,(byte)0x08, buff);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			
			//record 13 for terminal key
			tmpStr = this.txtTerminalkey;
			for (int i = 0; i < 8; i++)
			{
			    tmpInt = (int)tmpStr.charAt(i+8);
			    buff[i] = (byte) tmpInt;
			}				
			retCode = writeRecord((byte) 0x0D,(byte)0x08, buff);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			else
				displayOut(2, 0, "Write Terminal Key Success.");
		
		}
		displayOut(0, 0, "FF 03 is updated ");
		
		//select FF 04
	    retCode = selectFile(SELECT_FILE_FF04);
	    if(retCode != ACSModule.SCARD_S_SUCCESS)
			return -1;
		//send IC code
		retCode = submitIC();
		if(retCode != ACSModule.SCARD_S_SUCCESS)
			return -1;
		
		//write to FF 04
	    //7.1. Write to first record of FF 04

		tmpArray[0] = (byte)tFileLen1;
		tmpArray[1] = (byte)tFileLen2;
		tmpArray[2] = (byte) 0x00;
		tmpArray[3] = (byte) 0x00;
		tmpArray[4] = (byte)tFileID1;
		tmpArray[5] = (byte)tFileID2;
		
		if(cbSM)
			tmpArray[6] = (byte)0xE0;
		else
			tmpArray[6] = (byte)0x80;
		
		retCode = writeRecord((byte)0x00, (byte)0x07, tmpArray);
		if(retCode != ACSModule.SCARD_S_SUCCESS)
			return -1;
		
		tmpStr = "";
		tmpStr = tFileID1 + " " + tFileID2;
		displayOut(0, 0, "User File " + tmpStr + " is defined.");
        
		return 0;
	}
	
	public String getRegister(int regNum) {
		return "";
	}
	
	public String readFile(byte[] fileId, int offset) throws CardException, IOException {
		String tmpStr="";
		selectFile(fileId);
		/*byte[] ReadBuffer=readBinary(offset);
                
		String str = bytesToString(ReadBuffer,ENCODING);
		str=str.substring(0, str.indexOf("|"));
		int i=0;
		while((ReadBuffer[i] & 0xFF) != 0x00)
		{	
			if(i < maxLen)
				tmpStr = tmpStr + (char)(ReadBuffer[i] & 0xFF);					
			i++;
		}*/
		String str= alteredReadBinary();
                
		return str;
		
	}
	public byte[] readFileSM(byte[] fileId, int offset) throws CardException, IOException {
		selectFile(fileId);
		return readBinary(offset);
	}
	
	public int WriteFile(String inputText, byte[] fileId, int offset) throws CardException, IOException {
		selectFile(fileId);
		byte[] inputBytes = inputText.getBytes();
		return writeBinary(inputBytes, offset);
	}
	
	public int MutualAuth(String txtCardKey) throws IOException, CardException {
		byte [] cKey = new byte[16];
		byte [] tKey = new byte[16];			
		byte [] ReverseKey = new byte[16];
		String tmpStr = "";
		retCode = StartSession();
		if (retCode != ACSModule.SCARD_S_SUCCESS)
			return -1;
		// Retrieve Terminal Key from Input Template
		int indx, tmpInt;
		tmpStr = txtTerminalkey;			
		for(int i = 0; i < txtTerminalkey.length(); i++)
			tKey[i] = (byte)((int)tmpStr.charAt(i));	
		
	//  Encrypt Random No (CRnd) with Terminal Key (tKey)
				//    tmpArray will hold the 8-byte Enrypted number
				for (indx = 0; indx < 8; indx++)
					tempArray[indx] = CRnd[indx];
				
				if (!(getCipher().equals("3DES"))) 
				{
					for (indx = 0; indx < 8; indx++)
					{
					    tmpInt = (int)tmpStr.charAt(indx);
					    tKey[indx] = (byte) tmpInt;
					}
					
					DES(tempArray, tKey);
				}
				else
				{
					for (indx = 0; indx < 16; indx++)
					{
					    tmpInt = (int)tmpStr.charAt(indx);
					    tKey[indx] = (byte) tmpInt;
					}
					
					TripleDES(tempArray,tKey);
				}
				
			//  Issue Authenticate command using 8-byte Encrypted No (tmpArray)
				//    and Random Terminal number (TRnd)
				for (indx = 0; indx < 8; indx++)
					tempArray[indx + 8] = TRnd[indx];

				retCode = Authenticate(tempArray);
				if (retCode != ACSModule.SCARD_S_SUCCESS)
				{
					displayOut(0, 0, "Mutual Authenticate failed");
					return -1;
				}
			
				// Get 8-byte result of card-side authentication
				// and save to tmpResult
	            retCode = getResponse();
	            if (retCode != ACSModule.SCARD_S_SUCCESS)
					return -1;            
	            
	        	for (indx = 0; indx < 8; indx++)
	                tmpResult[indx] = RecvBuff[indx];
	        	
	        	/*  Terminal-side authentication process
	            '  Retrieve Card Key from Input Template */		
				tmpStr = txtCardKey;
				for (indx = 0; indx < txtCardKey.length(); indx++)
				{
				    tmpInt = (int)tmpStr.charAt(indx);
				    cKey[indx] = (byte) tmpInt;
				}
				
				//  Compute for Session Key
				if (!(getCipher().equals("3DES"))) 
				{
					/*  for single DES
					' prepare SessionKey
					' SessionKey = DES (DES(RNDc, KC) XOR RNDt, KT) */

					// calculate DES(cRnd,cKey)
					for (indx = 0; indx < 8; indx++)
						tempArray[indx] = CRnd[indx];

					DES(tempArray, cKey);
					
					// XOR the result with tRnd
					for (indx = 0; indx < 8; indx++)
						tempArray[indx] = (byte)(tempArray[indx] ^ TRnd[indx]);
	            
					// DES the result with tKey
					DES(tempArray,tKey);

					// temp now holds the SessionKey
					for (indx = 0; indx < 8; indx++)
						SessionKey[indx] = tempArray[indx];
				}
				else
				{
					/*  for triple DES
					' prepare SessionKey
					' Left half SessionKey =  3DES (3DES (CRnd, cKey), tKey)
					' Right half SessionKey = 3DES (TRnd, REV (tKey))
					' tmpArray = 3DES (CRnd, cKey) */
					
					// calculate DES(cRnd,cKey)
					for (indx=0; indx<8; indx++)
						tempArray[indx] = CRnd[indx];

					TripleDES(tempArray, cKey);
					
					// XOR the result with tRnd
					for (indx = 0; indx < 8; indx++)
						tempArray[indx] = tempArray[indx] ^= TRnd[indx];
					
					// 3DES the result with tKey
					TripleDES(tempArray,tKey);
					
					// tmpArray holds the left half of SessionKey
					for (indx=0; indx<8;indx++)
						SessionKey[indx] = tempArray[indx];

					/* compute ReverseKey of tKey
					' just swap its left side with right side
					' ReverseKey = right half of tKey + left half of tKey */
					for (indx = 0;indx < 8; indx++)
						ReverseKey[indx] = tKey[8 + indx];
	           
					for (indx = 0;indx < 8;indx++)
						ReverseKey[8 + indx] = tKey[indx];

					// compute tmpArray = 3DES (TRnd, ReverseKey)
					for (indx = 0; indx < 8; indx++)
						tempArray[indx] = TRnd[indx];

					TripleDES(tempArray, ReverseKey);

					// tmpArray holds the right half of SessionKey
					for (indx = 0; indx < 8; indx++)
						SessionKey[indx + 8] = tempArray[indx];	
				}
				
				// compute DES (TRnd, SessionKey)
				for (indx = 0; indx < 8;indx++)
					tempArray[indx] = TRnd[indx];
				
				if (!(getCipher().equals("3DES"))) 
					DES(tempArray,SessionKey);
				else 
					TripleDES(tempArray,SessionKey);
							
				for (indx = 0; indx < 8;indx++)
				{
					if (tmpResult[indx] != tempArray[indx]) 
					{				
						displayOut(0,0,"Mutual Authentication failed.");
						return -1;
					}
				}
					
				displayOut(0,0,"Mutual Authentication is successful.");
				return 0;
	}
	
	public int StartSession() throws IOException, CardException {
		int ret=0;
		byte[] data;
			ResponseAPDU responseApdu = transmit(START_SESSION);
			int sw = responseApdu.getSW();
			if (0x9000 != sw) {
				ret=-1;
				throw new IOException("APDU response error at StartSession: "
						+ responseApdu.getSW());
			}
			RecvBuff = responseApdu.getData();

				System.out.println("Bytes received in STARTSESSION: "+printBytes(RecvBuff));
			//store random number generated by card to CRnd
			for(int i = 0; i < 8; i++)
				CRnd[i] = RecvBuff[i];
			
			for(int i = 0; i < 6; i++)
				SeqNum[i] = (byte)0x00;
				
			SeqNum[6] = RecvBuff[6];
			SeqNum[7] = RecvBuff[7];
			
			return ret;
	}
	
	public int Authenticate(byte [] DataIn) throws CardException, IOException
	{	
		int ret=0;
		byte[] readData;
		CommandAPDU readBinaryApdu = new CommandAPDU(0x80, 0x82,
				0x00, 0x00, DataIn, 0x00, 0x10);
		ResponseAPDU responseApdu = transmit(readBinaryApdu);
			int sw = responseApdu.getSW();
			if (0x9000 != sw) {
				ret=-1;
				System.out.println(String.format("got response 0x%04x instead of 0x9000", sw));
				throw new IOException("APDU response error at Authenticate: "
						+ responseApdu.getSW());
			}
			return ret;
	}
	
	public int getResponse() throws CardException, IOException
	{	
		int ret=0;
		byte[] readData;
		CommandAPDU readBinaryApdu = new CommandAPDU(0x80, 0xC0,
				0x00, 0x00, 0x08);
		ResponseAPDU responseApdu = transmit(readBinaryApdu);
			int sw = responseApdu.getSW();
			if (0x9000 != sw) {
				ret=-1;
				throw new IOException("APDU response error at getResponse: "
						+ responseApdu.getSW());
			}
			RecvBuff = responseApdu.getData();
			System.out.println("Bytes received in getResponse: "+printBytes(RecvBuff));
			return ret;
	}
	
	public int submitIC() throws CardException, IOException{
		int ret=0;
		ResponseAPDU responseApdu = transmit(SUBMIT_IC);
		int sw = responseApdu.getSW();
		if (0x9000 != sw) {
			ret=-1;
			throw new IOException("APDU response error in submitIC: "
					+ responseApdu.getSW());
			
		}
		return ret;
	}
	
	public byte[] sendCommand(CommandAPDU command) throws CardException {  
		    ResponseAPDU responseAPDU = this.cardChannel.transmit(command);  
		    int responseStatus = responseAPDU.getSW();  
		  
		    if (!isResponseOk(responseStatus)){  
		      throw new RuntimeException("Error code: " + responseStatus);  
		    }  
		      
		    return responseAPDU.getData();  
		  } 
	
	private boolean isResponseOk(int responseStatus){  
	    return responseStatus == 0x9000;  
	  }
	
	public String bytesToString(byte[] data,final String enc) {  
		try 
		{  
			return new String(data, enc);  
		} catch (UnsupportedEncodingException e) {  
		    	throw new RuntimeException("Encoding " + enc + " not supported");  
		    }  
	} 
    
	private int selectFile(byte[] fileId) throws CardException,
			FileNotFoundException {
		int ret=0;
		CommandAPDU selectFileApdu = new CommandAPDU(0x80, 0xA4, 0x00, 0x00,
				fileId,0x00,0x02);
		ResponseAPDU responseApdu = transmit(selectFileApdu);
		if (responseApdu.getSW()<0x9000) {
			ret=-1;
			throw new FileNotFoundException(
					"wrong status word after selecting file: "
							+ Integer.toHexString(responseApdu.getSW()));
		}
		try {
			// SCARD_E_SHARING_VIOLATION fix
			Thread.sleep(20);
		} catch (InterruptedException e) {
			throw new RuntimeException("sleep error: " + e.getMessage());
		}
		return ret;
	}
        
        private boolean hasBar(String text)
        {
            return text.contains("|");
        }
        
        private String alteredReadBinary()
        {
            String result="";
            int offset= 0x00;
            while(true)
            {
                try {
                    byte[] ReadBuffer=readBinary(offset);
                    String temp= bytesToString(ReadBuffer,ENCODING);
                    result+= temp;
                    if(temp.contains("|")) break;  
                    offset+= ReadBuffer.length;
                } catch (CardException ex) {
                    Logger.getLogger(SmartCardManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SmartCardManager.class.getName()).log(Level.SEVERE, null, ex);
                }                              
            }
            return result;
        }
	
	private byte[] readBinary(int offset) throws CardException, IOException //Método a modificar para leer todo el string
        {
		//int offset = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data;
		//do {
			CommandAPDU readBinaryApdu = new CommandAPDU(0x80, 0xB0,
					offset >> 8, offset & 0xFF, BLOCK_SIZE);
			ResponseAPDU responseApdu = transmit(readBinaryApdu);
			int sw = responseApdu.getSW();
			if (0x6B00 == sw) {
				/*
				 * Wrong parameters (offset outside the EF) End of file reached.
				 * Can happen in case the file size is a multiple of 0xff bytes.
				 */
				//break;
			}
			if (0x9000 != sw) {
				throw new IOException("APDU response error at readBinary: "
						+ responseApdu.getSW());
			}

			/*
			 * Introduce some delay for old Belpic V1 eID cards.
			 */
			// try {
			// Thread.sleep(50);
			// } catch (InterruptedException e) {
			// throw new RuntimeException("sleep error: " + e.getMessage(), e);
			// }
			data = responseApdu.getData();
			baos.write(data);
			offset += data.length;
		//} while (BLOCK_SIZE == data.length);
		return baos.toByteArray();
	}
	
	private byte[] readRecord(byte recNo, byte dataLen) throws CardException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data;
			CommandAPDU readBinaryApdu = new CommandAPDU(0x80, 0xB2, recNo,0x00,dataLen);
			ResponseAPDU responseApdu = transmit(readBinaryApdu);
			int sw = responseApdu.getSW();
			if (0x6B00 == sw) {

			}
			if (0x9000 != sw) {
				throw new IOException("APDU response error at readRecord: "
						+ responseApdu.getSW());
			}

			data = responseApdu.getData();
			baos.write(data);
		return baos.toByteArray();
	}
	
	private int writeBinary(byte[] dataIn, int offset) throws CardException, IOException {
		int ret=0;
			CommandAPDU readBinaryApdu = new CommandAPDU(0x80, 0xD0,
					offset >> 8, offset & 0xFF, dataIn,0x00, dataIn.length);
			ResponseAPDU responseApdu = transmit(readBinaryApdu);
			int sw = responseApdu.getSW();
			if (0x9000 != sw) {
				ret=-1;
				throw new IOException("APDU response error at writeBinary: "
						+ responseApdu.getSW());
			}	
			return ret;
	}
	
	private int writeRecord(byte recNo, byte dataLen, byte[] dataIn) throws CardException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data;
		int ret=0;
			CommandAPDU readBinaryApdu = new CommandAPDU(0x80, 0xD2,recNo,0x00,dataIn,0x00,dataLen);
			ResponseAPDU responseApdu = transmit(readBinaryApdu);
			int sw = responseApdu.getSW();
			if (0x9000 != sw) {
				ret=-1;
				throw new IOException("APDU response error at writeRecord: "
						+ responseApdu.getSW());
				
			}

		return ret;
	}
	
	private ResponseAPDU transmit(CommandAPDU commandApdu) throws CardException {
		ResponseAPDU responseApdu = this.cardChannel.transmit(commandApdu);
		if (0x6c == responseApdu.getSW1()) {
			/*
			 * A minimum delay of 10 msec between the answer 6C xx and the
			 * next APDU is mandatory for eID v1.0 and v1.1 cards.
			 */
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException("cannot sleep");
			}
			responseApdu = this.cardChannel.transmit(commandApdu);
		}
		return responseApdu;
	}
	public void SelectCypher(String cipherAlgorithm) {
		this.cipherAlgorithm=cipherAlgorithm;
	}

	public String getCipher() {
		return this.cipherAlgorithm;
	}
	
	public void displayOut(int mType, int msgCode, String printText)
	{
		switch(mType)
		{
			case 1:				
			{	
				System.out.print("! " + printText);
				System.out.print(ACSModule.GetScardErrMsg(msgCode) + "\n");
				break;
			}
			case 2:	System.out.print("< " + printText + "\n"); break;
			case 3: System.out.print("> " + printText + "\n"); break;
			default: System.out.print("- " + printText + "\n"); break;
		}		
	}
	
	public static void DES(byte Data[], byte key[])
	{
		byte[] keyTemp = new byte[8];
		for(int i =0; i<8; i++)
		{
			keyTemp[i] = key[i];
		}
        try 
        {   
            DESKeySpec desKeySpec = new DESKeySpec(keyTemp);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);                  
           
            Cipher encryptCipher = Cipher.getInstance(algorithm);
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
           
            byte encryptedContents[] = process(Data, encryptCipher);
    
            for(int i=0;i<8;i++)
            {
            	Data[i] = encryptedContents[i];
            }
           
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
	}
	
	public static void DES2(byte Data[], byte key[])
	{
		byte[] keyTemp = new byte[8];
		for(int i =0; i<8; i++)
		{
			keyTemp[i] = key[i];
		}
        try 
        {   
            DESKeySpec desKeySpec = new DESKeySpec(keyTemp);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);                  
           
            Cipher encryptCipher = Cipher.getInstance(algorithm);
            encryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
           
            byte encryptedContents[] = process(Data, encryptCipher);
    
            for(int i=0;i<8;i++)
            {
            	Data[i] = encryptedContents[i];
            }
           
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
	}
	
	private static byte[] process(byte processMe[], Cipher cipher) throws Exception 
	{
		// Create the input stream to be used for encryption
		ByteArrayInputStream in = new ByteArrayInputStream(processMe);
	       
		// Now actually encrypt the data and put it into a
		// ByteArrayOutputStream so we can pull it out easily.
		CipherInputStream processStream = new CipherInputStream(in, cipher);
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		int whatWasRead = 0;
		while ((whatWasRead = processStream.read()) != -1) 
		{
			resultStream.write(whatWasRead);
		}
	       
		return resultStream.toByteArray();
	}
	
	public static void TripleDES(byte Data[], byte key[])
	{
		byte[] keyTemp = new byte[16];
			
		try 
		{   	
			for(int i =0; i<8; i++)
			{
				keyTemp[i] = key[i];
			}
	        	
			//Encrypt        	           
			DESKeySpec desKeySpec = new DESKeySpec(keyTemp);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);                  
	           
			Cipher encryptCipher = Cipher.getInstance(algorithm);
			encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
			
			byte encryptedContents[] = process(Data, encryptCipher);
			
			for(int i=0;i<8;i++)
			{
				Data[i] = encryptedContents[i];
			}
	            
			//End Encrypt 
	            
			//Decrypt
			for(int i =0; i<8; i++)
			{
				keyTemp[i] = key[i+8];
			}
	    		
			DESKeySpec desKeySpec2 = new DESKeySpec(keyTemp);
			SecretKeyFactory keyFactory2 = SecretKeyFactory.getInstance(algorithm);
			SecretKey secretKey2 = keyFactory2.generateSecret(desKeySpec2);                  
	           
			Cipher encryptCipher2 = Cipher.getInstance(algorithm);
			encryptCipher2.init(Cipher.DECRYPT_MODE, secretKey2);
	            
			byte decryptedContents[] = process(encryptedContents, encryptCipher2);
	        	
			for(int i=0;i<8;i++)
			{
				Data[i] = decryptedContents[i];
			}
	            
			//End Decrypt
			
			//Encrypt
			for(int i =0; i<8; i++)
			{
				keyTemp[i] = key[i];
			}
	            
			DESKeySpec desKeySpec3 = new DESKeySpec(keyTemp);
			SecretKeyFactory keyFactory3 = SecretKeyFactory.getInstance(algorithm);
			SecretKey secretKey3 = keyFactory3.generateSecret(desKeySpec3);                  
			
			Cipher encryptCipher3 = Cipher.getInstance(algorithm);
			encryptCipher3.init(Cipher.ENCRYPT_MODE, secretKey3);
	           
			byte encryptedContents2[] = process(Data, encryptCipher3);
	            
			for(int i=0;i<8;i++)
			{
				Data[i] = encryptedContents2[i];
			}	            
	            
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void TripleDES2(byte Data[], byte key[])
	{
		byte[] keyTemp = new byte[16];
		
		try 
		{   	
			for(int i =0; i<8; i++)
			{
				keyTemp[i] = key[i];
			}
	        	
			//Encrypt        	           
			DESKeySpec desKeySpec = new DESKeySpec(keyTemp);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);                  
			
			Cipher encryptCipher = Cipher.getInstance(algorithm);
			encryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
			
			byte encryptedContents[] = process(Data, encryptCipher);
			
			for(int i=0;i<8;i++)
			{
				Data[i] = encryptedContents[i];
			}
	            
			//End Encrypt 
	            
			//Decrypt
			for(int i =0; i<8; i++)
			{
				keyTemp[i] = key[i+8];
			}
	    		
			DESKeySpec desKeySpec2 = new DESKeySpec(keyTemp);
			SecretKeyFactory keyFactory2 = SecretKeyFactory.getInstance(algorithm);
			SecretKey secretKey2 = keyFactory2.generateSecret(desKeySpec2);                  
			
			Cipher encryptCipher2 = Cipher.getInstance(algorithm);
			encryptCipher2.init(Cipher.ENCRYPT_MODE, secretKey2);
			
			byte decryptedContents[] = process(encryptedContents, encryptCipher2);
	        
			for(int i=0;i<8;i++)
			{
				Data[i] = decryptedContents[i];
			}
	            
			//End Decrypt
	            
			//Encrypt
			for(int i =0; i<8; i++)
			{
				keyTemp[i] = key[i];
			}
	            
			DESKeySpec desKeySpec3 = new DESKeySpec(keyTemp);
			SecretKeyFactory keyFactory3 = SecretKeyFactory.getInstance(algorithm);
			SecretKey secretKey3 = keyFactory3.generateSecret(desKeySpec3);                  
			
			Cipher encryptCipher3 = Cipher.getInstance(algorithm);
			encryptCipher3.init(Cipher.DECRYPT_MODE, secretKey3);
			
			byte encryptedContents2[] = process(Data, encryptCipher3);
			
			for(int i=0;i<8;i++)
			{
				Data[i] = encryptedContents2[i];
			}
	            
	            
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static String printBytes(byte[] bytes) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("[ ");
	    for (byte b : bytes) {
	        sb.append(String.format("0x%02X ", b));
	    }
	    sb.append("]");
	    return sb.toString();
	}

}
