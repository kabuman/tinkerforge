package de.kabuman.tinkerforge.screencontroller.sources;

/**
 * Defines a item source which content is to pull. <br>
 * The screen controller collects the value of this item periodically.
 */
public interface ItemSourceToPull {
	
	/**
	 * Returns the item value of this item source
	 * @return Object - the content
	 */
	Object getItemValue();
	
}
