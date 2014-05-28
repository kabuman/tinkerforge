package de.kabuman.common.services;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides a little bit of statistic for double values
 * - Counter
 * - Minimum
 * - Maximum
 * - Average of the last 100 values (or lower)
 */
public class MinMaxServiceImpl implements MinMaxService {
	
	int count = 0;
	Double min = null;
	Double max = null;
	
	List<Double> values = new LinkedList<Double>();

	@Override
	public void regardValue(double value) {
		count++;
		
		// Min
		if (min != null){
			if (min > value){
				min = value;
			} else {
			}
		} else {
			min = value;
		}
		
		// Max
		if (max != null){
			if (max < value){
				max = value;
			} else {
			}
		} else {
			max = value;
		}
		
		// Latest 100 Values only
		if (count > 100){
			values.remove(0);
		}
		values.add(value);
	}
	

	@Override
	public Double getMin() {
		return min;
	}

	
	@Override
	public Double getMax() {
		return max;
	}

	
	@Override
	public Double getAverage() {
		Double sum = 0d;
		for (Double value : values) {
			sum = sum + value;
		}
		return (double) (Math.round((sum / values.size()) * 10)) / 10 ;
		
//		(int)Math.round(value * 0.1);
	}


	@Override
	public int getCount() {
		return count;
	}

}
