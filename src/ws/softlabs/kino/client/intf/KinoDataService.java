package ws.softlabs.kino.client.intf;

import java.util.List;

import ws.softlabs.lib.kino.model.client.Hall;
import ws.softlabs.lib.kino.model.client.Show;
import ws.softlabs.lib.kino.model.client.Theater;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("kino")
public interface KinoDataService extends RemoteService {

	void loadDataFromInternet();

	List<Theater> listTheaters();
	List<String>  listDays(Theater theater);
	List<Object>  listShows(Theater t, String day);
/*	List<Hall> 	  listHalls(Theater t); */
	List<Hall>	  stubListHall();
	List<Show>	  stubListShow();
}
