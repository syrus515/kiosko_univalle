

import java.util.ArrayList;
import java.util.List;

public class SAMUsage {
	
	//JPCSC Variables
		int retCode;
		boolean connActive, connActiveSAM; 
		public static final int INVALID_SW1SW2 = -450;
		static String VALIDCHARS = "ABCDEFabcdef0123456789";
		static String SN="", RNDc="", RNDt="", Rsp="", EncPIN="", DecPIN="", Rsp2="";
		
		//All variables that requires pass-by-reference calls to functions are
		//declared as 'Array of int' with length 1
		//Java does not process pass-by-ref to int-type variables, thus Array of int was used.
		int [] ATRLen = new int[1]; 
		int [] hContext = new int[1]; 
		int [] cchReaders = new int[1];
		int [] hCard = new int[1];
		int [] hCardSAM = new int[1];
		int [] PrefProtocols = new int[1]; 		
		int [] RecvLen = new int[1];
		int SendLen = 0;
		byte [] SendBuff = new byte[300];
		byte [] RecvBuff = new byte[300];
		byte [] ATRVal = new byte[128];
		byte [] szReaders = new byte[1024];
		
		static JacspcscLoader jacs = new JacspcscLoader();
		
		 List<String> cbSAM = new ArrayList<String>();
		 List<String> cbSLT = new ArrayList<String>();
		 
		 int ret;
		 int indexSAMReader;
		 int indexACOSReader;
		 boolean enableConnectSAM = false;
		 boolean enableConnectACOS = false;
		 String cipherAlgorithm="3DES";
		
		
		public int ListReaders() {
			//Initialize List of Available Readers
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
			cbSAM.clear();
			cbSLT.clear();
			
			for (int i = 0; i < cchReaders[0]-1; i++)
			{	
			  	if (szReaders[i] == 0x00)
			  	{	
			  		cbSAM.add(new String(szReaders, offset, i - offset));
			  		cbSLT.add(new String(szReaders, offset, i - offset));
			  		offset = i+1;
			  	}
			}
			
			if ((cbSAM.isEmpty()) || (cbSLT.isEmpty()))
			{
				cbSAM.add("No PC/SC reader detected");
				cbSLT.add("No PC/SC reader detected");
				return -2;
			}
			
			for (int i = 0; i < cchReaders[0]; i++)
			{				
				//cbSAM.setSelectedIndex(i);

				if (((String) cbSAM.get(i)).lastIndexOf("ACS ACR1281 1S Dual Reader SAM")> -1) {
					ret = i;
					indexSAMReader = i;
					break;
				}else
					cbSAM.get(0);		//Mirar bien		
			}
			
			for (int i = 0; i < cchReaders[0]; i++)
			{				

				if (((String) cbSLT.get(i)).lastIndexOf("ACS ACR1281 1S Dual Reader ICC")> -1) {
					ret = i;
					indexACOSReader = i;
					break;
				}else
					cbSLT.get(0);	//mirar bien			
			}
			enableConnectSAM = true;
			enableConnectACOS = true;
			return ret;
 // List Readers
		}
		
	public int ConnectSAM() {
			
	
		//Establish reader connection
		//get reader name
		if(connActiveSAM)	
			retCode = jacs.jSCardDisconnect(hCard, ACSModule.SCARD_UNPOWER_CARD);
		
		String rdrcon = (String)cbSLT.get(indexACOSReader);  	      	      	
	    
	    retCode = jacs.jSCardConnect(hContext, 
	    							 rdrcon, 
	    							 ACSModule.SCARD_SHARE_SHARED,
	    							 ACSModule.SCARD_PROTOCOL_T1 | ACSModule.SCARD_PROTOCOL_T0,
	    							 hCard, 
	      							 PrefProtocols);
	    if (retCode != ACSModule.SCARD_S_SUCCESS)
	    {
	  		displayOut(1, retCode, "");
			connActive = false;
			return -1;
	    } 
	    else      	
	    	displayOut(0, 0, "Successful connection to " + (String)cbSLT.get(indexACOSReader));
	    
	    connActive = true;
	    
	    //Establish SAM reader connection
	    if(connActiveSAM)	
			retCode = jacs.jSCardDisconnect(hCardSAM, ACSModule.SCARD_UNPOWER_CARD);
		
		rdrcon = (String)cbSAM.get(indexSAMReader);  	      	      	
	    
	    retCode = jacs.jSCardConnect(hContext, 
	    							 rdrcon, 
	    							 ACSModule.SCARD_SHARE_SHARED,
	    							 ACSModule.SCARD_PROTOCOL_T1 | ACSModule.SCARD_PROTOCOL_T0,
	    							 hCardSAM, 
	      							 PrefProtocols);
	    if (retCode != ACSModule.SCARD_S_SUCCESS)
	    {	
	      	displayOut(1, retCode, "");
			connActiveSAM = false;
			return -1;
	    } 
	    else 
	    	displayOut(0, 0, "Successful connection to " + (String)cbSAM.get(indexSAMReader));
	    
	    connActiveSAM = true;
	    return 0;		    		
	}
	
	public int MutualAuth(String tSAMGPIN) {
		
		ACSModule.SCARD_IO_REQUEST IO_REQ = new ACSModule.SCARD_IO_REQUEST(); 
		ACSModule.SCARD_IO_REQUEST IO_REQ_Recv = new ACSModule.SCARD_IO_REQUEST(); 
		IO_REQ.dwProtocol = PrefProtocols[0];
		IO_REQ.cbPciLength = 8;
		IO_REQ_Recv.dwProtocol = PrefProtocols[0];
		IO_REQ_Recv.cbPciLength = 8;
		
		String tmpHex="", tmpStr="",txtSN="", tmpSN="", txtRNDc="", tmpRNDc="", tmpRNDt="", txtRNDt="", tmpRsp="", txtRsp="";
		
		//Get Card Serial Number
		//select FF00
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0xA4;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x02;
		SendBuff[5] = (byte) 0xFF;
		SendBuff[6] = (byte) 0x00;
		SendLen = 7;
		RecvLen[0] = 2;
				    
	    tmpStr="";
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
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
				displayOut(0, 0, "Select FF 00 Success!");
			else
				displayOut(0, 0, "Select FF 00 Failed!");
			
		}
		
		//read FF 00 to retrieve card serial number
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0xB2;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x08;
		SendLen = 5;
		RecvLen[0] = 10;
		
	    tmpStr="";
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
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 8; i < 10; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
			{	
				displayOut(0, 0, "Retrieve Serial Number Success!");
				
				//retrieve Card Serial Number
				for(int i = 0; i < 8; i++)
				{
					tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
					//For single character hex
					if (tmpHex.length() == 1) 
						tmpHex = "0" + tmpHex;
					
					txtSN = txtSN + tmpHex;
					tmpSN += " " + tmpHex;  
				}
				displayOut(3, 0, "Serial Number: " + tmpSN);
				SN = txtSN;
			}
			else
			{
				displayOut(0, 0, "Retrieve Serial Number Failed!");
			}
			
		}
		
		//Select Issuer DF
		clearBuffers();
		SendBuff[0] = (byte) 0x00;
		SendBuff[1] = (byte) 0xA4;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x02;
		SendBuff[5] = (byte) 0x11;
		SendBuff[6] = (byte) 0x00;
		SendLen = 7;
		RecvLen[0] =2;
		
	    tmpStr="";
	    for(int i = 0; i < SendLen; i++)
		{	
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("61 2D"))
				displayOut(0, 0, "Select Issuer DF Success!");
			else
				displayOut(0, 0, "Select Issuer DF Failed!");
			
		}
		
		//Submit Issuer PIN (SAM Global PIN)
		String tmpSAMGPIN = tSAMGPIN;
		
		clearBuffers();
		SendBuff[0] = (byte) 0x00;
		SendBuff[1] = (byte) 0x20;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x01;
		SendBuff[4] = (byte) 0x08;
		SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(0)) + Character.toString(tmpSAMGPIN.charAt(1)), 16)).byteValue();
		SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(2)) + Character.toString(tmpSAMGPIN.charAt(3)), 16)).byteValue();
		SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(4)) + Character.toString(tmpSAMGPIN.charAt(5)), 16)).byteValue();
		SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(6)) + Character.toString(tmpSAMGPIN.charAt(7)), 16)).byteValue();
		SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(8)) + Character.toString(tmpSAMGPIN.charAt(9)), 16)).byteValue();
		SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(10)) + Character.toString(tmpSAMGPIN.charAt(11)), 16)).byteValue();
		SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(12)) + Character.toString(tmpSAMGPIN.charAt(13)), 16)).byteValue();
		SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(14)) + Character.toString(tmpSAMGPIN.charAt(15)), 16)).byteValue();
		SendLen = 13;
		RecvLen[0] = 2;
		
		tmpStr="";
	    for(int i = 0; i < SendLen; i++)
		{	
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
				displayOut(0, 0, "Submit Issuer PIN Success!");
			else
			{
				displayOut(0, 0, "Submit Issuer PIN Failed!");
				return -1;
			}
		}
		
		//diversify Kc
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0x72;
		SendBuff[2] = (byte) 0x04;
		SendBuff[3] = (byte) 0x82;
		SendBuff[4] = (byte) 0x08;
		SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(0)) + Character.toString(SN.charAt(1)), 16)).byteValue();
		SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(2)) + Character.toString(SN.charAt(3)), 16)).byteValue();
		SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(4)) + Character.toString(SN.charAt(5)), 16)).byteValue();
		SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(6)) + Character.toString(SN.charAt(7)), 16)).byteValue();
		SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(8)) + Character.toString(SN.charAt(9)), 16)).byteValue();
		SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(10)) + Character.toString(SN.charAt(11)), 16)).byteValue();
		SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(12)) + Character.toString(SN.charAt(13)), 16)).byteValue();
		SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(14)) + Character.toString(SN.charAt(15)), 16)).byteValue();
		SendLen = 13;
		RecvLen[0] = 2;
		
		tmpStr="";
	    for(int i = 0; i < SendLen; i++)
		{	
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
				displayOut(0, 0, "Diversify Kc Success!");
			else
			{
				displayOut(0, 0, "Diversify Kc Failed!");
				return -2;
			}
		}
		
		//diversify Kt
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0x72;
		SendBuff[2] = (byte) 0x03;
		SendBuff[3] = (byte) 0x83;
		SendBuff[4] = (byte) 0x08;
		SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(0)) + Character.toString(SN.charAt(1)), 16)).byteValue();
		SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(2)) + Character.toString(SN.charAt(3)), 16)).byteValue();
		SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(4)) + Character.toString(SN.charAt(5)), 16)).byteValue();
		SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(6)) + Character.toString(SN.charAt(7)), 16)).byteValue();
		SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(8)) + Character.toString(SN.charAt(9)), 16)).byteValue();
		SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(10)) + Character.toString(SN.charAt(11)), 16)).byteValue();
		SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(12)) + Character.toString(SN.charAt(13)), 16)).byteValue();
		SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(SN.charAt(14)) + Character.toString(SN.charAt(15)), 16)).byteValue();
		SendLen = 13;
		RecvLen[0] = 2;
		
		tmpStr="";
	    for(int i = 0; i < SendLen; i++)
		{	
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
				displayOut(0, 0, "Diversify Kt Success!");
			else
			{
				displayOut(0, 0, "Diversify Kt Failed!");
				return -2;
			}
		}
		
		//Get Challenge
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0x84;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x08;
		SendLen = 5;
		RecvLen[0] = 10;
		
	    tmpStr="";
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
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 8; i < 10; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;  
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
			{	
				displayOut(0, 0, "Get Challenge Success!");
				
				//retrieve RNDc
				for(int i = 0; i < 8; i++)
				{
					tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
					//For single character hex
					if (tmpHex.length() == 1) 
						tmpHex = "0" + tmpHex;
					
					txtRNDc = txtRNDc + tmpHex;
					tmpRNDc += " " + tmpHex;
				}
				displayOut(3, 0, "RNDc: " + tmpRNDc);					
				RNDc = txtRNDc;
			}
			else
			{
				displayOut(0, 0, "Get Challenge Failed!");
			}
			
		}
		
		//prepare ACOS Authentication
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0x78;
		
		if(!(getCipher().equals("3DES")))
			SendBuff[2] = (byte) 0x01;
		else
			SendBuff[2] = (byte) 0x00;
		
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x08;
		SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDc.charAt(0)) + Character.toString(RNDc.charAt(1)), 16)).byteValue();
		SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDc.charAt(2)) + Character.toString(RNDc.charAt(3)), 16)).byteValue();
		SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDc.charAt(4)) + Character.toString(RNDc.charAt(5)), 16)).byteValue();
		SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDc.charAt(6)) + Character.toString(RNDc.charAt(7)), 16)).byteValue();
		SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDc.charAt(8)) + Character.toString(RNDc.charAt(9)), 16)).byteValue();
		SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDc.charAt(10)) + Character.toString(RNDc.charAt(11)), 16)).byteValue();
		SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDc.charAt(12)) + Character.toString(RNDc.charAt(13)), 16)).byteValue();
		SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDc.charAt(14)) + Character.toString(RNDc.charAt(15)), 16)).byteValue();
		SendLen = 13;
		RecvLen[0] = 2;
		
	    for(int i = 0; i < SendLen; i++)
		{	
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;  
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("61 10"))
				displayOut(0, 0, "Prepare ACOS Authentication Success!");
			else
			{
				displayOut(0, 0, "Prepaer ACOS Authentication Failed!");
				return -1;
			}
		}
		
		//get response to get result + RNDt
		clearBuffers();
		SendBuff[0] = (byte) 0x00;
		SendBuff[1] = (byte) 0xC0;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x10;
		SendLen = 5;
		RecvLen[0] = 0x12;
		
	    tmpStr="";
	    for(int i = 0; i < SendLen; i++)
		{		
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 16; i < 18; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
			{	
				displayOut(0, 0, "Get Response Success!");
				
				//retrieve RNDt
				for(int i = 0; i < 16; i++)
				{
					tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
					//For single character hex
					if (tmpHex.length() == 1) 
						tmpHex = "0" + tmpHex;
					
					txtRNDt = txtRNDt + tmpHex;
					tmpRNDt += " " + tmpHex;  
				}
				displayOut(3, 0, "RNDt: " + tmpRNDt);					
				RNDt = txtRNDt;
			}
			else
			{
				displayOut(0, 0, "Get Response Failed!");
			}
			
		}
		
		//Authenticate
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0x82;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x10;
		SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(0)) + Character.toString(RNDt.charAt(1)), 16)).byteValue();
		SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(2)) + Character.toString(RNDt.charAt(3)), 16)).byteValue();
		SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(4)) + Character.toString(RNDt.charAt(5)), 16)).byteValue();
		SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(6)) + Character.toString(RNDt.charAt(7)), 16)).byteValue();
		SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(8)) + Character.toString(RNDt.charAt(9)), 16)).byteValue();
		SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(10)) + Character.toString(RNDt.charAt(11)), 16)).byteValue();
		SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(12)) + Character.toString(RNDt.charAt(13)), 16)).byteValue();
		SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(14)) + Character.toString(RNDt.charAt(15)), 16)).byteValue();
		SendBuff[13] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(16)) + Character.toString(RNDt.charAt(17)), 16)).byteValue();
		SendBuff[14] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(18)) + Character.toString(RNDt.charAt(19)), 16)).byteValue();
		SendBuff[15] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(20)) + Character.toString(RNDt.charAt(21)), 16)).byteValue();
		SendBuff[16] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(22)) + Character.toString(RNDt.charAt(23)), 16)).byteValue();
		SendBuff[17] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(24)) + Character.toString(RNDt.charAt(25)), 16)).byteValue();
		SendBuff[18] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(26)) + Character.toString(RNDt.charAt(27)), 16)).byteValue();
		SendBuff[19] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(28)) + Character.toString(RNDt.charAt(29)), 16)).byteValue();
		SendBuff[20] = (byte) ((Integer)Integer.parseInt(Character.toString(RNDt.charAt(30)) + Character.toString(RNDt.charAt(31)), 16)).byteValue();
		SendLen = 21;
		RecvLen[0] = 2;
		
		tmpStr="";
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
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}				
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("61 08"))
				displayOut(0, 0, "Authentication Success!");
			else
			{
				displayOut(0, 0, "Authentication Failed!");
				return -1;
			}
		}
		
		//Get Response to get result
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0xC0;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x08;			
		SendLen = 5;
		RecvLen[0] = 10;
		
	    tmpStr="";
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
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 8; i < 10; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}				
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
			{	
				displayOut(0, 0, "Get Response Success!");
				
				//retrieve RNDt
				for(int i = 0; i < 8; i++)
				{
					tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
					//For single character hex
					if (tmpHex.length() == 1) 
						tmpHex = "0" + tmpHex;
					
					txtRsp = txtRsp + tmpHex;
					tmpRsp += " " + tmpHex; 
				}					
				displayOut(3, 0, "Response: " + tmpRsp);					
				Rsp = txtRsp;
			}
			else
			{
				displayOut(0, 0, "Get Response Failed!");
			}
			
		}
		
		//Verify ACOS Authentication
		clearBuffers();
		SendBuff[0]= (byte) 0x80;
		SendBuff[1]= (byte) 0x7A;
		SendBuff[2]= (byte) 0x00;
		SendBuff[3]= (byte) 0x00;
		SendBuff[4]= (byte) 0x08;
		SendBuff[5]= (byte) ((Integer)Integer.parseInt(Character.toString(Rsp.charAt(0)) + Character.toString(Rsp.charAt(1)), 16)).byteValue();
		SendBuff[6]= (byte) ((Integer)Integer.parseInt(Character.toString(Rsp.charAt(2)) + Character.toString(Rsp.charAt(3)), 16)).byteValue();
		SendBuff[7]= (byte) ((Integer)Integer.parseInt(Character.toString(Rsp.charAt(4)) + Character.toString(Rsp.charAt(5)), 16)).byteValue();
		SendBuff[8]= (byte) ((Integer)Integer.parseInt(Character.toString(Rsp.charAt(6)) + Character.toString(Rsp.charAt(7)), 16)).byteValue();
		SendBuff[9]= (byte) ((Integer)Integer.parseInt(Character.toString(Rsp.charAt(8)) + Character.toString(Rsp.charAt(9)), 16)).byteValue();
		SendBuff[10]= (byte) ((Integer)Integer.parseInt(Character.toString(Rsp.charAt(10)) + Character.toString(Rsp.charAt(11)), 16)).byteValue();
		SendBuff[11]= (byte) ((Integer)Integer.parseInt(Character.toString(Rsp.charAt(12)) + Character.toString(Rsp.charAt(13)), 16)).byteValue();
		SendBuff[12]= (byte) ((Integer)Integer.parseInt(Character.toString(Rsp.charAt(14)) + Character.toString(Rsp.charAt(15)), 16)).byteValue();
		SendLen = 13;
		RecvLen[0] = 2;
		
		tmpStr="";
	    for(int i = 0; i < SendLen; i++)
		{	
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);			
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;  
			}				
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
				displayOut(0, 0, "Verify ACOS Authentication Success!");
			else
			{
				displayOut(0, 0, "Verify ACOS Authentication Failed!");
				return -2;
			}
		}
		return 0;
//		bSubmitPIN.setEnabled(true);
//		tACOSCardPIN.setEnabled(true);
//		bInqAcct.setEnabled(true);
//	    bCredit.setEnabled(true);
//	    tCreditAmt.setEnabled(true);		    
	
		
	}
	
	public int SubmitPIN_ACOS3(String tACOSCardPIN) {
		
		ACSModule.SCARD_IO_REQUEST IO_REQ = new ACSModule.SCARD_IO_REQUEST(); 
		ACSModule.SCARD_IO_REQUEST IO_REQ_Recv = new ACSModule.SCARD_IO_REQUEST(); 
		IO_REQ.dwProtocol = PrefProtocols[0];
		IO_REQ.cbPciLength = 8;
		IO_REQ_Recv.dwProtocol = PrefProtocols[0];
		IO_REQ_Recv.cbPciLength = 8;
		
		String tmpHex="", tmpStr="", tmpEncPIN= "", txtEncPIN="";
		
		//Encrypt PIN
		String tmpPIN = tACOSCardPIN;
		
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0x74;
		
		if(!(getCipher().equals("3DES")))
			SendBuff[2] = (byte) 0x01;
		else
			SendBuff[2] = (byte) 0x00;
		
		SendBuff[3] = (byte) 0x01;
		SendBuff[4] = (byte) 0x08;
		SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpPIN.charAt(0)) + Character.toString(tmpPIN.charAt(1)), 16)).byteValue();
		SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpPIN.charAt(2)) + Character.toString(tmpPIN.charAt(3)), 16)).byteValue();
		SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpPIN.charAt(4)) + Character.toString(tmpPIN.charAt(5)), 16)).byteValue();
		SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpPIN.charAt(6)) + Character.toString(tmpPIN.charAt(7)), 16)).byteValue();
		SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpPIN.charAt(8)) + Character.toString(tmpPIN.charAt(9)), 16)).byteValue();
		SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpPIN.charAt(10)) + Character.toString(tmpPIN.charAt(11)), 16)).byteValue();
		SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpPIN.charAt(12)) + Character.toString(tmpPIN.charAt(13)), 16)).byteValue();
		SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpPIN.charAt(14)) + Character.toString(tmpPIN.charAt(15)), 16)).byteValue();			
		SendLen = 13;
		RecvLen[0] = 2;
		
		tmpStr="";
	    for(int i = 0; i < SendLen; i++)
		{	
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}			
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);			
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}				
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("61 08"))
				displayOut(0, 0, "Encrypt PIN Success!");
			else
			{
				displayOut(0, 0, "Encrypt PIN Failed!");
				return -2;
			}
		}
		
		//Get Response to get encrypted PIN
		clearBuffers();
		SendBuff[0] = (byte) 0x00;
		SendBuff[1] = (byte) 0xC0;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x08;			
		SendLen = 5;
		RecvLen[0] = 10;
		
	    tmpStr="";
	    for(int i = 0; i < SendLen; i++)
		{	
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}			
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);			
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 8; i < 10; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}				
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
			{	
				displayOut(0, 0, "Get Response Success!");
				
				//retrieve Encrypted PIN
				for(int i = 0; i < 8; i++)
				{
					tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
					//For single character hex
					if (tmpHex.length() == 1) 
						tmpHex = "0" + tmpHex;
					
					txtEncPIN = txtEncPIN + tmpHex;
					tmpEncPIN += " " + tmpHex;
				}					
				displayOut(3, 0, "Encrypted PIN: " + tmpEncPIN);
				EncPIN = txtEncPIN;
			}
			else
			{
				displayOut(0, 0, "Get Response Failed!");
				return -2;
			}
			
		}
		
		//Submit Encrypted PIN
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0x20;
		SendBuff[2] = (byte) 0x06;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x08;
		SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(EncPIN.charAt(0)) + Character.toString(EncPIN.charAt(1)), 16)).byteValue();
		SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(EncPIN.charAt(2)) + Character.toString(EncPIN.charAt(3)), 16)).byteValue();
		SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(EncPIN.charAt(4)) + Character.toString(EncPIN.charAt(5)), 16)).byteValue();
		SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(EncPIN.charAt(6)) + Character.toString(EncPIN.charAt(7)), 16)).byteValue();
		SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(EncPIN.charAt(8)) + Character.toString(EncPIN.charAt(9)), 16)).byteValue();
		SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(EncPIN.charAt(10)) + Character.toString(EncPIN.charAt(11)), 16)).byteValue();
		SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(EncPIN.charAt(12)) + Character.toString(EncPIN.charAt(13)), 16)).byteValue();
		SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(EncPIN.charAt(14)) + Character.toString(EncPIN.charAt(15)), 16)).byteValue();
		SendLen = 13;
		RecvLen[0] = 2;
		
		tmpStr="";
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
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
				displayOut(0, 0, "Submit Encrypted PIN Success!");
			else
			{
				displayOut(0, 0, "Submit Encrypted PIN Failed!");
				return -2;
			}
		}			
		return 0;
//		bChangePIN.setEnabled(true);
//		tNewPIN.setEnabled(true);	
//		bDebit.setEnabled(true);		    
//	    tDebitAmt.setEnabled(true);
	
		
	}
	
	public int ChangePIN_ACOS3(String tNewPIN) {
		
		ACSModule.SCARD_IO_REQUEST IO_REQ = new ACSModule.SCARD_IO_REQUEST(); 
		ACSModule.SCARD_IO_REQUEST IO_REQ_Recv = new ACSModule.SCARD_IO_REQUEST(); 
		IO_REQ.dwProtocol = PrefProtocols[0];
		IO_REQ.cbPciLength = 8;
		IO_REQ_Recv.dwProtocol = PrefProtocols[0];
		IO_REQ_Recv.cbPciLength = 8;
		
		String tmpHex="", tmpStr="", txtDecPIN="", tmpDecPIN="";
		
		//Decrypt PIN
		String tmpNew = tNewPIN;
		
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0x76;
		
		if(!(getCipher().equals("3DES")))
			SendBuff[2] = (byte) 0x01;
		else
			SendBuff[2] = (byte) 0x00;
		
		SendBuff[3] = (byte) 0x01;
		SendBuff[4] = (byte) 0x08;
		SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpNew.charAt(0)) + Character.toString(tmpNew.charAt(1)), 16)).byteValue();
		SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpNew.charAt(2)) + Character.toString(tmpNew.charAt(3)), 16)).byteValue();
		SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpNew.charAt(4)) + Character.toString(tmpNew.charAt(5)), 16)).byteValue();
		SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpNew.charAt(6)) + Character.toString(tmpNew.charAt(7)), 16)).byteValue();
		SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpNew.charAt(8)) + Character.toString(tmpNew.charAt(9)), 16)).byteValue();
		SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpNew.charAt(10)) + Character.toString(tmpNew.charAt(11)), 16)).byteValue();
		SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpNew.charAt(12)) + Character.toString(tmpNew.charAt(13)), 16)).byteValue();
		SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpNew.charAt(14)) + Character.toString(tmpNew.charAt(15)), 16)).byteValue();
		SendLen = 13;
		RecvLen[0] = 2;
		
		tmpStr="";
	    for(int i = 0; i < SendLen; i++)
		{	
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);			
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("61 08"))
				displayOut(0, 0, "Decrypt PIN Success!");
			else
			{
				displayOut(0, 0, "Decrypt PIN Failed!");
				return -2;
			}
		}
		
		//get response to get decrypted PIN
		clearBuffers();
		SendBuff[0] = (byte) 0x00;
		SendBuff[1] = (byte) 0xC0;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x08;			
		SendLen = 5; 
		RecvLen[0] = 10;
		
	    tmpStr="";
	    for(int i = 0; i < SendLen; i++)
		{	
			tmpHex = Integer.toHexString(((Byte)SendBuff[i]).intValue() & 0xFF).toUpperCase();
			
			//For single character hex
			if (tmpHex.length() == 1) 
				tmpHex = "0" + tmpHex;
			
			tmpStr += " " + tmpHex;
		}			
		displayOut(2, 0, tmpStr);
					
		retCode = jacs.jSCardTransmit(hCardSAM, 
									  IO_REQ, 
									  SendBuff, 
									  SendLen, 
									  null, 
									  RecvBuff, 
									  RecvLen);			
		if (retCode != ACSModule.SCARD_S_SUCCESS)
		{
			displayOut(1, retCode, "");
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 8; i < 10; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}				
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
			{	
				displayOut(0, 0, "Get Response Success!");
				
				//retrieve Decrypted PIN
				for(int i = 0; i < 8; i++)
				{
					tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
					
					//For single character hex
					if (tmpHex.length() == 1) 
						tmpHex = "0" + tmpHex;
					
					txtDecPIN = txtDecPIN + tmpHex;
					tmpDecPIN += " " + tmpHex;
				}					
				displayOut(3, 0, "Decrypted PIN: " + tmpDecPIN);
				DecPIN = txtDecPIN;
			}
			else
			{
				displayOut(0, 0, "Get Response Failed!");
			}
			
		}
		
		//change PIN
		clearBuffers();
		SendBuff[0] = (byte) 0x80;
		SendBuff[1] = (byte) 0x24;
		SendBuff[2] = (byte) 0x00;
		SendBuff[3] = (byte) 0x00;
		SendBuff[4] = (byte) 0x08;
		SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(DecPIN.charAt(0)) + Character.toString(DecPIN.charAt(1)), 16)).byteValue();
		SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(DecPIN.charAt(2)) + Character.toString(DecPIN.charAt(3)), 16)).byteValue();
		SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(DecPIN.charAt(4)) + Character.toString(DecPIN.charAt(5)), 16)).byteValue();
		SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(DecPIN.charAt(6)) + Character.toString(DecPIN.charAt(7)), 16)).byteValue();
		SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(DecPIN.charAt(8)) + Character.toString(DecPIN.charAt(9)), 16)).byteValue();
		SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(DecPIN.charAt(10)) + Character.toString(DecPIN.charAt(11)), 16)).byteValue();
		SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(DecPIN.charAt(12)) + Character.toString(DecPIN.charAt(13)), 16)).byteValue();
		SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(DecPIN.charAt(14)) + Character.toString(DecPIN.charAt(15)), 16)).byteValue();
		SendLen = 13;
		RecvLen[0] = 2;
		
		tmpStr="";
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
			return -1;
		}
		else
		{
			tmpStr="";
			for(int i = 0; i < 2; i++)
			{	
				tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
				
				//For single character hex
				if (tmpHex.length() == 1) 
					tmpHex = "0" + tmpHex;
				
				tmpStr += " " + tmpHex;
			}				
			displayOut(3, 0, tmpStr);
			
			if(tmpStr.trim().equals("90 00"))
				displayOut(0, 0, "Change PIN Success!");
			else
			{
				displayOut(0, 0, "Change PIN Failed!");
				return -2;
			}
			return 0;
		}
	
		
	}
	

public void SelectCypher(String cipherAlgorithm) {
	this.cipherAlgorithm=cipherAlgorithm;
}

public String getCipher() {
	return this.cipherAlgorithm;
}

public void ResetReader ()
{	
	jacs.jSCardDisconnect(hCard, ACSModule.SCARD_UNPOWER_CARD);
	jacs.jSCardDisconnect(hCardSAM, ACSModule.SCARD_UNPOWER_CARD);
	jacs.jSCardReleaseContext(hContext);
	
}  // Reset

public void clearBuffers()
{	
	for(int i = 0; i < 300; i++)
	{	
		SendBuff[i] = 0x00;
		RecvBuff[i] = 0x00;
	}
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
