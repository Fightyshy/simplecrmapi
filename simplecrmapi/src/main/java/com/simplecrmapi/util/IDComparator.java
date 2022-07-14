package com.simplecrmapi.util;

import java.lang.reflect.Method;

public class IDComparator<T>{

	public boolean EqualsID(T o1, T o2) {
		try {
		String objName1 = o1.getClass().getName();
		String objName2 = o2.getClass().getName();
		Method obj1 = o1.getClass().getDeclaredMethod("getId", o1.getClass().getClasses());
		int id1 = (int) obj1.invoke(obj1);
		Method obj2 = o2.getClass().getDeclaredMethod("getId", o2.getClass().getClasses());
		int id2 = (int) obj2.invoke(obj2);
		
		if(objName1.equals(objName2)&&(id1==id2)) {
			return true;
		}else {
			return false;
		}
		
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
