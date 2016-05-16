package dhbw.sysnprog.pic.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Model des PIC-Simulator. Führt sämtliche Funktionen des Simulators durch,
 * unabhängig von der Benutzeroberfläche
 *
 * @author Daniel
 *
 *
 */
public class PicSimModel {

	/**
	 * Inhalt des W-Registers
	 */
	public int registerW = 0b0;
	/**
	 * Sprungadresse
	 */
	public int sprung;
	/**
	 * Laufzeit
	 */
	private long runningTime = 0;
	/**
	 * Startzeit
	 */
	private long startTime = 0;
	/**
	 * Stack
	 */
	public Deque<Integer> StackPC = new ArrayDeque<Integer>();
	/**
	 * Speicherinhalt
	 */
	public int[] registerArray = new int[256];
	/**
	 * Befehlsliste
	 */
	public List<Integer> codeList = new ArrayList<Integer>();
	private int takt;
	private String programFilePath;
	private int steps;

	private int portA;
	private int portB;
	private int prescaler;
	/**
	 * true = counter-Modus false = timer-Modus
	 */
	private boolean mode;

	/**
	 * Standardkonstruktor
	 */
	public PicSimModel() {
	}

	/**
	 * Überprüft ein einzelnes Bit an bestimmer Speicheradresse
	 *
	 * @param position
	 *            Gibt das Bit an, welches überprüft wird.
	 * @param adress
	 *            Gibt die Adresse an, an der sich das Bit befindet.
	 *
	 * @return boolean, true, wenn das Bit gesetzt ist. False, wenn nicht.
	 */
	public boolean checkBitSet(int position, int adress) {
		switch (position) {
		case 0: {
			final int d = getRegisterEntry(adress) & 0b00000001;
			if (d == 1) {
				return true;
			} else {
				return false;
			}
		}
		case 1: {
			final int d = getRegisterEntry(adress) & 0b00000010;
			if (d == 2) {
				return true;
			} else {
				return false;
			}
		}
		case 2: {
			final int d = getRegisterEntry(adress) & 0b00000100;
			if (d == 4) {
				return true;
			} else {
				return false;
			}
		}
		case 3: {
			final int d = getRegisterEntry(adress) & 0b00001000;
			if (d == 8) {
				return true;
			} else {
				return false;
			}
		}
		case 4: {
			final int d = getRegisterEntry(adress) & 0b00010000;
			if (d == 16) {
				return true;
			} else {
				return false;
			}
		}
		case 5: {
			final int d = getRegisterEntry(adress) & 0b00100000;
			if (d == 32) {
				return true;
			} else {
				return false;
			}
		}
		case 6: {
			final int d = getRegisterEntry(adress) & 0b01000000;
			if (d == 64) {
				return true;
			} else {
				return false;
			}
		}
		case 7: {
			final int d = getRegisterEntry(adress) & 0b10000000;
			if (d == 128) {
				return true;
			} else {
				return false;
			}
		}
		default:
			return false;
		}
	}

	/**
	 * Extrahiert aus einer Programmzeile den Befehlscode und speichert ihn in
	 * der CodeList ab.
	 *
	 * @param codeLine
	 *            String einzelne Zeile des Programms
	 *
	 */
	public void checkCode(String codeLine) {
		final String codeLineTmp = codeLine;
		final StringBuffer buffer = new StringBuffer(codeLineTmp);
		int i;
		for (i = 0; i < 5; i++) {
			buffer.deleteCharAt(0);
		}
		final StringBuilder builder = new StringBuilder();
		for (i = 0; i < 4; i++) {
			builder.append(buffer.charAt(i));
		}
		final int code = Integer.parseInt(builder.toString(), 16);
		setCode(code);
	}

	/**
	 * Löschen des Stacks
	 */
	public void cleanStack() {
		StackPC.clear();
	}

	/**
	 * Löschen des W-Registers
	 */
	public void cleanWReg() {
		registerW = 0;
	}

	/**
	 * Löscht ein einzelnes Bit an bestimmer Speicheradresse
	 *
	 * @param position
	 *            Gibt das Bit an, welches gelöscht wird.
	 * @param adress
	 *            Gibt die Adresse an, an der das Bit gelöscht wird.
	 */
	public void clearBit(int position, int adress) {
		switch (position) {
		case 1: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11111110));
			break;
		}
		case 2: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11111101));
			break;
		}
		case 3: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11111011));
			break;
		}
		case 4: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11110111));
			break;
		}
		case 5: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11101111));
			break;
		}
		case 6: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11011111));
			break;
		}
		case 7: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b10111111));
			break;
		}
		case 8: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b01111111));
			break;
		}
		default: {
		}
		}
	}

	/**
	 * The content of the w-register are added to the eight bit literal 'hex4'
	 * and the result is placed in the w-register
	 *
	 * @param lit_K
	 *
	 */
	private void do_addlw(int lit_K) {
		final int lit_K_temp = lit_K & 0b011111111;
		int value = lit_K_temp + registerW;
		if (value > 255) {
			setC(true);
			value = value & 0xFF;
		} else {
			setC(false);
		}
		if ((((lit_K_temp & 0b1000) + (registerW & 0b1000)) == 16)) {
			setDC(true);
		} else {
			setDC(false);
		}
		if (value == 0) {
			setZ(true);
		} else {
			setZ(false);
		}
		registerW = value;
	}

	/**
	 * add the contents of the W-register with register 'f' if 'd' is 0 the
	 * result is stored in the W-Register. If 'd' is 1, the result is stored
	 * back in the register 'f'
	 *
	 * @param d_f
	 *
	 */
	private void do_addwf(int d_f) {
		final int d = d_f & 0b10000000;
		final int adress = d_f & 0b01111111;
		final int value = getRegisterEntry(adress);
		final int result = registerW + value;
		if ((0xFF < result) && !checkBitSet(0, 2)) {
			setC(true);
		} else {
			setC(false);
		}
		if (0xF < ((registerW & 0xF) + (value & 0xF))) {
			setDC(true);
		} else {
			setDC(false);
		}
		// result = result & 0xFF;
		if (0 == d) {
			registerW = result;
		} else {
			setRegisterEntry(adress, result);
		}
		if (0 == result) {
			setZ(true);
		} else {
			setZ(false);
		}
	}

	/**
	 * The contents of W-Register are AND'ed with the eight bit literal 'k'. The
	 * result is placed in the W-Register.
	 *
	 * @param lit_K
	 *
	 */
	private void do_andlw(int lit_K) {
		final int value = registerW & lit_K;
		if (value == 0) {
			setZ(true);
		}
		registerW = value;
	}

	/**
	 *
	 * AND the W-Register with register 'f'. If 'd' is 0, the result is stored
	 * in the W-Register. If 'd' is 1 the result is stored back in the register
	 * 'f'.
	 *
	 * @param d_f
	 */
	private void do_andwf(int d_f) {
		final int d = d_f & 0b10000000;
		final int adress = d_f & 0b01111111;
		final int value = getRegisterEntry(adress);
		final int result = registerW & value;
		if (d == 0) {
			registerW = result;
		} else {
			setRegisterEntry(adress, result);
		}
		if (result == 0) {
			setZ(true);
		}
	}

	/**
	 * Bit 'b' in the register 'f' is cleared
	 *
	 * @param b_f
	 *
	 */
	private void do_bcf(int b_f) {
		final int bit = (b_f & 0b1110000000) / 128;
		final int adress = b_f & 0b0001111111;
		clearBit(bit + 1, adress);
	}

	/**
	 * Bit 'b' in the register 'f' is set
	 *
	 * @param b_f
	 *
	 */
	private void do_bsf(int b_f) {
		final int bit = (b_f & 0b1110000000) / 128;
		final int adress = b_f & 0b0001111111;
		setBit(bit + 1, adress);
	}

	/**
	 *
	 * If bit 'b' in register 'f' is '1' the the next instruction is executed.
	 * If bit 'b' in register 'f' is '0' then the next instruction is discarded
	 * and a NOP is executed instead, making this a 2Tcy instruction
	 *
	 * @param b_f
	 */
	private void do_btfsc(int b_f) {
		final int adress = b_f & 0b0001111111;
		final int bit = (b_f & 0b1110000000) / 128;
		if (!checkBitSet(bit, adress)) {
			do_nop();
			setProgramCounter(getProgrammCounter() + 1);
		}
	}

	/**
	 * If bit 'b' in register 'f' is '0' the the next instruction is executed.
	 * If bit 'b' is '1' then the next unstruction is discarded and a NOP is
	 * executed instead, making this a 2Tcy instruction
	 *
	 * @param b_f
	 *
	 */
	private void do_btfss(int b_f) {
		final int adress = b_f & 0b0001111111;
		final int bit = (b_f & 0b1110000000) / 128;
		if (checkBitSet(bit, adress)) {
			setProgramCounter(getProgrammCounter() + 1);
		}
	}

	/**
	 * Call Subroutine. First, return address (PC+1) is pushed onto the STACK.
	 * The eleven bit immediate address is loaded into OC bits <10:0>. The upper
	 * bits of the PC are loaded from PCLATH. CALL is a two cycle instruction.
	 *
	 * @param lit_K
	 *
	 */
	private void do_call(int lit_K) {
		StackPC.add(getProgrammCounter());
		setProgramCounter(lit_K - 1);
	}

	/**
	 * The contents of register 'f' are cleared and the Z bit is set.
	 *
	 * @param f
	 *
	 */
	private void do_clrf(int f) {
		setRegisterEntry(f, 0);
		setZ(true);
	}

	/**
	 * W register is cleared. Zero bit (Z) is set.
	 *
	 * @param value
	 *
	 */
	private void do_clrw(int value) {
		registerW = 0b0;
		setZ(true);
	}

	/**
	 * CLRWDT Instruction resets the Watchdog-Timer. It also resets the
	 * prescaler of th WDT. Status bits TO and PD are set.
	 */
	private void do_clrwdt() {
		// TODO Clear WatchDog Timer
	}

	/**
	 * The contents of register 'f' are complemented. If 'd' is 0 the result is
	 * stored back in W. If 'd' is 1 the result is stored back in register 'f'.
	 *
	 * @param f_d
	 *
	 */
	private void do_comf(int f_d) {

		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		final int temp = getRegisterEntry(adress);
		final int value = ~temp;
		if (d == 0) {
			registerW = value & 0xFF;
		} else {
			setRegisterEntry(adress, registerW);
		}
		if ((value & 0xFF) == 0) {
			setZ(true);
		} else {
			setZ(false);
		}
	}

	/**
	 * Decrement register 'f'. If 'd' is 0 the result is stored back in the W
	 * register. If 'd' is 1 the result is stored back in register 'f'.
	 *
	 * @param f_d
	 *
	 */
	private void do_decf(int f_d) {
		final int d = f_d & 0b10000000;
		final int address = f_d & 0b01111111;
		int value = (getRegisterEntry(address) - 1);

		if (0 > value) {
			value = 0xFF;
		}
		if (0 == value) {
			setZ(true);
		} else {
			setZ(false);
		}
		if (0 == d) {
			registerW = value & 0xFF;
		} else {
			setRegisterEntry(address, value & 0xFF);
		}
	}

	/**
	 * The contents of register 'f' are decremented. If 'd' is 0 the result is
	 * placed in the W register. If 'd' is 1 the result is placed back in
	 * register 'f'. If the result is 1, the next instruction, is executed. If
	 * the result is 0, then a NOP is executed instead making it a 2Tcy
	 * instruction
	 *
	 * @param f_d
	 *
	 */
	private void do_decfsz(int f_d) {

		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		final int decr = getRegisterEntry(adress) - 1;

		if (d == 128) {
			setRegisterEntry(adress, decr);
		} else {

			registerW = decr;
		}
		if (decr == 0) {
			setProgramCounter(getProgrammCounter() + 1);
		}
	}

	/**
	 * GOTO is an unconditional branch. The eleven bit immediate value is loaded
	 * into PC bits <10:0>. The upper bits of PC are loaded from PCLATH<4:3>.
	 * GOTO is a two cycle instruction.
	 *
	 * @param lit_K
	 *
	 */
	private void do_goto(int lit_K) {
		sprung = getProgrammCounter();
		setProgramCounter(lit_K - 1);
	}

	/**
	 * The contents of register 'f' are incremented. If 'd' is 0 the result is
	 * placed in the W register. If 'd' is 1 the result is placed back in
	 * register 'f'.
	 *
	 * @param f_d
	 *
	 */
	private void do_incf(int f_d) {
		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		final int value = getRegisterEntry(adress);
		if (getZ()) {
			setZ(false);

		} else {
			if (value == 255) {
				setZ(true);
			}
		}

		if (d == 0) {
			registerW = getRegisterEntry(adress) + 1;

		} else {
			setRegisterEntry(adress, (getRegisterEntry(adress) + 1));
		}
	}

	/**
	 * The contents of register 'f' are incremented. If 'd' is 0 the result is
	 * placed in the W register. If 'd' is 1 the result is placed back in
	 * register 'f'. If the result is 1, the next instruction is executed. If
	 * the result is 0, a NOP is executed instead making it a 2TCY instruction.
	 *
	 * @param f_d
	 *
	 */
	private void do_incfsz(int f_d) {
		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		final int decr = getRegisterEntry(adress) + 1;
		if (d == 128) {
			setRegisterEntry(adress, decr);
		} else {
			registerW = decr;
		}
		if (getRegisterEntry(adress) == 0) {
			setProgramCounter(getProgrammCounter() + 1);
		}
	}

	/**
	 * The contents of the W register is OR’ed with the eight bit literal 'k'.
	 * The result is placed in the W register.
	 *
	 * @param lit_K
	 *
	 */
	private void do_iorlw(int lit_K) {
		final int value = registerW | lit_K;
		if (value == 0) {
			setZ(true);
		}
		registerW = value;
	}

	/**
	 * Inclusive OR the W register with register 'f'. If 'd' is 0 the result is
	 * placed in the W register. If 'd' is 1 the result is placed back in
	 * register 'f'.
	 *
	 * @param f_d
	 *
	 */
	private void do_iorwf(int f_d) {
		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		final int value = getRegisterEntry(adress) | registerW;
		if (d == 0) {
			registerW = value;
		} else {
			setRegisterEntry(adress, value);
		}
		if (value == 0) {
			setZ(true);
		} else {
			setZ(false);
		}
	}

	/**
	 * The contents of register f is moved to a destination dependant upon the
	 * status of d. If d = 0, destination is W register. If d = 1, the
	 * destination is file register f itself. d = 1 is useful to test a file
	 * register since status flag Z is affected. Words:
	 *
	 * @param f_d
	 *
	 */
	private void do_movf(int f_d) {
		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		final int value = getRegisterEntry(adress);
		if (d == 0) {
			registerW = value;
		}
		if (value == 0) {
			setZ(true);
		} else {
			setZ(false);
		}
	}

	/**
	 * The eight bit literal 'k' is loaded into W register. The don’t cares will
	 * assemble as 0’s.
	 *
	 * @param lit_K
	 *
	 */
	private void do_movlw(int lit_K) {
		final int value = lit_K & 0b0011111111;
		registerW = value;
	}

	/**
	 * Move data from W register to register 'f'.
	 *
	 * @param var_F
	 *
	 */
	private void do_movwf(int var_F) {
		setRegisterEntry(var_F, registerW);
	}

	/**
	 * No operation.
	 */
	private void do_nop() {
		// No Operation
	}

	/**
	 * Return from Interrupt. Stack is POPed and Top of Stack (TOS) is loaded in
	 * the PC. Interrupts are enabled by setting Global Interrupt Enable bit,
	 * GIE (INTCON<7>). This is a two cycle instruction.
	 */
	private void do_retfie() {
		final int adress = StackPC.pop();
		setProgramCounter(adress);
		// Global Interrupt setzen
		setBit(7, 0xb);
	}

	/**
	 * The W register is loaded with the eight bit literal 'k'. The program
	 * counter is loaded from the top of the stack (the return address). This is
	 * a two cycle instruction.
	 *
	 * @param lit_K
	 *
	 */
	private void do_retlw(int lit_K) {
		final int temp = lit_K & 0b0011111111;
		registerW = temp;
		final int adress = StackPC.pop();
		setProgramCounter(adress);
	}

	/**
	 *
	 * Return from Subroutine. The Stack is popped and th top of the stack is
	 * loaded into the program counter. This is a two cycle instruction.
	 *
	 * @throws InterruptedException
	 */
	private void do_return() throws InterruptedException {
		final int adress = StackPC.pop();
		setProgramCounter(adress);
	}

	/**
	 * The contents of register 'f' are rotated one bit to the left through the
	 * Carry Flag. If 'd' is 0 the result is placed in the W register. If 'd' is
	 * 1 the result is stored back in register 'f'.
	 *
	 * @param f_d
	 *
	 */
	private void do_rlf(int f_d) {
		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		int value = getRegisterEntry(adress);
		value = value << 1;
		if (1 == getC()) {
			value += 1;
		}
		if ((value & 0x100) == 256) {
			setC(true);
		} else {
			setC(false);
		}
		value = value & 0xFF;
		if (d == 0) {
			registerW = value;
		} else {
			setRegisterEntry(adress, value);
		}
	}

	/**
	 * The contents of register 'f' are rotated one bit to the right through the
	 * Carry Flag. If 'd' is 0 the result is placed in the W register. If 'd' is
	 * 1 the result is placed back in register 'f'.
	 *
	 * @param f_d
	 *
	 */
	private void do_rrf(int f_d) {
		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		int value = getRegisterEntry(adress);
		if (1 == getC()) {
			value += 256;
		}
		if ((value & 0b00000001) == 1) {
			setC(true);
			value = value & 0b111111110;
		} else {
			setC(false);
		}
		value = value >> 1;
		if (0 == d) {
			registerW = value;
		} else {
			setRegisterEntry(adress, value);
		}
	}

	/**
	 * The power-down status bit, PD is cleared. Time-out status bit, TO is
	 * set.Watchdog Timer and its prescaler are cleared. The processor is put
	 * into SLEEP mode with the oscillator stopped. See Section 14.8 for more
	 * details.
	 */
	private void do_sleep() {
		// clear bit PD
		clearBit(4, 3);
		// clear bit TO
		clearBit(5, 3);
		// clear bit Watchdog
		clearBit(4, 129);
	}

	/**
	 * The W register is subtracted (2’s complement method) from the eight bit
	 * literal 'k'. The result is placed in the W register.
	 *
	 * @param lit_K
	 *
	 */
	private void do_sublw(int lit_K) {
		final int temp = lit_K & 0b011111111;

		final int value = temp + ~registerW + 1;
		final int dc = temp & (0xF + ((~registerW) & 0xF));
		if (value >= 0) {
			setC(true);
		} else {
			setC(false);
		}
		if (8 < dc) {
			setDC(true);
		} else {
			setDC(false);
		}
		if (value == 0) {
			setZ(true);
		} else {
			setZ(false);
		}
		registerW = value;
	}

	/**
	 * Subtract (2’s complement method) W register from register 'f'. If 'd' is
	 * 0 the result is stored in the W register. If 'd' is 1 the result is
	 * stored back in register 'f'.
	 *
	 * @param f_d
	 *
	 */
	private void do_subwf(int f_d) {
		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		final int registerEntry = getRegisterEntry(adress);
		final int regW = ~registerW;
		int value = registerEntry + regW + 1;

		final int dc = (registerEntry & 0xF) + ((regW) & 0xF);

		if (value >= 0) {
			setC(true);
		} else {
			setC(false);
		}
		if (8 < dc) {
			setDC(true);
		} else {
			setDC(false);
		}

		value = value & 0xFF;
		if (d == 0) {
			registerW = value;
		} else {
			setRegisterEntry(adress, value);
		}

		if (value == 0) {
			setZ(true);
		} else {
			setZ(false);
		}
	}

	/**
	 * The upper and lower nibbles of register 'f' are exchanged. If 'd' is 0
	 * the result is placed in W register. If 'd' is 1 the result is placed in
	 * register 'f'.
	 *
	 * @param f_d
	 *
	 */
	private void do_swapf(int f_d) {
		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		final int nibble1 = (getRegisterEntry(adress) & 0b00001111) * 16;
		final int nibble2 = (getRegisterEntry(adress) & 0b11110000) / 16;
		final int ergebnis = nibble1 + nibble2;
		if (d == 0) {
			registerW = ergebnis;
		} else {
			setRegisterEntry(adress, ergebnis);
		}
	}

	/**
	 * The contents of the W register are XOR’ed with the eight bit literal 'k'.
	 * The result is placed in the W register.
	 *
	 * @param lit_K
	 *
	 */
	private void do_xorlw(int lit_K) {
		final int value = registerW ^ lit_K;
		if (value == 0) {
			setZ(true);
		} else {
			setZ(false);
		}
		registerW = value;
	}

	/**
	 * Exclusive OR the contents of the W register with register 'f'. If 'd' is
	 * 0 the result is stored in the W register. If 'd' is 1 the result is
	 * stored back in register 'f'.
	 *
	 * @param f_d
	 *
	 */
	private void do_xorwf(int f_d) {
		final int d = f_d & 0b10000000;
		final int adress = f_d & 0b01111111;
		final int value = getRegisterEntry(adress);
		final int result = registerW ^ value;
		if (d == 0) {
			registerW = result;
		} else {
			setRegisterEntry(adress, result);
		}
		if (result == 0) {
			setZ(true);
		} else {
			setZ(false);
		}
	}

	/*
	 * Tatsächliche Pic-Befehle Hier findet die Auswahl der Befehle nach
	 * Bitfolge statt
	 */
	/**
	 * Auswertung des Befehls und anschließender Befehlsaufruf
	 *
	 * @param code
	 *            Befehlscode als Integer
	 * @throws InterruptedException
	 *
	 */
	public void doAction(int code) throws InterruptedException {
		final int codeTmp = code;

		final int hex16 = codeTmp & 0xFFFF;
		switch (hex16) {
		case 100:
			do_clrwdt();
			break;
		case 9:
			do_retfie();
			break;
		case 8:
			do_return();
			break;
		case 99:
			do_sleep();
			break;
		}

		final int hex10 = codeTmp & 0xFC00;
		final int _hex10 = codeTmp & 0x03FF;
		switch (hex10) {
		case 4096:
			do_bcf(_hex10);
			break;
		case 5120:
			do_bsf(_hex10);
			break;
		case 6144:
			do_btfsc(_hex10);
			break;
		case 7168:
			do_btfss(_hex10);
			break;
		case 12288:
			do_movlw(_hex10);
			break;
		case 13312:
			do_retlw(_hex10);
			break;
		}

		final int hex9 = codeTmp & 0xFF80;
		final int _hex9 = codeTmp & 0x007F;
		switch (hex9) {
		case 384:
			do_clrf(_hex9);
			break;
		case 256:
			do_clrw(_hex9);
			break;
		case 128:
			do_movwf(_hex9);
			break;
		case 0:
			do_nop();
			break;
		}

		final int hex8 = codeTmp & 0xFF00;
		final int _hex8 = codeTmp & 0x00FF;
		switch (hex8) {
		case 1792:
			do_addwf(_hex8);
			break;
		case 1280:
			do_andwf(_hex8);
			break;
		case 2304:
			do_comf(_hex8);
			break;
		case 768:
			do_decf(_hex8);
			break;
		case 2816:
			do_decfsz(_hex8);
			break;
		case 2560:
			do_incf(_hex8);
			break;
		case 3840:
			do_incfsz(_hex8);
			break;
		case 1024:
			do_iorwf(_hex8);
			break;
		case 2048:
			do_movf(_hex8);
			break;
		case 3328:
			do_rlf(_hex8);
			break;
		case 3072:
			do_rrf(_hex8);
			break;
		case 512:
			do_subwf(_hex8);
			break;
		case 3584:
			do_swapf(_hex8);
			break;
		case 1536:
			do_xorwf(_hex8);
			break;
		case 14592:
			do_andlw(_hex8);
			break;
		case 14336:
			do_iorlw(_hex8);
			break;
		case 14848:
			do_xorlw(_hex8);
			break;

		}

		final int hex7 = codeTmp & 0xFE00;
		final int _hex7 = codeTmp & 0x01FF;
		switch (hex7) {
		case 15872:
			do_addlw(_hex7);
			break;
		case 15360:
			do_sublw(_hex7);
			break;
		}

		final int hex5 = codeTmp & 0xF800;
		final int _hex5 = codeTmp & 0x07FF;
		switch (hex5) {
		case 8192:
			do_call(_hex5);
			break;
		case 10240:
			do_goto(_hex5);
			break;
		}

	}

	/**
	 * Interrupt wird gesetzt
	 *
	 * @param ID
	 *
	 */
	public void doInterrupt(int ID) {
		System.out.println("Interrupt");
		if (checkBitSet(7, 0xb)) {
			switch (ID) {
			// RB0-Interrupt
			case 1:
				if (checkBitSet(4, 0xb)) {
					StackPC.add(getProgrammCounter());
					setProgramCounter(4);
					setBit(1, 0xb);
				}
				System.out.println("RB0-Interrupt");
				break;
			// Timer-Interrupt
			case 2:
				if (checkBitSet(5, 0xb)) {
					StackPC.add(getProgrammCounter());
					setProgramCounter(4);
					setBit(2, 0xb);
				}
				break;
			// Port-B-Interrupt
			case 3:
				if (checkBitSet(3, 0xb)) {
					StackPC.add(getProgrammCounter());
					setProgramCounter(4);
					setBit(0, 0xb);
				}
				System.out.println("RB4-7 Interrupt");
				break;
			// EEPROM-Interrupt
			case 4:
				if (checkBitSet(6, 0xb)) {
					StackPC.add(getProgrammCounter());
					setProgramCounter(4);
				}
				break;
			}
		}
	}

	/**
	 * @return Wert des DC-Bits
	 */
	public int get_DC() {
		if (checkBitSet(2, 3)) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * @return Wert des C-Bits
	 */
	public int getC() {
		if (checkBitSet(0, 3)) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * @return den Speicherinhalt des Intcon-Registers
	 */
	public int getIntcon() {
		return registerArray[0xb];
	}

	public boolean getMode() {
		return mode;
	}

	/**
	 * @return den Speicherinhalt des Option-Registers
	 */
	public int getOption() {
		return registerArray[0x81];
	}

	/**
	 * @return Integer Wert von PortA
	 */
	public int getPortA() {
		return portA;
	}

	/**
	 * @return Integer Wert von PortB
	 */
	public int getPortB() {
		return portB;
	}

	/**
	 * @return den Vorteiler
	 */
	public int getPrescaler() {
		return prescaler;
	}

	/**
	 * @return String Wert des Pfads der Programmdatei
	 */
	public String getProgramFilePath() {
		return programFilePath;
	}

	/**
	 * Gibt den Wert des Programmzählers zurück
	 *
	 * @return Programmzähler als Integer-Wert
	 */
	public int getProgrammCounter() {
		return getRegisterEntry(2);
	}

	/**
	 * Gibt den Speicherinhalt an bestimmter Stelle "index" zurück
	 *
	 * @param index
	 *            Integer Wert Index des Registers
	 * @return Integer Wert des Speicherinhalts
	 */
	public int getRegisterEntry(int index) {
		if (index == 0) {
			return registerArray[registerArray[4]];
		}
		return registerArray[index];
	}

	/**
	 * Gibt die aktuelle Laufzeit als Long-Wert zurück
	 *
	 * @return Long Wert der Laufzeit
	 */
	public long getRunningTime() {
		return runningTime;
	}

	public int getStackSize() {
		return StackPC.size();
	}

	/**
	 * @return Long Wert der Startzeit
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @return Integer-Wert des Status-Registers
	 */
	public int getStatus() {
		return getRegisterEntry(3);
	}

	/**
	 * @return Integer Wert der durchgeführten Schritte
	 */
	public int getSteps() {
		return steps;
	}

	/**
	 * @return Integer-Wert des Taktes
	 */
	public int getTakt() {
		return takt;
	}

	/**
	 * @return Wert des Z-Bits
	 */
	public boolean getZ() {
		if (checkBitSet(3, 3)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Erhöht den Prescaler um 1
	 */
	public void incrPrescaler() {
		prescaler++;
		System.out.println("prescaler++");
	}

	/**
	 * Setzt das gesamte Model auf Ausgangswerte zurück
	 */
	public void resetModel() {
		int m;
		for (m = 0; m < 256; m++) {
			registerArray[m] = 0;
		}
		registerW = 0;
		StackPC.clear();
		sprung = 0;
		codeList.clear();
		programFilePath = "";
		takt = 4000;
		steps = 0;
		runningTime = 0;
	}

	/**
	 * Zurücksetzen des Inhalts von RA auf den Wert 0
	 */
	public void resetRA() {
		setPortA(0);
	}

	/**
	 * Zurücksetzen des Inhalts von RA auf den Wert 0
	 */
	public void resetRB() {
		setPortB(0);
	}

	/**
	 * Setzt ein einzelnes Bit an bestimmer Speicheradresse
	 *
	 * @param position
	 *            Gibt das Bit an, welches gesetzt wird.
	 * @param adress
	 *            Gibt die Adresse an, an der das Bit gesetzt wird.
	 */
	public void setBit(int position, int adress) {
		switch (position) {
		case 1: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00000001));
			break;
		}
		case 2: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00000010));
			break;
		}
		case 3: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00000100));
			break;
		}
		case 4: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00001000));
			break;
		}
		case 5: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00010000));
			break;
		}
		case 6: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00100000));
			break;
		}
		case 7: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b01000000));
			break;
		}
		case 8: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b10000000));
			break;
		}
		default: {
		}
		}
	}

	/**
	 * Setzt das C-Bit mit dem übergebenen Wert
	 *
	 * @param s
	 *            boolean, gibt an, ob C gesetzt werden soll oder nicht
	 *
	 */
	public void setC(boolean s) {
		if (s) {
			setBit(1, 3);
		} else {
			clearBit(1, 3);
		}
	}

	/**
	 *
	 * Setzt Werte in die Code-Liste. In dieser Liste sind die Befehle, die
	 * abgearbeitet werden.
	 *
	 * @param code
	 *            Integer-Wert zum Einfügen in die Code-Liste
	 */
	public void setCode(int code) {
		codeList.add(code);
	}

	/**
	 * Setzt das DC-Bit mit dem übergebenen Wert
	 *
	 * @param s
	 *            boolean, gibt an, ob DC gesetzt werden soll oder nicht
	 *
	 */
	public void setDC(boolean s) {
		if (s) {
			setBit(2, 3);
		} else {
			clearBit(2, 3);
		}
	}

	/**
	 * Setzt den übergebenen Wert im Intcon-Register
	 *
	 * @param value
	 *            zu setzender Speicherwert
	 */
	public void setIntcon(int value) {
		registerArray[0xb] = value;
	}

	public void setMode(boolean mode) {
		this.mode = mode;
	}

	/**
	 * Setzt den übergebenen Wert im Options-Register
	 *
	 * @param value
	 *            zu setzender Speicherwert
	 */
	public void setOption(int value) {
		registerArray[0x81] = value;
	}

	/**
	 * Setzt den Wert des PortA
	 *
	 * @param portA
	 *            zu setzender Integer Wert des PortA
	 */
	public void setPortA(int portA) {
		this.portA = portA;
	}

	/**
	 * Setzt den Wert des PortB
	 *
	 * @param portB
	 *            zu setzender Integer Wert des PortB
	 */
	public void setPortB(int portB) {
		this.portB = portB;
	}

	/**
	 * Setzt den Vorteiler
	 *
	 * @param prescaler
	 */
	public void setPrescaler(int prescaler) {
		this.prescaler = prescaler;
	}

	/**
	 * Setzt den Programmzähler auf den neuen Wert
	 *
	 * @param counter
	 *            Integer Wert des neuen Programmzählers
	 */
	public void setProgramCounter(int counter) {
		setRegisterEntry(2, counter);
	}

	/**
	 * Setzt den Pfad der Programmdatei
	 *
	 * @param path
	 *            String des neuen Dateipfades der Programmdatei
	 */
	public void setProgramFilePath(String path) {
		programFilePath = path;
	}

	/**
	 * Setzt den Speicherinhalt an der Stelle "index" auf den Wert "value"
	 *
	 * @param index
	 *            Integer-Wert des Index im Register
	 * @param value
	 *            Integer Wert des zu setzenden Speicherwertes
	 */
	public void setRegisterEntry(int index, int value) {
		if (checkBitSet(5, 3)) {
			if (2 == index) {
				registerArray[index] = value;
			}
			// Wenn das Bit für Bankumschaltung gesetzt ist
			if (index == 0) {
				value = value & 0b11111111;
				registerArray[registerArray[4] + 128] = value;

			} else {
				value = value & 0b11111111;
				if (129 > index) {
					index += 128;
				}
				registerArray[index] = value;
			}
		} else {
			if (index == 0) {
				value = value & 0b11111111;
				registerArray[registerArray[4]] = value;

			} else if (!checkBitSet(0, 0xA)) {
				value = value & 0b11111111;
			}
			registerArray[index] = value;
		}
	}

	/**
	 * Setzt ein Bit an bestimmter Speicherstelle "index" auf den Wert "value"
	 *
	 * @param index
	 *            Integer-Wert des Index im Register
	 * @param value
	 *            Integer Wert des zu setzenden Speicherwertes
	 */
	public void setRegisterEntryOneBit(int index, int value) {
		if (index == 1) {
			System.out.println("index==1");
		}

		if (index == 0) {
			value = value & 0b11111111;
			registerArray[registerArray[4]] = value;

		} else {
			value = value & 0b11111111;
			registerArray[index] = value;
		}
	}

	/**
	 * Setzt die Laufzeit auf den übergebenen Wert
	 *
	 * @param runningTime
	 *            Long Wert der zu setzenden Laufzeit
	 */
	public void setRunningTime(long runningTime) {
		this.runningTime = runningTime;
	}

	/**
	 * Setzt die Startzeit auf den übergebenen Wert
	 *
	 * @param startTime
	 *            Long-Wert Startzeit
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * Setzt den übergebenen Wert im Status-Register
	 *
	 * @param value
	 *            zu setzender Speicherwert
	 */
	public void setStatus(int value) {
		registerArray[3] = value;
		registerArray[131] = value;
	}

	/**
	 * Erhöht die Anzahl der durchgeführten Schritte um 1
	 */
	public void setSteps() {
		steps = steps + 1;
	}

	/**
	 * Setzt den neuen Takt
	 *
	 * @param takt
	 *            zu setzender Takt Integer
	 */
	public void setTakt(int takt) {
		this.takt = takt;
	}

	/**
	 * Setzt das Z-Bit mit dem übergebenen Wert
	 *
	 * @param s
	 *            boolean, gibt an, ob Z gesetzt werden soll oder nicht
	 *
	 */
	public void setZ(boolean s) {
		if (s) {
			setBit(3, 3);
		} else {
			clearBit(3, 3);
		}
	}

	/**
	 * Setzt den übergebenen Wert in die berechnete Speicheradresse
	 *
	 * @param xColumn
	 *            Speicheradresse Spalte
	 * @param yRow
	 *            Speicheradresse Zeile
	 * @param value
	 *            Zu setzender Wert
	 *
	 */
	public void writeToRegister(int xColumn, int yRow, String value) {
		final int adress = (yRow * 8) + xColumn + 1;
		setRegisterEntry(adress, (Integer.parseInt(value)));
	}
}
