

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailTemplateUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MailTemplateUtil.class);

	/**
	 * Resolve a template and populate place holders with params
	 * 
	 * @param params
	 * @param path
	 * @param session
	 * @return String content of the template with variables value resolved
	 * @throws Exception
	 */
	public static String resolveTemplate(Map<String, Object> params,
			String path, Session session) {

		String html = "";

		try {
			if (session.itemExists(path)) {
				Node node = session.getNode(path);
				if ("nt:file".equals(node.getPrimaryNodeType().getName())) {

					Node content = node.getNode("jcr:content");
					html = content.getProperty("jcr:data").getString();
					StrSubstitutor substitutor = new StrSubstitutor(
							StrLookup.mapLookup(params));
					html = substitutor.replace(html);

				} else {
					throw new Exception(
							"Invalid template primarty type, it must be nt:file");
				}
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return html;
	}
}
