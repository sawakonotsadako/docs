/**
 * Validation Regex Repository
 * Refer to https://www.owasp.org/index.php/OWASP_Validation_Regex_Repository
 */
public class RegexRepository {

	/**
	 * A valid e-mail address
	 */
	// public static final String EMAIL =
	// "^[a-zA-Z0-9+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$]";
	public static final String EMAIL = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";

	/**
	 * A valid URL per the URL spec.
	 */
	public static final String URL = "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))(%[0-9A-Fa-f]{2}|"
			+ "[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)([).!';/?:,][[:blank:]])?$]";
	/**
	 * A valid IP Address
	 */
	public static final String IP = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
			+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$]";

	/**
	 * Lower and upper case letters and all digits
	 */
	public static final String SAFE_TEXT = "^[a-zA-Z0-9 .-]+$]";

	/**
	 * Date in US format with support for leap years
	 */
	public static final String DATE = "^(?:(?:(?:0?[13578]|1[02])(\\/|-|\\.)31)\1|(?:(?:0?[1,3-9]|1[0-2])(\\/|-|\\.)"
			+ "(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:0?2(\\/|-|\\.)29\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|"
			+ "[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:(?:0?[1-9])|(?:1[0-2]))(\\/|-|\\.)"
			+ "(?:0?[1-9]|1\\d|2[0-8])\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$]";
	/**
	 * 4 to 8 character SIMPLE_PW requiring numbers and both lowercase and
	 * uppercase letters
	 */
	public static final String SIMPLE_PW = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{4,8}$]";

	/**
	 * 4 to 32 character COMPLEX_PW requiring at least 3 out 4 (uppercase and
	 * lowercase letters, numbers and special characters) and no more than 2
	 * equal characters in a row
	 */
	public static final String COMPLEX_PW = "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])"
			+ "|(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})[A-Za-z0-9!~<>"
			+ ",;:_=?*+#.\"&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]{8,32}$]";

	/**
	 * A valid telephone number
	 */
	public static final String PHONE_NUMBER = "[^a-zA-Z]*";

	public static final String PHONE_REGEX = "^[\\s()+-]*([0-9][\\s()+-]*){6,14}$";

	public static final String POSTALCODE_NUMBER = "[^a-zA-Z]*";

}
