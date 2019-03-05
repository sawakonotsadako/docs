/**
 * 
 */
package sg.gov.spring.enterpriseone.organisation.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author NCS Portal City
 *
 */
public class CaptchaUtil {

	/**
	 * Validates the answer
	 * @param key
	 * @param answer
	 * @return
	 */
	public static boolean isValid(String key, String answer){
		
		final String minsCurrent = "" + (System.currentTimeMillis() / (60 * 1000));
        final String minsOld = "" + (System.currentTimeMillis() / (60 * 1000) - 1);
        
        final String captchaCurrent = (DigestUtils.sha256Hex("" + (key + minsCurrent))).substring(1, 6);
        final String captchaOld = (DigestUtils.sha256Hex("" + (key + minsOld))).substring(1, 6);
        
        if (!answer.equals(captchaCurrent) && !answer.equals(captchaOld)) {
        	return false;
        }
		return true;
	}

}
