/**
 * 
 */
package sg.gov.spring.enterpriseone.organisation.util;

import org.apache.sling.api.SlingHttpServletRequest;

import com.adobe.granite.xss.XSSAPI;

/**
 * @author NCS Portal City
 *
 */
public class RequestUtil {
	
	/**
	 * Get and trim parameter from request object
	 * @param request
	 * @param name
	 * @return null if name does exist in request object otherwise string value of the name parameter
	 */
	public static String getParameter(SlingHttpServletRequest request, String name ){

		String param = (String) request.getParameter(name) ;
		if(param != null){
			return param.trim();
		}else{
			return null;
		}
		
	}

	/**
	 * Get parameter and implement HTML encoding from request object
	 * @param request
	 * @param name
	 * @return null if name does exist in request object otherwise string value of the name parameter
	 */
	public static String getParameterEncodeForHTML(SlingHttpServletRequest request, String name ){

		XSSAPI xssAPI = request.getResourceResolver().adaptTo(XSSAPI.class);
		String param = xssAPI.encodeForHTML((String) request.getParameter(name));
		if(param != null){
			return param.trim();
		}else{
			return null;
		}
		
	}

}
