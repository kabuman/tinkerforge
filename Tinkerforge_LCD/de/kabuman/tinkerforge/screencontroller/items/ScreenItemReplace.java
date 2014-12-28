package de.kabuman.tinkerforge.screencontroller.items;

public interface ScreenItemReplace {
	
	/**
	 * Replace the item source for this item
	 * @param oldItemSource - the old item source instance
	 * @param newItemSource - the new item source instance
	 * @return boolean - true: replaced, false: not replaced
	 */
	boolean replaceItemSource(Object oldItemSource, Object newItemSource);


}
