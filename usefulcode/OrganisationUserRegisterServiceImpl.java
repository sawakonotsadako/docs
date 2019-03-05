/**
 *
 */
package sg.gov.spring.enterpriseone.organisation.impl;

import com.adobe.acs.commons.email.EmailService;
import com.day.cq.commons.Externalizer;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.gov.spring.enterpriseone.core.domain.*;
import sg.gov.spring.enterpriseone.core.exception.UserAlreadyExistException;
import sg.gov.spring.enterpriseone.core.exception.UserSessionNotFoundException;
import sg.gov.spring.enterpriseone.core.jpa.model.*;
import sg.gov.spring.enterpriseone.core.model.EmailLog;
import sg.gov.spring.enterpriseone.core.model.OrganisationAuditLog;
import sg.gov.spring.enterpriseone.core.service.CoreFactoryService;
import sg.gov.spring.enterpriseone.core.service.JpaService;
import sg.gov.spring.enterpriseone.core.util.DateUtil;
import sg.gov.spring.enterpriseone.core.util.PasswordUtil;
import sg.gov.spring.enterpriseone.core.util.RegexRepository;
import sg.gov.spring.enterpriseone.organisation.jpa.service.UserService;
import sg.gov.spring.enterpriseone.organisation.service.*;
import sg.gov.spring.enterpriseone.organisation.util.CaptchaUtil;
import sg.gov.spring.enterpriseone.organisation.util.MessageConstants;
import sg.gov.spring.enterpriseone.organisation.util.RequestUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author NCS Portal City
 *
 */
@Service
@Component(immediate = true, metatype = true, label = "EnterpriseOne Organisation User Registration Service", name = "sg.gov.spring.enterpriseone.organisation.impl.OrganisationUserRegisterServiceImpl")
public class OrganisationUserRegisterServiceImpl implements
		OrganisationUserRegisterService {

	private static final Logger logger = LoggerFactory
			.getLogger(OrganisationUserRegisterServiceImpl.class);

	@Reference
	protected JpaService jpaService;

	@Reference
	protected EmailService emailService;

	@Property(label = "From Email", value = MessageConstants.FROM_EMAIL_DEFAULT)
	private static final String FROM_EMAIL = "from.email";

	@Property(label = "Admin Email", value = MessageConstants.ADMIN_EMAIL_DEFAULT)
	private static final String ADMIN_EMAIL = "admin.email";

	@Property(label = "From Name", value = MessageConstants.FROM_EMAIL_DEFAULT)
	private static final String FROM_NAME = "from.name";

	public static final String EMAIL_SUBJECT_DEFAULT = "Activation for SME Portal Account";
	public static final String EMAIL_DEACTIVATION_SUBJECT_DEFAULT = "Deactivation SME Portal Account";
	public static final String EMAIL_REACTIVATION_SUBJECT_DEFAULT = "Reactivation for SME Portal Account";

	@Property(label = "Email Subject", value = EMAIL_SUBJECT_DEFAULT)
	private static final String EMAIL_SUBJECT = "email.subject";

	@Property(label = "Email Reactivation Subject", value = EMAIL_REACTIVATION_SUBJECT_DEFAULT)
	private static final String EMAIL_REACTIVATION_SUBJECT = "email.reactivation.subject";

	@Property(label = "Email Deactivation Subject", value = EMAIL_DEACTIVATION_SUBJECT_DEFAULT)
	private static final String EMAIL_DEACTIVATION_SUBJECT = "email.deactivation.subject";

	@Property(label = "Activate Token Expire Days", intValue = 7)
	private static final String ACTIVATE_TOKEN_EXPIRE_DAYS = "activate.token.expire.days";

	public static final String EMAIL_TEMPLATE_DEFAULT = "/etc/notification/email/html/registration/en/welcome.html";
	public static final String EMAIL_REACTIVATION_TEMPLATE_DEFAULT = "/etc/notification/email/html/registration/en/reactivation.html";
	public static final String EMAIL_DEACTIVATION_TEMPLATE_DEFAULT = "/etc/notification/email/html/registration/en/deactivation.html";

	@Property(label = "Email Template", value = EMAIL_TEMPLATE_DEFAULT)
	private static final String EMAIL_TEMPLATE = "email.template";

	@Property(label = "Email Reactivation Template", value = EMAIL_REACTIVATION_TEMPLATE_DEFAULT)
	private static final String EMAIL_REACTIVATION_TEMPLATE = "email.reactivation.template";

	@Property(label = "Email Deactivation Template", value = EMAIL_DEACTIVATION_TEMPLATE_DEFAULT)
	private static final String EMAIL_DEACTIVATION_TEMPLATE = "email.deactivation.template";

	public static final String ACTIVATION_PATH_DEFAULT = "/content/smeportal/en/profile/register-activate.html";
	public static final String REACTIVATION_PATH_DEFAULT = "/content/smeportal/en/profile/register-activate.html";

	@Property(label = "Activation Path", value = ACTIVATION_PATH_DEFAULT)
	private static final String ACTIVATION_PATH = "activation.path";

	@Property(label = "Reactivation Path", value = REACTIVATION_PATH_DEFAULT)
	private static final String REACTIVATION_PATH = "reactivation.path";

	private String fromEmail;
	private String adminEmail;
	private String fromName;
	private String emailSubject;
	private String emailReactivationSubject;
	private String emailDeactivationSubject;
	private String emailTemplate;
	private String emailReactivationTemplate;
	private String emailDeactivationTemplate;
	private String activationPath;
	private String reactivationPath;
	private int activateTokenExpireDays;

	@Activate
	protected void activate(ComponentContext ctx) {

		fromEmail = PropertiesUtil.toString(
				ctx.getProperties().get(FROM_EMAIL),
				MessageConstants.FROM_EMAIL_DEFAULT);
		adminEmail = PropertiesUtil.toString(
				ctx.getProperties().get(ADMIN_EMAIL),
				MessageConstants.ADMIN_EMAIL_DEFAULT);
		fromName = PropertiesUtil.toString(ctx.getProperties().get(FROM_NAME),
				MessageConstants.FROM_NAME_DEFAULT);
		emailSubject = PropertiesUtil.toString(
				ctx.getProperties().get(EMAIL_SUBJECT), EMAIL_SUBJECT_DEFAULT);
		emailReactivationSubject = PropertiesUtil.toString(ctx.getProperties()
				.get(EMAIL_REACTIVATION_SUBJECT),
				EMAIL_REACTIVATION_SUBJECT_DEFAULT);
		emailDeactivationSubject = PropertiesUtil.toString(ctx.getProperties()
				.get(EMAIL_DEACTIVATION_SUBJECT),
				EMAIL_DEACTIVATION_SUBJECT_DEFAULT);
		emailTemplate = PropertiesUtil
				.toString(ctx.getProperties().get(EMAIL_TEMPLATE),
						EMAIL_TEMPLATE_DEFAULT);
		emailReactivationTemplate = PropertiesUtil.toString(ctx.getProperties()
				.get(EMAIL_REACTIVATION_TEMPLATE),
				EMAIL_REACTIVATION_TEMPLATE_DEFAULT);
		emailDeactivationTemplate = PropertiesUtil.toString(ctx.getProperties()
				.get(EMAIL_DEACTIVATION_TEMPLATE),
				EMAIL_DEACTIVATION_TEMPLATE_DEFAULT);

		activationPath = PropertiesUtil.toString(
				ctx.getProperties().get(ACTIVATION_PATH),
				ACTIVATION_PATH_DEFAULT);
		reactivationPath = PropertiesUtil.toString(
				ctx.getProperties().get(REACTIVATION_PATH),
				REACTIVATION_PATH_DEFAULT);
		activateTokenExpireDays = PropertiesUtil.toInteger(ctx.getProperties()
				.get(ACTIVATE_TOKEN_EXPIRE_DAYS), 7);

		logger.info("activate fromEmail: " + fromEmail);
		logger.info("adminEmail: " + adminEmail);
		logger.info("activate fromName: " + fromName);
		logger.info("activate emailSubject: " + emailSubject);
		logger.info("reactivate emailReactivationSubject: "
				+ emailReactivationSubject);
		logger.info("deactivate emailDeactivationSubject: "
				+ emailDeactivationSubject);
		logger.info("activate emailTemplate: " + emailTemplate);
		logger.info("reactivate emailReactivationTemplate: "
				+ emailReactivationTemplate);
		logger.info("deactivate emailDeactivationTemplate: "
				+ emailDeactivationTemplate);
		logger.info("activate activationPath: " + activationPath);
		logger.info("reactivate reactivationPath: " + reactivationPath);
	}

	/**
	 *
	 */
	public OrganisationUserRegisterServiceImpl() {
	}

	public AJAXResponse register(SlingHttpServletRequest request) {

		OrganisationUser user = bindForm(request);
		Map<String, String> validationErrors = validateForm(user);
		AJAXResponse ajaxResponse = new AJAXResponse();

		try {

			EnterpriseOneCentralConfigurationService captchaService = EnterpriseOneServiceFactory
					.getInstance().getService(
							EnterpriseOneCentralConfigurationService.class);

			if (captchaService.registerCaptcha()) {
				String captchaAnswer = RequestUtil.getParameter(request,
						"captchaAnswer");
				String captchaKey = RequestUtil.getParameter(request,
						"captchaKey");
				if (StringUtils.isEmpty(captchaAnswer)) {
					ajaxResponse.getError().add("Captcha cannot be empty.");
				} else {
					if (!CaptchaUtil.isValid(captchaKey, captchaAnswer)) {
						ajaxResponse.getError().add(
								"CAPTCHA <br/>Incorrect Captcha characters.");
						ajaxResponse.getField().add("captchaAnswer");
					}
				}
			}

			if (validationErrors.isEmpty() && ajaxResponse.getError().isEmpty()) {
				OrganisationAuditLog auditLog = new OrganisationAuditLog();
				auditLog.setAuditType(String
						.valueOf(OrganisationAuditLogType.Registration
								.ordinal()));
				auditLog.setDateCreated(DateUtil.getDateTimestamp());
				try {

					EntityManager em = jpaService.getEntityManager();

					ServiceToken foundToken = null;
					String inviteToken = RequestUtil.getParameter(request,
							"invite_token");
					String tcENewsLetter = RequestUtil.getParameter(request,
							"tcENewsLetter");
					String tcGebizAlert = RequestUtil.getParameter(request,
							"tcGebizAlert");

					user.setSubscribeNewsLetter("on".equals(tcENewsLetter));
					user.setSubscribeGbizAlert("on".equals(tcGebizAlert));

					if (!StringUtils.isEmpty(inviteToken)) {
						try {
							// check ACRA invite details
							TypedQuery<ServiceToken> tokenQuery = em
									.createNamedQuery(
											"ServiceToken.getByToken",
											ServiceToken.class);
							tokenQuery.setParameter("token", inviteToken);
							List<ServiceToken> acraToken = tokenQuery
									.getResultList();
							if (acraToken != null && !acraToken.isEmpty()) {
								foundToken = acraToken.get(0);
							} else {
								throw new Exception(inviteToken + " not found!");
							}
						} catch (Exception e) {
							logger.error("Exception: ", e);
							logger.info("falling back to default");
						}
					}

					OrganisationUser createdUser = create(user);
					try{
						String primaryIndustry = RequestUtil.getParameter(request, "primaryIndustry");
						String[] secondaryIndustries = request.getParameterValues("secondaryIndustry");

						if (StringUtils.isNoneBlank(primaryIndustry)) {
							Industry industry = em.find(Industry.class,
									Long.valueOf(primaryIndustry));
							UserIndustry primaryUserIndustry = new UserIndustry();
							primaryUserIndustry.setIndustry(industry);
							primaryUserIndustry.setType(IndustryType.PRIMARY.ordinal());
							user.getUserIndustries().add(primaryUserIndustry);
						}

						if (secondaryIndustries != null && secondaryIndustries.length > 0
								&& secondaryIndustries.length <= 2) {
							Industry industry = null;
							for (String secondaryIndustry : secondaryIndustries) {
								industry = em.find(Industry.class,
										Long.valueOf(secondaryIndustry));

								UserIndustry secondaryUserIndustry = new UserIndustry();
								secondaryUserIndustry.setIndustry(industry);
								secondaryUserIndustry.setType(IndustryType.SECONDARY
										.ordinal());

								user.getUserIndustries().add(secondaryUserIndustry);
							}
						}
						em.getTransaction().begin();
						em.merge(createdUser);
						em.getTransaction().commit();
					}catch (Exception e){
						logger.error("Update industries: " + e);
						e.printStackTrace();
					}

					ajaxResponse.getMessage().add(
							"You have been successfully registered!");

					ServiceToken token = new ServiceToken();
					token.setUserId(createdUser.getId());
					token.setType(ServiceTokenType.REGISTER_ACTIVATION
							.ordinal());
					em.getTransaction().begin();
					// Invalidate the token
					if (foundToken != null) {
						try {
							foundToken.setDateAccomplished(DateUtil
									.getDateTimestamp());
						} catch (Exception e) {
							logger.error("", e);
						}
					}
					em.persist(token);
					em.getTransaction().commit();

					auditLog.setEmail(createdUser.getEmail());
					auditLog.setEntry(String.valueOf(createdUser.getId()));

					sendWelcomeEmail(user, request, token);

					if (user != null){
						OrganisationAuditLog auditLogEnews = new OrganisationAuditLog();
						auditLogEnews.setEmail(user.getEmail());
						auditLogEnews.setEntry(String.valueOf(user.getId()));
						if (user.getSubscribeNewsLetter()){
							auditLogEnews.setAuditType(String.valueOf(OrganisationAuditLogType.SubscribeEnews.ordinal()));
						}else {
							auditLogEnews.setAuditType(String.valueOf(OrganisationAuditLogType.UnSubscribeEnews.ordinal()));
						}
						auditLogEnews.setDateCreated(DateUtil.getDateTimestamp());
						auditLogEnews.setContent("Set eNewsLetter status by user.");
						OrganisationServiceFactory.getInstance().getAuditLogService().log(auditLogEnews);

						OrganisationAuditLog auditLogGbiz = new OrganisationAuditLog();
						auditLogGbiz.setEmail(user.getEmail());
						auditLogGbiz.setEntry(String.valueOf(user.getId()));
						if (user.getSubscribeGbizAlert()){
							auditLogGbiz.setAuditType(String.valueOf(OrganisationAuditLogType.SubscribeGebiz.ordinal()));
						}else {
							auditLogGbiz.setAuditType(String.valueOf(OrganisationAuditLogType.UnSubscribeGebiz.ordinal()));
						}
						auditLogGbiz.setDateCreated(DateUtil.getDateTimestamp());
						auditLogGbiz.setContent("Set gBiz status by user.");
						OrganisationServiceFactory.getInstance().getAuditLogService().log(auditLogGbiz);
					}

				} catch (UserAlreadyExistException e) {
					logger.error("", e);
					ajaxResponse.getError().add(e.getMessage());
				} catch (Exception e) {
					logger.error("", e);
					ajaxResponse.getError().add(
							"Unknown error occurred. Please try again later.");
				}

				try {
					if (!StringUtils.isEmpty(auditLog.getEmail())) {
						if (!ajaxResponse.getError().isEmpty()) {
							auditLog.setContent(ajaxResponse.getError().get(0));
						}
						if (!ajaxResponse.getMessage().isEmpty()) {
							auditLog.setContent(ajaxResponse.getMessage()
									.get(0));
						}
						OrganisationServiceFactory.getInstance()
								.getAuditLogService().log(auditLog);
					}
				} catch (Exception e) {
					logger.error("", e);
				}

			} else {
				Iterator<Entry<String, String>> iterator = validationErrors
						.entrySet().iterator();
				logger.info("form validation errors: "
						+ validationErrors.toString());
				while (iterator.hasNext()) {
					Map.Entry<String, String> mapEntry = iterator.next();
					ajaxResponse.getError().add(mapEntry.getValue().toString());
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);
		}
		return ajaxResponse;
	}

	public OrganisationUser bindForm(SlingHttpServletRequest request) {
		OrganisationUser user = new OrganisationUser();
		user.setEmail(RequestUtil.getParameter(request, "email"));
		user.setFirstName(RequestUtil.getParameter(request, "firstName"));
		user.setLastName(RequestUtil.getParameter(request, "lastName"));
		user.setLinkedinId(RequestUtil.getParameter(request, "linkedinId"));


		String birthDay = RequestUtil.getParameter(request, "birthDay");
		String birthMonth = RequestUtil.getParameter(request, "birthMonth");
		String birthYear = RequestUtil.getParameter(request, "birthYear");

		if (StringUtils.isEmpty(birthDay) || StringUtils.isEmpty(birthMonth)
				|| StringUtils.isEmpty(birthYear)) {
			birthDay = "01";
			birthMonth = "01";
			birthYear = "1930";
		}
		String stringBirthDate = birthYear + "-" + birthMonth + "-" + birthDay
				+ " 00:00:00";
		try {

			Date birthDate = DateUtil.parse(DateUtil.TIME_STAMP,
					stringBirthDate);
			user.setBirthDate(DateUtil.format(DateUtil.TIME_STAMP, birthDate));

		} catch (Exception e) {
			logger.error("Invalid birth date: " + e);
		}

		return user;
	}

	/**
	 * Validate the OrganisationUser bean
	 *
	 * @param user
	 * @return list of errors if any
	 */
	public Map<String, String> validateForm(OrganisationUser user) {

		Map<String, String> errors = new HashMap<String, String>();

		Pattern emailPattern = Pattern.compile(RegexRepository.EMAIL);
		if (!emailPattern.matcher(user.getEmail()).matches()) {
			errors.put("email",
					"Invalid Email.  Please key in valid email format e.g name@company.com");
		}

		if (StringUtils.isEmpty(user.getFirstName())) {
			errors.put("firstName", "Firstname is required");
		}

		if (user.getBirthDate() == null) {
			errors.put("birthDate", "Invalid Birthday");
		}

		return errors;

	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws UserAlreadyExistException
	 * @throws Exception
	 */
	public OrganisationUser create(OrganisationUser user)
			throws UserAlreadyExistException {
		try {
			EntityManager em = jpaService.getEntityManager();
			UserService userService = EnterpriseOneServiceFactory.getInstance()
					.getService(UserService.class);
			OrganisationUser existedUser = userService
					.getNonTerminatedUser(user.getEmail());
			if (existedUser != null
					&& OrganisationUserStatus.MIGRATED_USER.ordinal() != existedUser
							.getStatus()
					&& OrganisationUserStatus.ACRA_INVITED_USER.ordinal() != existedUser
							.getStatus()) {
				throw new UserAlreadyExistException("Email " + user.getEmail()
						+ " is already used by a different user!");
			} else if (existedUser != null) {
				user.setId(existedUser.getId());
				user.setDesignation(existedUser.getDesignation());
				user.setCompanyName(existedUser.getCompanyName());
			}

			// if (StringUtils.isEmpty(user.getLinkedinId())) {
			// if (userService.getNonTerminatedUserByField("linkedin_id",
			// user.getEmail()) != null) {
			// throw new UserAlreadyExistException(
			// "Your LinkedIn account is already used by a different user!");
			// }
			// }

			// set initial status to NEW if not via linked in signup
			if (user.getLinkedinId() != null) {
				user.setStatus(OrganisationUserStatus.ACTIVE.ordinal());
			} else {
				user.setStatus(OrganisationUserStatus.NEW.ordinal());
			}

			//SR: Added create and modified date to OrgaanisationUser table
			//user.setRegisterDate(DateUtil.getDateTimestamp());
			user.setCreatedDate(DateUtil.getDateTimestamp());
			user.setCreatedBy(user.getEmail());

			// set temp pass because pass cannot be null
			user.setPassword(PasswordUtil.getDigest(RandomStringUtils
					.randomAlphanumeric(16)));

			if (user.getId() == null) {
				em.getTransaction().begin();
				em.persist(user);
				em.getTransaction().commit();
			} else {
				// Deactivate the existed token
				TypedQuery<ServiceToken> tokenQuery = em.createNamedQuery(
						"ServiceToken.getByUserTypeList", ServiceToken.class);
				tokenQuery.setParameter("userId", user.getId());
				List<Integer> typeList = Arrays.asList(
						ServiceTokenType.REGISTER_ACTIVATION.ordinal(),
						ServiceTokenType.GEBIZ_USER_INVITATION.ordinal(),
						ServiceTokenType.ACRA_INVITATION.ordinal());
				tokenQuery.setParameter("typeList", typeList);
				List<ServiceToken> existedActivateTokens = tokenQuery
						.getResultList();
				em.getTransaction().begin();
				for (ServiceToken token : existedActivateTokens) {
					token.setDateAccomplished(DateUtil.getDateTimestamp());
				}

				em.merge(user);
				em.getTransaction().commit();
			}
		} catch (Exception e) {
			logger.error("", e);
			throw new UserAlreadyExistException(e.getMessage());
		}
		return user;
	}

	/**
	 * Sends welcome email
	 *
	 * @param user
	 * @param request
	 * @param token
	 * @throws Exception
	 */
	public void sendWelcomeEmail(OrganisationUser user,
			SlingHttpServletRequest request, ServiceToken token) {

		try {
			Map<String, String> emailParams = new HashMap<String, String>();

			String toName = user.getFirstName();
			toName += !StringUtils.isEmpty(toName) ? " " + user.getLastName()
					: "";

			ResourceResolver resolver = request.getResourceResolver();
			Externalizer externalizer = resolver.adaptTo(Externalizer.class);
			String link = externalizer.publishLink(resolver, "http",
					activationPath + "?token=" + token.getToken());

			// required params
			emailParams.put("senderEmailAddress", fromEmail);
			emailParams.put("senderName", fromName);

			emailParams.put("link", link);
			emailParams.put("toName", toName);
			emailParams.put("subject", emailSubject);

			if (!StringUtils.isEmpty(user.getLinkedinId())) {
				emailTemplate = emailTemplate.replace("welcome",
						"welcome-linkedin");
			}

			logger.info("templatePath: " + emailTemplate);

			String[] recipients = { user.getEmail() };

			List<String> failureList = emailService.sendEmail(emailTemplate,
					emailParams, recipients);
			if (failureList.isEmpty()) {
				logger.info("Email sent successfully to the recipient");
				try {

					EmailLog log = new EmailLog();
					emailParams.put("template", emailTemplate);
					log.setContent(emailParams.toString());
					log.setDateCreated(DateUtil.getDateTimestamp());
					log.setEmailFrom(emailParams.get("senderEmailAddress"));
					log.setEmailTo(user.getEmail());
					log.setSubject(emailSubject);
					CoreFactoryService.getInstance().getEmailLogService()
							.log(log);

				} catch (Exception e) {
					logger.info("failed saving email log to DB ", e);
				}
			} else {
				logger.info("Email sent failed");
			}
		} catch (Exception e) {
			logger.error("", e);
		}

	}

	/**
	 * Deactivate the user account.
	 *
	 * @param request
	 * @return AjaxObject
	 * @throws Exception
	 */
	public AJAXResponse deactivate(SlingHttpServletRequest request) {
		AJAXResponse ajaxResponse = new AJAXResponse();

		try {
			String reasonOption = RequestUtil.getParameter(request,
					"reasonOption");
			String otherReason = RequestUtil.getParameter(request,
					"otherReason");
			String pwParam = RequestUtil.getParameter(request, "password");

			// Validate required fields.
			if (ajaxResponse.getError().isEmpty()
					&& StringUtils.isEmpty(reasonOption)) {
				ajaxResponse.getError().add(
						"Reason deactivate cannot be empty.");
			}

			if (ajaxResponse.getError().isEmpty()
					&& StringUtils.isEmpty(pwParam)) {
				ajaxResponse.getError().add("Password cannot be empty.");
			}

			OrganisationAuditLog auditLog = new OrganisationAuditLog();
			auditLog.setAuditType(String
					.valueOf(OrganisationAuditLogType.Deactivate.ordinal()));
			auditLog.setDateCreated(DateUtil.getDateTimestamp());

			// Get logged userId.
			final OrganisationUser user = EnterpriseOneServiceFactory
					.getInstance().getService(UserService.class)
					.getLoginedUser(request);

			if (user == null) {
				ajaxResponse.getError().add(
						MessageConstants.INVALID_USER_SESSION);
				throw new UserSessionNotFoundException(ajaxResponse.getError()
						.get(0));
			} else {

				if (!PasswordUtil.match(pwParam, user.getPassword())) {
					ajaxResponse.getError().add("Password is incorrect.");
				}
			}

			auditLog.setEmail(user.getEmail());
			auditLog.setEntry(String.valueOf(user.getId()));

			// Processing deactivate
			if (ajaxResponse.getError().isEmpty()) {
				try {
					// Deactivate user
					EntityManager em = jpaService.getEntityManager();
					em.getTransaction().begin();
					user.setStatus(OrganisationUserStatus.DEACTIVATED.ordinal());

					ServiceToken reactivateToken = new ServiceToken();
					reactivateToken.setUserId(user.getId());
					reactivateToken.setType(ServiceTokenType.REACTIVATION
							.ordinal());
					
					//SR: Added create and modified date to OrgaanisationUser table
					user.setModifiedDate(DateUtil.getDateTimestamp());
					user.setModifiedBy(user.getEmail());

					em.persist(reactivateToken);
					em.merge(user);
					em.getTransaction().commit();

					sendDeactivationEmail(user, reasonOption, otherReason);
					sendReactivationEmail(user, request, reactivateToken);

					ajaxResponse.getMessage().add(
							"Your account has been deactivated!");

				} catch (Exception e) {
					logger.error("", e);
					ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);
				}

				try {
					if (!StringUtils.isEmpty(auditLog.getEmail())) {
						if (!ajaxResponse.getError().isEmpty()) {
							auditLog.setContent(ajaxResponse.getError().get(0));
						}
						if (!ajaxResponse.getMessage().isEmpty()) {
							auditLog.setContent(ajaxResponse.getMessage()
									.get(0));
						}
						OrganisationServiceFactory.getInstance()
								.getAuditLogService().log(auditLog);
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);

		}

		return ajaxResponse;
	}

	/**
	 * Sends deactivation email to admin.
	 *
	 * @param user
	 * @param reasonOption
	 * @param otherReason
	 * @throws Exception
	 */
	public void sendDeactivationEmail(OrganisationUser user,
			String reasonOption, String otherReason) {

		try {
			Map<String, String> emailParams = new HashMap<String, String>();

			String toName = "Admin";

			// required params
			emailParams.put("senderEmailAddress", fromEmail);
			emailParams.put("senderName", fromName);

			emailParams.put("toName", toName);
			emailParams.put("subject", emailDeactivationSubject);

			logger.info("templatePath: " + emailDeactivationTemplate);

			String[] recipients = { adminEmail };

			if (!StringUtils.isEmpty(adminEmail)) {
				if (adminEmail.indexOf(',') > 0) {
					recipients = adminEmail.split(",");
				}
			}

			String userName = user.getFirstName();
			userName += !StringUtils.isEmpty(userName) ? " "
					+ user.getLastName() : "";

			String userEmail = user.getEmail();
			String userReason = reasonOption;
			if (StringUtils.equals(userReason, "Others")) {
				userReason = otherReason;
			}
			emailParams.put("userName", userName);
			emailParams.put("userEmail", userEmail);
			emailParams.put("userReason", userReason);

			boolean hasError = false;
			for (int i = 0; i < recipients.length; i++) {

				List<String> failureList = emailService.sendEmail(
						emailDeactivationTemplate, emailParams, recipients[i]);

				if (!failureList.isEmpty()) {
					hasError = true;
				}
			}

			if (!hasError) {
				logger.info("Email sent successfully to the recipient");
				try {

					EmailLog log = new EmailLog();
					emailParams.put("template", emailDeactivationTemplate);
					log.setContent(emailParams.toString());
					log.setDateCreated(DateUtil.getDateTimestamp());
					log.setEmailFrom(emailParams.get("senderEmailAddress"));
					log.setEmailTo(adminEmail);
					log.setSubject(emailDeactivationSubject);
					CoreFactoryService.getInstance().getEmailLogService()
							.log(log);

				} catch (Exception e) {
					logger.info("failed saving email log to DB ", e);
				}
			} else {
				logger.info("Email sent failed");
			}
		} catch (Exception e) {
			logger.error("", e);
		}

	}

	/**
	 * Sends reactivation email
	 *
	 * @param user
	 * @param request
	 * @param token
	 * @throws Exception
	 */
	public void sendReactivationEmail(OrganisationUser user,
			SlingHttpServletRequest request, ServiceToken token)
			throws Exception {

		try {
			Map<String, String> emailParams = new HashMap<String, String>();

			String toName = user.getFirstName();
			toName += !StringUtils.isEmpty(toName) ? " " + user.getLastName()
					: "";

			ResourceResolver resolver = request.getResourceResolver();
			Externalizer externalizer = resolver.adaptTo(Externalizer.class);
			String link = externalizer.publishLink(resolver, "http",
					reactivationPath + "?token=" + token.getToken());

			// required params
			emailParams.put("senderEmailAddress", fromEmail);
			emailParams.put("senderName", fromName);

			emailParams.put("link", link);
			emailParams.put("toName", toName);
			emailParams.put("subject", emailReactivationSubject);

			logger.info("templateReactivationPath: "
					+ emailReactivationTemplate);

			String[] recipients = { user.getEmail() };

			List<String> failureList = emailService.sendEmail(
					emailReactivationTemplate, emailParams, recipients);
			if (failureList.isEmpty()) {
				logger.info("Email sent successfully to the recipient");

				try {

					EmailLog log = new EmailLog();
					emailParams.put("template", emailReactivationTemplate);
					log.setContent(emailParams.toString());
					log.setDateCreated(DateUtil.getDateTimestamp());
					log.setEmailFrom(emailParams.get("senderEmailAddress"));
					log.setEmailTo(user.getEmail());
					log.setSubject(emailReactivationSubject);
					CoreFactoryService.getInstance().getEmailLogService()
							.log(log);

				} catch (Exception e) {
					logger.info("failed saving email log to DB ", e);
				}

			} else {
				logger.info("Email sent failed");
			}
		} catch (Exception e) {
			logger.error("", e);
		}

	}

	public AJAXResponse resend(SlingHttpServletRequest request) {

		AJAXResponse ajaxResponse = new AJAXResponse();

		try {
			String token = RequestUtil.getParameter(request, "token");

			EntityManager em = jpaService.getEntityManager();
			ServiceToken serviceToken = null;

			if (ajaxResponse.getError().isEmpty() && StringUtils.isEmpty(token)) {
				ajaxResponse.getError()
						.add("Invalid activation token request.");
			} else {

				try {
					TypedQuery<ServiceToken> tokenQuery = em.createNamedQuery(
							"ServiceToken.getByToken", ServiceToken.class);
					tokenQuery.setParameter("token", token);
					List<ServiceToken> serviceTokens = tokenQuery
							.getResultList();
					if (serviceTokens.isEmpty()) {
						ajaxResponse.getError().add(
								"Invalid activation token request.");
					} else {
						serviceToken = serviceTokens.get(0);

						if (!StringUtils.isEmpty(serviceToken
								.getDateAccomplished())) {
							ajaxResponse.getError().add(
									"Invalid activation token request.");
						} else {
							Date tokenCreatedDate = DateUtil
									.parseDbDate(serviceToken.getCreatedDate());
							boolean tokenExpired = DateUtil
									.getDateDiff(tokenCreatedDate, new Date(),
											TimeUnit.DAYS) > activateTokenExpireDays;
							if (tokenExpired) {
								final OrganisationUser user = em.find(
										OrganisationUser.class,
										serviceToken.getUserId());

								serviceToken.setDateAccomplished(DateUtil
										.getDateTimestamp());

								ServiceToken newToken = new ServiceToken();
								newToken.setUserId(user.getId());
								newToken.setType(ServiceTokenType.REGISTER_ACTIVATION
										.ordinal());

								em.getTransaction().begin();
								em.merge(serviceToken);
								em.persist(newToken);
								em.getTransaction().commit();

								sendWelcomeEmail(user, request, newToken);

								ajaxResponse
										.getMessage()
										.add("New Token has been sent to your email!");
							} else {
								ajaxResponse
										.getError()
										.add("Activation token request has not expired yet.");
							}
						}
					}
				} catch (Exception e) {
					logger.error("", e);
					ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);
				}
			}

			OrganisationAuditLog auditLog = new OrganisationAuditLog();
			auditLog.setAuditType(String
					.valueOf(OrganisationAuditLogType.ResendActivationToken
							.ordinal()));
			auditLog.setDateCreated(DateUtil.getDateTimestamp());
			if (ajaxResponse.getError().isEmpty()) {

				try {
					if (!StringUtils.isEmpty(auditLog.getEmail())) {
						if (!ajaxResponse.getError().isEmpty()) {
							auditLog.setContent(ajaxResponse.getError().get(0));
						}
						if (!ajaxResponse.getMessage().isEmpty()) {
							auditLog.setContent(ajaxResponse.getMessage()
									.get(0));
						}
						logger.info(auditLog.toString());
						OrganisationServiceFactory.getInstance()
								.getAuditLogService().log(auditLog);
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);
		}

		return ajaxResponse;
	}

	public AJAXResponse activate(SlingHttpServletRequest request) {

		AJAXResponse ajaxResponse = new AJAXResponse();

		try {
			String token = RequestUtil.getParameter(request, "token");
			String pwParam = RequestUtil.getParameter(request, "password");
			String verifyPwParam = RequestUtil.getParameter(request,
					"verifyPassword");

			EntityManager em = jpaService.getEntityManager();
			ServiceToken serviceToken = null;

			if (ajaxResponse.getError().isEmpty() && StringUtils.isEmpty(token)) {
				ajaxResponse.getError()
						.add("Invalid activation token request.");
			} else {

				try {
					TypedQuery<ServiceToken> tokenQuery = em.createNamedQuery(
							"ServiceToken.getByToken", ServiceToken.class);
					tokenQuery.setParameter("token", token);
					List<ServiceToken> serviceTokens = tokenQuery
							.getResultList();
					if (serviceTokens.isEmpty()) {
						ajaxResponse.getError().add(
								"Invalid activation token request.");
					} else {
						serviceToken = serviceTokens.get(0);

						if (!StringUtils.isEmpty(serviceToken
								.getDateAccomplished())) {
							ajaxResponse.getError().add(
									"Invalid activation token request.");
						} else {
							Date tokenCreatedDate = DateUtil
									.parseDbDate(serviceToken.getCreatedDate());
							boolean tokenExpired = DateUtil
									.getDateDiff(tokenCreatedDate, new Date(),
											TimeUnit.DAYS) > activateTokenExpireDays;
							if (tokenExpired) {
								ajaxResponse
										.getError()
										.add("Your activation link has expired. "
												+ "Please click <a href='#' onclick='resendToken();'>here</a> "
												+ "to re-send activation email.");
							}
						}
					}

				} catch (Exception e) {
					logger.error("", e);
					ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);
				}
			}
			if (ajaxResponse.getError().isEmpty()
					&& StringUtils.isEmpty(pwParam)) {
				ajaxResponse.getError().add("Password cannot be empty.");
			} else {

				if (ajaxResponse.getError().isEmpty()
						&& !pwParam.equals(verifyPwParam)) {
					ajaxResponse.getError()
							.add("Password Verify do not match.");
				}
			}

			if (ajaxResponse.getError().isEmpty()
					&& !PasswordUtil.validatePassword(pwParam).isEmpty()) {
				ajaxResponse
						.getError()
						.add("Invalid password format, please refer to password policy.");
			}

			OrganisationAuditLog auditLog = new OrganisationAuditLog();
			auditLog.setAuditType(String
					.valueOf(OrganisationAuditLogType.RegisterActivation
							.ordinal()));
			auditLog.setDateCreated(DateUtil.getDateTimestamp());

			if (ajaxResponse.getError().isEmpty()) {
				try {
					final OrganisationUser user = em.find(
							OrganisationUser.class, serviceToken.getUserId());
					em.getTransaction().begin();

					auditLog.setEmail(user.getEmail());
					auditLog.setEntry(String.valueOf(user.getId()));

					user.setStatus(OrganisationUserStatus.ACTIVE.ordinal());
					user.setResetPwFlag(false);
					pwParam = PasswordUtil.getDigest(pwParam);
					user.setPassword(pwParam);
					user.setPasswordLastUpdateDate(DateUtil.getDateTimestamp());
					
					//SR: Added create and modified date to OrgaanisationUser table
					if(user.getRegisterDate() == null){
						user.setRegisterDate(DateUtil.getDateTimestamp());
					}
					user.setModifiedDate(DateUtil.getDateTimestamp());
					user.setModifiedBy(user.getEmail());

					initUserAlertSubscription(user);

					serviceToken.setDateAccomplished(DateUtil
							.getDateTimestamp());
					em.getTransaction().commit();

					ajaxResponse.getMessage().add(
							"Account has been successfully activated.");

				} catch (Exception e) {
					logger.error("", e);
					ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);
				}

				try {
					if (!StringUtils.isEmpty(auditLog.getEmail())) {
						if (!ajaxResponse.getError().isEmpty()) {
							auditLog.setContent(ajaxResponse.getError().get(0));
						}
						if (!ajaxResponse.getMessage().isEmpty()) {
							auditLog.setContent(ajaxResponse.getMessage()
									.get(0));
						}
						logger.info(auditLog.toString());
						OrganisationServiceFactory.getInstance()
								.getAuditLogService().log(auditLog);
					}
				} catch (Exception e) {
					logger.error("", e);
				}

			}
		} catch (Exception e) {
			logger.error("", e);
			ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);
		}

		return ajaxResponse;
	}

	private void initUserAlertSubscription(OrganisationUser user) {

		if (user.getSubscribeGbizAlert() == null
				|| user.getSubscribeGbizAlert() == false
				|| user.getId() == null || user.getId() <= 0) {
			return;
		}

		EntityManager em = jpaService.getEntityManager();
		sg.gov.spring.enterpriseone.core.jpa.model.OrganisationUser jpaUser = em
				.find(sg.gov.spring.enterpriseone.core.jpa.model.OrganisationUser.class,
						user.getId());

		if (jpaUser != null) {
			// Get all gebiz categories
			GeBizAlertCategoryService gebizService = EnterpriseOneServiceFactory
					.getInstance().getService(GeBizAlertCategoryService.class);

			boolean isEmptyUserGebiz = jpaUser.getGebizCategories() == null
					|| jpaUser.getGebizCategories().size() == 0;

			if (isEmptyUserGebiz) {
				List<GebizCategory> gebizCategories = gebizService.getAll();
				if (gebizCategories != null && gebizCategories.size() > 0) {
					em.getTransaction().begin();
					for (GebizCategory item : gebizCategories) {
						jpaUser.getGebizCategories().add(item);
					}
					em.getTransaction().commit();
				}
			}
		}
	}

	/**
	 * Sends reactivate email
	 *
	 * @param user
	 * @param request
	 * @param token
	 * @throws Exception
	 */
	public void sendReactivateEmail(OrganisationUser user,
			SlingHttpServletRequest request, ServiceToken token) {

		try {

			Map<String, String> emailParams = new HashMap<String, String>();

			String toName = user.getFirstName();
			toName += !StringUtils.isEmpty(toName) ? " " + user.getLastName()
					: "";

			ResourceResolver resolver = request.getResourceResolver();
			Externalizer externalizer = resolver.adaptTo(Externalizer.class);
			String link = externalizer.publishLink(resolver, "http",
					activationPath + "?token=" + token.getToken());

			// required params
			emailParams.put("senderEmailAddress", fromEmail);
			emailParams.put("senderName", fromName);

			emailParams.put("link", link);
			emailParams.put("toName", toName);
			emailParams.put("subject", emailSubject);

			if (!StringUtils.isEmpty(user.getLinkedinId())) {
				emailTemplate = emailTemplate.replace("welcome",
						"welcome-linkedin");
			}

			logger.info("templatePath: " + emailTemplate);

			String[] recipients = { user.getEmail() };

			List<String> failureList = emailService.sendEmail(emailTemplate,
					emailParams, recipients);
			if (failureList.isEmpty()) {
				logger.info("Email sent successfully to the recipient");
				try {

					EmailLog log = new EmailLog();
					emailParams.put("template", emailTemplate);
					log.setContent(emailParams.toString());
					log.setDateCreated(DateUtil.getDateTimestamp());
					log.setEmailFrom(emailParams.get("senderEmailAddress"));
					log.setEmailTo(user.getEmail());
					log.setSubject(emailSubject);
					CoreFactoryService.getInstance().getEmailLogService()
							.log(log);

				} catch (Exception e) {
					logger.info("failed saving email log to DB ", e);
				}
			} else {
				logger.info("Email sent failed");
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public AJAXResponse getTokenDetails(SlingHttpServletRequest request) {

		AJAXResponse ajaxResponse = new AJAXResponse();
		try {

			String inviteToken = RequestUtil.getParameter(request,
					"invite_token");
			if (StringUtils.isEmpty(inviteToken)) {
				ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);
			} else {
				// check ACRA invite first
				try {

					EntityManager em = jpaService.getEntityManager();
					TypedQuery<ServiceToken> tokenQuery = em.createNamedQuery(
							"ServiceToken.getByToken", ServiceToken.class);
					tokenQuery.setParameter("token", inviteToken);
					List<ServiceToken> foundToken = tokenQuery.getResultList();
					ServiceToken token = null;
					if (foundToken != null && !foundToken.isEmpty()) {
						token = foundToken.get(0);
						if (token.getDateAccomplished() != null) {
							throw new Exception(inviteToken
									+ " has been used before!");
						}
					} else {
						throw new Exception(inviteToken + " not found!");
					}

					TypedQuery<ACRAInvite> acraQuery = em.createNamedQuery(
							"ACRAInvite.getByServiceToken", ACRAInvite.class);
					acraQuery.setParameter("serviceToken", token.getId());

					List<ACRAInvite> inviteFound = acraQuery.setMaxResults(1)
							.getResultList();
					if (inviteFound != null && !inviteFound.isEmpty()) {
						ACRAInvite invite = inviteFound.get(0);
						ajaxResponse.getInfo().add(invite);
					} else {
						logger.error("token id " + token.getId()
								+ " not found!");
					}
				} catch (Exception e) {
					logger.error("", e);
					ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);
				}

			}
		} catch (Exception e) {
			logger.error("", e);
			ajaxResponse.getError().add(MessageConstants.UNKNOWN_ERROR);
		}

		return ajaxResponse;
	}

}
