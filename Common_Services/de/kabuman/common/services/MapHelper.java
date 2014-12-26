package de.kabuman.common.services;

import java.util.Map;

public class MapHelper {
	
	@SuppressWarnings("rawtypes")
	Map map;
	
	@SuppressWarnings("rawtypes")
	MapHelper(Map map){
		this.map = map;
	}
	
	public String getString(Object object){
		return (String) map.get(object);
	}

	public Integer getInteger(Object object){
		return (Integer) map.get(object);
	}

}
