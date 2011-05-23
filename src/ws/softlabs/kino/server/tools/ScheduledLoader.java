package ws.softlabs.kino.server.tools;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ws.softlabs.lib.kino.dao.server.intf.DataService;
import ws.softlabs.lib.kino.dao.server.service.pmf.PMF;
import ws.softlabs.lib.kino.dao.server.service.pmf.PMFModelDataService;
import ws.softlabs.lib.kino.model.client.Hall;
import ws.softlabs.lib.kino.model.client.Theater;
import ws.softlabs.lib.parser.server.KinovlruParser;

@SuppressWarnings("serial")
public class ScheduledLoader extends HttpServlet {
		
	private static final Logger log = 
		Logger.getLogger("kino.gwt.service." + ScheduledLoader.class.getSimpleName());
	
	private static boolean  processing  = false;
	private DataService 	dataService = new PMFModelDataService();
	private KinovlruParser 	parser      = new KinovlruParser(dataService);
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		log.debug("ENTER");
		if (processing) {
			log.debug("EXIT (PROCESSING)");
			return;
		}

		boolean isCron = Boolean.parseBoolean(req.getParameter("X-AppEngine-Cron"));
		if (!isCron) {
			log.debug("NON-CRON REQUEST");
			log.debug("EXIT (UNAUTHORIZED)");
			return;
		}
		
		PersistenceManager pm = PMF.getPersistenceManager();
		try {			
//			ScheduleLog scheduleLog = null;
//			Query query = pm.newQuery(ScheduleLog.class);
//			List<ScheduleLog> result = (List<ScheduleLog>)query.execute();
//			
//			if (result != null && !result.isEmpty()) {
//				scheduleLog = result.get(0);
//			} 
//			if (scheduleLog == null) {
//				scheduleLog = new ScheduleLog();
//			}
			processing = true;

			ScheduleLog scheduleLog = new ScheduleLog();
			scheduleLog.lastRunTS = System.currentTimeMillis();
			
			log.debug("START LOAD");
			loadDataFromInternet();
			log.debug("STOP LOAD");
			
			scheduleLog.lastTimeTS = System.currentTimeMillis() - scheduleLog.lastRunTS;
			log.info("load complete in " + scheduleLog.lastTimeTS / 1000 + " seconds");
			pm.makePersistent(scheduleLog);
			
		} catch (Exception e) {
			log.debug("EXIT (EXCEPTION)" + e);
			e.printStackTrace();
			return;
		} finally {		
			processing = false;
			pm.close();
		}
		log.debug("EXIT");
	}	
	public void loadDataFromInternet() {
		log.debug("ENTER");
		try {
			Set<Theater> theaters = parser.getTheaters();
			if (theaters != null)
			for (Theater t : theaters) {
				List<String> days = parser.getTheaterShowDays(t);
				if (days != null) {
					for(String d : days) {
						Set<Hall> halls = parser.getHalls(t);
						if (halls != null)
						for(Hall h : halls) {
							parser.getDayShows(d, h);
						}
					}
				}
			}
		}
		catch(Exception ex) {			
			log.debug("EXCEPTION");
		}
		log.debug("EXIT");
	}
}
