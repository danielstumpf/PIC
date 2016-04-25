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


	/**
	 * @param linesReadIn ein Array von Strings
	 */
	public static void workWithWorker(ArrayList<String> linesReadIn) {
		
		//ein Stringarray mit dem Namen addressArrayHex von der Größe des Arrays linesReadIn wird erstellt
		String[] addressArrayHex = new String[linesReadIn.size()];
		//ein Stringarray mit dem Namen instructionArrayHex von der Größe des Arrays linesReadIn wird erstellt
		String[] instructionArrayHex = new String[linesReadIn.size()];



		
		for(int u = 0; u < registerInputArray.length; u++){
			
			//wenn das Zeichen an der ersten betrachteten Stelle "null" ist (also wenn es gerade erst initialisiert wurde)
			if(registerInputArray[u] == null) {
				
				//dann fülle den ganzen String mit Nullen
				Arrays.fill(registerInputArray, "00");
			}
		}

		//die Integervariable cyclesInMicroSeconds wird mit 1 gefüllt
		cyclesInMicroSeconds = 1;

		//solange Variable i kleiner als die Größe des Arrays linesReadIn ist
		for(int i = 0; i < linesReadIn.size(); i++){  
		
			//schreibe die ersten vier Zeichen des Strings an der Stelle i des Arrays linesReadIn in den String des Arrays addressArrayHex an der Stelle i
			addressArrayHex[i] = linesReadIn.get(i).substring(0, 4); 
			//schreibe die letzten vier Zeichen des Strings an der Stelle i des Arrays linesReadIn in den String des Arrays instructionArrayHex an der Stelle i
			instructionArrayHex[i] = linesReadIn.get(i).substring(5, 9); 
		}




		//TODO if adressArrayHex[i].equals does not find one match, it starts from "0000"
		//start where addressHex = 0000
		//durchlaufe das Array addressArrayHex
		for(int i = 0; i < addressArrayHex.length; i++) {
			
			//wenn addressArrayHex an der aktuell betrachteten Stelle dem String "0000" entspricht
			if(addressArrayHex[i].equals("0000")) { 
				//System.err.println("start setted");
				//überschreibe die Integervariable indexOfstartAddressOfAdressArray mit dem aktuellen i-Wert
				indexOfstartAddressOfAdressArray = i; 
				//überschreibe den String instructionsString mit dem String aus instructionArrayHex an der Stelle i
				instructionsString = instructionArrayHex[indexOfstartAddressOfAdressArray];
				//überschreibe den String startInstruction mit demselben Wert wie instructionsString				
				startInstruction = instructionArrayHex[indexOfstartAddressOfAdressArray]; 
				
			//wenn addressArrayHex an der aktuell betrachteten Stelle dem Wert von indexOfInstructions in Großbuchstaben entspricht
			} else if(addressArrayHex[i].equals(indexOfInstructions.toUpperCase())) { 
				
				//dann überschreibe den String instructionsString mit dem String aus instructionArrayHex an der Stelle i
				instructionsString = instructionArrayHex[i]; 
				//dann beende die for-Schleife
				break; 
			}
		}

		//wenn das startFlag auf true gesetzt ist
		if(startFlag == true) { 
		
			//instructionsString wird mit startInstruction überschrieben (beides sind Strings)
			instructionsString = startInstruction; 
			//startFlag wird auf false gesetzt
			startFlag = false; 
		} 

		//ein Integerwert (2 Byte) wird initialisert. Und das indem instructionsString von Hex in Dec umgewandelt
		int instructions = Integer.parseInt(instructionsString,16); 

		//Byte-Oriented File Register Operations
		//die 2 Byte Instruktionen werden hier getrennt. OpCode sind die 4 Bits 9-12 (xx OOOO xxxx xxxx). Dieser bestimmt den Befehl
		int opcode = instructions >> 8; 
		//unter destination wird das Bit an der Stelle 8 (von rechts) einzeln abgespeichert (xx xxxx Dxxx xxxx)
		int destination = (instructions & 0x0080) >> 7; 
		//unter address werden die letzten 7 Bits abgespeichert (xx xxxx xAAA AAAA)
		int address = (instructions & 0x007F); 
		
		//wenn das 2. Statusregister-Bit '1' ist
		if(CreateStateRegister.statusRegItem.getText(2).equals("1")) {
			
			//dann addiere zur Adresse 128 dazu
			address = 128 + address;	
		}

		//Flags C,DC,Z
		/*
		 * ADDWF (bekommt f,d - 00 0111 dfff ffff)
		 * addiere Inhalt vom W-Register mit Inhalt von Register 'f'
		 */
		if(opcode == 7) {

			//speichere Ergebnis in W-Register
			if(destination == 0) { 
			
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				//dcFlagSetter mit dem Integerwert von registerInputArray an der Stelle 'address', 'w' und 'true' => (nur für das Flag) w 
				dcFlagSetter(Integer.parseInt(registerInputArray[address],16),w,true); und f werden verrechnet, bei Überlauf wird das Overflowbit gesetzt
				
				//wenn f und w zusammen größer als 255 sind
				if((Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16)) > 255){ 
				
					//addiere beide aber "setze zurück" (also -256)
					w = (Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16)) - 256; 
					//Status-Register: C
					//im Statusregister wird das 7. Bit gesetzt
					CreateStateRegister.statusRegItem.setText(7, "1"); 
				} else {
					
					//wenn kein Überlauf passiert, dann verrechne beide normal
					w = Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16); 
					//im Statusregister wird das 7. Bit rückgesetzt
					CreateStateRegister.statusRegItem.setText(7, "0"); 
				}
				
				//mache aus dem Decimalwert von w wieder einen Hex-Wert
				wHex = Integer.toHexString(w); 
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(wHex); 

			//speichere Ergebnis in 'f'-Register
			} else if (destination == 1) { 
			
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//dcFlagSetter mit dem Integerwert von registerInputArray an der Stelle 'address', 'w' und 'true' => (nur für das Flag) w und f werden verrechnet, bei Überlauf wird das Overflowbit gesetzt
				dcFlagSetter(Integer.parseInt(registerInputArray[address],16),w,true); 
				
				//wenn f und w zusammen größer als 255 sind
				if((Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16)) > 255) { 
				
					//addiere beide aber "setze zurück" (also -256) und speichere als Hex-Wert
					registerInputArray[address] = 
							Integer.toHexString((Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16)) - 256); 
					//im Statusregister wird das 7. Bit gesetzt
					CreateStateRegister.statusRegItem.setText(7, "1");
					
				} else {
					
					//wenn kein Überlauf passiert, dann verrechne beide normal und speichere als Hex-Wert
					registerInputArray[address] = 
							Integer.toHexString(Integer.parseInt(registerInputArray[address],16) + Integer.parseInt(wHex,16)); 
					//im Statusregister wird das 7. Bit rückgesetzt
					CreateStateRegister.statusRegItem.setText(7, "0"); 
				}
				
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(registerInputArray[address]); 
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
			
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 

		}

		/*
		 * ANDWF (bekommt f,d - 00 0101 dfff ffff)
		 * verunde den Inhalt vom W-Register mit dem Inhalt von 'f'
		 */
		if(opcode == 5) {

			//speichere Ergebnis im W-Register
			if(destination == 0){ 
			
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//verunde die Hex-Werte von 'w' und 'f' und speichere als Hex-Wert
				wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) & Integer.parseInt(wHex,16)); 
				//übersetze in eine Dezimalzahl
				w = Integer.parseInt(wHex,16); 
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(wHex); 
				
			//speichere Ergebnis im 'f'-Register
			} else if(destination == 1) { 
			
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//verunde die Hex-Werte von 'w' und 'f' und speichere als Hex-Wert
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) & Integer.parseInt(wHex,16)); 
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(registerInputArray[address]); 
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
			
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}

		/*
		 * CLRF (bekommt f - 00 0001 1fff ffff)
		 * der Inhalt vom 'f'-Register wird gelöscht und das Z-Bit gesetzt
		 */
		if(opcode == 1 && destination == 1) {
			
			//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
			address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
			//setze den Inhalt von registerInputArray an der Stelle 'f' zurück auf '00'
			registerInputArray[address] = "00"; 
			//zFlagsetter mit dem Hex-Ergebnis -> da Null, wird das 5. Bit im Statusregister gesetzt
			zFlagSetter(registerInputArray[address]); 
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
			//ändere den Wert auf der Oberfläche in der Tabelle
			FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
		}

		/*
		 * CLRW (bekommt nichts - 00 0001 0xxx xxxx)
		 * der Inhalt vom W-Register wird gelöscht und das Z-Bit gesetzt
		 */
		if(opcode == 1 && destination == 0) {
			
			//setze 'w' zurück auf '0'
			w = 0; 
			//setze den Hex-Wert von 'w' zurück auf '00'
			wHex = "00"; 
			//zFlagsetter mit dem Hex-Ergebnis -> da Null, wird das 5. Bit im Statusregister gesetzt
			zFlagSetter(wHex); 
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
			//ändere den Wert auf der Oberfläche in der Tabelle
			FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
		}

		//address switchAdressdestination
		/*
		 * COMF (bekommt f,d - 00 1001 dfff ffff)
		 * der Inhalt von 'f' wird komplementiert
		 */
		if(opcode == 9) {
			
			//speichere Ergebnis in W-Register
			if(destination==0){ 
				/*w = (~Integer.parseInt(registerInputArray[address],16));*/
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//negiere den Input-Wert und speichere ihn als Hex-Wert
				wHex = Integer.toHexString(~Integer.parseInt(registerInputArray[address],16)).substring(6, 8);
				//speichere das Hex-Ergebnis als Dezimalzahl
				w = Integer.parseInt(wHex,16);
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(wHex); 
				
			//speichere Ergebnis in 'f'-Register
			} else if(destination ==1){ 
			
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//negiere den Input-Wert und speichere ihn als Hex-Wert
				registerInputArray[address] = (Integer.toHexString(~Integer.parseInt(registerInputArray[address],16)).substring(6, 8));
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(registerInputArray[address]); 
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}

			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}

		/*
		 * DECF (bekommt f,d - 00 0011 dfff ffff)
		 * dekrementiere den Inhalt von Register 'f'
		 */
		if(opcode == 3) {
			
			//speichere den Inhalt im W-Register
			if(destination == 0) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//verringere den Hex-Wert von in 'f' um 1
				wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)-1);
				//übersetze das Hex-Ergebnis in eine Dezimalzahl
				w = Integer.parseInt(wHex,16);
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(wHex); 
				
			//speichere den Inhalt im 'f'-Register
			} else if(destination == 1) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//verringere den Hex-Wert von in 'f' um 1
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)-1);
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(registerInputArray[address]); 
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}

			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}

		//defcz f,d <- d
		/*
		 * DECFSZ (bekommt f,d - 00 1011 dfff ffff)
		 * der Inhalt von Register 'f' wird dekrementiert
		 * ist das Ergebnis 1, wird die nächste Instruktion ausgeführt
		 * ist das Ergebnis 0, wird ein NOP ausgeführt
		 */
		if(opcode == 11) {
			
			//speichere das Ergebnis im W-Register
			if(destination ==  0){
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//verringere den Inhalt von 'f' um 1 und speichere ihn als Hex-Wert
				wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)-1);
				//übersetze das Hex-Ergebnis in eine Dezimalzahl
				w = Integer.parseInt(wHex,16);
				
				//ist das Ergebnis gleich Null
				if(Integer.parseInt(wHex,16) == 0) {
					
					//überschreibe die Integervariable cyclesInMicroSeconds mit 2
					cyclesInMicroSeconds = 2;
					//erhöhe den Indexwert der Instruktionen um 2
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2)); 
				
				//ist das Ergebnis gleich Eins
				} else {
					
					//erhöhe den Indexwert der Instruktionen um 1
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
				}
				
			//speichere das Ergebnis im 'f'-Register
			}else if(destination == 1){
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//verringere den Inhalt von 'f' um 1 und speichere ihn als Hex-Wert
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)-1);	
				
				//ist das Ergebnis gleich Null
				if(Integer.parseInt(registerInputArray[address],16) == 0) {
					
					//überschreibe die Integervariable cyclesInMicroSeconds mit 2
					cyclesInMicroSeconds = 2;
					//erhöhe den Indexwert der Instruktionen um 2
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2)); 
				
				//ist das Ergebnis gleich Eins
				} else {
					
					//erhöhe den Indexwert der Instruktionen um 1
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
				}
				
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
		}

		/*
		 * INCF (bekommt f,d - 00 1010 dfff ffff)
		 * der Inhalt vom Register 'f' wird inkrementiert
		 */
		if(opcode == 10) {
			
			//speichere das Ergebnis im W-Register
			if(destination == 0) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16));
				
				//wenn der Dec-Wert von 'f'+1 größer ist als 255
				if((Integer.parseInt(registerInputArray[address],16)+1)  > 255) {
					
					//"setze 'f'+1 zurück" (also -256) und speichere als Hex-Wert
					wHex = Integer.toHexString((Integer.parseInt(registerInputArray[address],16) + 1) - 256);
				} //TODO muss hier noch ein bit gesetzt werden??? 
				
				//wenn der Dec-Wert von 'f'+1 kleiner oder gleich 255 ist
				else {
					
					//inkrementiere 'f' und speichere als Hex-Wert
					wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)+1);	
				}
				
				//übersetze das Hex-Ergebnis in eine Dezimalzahl
				w = Integer.parseInt(wHex,16);
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(wHex); 
			
			//speichere das Ergebnis im 'f'-Register
			}else if(destination ==1){
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				
				//ist der Dec-Wert von 'f'+1 größer als 255
				if((Integer.parseInt(registerInputArray[address],16)+1)  > 255) {
					
					//"setze 'f'+1 zurück" (also -256) und speichere als Hex-Wert
					registerInputArray[address] = Integer.toHexString((Integer.parseInt(registerInputArray[address],16) + 1) - 256);
				} //TODO bit setzen??? 
				
				//ist der Dec-Wert von 'f'+1 kleiner oder gleich 255
				else {
					
					//inkrementiere 'f' und speichere als Hex-Wert
					registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)+1);
				}
				
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(registerInputArray[address]); 
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
			
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}

		/*
		 * INCFSZ (bekommt f,d - 00 1111 dfff ffff)
		 * inkrementiere den Inhalt von Register 'f'
		 * ist das Ergebnis 1, wird die nächste Instruktion ausgeführt
		 * ist das Ergebnis 0, wird ein NOP ausgeführt
		 */
		if(opcode == 15) {
			
			//speichere Ergebnis im W-Register
			if(destination == 0) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				
				//wenn der Dec-Wert von 'f' 255 entspricht
				if(Integer.parseInt(registerInputArray[address],16) == 255) {
					
					//setze den Hex-Wert von 'w' auf '00' zurück
					wHex = "00";
				
				//wenn der Dec-Wert von 'f' nicht 255 entspricht
				} else {
					
					//inkrementiere den Inhalt von 'f' und speichere als Hex-Wert
					wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) + 1);
				}				
				
				//übersetze das Hex-Ergebnis in eine Dezimalzahl
				w = Integer.parseInt(wHex,16);
				
				//wenn das Hex-Ergebnis '00' entspricht, also ein Überlauf stattgefunden hat
				if(Integer.parseInt(wHex,16) == 0) {
					
					//setze die Integer cyclesInMicroSeconds auf 2
					cyclesInMicroSeconds = 2;
					//erhöhe den Indexwert der Instruktionen um 2
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2)); 
				
				//wenn das Hex-Ergebnis nicht '00' entspricht, also kein Überlauf stattgefunden hat
				} else {
					
					//erhöhe den Indexwert der Instruktionen um 1
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
				}
			
			//speichere Ergebnis im 'f'-Register
			} else if(destination == 1) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				
				//wenn der Dec-Wert von 'f' 255 entspricht
				if(Integer.parseInt(registerInputArray[address],16) == 255) {
					
					//setze den Hex-Wert von 'f' auf '00' zurück
					registerInputArray[address] = "00";
				
				//wenn der Dec-Wert von 'f' nicht 255 entspricht
				} else {
					
					//inkrementiere den Inhalt von 'f' und speichere als Hex-Wert
					registerInputArray[address]  = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) + 1);
				}
				
				//geben den Fehlertext "nummer 21:" und 'f' aus 
				System.err.println("nummer 21: " + registerInputArray[address]);
				
				//wenn das Hex-Ergebnis '00' entspricht, also ein Überlauf stattgefunden hat
				if(Integer.parseInt(registerInputArray[address],16) == 0) {
					
					//setze die Integer cyclesInMicroSeconds auf 2
					cyclesInMicroSeconds = 2;
					//erhöhe den Indexwert der Instruktionen um 2
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2)); 
				
				//wenn das Hex-Ergebnis nicht '00' entspricht, also kein Überlauf stattgefunden hat
				} else {
					
					//erhöhe den Indexwert der Instruktionen um 1
					indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
				}
				
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
		}

		/*
		 * IORWF (bekommt f,d - 00 0100 dfff ffff)
		 * inklusives oder auf W-Register mit 'f'-Register
		 */
		if(opcode == 4) {
			
			//speichere Ergebnis im W-Register
			if(destination == 0) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//verodere die Dec-Werte von 'w' und 'f' und speichere als Hex-Wert
				wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) | Integer.parseInt(wHex,16));
				//übersetze das Hex-Ergebnis in eine Dezimalzahl
				w = Integer.parseInt(wHex,16);
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(wHex); 
			
			//speichere Ergebnis im 'f'-Register
			} else if(destination == 1) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//verodere die Dec-Werte von 'w' und 'f' und speichere als Hex-Wert
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) | Integer.parseInt(wHex,16));
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(registerInputArray[address]); 
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
			
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}

		/*
		 * MOVF (bekommt f,d - 00 1000 dfff ffff)
		 * der Inhalt  von Register 'f' wird abhängig von 'd' an eine Zieladresse kopiert
		 */
		if(opcode == 8) {
			
			//speichere im W-Register
			if(destination == 0) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//schreibe den Hex-Wert aus 'f' in 'w'
				wHex = registerInputArray[address];
				//übersetze den Hex-Wert in eine Dezimalzahl
				w = Integer.parseInt(wHex,16);
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(wHex); 
			
			//speichere im 'f'-Register
			} else if(destination == 1) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//schreibe den Inhalt von 'f' in 'f'
				registerInputArray[address] = registerInputArray[address];
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(registerInputArray[address]); 
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
			
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}

		/*
		 * MOVWF (bekommt f - 00 0000 1fff ffff)
		 * schreibe den Inhalt vom W-Register in das 'f'-Register
		 */
		if(opcode == 0 && destination == 1) {
			
			//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
			address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
			//schreibe den Inhalt von 'w' in 'f'
			registerInputArray[address] = wHex;
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
			//ändere den Wert auf der Oberfläche in der Tabelle
			FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
		}

		/*
		 * NOP (bekommt nichts - 00 0000 0xx0 0000)
		 * keine Operation
		 */
		if(opcode == 0 && destination == 0) {
			
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1));
		}

		/*
		 * RLF (bekommt f,d - 00 1101 dfff ffff)
		 * der Inhalt von Register 'f' wird ein Bit nach links rotiert durch das Carry Flag
		 */
		if(opcode == 13) {
			
			//speichere Ergebnis im W-Register
			if(destination == 0) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				//erstelle einen neuen String, in den der Binärwert von 'f' als String geschrieben wird
				String binary = new BigInteger(registerInputArray[address],16).toString(2);
				
				//fülle binary auf 8 Bit auf
				while(binary.length() < 8) {
					
					//und das vorne mit Nullen
					binary = "0"+binary;
				}
				
				// erste möglichkeit
				//erstelle ein Char-Array, in das die einzelnen Stellen von binary abgelegt werden
				char[] cAry = binary.toCharArray();
				//erstelle eine Integervariable temp, in die das erste Bit (von binary, das an der Stelle 0 in cAry liegt) geschrieben wird
				int temp = Integer.parseInt(cAry[0]+"");
				//setze das erste Bit auf '0' zurück
				cAry[0] = '0';
				
				//wandere durch das Array
				for(int u = 0; u < cAry.length-1; u++){
					
					//schreibe den Inhalt von einem Feld weiter rechts in das linke
					cAry[u] = cAry[u+1];
				}
				
				
				cAry[7] = CreateStateRegister.statusRegItem.getText(7).charAt(0);
				
				String backtogether = "";
				
				
				for(int i = 0; i < cAry.length; i++) {
					
					
					backtogether = backtogether +cAry[i];
				}
				
				
				CreateStateRegister.statusRegItem.setText(7, temp+"");
				
				wHex = Integer.toHexString(Integer.parseInt(backtogether,2));
				
				w = Integer.parseInt(wHex,16);

			//speichere Ergebnis im 'f'-Register
			} else if(destination == 1) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
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
				
				
				cAry[7] = CreateStateRegister.statusRegItem.getText(7).charAt(0);
				
				String backtogether = "";
				
				
				for(int i = 0; i < cAry.length; i++) {
					
					
					backtogether = backtogether +cAry[i];
				}
				
				
				CreateStateRegister.statusRegItem.setText(7,temp+"");		
				
				registerInputArray[address] =  Integer.toHexString(Integer.parseInt(backtogether,2));
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
			
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}

		//RRF
		if(opcode == 12) {
			
			
			if(destination == 0) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
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
				
				
				cAry[0] = CreateStateRegister.statusRegItem.getText(7).charAt(0);
				
				String backtogether = "";
				
				
				for(int i = 0; i < cAry.length; i++) {
					
					
					backtogether = backtogether +cAry[i];
				}
				
				
				CreateStateRegister.statusRegItem.setText(7, temp+"");
				
				wHex = Integer.toHexString(Integer.parseInt(backtogether,2));
				
				w = Integer.parseInt(wHex,16);
			
			
			} else if(destination == 1) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
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
				
				
				cAry[0] = CreateStateRegister.statusRegItem.getText(7).charAt(0);
				
				String backtogether = "";
				
				
				for(int i = 0; i < cAry.length; i++) {
					
					
					backtogether = backtogether +cAry[i];
				}
				
				
				CreateStateRegister.statusRegItem.setText(7,temp+"");		
				
				registerInputArray[address] =  Integer.toHexString(Integer.parseInt(backtogether,2));
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
			
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}

		//flags c, dc , z
		//SUBWF
		if(opcode == 2) {
			
			
			System.err.println();
			
			
			if(destination == 0) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				
				dcFlagSetter(Integer.parseInt(registerInputArray[address],16), w, false);
				
				// wenn  w kleiner null (negat überlauf)
				if((Integer.parseInt(registerInputArray[address],16)-w) < 0) { 
					
				
					w = 256+(Integer.parseInt(registerInputArray[address],16)-w);
					
					CreateStateRegister.statusRegItem.setText(7,"0");
				
				
				} else {
					
					
					w = Integer.parseInt(registerInputArray[address],16)-w;
					
					CreateStateRegister.statusRegItem.setText(7,"1");
				}

				
				wHex = Integer.toHexString(w);
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(wHex); 

			
			} else if(destination ==1) {
				
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				
				dcFlagSetter(Integer.parseInt(registerInputArray[address],16), w, false);
				
				
				if((Integer.parseInt(registerInputArray[address],16)-w) < 0) {
					
					
					registerInputArray[address] = Integer.toHexString(256 + (Integer.parseInt(registerInputArray[address],16)-w));
					
					CreateStateRegister.statusRegItem.setText(7,"0");
				
				
				} else {
					
					
					registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16)-w);
					
					CreateStateRegister.statusRegItem.setText(7,"1");
				}

				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(registerInputArray[address]); 
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
			
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}

		//SWAPF
		if(opcode == 14) {
			
			
			if(destination == 0) {
			
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
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
			
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				
				String binary = new BigInteger(registerInputArray[address],16).toString(2);
				
				
				while(binary.length() < 8) {
				

					binary = "0" + binary;
				}
				
				
				String vordere4Bit = binary.substring(0, 4);
				
				String hintere4Bit = binary.substring(4, 8);
				
				binary = hintere4Bit +""+ vordere4Bit;
				
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(binary,2));
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
			
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}

		//XORWF
		if(opcode == 6) {
			
			
			if(destination == 0) {
			
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				
				wHex = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) ^ Integer.parseInt(wHex,16));
				
				w = Integer.parseInt(wHex,16);
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(wHex); 

			
			} else if(destination == 1) {
			
				//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
				address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
				
				registerInputArray[address] = Integer.toHexString(Integer.parseInt(registerInputArray[address],16) ^ Integer.parseInt(wHex,16));
				//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
				zFlagSetter(registerInputArray[address]); 
				//ändere den Wert auf der Oberfläche in der Tabelle
				FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
			}
			
			//erhöhe den Indexwert der Instruktionen um 1
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

			//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
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
			

				CreateStateRegister.switchStatusClear(bit);
			}
		
			//					if(address == 129) {
			//						CreateStateRegister.checkOneOrZero(CreateStateRegister.optionBits[bit]);
			//					}


			registerInputArray[address] = Integer.toHexString(Integer.parseInt(backtogether,2));
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
			//ändere den Wert auf der Oberfläche in der Tabelle
			FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
		}

		
		//TODO Status bits die namen verteilen, zuweisen, dass bit nr 5 z.b = rp0 
		// hier wird rp0 zum Test per hand gesetzt
		//BSF
		if(opcode == 5) {

			//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
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
			

				CreateStateRegister.switchStatusSet(bit);
			}
		

			if(address == 129) {
			

				CreateStateRegister.checkOneOrZero(CreateStateRegister.optionBits[bit]);
			}

		
			registerInputArray[address] = Integer.toHexString(Integer.parseInt(backtogether,2));
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
			//ändere den Wert auf der Oberfläche in der Tabelle
			FillRegister.generalValueSetterForRegister(address, registerInputArray[address]); 
		}


		//TODO zero flag check bearbeiten!!!!
		//BTFSC
		if(opcode == 6) {
		
			//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
			address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
			
			String binary = new BigInteger(registerInputArray[address],16).toString(2);


			while(binary.length() < 8) {


				binary = "0"+binary;
			}
		

			char[] a = binary.toCharArray();
			
			bit= 7-bit;


			if(a[bit] == '0'){


				cyclesInMicroSeconds = 2;
				//erhöhe den Indexwert der Instruktionen um 2
				indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2)); 


			} else {

				//erhöhe den Indexwert der Instruktionen um 1
				indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
			}
		}

		//BTFSS
		if(opcode == 7) {
		
			//switchAdressDecider mit 'f' und dem Integerwert von registerInputArray an der Stelle 4
			address = switchAdressDecider(address, Integer.parseInt(registerInputArray[4],16)); 
			
			String binary = new BigInteger(registerInputArray[address],16).toString(2);


			while(binary.length() < 8) {


				binary = "0"+binary;
			}
		

			char[] a = binary.toCharArray();
			
			bit = 7-bit;


			if(a[bit] == '1'){


				cyclesInMicroSeconds = 2;
				//erhöhe den Indexwert der Instruktionen um 2
				indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+2)); 


			} else {

				//erhöhe den Indexwert der Instruktionen um 1
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
				
				CreateStateRegister.statusRegItem.setText(7,"1");


			}else {


				w = w + literals;	
				
				CreateStateRegister.statusRegItem.setText(7,"0");
			}


			wHex = Integer.toHexString(w);
			//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
			zFlagSetter(wHex); 
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}


		//ANDLW
		if(opcode == 57) {


			w = (literals & w);
			
			wHex = Integer.toHexString(w);
			//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
			zFlagSetter(wHex); 
			//erhöhe den Indexwert der Instruktionen um 1
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
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
			//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
			zFlagSetter(wHex); 
		}


		//MOVLW
		if((opcode & 0xFE) == 48) {


			w = literals;
			
			wHex = Integer.toHexString(literals);
			//erhöhe den Indexwert der Instruktionen um 1
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

				CreateStateRegister.statusRegItem.setText(7,"0");


			} else {


				w = (literals - w);
				
				CreateStateRegister.statusRegItem.setText(7,"1");
			}


			wHex = Integer.toHexString(w);
			//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
			zFlagSetter(wHex); 
			//erhöhe den Indexwert der Instruktionen um 1
			indexOfInstructions = literalToHex((Integer.parseInt(indexOfInstructions,16)+1)); 
		}


		//XORLW
		if(opcode == 58) {


			w = (literals ^ w);
			
			wHex = Integer.toHexString(w);
			//zFlagsetter mit dem Hex-Ergebnis -> bei Null wird das 5. Bit im Statusregister gesetzt und sonst rückgesetzt
			zFlagSetter(wHex); 
			//erhöhe den Indexwert der Instruktionen um 1
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


		CreateCodeTable.nextStepOnClick(indexOfInstructions.toUpperCase(), linesReadIn);

		registerInputArray[2] = indexOfInstructions.substring(2, 4);

		registerInputArray[3] = CreateStateRegister.calculateStatus();

		//						registerInputArray[129] = CreateStateRegister.calculateOption(CreateStateRegister.optionBits);

		registerInputArray[129] = FillRegister.table.getItem(16).getText(2);

		registerInputArray[5] = FillRegister.table.getItem(0).getText(6);

		registerInputArray[6] = FillRegister.table.getItem(0).getText(7);

		registerInputArray[133] = FillRegister.table.getItem(16).getText(6);

		registerInputArray[134] = FillRegister.table.getItem(16).getText(7);


		CreateStateRegister.optionToBinary(registerInputArray[129]);

		CreateStateRegister.intconToBinary(registerInputArray[11]);

		registerInputArray[131] = registerInputArray[3];

		System.err.println("pcl: " + registerInputArray[2] + "; status: " + registerInputArray[3] + "; fsr: "+registerInputArray[4]+"; count: "+ registerInputArray[12] + "; wHex: "+ wHex + "; z: " + CreateStateRegister.statusRegItem.getText(5) + "; dc: "+ CreateStateRegister.statusRegItem.getText(6) + "; c: "+ CreateStateRegister.statusRegItem.getText(7));

		System.err.println("rb: " +registerInputArray[6]);


		FillRegister.generalValueSetterForRegister(3, registerInputArray[3].toUpperCase()); // Status 

		FillRegister.generalValueSetterForRegister(131, registerInputArray[3].toUpperCase());

		FillRegister.generalValueSetterForRegister(2, registerInputArray[2].toUpperCase()); // PCL

		FillRegister.generalValueSetterForRegister(4, registerInputArray[4].toUpperCase()); // FSR

		FillRegister.generalValueSetterForRegister(129, registerInputArray[129].toUpperCase());


		CreateStateRegister.setStateValuesPclWFSR(registerInputArray);




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
				CreateStateRegister.statusRegItem.setText(6,"1");
			} else {
				CreateStateRegister.statusRegItem.setText(6,"0");
			}
		} else if(addOrSub == false) {
			if((Integer.parseInt(litHinten,2) - Integer.parseInt(wHinten,2)) < 16  && (Integer.parseInt(litHinten,2) - Integer.parseInt(wHinten,2)) != 0){
				CreateStateRegister.statusRegItem.setText(6,"0");
			} else {
				CreateStateRegister.statusRegItem.setText(6,"1");
			}
		}

	}


	public static void zFlagSetter(String givenValue) {
		int givenValueInt = Integer.parseInt(givenValue,16);
		if(givenValueInt == 0) {
			CreateStateRegister.statusRegItem.setText(5,"1");
		} else if(givenValue.equals("00")) {
			CreateStateRegister.statusRegItem.setText(5,"1");
		} else {
			CreateStateRegister.statusRegItem.setText(5,"0");
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
		if(Integer.parseInt(CreateStateRegister.intconBits[5]) == 1  && Integer.parseInt(CreateStateRegister.intconBits[2]) == 1) {
			if(Integer.parseInt(CreateStateRegister.intconBits[0]) == 1) {
				CreateStateRegister.intconBits[0] = "0";
			}
		}
	}
}
