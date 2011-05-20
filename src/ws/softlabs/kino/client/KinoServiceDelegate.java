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

	  void loadDataFromInternet() { /*
		  kinoService.loadDataFromInternet(
					new AsyncCallback<Void> () {
						public void onFailure(Throwable caught) {
							gui.service_eventLoadDataFailed(caught);
						}
			
						public void onSuccess(Void result) {
							gui.service_eventLoadDataSuccessful();
						}
					}//end of inner class 
		  );//end of method call.	/**/
	  }

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
	  
	  /*
	  void listHalls(final iTheater theater) {
		  kinoService.listHalls(
				  theater,
				  new AsyncCallback<List<iHall>>() {
					public void onFailure(Throwable caught) {
						gui.service_eventListHallsFailed(caught);
					}
					public void onSuccess(List<iHall> result) {
						gui.service_eventListHallsSuccess(result);
					}
				  }//end of inner class
		  );//end of method call.              
	  }/**/	  
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
/**/
	  
}
