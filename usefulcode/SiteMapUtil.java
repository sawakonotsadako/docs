/**
 * 
 */
package sg.gov.spring.enterpriseone.organisation.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.day.cq.wcm.api.Page;

/**
 * @author NCS Portal City
 *
 */
public class SiteMapUtil {

	public static final String ETC_BASE = "/etc/designs/enterpriseone/corp/";
	public static final String CONTENT_BASE = "/content/smeportal/en/";
	public static final String DAM_BASE = "/content/dam/smeportal/corp/";
	public static final String PAGE_EXTENSION = ".html";
	public static final String DATE_FORMAT = "dd MMMMM yyyy";
	public static final String DATETIME_FORMAT = "dd MMMMM yyyy, h.mmtt";

	public static enum NAVIGATION_TYPE {
		parents, siblings, children
	}

	private SiteMapUtil() {
	}

	public static String getPageTitle(Page oPage) {
		String sTitle = "";
		sTitle = oPage.getNavigationTitle();
		if (StringUtils.isEmpty(sTitle)) {
			sTitle = oPage.getTitle();
		}
		if (StringUtils.isEmpty(sTitle)) {
			sTitle = oPage.getName();
		}
		return escapeHTML(sTitle);
	}

	public static boolean isClickable(Page oPage) {
		boolean bClickable = true;
		if ("true".equalsIgnoreCase(oPage.getProperties()
				.get("./notClickable", "").trim())) {
			bClickable = false;
		}
		return bClickable;
	}

	public static boolean isNewWindow(Page oPage) {
		boolean bNewWindow = false;
		if ("true".equalsIgnoreCase(oPage.getProperties()
				.get("./newWindow", "").trim())) {
			bNewWindow = true;
		}
		return bNewWindow;
	}

	public static String getExternalPath(Page oPage) {
		String sExternalPath = "";
		if (!"".equals(oPage.getProperties().get("./externalPath", "").trim())) {
			sExternalPath = oPage.getProperties().get("./externalPath", "");
		}
		return sExternalPath;
	}

	public static String escapeHTML(String strHTML) {
		String result = strHTML;
		if (result != null) {
			result = StringEscapeUtils.escapeHtml4(result).trim();
		} else {
			result = "";
		}
		return result;
	}

	public static String truncate(String strTextParam, int intNoCharactersParam) {
		String strSuffix = "";
		String strText = strTextParam;
		int intNoCharacters = intNoCharactersParam;

		if (intNoCharacters == 0) {
			intNoCharacters = 500;
		}
		if (StringUtils.isNotEmpty(strText)) {
			strText = strText.trim();
			if (strText.length() > intNoCharacters) {
				strSuffix = "...";
			}
			strText = strText.substring(0, intNoCharacters) + strSuffix;
		}
		return strText;
	}

}
