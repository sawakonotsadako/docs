/**
 * 
 */
package sg.gov.spring.enterpriseone.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

/**
 * @author NCS Portal City
 *
 */
public class DateUtil {

	public static final String TIME_STAMP = "yyyy-MM-dd HH:mm:ss";
	public static final String TIME_USER_FORMAT = "dd/MM/yyyy HH:mm a";
	public static final String DATE_USER_FORMAT = "dd/MM/yyyy";

	public static String reformat(String datetimeString, String fromFormat,
			String toFormat) throws ParseException {
		String fromFormatStr = "";
		String toFormatStr = "";
		
		if (StringUtils.isEmpty(datetimeString)) {
			return "";
		}
		if (StringUtils.isEmpty(fromFormat)) {
			fromFormatStr = TIME_STAMP;
		}
		if (StringUtils.isEmpty(toFormat)) {
			toFormatStr = TIME_USER_FORMAT;
		}

		Date datetime = parse(fromFormatStr, datetimeString);
		return format(toFormatStr, datetime);
	}

	/**
	 * 
	 * @param date1
	 * @param date2
	 * @param timeUnit
	 * @return
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * @param dbDateString
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDbDate(String dbDateString) throws ParseException {
		return parse(TIME_STAMP, dbDateString);
	}

	/**
	 * 
	 * @return
	 */
	public static String getDateTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	/**
	 * 
	 * @param format
	 * @param thisDate
	 * @return
	 */
	public static String format(String format, Date thisDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(thisDate);
	}

	/**
	 * 
	 * @param format
	 * @param thisDateString
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String format, String thisDateString)
			throws ParseException {
		SimpleDateFormat parser = new SimpleDateFormat(format);
		parser.setLenient(false);
		return parser.parse(thisDateString);
	}

}
