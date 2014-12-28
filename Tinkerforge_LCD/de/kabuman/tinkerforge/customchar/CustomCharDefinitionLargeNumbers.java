package de.kabuman.tinkerforge.customchar;

import com.tinkerforge.BrickletLCD20x4;

/**
 * Defines and load custom characters for the BrickletLCD20x4  <br>
 * for large digits to show a clock on the LCD Display
 */
public class CustomCharDefinitionLargeNumbers extends AbstractLCDoutput{
	
	private static short[] cDef1 = new short[]{
		0b00000001,
		0b00000011,
		0b00000011,
		0b00000111,
		0b00000111,
		0b00001111,
		0b00001111,
		0b00011111
	};
	
	private static short[] cDef2 = new short[]{
		0b00010000,
		0b00011000,
		0b00011000,
		0b00011100,
		0b00011100,
		0b00011110,
		0b00011110,
		0b00011111
	};
	
	private static short[] cDef3 = new short[]{
		0b00011111,
		0b00001111,
		0b00001111,
		0b00000111,
		0b00000111,
		0b00000011,
		0b00000011,
		0b00000001
	};
	
	private static short[] cDef4 = new short[]{
		0b00011111,
		0b00011110,
		0b00011110,
		0b00011100,
		0b00011100,
		0b00011000,
		0b00011000,
		0b00010000
	};

	private static short[] cDef5 = new short[]{
		0b00011111,
		0b00011111,
		0b00011111,
		0b00011111,
		0b00000000,
		0b00000000,
		0b00000000,
		0b00000000
	};

	private static short[] cDef6 = new short[]{
		0b00000000,
		0b00000000,
		0b00000000,
		0b00000000,
		0b00011111,
		0b00011111,
		0b00011111,
		0b00011111
	};

	private static short[] cDef7 = new short[]{
		0b00011111,
		0b00011111,
		0b00011111,
		0b00011111,
		0b00011111,
		0b00011111,
		0b00011111,
		0b00011111
	};

	/**
	 * Define and load the custom specific defined characters
	 * @param lcd - the BrickletLCD20x4
	 */
	public CustomCharDefinitionLargeNumbers(BrickletLCD20x4 lcd) {
		super(lcd);
		
		setCustomCharacter((short)1, cDef1);
		setCustomCharacter((short)2, cDef2);
		setCustomCharacter((short)3, cDef3);
		setCustomCharacter((short)4, cDef4);
		setCustomCharacter((short)5, cDef5);
		setCustomCharacter((short)6, cDef6);
		setCustomCharacter((short)7, cDef7);
		
		setCustomCharAlreadyLoaded();
	}


}
