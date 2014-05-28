package de.kabuman.tinkerforge.alarm.items.digital.input;

import de.kabuman.common.services.MinMaxService;
import de.kabuman.common.services.MinMaxServiceImpl;

public class ItemImpl implements Item{

	// Statistic Purpose
	private MinMaxService minMaxService;

	public ItemImpl(){
		minMaxService = new MinMaxServiceImpl();
	}
	
	@Override
	public Double getAverageValue() {
		return minMaxService.getAverage();
	}

	@Override
	public Double getMinimumValue() {
		return minMaxService.getMin();
	}

	@Override
	public Double getMaximumValue() {
		return minMaxService.getMax();
	}

	@Override
	public int getCounter() {
		return minMaxService.getCount();
	}
	
	protected void regardValue(double value){
		minMaxService.regardValue(value);
	}

}
