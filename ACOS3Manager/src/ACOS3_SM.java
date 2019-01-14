

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ACOS3_SM {
	
	//JPCSC Variables
		int retCode, maxLen = 8;
		boolean connActive; 
		public static final int INVALID_SW1SW2 = -450;
		private static String algorithm = "DES";
		static String VALIDCHARS = "ABCDEFabcdef0123456789";
		
		//All variables that requires pass-by-reference calls to functions are
		//declared as 'Array of int' with length 1
		//Java does not process pass-by-ref to int-type variables, thus Array of int was used.
		int [] ATRLen = new int[1]; 
		int [] nBytesRet = new int[1];
		int [] hContext = new int[1]; 
		int [] cchReaders = new int[1];
		int [] hCard = new int[1];
		int [] PrefProtocols = new int[1]; 		
		int [] RecvLen = new int[1];
		int SendLen = 0;
		int reqType;	
		byte [] SendBuff = new byte[262];
		byte [] RecvBuff = new byte[262];
		byte [] CRnd = new byte[8];
		byte [] TRnd = new byte[8];
		byte [] tmpResult = new byte[32];
		byte [] CipherKey = new byte[16];
		byte [] SessionKey = new byte[16];
		byte [] SeqNum = new byte[8];
		byte [] tempArray = new byte[32];
		byte [] szReaders = new byte[1024];	
		byte SW1, SW2;
		
		int indexACOSReader;
		int ret;
		boolean enableConnectACOS = false;
	    String cipherAlgorithm="3DES";
	    boolean cbSM=true;
	    
	    String tFileID1;
	    String tFileID2;
	    String tFileLen1;
	    String tFileLen2;
	    
	    String tFID1;
	    String tFID2;
	    String tOffset1;
	    String tOffset2;
	    String tLen;
	    
	    private String txtCardKey;
	    private String txtTerminalkey;
	    
	    String ReadText;
	    String WriteText;
	    
		
		List<String> cbReader = new ArrayList<String>();
		
		static JacspcscLoader jacs = new JacspcscLoader();
		//JacspcscLoader jacs;
		
		public ACOS3_SM() {
			//this.jacs=jacs;
			//ListReaders();
			//ConnectACOS3();
		}
		
		public int ListReaders() {
			
			
			//1. Establish context and obtain hContext handle
			retCode = jacs.jSCardEstablishContext(ACSModule.SCARD_SCOPE_USER, 0, 0, hContext);
			if (retCode != ACSModule.SCARD_S_SUCCESS)
		    {
				System.out.print("Calling SCardEstablishContext...FAILED\n");
		      	displayOut(1, retCode, "");
		      	return -1;
		    }
			
			//2. List PC/SC card readers installed in the system
			retCode = jacs.jSCardListReaders(hContext, 0, szReaders, cchReaders);
			if (retCode != ACSModule.SCARD_S_SUCCESS)
		    {
				System.out.print("Calling SCardListReaders...FAILED\n");
		      	displayOut(1, retCode, "");
		      	return -1;
		    }
			
			int offset = 0;
			cbReader.clear();
			
			for (int i = 0; i < cchReaders[0]-1; i++)
			{				
			  	if (szReaders[i] == 0x00)
			  	{	
			  		cbReader.add(new String(szReaders, offset, i - offset));
			  		offset = i+1;
			  	}
			}
			
			if (cbReader.isEmpty())
			{
				cbReader.add("No PC/SC reader detected");
				return -2;
			}
			
			for (int i = 0; i < cchReaders[0]; i++)
			{				
				if (((String) cbReader.get(i)).lastIndexOf("ACS ACR1281 1S Dual Reader ICC")> -1) {
					ret = i;
					indexACOSReader = i;
					break;
				}else
					cbReader.get(0);	//mirar bien			
			}
			enableConnectACOS = true;
			return ret;
		
		}

		public int ConnectACOS3() {
			
			if(connActive)	
				retCode = jacs.jSCardDisconnect(hCard, ACSModule.SCARD_UNPOWER_CARD);
			
			String rdrcon = (String)cbReader.get(indexACOSReader);  	      	      	
		    
		    retCode = jacs.jSCardConnect(hContext, 
		    							 rdrcon, 
		    							 ACSModule.SCARD_SHARE_SHARED,
		    							 ACSModule.SCARD_PROTOCOL_T1 | ACSModule.SCARD_PROTOCOL_T0,
		      							 hCard, 
		      							 PrefProtocols);		    
		    if (retCode != ACSModule.SCARD_S_SUCCESS)
		    {
		      	//check if ACR1281 SAM is used and use Direct Mode if SAM is not detected
		      	if (((String) cbReader.get(indexACOSReader)).lastIndexOf("SAM")> -1)
				{					
		    		retCode = jacs.jSCardConnect(hContext, 
		    									 rdrcon, 
		    									 ACSModule.SCARD_SHARE_DIRECT,
		    									 0,
		    									 hCard, 
		    									 PrefProtocols);		    		
		    		if (retCode != ACSModule.SCARD_S_SUCCESS)
				    {	
		    			displayOut(1, retCode, "");
		    			connActive = false;
		    			return -1;	
				    }
		    		else
		    		{	
		    			displayOut(0, 0, "Successful connection to " + (String)cbReader.get(indexACOSReader));
		    		}
				}
		      	else
		      	{	
		      		displayOut(1, retCode, "");
	    			connActive = false;
	    			return -1;
		      	}
		    } 
		    else 
		    { 	
		    	displayOut(0, 0, "Successful connection to " + (String)cbReader.get(indexACOSReader));
		    }
			
		    connActive=true;
			maxLen = 8;
			getBinaryData();
			return 0;
			
		
		}
		
		public int SetTerminalKey(String txtTerminalkey) {
			if(txtTerminalkey.length()!=16) {
				System.out.print("Key should have 16 characters!!\n");
				return -1;
			}else {
				this.txtTerminalkey=txtTerminalkey;
				return 0;
			}
				
		}
		
		public int SetCardKey(String txtCardKey) {
			if(txtCardKey.length()!=16) {
				System.out.print("Key should have 16 characters!!\n");
				return -1;
			}else {
				this.txtCardKey=txtCardKey;
				return 0;
			}			
		}
		
		public void Reset() {
			
			//disconnect
			if (connActive)
			{	
				retCode = jacs.jSCardDisconnect(hCard, ACSModule.SCARD_UNPOWER_CARD);
				connActive= false;
			}
		    
			//release context
			retCode = jacs.jSCardReleaseContext(hContext);

			cbReader.clear();
			cbReader.add("Please select reader");
		
		}
		
		public int FormatCard(String tFileID1, String tFileID2, String tFileLen1, String tFileLen2) {
			
			String tmpStr = "", tmpHex = "";
			byte[] tmpArray = new byte[31];
			byte[] buff = new byte[256];
		
			//send IC code
			retCode = submitIC();			
			if (retCode != ACSModule.SCARD_S_SUCCESS)
			{
				displayOut(4, 0, "Insert ACOS3 card on contact card reader.");
				return -1;
			}
			
			//select FF 02
			retCode = selectFile((byte)0xFF, (byte)0x02);			
			if (retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			
			for(int i = 0; i < 2; i++)
			{
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;  
			}
			
			if(!tmpStr.trim().equals("90 00"))
			{
				displayOut(2, 0, "The return string is invalid. Value: " + tmpStr);
				retCode = INVALID_SW1SW2;
				return -1;
			}
			
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
			
			retCode = writeRecord(0, (byte)0x00, (byte)0x04, (byte)0x04, tmpArray);			
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			
			displayOut(0, 0, "File FF 02 is updated.");
			
			//4. perform a reset for changes in ACOS3 to take effect
			connActive = false;
			retCode = jacs.jSCardDisconnect(hCard, ACSModule.SCARD_UNPOWER_CARD);			
			if(retCode != ACSModule.SCARD_S_SUCCESS)
			{
				displayOut(1, retCode, "");
				return -1;
			}
			
			String rdrcon = (String)cbReader.get(indexACOSReader);  	      	      	
		    
		    retCode = jacs.jSCardConnect(hContext, 
		    							 rdrcon, 
		    							 ACSModule.SCARD_SHARE_SHARED,
		    							 ACSModule.SCARD_PROTOCOL_T1 | ACSModule.SCARD_PROTOCOL_T0,
		      							 hCard, 
		      							 PrefProtocols);			
		    if(retCode != ACSModule.SCARD_S_SUCCESS)
			{
				displayOut(1, retCode, "");
				return -1;
			}
		    
		    displayOut(3, 0, "Card reset is successful.");
		    connActive = true;
		    
		    //send IC code
			retCode = submitIC();
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			
			tmpStr = "";			
		    for(int i = 0; i < 2; i++)
		    {
		    	tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
		    	//For single character hex
		    	if (tmpHex.length() == 1) 
		    		tmpHex = "0" + tmpHex;
					
		    	tmpStr += " " + tmpHex;  
		    }
			
			if(!tmpStr.trim().equals("90 00"))
			{
				displayOut(2, 0, "Reset IC Failed.");
				return -1;
			}
			else
			{
				displayOut(2, 0, "Reset IC Success.");
			}
			
			//select FF03
			retCode = selectFile((byte)0xFF, (byte)0x03);
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
				retCode = writeRecord(0, (byte) 0x02,(byte) 0x08, (byte)0x08, buff);
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
				retCode = writeRecord(0, (byte) 0x03,(byte) 0x08, (byte)0x08, buff);
				if(retCode != ACSModule.SCARD_S_SUCCESS)
					return -1;
				else
					displayOut(2, 0, "Write Terminal Key Success.");
			}
			else
			{
				//record 02 of cardkey
				tmpStr = this.txtCardKey;
				for (int i = 0; i < 8; i++)
				{
				    tmpInt = (int)tmpStr.charAt(i);
				    buff[i] = (byte) tmpInt;
				}				
				retCode = writeRecord(0, (byte) 0x02,(byte) 0x08, (byte)0x08, buff);
				if(retCode != ACSModule.SCARD_S_SUCCESS)
					return -1;
				
				//record 12 for cardkey
				tmpStr = this.txtCardKey;
				for (int i = 0; i < 8; i++)
				{
				    tmpInt = (int)tmpStr.charAt(i+8);
				    buff[i] = (byte) tmpInt;
				}				
				retCode = writeRecord(0, (byte) 0x0C,(byte) 0x08, (byte)0x08, buff);
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
				retCode = writeRecord(0, (byte) 0x03,(byte) 0x08, (byte)0x08, buff);
				if(retCode != ACSModule.SCARD_S_SUCCESS)
					return -1;
				
				//record 13 for terminal key
				tmpStr = this.txtTerminalkey;
				for (int i = 0; i < 8; i++)
				{
				    tmpInt = (int)tmpStr.charAt(i+8);
				    buff[i] = (byte) tmpInt;
				}				
				retCode = writeRecord(0, (byte) 0x0D,(byte) 0x08, (byte)0x08, buff);
				if(retCode != ACSModule.SCARD_S_SUCCESS)
					return -1;
				else
					displayOut(2, 0, "Write Terminal Key Success.");
			}
			
			displayOut(0, 0, "FF 03 is updated ");
			
			//select FF 04
		    retCode = selectFile((byte)0xFF, (byte)0x04);
		    if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
		    
			tmpStr = "";			
		    for(int i = 0; i < 2; i++)
		    {
		    	tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
		    	
		    	//For single character hex
		    	if (tmpHex.length() == 1) 
		    		tmpHex = "0" + tmpHex;
					
		    	tmpStr += " " + tmpHex;  
		    }
			
			if(!tmpStr.trim().equals("90 00"))
			{
				displayOut(2, 0, "The return string is invalid. Value: " + tmpStr);
				retCode = INVALID_SW1SW2;
				return -1;
			}
			
			//send IC code
			retCode = submitIC();
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			
			tmpStr = "";
		    for(int i = 0; i < 2; i++)
		    {
		    	tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
		    	//For single character hex
		    	if (tmpHex.length() == 1) 
		    		tmpHex = "0" + tmpHex;
					
		    	tmpStr += " " + tmpHex;  
		    }
			
			if(!tmpStr.trim().equals("90 00"))
			{
				displayOut(2, 0, "Reset IC Failed.");
				return -1;
			}
			else
			{
				displayOut(2, 0, "Reset IC Success.");
			}
			
		    //write to FF 04
		    //7.1. Write to first record of FF 04
			if(tFileLen1.equals(""))
				tmpArray[0] = (byte)0x00;
			else
				tmpArray[0] = (byte)((Integer)Integer.parseInt(tFileLen1, 16)).byteValue();
			
			tmpArray[1] = (byte)((Integer)Integer.parseInt(tFileLen2, 16)).byteValue();
			tmpArray[2] = (byte) 0x00;
			tmpArray[3] = (byte) 0x00;
			tmpArray[4] = (byte)((Integer)Integer.parseInt(tFileID1, 16)).byteValue();
			tmpArray[5] = (byte)((Integer)Integer.parseInt(tFileID2, 16)).byteValue();
			
			if(cbSM)
				tmpArray[6] = (byte)0xE0;
			else
				tmpArray[6] = (byte)0x80;
			
			retCode = writeRecord(0, (byte)0x00, (byte)0x07, (byte)0x07, tmpArray);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			
			tmpStr = "";
			tmpStr = tFileID1 + " " + tFileID2;
			displayOut(0, 0, "User File " + tmpStr + " is defined.");
			return 0;
		
		}
		
		public int ReadFile(String tFID1, String tFID2, String tOffset1, String tOffset2, String tLen) {
			
			this.tFID1=tFID1;
			this.tFID2=tFID2;
			this.tOffset1=tOffset1;
			this.tOffset2=tOffset2;
			this.tLen=tLen;
			
			String tmpStr = "", tmpHex = "";
			byte fileID1, fileID2, hiByte, loByte, tmpLen;
			clearBuffers();
			fileID1 = (byte)((Integer)Integer.parseInt(tFID1, 16)).byteValue();
			fileID2 = (byte)((Integer)Integer.parseInt(tFID2, 16)).byteValue();
			
			if(tOffset1.equals(""))
				hiByte = (byte) 0x00;
			else
				hiByte = ((Integer)Integer.parseInt(tOffset1, 16)).byteValue();
			
			loByte = (byte)((Integer)Integer.parseInt(tOffset2, 16)).byteValue();
			tmpLen = (byte)((Integer)Integer.parseInt(tLen, 16)).byteValue();
			
			//select user file
			retCode = selectFile(fileID1, fileID2);			
			if (retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			
			for(int i = 0; i < 2; i++)
			{
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			
			if(!tmpStr.trim().equals("91 00"))
			{
				displayOut(2, 0, "The return string is invalid. Value: " + tmpStr);
				retCode = INVALID_SW1SW2;
				return -1;
			}
			
			//read binary
			if(cbSM)
			{	
				retCode = readBinarySM(hiByte, loByte, tmpLen);
			}
			else
			{
				tmpStr = "";
				int i = 0;
				retCode = readBinary(hiByte, loByte, tmpLen);
				while((RecvBuff[i] & 0xFF) != 0x00)
				{	
					if(i < maxLen)
						tmpStr = tmpStr + (char)(RecvBuff[i] & 0xFF);					
					i++;
				}
				ReadText=(tmpStr);
			}			
			
			if (retCode != ACSModule.SCARD_S_SUCCESS)
			{
				displayOut(4, 0, "Card may not have been formatted yet.");
				return -1;
			}			
			
			if(!(getCipher().equals("3DES")))
				maxLen = 8;
			else
				maxLen = 16;
			
			return 0;
		}
		public String TextToRead() {
			return ReadText;
		}
		
		public int TextToWrite(String WriteText) {
			if(WriteText.length()>112) {
				System.out.print("El texto debe tener menos de 112 caracteres\n");
				return -1;
			}else {
				this.WriteText=WriteText;
				return 0;
			}
		}
		
		public int WriteFile(String tFID1, String tFID2, String tOffset1, String tOffset2, String tLen) {
			
			String tmpStr = "", tmpHex = "";
			byte hiByte, loByte, fileID1, fileID2, tmpLen;
			byte[] tmpArray = new byte[255];
			
			clearBuffers();
			fileID1 = (byte)((Integer)Integer.parseInt(tFID1, 16)).byteValue();
			fileID2 = (byte)((Integer)Integer.parseInt(tFID2, 16)).byteValue();
			
			if(tOffset1.equals(""))
				hiByte = (byte)0x00;
			else
				hiByte = (byte)((Integer)Integer.parseInt(tOffset1, 16)).byteValue();
			
			loByte = (byte)((Integer)Integer.parseInt(tOffset2, 16)).byteValue();
			tmpLen = (byte)((Integer)Integer.parseInt(tLen, 16)).byteValue();
			
			//select user file
			retCode = selectFile(fileID1, fileID2);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			
			for(int i = 0; i < 2; i++)
			{
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;
			}
			
			if(!tmpStr.trim().equals("91 00"))
			{
				displayOut(2, 0, "The return string is invalid. Value: " + tmpStr);
				retCode = INVALID_SW1SW2;
				return -1;
			}
			
			//write input data to card
			tmpStr = WriteText;
			int tmpInt;			
			for(int i = 0 ; i < tmpStr.length(); i++)
			{
				tmpInt = (int)tmpStr.charAt(i);
				tmpArray[i] = (byte) tmpInt;
			}
			
			if(cbSM)
				retCode = writeBinarySM(1, (byte)hiByte, (byte)loByte, (byte)tmpLen, tmpArray);
			else
				retCode = writeBinary(1, (byte)hiByte, (byte)loByte, (byte)tmpLen, tmpArray);
			
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return -1;
			
			return 0;
		}
		
		public int MutualAuth(String txtCardKey) {

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
		
		public int StartSession()
		{	
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0x84;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x08;		
			SendLen = 5;
			RecvLen[0] = 10;
			
			retCode = sendAPDUandDisplay(2);
			if(retCode != ACSModule.SCARD_S_SUCCESS)			
				return retCode;
			
			//store random number generated by card to CRnd
			for(int i = 0; i < 8; i++)
				CRnd[i] = RecvBuff[i];
			
			for(int i = 0; i < 6; i++)
				SeqNum[i] = (byte)0x00;
				
			SeqNum[6] = RecvBuff[6];
			SeqNum[7] = RecvBuff[7];
			
			return retCode;
		}
		
		public int Authenticate(byte [] DataIn)
		{	
			clearBuffers();
			SendBuff[0] = (byte)0x80;
			SendBuff[1] = (byte)0x82;
			SendBuff[2] = (byte)0x00;
			SendBuff[3] = (byte)0x00;
			SendBuff[4] = (byte)0x10;		
			for(int i = 5; i < 21; i++)
				SendBuff[i] = DataIn[i-5];

			SendLen = 21;
			RecvLen[0] = 8;	

			retCode=sendAPDUandDisplay(2);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return retCode;
			
			return retCode;
		}
		
		public int getResponse()
		{	
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xC0;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x08;
			SendLen = 5;
			RecvLen[0] = 10;
			
			retCode = sendAPDUandDisplay(2);
			if(retCode !=  ACSModule.SCARD_S_SUCCESS)
				return retCode;
			
			return retCode;
		}
		
		public int submitIC()
		{	
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0x20;
			SendBuff[2] = (byte) 0x07;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) 0x41;
			SendBuff[6] = (byte) 0x43;
			SendBuff[7] = (byte) 0x4F;
			SendBuff[8] = (byte) 0x53;
			SendBuff[9] = (byte) 0x54;
			SendBuff[10] = (byte) 0x45;
			SendBuff[11] = (byte) 0x53;
			SendBuff[12] = (byte) 0x54;		
			SendLen = 0x0D;
			RecvLen[0] = 0x02;
			
			retCode = sendAPDUandDisplay(0);
			if (retCode != ACSModule.SCARD_S_SUCCESS)
				return retCode;
			
			String tmpStr = "", tmpHex = "";		
			for(int i = 0; i < 2; i++)
			{
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;  
			}
			
			if(!tmpStr.trim().equals("90 00"))
			{	
				displayOut(0, 0, "Return string is invalid. Value: " + tmpStr);
				retCode = INVALID_SW1SW2;
				return retCode;
			}
			
			return retCode;
		}
		
		public int sendAPDUandDisplay(int sendType)
		{	
			ACSModule.SCARD_IO_REQUEST IO_REQ = new ACSModule.SCARD_IO_REQUEST(); 
			ACSModule.SCARD_IO_REQUEST IO_REQ_Recv = new ACSModule.SCARD_IO_REQUEST(); 
			IO_REQ.dwProtocol = PrefProtocols[0];
			IO_REQ.cbPciLength = 8;
			IO_REQ_Recv.dwProtocol = PrefProtocols[0];
			IO_REQ_Recv.cbPciLength = 8;
			
			String tmpStr = "", tmpHex = "";
			for(int i = 0; i < SendLen; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			displayOut(2, 0, tmpStr);
			
			retCode = jacs.jSCardTransmit(hCard, 
										  IO_REQ, 
										  SendBuff, 
										  SendLen, 
										  null, 
										  RecvBuff, 
										  RecvLen);
					
			if (retCode != ACSModule.SCARD_S_SUCCESS)
			{	
				displayOut(1, retCode, "");
				return retCode;
			}
			else
			{
				tmpStr = "";
				switch(sendType)
				{
					//display SW1/SW2 value
					case 0: 
					{
						for(int i = RecvLen[0]-2; i < RecvLen[0]; i++)
						{
							tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
							
							//For single character hex
							if (tmpHex.length() == 1) 
								tmpHex = "0" + tmpHex;
							
							tmpStr += " " + tmpHex;
						}
						
						if(!tmpStr.trim().equals("90 00"))
							displayOut(4, 0, "Return bytes are not acceptable");
						
						break;
					}
					//Display ATR after checking SW1/SW2
					case 1:
					{	
						for(int i = RecvLen[0]-2; i < RecvLen[0]; i++)
						{	
							tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
							
							//For single character hex
							if (tmpHex.length() == 1) 
								tmpHex = "0" + tmpHex;
							
							tmpStr += " " + tmpHex;
						}
						
						if(!tmpStr.trim().equals("90 00"))
						{
							displayOut(4, 0, "Return bytes are not acceptable");
						}
						else
						{						
							tmpStr = "ATR: ";
							for (int i = 0; i < RecvLen[0]-2; i++)
							{	
								tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
								
								//For single character hex
								if (tmpHex.length() == 1) 
									tmpHex = "0" + tmpHex;
								
								tmpStr += " " + tmpHex;
							}
						}
						
						break;
					}
					//Display all Data
					case 2:
					{
						for(int i = 0; i < RecvLen[0]; i++)
						{	
							tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
							
							//For single character hex
							if (tmpHex.length() == 1) 
								tmpHex = "0" + tmpHex;
							
							tmpStr += " " + tmpHex;
						}
						
						break;
					}	
				}
				
				displayOut(3, 0, tmpStr);
			}
			
			return retCode;
		}
		
		public int selectFile(byte hiAddr, byte loAddr)
		{
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xA4;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x02;
			SendBuff[5] = (byte) hiAddr;
			SendBuff[6] = (byte) loAddr;
			SendLen = 7;
			RecvLen[0] = 2;
			
			retCode = sendAPDUandDisplay(2);
			if (retCode != ACSModule.SCARD_S_SUCCESS)
				return retCode;
			
			return retCode;
		}
		
		public void getBinaryData()
		{
			String tmpStr = "", tmpHex = "";
			int tmpLen;
			
			//1. send IC code
			retCode = submitIC();
			if (retCode != ACSModule.SCARD_S_SUCCESS)
			{
				displayOut(4, 0, "Insert ACOS3 card on contact card reader.");
				return;
			}
			
			//select FF 04
			retCode = selectFile((byte)0xFF, (byte)0x04);
			if (retCode != ACSModule.SCARD_S_SUCCESS)
				return;
			
			for(int i = 0; i < 2; i++)
			{
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;
			}
			
			if(!tmpStr.trim().equals("90 00"))
			{
				displayOut(2, 0, "The return string is invalid. Value: " + tmpStr);
				retCode = INVALID_SW1SW2;
				return;
			}
			
			//read first record
			retCode = readRecord((byte)0x00, (byte)0x07);
			if (retCode != ACSModule.SCARD_S_SUCCESS)
			{
				displayOut(4, 0, "Card may not have been formatted yet.");
				return;
			}
			
			//provide parameter for data input box
			tFID1=(Integer.toHexString(((Byte)RecvBuff[4]).intValue() & 0xFF).toUpperCase());
			tFID2=(Integer.toHexString(((Byte)RecvBuff[5]).intValue() & 0xFF).toUpperCase());
			tmpLen = RecvBuff[1] & 0xFF;
			tmpLen = tmpLen + ((RecvBuff[0] & 0xFF) * 256);
			maxLen = tmpLen;		
		}
		
		public int readRecord(byte recNo, byte dataLen)
		{
			String tmpStr = "", tmpHex = "";
			  
			clearBuffers();
			SendBuff[0] = (byte)0x80;        // CLA
			SendBuff[1] = (byte)0xB2;        // INS
			SendBuff[2] = recNo;             // P1    Record No
			SendBuff[3] = (byte)0x00;        // P2
			SendBuff[4] = dataLen;           // P3    Length of data to be read
			SendLen = 5;
			RecvLen[0] = (SendBuff[4] & 0xFF) + 2;
			
			retCode = sendAPDUandDisplay(0);
			if (retCode != ACSModule.SCARD_S_SUCCESS)
				return retCode;
			  
			for(int i = 0; i < 2; i++)
			{
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i + dataLen]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;
			}

			if (!tmpStr.trim().equals("90 00"))
			{
				displayOut(2, 0, "The return string is invalid. Value: " + tmpStr);
				retCode = INVALID_SW1SW2;
			}	
			 
			return retCode;
		}
		
		public int writeRecord(int caseType, byte recNo, byte maxDataLen, byte dataLen, byte[] dataIn)
		{
			String tmpStr = "", tmpHex = "";
			// If card data is to be erased before writing new data
			if (caseType == 1)
			{	
				clearBuffers();
			    SendBuff[0] = (byte) 0x80;          // CLA
			    SendBuff[1] = (byte) 0xD2;          // INS
			    SendBuff[2] = recNo;        		// P1    Record to be written
			    SendBuff[3] = (byte) 0x00;          // P2
			    SendBuff[4] = maxDataLen;   		// P3    Length
			    for(int i = 0; i < maxDataLen; i++)
			    	SendBuff[i+5] = (byte) 0x00;
			    	
			    SendLen = maxDataLen +5;
			    RecvLen[0] = 2;
			    
			    for(int i = 0; i < SendLen; i++)
			    {
			    	tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
					
			    	//For single character hex
			    	if (tmpHex.length() == 1) 
			    		tmpHex = "0" + tmpHex;
					
			    	tmpStr += " " + tmpHex;
			    }
			    
			    retCode = sendAPDUandDisplay(0);
			    if (retCode != ACSModule.SCARD_S_SUCCESS) 
					 return retCode;
				 
			    tmpStr = "";
			    for(int i = 0; i < 2; i++)
			    {
			    	tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
			    	//For single character hex
			    	if (tmpHex.length() == 1) 
			    		tmpHex = "0" + tmpHex;
						
			    	tmpStr += " " + tmpHex;  
			    }
				 
			    if (tmpStr.indexOf("90 00") < 0)
			    {	
			    	displayOut(0, 0, "Return string is invalid. Value: " + tmpStr);
			    	return INVALID_SW1SW2;
			    }
			}
			
			//write data to card
			clearBuffers();
			SendBuff[0] = (byte) 0x80;          // CLA
			SendBuff[1] = (byte) 0xD2;          // INS
			SendBuff[2] = recNo;        		// P1    Record to be written
			SendBuff[3] = (byte) 0x00;          // P2
			SendBuff[4] = dataLen;   			// P3    Length
			for (int i = 0; i < dataLen; i++)
				SendBuff[i+5] = dataIn[i];
			 
			 SendLen = dataLen+5;
			 RecvLen[0] = 2;
			 
			 tmpStr = "";
			 for(int i = 0; i < SendLen; i++)
			 {
				 tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
					
				 //For single character hex
				 if (tmpHex.length() == 1) 
					 tmpHex = "0" + tmpHex;
					
				 tmpStr += " " + tmpHex;
			 }
			 
			 retCode = sendAPDUandDisplay(0);
			 if (retCode != ACSModule.SCARD_S_SUCCESS)
				 return retCode;
			
			 tmpStr = "";
			 for(int i = 0; i < 2; i++)
			 {
				 tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
				 //For single character hex
				 if (tmpHex.length() == 1) 
					 tmpHex = "0" + tmpHex;
					
				 tmpStr += " " + tmpHex;
			 }
			 
			 if (tmpStr.indexOf("90 00") < 0)
			 {
				  displayOut(0, 0, "Return string is invalid. Value: " + tmpStr);
				  return INVALID_SW1SW2;
			 }
			 
			 return retCode;
		}
		
		public int readBinary(byte hiByte, byte loByte, byte dataLen)
		{	
			clearBuffers();
			SendBuff[0] = (byte)0x80;
			SendBuff[1] = (byte)0xB0;
			SendBuff[2] = hiByte;
			SendBuff[3] = loByte;
			SendBuff[4] = dataLen;
			SendLen = 0x05;
			RecvLen[0] = (dataLen & 0xFF) + 2;
			
			retCode = sendAPDUandDisplay(0);
			if (retCode != ACSModule.SCARD_S_SUCCESS)
				return retCode;
			
			return retCode;
		}
		
		public int readBinarySM(byte hiByte, byte loByte, byte dataLen)
		{
			ACSModule.SCARD_IO_REQUEST IO_REQ = new ACSModule.SCARD_IO_REQUEST(); 
			ACSModule.SCARD_IO_REQUEST IO_REQ_Recv = new ACSModule.SCARD_IO_REQUEST(); 
			IO_REQ.dwProtocol = PrefProtocols[0];
			IO_REQ.cbPciLength = 8;
			IO_REQ_Recv.dwProtocol = PrefProtocols[0];
			IO_REQ_Recv.cbPciLength = 8;
			byte [] buff = new byte[128];
			int lastblk;
			byte L, Pi;		
		
			//build TLV < 89 04 CLA INS P1 P2><97 01 P3>
			buff[0] = (byte) 0x89;
			buff[1] = (byte) 0x04;
			buff[2] = (byte) (0x80 | 0x0C);
			buff[3] = (byte) 0xB0;
			buff[4] =  hiByte;
			buff[5] =  loByte;
			buff[6] = (byte) 0x97;
			buff[7] = (byte) 0x01;
			buff[8] = dataLen;
			
			//increment SeqNum
			if((SeqNum[7] +1)==256)
				SeqNum[6] = (byte)(SeqNum[6] + 1);
			else
				SeqNum[7] = (byte)(SeqNum[7] + 1);
			
			//last block of buff will have the MAC
			lastblk = ENC_CBC(SeqNum, buff, 9, SessionKey);
			
			//increment SeqNum
			if((SeqNum[7] +1)==256)
				SeqNum[6] = (byte)(SeqNum[6] + 1);
			else
				SeqNum[7] = (byte)(SeqNum[7] + 1);
			
			//prepare SM APDU
			clearBuffers();
			SendBuff[0] = (byte)(0x80 | 0x0C);
			SendBuff[1] = (byte)0xB0;
			SendBuff[2] = hiByte;
			SendBuff[3] = loByte;
			SendBuff[4] = (byte)0x09;
			SendBuff[5] = (byte)0x97;
			SendBuff[6] = (byte)0x01;
			SendBuff[7] = dataLen;
			SendBuff[8] = (byte)0x8E;
			SendBuff[9] = (byte)0x04;
			
			int tmp= lastblk;		
			for(int i = 10; i < 14; i++)
			{
				SendBuff[i] = buff[tmp];			
				tmp+=1;
			}
					
			SendLen = 9+5;
			RecvLen[0] = 256;		
			
			String tmpStr = "", tmpHex = "";
			for(int i = 0; i < SendLen; i++)
			{
				tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;
			}
			displayOut(2, 0,  tmpStr);
					
			retCode = jacs.jSCardTransmit(hCard, 
										  IO_REQ, 
										  SendBuff, 
										  SendLen, 
										  null, 
										  RecvBuff, 
										  RecvLen);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
			{
				displayOut(1, retCode, "");
				return retCode;
			}

			tmpStr = "";
			for(int i = 0; i < RecvLen[0]; i++)
			{
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;
			}
			displayOut(3, 0,  tmpStr);
			
			tmpStr = "";		
			tmpHex = Integer.toHexString(((Byte)RecvBuff[0]).intValue() & 0xFF).toUpperCase();
			
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
				
			tmpStr += " " + tmpHex;
			
			if(!tmpStr.trim().equals("61"))
			{
				displayOut(0, 0, ">MAC Incorrect: ");
				return 1;
			}
			
			//get card's SM Response		
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xC0;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x00;
			SendBuff[4] = RecvBuff[1];
			SendLen = 5;
			RecvLen[0] = 256;
			
			retCode = jacs.jSCardTransmit(hCard, 
										  IO_REQ, 
										  SendBuff, 
										  SendLen, 
										  null, 
										  RecvBuff, 
										  RecvLen);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
			{
				displayOut(1, retCode, "");
				return retCode;
			}

			tmpStr = ""; tmpHex = "";
			for(int i = RecvLen[0]-2; i < RecvLen[0]; i++)
			{
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;
			}
			
			if(!tmpStr.trim().equals("90 00"))
			{
				displayOut(0, 0, "Get Response Failed");
				return INVALID_SW1SW2;
			}
			
			tmpStr = ""; tmpHex = "";
			tmpHex = Integer.toHexString(((Byte)RecvBuff[0]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
				
			tmpStr += " " + tmpHex;  

			if(!tmpStr.trim().equals("87"))
			{
				displayOut(0, 0, "Get Response Failed");
				return INVALID_SW1SW2;	
			}
			
			tmpStr = "";
			tmpHex =Integer.toHexString(((Byte)RecvBuff[RecvBuff[1] + 2]).intValue() & 0xFF).toUpperCase();;
			
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr = tmpHex;	
			
			tmpHex =Integer.toHexString(((Byte)RecvBuff[RecvBuff[1] + 2 +1]).intValue() & 0xFF).toUpperCase();;
			
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr = tmpStr + " " +tmpHex;
			
			if(!tmpStr.trim().equals("99 02"))
			{
				displayOut(0, 0, "Get Response Failed");
				return INVALID_SW1SW2;
			}
				
			tmpStr = "";
			tmpHex =Integer.toHexString(((Byte)RecvBuff[RecvBuff[1] + 2 + 2 + 2]).intValue() & 0xFF).toUpperCase();;
			
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr = tmpHex;
			
			tmpHex =Integer.toHexString(((Byte)RecvBuff[RecvBuff[1] + 2 + 2 + 2 + 1]).intValue() & 0xFF).toUpperCase();;
			
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr = tmpStr + " " +tmpHex;
			
			if(!tmpStr.trim().equals("8E 04"))
			{
				displayOut(0, 0, "Get Response Failed");
				return INVALID_SW1SW2;
			}
			
			//get ENC_Data and decrypt it
			L = RecvBuff[1];
			Pi = RecvBuff[2];
			
			for(int i=0; i<L; i++)
				buff[i] = RecvBuff[3+i];
			
			DEC_CBC(SeqNum, buff, L-1, SessionKey);
			
			tmpStr = "";
			int i=0;
			int len = (Integer)Integer.parseInt(tLen, 16);
			
			while(i !=  len)
			{	
				if(i < len)
					tmpStr = tmpStr + (char)(buff[i] & 0xFF);
				
				i++;
			}

			ReadText=(tmpStr);
			
			SW1 = RecvBuff[RecvBuff[1] + 2 + 1 + 1];
			SW2 = RecvBuff[RecvBuff[1] + 2 + 1 + 1 + 1];
			
			//get MAC, MAC length =4
			buff[0] = (byte) 0x89;
			buff[1] = (byte) 0x04;
			buff[2] = (byte) 0x8C;
			buff[3] = (byte) 0xB0;
			buff[4] = (byte) hiByte;
			buff[5] = (byte) loByte;
			buff[6] = (byte) 0x87;
			buff[7] = (byte) L;
			buff[8] = (byte) Pi;
			
			i = 9;
			for(int j = 3; j < L+3; j++)
			{	
				buff[i] = RecvBuff[j];
				i=i+1;
			}
			
			buff[9+L-1] = (byte)0x99;
			buff[9+L-1 + 1] = (byte)0x02;
			buff[9+L-1 + 2] = (byte)SW1;
			buff[9+L-1 + 3] = (byte)SW2;
			
			lastblk = ENC_CBC(SeqNum, buff, L + 6 + 2 + 4, SessionKey);
			
			for(int j = lastblk; j < lastblk+4; j++)
			{			
				if(buff[j] != RecvBuff[j+1])
				{
					displayOut(0, 0, "MAC is Incorrect");
					return lastblk;
				}
				else
				{
					displayOut(0, 0, "MAC is Correct");
					displayOut(0, 0, "Secure Messaging Success");
					return 0;
				}
			}
			
			return 0;
		}
		
		public int writeBinarySM(int caseType, byte hiByte, byte loByte, byte dataLen, byte[] dataIn)
		{
			ACSModule.SCARD_IO_REQUEST IO_REQ = new ACSModule.SCARD_IO_REQUEST(); 
			ACSModule.SCARD_IO_REQUEST IO_REQ_Recv = new ACSModule.SCARD_IO_REQUEST(); 
			IO_REQ.dwProtocol = PrefProtocols[0];
			IO_REQ.cbPciLength = 8;
			IO_REQ_Recv.dwProtocol = PrefProtocols[0];
			IO_REQ_Recv.cbPciLength = 8;
			byte [] ENCData = new byte[128];
			byte [] buff = new byte[128];
			int lastblk, pi;
			
			if(caseType == 1)
			{	
				//prepare SeqNum
				if((SeqNum[7] + 1) == 256)
					SeqNum[6] = (byte)(SeqNum[6] + 1);
				else
					SeqNum[7] = (byte)(SeqNum[7] + 1);
				
				pi = ((((int)dataLen % 8) - 8) % 8) * -1;
				
				ENC_CBC(SeqNum, dataIn, (int)dataLen, SessionKey);
							
				ENCData = new byte[dataLen + pi];
				
				for(int i = 0; i < dataLen + pi; i++)
					ENCData[i] = dataIn[i];					

				//Build TLV
				buff[0] = (byte) 0x89;
				buff[1] = (byte) 0x04;
				buff[2] = (byte) (0x80 | 0x0C);
				buff[3] = (byte) 0xD0;
				buff[4] = hiByte;
				buff[5] = loByte;
				buff[6] = (byte) 0x87;
				buff[7] = (byte) (dataLen + pi + 1);
				buff[8] = (byte) pi;				
				for(int i = 0; i < dataLen + pi; i++)
					buff[i+9] = ENCData[i];
				
				lastblk = ENC_CBC(SeqNum, buff, 9 + dataLen + pi, SessionKey);
				
				//prepare SM APDU
				clearBuffers();
				SendBuff[0] = (byte)(0x80 | 0x0C);
				SendBuff[1] = (byte)0xD0;
				SendBuff[2] = hiByte;
				SendBuff[3] = loByte;
				SendBuff[4] = (byte)(9 + dataLen + pi);
				SendBuff[5] = (byte)0x87;
				SendBuff[6] = (byte)(dataLen + pi + 1);
				SendBuff[7] = (byte)pi;			
				for(int i = 0; i < dataLen + pi; i++)
					SendBuff[i+8] = ENCData[i];
				
				SendBuff[8 + dataLen + pi] = (byte)0x8E;
				SendBuff[8 + dataLen + pi + 1] = (byte)0x04;
				
				int j = 8 + dataLen + pi + 2;
				for(int i = lastblk; i < lastblk + 4; i++ )
				{
					SendBuff[j] = buff[i];
					j++;
				}
				
				SendLen = 5 + SendBuff[4];
				RecvLen[0] = 256;
				
				retCode = sendAPDUandDisplay(2);
				if(retCode != ACSModule.SCARD_S_SUCCESS)
					return retCode;
				
				String tmpStr = "", tmpHex = "";			
				tmpHex = Integer.toHexString(((Byte)RecvBuff[0]).intValue() & 0xFF).toUpperCase();
					
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;  
					
				if(!tmpStr.trim().equals("61"))
				{
					displayOut(0, 0, "Error RecvBuff");
					return INVALID_SW1SW2;
				}
				
				//get card's SM response			
				SendBuff[0] = (byte)0x80;
				SendBuff[1] = (byte)0xC0;
				SendBuff[2] = (byte)0x00;
				SendBuff[3] = (byte)0x00;
				SendBuff[4] = (byte)0x00;
				SendBuff[4] = RecvBuff[1];
				SendLen = 5;
				RecvLen[0] = 256;
				
				retCode = jacs.jSCardTransmit(hCard, 
											  IO_REQ, 
											  SendBuff, 
											  SendLen, 
											  null, 
											  RecvBuff, 
											  RecvLen);			
				if(retCode != ACSModule.SCARD_S_SUCCESS)
				{
					displayOut(1, retCode, "");
					return retCode;
				}
				
				tmpStr = "";
				for(int i = RecvLen[0] -2; i < RecvLen[0]; i++)
				{
					tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
					//For single character hex
					if (tmpHex.length() == 1) 
						tmpHex = "0" + tmpHex;
						
					tmpStr += " " + tmpHex;
				}
							
				if(!tmpStr.trim().equals("90 00"))
				{
					displayOut(0, 0, "Error SW1SW2: "+ tmpStr);
					return INVALID_SW1SW2;
				}
				
				//increment SeqNum
				if((SeqNum[7]+1)==256)
					SeqNum[6] = (byte)(SeqNum[6] +1);
				else
					SeqNum[7] = (byte)(SeqNum[7] +1);
				
				tmpStr = "";
				tmpHex = Integer.toHexString(((Byte)RecvBuff[0]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;  
				
				if(!tmpStr.trim().equals("99"))
				{
					displayOut(0, 0, "Error in Write Binary (Secure Message)");
					return INVALID_SW1SW2;
				}
				
				tmpStr = "";
				tmpHex = Integer.toHexString(((Byte)RecvBuff[1]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;  
				
				if(!tmpStr.trim().equals("02"))
				{
					displayOut(0, 0, "Error in Write Binary (Secure Message)");
					return INVALID_SW1SW2;
				}
				
				tmpStr = "";
				tmpHex = Integer.toHexString(((Byte)RecvBuff[4]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;  
				
				if(!tmpStr.trim().equals("8E"))
				{
					displayOut(0, 0, "Error in Write Binary (Secure Message)");
					return INVALID_SW1SW2;
				}
				
				tmpStr = "";
				tmpHex = Integer.toHexString(((Byte)RecvBuff[5]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;  
				
				if(!tmpStr.trim().equals("04"))
				{
					displayOut(0, 0, "Error in Write Binary (Secure Message)");
					return INVALID_SW1SW2;
				}
				
				SW1 = RecvBuff[2];
				SW2 = RecvBuff[3];
				
				buff[0] = (byte) 0x89;
				buff[1] = (byte) 0x04;
				buff[2] = (byte) 0x8C;
				buff[3] = (byte) 0xD2;
				buff[4] = (byte) 0x00;
				buff[5] = (byte) 0x00;
				buff[6] = (byte) 0x99;
				buff[7] = (byte) 0x02;
				buff[8] = (byte) SW1;
				buff[9] = (byte) SW2;
				
				lastblk = ENC_CBC(SeqNum, buff, 10, SessionKey);
				
				tmpHex = ""; tmpStr = "";
				for(int i = 0; i < RecvLen[0]; i++)
				{
					tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
						
					//For single character hex
					if (tmpHex.length() == 1) 
						tmpHex = "0" + tmpHex;
						
					tmpStr += " " + tmpHex;
				}
				
				displayOut(3, 0, tmpStr);
				displayOut(0, 0, "Secure Messaging Success.");			
			}
			
			return 0;
		}
		
		public int writeBinary(int caseType, byte hiByte, byte loByte, byte dataLen, byte[] dataIn)
		{
			String tmpStr = "", tmpHex = "";		
			//If card data is to be erased before writing new data
			if(caseType == 1)
			{
				//reinitialize card value to 0x00
				clearBuffers();
				SendBuff[0] = (byte)0x80;
				SendBuff[1] = (byte)0xD0;
				SendBuff[2] = hiByte;
				SendBuff[3] = loByte;
				SendBuff[4] = dataLen;			
				for(int i = 0; i < (dataLen & 0xFF); i++)
					SendBuff[i+5] = (byte)0x00;
				
				SendLen = (dataLen & 0xFF)+5;
				RecvLen[0] = 0x02;
				
				retCode = sendAPDUandDisplay(2);
				if(retCode != ACSModule.SCARD_S_SUCCESS)
					return retCode;
				
				for(int i = 0; i < 2; i++)
				{
					tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
					//For single character hex
					if (tmpHex.length() == 1) 
						tmpHex = "0" + tmpHex;
						
					tmpStr += " " + tmpHex;
				}
				
				if(!tmpStr.trim().equals("90 00"))
				{	
					displayOut(3, 0, "The return string is invalid. Value: " + tmpStr);
					retCode = INVALID_SW1SW2;
					return retCode;
				}
			}
			
			//write data to card
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD0;
			SendBuff[2] = hiByte;
			SendBuff[3] = loByte;
			SendBuff[4] = dataLen;		
			for(int i = 0 ; i < (dataLen & 0xFF); i++)
				SendBuff[i + 5] = dataIn[i];
			
			SendLen = (dataLen & 0xFF) + 5;
			RecvLen[0] = 0x02;
			
			retCode = sendAPDUandDisplay(0);
			if(retCode != ACSModule.SCARD_S_SUCCESS)
				return retCode;
			
			tmpStr = "";
			for(int i = 0; i < 2; i++)
			{
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
					
				tmpStr += " " + tmpHex;
			}
			
			if(!tmpStr.trim().equals("90 00"))
			{
				displayOut(3, 0, "The return string is invalid. Value: " + tmpStr);
				retCode = INVALID_SW1SW2;
				return retCode;
			}	
			
			return retCode;	
		}
		
		public int ENC_CBC(byte [] SeqNumber, byte [] buff, int length, byte [] key)
		{
			int blocklen;		
			byte [] xorBlk = new byte[8];
			byte [] lstBlk = new byte[8];
			
			if((length % 8) != 0)
			{	
				buff[length] = (byte)0x80;
				length = length +1;
				
				while((length % 8) != 0)
				{	
					buff[length] = (byte) 0x00;
					length = length + 1;
				}	
			}
			
			for(int i = 0; i < key.length; i++)
				CipherKey[i] = key[i];
			
			blocklen = length /8;
			
			//initial vector
			for(int i = 0; i < 8; i++)
				xorBlk[i] = SeqNum[i];
						
			for(int i = 0; i < blocklen; i++)
			{
				//Get the block to be processed. This is the 8 bytes of data.
				for (int j = 0; j < 8; j++)
					lstBlk[j] = buff[j + (8 * i)];
				
				//Xor the current block with the xor block.
	            for (int j = 0; j < 8; j++)
	            {
	                lstBlk[j] = (byte)(lstBlk[j] ^ xorBlk[j]);
	            }
				
				if(!(getCipher().equals("3DES")))
					DES(lstBlk, CipherKey);
				else
					TripleDES(lstBlk, CipherKey);
				
				for(int j = 0; j < 8; j++)
				{	
	                //Make the latest encrypted block as the next xor block.
	                xorBlk[j] = lstBlk[j];

	                //Copy the encrypted data to the output
	                buff[j + (8 * i)] = lstBlk[j];				
				}
			}
					
			return (length - 8);
		}
		
		public int DEC_CBC(byte [] SeqNumber, byte [] buff, int length, byte [] key)
		{
			byte [] xorBlk = new byte[128];
			byte [] lastBlk = new byte[128];
			byte [] temp = new byte[128];
			int blockLen;
			
			if((length%8) != 0)
				return -1;
				
			for(int i = 0; i < 8; i++)
				xorBlk[i] = SeqNum[i];
			
			blockLen = length /8;		
			
			for(int i = 0; i < blockLen; i++)
			{	
				for(int x = 0; x < 8; x++)
					lastBlk[x] = buff[x + (8*i)];
				
				if(!(getCipher().equals("3DES")))
					DES2(lastBlk, SessionKey);
				else
					TripleDES2(lastBlk, SessionKey);
				
				for(int x = 0; x < 8; x++)
					temp[x] = buff[x + (8*i)];
				
				for(int x = 0; x < 8; x++)
					buff[x + (8*i)] = (byte)(lastBlk[x] ^ xorBlk[x]);
				
				for(int x = 0; x < 8; x++)
					xorBlk[x] = temp[x];
				
			}
			
			for(int i = 0; i < 8; i++)
				xorBlk[i] = lastBlk[i];
			
			return (length -8);
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
		
		
		public void clearBuffers()
		{	
			for(int i=0; i<262; i++)
			{	
				SendBuff[i] = (byte) 0x00;
				RecvBuff[i] = (byte) 0x00;
			}
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
		
}
