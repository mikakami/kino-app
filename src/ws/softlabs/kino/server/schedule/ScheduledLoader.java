package ws.softlabs.kino.server.schedule;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
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
	
	private static boolean processing = false;
	private static long PARSE_TIMEOUT = 1000 * 60;// * 60 * 6 ; // 3 HOURS
									  // ms     s    m   h
	private DataService 	dataService = new PMFModelDataService();
	private KinovlruParser 	parser      = new KinovlruParser(dataService);
	
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		log.debug("ENTER");
		if (processing) {
			log.debug("EXIT (PROCESSING)");
			return;
		}

		/*
		boolean isCron = Boolean.parseBoolean(req.getParameter("X-AppEngine-Cron"));
		if (!isCron) {
			log.debug("NON-CRON REQUEST");
			return;
		}/**/
		
		boolean startLoad = false;		
		PersistenceManager pm = PMF.getPersistenceManager();
		try {			
			ScheduleLog scheduleLog = null;
			Query query = pm.newQuery(ScheduleLog.class);
			List<ScheduleLog> result = (List<ScheduleLog>)query.execute();
			if (result != null && !result.isEmpty()) {
				scheduleLog = result.get(0);
			} 
			
			if (scheduleLog != null) {
				if (scheduleLog.lastRunTS < System.currentTimeMillis() - PARSE_TIMEOUT) {
					log.debug("STARTING LOAD...");
					startLoad = true;
				}
				else {
					log.debug("WAITING FOR TIMEOUT...");
					log.debug("EXIT (WAIT)");
					return;
				}
			} else {
				scheduleLog = new ScheduleLog();
				startLoad = true;
			}
			
			if (startLoad) {
				processing = true;
				log.debug("START LOAD");
				scheduleLog.lastRunTS = System.currentTimeMillis();
				loadDataFromInternet();
				scheduleLog.lastTimeTS = System.currentTimeMillis() - scheduleLog.lastRunTS;
				log.debug("STOP LOAD");
				log.debug("load complete in " + scheduleLog.lastTimeTS / 1000 + " seconds");
				pm.makePersistent(scheduleLog);
				processing = false;
			}			
		} catch (Exception e) {
			log.debug("EXIT (EXCEPTION)" + e);
			e.printStackTrace();
			return;
		} finally {			
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
				if (days != null)
				for(String d : days) {
					Set<Hall> halls = parser.getHalls(t);
					if (halls != null)
					for(Hall h : halls) {
						parser.getDayShows(d, h);
					}
				}
			}
		}
		catch(Exception ex) {			
			log.debug("EXCEPTION");
		}
		log.debug("EXIT");
	}
	
	/*
	public void printLoadedData() {
		try {		
			for(Theater t : dataService.getTheaterList()) {
				System.out.println(t);
				for(String d : dataService.getShowDaysList(t)) {
					System.out.println("\t" + d);
					for(Hall h : dataService.getTheaterHallList(t)) {
						System.out.println("\t\t" + h);
						for(Show s : 
							dataService.getShowList(h, 
								DateUtils.stringToDate(d))) {
							System.out.println("\t\t\t" + s);
						}
					}
				}			
			}
		}
		catch(Exception ex) {			
		}
	}	
	/**/	
}
