package ws.softlabs.kino.server;

import java.util.ArrayList;
import java.util.Collections;
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
import ws.softlabs.lib.util.client.DayComparator;

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
		log.debug("ENTER");
		List<Theater> daoResults = dataService.getTheaterList();
		if (daoResults != null && daoResults.size() > 0) {
			log.debug("GOT THEATERS FROM -+= DATASTORE =+-");
			return daoResults;
		}
		log.debug("EXIT");
		return null;
	}
	public List<String>  listDays(Theater theater) {
		log.debug("ENTER (theater = " + theater + ")");
		List<String> daoResults = dataService.getShowDaysList(theater);
		if (daoResults != null && daoResults.size() > 0) {
			Collections.sort(daoResults, new DayComparator());
			log.debug("GOT DAYS FROM -+= DATASTORE =+-");
		}
		log.debug("EXIT");
		return daoResults;
	}
	public List<Object>  listShows(Theater theater, String date) {
		log.debug("ENTER (theater = " + theater + ", day = " + date + ")");
		List<Hall> halls = dataService.getTheaterHallList(theater);
		List<Object> result = null;
		
		if (halls != null) {
			log.debug("GOT HALLS FROM -+= DATASTORE =+-");
		
			int i = 1;
			for(Hall hall : halls) {
				log.info("HALL #" + i + ": " + hall.getId() + " - " + hall.getName() + " - " + hall.getHtml());
				i++;
			}
			
			result = new ArrayList<Object>();
			for(Hall hall : halls) {
				log.debug("fetching shows for hall '" + hall.getName() + "'");
				if (hall.getName() != null) {
					log.debug("added hall to result");
					result.add(hall);
				}
				List<Show> shows = dataService.getShowList(hall, DateUtils.dateToMidnight(DateUtils.stringToDate(date)));
				if (shows != null && !shows.isEmpty()) {
					log.debug("GOT SHOWS FROM -+= DATASTORE =+-");
					for(Show show : shows) {
						log.debug(	"adding show '" + 
									show.getTimeString() + " " + 
									show.getMovie().getName() + "' to result");
						result.add(show);
					}
				}
			}
		}
		return result;
	}	
	public List<Hall> listHalls() {  
		// STUB
		// for HALL serialization ***** DON'T DELETE
		return null;
	}
	public List<Show> listShows() {
		// STUB
		// for SHOW serialization ***** DON'T DELETE
		return null;
	}

	
	/*******************************************************************/
	/***********  T O O L S  **  F O R  **  T E S T I N G  *************/
	/*******************************************************************/
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
