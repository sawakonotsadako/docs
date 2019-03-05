

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(DateTimeUtil.class);

	private DateTimeUtil() {
	}

	public static String format(String datetime, String inputFormat,
			String outputFormat) {
		DateFormat inputFormatter = new SimpleDateFormat(inputFormat);
		DateFormat outputFormatter = new SimpleDateFormat(outputFormat);
		String output = "";
		try {
			Date date = inputFormatter.parse(datetime);
			output = outputFormatter.format(date);
		} catch (Exception e) {
			logger.info("" + e);
		}
		return output;
	}

	public static String formatFromDbToCommon(String datetime) {
		String inputFormat = "yyyy-MM-dd hh:mm:ss";
		String outputFormat = "dd MMM yyyy";
		return format(datetime, inputFormat, outputFormat);
	}
}
