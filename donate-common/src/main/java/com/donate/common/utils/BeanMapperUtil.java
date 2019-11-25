package com.donate.common.utils;


import org.dozer.DozerBeanMapper;
import java.util.*;

public class BeanMapperUtil {
	private static DozerBeanMapper dozer = new DozerBeanMapper();

	public static <T> T map(Object sourceObject, Class<T> destObjectclazz) {
		return ((sourceObject == null) ? null : dozer.map(sourceObject, destObjectclazz));
	}

	public static <T, S> List<T> mapList(Collection<S> sourceList, Class<T> destObjectclazz) {
		if (sourceList == null) {
			return null;
		}
		List destinationList = new ArrayList();
		for (Iterator it = sourceList.iterator(); it.hasNext();) {
			destinationList.add(map(it.next(), destObjectclazz));
		}
		return destinationList;
	}
	public static <T> T mapToBean(Map<String,Object> map,Class<T> destObjectclazz)throws Exception {
		if (map == null)  
            return null;  
  
        Object obj = destObjectclazz.newInstance();  
  
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);  
       
        return ((obj == null) ? null : dozer.map(obj, destObjectclazz));  
	}
	
	public static <T> T mapToBeanWithString(Map<String,String> map,Class<T> destObjectclazz)throws Exception {
		if (map == null)  
            return null;  
  
        Object obj = destObjectclazz.newInstance();  
  
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);  
       
        return ((obj == null) ? null : dozer.map(obj, destObjectclazz));  
	}
}