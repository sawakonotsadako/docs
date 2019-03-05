package sg.gov.spring.enterpriseone.core.util;

import org.apache.sling.api.SlingHttpServletRequest;

/**
 * @author NCS Portal City
 *
 */
public class RequestUtil {
	/**
	 * Get and trim parameter from request object
	 * 
	 * @param request
	 * @param name
	 * @return null if name does exist in request object otherwise string value
	 *         of the name parameter
	 */
	public static String getParameter(SlingHttpServletRequest request,
			String name) {

		String param = request.getParameter(name);
		if (param != null) {
			return param.trim();
		} else {
			return null;
		}

	}

	public static String getParameter(SlingHttpServletRequest request,
			String name, String defaultValue) {
		String param = request.getParameter(name);
		if (param != null) {
			return param.trim();
		} else {
			return defaultValue;
		}
	}

}