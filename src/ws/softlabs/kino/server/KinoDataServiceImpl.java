package ws.softlabs.kino.server;

/**
 *    http://objectuser.wordpress.com/2009/07/04/17-minute-jdo/
 */
		
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import ws.softlabs.kino.client.intf.KinoDataService;
import ws.softlabs.lib.kino.dao.server.intf.DataService;
import ws.softlabs.lib.kino.dao.server.service.pmf.PMFModelDataService;
import ws.softlabs.lib.kino.model.client.Hall;
import ws.softlabs.lib.kino.model.client.Show;
import ws.softlabs.lib.kino.model.client.Theater;
import ws.softlabs.lib.parser.server.KinovlruParser;
import ws.softlabs.lib.util.client.DateUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class KinoDataServiceImpl 
				extends RemoteServiceServlet 
				implements KinoDataService {

	private static final Logger log = 
		Logger.getLogger("kino.gwt.service." + KinoDataServiceImpl.class.getSimpleName());
	
	private DataService 	dataService = new PMFModelDataService();
	private KinovlruParser 	parser      = new KinovlruParser(dataService);

	public List<Theater> listTheaters() {
		List<Theater> daoResults   = dataService.getTheaterList();
		Set<Theater> parserResults = null;
		if (daoResults != null && daoResults.size() > 0) {
			log.debug("GOT THEATERS FROM -+= DATASTORE =+-");
			return daoResults;
		}
		else 
			try {
				parserResults = parser.getTheaters();
				log.debug("GOT THEATERS FROM -+= PARSER =+-");
			}
			catch(Exception ex) {
				parserResults = null;
			}
		return new ArrayList<Theater>(parserResults);/**/
	}
	public List<String>  listDays(Theater theater) {
		List<String> daoResults = dataService.getShowDaysList(theater);
		List<String> result 	= null;
		if (daoResults != null && daoResults.size() > 0) {
			result = sortDays(daoResults);
			log.debug("GOT DAYS FROM -+= DATASTORE =+-");
		}
		else {
			try {
				List<String> set = parser.getTheaterShowDays(theater);
				if (set != null) {
					result = sortDays(set);
					log.debug("GOT DAYS FROM -+= PARSER =+-");
				}
			}
			catch(Exception ex) {
				result = null;
			}
		}
		return result;
	}
	public List<Object>  listShows(Theater theater, String date) {
		log.debug("ENTER (theater = " + theater + ")");
		List<Hall> halls = dataService.getTheaterHallList(theater);
		List<Object> result = null;
		if (halls == null || halls.isEmpty()) {
			log.debug("CAN'T GET HALLS FROM DATASTORE");
			try {
				halls = new ArrayList<Hall>(parser.getHalls(theater));
				log.debug("GOT HALLS FROM -+= PARSER =+-");
			} catch(Exception ex) {
				System.out.println(ex);
				halls = null;
			}
		}
		else
			log.debug("GOT HALLS FROM -+= DATASTORE =+-");

		if (halls != null) {
			result = new ArrayList<Object>();
			for(Hall hall : halls) {
				if (hall.getName() != null)
					result.add(hall);
				List<Show> shows = dataService.getShowList(hall, DateUtils.dateToMidnight(DateUtils.stringToDate(date)));
				if (shows == null || shows.size() < 1)
					try {
						shows = new ArrayList<Show>(parser.getDayShows(date, hall));
						log.debug("GOT SHOWS FROM -+= PARSER =+-");
					} catch(Exception ex) {
						shows = null;
					}
				else
					log.debug("GOT SHOWS FROM -+= DATASTORE =+-");
				if (shows != null) {
					for(Show show : shows) {
						result.add(show);
					}
				}
				
			}
		}
		return result;
	}	
	private List<String> sortDays(Collection<String> days) {
		if (days == null) return null;
		List<String> result = new ArrayList<String>(days);
		Comparator<String> comparator = new Comparator<String>(){
			public int compare(String s1, String s2) {
				Date d1 = DateUtils.stringToDate(s1);
				Date d2 = DateUtils.stringToDate(s2);
				if (d1 != null)
					return d1.compareTo(d2);
				else if (d2 != null)
					return d2.compareTo(d1);
				else 
					return 0;
			}};
		Collections.sort(result, comparator);
		return result;
	}
	public List<Hall> stubListHall() {
		return new ArrayList<Hall>();
	}
	public List<Show> stubListShow() {
		return new ArrayList<Show>();
	}
	public List<String> loadDataFromDB(String param) {
		// "theaters", "halls" , "days", "movies", "shows", "clear"
		if (param == null || param.isEmpty()) {
			return null;
		}
		else if ("theaters".equalsIgnoreCase(param)) {
			log.debug("asking for RAW theaters");
			return dataService.getRawTheaterList();
		} else if ("halls".equalsIgnoreCase(param)) {
			log.debug("asking for RAW halls");
			return dataService.getRawHallList();
		} else if ("days".equalsIgnoreCase(param)) { 
			log.debug("asking for RAW days");
			return dataService.getRawDayList();
		} else if ("movies".equalsIgnoreCase(param)) {
			log.debug("asking for RAW movies");
			return dataService.getRawMovieList();
		} else if ("shows".equalsIgnoreCase(param)) {
			log.debug("asking for RAW shows");
			return dataService.getRawShowList();
		} else {
			return null;
		}
	}
	public void clearDatabase() {
		dataService.clearDatabase();		
	}
	public void clearDatabase(String type) {
		// "theaters", "halls" , "days", "movies", "shows"
		log.debug("ENTER (type = " + type + ")");

		if ("theaters".equalsIgnoreCase(type)) {
			log.debug("clearing PTheaters");
			dataService.clearTheaters();
		} else if ("halls".equalsIgnoreCase(type)) {
			log.debug("clearing PHalls");
			dataService.clearHalls();
		} else if ("days".equalsIgnoreCase(type) || "shows".equalsIgnoreCase(type)) {
			log.debug("clearing PShows");
			dataService.clearShows();
		} else if ("movies".equalsIgnoreCase(type)) {
			log.debug("clearing PMovies");
			dataService.clearMovies();
		}
		log.debug("EXIT");
	}
	public void loadTheaters() {
		try {
			parser.getTheaters();
			log.debug("GOT THEATERS FROM PARSER");
		}
		catch(Exception ex) {
		}		
	}
	public void loadHalls(Theater theater) {
		try {
			parser.getHalls(theater);
			log.debug("GOT HALLS FROM PARSER");
		}
		catch(Exception ex) {
		}		
	}
	public void loadDays(Theater theater) {
		try {
			parser.getTheaterShowDays(theater);
			log.debug("GOT DAYS FROM PARSER");
		}
		catch(Exception ex) {
		}		
	}
	public void loadShows(Theater theater, String date) {
		try {
			Set<Hall> halls = parser.getHalls(theater);
			if (halls != null)
				for(Hall h : halls)
					parser.getDayShows(date, h);
		} catch (Exception e) {
		}
	}
} 
