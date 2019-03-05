/**
 * 
 */
package sg.gov.spring.enterpriseone.organisation.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import flexjson.JSONSerializer;

/**
 * JSON wrapper utility class for JSONSerializer 
 * @author NCS Portal City
 *
 */
public class JsonUtil {

	public static String toJson(List<Object> list){
		
		String json = new JSONSerializer().exclude("class").deepSerialize(list);
		return json;
	}
	
	public static String toJson(List<Object> list, List<String> includes, List<String> excludes){
		
		excludes.add("class");//by default exclude this 
		String json = new JSONSerializer().include(StringUtils.join(includes, ","))
					.exclude(StringUtils.join(excludes, ",")).deepSerialize(list);
		return json;
	}

}
