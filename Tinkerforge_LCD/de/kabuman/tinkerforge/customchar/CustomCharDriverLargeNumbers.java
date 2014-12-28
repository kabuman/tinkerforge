package de.kabuman.tinkerforge.customchar;

import java.util.Date;

import com.tinkerforge.BrickletLCD20x4;

/**
 * Provides functions to use/write the defined custom characters to LCD  <br>
 *  <br>
 * In other words: it is the driver for this custom characters.
 */
public class CustomCharDriverLargeNumbers extends AbstractLCDoutput{
	
	
	/**
	 * Constructor  <br>
	 * 
	 * @param lcd - BrickletLCD20x4: may be null: output via screen controller
	 */
	public CustomCharDriverLargeNumbers(BrickletLCD20x4 lcd){
		super(lcd);
		
		new CustomCharDefinitionLargeNumbers(lcd);
	}
	
	/**
	 * Writes the numeric value at the given position 
	 * 
	 * @param position - the position (0-16)
	 * @param value - the value to write (0-9)
	 */
	public void write(short position, int value){
		switch (value) {
		case 0:
			write0(position);
			break;
		case 1:
			write1(position);
			break;
		case 2:
			write2(position);
			break;
		case 3:
			write3(position);
			break;
		case 4:
			write4(position);
			break;
		case 5:
			write5(position);
			break;
		case 6:
			write6(position);
			break;
		case 7:
			write7(position);
			break;
		case 8:
			write8(position);
			break;
		case 9:
			write9(position);
			break;
	
		default:
			break;
		}
	}

	
	/**
	 * Writes the given numeric values at the internal defined positions
	 * 
	 * @param hZehn - hour
	 * @param hEins - hour
	 * @param mZehn - minute
	 * @param mEins - minute
	 */
	private void writeClock(int hZehn, int hEins, int mZehn, int mEins){
		write((short)0, hZehn);
		write((short)4, hEins);
		write((short)11, mZehn);
		write((short)15, mEins);
	}

	
	/**
	 * Writes the given Date 
	 * 
	 * @param date - the date to display
	 */
	public void writeDate(Date date){
		int hZehn = date.getHours()/10;
		int hEins = date.getHours() - (hZehn * 10);
		int mZehn = date.getMinutes()/10;
		int mEins = date.getMinutes() - (mZehn * 10);
		
		writeClock(hZehn, hEins, mZehn, mEins);
	}

	
	/**
	 * Writes the given numeric value (0- 
	 * 
	 * @param int - the number to display
	 */
	public void writeNumber(int number){
		String s = Integer.toString(number);
		
		for (int i = 0; i < s.length(); i++) {
			
			int position = (5 - i) * 4 - 4; 
			String numStrg = s.substring(i,i+1);
			
			Integer numInt = new Integer(numStrg);
			
			write((short)position, numInt);
		}
	}

	public void writeMonthDay(Date date){
		String monthDay = String.format("%td", date);
		
		int digit1 = Integer.parseInt(monthDay.substring(0, 1));
		int digit2 = Integer.parseInt(monthDay.substring(1, 2));
		

		write((short)11, digit1);
		write((short)15, digit2);
	}
	
	
	public void writeWeekMonthDay(Date date){
		writeWeekDay(date);
		writeMonthDay(date);
		writeLine((short)3, (short)19, "\6");

	}
	
	
	/**
	 * Writes the given Date 
	 * 
	 * @param int - the number to display
	 */
	public void writeWeekDay(Date date){
		String s = String.format("%ta", date);
		writeWeekDay(s);
	}
	
	
	/**
	 * Writes the given Date 
	 * 
	 * @param int - the number to display
	 */
	public void writeWeekDay(String s){
		
		for (int i = 0; i < s.length(); i++) {
			
			int position = (i + 1) * 4 - 4; 
			
			String letter = s.substring(i,i+1);
			
			if (letter.equals("o")){
				writeLetterO((short)position);
			}
			if (letter.equals("i")){
				// TODO
				writeLetterI((short)position);
			}
			if (letter.equals("a")){
				// TODO
				writeLetterA((short)position);
			}
			if (letter.equals("M")){
				writeLetterM((short)position);
			}
			if (letter.equals("D")){
				writeLetterD((short)position);
			}
			if (letter.equals("S")){
				writeLetterS((short)position);
			}
			if (letter.equals("F")){
				writeLetterF((short)position);
			}
			if (letter.equals("r")){
				writeLetterR((short)position);
			}
			
		}
		
		writeLine((short)3, (short)8, "\6");

	}

	
	private void writeLetterO(short offset){
		writeLine((short)0, offset, "    ");
		writeLine((short)1, offset, "    ");
		writeLine((short)2, offset, "\1\5\5\2");
		writeLine((short)3, offset, "\3\6\6\4");
	}

	
	private void writeLetterI(short offset){
		writeLine((short)0, offset, "    ");
		writeLine((short)1, offset, "  \5 ");
		writeLine((short)2, offset, "  \7 ");
		writeLine((short)3, offset, "  \7 ");
	}

	
	private void writeLetterA(short offset){
		writeLine((short)0, offset, "    ");
		writeLine((short)1, offset, "    ");
		writeLine((short)2, offset, "\1\5\5\2");
		writeLine((short)3, offset, "\3\6\6\7");
	}

	
	private void writeLetterS(short offset){
		writeLine((short)0, offset, "\1\5\5\2");
		writeLine((short)1, offset, "\3\6\6\6");
		writeLine((short)2, offset, "   \7");
		writeLine((short)3, offset, "\3\6\6\4");
	}

	
	private void writeLetterM(short offset){
		writeLine((short)0, offset, "\2  \1");
		writeLine((short)1, offset, "\7\2\1\7");
		writeLine((short)2, offset, "\7  \7");
		writeLine((short)3, offset, "\7  \7");
	}

	
	private void writeLetterD(short offset){
		writeLine((short)0, offset, "\7\5\5\2");
		writeLine((short)1, offset, "\7  \7");
		writeLine((short)2, offset, "\7  \7");
		writeLine((short)3, offset, "\7\6\6\4");
	}

	
	private void writeLetterF(short offset){
		writeLine((short)0, offset, "\7\5\5\5");
		writeLine((short)1, offset, "\7   ");
		writeLine((short)2, offset, "\7\5\5 ");
		writeLine((short)3, offset, "\7   ");
	}

	
	private void writeLetterR(short offset){
		writeLine((short)0, offset, "    ");
		writeLine((short)1, offset, "    ");
		writeLine((short)2, offset, " \7\1 ");
		writeLine((short)3, offset, " \7  ");
	}

	
	private void write0(short offset){
		writeLine((short)0, offset, "\1\5\5\2");
		writeLine((short)1, offset, "\7  \7");
		writeLine((short)2, offset, "\7  \7");
		writeLine((short)3, offset, "\3\6\6\4");
	}

	
	private void write1(short offset){
		writeLine((short)0, offset, " \1\7 ");
		writeLine((short)1, offset, "  \7 ");
		writeLine((short)2, offset, "  \7 ");
		writeLine((short)3, offset, " \6\7\6");
	}

	
	private void write2(short offset){
		writeLine((short)0, offset, "\1\5\5\2");
		writeLine((short)1, offset, "  \1\4");
		writeLine((short)2, offset, " \1\4 ");
		writeLine((short)3, offset, "\1\7\6\6");
	}


	private void write3(short offset){
		writeLine((short)0, offset, "\1\5\5\2");
		writeLine((short)1, offset, "  \1\4");
		writeLine((short)2, offset, "  \3\2");
		writeLine((short)3, offset, "\3\6\6\4");
	}


	private void write4(short offset){
		writeLine((short)0, offset, " \1\4\7");
		writeLine((short)1, offset, "\1\4 \7");
		writeLine((short)2, offset, "\7\6\6\7");
		writeLine((short)3, offset, "   \7");
	}


	private void write5(short offset){
		writeLine((short)0, offset, "\1\5\5\5");
		writeLine((short)1, offset, "\3\6\6\6");
		writeLine((short)2, offset, "   \7");
		writeLine((short)3, offset, "\3\6\6\4");
	}

	
	private void write6(short offset){
		writeLine((short)0, offset, "\1\5\5 ");
		writeLine((short)1, offset, "\7   ");
		writeLine((short)2, offset, "\7\5\5\2");
		writeLine((short)3, offset, "\3\6\6\4");
	}

	
	private void write7(short offset){
		writeLine((short)0, offset, "\5\5\7 ");
		writeLine((short)1, offset, " \1\4 ");
		writeLine((short)2, offset, " \7  ");
		writeLine((short)3, offset, " \7  ");
	}
	
	
	private void write8(short offset){
		writeLine((short)0, offset, "\1\5\5\2");
		writeLine((short)1, offset, "\3\6\6\4");
		writeLine((short)2, offset, "\1\5\5\2");
		writeLine((short)3, offset, "\3\6\6\4");
	}
	
	
	private void write9(short offset){
		writeLine((short)0, offset, "\1\5\5\2");
		writeLine((short)1, offset, "\3\6\6\7");
		writeLine((short)2, offset, "   \7");
		writeLine((short)3, offset, " \6\6\4");
	}
	
	
	/**
	 * Writes the colon between hours and minutes 
	 */
	public void writeColon(){
		short offset = 9;
		writeLine((short)0, offset, "");
		writeLine((short)1, offset, "\5");
		writeLine((short)2, offset, "\6");
		writeLine((short)3, offset, "");
	}

	public void setLcd(BrickletLCD20x4 lcd) {
		this.lcd = lcd;
	}
	

}
