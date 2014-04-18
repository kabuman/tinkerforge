package de.kabuman.tinkerforge.led;

import com.tinkerforge.BrickletIO16;

import de.kabuman.tinkerforge.alarm.items.digital.output.OutputIO16ItemImpl;

public class LedBasis {
	
	BrickletIO16 ledProcessor;
	
	OutputIO16ItemImpl[] leds = new OutputIO16ItemImpl[16];
	
	public LedBasis(BrickletIO16 ledProcessor) {
		this.ledProcessor = ledProcessor;
		initPortLeds('a',(short)0);
		initPortLeds('b',(short)8);
	}
	
//	private void initPortALeds(){
//		for (short i = 0; i < 8; i++) {
//			leds[i] = new OutputIO16ItemImpl(ledProcessor, 'a', i);
//		}
//	}
//	
//	private void initPortBLeds(){
//		for (short i = 0; i < 8; i++) {
//			leds[i+8] = new OutputIO16ItemImpl(ledProcessor, 'b', i);
//		}
//	}
//	
	private void initPortLeds(char port, short offset){
		for (short i = 0; i < 8; i++) {
			leds[i+offset] = new OutputIO16ItemImpl(ledProcessor, port, i);
		}
	}
	
	/**
	 * Läßt eine LED eine bestimmte Dauer leuchten. <br>
	 * Danach wird die LED wieder abgeschaltet.
	 *  <br>
	 * @param dauer - die Leuchtdauer in Millisekunden (1 Sek. = 1000 Millisekunden)br#
	 * 					Beispiel: 5 Sekunden Leuchdauer = 5000 Millisekunden <br>
	 * @param ledNr - die Nummern der A- und B-LEDs: <br>
	 * 					1=A0,  2=A1,.., 8=A7 <br>
	 * 					9=B0, 10=B1,..,16=B7 <br>
	 */
	public void ledEin(long dauer, int... ledNr){
		for (int i = 0; i < ledNr.length; i++) {
			leds[ledNr[i]-1].switchON(dauer);
		}
	}
	
	/**
	 * Schalted mehrere Leds ohne Pause  ein. <br> <br>
	 * Die Leds werden bestimmt durch "vonLedNr" und "bisLedNr". <br>
	 * Begonnen wird mit der Led "vonLedNr" <br>
	 * Die nächste Led wird bestimmt durch = "vonLedNr" + "plusLedNr" <br>
	 *  <br>
	 * @param dauer - Leuchtdauer der letzten Led / Verzögerung bis nächste Led eingeschaltet wird
	 * @param vonLedNr - die Lednummern 1 bis 16
	 * @param plusLedNr - die nächste Lednummer = vonLedNr + plusLedNr
	 * @param bisLedNr - die Lednummern 1 bis 16
	 */
	public void ledsEin(long dauer, int vonLedNr, int plusLedNr, int bisLedNr){
		for (int i = vonLedNr; i < bisLedNr+1; i=i+plusLedNr) {
			ledEin(dauer,i);
		}
	}
	
	/**
	 * Schalted mehrere Leds ein. <br> <br>
	 * Die Leds werden bestimmt durch "vonLedNr" und "bisLedNr". <br>
	 * Begonnen wird mit der Led "vonLedNr" <br>
	 * Die nächste Led wird bestimmt durch = "vonLedNr" + "plusLedNr" <br>
	 *  <br>
	 * @param vonLedNr - die Lednummern 1 bis 16
	 * @param plusLedNr - die nächste Lednummer = vonLedNr + plusLedNr
	 * @param bisLedNr - die Lednummern 1 bis 16
	 */
	public void ledsAus(int vonLedNr, int plusLedNr, int bisLedNr){
		for (int i = vonLedNr; i < bisLedNr+1; i=i+plusLedNr) {
			ledAus(i);
		}
	}
	
	
	
	/**
	 * Läßt mehrere Leds für eine bestimmte Zeit leuchten. <br> <br>
	 * Die Leds werden bestimmt durch "vonLedNr" und "bisLedNr". <br>
	 * Begonnen wird mit der Led "vonLedNr" <br>
	 * Die nächste Led wird bestimmt durch = "vonLedNr" + "plusLedNr" <br>
	 *  <br>
	 * @param dauer - Leuchtdauer der letzten Led / Verzögerung bis nächste Led eingeschaltet wird
	 * @param vonLedNr - die Lednummern 1 bis 16
	 * @param plusLedNr - die nächste Lednummer = vonLedNr + plusLedNr
	 * @param bisLedNr - die Lednummern 1 bis 16
	 */
	public void ledsVerzögertEin(long dauer, int vonLedNr, int plusLedNr, int bisLedNr){
		ledsVerzögertEin(dauer, vonLedNr, plusLedNr, bisLedNr, dauer);
	}
		
		

	/**
	 * Läßt mehrere Leds leuchten. <br> <br>
	 * Die Leds werden bestimmt durch "vonLedNr" und "bisLedNr". <br>
	 * Begonnen wird mit der Led "vonLedNr" <br>
	 * Die nächste Led wird bestimmt durch = "vonLedNr" + "plusLedNr" <br>
	 *  <br>
	 * @param dauer - Leuchtdauer der letzten Led / Verzögerung bis nächste Led eingeschaltet wird
	 * @param vonLedNr - die Lednummern 1 bis 16
	 * @param plusLedNr - die nächste Lednummer = vonLedNr + plusLedNr
	 * @param bisLedNr - die Lednummern 1 bis 16
	 * @param pause - Pausendauer zwischen dem Einschalten der einzelnen Leds
	 */
	public void ledsVerzögertEin(long dauer, int vonLedNr, int plusLedNr, int bisLedNr, long pause){
		// Ermitteln, wieviele Leds betroffen sind
		
		long anzLeds = 1;
		for (int i = vonLedNr; i < bisLedNr+1; i=i+plusLedNr) {
			anzLeds++;
		}
		
		// Ermitteln der Leuchtdauer der ersten Led
		long dauerErsteLed = anzLeds *  dauer;

		
		for (int i = vonLedNr; i < bisLedNr+1; i=i+plusLedNr) {
			ledEin(dauerErsteLed,i);
			dauerErsteLed = dauerErsteLed - (int)dauer;
			warte(pause);
		}
	}
		
		
	/**
	 * Schaltet die LED ein. 
	 *  <br>
	 * @param ledNr -  die Lednummern 1 bis 16
	 */
	public void ledEin(int... ledNr){
		for (int i = 0; i < ledNr.length; i++) {
			leds[ledNr[i]-1].switchON();
		}
	}

	/**
	 * Schaltet die LED aus.
	 *  <br>
	 * @param ledNr -  die Lednummern 1 bis 16
	 */
	public void ledAus(int... ledNr){
		for (int i = 0; i < ledNr.length; i++) {
			leds[ledNr[i]-1].switchOFF();
		}
	}

	/**
	 * Wartet in der angegebenen Dauer von .. Millisekunden <br>
	 * Beispiel: Warte 3 Sekunden lang:  warte(3000); <br>
	 *  <br>
	 * @param dauer - Wartezeit in Millisekunden
	 */
	public void warte(long dauer){
		try {
			Thread.sleep(dauer);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Ermittle die Port Id ('a' oder 'b')
	 * 
	 * @param ledNr - die LED Nr. (1,2,..,16)
	 * @return
	 */
	private char ermittlePortId(int ledNr){
		if (ledNr<9){
			return 'a';
		} else {
			return 'b';
		}
	}

	/**
	 * Ermittle die Led Id (0,1,..7) anhand der LED Nr (1,2,..,16)
	 * 
	 * @param ledNr - die LED Nr. (1,2,..,16)
	 * @return ledId - die LED Id (0,1,..,7)
	 */
	private int ermittleLedId(int ledNr){
		if (ledNr<9){
			return ledNr-1;
		} else {
			return ledNr-9;
		}
	}

}
