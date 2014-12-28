package de.kabuman.tinkerforge.screencontroller.sources;

import de.kabuman.tinkerforge.screencontroller.items.ScreenItem;

/**
 * Defines a item source which content is to push. <br>
 * The screen controller gets the value injected whenever it is changed. <br>
 * The source pushes the changed value by itself.
 */
public interface ItemSourceToPush extends ItemSourceToPull{
	
	/**
	 * Add the item to display
	 * @param screenItem - the item which was created by "new ScreenItemImpl(..)" 
	 */
	void addTtem(ScreenItem screenItem);


}
