package de.kabuman.tinkerforge.screencontroller.items;


public interface FieldItem extends Item {

	/**
	 * Returns line id
	 * @return lineId
	 */
	int getLineId();

	/**
	 * Returns position
	 * @return position
	 */
	int getPosition();
	

}
