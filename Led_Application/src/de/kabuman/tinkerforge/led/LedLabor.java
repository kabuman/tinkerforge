package de.kabuman.tinkerforge.led;

import com.tinkerforge.BrickletIO16;

public class LedLabor extends LedBasis {
	BrickletIO16 ledProcessor;
	
	public LedLabor(BrickletIO16 ledProcessor) {
		super(ledProcessor);
		this.ledProcessor = ledProcessor;
		
//		for (int i = 2000; i > 50; i=i-100) {
//			zweierLauflicht(i,i);
//			
//		}
//		zweierLauflicht(500,750);

		// Obere Reihe
		ledsEin(1000, 1, 1, 4);
		
		// Linke Spalte
		ledsEin(1000, 5, 4, 13);
		
		// Rechte Spalte
		ledsEin(1000, 8, 4, 16);
		
		// Untere Reihe
		ledsEin(1000, 14, 1, 15);
		
		warte(1000);
		
		// Obere Reihe
		ledsAus(1, 1, 4);
		
		// Linke Spalte
		ledsAus(5, 4, 13);
		
		// Rechte Spalte
		ledsAus(8, 4, 16);
		
		// Untere Reihe
		ledsAus(14, 1, 15);
		
//		laurasLabor();
	}
	
	
	private void zweierLauflicht(long leuchtDauer, long pausenDauer){
		
		while (1!=2) {
			int j;
			for (int i = 1; i < 17; i++) {
				if (i==16){
					j = 1;
				} else {
					j = i + 1;
				}
				ledEin(leuchtDauer,i,j);
				warte(pausenDauer);
			}
		}
	}
	private void laurasLabor(){
//		leuchte(1, 2000);
//		warte(1000);
//		leuchte(2,2000);
//		warte(1000);
//		leuchte(3,2000);
		
	}

}
