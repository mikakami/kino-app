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

import ws.softlabs.kino.client.intf.KinoDataService;
import ws.softlabs.lib.kino.dao.server.intf.DataService;
import ws.softlabs.lib.kino.dao.server.service.pmf.PMFModelDataService;
import ws.softlabs.lib.kino.dao.server.util.DAOResultUtils;
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

	private DataService 	dataService = new PMFModelDataService();
	private KinovlruParser 	parser      = new KinovlruParser(dataService);

	public void loadDataFromInternet() {
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
		}
	}
	public List<Theater> listTheaters() {
		
		//dataService.clearDatabase();
		//return null;/*
		
		List<Theater> daoResults   = dataService.getTheaterList();
		Set<Theater> parserResults = null;
		if (daoResults != null && daoResults.size() > 0) {
System.err.println("GOT THEATERS FROM -+= DATASTORE =+-");
			return daoResults;
		}
		else 
			try {
				parserResults = parser.getTheaters();
System.err.println("GOT THEATERS FROM -+= PARSER =+-");
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
System.err.println("GOT DAYS FROM -+= DATASTORE =+-");
		}
		else {
			try {
				List<String> set = parser.getTheaterShowDays(theater);
				if (set != null) {
					result = sortDays(set);
System.err.println("GOT DAYS FROM -+= PARSER =+-");
				}
			}
			catch(Exception ex) {
				result = null;
			}
		}
		return result;
	}
	public List<Object>  listShows(Theater theater, String date) {
//System.err.println("Entered 'listShows' at KinoDataServiceImpl. Theater = " + theater);
		List<Hall> halls = dataService.getTheaterHallList(theater);
DAOResultUtils.printHallList("KinoDataServiceImpl.listShows", halls);
		List<Object> result = null;
		if (halls == null || halls.isEmpty()) {
System.err.println("CAN'T GET HALLS FROM DATASTORE");
			try {
				halls = new ArrayList<Hall>(parser.getHalls(theater));
System.err.println("GOT HALLS FROM -+= PARSER =+-");
			} catch(Exception ex) {
				System.out.println(ex);
				halls = null;
			}
		}
		else
System.err.println("GOT HALLS FROM -+= DATASTORE =+-");

		if (halls != null) {
			result = new ArrayList<Object>();
			for(Hall hall : halls) {
				if (hall.getName() != null)
					result.add(hall);
				List<Show> shows = dataService.getShowList(hall, DateUtils.stringToDate(date));
				if (shows == null || shows.size() < 1)
					try {
						shows = new ArrayList<Show>(parser.getDayShows(date, hall));
System.err.println("GOT SHOWS FROM -+= PARSER =+-");
					} catch(Exception ex) {
						shows = null;
					}
				else
System.err.println("GOT SHOWS FROM -+= DATASTORE =+-");
				if (shows != null) {
					for(Show show : shows) {
						result.add(show);
					}
				}
				
			}
		}
		return result;
	}	
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

} 
