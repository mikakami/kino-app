package ws.softlabs.kino.client.intf;

import java.util.List;

import ws.softlabs.lib.kino.model.client.Hall;
import ws.softlabs.lib.kino.model.client.Show;
import ws.softlabs.lib.kino.model.client.Theater;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("kino")
public interface KinoDataServiceAsync {

	//void loadDataFromInternet(AsyncCallback<Void> callback);	

	void listTheaters(AsyncCallback<List <Theater>> callback);
	void listDays(Theater theater, AsyncCallback<List <String>> callback);
	void listShows(Theater t, String date, AsyncCallback<List <Object>> callback);
	void stubListHall(AsyncCallback<List <Hall>> callback);
	void stubListShow(AsyncCallback<List <Show>> callback);

	/* tools */
	void loadDataFromDB(String param, AsyncCallback<List <String>> callback);
	void clearDatabase(AsyncCallback<Void> callback);
	void clearDatabase(String type, AsyncCallback<Void> callback);

	void loadTheaters(AsyncCallback<Void> callback);
	void loadHalls(Theater theater, AsyncCallback<Void> callback);
	void loadDays(Theater theater, AsyncCallback<Void> callback);
	void loadShows(Theater theater, String date, AsyncCallback<Void> callback);

}
