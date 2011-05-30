package ws.softlabs.kino.server.tools;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ws.softlabs.lib.kino.dao.server.service.pmf.PMF;
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
		
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			Query query = pm.newQuery(ScheduleLog.class);
			query.deletePersistentAll();
			log.debug("CLEARED LOG DATA");
		} catch (Exception ex) {
			log.debug("EXIT (EXCEPTION)" + ex);
		} finally {
			pm.close();
		}
		
		log.debug("EXIT");
		
		resp.sendRedirect("/");
	}
}
