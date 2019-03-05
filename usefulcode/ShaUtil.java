package sg.gov.spring.enterpriseone.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ShaUtil {
	private static final Logger logger = LoggerFactory.getLogger(ShaUtil.class);

	private ShaUtil() {
	}

	public static String sha256Encode(String data) {
		if (StringUtils.isNotEmpty(data)) {
			try {
				MessageDigest md5 = MessageDigest.getInstance("SHA-256");
				byte[] hash = md5.digest(data.getBytes("UTF-8"));
				StringBuilder sb = new StringBuilder(2 * hash.length);
				for (byte b : hash) {
					sb.append(String.format("%02x", b & 0xff));
				}
				return sb.toString();
			} catch (NoSuchAlgorithmException e) {
				logger.info("" + e);
				return null;
			} catch (UnsupportedEncodingException e) {
				logger.info("" + e);
				return null;
			}
		}
		return data;
	}
}
