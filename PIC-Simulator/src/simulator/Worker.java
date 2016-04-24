package simulator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Worker {
	static int indexOfstartAddressOfAdressArray = 0;
	static String indexOfInstructions = "0000";
	static String instructionsString ="";
	static String registerInputArray[] = new String[256];
	static int statusBitsArray[] = new int[8];
	static String wHex;
	static int w;

	static String callStackSaver = "";
	static ArrayList<String> callStackSaverAll = new ArrayList<>();
	static HashMap<String, Integer> statusTest = new HashMap<>();
	static int wCheck;
	static int getValue;
	static int cyclesInMicroSeconds;
	static String startInstruction;
	static boolean startFlag = false;



	public static void workWithWorker(ArrayList<String> linesReadIn) { //der Funktion workWithWorker wird ein Array von Strings mit dem Namen linesReadIn übergeben

		String[] addressArrayHex = new String[linesReadIn.size()]; //ein Stringarray mit dem Namen addressArrayHex von der Größe des Arrays linesReadIn wird erstellt 
		String[] instructionArrayHex = new String[linesReadIn.size()]; //ein Stringarray mit dem Namen instructionArrayHex von der Größe des Arrays linesReadIn wird erstellt



		//fill array only the first time with ""
		for(int u = 0; u < registerInputArray.length; u++){
			if(registerInputArray[u] == null) { //wenn das Zeichen an der ersten betrachteten Stelle "null" ist (also wenn es gerade erst initialisiert wurde)
				Arrays.fill(registerInputArray, "00"); //dann fülle den ganzen String mit Nullen
			}
		}
		
		//die Integervariable cyclesInMicroSeconds wird mit 1 gefüllt
		cyclesInMicroSeconds = 1; 

		for(int i = 0; i < linesReadIn.size(); i++){  //solange Variable i kleiner als die Größe des Arrays linesReadIn ist
			addressArrayHex[i] = linesReadIn.get(i).substring(0, 4); //schreibe die ersten vier Zeichen des Strings an der Stelle i des Arrays linesReadIn in den String des Arrays addressArrayHex an der Stelle i
			instructionArrayHex[i] = linesReadIn.get(i).substring(5, 9); //schreibe die letzten vier Zeichen des Strings an der Stelle i des Arrays linesReadIn in den String des Arrays instructionArrayHex an der Stelle i
		}




		//TODO if adressArrayHex[i].equals does not find one match, it starts from "0000" o.O
		//start where addressHex = 0000
		for(int i = 0; i < addressArrayHex.length; i++) { //durchlaufe das Array addressArrayHex
			if(addressArrayHex[i].equals("0000")) { //wenn addressArrayHex an der aktuell betrachteten Stelle dem String "0000" entspricht
				//				System.err.println("start setted");
				indexOfstartAddressOfAdressArray = i; //überschreibe die Integervariable indexOfstartAddressOfAdressArray mit dem aktuellen i-Wert
				instructionsString = instructionArrayHex[indexOfstartAddressOfAdressArray]; //überschreibe den String instructionsString mit dem String aus instructionArrayHex an der Stelle i
				startInstruction = instructionArrayHex[indexOfstartAddressOfAdressArray]; //überschreibe den String startInstruction mit demselben Wert wie instructionsString
			} else if(addressArrayHex[i].equals(indexOfInstructions.toUpperCase())) { //wenn addressArrayHex an der aktuell betrachteten Stelle dem Wert von indexOfInstructions in Großbuchstaben entspricht 
				instructionsString = instructionArrayHex[i]; //dann überschreibe den String instructionsString mit dem String aus instructionArrayHex an der Stelle i
				break; //dann beende die for-Schleife
			}
		}

		if(startFlag == true) { //wenn das startFlag auf true gesetzt ist
			instructionsString = startInstruction; //instructionsString wird mit startInstruction überschrieben (beides sind Strings)
			startFlag = false; //startFlag wird auf false gesetzt
		} 

		int instructions = Integer.parseInt(instructionsString,16); //ein Integerwert (2 Byte) wird initialisert. Und das indem instructionsString von Hex in Dec umgewandelt

		//Byte-Oriented File Register Operations
		int opcode = instructions >> 8; //die 2 Byte Instruktionen werden hier getrennt. OpCode sind die 4 Bits 9-12 (die dritte Gruppe von rechts). Dieser bestimmt den Befehl
		int destination = (instructions & 0x0080) >> 7; //unter destination wird das Bit an der Stelle 8 (von rechts) einzeln abgespeichert
		int address = (instructions & 0x007F); //unter address werden die letzten 7 Bits abgespeichert
		
		if(CreateRegisters.statusRegItem.getText(2).equals("1")) {
			address = 128 + address;	
		}

		//TODO flags C,DC,Z!!!!
		//ADDWF
		if(opcode == 7) {

			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				dcFlagSetter(Integer.parseInt(registerInputArray[address],16),w,true);
				if((Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16)) > 255){
					w = (Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16)) - 256;
					//Status-Register: C
					CreateRegisters.statusRegItem.setText(7, "1");
				} else {
					w = Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16);
					CreateRegisters.statusRegItem.setText(7, "0");
				}
				wHex = Integer.toHexString(w);
				zFlagSetter(wHex);

			} else if (destination == 1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				dcFlagSetter(Integer.parseInt(registerInputArray[address],16),w,true);
				if((Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16)) > 255) {
					registerInputArray[address] = 
							Integer.toHexString((Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16)) - 256);
					CreateRegisters.statusRegItem.setText(7, "1");
				} else {
					registerInputArray[address] = 
							Integer.toHexString(Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16));
					CreateRegisters.statusRegItem.setText(7, "0");
				}
				zFlagSetter(registerInputArray[address]);
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));

		}

		//ANDWF
		if(opcode == 5) {

			if(destination == 0){
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) & Integer.parseInt(wHex,16));
				w = Integer.parseInt(wHex,16);
				zFlagSetter(wHex);
			} else if(destination == 1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) & Integer.parseInt(wHex,16));
				zFlagSetter(registerInputArray[address]);
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//CLRF
		if(opcode == 1 && destination == 1) {
			address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
			registerInputArray[address] = "00";
			zFlagSetter(registerInputArray[address]);			
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
			FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);


		}

		//CLRW
		if(opcode == 1 && destination == 0) {
			w = 0;
			wHex = "00";
			zFlagSetter(wHex);
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
			FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);


		}

		// TODO schauen wegen address switchAdressdestination
		//COMF
		if(opcode == 9) {
			if(destination==0){
				//				w = (~Integer.parseInt(registerInputArray[address],16));
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				wHex = Integer.toHexString(~Integer.parseInt(registerInputArray[address],16)).substring(6, 8);
				w = Integer.parseInt(wHex,16);
				zFlagSetter(wHex);
			} else if(destination ==1){
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				registerInputArray[address] = (Integer.toHexString(~Integer.parseInt(registerInputArray[address],16)).substring(6, 8));
				zFlagSetter(registerInputArray[address]);
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}

			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//DECF
		if(opcode == 3) {
			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)-1);
				w = Integer.parseInt(wHex,16);
				zFlagSetter(wHex);
			} else if(destination == 1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)-1);
				zFlagSetter(registerInputArray[address]);	
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}

			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//TODO defcz f,d <- d bearbeiten!!!
		//DECFSZ
		if(opcode == 11) {
			if(destination ==  0){
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)-1);
				w = Integer.parseInt(wHex,16);
				if(Integer.parseInt(wHex,16) == 0) {
					cyclesInMicroSeconds = 2;
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2));
				} else {
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
				}
			}else if(destination == 1){
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)-1);	
				if(Integer.parseInt(registerInputArray[address],16) == 0) {
					cyclesInMicroSeconds = 2;
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2));
				} else {
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
				}
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}

		}

		//TODO 
		//INCF
		if(opcode == 10) {
			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				if((Integer.parseInt(registerInputArray[address],16)+1)  > 255) {
					wHex = Integer.toHexString((Integer.parseInt(registerInputArray[address],16) + 1) - 256);
				} //TODO //TODO muss hier noch ein bit gesetzt werden??? 
				else {
					wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)+1);	
				}
				w = Integer.parseInt(wHex,16);
				zFlagSetter(wHex);
			}else if(destination ==1){
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				if((Integer.parseInt(registerInputArray[address],16)+1)  > 255) {
					registerInputArray[address] = Integer.toHexString((Integer.parseInt(registerInputArray[address],16) + 1) - 256);
				} //TODO //TODO bit setzen??? 
				else {
					registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)+1);
				}
				zFlagSetter(registerInputArray[address]);
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//INCFSZ
		if(opcode == 15) {
			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				if(Integer.parseInt(registerInputArray[address],16) == 255) {
					wHex = "00";
				} else {
					wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) + 1);
				}				
				w = Integer.parseInt(wHex,16);
				if(Integer.parseInt(wHex,16) == 0) {
					cyclesInMicroSeconds = 2;
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2));
				} else {
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
				}
			} else if(destination == 1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				if(Integer.parseInt(registerInputArray[address],16) == 255) {
					registerInputArray[address] = "00";
				} else {
					registerInputArray[address]  = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) + 1);
				}
				System.err.println("nummer 21: " + registerInputArray[address]);
				if(Integer.parseInt(registerInputArray[address],16) == 0) {
					cyclesInMicroSeconds = 2;
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2));
				} else {
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
				}
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}

		}

		//IORWF
		if(opcode == 4) {
			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) | Integer.parseInt(wHex,16));
				w = Integer.parseInt(wHex,16);
				zFlagSetter(wHex);
			} else if(destination == 1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) | Integer.parseInt(wHex,16));
				zFlagSetter(registerInputArray[address]);
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//MOVF
		if(opcode == 8) {
			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				wHex = registerInputArray[address];
				w = Integer.parseInt(wHex,16);
				zFlagSetter(wHex);
			} else if(destination == 1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				registerInputArray[address] = registerInputArray[address];
				zFlagSetter(registerInputArray[address]);
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//MOVWF
		if(opcode == 0 && destination == 1) {
			address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
			registerInputArray[address] = wHex;
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
			FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
		}

		//NOP
		if(opcode == 0 && destination == 0) {
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//RLF
		if(opcode == 13) {
			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				String binary = new BigInteger(registerInputArray[address],16).toString(2);
				while(binary.length() < 8) {
					binary = "0"+binary;
				}
				// erste möglichkeit
				char[] cAry = binary.toCharArray();
				int temp = Integer.parseInt(cAry[0]+"");
				cAry[0] = '0';
				for(int u = 0; u < cAry.length-1; u++){
					cAry[u] = cAry[u+1];
				}
				cAry[7] = CreateRegisters.statusRegItem.getText(7).charAt(0);
				String backtogether = "";
				for(int i = 0; i < cAry.length; i++) {
					backtogether = backtogether +cAry[i];
				}
				CreateRegisters.statusRegItem.setText(7, temp+"");
				wHex = Integer.toHexString(Integer.parseInt(backtogether,2));
				w = Integer.parseInt(wHex,16);

			} else if(destination == 1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				String binary = new BigInteger(registerInputArray[address],16).toString(2);
				while(binary.length() < 8) {
					binary = "0"+binary;
				}
				// erste möglichkeit
				char[] cAry = binary.toCharArray();
				int temp = Integer.parseInt(cAry[0]+"");
				cAry[0] = '0';
				for(int u = 0; u < cAry.length-1; u++){
					cAry[u] = cAry[u+1];
				}
				cAry[7] = CreateRegisters.statusRegItem.getText(7).charAt(0);
				String backtogether = "";
				for(int i = 0; i < cAry.length; i++) {
					backtogether = backtogether +cAry[i];
				}
				CreateRegisters.statusRegItem.setText(7,temp+"");		
				registerInputArray[address] =  Integer.toHexString(Integer.parseInt(backtogether,2));
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//RRF
		if(opcode == 12) {
			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				String binary = new BigInteger(registerInputArray[address],16).toString(2);
				while(binary.length() < 8) {
					binary = "0"+binary;
				}
				// erste möglichkeit
				char[] cAry = binary.toCharArray();
				int temp = Integer.parseInt(cAry[7]+"");
				cAry[7] = '0';
				for(int u = 7; u > 0; u--){
					cAry[u] = cAry[u-1];
				}
				cAry[0] = CreateRegisters.statusRegItem.getText(7).charAt(0);
				String backtogether = "";
				for(int i = 0; i < cAry.length; i++) {
					backtogether = backtogether +cAry[i];
				}
				CreateRegisters.statusRegItem.setText(7, temp+"");
				wHex = Integer.toHexString(Integer.parseInt(backtogether,2));
				w = Integer.parseInt(wHex,16);
			} else if(destination == 1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				String binary = new BigInteger(registerInputArray[address],16).toString(2);
				while(binary.length() < 8) {
					binary = "0"+binary;
				}
				// erste möglichkeit
				char[] cAry = binary.toCharArray();
				int temp = Integer.parseInt(cAry[7]+"");
				cAry[7] = '0';
				for(int u = 7; u > 0; u--){
					cAry[u] = cAry[u-1];
				}
				cAry[0] = CreateRegisters.statusRegItem.getText(7).charAt(0);
				String backtogether = "";
				for(int i = 0; i < cAry.length; i++) {
					backtogether = backtogether +cAry[i];
				}
				CreateRegisters.statusRegItem.setText(7,temp+"");		
				registerInputArray[address] =  Integer.toHexString(Integer.parseInt(backtogether,2));
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//TODO flags c, dc , z
		//SUBWF
		if(opcode == 2) {
			System.err.println();
			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				dcFlagSetter(Integer.parseInt(registerInputArray[address],16), w, false);
				if((Integer.parseInt(registerInputArray[address],16)-w) < 0) { // wenn  w kleiner null (negat überlauf)
					w = 256+(Integer.parseInt(registerInputArray[address],16)-w);
					CreateRegisters.statusRegItem.setText(7,"0");
				} else {
					w = Integer.parseInt(registerInputArray[address],16)-w;
					CreateRegisters.statusRegItem.setText(7,"1");
				}

				wHex = Integer.toHexString(w);
				zFlagSetter(wHex);

			} else if(destination ==1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				dcFlagSetter(Integer.parseInt(registerInputArray[address],16), w, false);
				if((Integer.parseInt(registerInputArray[address],16)-w) < 0) {
					registerInputArray[address] = Integer.toHexString(256 + (Integer.parseInt(registerInputArray[address],16)-w));
					CreateRegisters.statusRegItem.setText(7,"0");
				} else {
					registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)-w);
					CreateRegisters.statusRegItem.setText(7,"1");
				}

				zFlagSetter(registerInputArray[address]);
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//SWAPF
		if(opcode == 14) {
			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				String binary = new BigInteger(registerInputArray[address],16).toString(2);
				while(binary.length() < 8) {
					binary = "0" + binary;
				}
				String vordere4Bit = binary.substring(0, 4);
				String hintere4Bit = binary.substring(4, 8);
				binary = hintere4Bit + vordere4Bit;
				wHex = Integer.toHexString(Integer.parseInt(binary,2));
				w = Integer.parseInt(wHex,16);

			} else if(destination == 1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				String binary = new BigInteger(registerInputArray[address],16).toString(2);
				while(binary.length() < 8) {
					binary = "0" + binary;
				}
				String vordere4Bit = binary.substring(0, 4);
				String hintere4Bit = binary.substring(4, 8);
				binary = hintere4Bit +""+ vordere4Bit;
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(binary,2));
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		//XORWF
		if(opcode == 6) {
			if(destination == 0) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) ^ Integer.parseInt(wHex,16));
				w = Integer.parseInt(wHex,16);
				zFlagSetter(wHex);

			} else if(destination == 1) {
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) ^ Integer.parseInt(wHex,16));
				zFlagSetter(registerInputArray[address]);
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
			}
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}



		//TODO
		//Bit-oriented File Register Operations
		opcode = instructions >> 10;
				int bit = (instructions & 0x0380) >> 7;
				address = instructions & 127;


				//TODO gleiches wie bei BSF
				//BCF
				if(opcode == 4) {

					address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
					String binary = new BigInteger(registerInputArray[address],16).toString(2);
					while(binary.length() < 8) {
						binary = "0"+binary;
					}
					char[] a = binary.toCharArray();
					bit = 7-bit;
					a[bit] = '0';
					String backtogether ="";
					for(int i = 0; i < a.length;i++){
						backtogether = backtogether + a[i];
					}

					//nur wennn status
					if(address == 3){
						CreateRegisters.switchStatusClear(bit);
					}
					//					if(address == 129) {
					//						CreateRegisters.checkOneOrZero(CreateRegisters.optionBits[bit]);
					//					}

					registerInputArray[address] = Integer.toHexString(Integer.parseInt(backtogether,2));
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
					FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
				}

				//TODO Status bits die namen verteilen, zuweisen, dass bit nr 5 z.b = rp0 
				// hier wird rp0 zum Test per hand gesetzt
				//BSF
				if(opcode == 5) {
					address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
					String binary = new BigInteger(registerInputArray[address],16).toString(2);
					while(binary.length() < 8) {
						binary = "0"+binary;
					}
					char[] a = binary.toCharArray();
					bit = 7-bit;
					a[bit] = '1';
					String backtogether ="";
					for(int i = 0; i < a.length;i++){
						backtogether = backtogether + a[i];
					}

					if(address == 3){
						CreateRegisters.switchStatusSet(bit);
					}
					if(address == 129) {
						CreateRegisters.checkOneOrZero(CreateRegisters.optionBits[bit]);
					}

					registerInputArray[address] = Integer.toHexString(Integer.parseInt(backtogether,2));
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
					FillRegister.generalValueSetterForRegister(address, registerInputArray[address]);
				}

				//TODO zero flag check bearbeiten!!!!
				//BTFSC
				if(opcode == 6) {
					address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
					String binary = new BigInteger(registerInputArray[address],16).toString(2);
					while(binary.length() < 8) {
						binary = "0"+binary;
					}
					char[] a = binary.toCharArray();
					bit= 7-bit;
					if(a[bit] == '0'){
						cyclesInMicroSeconds = 2;
						indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2));
					} else {
						indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
					}
				}

				//BTFSS
				if(opcode == 7) {
					address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
					String binary = new BigInteger(registerInputArray[address],16).toString(2);
					while(binary.length() < 8) {
						binary = "0"+binary;
					}
					char[] a = binary.toCharArray();
					bit = 7-bit;
					if(a[bit] == '1'){
						cyclesInMicroSeconds = 2;
						indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2));
					} else {
						indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
					}
				}



				//TODO
				//Literal and Control Operations
				opcode = instructions >> 8;
					int literals = (instructions & 0x00FF);


					// ADDLW
					if((opcode & 0xFE) == 62) {
						dcFlagSetter(literals,w, true);

						if((w+literals) > 255){
							w = (w+literals)-256;
							CreateRegisters.statusRegItem.setText(7,"1");
						}else {
							w = w + literals;	
							CreateRegisters.statusRegItem.setText(7,"0");
						}

						wHex = Integer.toHexString(w);
						zFlagSetter(wHex);
						indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
					}

					//ANDLW
					if(opcode == 57) {
						w = (literals & w);
						wHex = Integer.toHexString(w);
						zFlagSetter(wHex);
						indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
					}

					//CLRWDT
					if(opcode == 100) {
						//TODO Watchdog
					}

					//RETFIE
					if(instructions == 9) {
						cyclesInMicroSeconds = 2;
					}

					//RETURN
					if(instructions == 8) {
						int sizeOfStack = callStackSaverAll.size()-1;
						indexOfInstructions = literalToHex(Integer.parseInt(callStackSaverAll.get(sizeOfStack),16)+1);
						callStackSaverAll.remove(sizeOfStack);
						Stack.setStackInput(callStackSaverAll);
						cyclesInMicroSeconds = 2;
					}

					//SLEEP
					if(opcode == 99) {

					}

					//IORLW
					if(opcode == 56) {
						w = (w | literals);
						wHex = Integer.toHexString(w);
						indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
						zFlagSetter(wHex);
					}

					//MOVLW
					if((opcode & 0xFE) == 48) {
						w = literals;
						wHex = Integer.toHexString(literals);
						indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
					}

					//RETLW
					if((opcode & 0xFE) == 52) {
						w = literals;
						wHex = Integer.toHexString(literals);
						//						indexOfInstructions = literalToHex((Integer.parseInt(callStackSaver,16)+1));

						int sizeOfStack = callStackSaverAll.size()-1;
						indexOfInstructions = literalToHex(Integer.parseInt(callStackSaverAll.get(sizeOfStack),16)+1);
						callStackSaverAll.remove(sizeOfStack);
						Stack.setStackInput(callStackSaverAll);
						cyclesInMicroSeconds = 2;
					}

					//SUBLW
					if((opcode & 0xFE) == 60) {
						dcFlagSetter(literals, w,false);
						if(literals-w < 0) {
							registerInputArray[address] = Integer.toHexString(256 + (Integer.parseInt(registerInputArray[address],16)-w));
							w = 256 + (literals-w);
							CreateRegisters.statusRegItem.setText(7,"0");
						} else {
							w = (literals - w);
							CreateRegisters.statusRegItem.setText(7,"1");
						}
						wHex = Integer.toHexString(w);
						zFlagSetter(wHex);
						indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));					
					}

					//XORLW
					if(opcode == 58) {
						w = (literals ^ w);
						wHex = Integer.toHexString(w);
						zFlagSetter(wHex);
						indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
					}



					//TODO
					// calculation for call & goto
					opcode = instructions >> 11;
						literals = (instructions & 0x07FF);
						//		literalsHex = literalToHex(literals);

						//call
						if(opcode == 4) {
							callStackSaver = indexOfInstructions;
							callStackSaverAll.add(callStackSaver);
							indexOfInstructions = literalToHex(literals);
							cyclesInMicroSeconds = 2;
							Stack.setStackInput(callStackSaverAll);
						}

						//goto
						if(opcode == 5) {
							indexOfInstructions = literalToHex(literals);
							cyclesInMicroSeconds = 2;
						}

						FillCodeTable.nextStepOnClick(indexOfInstructions.toUpperCase(), linesReadIn);
						registerInputArray[2] = indexOfInstructions.substring(2, 4);
						registerInputArray[3] = CreateRegisters.calculateStatus();
						//						registerInputArray[129] = CreateRegisters.calculateOption(CreateRegisters.optionBits);
						registerInputArray[129] = FillRegister.table.getItem(16).getText(2);
						registerInputArray[5] = FillRegister.table.getItem(0).getText(6);
						registerInputArray[6] = FillRegister.table.getItem(0).getText(7);
						registerInputArray[133] = FillRegister.table.getItem(16).getText(6);
						registerInputArray[134] = FillRegister.table.getItem(16).getText(7);

						CreateRegisters.optionToBinary(registerInputArray[129]);
						CreateRegisters.intconToBinary(registerInputArray[11]);
						registerInputArray[131] = registerInputArray[3];
						System.err.println("pcl: " + registerInputArray[2] + "; status: " + registerInputArray[3] + "; fsr: "+registerInputArray[4]+"; count: "+ registerInputArray[12] + 
								"; wHex: "+ wHex + "; z: " + CreateRegisters.statusRegItem.getText(5) + "; dc: "+ CreateRegisters.statusRegItem.getText(6) + "; c: "+ CreateRegisters.statusRegItem.getText(7));
						System.err.println("rb: " +registerInputArray[6]);

						FillRegister.generalValueSetterForRegister(3, registerInputArray[3].toUpperCase()); // Status 
						FillRegister.generalValueSetterForRegister(131, registerInputArray[3].toUpperCase());
						FillRegister.generalValueSetterForRegister(2, registerInputArray[2].toUpperCase()); // PCL
						FillRegister.generalValueSetterForRegister(4, registerInputArray[4].toUpperCase()); // FSR
						FillRegister.generalValueSetterForRegister(129, registerInputArray[129].toUpperCase());

						CreateRegisters.setStateValuesPclWFSR(registerInputArray);



						Timer0.checkForTimer0(registerInputArray[129]);

	}


	public static void dcFlagSetter(int litq, int wq, boolean addOrSub) {
		String wqbinary = new BigInteger(wq+"").toString(2);
		String litqbinary = new BigInteger(litq+"").toString(2);
		while(wqbinary.length() < 8) {
			wqbinary = "0"+wqbinary;
		}
		while(litqbinary.length() < 8) {
			litqbinary = "0"+litqbinary;
		}
		String wHinten = wqbinary.substring(4, 8);
		String litHinten = litqbinary.substring(4, 8);

		if(addOrSub == true) {
			if((Integer.parseInt(litHinten,2) + Integer.parseInt(wHinten,2)) > 15 ) {
				CreateRegisters.statusRegItem.setText(6,"1");
			} else {
				CreateRegisters.statusRegItem.setText(6,"0");
			}
		} else if(addOrSub == false) {
			if((Integer.parseInt(litHinten,2) - Integer.parseInt(wHinten,2)) < 16  && (Integer.parseInt(litHinten,2) - Integer.parseInt(wHinten,2)) != 0){
				CreateRegisters.statusRegItem.setText(6,"0");
			} else {
				CreateRegisters.statusRegItem.setText(6,"1");
			}
		}

	}


	public static void zFlagSetter(String givenValue) {
		int givenValueInt = Integer.parseInt(givenValue,16);
		if(givenValueInt == 0) {
			CreateRegisters.statusRegItem.setText(5,"1");
		} else if(givenValue.equals("00")) {
			CreateRegisters.statusRegItem.setText(5,"1");
		} else {
			CreateRegisters.statusRegItem.setText(5,"0");
		}
	}

	public static String literalToHex(int literal) {
		String literalToHex = Integer.toHexString(literal);
		while(literalToHex.length() < 4) {
			literalToHex = "0" + literalToHex;
		}
		return literalToHex;
	}

	public static int switchAdressDecider(int adress, int fsrVal) {
		switch (adress) {
		case 0x00: if(fsrVal != 0){
			adress = fsrVal;
		} else {
		}
		}
		return adress;
	}

	public static void startInstructionSetter(){
		startFlag = true;
	}

	public static void clearallVariablesOnReset() {
		callStackSaver = "";
		callStackSaverAll.clear();
		statusTest.clear();
		instructionsString = "";
		indexOfInstructions = "0000";
		setVarsOnReset();
		startInstructionSetter();
	}

	public static void setVarsOnReset() {
		registerInputArray[0] = "00";//0h
		registerInputArray[2] = "00";//2h
		if(Integer.parseInt(registerInputArray[3],16) < 24) { //3h Status
			registerInputArray[3] = "18";
		} else {
		}
		registerInputArray[10] = "00";//Ah
		registerInputArray[11] = BigInteger.valueOf(Integer.parseInt(registerInputArray[11],16)).toString(2).charAt(0)+"";

		registerInputArray[128] = "00";//80h
		registerInputArray[129] = "FF";//81h
		registerInputArray[130] = "00";//82h
		registerInputArray[131] = registerInputArray[3];//83h
		registerInputArray[133] = "1F";//85h
		registerInputArray[134] = "FF";//86h
		String aTest = BigInteger.valueOf(Integer.parseInt(registerInputArray[136],16)).toString(2);
		while(aTest.length() < 8) {
			aTest = "0" + aTest;
		}
		if(aTest.charAt(3) == 1) { //88h
			registerInputArray[136] = "8";
		} else {
			registerInputArray[136] = "0";
		}
	}
	
	public static void interruptCheck() {
		if(Integer.parseInt(CreateRegisters.intconBits[5]) == 1  && Integer.parseInt(CreateRegisters.intconBits[2]) == 1) {
			if(Integer.parseInt(CreateRegisters.intconBits[0]) == 1) {
				CreateRegisters.intconBits[0] = "0";
			}
		}
	}
}
