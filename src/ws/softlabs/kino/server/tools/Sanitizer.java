package ws.softlabs.kino.server.tools;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ws.softlabs.lib.kino.dao.server.service.pmf.PMFModelDataService;

@SuppressWarnings("serial")
public class Sanitizer extends HttpServlet {

	private static final Logger log = 
		Logger.getLogger("kino.gwt.service." + Sanitizer.class.getSimpleName());

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
									throws ServletException, IOException {
		log.debug("ENTER");
		(new PMFModelDataService()).clearDatabase();
		log.debug("CLEARED DATASTORE");
		log.debug("EXIT");
	}
}
