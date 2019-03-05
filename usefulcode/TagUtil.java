/**
 * 
 */
package sg.gov.spring.enterpriseone.organisation.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author NCS Portal City
 *
 */
public class TagUtil {

	/**
	 * 
	 */
	public TagUtil() {
	}
	
	/**
	 * 
	 * @param thisMap
	 * @return
	 */
	public static String mapToCSVKeys(Map<String, String> thisMap){
		
		if(thisMap != null && !thisMap.isEmpty() ){
			return StringUtils.join(thisMap.keySet(), ",");
		}
		
		return "";
	}
	
	/**
	 * 
	 * @param thisMap
	 * @return
	 */
	public static String mapToCSVValues(Map<String, String> thisMap){
		
		if(thisMap != null && !thisMap.isEmpty() ){
			return StringUtils.join(thisMap.values(), ",");
		}
		
		return "";
	}

	

}
