

import java.util.ArrayList;
import java.util.List;

public class SAMKeyManagement {

	//JPCSC Variables
		int retCode;
		boolean connActive, connActiveSAM; 
		public static final int INVALID_SW1SW2 = -450;
		static String VALIDCHARS = "ABCDEFabcdef0123456789";
		
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
		 
		 String cipherAlgorithm="3DES";
		 int ret;
		 int indexSAMReader;
		 int indexACOSReader;
		 boolean enableConnectSAM = false;
		 boolean enableConnectACOS = false;
		 String tCardSN;
		 String tIC2;
		 String tKc1;
		 String tKc2;
		 String tKt1;
		 String tKt2;
		 String tKd1;
		 String tKd2;
		 String tKcr1;
		 String tKcr2;
		 String tKcf1;
		 String tKcf2;
		 String tKrd1;
		 String tKrd2;
		 
		
		public SAMKeyManagement() 
		{

		}
		
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
				System.out.print("Calling SCardEstablishContext...FAILED\n");
		      	displayOut(1, retCode, "");		
		      	return -1;
		    }
		    
			int offset = 0;
			cbSAM.clear();
			
			for (int i = 0; i < cchReaders[0]-1; i++)
			{	
			  	if (szReaders[i] == 0x00)
			  	{	
			  		cbSAM.add(new String(szReaders, offset, i - offset));
			  		offset = i+1;
			  	}
			}
			
			if (cbSAM.isEmpty())
			{
				cbSAM.add("No PC/SC reader detected");
				return -2;
			}
			
			for (int i = 0; i < cchReaders[0]; i++)
			{				

				if (((String) cbSAM.get(i)).lastIndexOf("ACS ACR1281 1S Dual Reader SAM")> -1) {
					ret = i;
					indexSAMReader = i;
					break;
				}
			}
			enableConnectSAM = true;
			return ret;			
		}
		
		public int ConnectSAM() {
			
			if(connActiveSAM)	
				retCode = jacs.jSCardDisconnect(hCardSAM, ACSModule.SCARD_UNPOWER_CARD);
			
			String rdrcon = (String)cbSAM.get(ret);  	      	      	
		    
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
		    {
		    	displayOut(0, 0, "Successful connection to " + (String)cbSAM.get(ret));
		    }
		    
		    connActiveSAM = true;
		    return 0;
		    		
		}
		
		public int InitSAM(String tSAMGPIN, String tIC, String tKc, String tKt, 
				String tKd, String tKcr, String tKcf, String tKrd) {
			
			ACSModule.SCARD_IO_REQUEST IO_REQ = new ACSModule.SCARD_IO_REQUEST(); 
			ACSModule.SCARD_IO_REQUEST IO_REQ_Recv = new ACSModule.SCARD_IO_REQUEST(); 
			IO_REQ.dwProtocol = PrefProtocols[0];
			IO_REQ.cbPciLength = 8;
			IO_REQ_Recv.dwProtocol = PrefProtocols[0];
			IO_REQ_Recv.cbPciLength = 8;
			
			String tmpHex="", tmpStr="";
			
			//verify input
			if((tSAMGPIN.equals(""))||(tSAMGPIN.length()<16))
			{
				System.out.print("SAM PIN debe ser igual a 16 bytes\n");
				return -1;
			}			
			if((tIC.equals(""))||(tIC.length()<32))
			{
				System.out.print("Issuer Code debe ser igual a 32 bytes\n");
				return -1;
			}			
			if((tKc.equals(""))||(tKc.length()<32))
			{
				System.out.print("Key card debe ser igual a 32 bytes\n");
				return -1;
			}			
			if((tKt.equals(""))||(tKt.length()<32))
			{
				System.out.print("Key terminal debe ser igual a 32 bytes\n");
				return -1;
			}			
			if((tKd.equals(""))||(tKd.length()<32))
			{
				System.out.print("Key debit debe ser igual a 32 bytes\n");
				return -1;
			}			
			if((tKcr.equals(""))||(tKcr.length()<32))
			{
				System.out.print("Key credit debe ser igual a 32 bytes\n");
				return -1;
			}			
			if((tKcf.equals(""))||(tKcf.length()<32))
			{
				System.out.print("Certify Key debe ser igual a 32 bytes\n");
				return -1;
			}			
			if((tKrd.equals(""))||(tKrd.length()<32))
			{
				System.out.print("Revoke Debit Key debe ser igual a 32 bytes\n");
				return -1;
			}
			
			//clear card's EEPROM
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0x30;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x00;			
			SendLen = 5;
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
				return -2;
			}			
			
			//reset
		    retCode = jacs.jSCardDisconnect(hCardSAM, ACSModule.SCARD_UNPOWER_CARD);		    
		    
		    String rdrcon = (String)cbSAM.get(indexSAMReader);  	      	      	
		    
		    retCode = jacs.jSCardConnect(hContext, 
										 rdrcon, 
										 ACSModule.SCARD_SHARE_SHARED,
										 ACSModule.SCARD_PROTOCOL_T1 | ACSModule.SCARD_PROTOCOL_T0,
										 hCardSAM, 
										 PrefProtocols);		    
		    if(retCode != ACSModule.SCARD_S_SUCCESS)
		    {
		    	displayOut(1, retCode, "");		    
		    	return -2;
		    }
		    displayOut(0, 0, "Reset success.");
				    
		    //Create MF
		    clearBuffers();
		    SendBuff[0] = (byte) 0x00;
		    SendBuff[1] = (byte) 0xE0;
		    SendBuff[2] = (byte) 0x00;
		    SendBuff[3] = (byte) 0x00;
		    SendBuff[4] = (byte) 0x0E;
		    SendBuff[5] = (byte) 0x62;
		    SendBuff[6] = (byte) 0x0C; 
		    SendBuff[7] = (byte) 0x80;
		    SendBuff[8] = (byte) 0x02;
		    SendBuff[9] = (byte) 0x2C;
		    SendBuff[10] = (byte) 0x00;
		    SendBuff[11] = (byte) 0x82;
		    SendBuff[12] = (byte) 0x02;
		    SendBuff[13] = (byte) 0x3F;
		    SendBuff[14] = (byte) 0xFF;
		    SendBuff[15] = (byte) 0x83;
		    SendBuff[16] = (byte) 0x02;
		    SendBuff[17] = (byte) 0x3F;
		    SendBuff[18] = (byte) 0x00;		    
		    SendLen = 19;
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
				return -2;
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
					displayOut(0, 0, "Create MF Success!");
				else
					displayOut(0, 0, "Create MF Failed!");				
			}			
			
			//create EF1 to store PIN's
			//FDB=0C MLR=0A NOR=01 READ=NONE WRITE=IC			
			clearBuffers();
			SendBuff[0] = (byte)0x00;
			SendBuff[1] = (byte)0xE0;
			SendBuff[2] = (byte)0x00;
			SendBuff[3] = (byte)0x00;
			SendBuff[4] = (byte)0x1B;
			SendBuff[5] = (byte)0x62;
			SendBuff[6] = (byte)0x19;
			SendBuff[7] = (byte)0x83;
			SendBuff[8] = (byte)0x02;
			SendBuff[9] = (byte)0xFF;
			SendBuff[10] = (byte)0x0A;
			SendBuff[11] = (byte)0x88;
			SendBuff[12] = (byte)0x01;
			SendBuff[13] = (byte)0x01;
			SendBuff[14] = (byte)0x82;
			SendBuff[15] = (byte)0x06;
			SendBuff[16] = (byte)0x0C;
			SendBuff[17] = (byte)0x00;
			SendBuff[18] = (byte)0x00;
			SendBuff[19] = (byte)0x0A;
			SendBuff[20] = (byte)0x00;
			SendBuff[21] = (byte)0x01;
			SendBuff[22] = (byte)0x8C;
			SendBuff[23] = (byte)0x08;
			SendBuff[24] = (byte)0x7F;
			SendBuff[25] = (byte)0xFF;
			SendBuff[26] = (byte)0xFF;
			SendBuff[27] = (byte)0xFF;
			SendBuff[28] = (byte)0xFF;
			SendBuff[29] = (byte)0x27;
			SendBuff[30] = (byte)0x27;
			SendBuff[31] = (byte)0xFF;
			SendLen = 32;
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
				return -2;
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
					displayOut(0, 0, "Create EF1 Success!");
				else
					displayOut(0, 0, "Create EF1 Failed!");				
			}
			
			//Set Global PIN's
			String tmpSAMGPIN = tSAMGPIN;
			
			clearBuffers();
			SendBuff[0] = (byte) 0x00;
			SendBuff[1] = (byte) 0xDC;
			SendBuff[2] = (byte) 0x01;
			SendBuff[3] = (byte) 0x04;
			SendBuff[4] = (byte) 0x0A;
			SendBuff[5] = (byte) 0x01;
			SendBuff[6] = (byte) 0x88;
			SendBuff[7] = (byte)((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(0)) + Character.toString(tmpSAMGPIN.charAt(1)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(2)) + Character.toString(tmpSAMGPIN.charAt(3)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(4)) + Character.toString(tmpSAMGPIN.charAt(5)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(6)) + Character.toString(tmpSAMGPIN.charAt(7)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(8)) + Character.toString(tmpSAMGPIN.charAt(9)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(10)) + Character.toString(tmpSAMGPIN.charAt(11)), 16)).byteValue();
			SendBuff[13] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(12)) + Character.toString(tmpSAMGPIN.charAt(13)), 16)).byteValue();
			SendBuff[14] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(14)) + Character.toString(tmpSAMGPIN.charAt(15)), 16)).byteValue();
			SendLen = 15;
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
				return -2;
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
					displayOut(0, 0, "Set Global PIN Success!");
				else
					displayOut(0, 0, "Set Global PIN Failed!");				
			}
			
			//Create Next DF DRT01: 1100
			clearBuffers();
			SendBuff[0] = (byte) 0x00;
			SendBuff[1] = (byte) 0xE0;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x2B;
			SendBuff[5] = (byte) 0x62;
			SendBuff[6] = (byte) 0x29;
			SendBuff[7] = (byte) 0x82;
			SendBuff[8] = (byte) 0x01;
			SendBuff[9] = (byte) 0x38;
			SendBuff[10] = (byte) 0x83;
			SendBuff[11] = (byte) 0x02;
			SendBuff[12] = (byte) 0x11;
			SendBuff[13] = (byte) 0x00;
			SendBuff[14] = (byte) 0x8A;
			SendBuff[15] = (byte) 0x01;
			SendBuff[16] = (byte) 0x01;
			SendBuff[17] = (byte) 0x8C;
			SendBuff[18] = (byte) 0x08;
			SendBuff[19] = (byte) 0x7F;
			SendBuff[20] = (byte) 0x03;
			SendBuff[21] = (byte) 0x03;
			SendBuff[22] = (byte) 0x03;
			SendBuff[23] = (byte) 0x03;
			SendBuff[24] = (byte) 0x03;
			SendBuff[25] = (byte) 0x03;
			SendBuff[26] = (byte) 0x03;
			SendBuff[27] = (byte) 0x8D;
			SendBuff[28] = (byte) 0x02;
			SendBuff[29] = (byte) 0x41;
			SendBuff[30] = (byte) 0x03;
			SendBuff[31] = (byte) 0x80;
			SendBuff[32] = (byte) 0x02;
			SendBuff[33] = (byte) 0x03;
			SendBuff[34] = (byte) 0x20;
			SendBuff[35] = (byte) 0xAB;
			SendBuff[36] = (byte) 0x0B;
			SendBuff[37] = (byte) 0x84;
			SendBuff[38] = (byte) 0x01;
			SendBuff[39] = (byte) 0x88;
			SendBuff[40] = (byte) 0xA4;
			SendBuff[41] = (byte) 0x06;
			SendBuff[42] = (byte) 0x83;
			SendBuff[43] = (byte) 0x01;
			SendBuff[44] = (byte) 0x81;
			SendBuff[45] = (byte) 0x95;
			SendBuff[46] = (byte) 0x01;
			SendBuff[47] = (byte) 0xFF;			
			SendLen = 48;
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
				return -2;
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
					displayOut(0, 0, "Create DF Success!");
				else
					displayOut(0, 0, "Create DF Failed!");
			}
			
			//Create Key File EF2 1101
		    //MRL=16 NOR=08
			clearBuffers();
			SendBuff[0] = (byte)0x00;
			SendBuff[1] = (byte)0xE0;
			SendBuff[2] = (byte)0x00;
			SendBuff[3] = (byte)0x00;
			SendBuff[4] = (byte)0x1D;
			SendBuff[5] = (byte)0x62;
			SendBuff[6] = (byte)0x1B;
			SendBuff[7] = (byte)0x82;
			SendBuff[8] = (byte)0x05;
			SendBuff[9] = (byte)0x0C;
			SendBuff[10] = (byte)0x41;
			SendBuff[11] = (byte)0x00;
			SendBuff[12] = (byte)0x16;
			SendBuff[13] = (byte)0x08;
			SendBuff[14] = (byte)0x83;
			SendBuff[15] = (byte)0x02;
			SendBuff[16] = (byte)0x11;
			SendBuff[17] = (byte)0x01;
			SendBuff[18] = (byte)0x88;
			SendBuff[19] = (byte)0x01;
			SendBuff[20] = (byte)0x02;
			SendBuff[21] = (byte)0x8A;
			SendBuff[22] = (byte)0x01;
			SendBuff[23] = (byte)0x01;
			SendBuff[24] = (byte)0x8C;
			SendBuff[25] = (byte)0x08;
			SendBuff[26] = (byte)0x7F;
			SendBuff[27] = (byte)0x03;
			SendBuff[28] = (byte)0x03;
			SendBuff[29] = (byte)0x03;
			SendBuff[30] = (byte)0x03;
			SendBuff[31] = (byte)0x03;
			SendBuff[32] = (byte)0x03;
			SendBuff[33] = (byte)0x03;
			SendLen = 34;
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
				return -2;
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
					displayOut(0, 0, "Create Key File Success!");
				else
					displayOut(0, 0, "Create Key File Failed!");				
			}
			
			//Append Record To EF2, Define 8 Key Records in EF2 - Master Keys
		    //1st Master key, key ID=81, key type=03, int/ext authenticate, usage counter = FF FF
			String tmpIC = tIC;
			
			clearBuffers();
			SendBuff[0] = (byte)0x00;
			SendBuff[1] = (byte)0xE2;
			SendBuff[2] = (byte)0x00;
			SendBuff[3] = (byte)0x00;
			SendBuff[4] = (byte)0x16;
			SendBuff[5] = (byte)0x81; //Key ID
			SendBuff[6] = (byte)0x03;
			SendBuff[7] = (byte)0xFF;
			SendBuff[8] = (byte)0xFF;
			SendBuff[9] = (byte)0x88;
			SendBuff[10] = (byte)0x00;
			SendBuff[11] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(0)) + Character.toString(tmpIC.charAt(1)), 16)).byteValue();
			SendBuff[12] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(2)) + Character.toString(tmpIC.charAt(3)), 16)).byteValue();
			SendBuff[13] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(4)) + Character.toString(tmpIC.charAt(5)), 16)).byteValue();
			SendBuff[14] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(6)) + Character.toString(tmpIC.charAt(7)), 16)).byteValue();
			SendBuff[15] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(8)) + Character.toString(tmpIC.charAt(9)), 16)).byteValue();
			SendBuff[16] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(10)) + Character.toString(tmpIC.charAt(11)), 16)).byteValue();
			SendBuff[17] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(12)) + Character.toString(tmpIC.charAt(13)), 16)).byteValue();
			SendBuff[18] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(14)) + Character.toString(tmpIC.charAt(15)), 16)).byteValue();
			SendBuff[19] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(16)) + Character.toString(tmpIC.charAt(17)), 16)).byteValue();
			SendBuff[20] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(18)) + Character.toString(tmpIC.charAt(19)), 16)).byteValue();
			SendBuff[21] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(20)) + Character.toString(tmpIC.charAt(21)), 16)).byteValue();
			SendBuff[22] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(22)) + Character.toString(tmpIC.charAt(23)), 16)).byteValue();
			SendBuff[23] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(24)) + Character.toString(tmpIC.charAt(25)), 16)).byteValue();
			SendBuff[24] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(26)) + Character.toString(tmpIC.charAt(27)), 16)).byteValue();
			SendBuff[25] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(28)) + Character.toString(tmpIC.charAt(29)), 16)).byteValue();
			SendBuff[26] = (byte)((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(30)) + Character.toString(tmpIC.charAt(31)), 16)).byteValue();			
			SendLen = 27;
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
				return -2;
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
					displayOut(0, 0, "Create 1st Master Key Success!");
				else
					displayOut(0, 0, "Create 1st Master Key Failed!");
			}
			
			//2nd Master key, key ID=82, key type=03, int/ext authenticate, usage counter = FF FF
			String tmpKc = tKc;
			
			clearBuffers();
			SendBuff[0] = (byte)0x00;
			SendBuff[1] = (byte)0xE2;
			SendBuff[2] = (byte)0x00;
			SendBuff[3] = (byte)0x00;
			SendBuff[4] = (byte)0x16;
			SendBuff[5] = (byte)0x82; //Key ID
			SendBuff[6] = (byte)0x03;
			SendBuff[7] = (byte)0xFF;
			SendBuff[8] = (byte)0xFF;
			SendBuff[9] = (byte)0x88;
			SendBuff[10] = (byte)0x00;
			SendBuff[11] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(0)) + Character.toString(tmpKc.charAt(1)), 16)).byteValue();
			SendBuff[12] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(2)) + Character.toString(tmpKc.charAt(3)), 16)).byteValue();
			SendBuff[13] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(4)) + Character.toString(tmpKc.charAt(5)), 16)).byteValue();
			SendBuff[14] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(6)) + Character.toString(tmpKc.charAt(7)), 16)).byteValue();
			SendBuff[15] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(8)) + Character.toString(tmpKc.charAt(9)), 16)).byteValue();
			SendBuff[16] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(10)) + Character.toString(tmpKc.charAt(11)), 16)).byteValue();
			SendBuff[17] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(12)) + Character.toString(tmpKc.charAt(13)), 16)).byteValue();
			SendBuff[18] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(14)) + Character.toString(tmpKc.charAt(15)), 16)).byteValue();
			SendBuff[19] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(16)) + Character.toString(tmpKc.charAt(17)), 16)).byteValue();
			SendBuff[20] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(18)) + Character.toString(tmpKc.charAt(19)), 16)).byteValue();
			SendBuff[21] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(20)) + Character.toString(tmpKc.charAt(21)), 16)).byteValue();
			SendBuff[22] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(22)) + Character.toString(tmpKc.charAt(23)), 16)).byteValue();
			SendBuff[23] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(24)) + Character.toString(tmpKc.charAt(25)), 16)).byteValue();
			SendBuff[24] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(26)) + Character.toString(tmpKc.charAt(27)), 16)).byteValue();
			SendBuff[25] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(28)) + Character.toString(tmpKc.charAt(29)), 16)).byteValue();
			SendBuff[26] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKc.charAt(30)) + Character.toString(tmpKc.charAt(31)), 16)).byteValue();
			SendLen = 27;
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
				return -2;
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
					displayOut(0, 0, "Create 2nd Master Key Success!");
				else
					displayOut(0, 0, "Create 2nd Master Key Failed!");
			}
			
			//3rd Master key, key ID=83, key type=03, int/ext authenticate, usage counter = FF FF
			String tmpKt=tKt;
			
			clearBuffers();
			SendBuff[0] = (byte)0x00;
			SendBuff[1] = (byte)0xE2;
			SendBuff[2] = (byte)0x00;
			SendBuff[3] = (byte)0x00;
			SendBuff[4] = (byte)0x16;
			SendBuff[5] = (byte)0x83; //Key ID
			SendBuff[6] = (byte)0x03;
			SendBuff[7] = (byte)0xFF;
			SendBuff[8] = (byte)0xFF;
			SendBuff[9] = (byte)0x88;
			SendBuff[10] = (byte)0x00;
			SendBuff[11] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(0)) + Character.toString(tmpKt.charAt(1)), 16)).byteValue();
			SendBuff[12] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(2)) + Character.toString(tmpKt.charAt(3)), 16)).byteValue();
			SendBuff[13] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(4)) + Character.toString(tmpKt.charAt(5)), 16)).byteValue();
			SendBuff[14] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(6)) + Character.toString(tmpKt.charAt(7)), 16)).byteValue();
			SendBuff[15] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(8)) + Character.toString(tmpKt.charAt(9)), 16)).byteValue();
			SendBuff[16] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(10)) + Character.toString(tmpKt.charAt(11)), 16)).byteValue();
			SendBuff[17] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(12)) + Character.toString(tmpKt.charAt(13)), 16)).byteValue();
			SendBuff[18] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(14)) + Character.toString(tmpKt.charAt(15)), 16)).byteValue();
			SendBuff[19] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(16)) + Character.toString(tmpKt.charAt(17)), 16)).byteValue();
			SendBuff[20] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(18)) + Character.toString(tmpKt.charAt(19)), 16)).byteValue();
			SendBuff[21] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(20)) + Character.toString(tmpKt.charAt(21)), 16)).byteValue();
			SendBuff[22] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(22)) + Character.toString(tmpKt.charAt(23)), 16)).byteValue();
			SendBuff[23] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(24)) + Character.toString(tmpKt.charAt(25)), 16)).byteValue();
			SendBuff[24] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(26)) + Character.toString(tmpKt.charAt(27)), 16)).byteValue();
			SendBuff[25] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(28)) + Character.toString(tmpKt.charAt(29)), 16)).byteValue();
			SendBuff[26] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKt.charAt(30)) + Character.toString(tmpKt.charAt(31)), 16)).byteValue();
			SendLen = 27;
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
				return -2;
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
					displayOut(0, 0, "Create 3rd Master Key Success!");
				else
					displayOut(0, 0, "Create 3rd Master Key Failed!");				
			}
			
			//4th Master key, key ID=84, key type=03, int/ext authenticate, usage counter = FF FF
			String tmpKd=tKd;
			
			clearBuffers();
			SendBuff[0] = (byte)0x00;
			SendBuff[1] = (byte)0xE2;
			SendBuff[2] = (byte)0x00;
			SendBuff[3] = (byte)0x00;
			SendBuff[4] = (byte)0x16;
			SendBuff[5] = (byte)0x84; //Key ID
			SendBuff[6] = (byte)0x03;
			SendBuff[7] = (byte)0xFF;
			SendBuff[8] = (byte)0xFF;
			SendBuff[9] = (byte)0x88;
			SendBuff[10] = (byte)0x00;
			SendBuff[11] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(0)) + Character.toString(tmpKd.charAt(1)), 16)).byteValue();
			SendBuff[12] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(2)) + Character.toString(tmpKd.charAt(3)), 16)).byteValue();
			SendBuff[13] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(4)) + Character.toString(tmpKd.charAt(5)), 16)).byteValue();
			SendBuff[14] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(6)) + Character.toString(tmpKd.charAt(7)), 16)).byteValue();
			SendBuff[15] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(8)) + Character.toString(tmpKd.charAt(9)), 16)).byteValue();
			SendBuff[16] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(10)) + Character.toString(tmpKd.charAt(11)), 16)).byteValue();
			SendBuff[17] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(12)) + Character.toString(tmpKd.charAt(13)), 16)).byteValue();
			SendBuff[18] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(14)) + Character.toString(tmpKd.charAt(15)), 16)).byteValue();
			SendBuff[19] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(16)) + Character.toString(tmpKd.charAt(17)), 16)).byteValue();
			SendBuff[20] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(18)) + Character.toString(tmpKd.charAt(19)), 16)).byteValue();
			SendBuff[21] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(20)) + Character.toString(tmpKd.charAt(21)), 16)).byteValue();
			SendBuff[22] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(22)) + Character.toString(tmpKd.charAt(23)), 16)).byteValue();
			SendBuff[23] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(24)) + Character.toString(tmpKd.charAt(25)), 16)).byteValue();
			SendBuff[24] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(26)) + Character.toString(tmpKd.charAt(27)), 16)).byteValue();
			SendBuff[25] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(28)) + Character.toString(tmpKd.charAt(29)), 16)).byteValue();
			SendBuff[26] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKd.charAt(30)) + Character.toString(tmpKd.charAt(31)), 16)).byteValue();
			SendLen = 27;
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
				return -2;
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
					displayOut(0, 0, "Create 4th Master Key Success!");
				else
					displayOut(0, 0, "Create 4th Master Key Failed!");				
			}
			
			//5th Master key, key ID=85, key type=03, int/ext authenticate, usage counter = FF FF
			String tmpKcr=tKcr;
			
			clearBuffers();
			SendBuff[0] = (byte)0x00;
			SendBuff[1] = (byte)0xE2;
			SendBuff[2] = (byte)0x00;
			SendBuff[3] = (byte)0x00;
			SendBuff[4] = (byte)0x16;
			SendBuff[5] = (byte)0x85; //Key ID
			SendBuff[6] = (byte)0x03;
			SendBuff[7] = (byte)0xFF;
			SendBuff[8] = (byte)0xFF;
			SendBuff[9] = (byte)0x88;
			SendBuff[10] = (byte)0x00;
			SendBuff[11] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(0)) + Character.toString(tmpKcr.charAt(1)), 16)).byteValue();
			SendBuff[12] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(2)) + Character.toString(tmpKcr.charAt(3)), 16)).byteValue();
			SendBuff[13] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(4)) + Character.toString(tmpKcr.charAt(5)), 16)).byteValue();
			SendBuff[14] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(6)) + Character.toString(tmpKcr.charAt(7)), 16)).byteValue();
			SendBuff[15] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(8)) + Character.toString(tmpKcr.charAt(9)), 16)).byteValue();
			SendBuff[16] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(10)) + Character.toString(tmpKcr.charAt(11)), 16)).byteValue();
			SendBuff[17] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(12)) + Character.toString(tmpKcr.charAt(13)), 16)).byteValue();
			SendBuff[18] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(14)) + Character.toString(tmpKcr.charAt(15)), 16)).byteValue();
			SendBuff[19] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(16)) + Character.toString(tmpKcr.charAt(17)), 16)).byteValue();
			SendBuff[20] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(18)) + Character.toString(tmpKcr.charAt(19)), 16)).byteValue();
			SendBuff[21] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(20)) + Character.toString(tmpKcr.charAt(21)), 16)).byteValue();
			SendBuff[22] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(22)) + Character.toString(tmpKcr.charAt(23)), 16)).byteValue();
			SendBuff[23] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(24)) + Character.toString(tmpKcr.charAt(25)), 16)).byteValue();
			SendBuff[24] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(26)) + Character.toString(tmpKcr.charAt(27)), 16)).byteValue();
			SendBuff[25] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(28)) + Character.toString(tmpKcr.charAt(29)), 16)).byteValue();
			SendBuff[26] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcr.charAt(30)) + Character.toString(tmpKcr.charAt(31)), 16)).byteValue();
			SendLen = 27;
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
				return -2;
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
					displayOut(0, 0, "Create 5th Master Key Success!");
				else
					displayOut(0, 0, "Create 5th Master Key Failed!");				
			}
			
			//6th Master key, key ID=86, key type=03, int/ext authenticate, usage counter = FF FF
			String tmpKcf=tKcf;
			
			clearBuffers();
			SendBuff[0] = (byte)0x00;
			SendBuff[1] = (byte)0xE2;
			SendBuff[2] = (byte)0x00;
			SendBuff[3] = (byte)0x00;
			SendBuff[4] = (byte)0x16;
			SendBuff[5] = (byte)0x86; //Key ID
			SendBuff[6] = (byte)0x03;
			SendBuff[7] = (byte)0xFF;
			SendBuff[8] = (byte)0xFF;
			SendBuff[9] = (byte)0x88;
			SendBuff[10] = (byte)0x00;
			SendBuff[11] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(0)) + Character.toString(tmpKcf.charAt(1)), 16)).byteValue();
			SendBuff[12] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(2)) + Character.toString(tmpKcf.charAt(3)), 16)).byteValue();
			SendBuff[13] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(4)) + Character.toString(tmpKcf.charAt(5)), 16)).byteValue();
			SendBuff[14] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(6)) + Character.toString(tmpKcf.charAt(7)), 16)).byteValue();
			SendBuff[15] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(8)) + Character.toString(tmpKcf.charAt(9)), 16)).byteValue();
			SendBuff[16] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(10)) + Character.toString(tmpKcf.charAt(11)), 16)).byteValue();
			SendBuff[17] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(12)) + Character.toString(tmpKcf.charAt(13)), 16)).byteValue();
			SendBuff[18] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(14)) + Character.toString(tmpKcf.charAt(15)), 16)).byteValue();
			SendBuff[19] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(16)) + Character.toString(tmpKcf.charAt(17)), 16)).byteValue();
			SendBuff[20] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(18)) + Character.toString(tmpKcf.charAt(19)), 16)).byteValue();
			SendBuff[21] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(20)) + Character.toString(tmpKcf.charAt(21)), 16)).byteValue();
			SendBuff[22] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(22)) + Character.toString(tmpKcf.charAt(23)), 16)).byteValue();
			SendBuff[23] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(24)) + Character.toString(tmpKcf.charAt(25)), 16)).byteValue();
			SendBuff[24] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(26)) + Character.toString(tmpKcf.charAt(27)), 16)).byteValue();
			SendBuff[25] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(28)) + Character.toString(tmpKcf.charAt(29)), 16)).byteValue();
			SendBuff[26] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKcf.charAt(30)) + Character.toString(tmpKcf.charAt(31)), 16)).byteValue();
			SendLen = 27;
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
				return -2;
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
					displayOut(0, 0, "Create 6th Master Key Success!");
				else
					displayOut(0, 0, "Create 6th Master Key Failed!");
			}
			
			//7th Master key, key ID=87, key type=03, int/ext authenticate, usage counter = FF FF
			String tmpKrd=tKrd;
			
			clearBuffers();
			SendBuff[0] = (byte)0x00;
			SendBuff[1] = (byte)0xE2;
			SendBuff[2] = (byte)0x00;
			SendBuff[3] = (byte)0x00;
			SendBuff[4] = (byte)0x16;
			SendBuff[5] = (byte)0x87; //Key ID
			SendBuff[6] = (byte)0x03;
			SendBuff[7] = (byte)0xFF;
			SendBuff[8] = (byte)0xFF;
			SendBuff[9] = (byte)0x88;
			SendBuff[10] = (byte)0x00;
			SendBuff[11] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(0)) + Character.toString(tmpKrd.charAt(1)), 16)).byteValue();
			SendBuff[12] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(2)) + Character.toString(tmpKrd.charAt(3)), 16)).byteValue();
			SendBuff[13] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(4)) + Character.toString(tmpKrd.charAt(5)), 16)).byteValue();
			SendBuff[14] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(6)) + Character.toString(tmpKrd.charAt(7)), 16)).byteValue();
			SendBuff[15] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(8)) + Character.toString(tmpKrd.charAt(9)), 16)).byteValue();
			SendBuff[16] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(10)) + Character.toString(tmpKrd.charAt(11)), 16)).byteValue();
			SendBuff[17] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(12)) + Character.toString(tmpKrd.charAt(13)), 16)).byteValue();
			SendBuff[18] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(14)) + Character.toString(tmpKrd.charAt(15)), 16)).byteValue();
			SendBuff[19] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(16)) + Character.toString(tmpKrd.charAt(17)), 16)).byteValue();
			SendBuff[20] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(18)) + Character.toString(tmpKrd.charAt(19)), 16)).byteValue();
			SendBuff[21] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(20)) + Character.toString(tmpKrd.charAt(21)), 16)).byteValue();
			SendBuff[22] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(22)) + Character.toString(tmpKrd.charAt(23)), 16)).byteValue();
			SendBuff[23] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(24)) + Character.toString(tmpKrd.charAt(25)), 16)).byteValue();
			SendBuff[24] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(26)) + Character.toString(tmpKrd.charAt(27)), 16)).byteValue();
			SendBuff[25] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(28)) + Character.toString(tmpKrd.charAt(29)), 16)).byteValue();
			SendBuff[26] = (byte)((Integer)Integer.parseInt(Character.toString(tmpKrd.charAt(30)) + Character.toString(tmpKrd.charAt(31)), 16)).byteValue();			
			SendLen = 27;
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
				return -2;
			}
			else
			{
				tmpStr = "";
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
					displayOut(0, 0, "Create 7th Master Key Success!");
				else
					displayOut(0, 0, "Create 7th Master Key Failed!");				
			}
			return 0;
		}
		
		public int ListACOS3() {
			//initialize list of available readers
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
			cbSLT.clear();;
			
			for (int i = 0; i < cchReaders[0]-1; i++)
			{	
			  	if (szReaders[i] == 0x00)
			  	{	
			  		cbSLT.add(new String(szReaders, offset, i - offset));
			  		offset = i+1;
			  	}
			}
			
			if (cbSLT.isEmpty())
			{
				cbSLT.add("No PC/SC reader detected");
				return -1;
			}
			
			enableConnectACOS=true;
			return 0;
		}
		
		public int ConnectACOS3() {
			if(enableConnectACOS)
			{			
				if(connActive)	
					retCode = jacs.jSCardDisconnect(hCard, ACSModule.SCARD_UNPOWER_CARD);
				
				for (int i = 0; i < cchReaders[0]; i++)
				{				

					if (((String) cbSLT.get(i)).lastIndexOf("ACS ACR1281 1S Dual Reader ICC")> -1) {
						indexACOSReader = i;
						break;
					}
				}
				
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
			    	return -2;
			    } 
			    else 
			    	displayOut(0, 0, "Successful connection to " + (String)cbSLT.get(indexACOSReader));
			    
			    connActive = true;
			    return 0;
			}  // Connect ACOS
			else
				return -1;
		}
		
		
		public int GenerateKeysWithSAM(String tSAMGPIN) {

			ACSModule.SCARD_IO_REQUEST IO_REQ = new ACSModule.SCARD_IO_REQUEST(); 
			ACSModule.SCARD_IO_REQUEST IO_REQ_Recv = new ACSModule.SCARD_IO_REQUEST(); 
			IO_REQ.dwProtocol = PrefProtocols[0];
			IO_REQ.cbPciLength = 8;
			IO_REQ_Recv.dwProtocol = PrefProtocols[0];
			IO_REQ_Recv.cbPciLength = 8;
			
			String tmpStr="", tmpHex="", SN="", txtSN="", GenKey = "", txtGenKey="", tmpSN="";
			
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
				return -2;
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
				{
					displayOut(0, 0, "Select Issuer DF Failed!");
					return -2;
				}
			}			

			String tmpSAMGPIN = tSAMGPIN;
		
			//Submit Issuer PIN
			clearBuffers();			
			SendBuff[0] = (byte) 0x00;
			SendBuff[1] = (byte) 0x20;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x01;
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte)((Integer)Integer.parseInt(Character.toString(tmpSAMGPIN.charAt(0)) + Character.toString(tmpSAMGPIN.charAt(1)), 16)).byteValue();
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
				return -2;
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
					return -2;
				}				
			}
			
			//Get Card Serial Number
		    //Select FF00			
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
				return -2;
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
					displayOut(0, 0, "Select FF00 Success!");
				else
				{
					displayOut(0, 0, "Select FF00 Failed!");
					return -2;
				}				
			}
			
			//Read FF 00 to retrieve card serial number
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
				return -2;
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
					displayOut(0, 0, "Read FF00 Success!");

					//retrieve Card Serial Number
					for(int i = 0; i < 8; i++)
					{
						tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
						//For single character hex
						if (tmpHex.length() == 1) 
							tmpHex = "0" + tmpHex;
						
						txtSN = txtSN + tmpHex;
						SN += " " + tmpHex;  
					}
					displayOut(3, 0, "Serial Number: "+SN);
					
					tCardSN = txtSN;
					tmpSN = txtSN;
				}
				else
				{
					displayOut(0, 0, "Read FF00 Failed!");
					return -2;
				}				
			}
			
			//Generate Key
		    //Generate IC Using 1st SAM Master Key (KeyID=81)			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0x88;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x81; //KeyID
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Generate IC Success!");
				else
				{
					displayOut(0, 0, "Generate IC Failed!");
					return -2;
				}				
			}
			
			//Get Response to Retrieve Generated Key
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
				return -2;
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
					displayOut(0, 0, "Retrieve IC Success!");

					//retrieve Card Serial Number
					for(int i = 0; i < 8; i++)
					{
						tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
						//For single character hex
						if (tmpHex.length() == 1) 
							tmpHex = "0" + tmpHex;
						
						txtGenKey = txtGenKey + tmpHex;
						GenKey += " " + tmpHex;
					}
					displayOut(3, 0, "Generated IC: " + GenKey);
					
					tIC2=txtGenKey;
				}
				else
				{
					displayOut(0, 0, "Retrieve IC Failed!");
					return -2;
				}
			}
			
			//Generate Card Key (Kc) Using 2nd SAM Master Key (KeyID=82)
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0x88;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x82; //KeyID
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Generate Kc Success!");
				else
				{
					displayOut(0, 0, "Generate Kc Failed!");
					return -2;
				}				
			}
			
			//Get Response to Retrieve Generated Key
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
				return -2;
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
					displayOut(0, 0, "Retrieve Kc Success!");
					GenKey = txtGenKey = "";
					
					//retrieve Card Serial Number
					for(int i = 0; i < 8; i++)
					{
						tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
						//For single character hex
						if (tmpHex.length() == 1) 
							tmpHex = "0" + tmpHex;
						
						txtGenKey = txtGenKey + tmpHex;
						GenKey += " " + tmpHex;
					}
					displayOut(3, 0, "Generated Kc: " + GenKey);					
					tKc1=txtGenKey;
				}
				else
				{
					displayOut(0, 0, "Retrieve Kc Failed!");
					return -2;
				}
			}
			
			//If Algorithm Reference = 3DES then Generate Right Half of Card Key (Kc) Using 2nd SAM Master Key (KeyID=82)
			if(getCipher().equals("3DES"))
			{	
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0x88;
				SendBuff[2] = (byte) 0x00;
				SendBuff[3] = (byte) 0x82; //KeyID
				SendBuff[4] = (byte) 0x08;
				
				//compliment the card serial number to generate right half key for 3DES algorithm				
				SendBuff[5] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue() ^ 0xFF);
				SendBuff[6] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue() ^ 0xFF);
				SendBuff[7] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue() ^ 0xFF);
				SendBuff[8] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue() ^ 0xFF);
				SendBuff[9] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue() ^ 0xFF);
				SendBuff[10] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue() ^ 0xFF);
				SendBuff[11] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue() ^ 0xFF);
				SendBuff[12] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue() ^ 0xFF);
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
					return -2;
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
						displayOut(0, 0, "Generate Kc (3DES) Success!");
					else
					{
						displayOut(0, 0, "Generate Kc (3DES) Failed!");
						return -2;
					}
					
				}
				
				//Get Response to Retrieve Generated Data
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
					return -2;
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
						displayOut(0, 0, "Retrieve Kc (3DES) Success!");
						GenKey = txtGenKey = "";
						
						//retrieve Card Serial Number
						for(int i = 0; i < 8; i++)
						{
							tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
							
							//For single character hex
							if (tmpHex.length() == 1) 
								tmpHex = "0" + tmpHex;
							
							txtGenKey = txtGenKey + tmpHex;
							GenKey += " " + tmpHex; 
						}
						displayOut(3, 0, "Generated Kc (right side): " + GenKey);
						tKc2 = txtGenKey;						
					}
					else
					{
						displayOut(0, 0, "Retrieve Kc (3DES) Failed!");
						return -2;
					}
				}
			}
			else
			{
				tKc2 = "";
			}
			
			
			//Generate Terminal Key (Kt) Using 3rd SAM Master Key (KeyID=83)
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0x88;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x83; //KeyID
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Generate Kc Success!");
				else
				{
					displayOut(0, 0, "Generate Kc Failed!");
					return -2;
				}				
			}			
			
			//Get Response to Retrieve Generated Key
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
				return -2;
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
					displayOut(0, 0, "Retrieve Kt Success!");
					GenKey = txtGenKey = "";
					
					//retrieve Card Serial Number
					for(int i = 0; i < 8; i++)
					{
						tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
						//For single character hex
						if (tmpHex.length() == 1) 
							tmpHex = "0" + tmpHex;
						
						txtGenKey = txtGenKey + tmpHex;
						GenKey += " " + tmpHex;
					}
					displayOut(3, 0, "Generated Kt: " + GenKey);
					tKt1=txtGenKey;
				}
				else
				{
					displayOut(0, 0, "Retrieve Kt Failed!");
					return -2;
				}
			}
			
			//If Algorithm Reference = 3DES then Generate Right Half of Terminal Key (Kt) Using 3rd SAM Master Key (KeyID=83)
			if(getCipher().equals("3DES"))
			{	
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0x88;
				SendBuff[2] = (byte) 0x00;
				SendBuff[3] = (byte) 0x83; //KeyID
				SendBuff[4] = (byte) 0x08;
				
				//compliment the card serial number to generate right half key for 3DES algorithm				
				SendBuff[5] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue() ^ 0xFF);
				SendBuff[6] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue() ^ 0xFF);
				SendBuff[7] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue() ^ 0xFF);
				SendBuff[8] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue() ^ 0xFF);
				SendBuff[9] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue() ^ 0xFF);
				SendBuff[10] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue() ^ 0xFF);
				SendBuff[11] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue() ^ 0xFF);
				SendBuff[12] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue() ^ 0xFF);
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
					return -2;
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
						displayOut(0, 0, "Generate Kt (3DES) Success!");
					else
					{
						displayOut(0, 0, "Generate Kt (3DES) Failed!");
						return -2;
					}
				}
				
				//Get Response to Retrieve Generated Data
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
					return -2;
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
						displayOut(0, 0, "Retrieve Kt (3DES) Success!");
						GenKey = txtGenKey = "";
						
						//retrieve Card Serial Number
						for(int i = 0; i < 8; i++)
						{
							tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
							
							//For single character hex
							if (tmpHex.length() == 1) 
								tmpHex = "0" + tmpHex;
							
							txtGenKey = txtGenKey + tmpHex;
							GenKey += " " + tmpHex;  
						}
						displayOut(3, 0, "Generated Kt (right side): " + GenKey);						
						tKt2=txtGenKey;
					}
					else
					{
						displayOut(0, 0, "Retrieve Kt (3DES) Failed!");
						return -2;
					}
				}
			}
			else
			{
				tKt2="";
			}
			
			
			//Generate Debit Key (Kd) Using 4th SAM Master Key (KeyID=84)
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0x88;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x84; //KeyID
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Generate Kc Success!");
				else
				{
					displayOut(0, 0, "Generate Kc Failed!");
					return -2;
				}
			}
			
			//Get Response to Retrieve Generated Key
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
				return -2;
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
					displayOut(0, 0, "Retrieve Kd Success!");
					GenKey = txtGenKey = "";
					
					//retrieve Card Serial Number
					for(int i = 0; i < 8; i++)
					{
						tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
						//For single character hex
						if (tmpHex.length() == 1) 
							tmpHex = "0" + tmpHex;
						
						txtGenKey = txtGenKey + tmpHex;
						GenKey += " " + tmpHex;
					}
					displayOut(3, 0, "Generated Kd: " + GenKey);
					tKd1 =(txtGenKey);
				}
				else
				{
					displayOut(0, 0, "Retrieve Kd Failed!");
					return -2;
				}
			}
			
			//If Algorithm Reference = 3DES then Generate Right Half of Debit Key (Kd) Using 4th SAM Master Key (KeyID=84)
			if(getCipher().equals("3DES"))
			{	
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0x88;
				SendBuff[2] = (byte) 0x00;
				SendBuff[3] = (byte) 0x84; //KeyID
				SendBuff[4] = (byte) 0x08;
				
				//compliment the card serial number to generate right half key for 3DES algorithm				
				SendBuff[5] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue() ^ 0xFF);
				SendBuff[6] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue() ^ 0xFF);
				SendBuff[7] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue() ^ 0xFF);
				SendBuff[8] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue() ^ 0xFF);
				SendBuff[9] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue() ^ 0xFF);
				SendBuff[10] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue() ^ 0xFF);
				SendBuff[11] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue() ^ 0xFF);
				SendBuff[12] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue() ^ 0xFF);
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
					return -2;
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
						displayOut(0, 0, "Generate Kd (3DES) Success!");
					else
					{
						displayOut(0, 0, "Generate Kd (3DES) Failed!");
						return -2;
					}
				}
				
				//Get Response to Retrieve Generated Data
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
					return -2;
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
						displayOut(0, 0, "Retrieve Kd (3DES) Success!");
						GenKey = txtGenKey = "";
						
						//retrieve Card Serial Number
						for(int i = 0; i < 8; i++)
						{
							tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
							
							//For single character hex
							if (tmpHex.length() == 1) 
								tmpHex = "0" + tmpHex;
							
							txtGenKey = txtGenKey + tmpHex;
							GenKey += " " + tmpHex;
						}
						displayOut(3, 0, "Generated Kd (right side): " + GenKey);
						tKd2=(txtGenKey);
					}
					else
					{
						displayOut(0, 0, "Retrieve Kd (3DES) Failed!");
						return -2;
					}
				}
			}
			else
			{
				tKd2=("");
			}
			
			
			//Generate Credit Key (Kcr) Using 5th SAM Master Key (KeyID=85)
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0x88;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x85; //KeyID
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Generate Kcr Success!");
				else
				{
					displayOut(0, 0, "Generate Kcr Failed!");
					return -2;
				}
			}
			
			//Get Response to Retrieve Generated Key
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
				return -2;
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
					displayOut(0, 0, "Retrieve Kcr Success!");
					GenKey = txtGenKey = "";
					
					//retrieve Card Serial Number
					for(int i = 0; i < 8; i++)
					{
						tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
						//For single character hex
						if (tmpHex.length() == 1) 
							tmpHex = "0" + tmpHex;
						
						txtGenKey = txtGenKey + tmpHex;
						GenKey += " " + tmpHex;
					}
					displayOut(3, 0, "Generated Kcr: " + GenKey);
					tKcr1=(txtGenKey);
				}
				else
				{
					displayOut(0, 0, "Retrieve Kcr Failed!");
					return -2;
				}
			}
			
			//If Algorithm Reference = 3DES then Generate Right Half of Credit Key (Kcr) Using 5th SAM Master Key (KeyID=85)
			if(getCipher().equals("3DES"))
			{	
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0x88;
				SendBuff[2] = (byte) 0x00;
				SendBuff[3] = (byte) 0x85; //KeyID
				SendBuff[4] = (byte) 0x08;
				
				//compliment the card serial number to generate right half key for 3DES algorithm
				SendBuff[5] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue() ^ 0xFF);
				SendBuff[6] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue() ^ 0xFF);
				SendBuff[7] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue() ^ 0xFF);
				SendBuff[8] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue() ^ 0xFF);
				SendBuff[9] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue() ^ 0xFF);
				SendBuff[10] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue() ^ 0xFF);
				SendBuff[11] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue() ^ 0xFF);
				SendBuff[12] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue() ^ 0xFF);
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
					return -2;
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
						displayOut(0, 0, "Generate Kcr (3DES) Success!");
					else
					{
						displayOut(0, 0, "Generate Kcr (3DES) Failed!");
						return -2;
					}
				}
				
				//Get Response to Retrieve Generated Data
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
					return -2;
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
						displayOut(0, 0, "Retrieve Kcr (3DES) Success!");
						GenKey = txtGenKey = "";
						
						//retrieve Card Serial Number
						for(int i = 0; i < 8; i++)
						{
							tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
							
							//For single character hex
							if (tmpHex.length() == 1) 
								tmpHex = "0" + tmpHex;
							
							txtGenKey = txtGenKey + tmpHex;
							GenKey += " " + tmpHex;
						}
						
						displayOut(3, 0, "Generated Kcr (right side): " + GenKey);
						tKcr2=(txtGenKey);
					}
					else
					{
						displayOut(0, 0, "Retrieve Kcr (3DES) Failed!");
						return -2;
					}
				}
			}
			else
			{
				tKcr2=("");
			}
			
			
			//Generate Certify Key (Kcf) Using 6th SAM Master Key (KeyID=86)
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0x88;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x86; //KeyID
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Generate Kcf Success!");
				else
				{
					displayOut(0, 0, "Generate Kcf Failed!");
					return -2;
				}
			}
			
			//Get Response to Retrieve Generated Key
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
				return -2;
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
					displayOut(0, 0, "Retrieve Kcf Success!");
					GenKey = txtGenKey = "";
					
					//retrieve Card Serial Number
					for(int i = 0; i < 8; i++)
					{
						tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
						//For single character hex
						if (tmpHex.length() == 1) 
							tmpHex = "0" + tmpHex;
						
						txtGenKey = txtGenKey + tmpHex;
						GenKey += " " + tmpHex;  
					}
					displayOut(3, 0, "Generated Kcf: " + GenKey);
					tKcf1=(txtGenKey);
				}
				else
				{
					displayOut(0, 0, "Retrieve Kcf Failed!");
					return -2;
				}
			}
			
			//If Algorithm Reference = 3DES then Generate Right Half of Certify Key (Kcf) Using 6th SAM Master Key (KeyID=86)
			if(getCipher().equals("3DES"))
			{	
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0x88;
				SendBuff[2] = (byte) 0x00;
				SendBuff[3] = (byte) 0x86; //KeyID
				SendBuff[4] = (byte) 0x08;
				
				//compliment the card serial number to generate right half key for 3DES algorithm				
				SendBuff[5] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue() ^ 0xFF);
				SendBuff[6] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue() ^ 0xFF);
				SendBuff[7] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue() ^ 0xFF);
				SendBuff[8] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue() ^ 0xFF);
				SendBuff[9] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue() ^ 0xFF);
				SendBuff[10] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue() ^ 0xFF);
				SendBuff[11] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue() ^ 0xFF);
				SendBuff[12] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue() ^ 0xFF);
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
					return -2;
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
						displayOut(0, 0, "Generate Kcf (3DES) Success!");
					else
					{
						displayOut(0, 0, "Generate Kcf (3DES) Failed!");
						return -2;
					}
				}
				
				//Get Response to Retrieve Generated Data
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
					return -2;
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
						displayOut(0, 0, "Retrieve Kcf (3DES) Success!");
						GenKey = txtGenKey = "";
						
						//retrieve Card Serial Number
						for(int i = 0; i < 8; i++)
						{
							tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
							
							//For single character hex
							if (tmpHex.length() == 1) 
								tmpHex = "0" + tmpHex;
							
							txtGenKey = txtGenKey + tmpHex;
							GenKey += " " + tmpHex;  
						}						
						displayOut(3, 0, "Generated Kcf (right side): " + GenKey);
						tKcf2=(txtGenKey);
					}
					else
					{
						displayOut(0, 0, "Retrieve Kcf (3DES) Failed!");
						return -2;
					}
				}
			}
			else
			{
				tKcf2=("");
			}
			
			
			//Generate Revoke Debit Key (Krd) Using 7th SAM Master Key (KeyID=87)
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0x88;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x87; //KeyID
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Generate Krd Success!");
				else
				{
					displayOut(0, 0, "Generate Krd Failed!");
					return -2;
				}
			}
			
			//Get Response to Retrieve Generated Key
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
				return -2;
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
					displayOut(0, 0, "Retrieve Krd Success!");
					GenKey = txtGenKey = "";
					
					//retrieve Card Serial Number
					for(int i = 0; i < 8; i++)
					{
						tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
						
						//For single character hex
						if (tmpHex.length() == 1) 
							tmpHex = "0" + tmpHex;
						
						txtGenKey = txtGenKey + tmpHex;
						GenKey += " " + tmpHex;  
					}
					displayOut(3, 0, "Generated Krd: " + GenKey);
					tKrd1=(txtGenKey);
				}
				else
				{
					displayOut(0, 0, "Retrieve Krd Failed!");
					return -2;
				}
			}
			
			//If Algorithm Reference = 3DES then Generate Right Half of Revoke Debit Key (Krd) Using 7th SAM Master Key (KeyID=87)
			if(getCipher().equals("3DES"))
			{	
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0x88;
				SendBuff[2] = (byte) 0x00;
				SendBuff[3] = (byte) 0x87; //KeyID
				SendBuff[4] = (byte) 0x08;
				
				//compliment the card serial number to generate right half key for 3DES algorithm
				SendBuff[5] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(0)) + Character.toString(tmpSN.charAt(1)), 16)).byteValue() ^ 0xFF);
				SendBuff[6] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(2)) + Character.toString(tmpSN.charAt(3)), 16)).byteValue() ^ 0xFF);
				SendBuff[7] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(4)) + Character.toString(tmpSN.charAt(5)), 16)).byteValue() ^ 0xFF);
				SendBuff[8] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(6)) + Character.toString(tmpSN.charAt(7)), 16)).byteValue() ^ 0xFF);
				SendBuff[9] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(8)) + Character.toString(tmpSN.charAt(9)), 16)).byteValue() ^ 0xFF);
				SendBuff[10] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(10)) + Character.toString(tmpSN.charAt(11)), 16)).byteValue() ^ 0xFF);
				SendBuff[11] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(12)) + Character.toString(tmpSN.charAt(13)), 16)).byteValue() ^ 0xFF);
				SendBuff[12] = (byte) (((Integer)Integer.parseInt(Character.toString(tmpSN.charAt(14)) + Character.toString(tmpSN.charAt(15)), 16)).byteValue() ^ 0xFF);
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
					return -2;
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
						displayOut(0, 0, "Generate Krd (3DES) Success!");
					else
					{
						displayOut(0, 0, "Generate Krd (3DES) Failed!");
						return -2;
					}
				}
				
				//Get Response to Retrieve Generated Data
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
					return -2;
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
						displayOut(0, 0, "Retrieve Krd (3DES) Success!");
						GenKey = txtGenKey = "";
						
						//retrieve Card Serial Number
						for(int i = 0; i < 8; i++)
						{
							tmpHex = Integer.toHexString(((Byte)RecvBuff[i]).intValue() & 0xFF).toUpperCase();
							
							//For single character hex
							if (tmpHex.length() == 1) 
								tmpHex = "0" + tmpHex;
							
							txtGenKey = txtGenKey + tmpHex;
							GenKey += " " + tmpHex;  
						}
						displayOut(3, 0, "Generated Krd (right side): " + GenKey);
						tKrd2=(txtGenKey);
					}
					else
					{
						displayOut(0, 0, "Retrieve Krd (3DES) Failed!");
						return -2;
					}
				}
			}
			else
			{
				tKrd2=("");
			}
			return 0;		
		}
		
		public int SaveGeneratedKeys(String tPIN) {

			String tmpStr="", tmpHex="";
			
			ACSModule.SCARD_IO_REQUEST IO_REQ = new ACSModule.SCARD_IO_REQUEST(); 
			ACSModule.SCARD_IO_REQUEST IO_REQ_Recv = new ACSModule.SCARD_IO_REQUEST(); 
			IO_REQ.dwProtocol = PrefProtocols[0];
			IO_REQ.cbPciLength = 8;
			IO_REQ_Recv.dwProtocol = PrefProtocols[0];
			IO_REQ_Recv.cbPciLength = 8;
			
			if((tPIN.equals(""))||(tPIN.length()<16))
			{
				return -1;
			}
			
			//update Personalization file (FF02)
			//select FF02			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xA4;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x02;
			SendBuff[5] = (byte) 0xFF;
			SendBuff[6] = (byte) 0x02;
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
				return -2;
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
					displayOut(0, 0, "Select FF 02 Success!");
				else
				{
					displayOut(0, 0, "Select FF 02 Failed!");
					return -2;
				}
			}
			
			//submit default IC
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
				return -2;
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
					displayOut(0, 0, "Submit IC Success!");
				else
				{
					displayOut(0, 0, "Submit IC Failed!");
					return -2;
				}
			}
			
			//update FF02 record 0
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x04;
			
			if(getCipher().equals("3DES"))
				SendBuff[5] = (byte) 0xFF; //INQ_AUT, TRNS_AUT, REV_DEB, DEB_PIN, DEB_MAC, Account, 3-DES, PIN_ALT Enabled
			else
				SendBuff[5] = (byte) 0xFD; //INQ_AUT, TRNS_AUT, REV_DEB, DEB_PIN, DEB_MAC, Account, 3-DES, PIN_ALT Enabled
			
			SendBuff[6] = (byte) 0x40; //PIN was encrypted and the PIN should be submitted in the submit code command must be encrypted with the current session key.
			SendBuff[7] = (byte) 0x00; //no user file created
			SendBuff[8] = (byte) 0x00;
			SendLen = 9;
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
				return -2;
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
					displayOut(0, 0, "Update FF 02 Success!");
				else
				{
					displayOut(0, 0, "Update FF 02 Failed!");
					return -2;
				}
			}
			
			//reset
		    retCode = jacs.jSCardDisconnect(hCard, ACSModule.SCARD_UNPOWER_CARD);
		    
		    String rdrcon = (String)cbSLT.get(indexACOSReader);  	      	      	
		    
		    retCode = jacs.jSCardConnect(hContext, 
										 rdrcon, 
										 ACSModule.SCARD_SHARE_SHARED,
										 ACSModule.SCARD_PROTOCOL_T1 | ACSModule.SCARD_PROTOCOL_T0,
										 hCard, 
										 PrefProtocols);
		    if(retCode != ACSModule.SCARD_S_SUCCESS)
		    {
		    	displayOut(1, retCode, "");
		    	return -2;
		    }		    
		    displayOut(0, 0, "Reset success.");
		    
			//update card keys
		    //select FF03
		    clearBuffers();
		    SendBuff[0] = (byte) 0x80;
		    SendBuff[1] = (byte) 0xA4;
		    SendBuff[2] = (byte) 0x00;
		    SendBuff[3] = (byte) 0x00;
		    SendBuff[4] = (byte) 0x02;
		    SendBuff[5] = (byte) 0xFF;
		    SendBuff[6] = (byte) 0x03;
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
				return -2;
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
					displayOut(0, 0, "Select FF 03 Success!");
				else
				{
					displayOut(0, 0, "Select FF 03 Failed!");
					return -2;
				}
			}
			
			//submit default IC
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
				return -2;
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
					displayOut(0, 0, "Submit IC Success!");
				else
				{
					displayOut(0, 0, "Submit IC Failed!");
					return -2;
				}
			}
			
			//update FF03 record 0 (IC)
			String tmpIC = tIC2;
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(0)) + Character.toString(tmpIC.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(2)) + Character.toString(tmpIC.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(4)) + Character.toString(tmpIC.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(6)) + Character.toString(tmpIC.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(8)) + Character.toString(tmpIC.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(10)) + Character.toString(tmpIC.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(12)) + Character.toString(tmpIC.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpIC.charAt(14)) + Character.toString(tmpIC.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Update IC Success!");
				else
				{
					displayOut(0, 0, "Update IC Failed!");
					return -2;
				}
			}
			
			//update FF 03 record 1 (PIN)
			String tmpPIN = tPIN;
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x01;
			SendBuff[3] = (byte) 0x00;
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
				return -2;
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
					displayOut(0, 0, "Update PIN Success!");
				else
				{
					displayOut(0, 0, "Update PIN Failed!");
					return -2;
				}
			}
			
			//update FF 03 record 2 (Kc)
			String tmpKc1 = tKc1;
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x02;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc1.charAt(0)) + Character.toString(tmpKc1.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc1.charAt(2)) + Character.toString(tmpKc1.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc1.charAt(4)) + Character.toString(tmpKc1.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc1.charAt(6)) + Character.toString(tmpKc1.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc1.charAt(8)) + Character.toString(tmpKc1.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc1.charAt(10)) + Character.toString(tmpKc1.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc1.charAt(12)) + Character.toString(tmpKc1.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc1.charAt(14)) + Character.toString(tmpKc1.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Update Kc Success!");
				else
				{
					displayOut(0, 0, "Update Kc Failed!");
					return -2;
				}
			}
			
			if(getCipher().equals("3DES"))//If Algorithm Reference = 3DES Update FF03 record 0x0C Right Half (Kc)
			{
				String tmpKc2 = tKc2;
				
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0xD2;
				SendBuff[2] = (byte) 0x0C;
				SendBuff[3] = (byte) 0x00;
				SendBuff[4] = (byte) 0x08;
				SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc2.charAt(0)) + Character.toString(tmpKc2.charAt(1)), 16)).byteValue();
				SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc2.charAt(2)) + Character.toString(tmpKc2.charAt(3)), 16)).byteValue();
				SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc2.charAt(4)) + Character.toString(tmpKc2.charAt(5)), 16)).byteValue();
				SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc2.charAt(6)) + Character.toString(tmpKc2.charAt(7)), 16)).byteValue();
				SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc2.charAt(8)) + Character.toString(tmpKc2.charAt(9)), 16)).byteValue();
				SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc2.charAt(10)) + Character.toString(tmpKc2.charAt(11)), 16)).byteValue();
				SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc2.charAt(12)) + Character.toString(tmpKc2.charAt(13)), 16)).byteValue();
				SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKc2.charAt(14)) + Character.toString(tmpKc2.charAt(15)), 16)).byteValue();
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
					return -2;
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
						displayOut(0, 0, "Update Kc (3DES) Success!");
					else
					{
						displayOut(0, 0, "Update Kc (3DES) Failed!");
						return -2;
					}
				}
			}
			
			//update FF03 record 3 (Kt)
			String tmpKt1 = tKt1;
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x03;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt1.charAt(0)) + Character.toString(tmpKt1.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt1.charAt(2)) + Character.toString(tmpKt1.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt1.charAt(4)) + Character.toString(tmpKt1.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt1.charAt(6)) + Character.toString(tmpKt1.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt1.charAt(8)) + Character.toString(tmpKt1.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt1.charAt(10)) + Character.toString(tmpKt1.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt1.charAt(12)) + Character.toString(tmpKt1.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt1.charAt(14)) + Character.toString(tmpKt1.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Update Kt Success!");
				else
				{
					displayOut(0, 0, "Update Kt Failed!");
					return -2;
				}
			}
			
			if(getCipher().equals("3DES"))//If Algorithm Reference = 3DES Update FF03 record 0x0D Right Half (Kt)
			{
				String tmpKt2 = tKt2;
				
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0xD2;
				SendBuff[2] = (byte) 0x0D;
				SendBuff[3] = (byte) 0x00;
				SendBuff[4] = (byte) 0x08;
				SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt2.charAt(0)) + Character.toString(tmpKt2.charAt(1)), 16)).byteValue();
				SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt2.charAt(2)) + Character.toString(tmpKt2.charAt(3)), 16)).byteValue();
				SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt2.charAt(4)) + Character.toString(tmpKt2.charAt(5)), 16)).byteValue();
				SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt2.charAt(6)) + Character.toString(tmpKt2.charAt(7)), 16)).byteValue();
				SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt2.charAt(8)) + Character.toString(tmpKt2.charAt(9)), 16)).byteValue();
				SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt2.charAt(10)) + Character.toString(tmpKt2.charAt(11)), 16)).byteValue();
				SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt2.charAt(12)) + Character.toString(tmpKt2.charAt(13)), 16)).byteValue();
				SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKt2.charAt(14)) + Character.toString(tmpKt2.charAt(15)), 16)).byteValue();
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
					return -2;
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
						displayOut(0, 0, "Update Kt (3DES) Success!");
					else
					{
						displayOut(0, 0, "Update Kt (3DES) Failed!");
						return -2;
					}
				}
			}
			
			//select FF06
		    clearBuffers();
		    SendBuff[0] = (byte) 0x80;
		    SendBuff[1] = (byte) 0xA4;
		    SendBuff[2] = (byte) 0x00;
		    SendBuff[3] = (byte) 0x00;
		    SendBuff[4] = (byte) 0x02;
		    SendBuff[5] = (byte) 0xFF;
		    SendBuff[6] = (byte) 0x06;
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
				return -2;
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
					displayOut(0, 0, "Select FF 06 Success!");
				else
				{
					displayOut(0, 0, "Select FF 06 Failed!");
					return -2;
				}				
			}
			
			//update FF06 record 0 (Kd)
			String tmpKd1 = tKd1;
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			
			if(getCipher().equals("3DES"))
				SendBuff[2] = (byte) 0x04;
			else
				SendBuff[2] = (byte) 0x00;
				
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd1.charAt(0)) + Character.toString(tmpKd1.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd1.charAt(2)) + Character.toString(tmpKd1.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd1.charAt(4)) + Character.toString(tmpKd1.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd1.charAt(6)) + Character.toString(tmpKd1.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd1.charAt(8)) + Character.toString(tmpKd1.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd1.charAt(10)) + Character.toString(tmpKd1.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd1.charAt(12)) + Character.toString(tmpKd1.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd1.charAt(14)) + Character.toString(tmpKd1.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Update Kd Success!");
				else
				{
					displayOut(0, 0, "Update Kd Failed!");
					return -2;
				}
			}
			
			//update FF06 record 1 (Kcr)
			String tmpKcr1 = tKcr1;
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			
			if(getCipher().equals("3DES"))
				SendBuff[2] = (byte) 0x05;
			else
				SendBuff[2] = (byte) 0x01;
				
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr1.charAt(0)) + Character.toString(tmpKcr1.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr1.charAt(2)) + Character.toString(tmpKcr1.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr1.charAt(4)) + Character.toString(tmpKcr1.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr1.charAt(6)) + Character.toString(tmpKcr1.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr1.charAt(8)) + Character.toString(tmpKcr1.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr1.charAt(10)) + Character.toString(tmpKcr1.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr1.charAt(12)) + Character.toString(tmpKcr1.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr1.charAt(14)) + Character.toString(tmpKcr1.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Update Kcr Success!");
				else
				{
					displayOut(0, 0, "Update Kcr Failed!");
					return -2;
				}
			}
			
			//update FF06 record 2 (Kcf)
			String tmpKcf1 = tKcf1;
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			
			if(getCipher().equals("3DES"))
				SendBuff[2] = (byte) 0x06;
			else
				SendBuff[2] = (byte) 0x02;
				
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf1.charAt(0)) + Character.toString(tmpKcf1.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf1.charAt(2)) + Character.toString(tmpKcf1.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf1.charAt(4)) + Character.toString(tmpKcf1.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf1.charAt(6)) + Character.toString(tmpKcf1.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf1.charAt(8)) + Character.toString(tmpKcf1.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf1.charAt(10)) + Character.toString(tmpKcf1.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf1.charAt(12)) + Character.toString(tmpKcf1.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf1.charAt(14)) + Character.toString(tmpKcf1.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Update Kcf Success!");
				else
				{
					displayOut(0, 0, "Update Kcf Failed!");
					return -2;
				}
			}
			
			//update FF06 record 3 (Krd)
			String tmpKrd1 = tKrd1;
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			
			if(getCipher().equals("3DES"))
				SendBuff[2] = (byte) 0x07;
			else
				SendBuff[2] = (byte) 0x03;
				
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x08;
			SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd1.charAt(0)) + Character.toString(tmpKrd1.charAt(1)), 16)).byteValue();
			SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd1.charAt(2)) + Character.toString(tmpKrd1.charAt(3)), 16)).byteValue();
			SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd1.charAt(4)) + Character.toString(tmpKrd1.charAt(5)), 16)).byteValue();
			SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd1.charAt(6)) + Character.toString(tmpKrd1.charAt(7)), 16)).byteValue();
			SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd1.charAt(8)) + Character.toString(tmpKrd1.charAt(9)), 16)).byteValue();
			SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd1.charAt(10)) + Character.toString(tmpKrd1.charAt(11)), 16)).byteValue();
			SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd1.charAt(12)) + Character.toString(tmpKrd1.charAt(13)), 16)).byteValue();
			SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd1.charAt(14)) + Character.toString(tmpKrd1.charAt(15)), 16)).byteValue();
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
				return -2;
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
					displayOut(0, 0, "Update Krd Success!");
				else
				{
					displayOut(0, 0, "Update Krd Failed!");
					return -2;
				}
			}
			
			//If Algorithm Reference = 3DES then update Right Half of the Keys
			if(getCipher().equals("3DES"))
			{	
				//Update FF06 record 0 (Kd) right half
				String tmpKd2 = tKd2;
				
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0xD2;
				SendBuff[2] = (byte) 0x00;
				SendBuff[3] = (byte) 0x00;
				SendBuff[4] = (byte) 0x08;
				SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd2.charAt(0)) + Character.toString(tmpKd2.charAt(1)), 16)).byteValue();
				SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd2.charAt(2)) + Character.toString(tmpKd2.charAt(3)), 16)).byteValue();
				SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd2.charAt(4)) + Character.toString(tmpKd2.charAt(5)), 16)).byteValue();
				SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd2.charAt(6)) + Character.toString(tmpKd2.charAt(7)), 16)).byteValue();
				SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd2.charAt(8)) + Character.toString(tmpKd2.charAt(9)), 16)).byteValue();
				SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd2.charAt(10)) + Character.toString(tmpKd2.charAt(11)), 16)).byteValue();
				SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd2.charAt(12)) + Character.toString(tmpKd2.charAt(13)), 16)).byteValue();
				SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKd2.charAt(14)) + Character.toString(tmpKd2.charAt(15)), 16)).byteValue();
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
					return -2;
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
						displayOut(0, 0, "Update Kd (3DES) Success!");
					else
					{
						displayOut(0, 0, "Update Kd (3DES) Failed!");
						return -2;
					}
				}
				
				//Update FF06 record 1 (Kcr) right half
				String tmpKcr2 = tKcr2;
				
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0xD2;
				SendBuff[2] = (byte) 0x01;
				SendBuff[3] = (byte) 0x00;
				SendBuff[4] = (byte) 0x08;
				SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr2.charAt(0)) + Character.toString(tmpKcr2.charAt(1)), 16)).byteValue();
				SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr2.charAt(2)) + Character.toString(tmpKcr2.charAt(3)), 16)).byteValue();
				SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr2.charAt(4)) + Character.toString(tmpKcr2.charAt(5)), 16)).byteValue();
				SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr2.charAt(6)) + Character.toString(tmpKcr2.charAt(7)), 16)).byteValue();
				SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr2.charAt(8)) + Character.toString(tmpKcr2.charAt(9)), 16)).byteValue();
				SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr2.charAt(10)) + Character.toString(tmpKcr2.charAt(11)), 16)).byteValue();
				SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr2.charAt(12)) + Character.toString(tmpKcr2.charAt(13)), 16)).byteValue();
				SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcr2.charAt(14)) + Character.toString(tmpKcr2.charAt(15)), 16)).byteValue();
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
					return -2;
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
						displayOut(0, 0, "Update Kcr (3DES) Success!");
					else
					{
						displayOut(0, 0, "Update Kcr (3DES) Failed!");
						return -2;
					}
				}
				
				//update FF06 record 2 (Kcf) right half
				String tmpKcf2 = tKcf2;
				
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0xD2;
				SendBuff[2] = (byte) 0x02;
				SendBuff[3] = (byte) 0x00;
				SendBuff[4] = (byte) 0x08;
				SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf2.charAt(0)) + Character.toString(tmpKcf2.charAt(1)), 16)).byteValue();
				SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf2.charAt(2)) + Character.toString(tmpKcf2.charAt(3)), 16)).byteValue();
				SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf2.charAt(4)) + Character.toString(tmpKcf2.charAt(5)), 16)).byteValue();
				SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf2.charAt(6)) + Character.toString(tmpKcf2.charAt(7)), 16)).byteValue();
				SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf2.charAt(8)) + Character.toString(tmpKcf2.charAt(9)), 16)).byteValue();
				SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf2.charAt(10)) + Character.toString(tmpKcf2.charAt(11)), 16)).byteValue();
				SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf2.charAt(12)) + Character.toString(tmpKcf2.charAt(13)), 16)).byteValue();
				SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKcf2.charAt(14)) + Character.toString(tmpKcf2.charAt(15)), 16)).byteValue();
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
					return -2;
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
						displayOut(0, 0, "Update Kcf (3DES) Success!");
					else
					{
						displayOut(0, 0, "Update Kcf (3DES) Failed!");
						return -2;
					}
				}
				
				//update FF06 record 3 (Krd) right half
				String tmpKrd2 = tKrd2;
				clearBuffers();
				SendBuff[0] = (byte) 0x80;
				SendBuff[1] = (byte) 0xD2;
				SendBuff[2] = (byte) 0x03;
				SendBuff[3] = (byte) 0x00;
				SendBuff[4] = (byte) 0x08;
				SendBuff[5] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd2.charAt(0)) + Character.toString(tmpKrd2.charAt(1)), 16)).byteValue();
				SendBuff[6] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd2.charAt(2)) + Character.toString(tmpKrd2.charAt(3)), 16)).byteValue();
				SendBuff[7] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd2.charAt(4)) + Character.toString(tmpKrd2.charAt(5)), 16)).byteValue();
				SendBuff[8] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd2.charAt(6)) + Character.toString(tmpKrd2.charAt(7)), 16)).byteValue();
				SendBuff[9] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd2.charAt(8)) + Character.toString(tmpKrd2.charAt(9)), 16)).byteValue();
				SendBuff[10] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd2.charAt(10)) + Character.toString(tmpKrd2.charAt(11)), 16)).byteValue();
				SendBuff[11] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd2.charAt(12)) + Character.toString(tmpKrd2.charAt(13)), 16)).byteValue();
				SendBuff[12] = (byte) ((Integer)Integer.parseInt(Character.toString(tmpKrd2.charAt(14)) + Character.toString(tmpKrd2.charAt(15)), 16)).byteValue();
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
					return -2;
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
						displayOut(0, 0, "Update Krd (3DES) Success!");
					else
					{
						displayOut(0, 0, "Update Krd (3DES) Failed!");
						return -2;
					}
				}
			}
			
			//select FF05
		    clearBuffers();
		    SendBuff[0] = (byte) 0x80;
		    SendBuff[1] = (byte) 0xA4;
		    SendBuff[2] = (byte) 0x00;
		    SendBuff[3] = (byte) 0x00;
		    SendBuff[4] = (byte) 0x02;
		    SendBuff[5] = (byte) 0xFF;
		    SendBuff[6] = (byte) 0x05;
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
				return -2;
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
					displayOut(0, 0, "Select FF 05 Success!");
				else
				{
					displayOut(0, 0, "Select FF 05 Failed!");
					return -2;
				}
			}
			
			// Initialize FF05 Account File
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x00;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x04;
			SendBuff[5] = (byte) 0x00;
			SendBuff[6] = (byte) 0x00;
			SendBuff[7] = (byte) 0x00;
			SendBuff[8] = (byte) 0x00;
			SendLen = 9;
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
				return -2;
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
					displayOut(0, 0, "Initialize FF 05 Success!");
				else
				{
					displayOut(0, 0, "Initialize FF 05 Failed!");
					return -2;
				}
			}
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x01;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x04;
			SendBuff[5] = (byte) 0x00;
			SendBuff[6] = (byte) 0x00;
			SendBuff[7] = (byte) 0x01;
			SendBuff[8] = (byte) 0x00;
			SendLen = 9;
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
				return -2;
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
					displayOut(0, 0, "Initialize FF 05 Success!");
				else
				{
					displayOut(0, 0, "Initialize FF 05 Failed!");
					return -2;
				}
			}
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x02;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x04;
			SendBuff[5] = (byte) 0x00;
			SendBuff[6] = (byte) 0x00;
			SendBuff[7] = (byte) 0x00;
			SendBuff[8] = (byte) 0x00;
			SendLen = 9;
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
				return -2;
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
					displayOut(0, 0, "Initialize FF 05 Success!");
				else
				{
					displayOut(0, 0, "Initialize FF 05 Failed!");
					return -2;
				}
			}
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x03;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x04;
			SendBuff[5] = (byte) 0x00;
			SendBuff[6] = (byte) 0x00;
			SendBuff[7] = (byte) 0x01;
			SendBuff[8] = (byte) 0x00;
			SendLen = 9;
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
				return -2;
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
					displayOut(0, 0, "Initialize FF 05 Success!");
				else
				{
					displayOut(0, 0, "Initialize FF 05 Failed!");
					return -2;
				}
			}
			
			//set max balance to 98 96 7F = 9,999,999
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x04;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x04;
			SendBuff[5] = (byte) 0x98;
			SendBuff[6] = (byte) 0x96;
			SendBuff[7] = (byte) 0x7F;
			SendBuff[8] = (byte) 0x00;
			SendLen = 9;
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
				return -2;
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
					displayOut(0, 0, "Max Balance set Success!");
				else
				{
					displayOut(0, 0, "Max Balance set Failed!");
					return -2;
				}
			}
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x05;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x04;
			SendBuff[5] = (byte) 0x00;
			SendBuff[6] = (byte) 0x00;
			SendBuff[7] = (byte) 0x00;
			SendBuff[8] = (byte) 0x00;			
			SendLen = 9;
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
				return -2;
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
					displayOut(0, 0, "Initialize FF 05 Success!");
				else
				{
					displayOut(0, 0, "Initialize FF 05 Failed!");
					return -2;
				}
			}
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x06;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x04;
			SendBuff[5] = (byte) 0x00;
			SendBuff[6] = (byte) 0x00;
			SendBuff[7] = (byte) 0x00;
			SendBuff[8] = (byte) 0x00;
			SendLen = 9;
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
				return -2;
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
					displayOut(0, 0, "Initialize FF 05 Success!");
				else
				{
					displayOut(0, 0, "Initialize FF 05 Failed!");
					return -2;
				}
			}
			
			clearBuffers();
			SendBuff[0] = (byte) 0x80;
			SendBuff[1] = (byte) 0xD2;
			SendBuff[2] = (byte) 0x07;
			SendBuff[3] = (byte) 0x00;
			SendBuff[4] = (byte) 0x04;
			SendBuff[5] = (byte) 0x00;
			SendBuff[6] = (byte) 0x00;
			SendBuff[7] = (byte) 0x00;
			SendBuff[8] = (byte) 0x00;
			SendLen = 9;
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
				return -2;
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
					displayOut(0, 0, "Initialize FF 05 Success!");
				else
				{
					displayOut(0, 0, "Initialize FF 05 Failed!");
					return -2;
				}
			}			
			return 0;
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
