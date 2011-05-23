package ws.softlabs.kino.client;

import java.util.List;

import ws.softlabs.kino.client.intf.KinoDataService;
import ws.softlabs.kino.client.intf.KinoDataServiceAsync;
import ws.softlabs.lib.kino.model.client.Theater;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class KinoServiceDelegate {
	  
	private KinoDataServiceAsync kinoService = 
		  GWT.create(KinoDataService.class);
	  
	  KinoAppGUI gui;

	  void listTheaters() {
		  kinoService.listTheaters(
				  new AsyncCallback<List<Theater>>() {
					public void onFailure(Throwable caught) {
						gui.service_eventListTheatersFailed(caught);
					}
					public void onSuccess(List<Theater> result) {
						gui.service_eventListTheatersSuccess(result);
					}
				  }//end of inner class
		  );//end of method call.              
	  }	  

	  void listDays(Theater theater) {
		  kinoService.listDays(
				  theater, 
				  new AsyncCallback<List<String>>() {
					public void onFailure(Throwable caught) {
						gui.service_eventListDaysFailed(caught);
					}
					public void onSuccess(List<String> result) {
						gui.service_eventListDaysSuccess(result);
					}
				  }//end of inner class
		  );//end of method call.              
	  }	  
	  void listShows(final Theater theater, 
			  		 final String day) {
		  kinoService.listShows(
				  theater,
				  day,
				  new AsyncCallback<List<Object>>() {
					public void onFailure(Throwable caught) {
						gui.service_eventListShowsFailed(caught);
					}
					public void onSuccess(List<Object> result) {
						gui.service_eventListShowsSuccess(result);
					}
				  }//end of inner class
		  );//end of method call.	            
	  }	 
	  
	  /***************************************/
	  /*************** TOOLS *****************/
	  /***************************************/
	  public void loadDataFromDB(String string) {
		  kinoService.loadDataFromDB(
			  		string, 
			  		new AsyncCallback<List<String>>() {
			  			public void onFailure(Throwable caught) {
			  				gui.service_eventListDBFailed(caught);
			  			}
					public void onSuccess(List<String> result) {
						gui.service_eventListDBSuccess(result);
					}
				  }//end of inner class
		  );//end of method call. 
	  }
	  public void clearDatabase() {
		  kinoService.clearDatabase(
			  new AsyncCallback<Void>() {
		  			public void onFailure(Throwable caught) {
		  				gui.service_eventClearDBFailed(caught);
		  			}
					public void onSuccess(Void result) {
		  				gui.service_eventClearDBSuccess();
					}});
	  }
	  public void clearDatabase(String type) {
		  kinoService.clearDatabase(type, 
				  new AsyncCallback<Void>() {
			  			public void onFailure(Throwable caught) {
			  				gui.service_eventClearDBFailed(caught);
			  			}
						public void onSuccess(Void result) {
			  				gui.service_eventClearDBSuccess();
						}});
	  }
	  public void loadFromParser(String type) {
		  if ("theaters".equalsIgnoreCase(type)) {
			  kinoService.loadTheaters(new AsyncCallback<Void>() {
			  			public void onFailure(Throwable caught) {
			  				gui.service_eventLoadDataFailed(caught);
			  			}
						public void onSuccess(Void result) {
			  				gui.service_eventLoadDataSuccess();
						}});
		  } else if ("halls".equalsIgnoreCase(type)) {
			  kinoService.loadHalls(gui.currentTheater, 
					  new AsyncCallback<Void>() {
				  			public void onFailure(Throwable caught) {
				  				gui.service_eventLoadDataFailed(caught);
				  			}
							public void onSuccess(Void result) {
				  				gui.service_eventLoadDataSuccess();
					}});			  
		  } else if ("movies".equalsIgnoreCase(type)) {
			  
		  } else if ("shows".equalsIgnoreCase(type)) {
			  kinoService.loadShows(gui.currentTheater, 
					  				gui.currentDay,
					  new AsyncCallback<Void>() {
				  			public void onFailure(Throwable caught) {
				  				gui.service_eventLoadDataFailed(caught);
				  			}
							public void onSuccess(Void result) {
				  				gui.service_eventLoadDataSuccess();
					}});			  
		  } else if ("days".equalsIgnoreCase(type)) {
			  kinoService.loadDays(gui.currentTheater, 
					  new AsyncCallback<Void>() {
				  			public void onFailure(Throwable caught) {
				  				gui.service_eventLoadDataFailed(caught);
				  			}
							public void onSuccess(Void result) {
				  				gui.service_eventLoadDataSuccess();
					}});			  
		  }
	  }
	  
}
