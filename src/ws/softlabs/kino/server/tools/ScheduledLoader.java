package ws.softlabs.kino.server.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.softlabs.lib.kino.dao.server.intf.DataService;
import ws.softlabs.lib.kino.dao.server.service.pmf.PMF;
import ws.softlabs.lib.kino.dao.server.service.pmf.PMFModelDataService;
import ws.softlabs.lib.kino.model.client.Hall;
import ws.softlabs.lib.kino.model.client.Show;
import ws.softlabs.lib.kino.model.client.Theater;
import ws.softlabs.lib.parser.server.KinovlruParser;

@SuppressWarnings("serial")
public class ScheduledLoader extends HttpServlet {
		
	private static final Logger log = 
		LoggerFactory.getLogger("kino.gwt.service." + ScheduledLoader.class.getSimpleName());
	private static final Logger log2 = 
		LoggerFactory.getLogger("kino.gwt.service." + ScheduledLoader.class.getSimpleName() + ".2");
	
	private static boolean  processing  = false;
	private DataService 	dataService = new PMFModelDataService();
	private KinovlruParser 	parser      = new KinovlruParser(dataService);
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		log.debug("ENTER");
		
		if (processing) {
			log.debug("EXIT (PROCESSING)");
			resp.sendRedirect("/");
			return;
		}
/*		boolean isCron = Boolean.parseBoolean(req.getParameter("X-AppEngine-Cron"));
		if (!isCron) {
			log.debug("NON-CRON REQUEST");
			log.debug("EXIT (UNAUTHORIZED)");
			return;
		}
/**/
		PersistenceManager pm = PMF.getPersistenceManager();
		processing = true;
		try {			
			ScheduleLog scheduleLog = getNextScheduled(pm, true);
			if (scheduleLog != null) {
				log.debug("START LOAD FOR " + scheduleLog.getTheater().getName());
				scheduleLog.setLastRun(new Date(System.currentTimeMillis()));
				
				loadDataForTheater(scheduleLog.getTheater());
				
				scheduleLog.setLoadTime((System.currentTimeMillis() - 
												 scheduleLog.getLastRun().getTime())/1000);
				log.debug("FINISHED LOAD");
				log.info("load complete in " + scheduleLog.loadTime + " seconds");
				
				pm.makePersistent(scheduleLog);
			} else {
				log.warn("can't get next scheduled action!");
			}
		} catch (Exception e) {
			log.debug("EXIT (EXCEPTION)" + e);
		} finally {		
			processing = false;
			pm.close();
		}
		log.debug("EXIT");
		resp.sendRedirect("/");
	}	

	@SuppressWarnings("unchecked")
	private ScheduleLog getNextScheduled(PersistenceManager pm, boolean firstRun) {
		log.debug("ENTER"); 		
		ScheduleLog scheduleLog = null;
		Query query = pm.newQuery(ScheduleLog.class);
		query.setOrdering("lastRun");
		List<ScheduleLog> result = (List<ScheduleLog>)query.execute();
		log.debug("query executed");		
		if (result == null || result.isEmpty()) {
			log.debug("got no results");
			if (firstRun) {
				log.debug("first run: creating schedule log");
				scheduleLog = createScheduleLog(pm);
			}
		} else {
			log.debug("got schedule log");
			scheduleLog = result.get(0);
		}
		log.debug("EXIT (result = " + scheduleLog + ")");
		return scheduleLog;
	}
	
	private ScheduleLog createScheduleLog(PersistenceManager pm) {
		log.debug("ENTER"); 
		try {
			Set<Theater> theaters = parser.getTheaters();
			if (theaters != null) {
				ScheduleLog sLog = null;
				for(Theater t : theaters) {
					log.debug("adding theater " + t + " to log"); 
					sLog = new ScheduleLog(t);
					pm.makePersistent(sLog);
				}
				log.debug("EXIT");
				return getNextScheduled(pm, false);
			}
		} catch (Exception e) {
			log.debug("EXIT (EXCEPTION) " + e);
			return null;
		}
		log.debug("EXIT (NULL)");
		return null;
	}
	
	private void loadDataForTheater(Theater theater) {
		log.debug("ENTER"); 
		try {
			if (theater != null) {
				log2.debug(theater.toString());
				List<String> days = parser.getTheaterShowDays(theater);
				if (days != null) {
					for(String d : days) {
						log2.debug("\t" + d);
						Set<Hall> halls = parser.getHalls(theater);
						if (halls != null)
						for(Hall h : halls) {
							log2.debug("\t\t" + h);
							List<Show> shows = new ArrayList<Show>(parser.getDayShows(d, h));
							if (shows != null)
								for(Show show : shows)
									log2.debug("\t\t\t" + show);
						}
					}
				}
			}
		} catch(Exception ex) {			
			log.debug("EXCEPTION");
		}		
		log.debug("EXIT");
	}
	
	/*
	private void loadDataFromInternet() {
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
	}/**/
}
