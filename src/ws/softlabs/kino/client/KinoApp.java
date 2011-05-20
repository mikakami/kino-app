package ws.softlabs.kino.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class KinoApp implements EntryPoint {

	private KinoAppGUI gui;
	private KinoServiceDelegate delegate;

	public void onModuleLoad() {
	      gui             = new KinoAppGUI();
	      gui.init();
	      delegate        = new KinoServiceDelegate();
	      gui.kinoService = delegate;
	      delegate.gui       = gui;
	      delegate.listTheaters();
	      wireGUIEvents();
	}
	
	private void wireGUIEvents() {
		gui.theatersGrid.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				Cell cellForEvent = gui.theatersGrid.getCellForEvent(event);
				gui.gui_eventTheaterGridClicked(cellForEvent);
		}});
		gui.daysGrid.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				Cell cellForEvent = gui.daysGrid.getCellForEvent(event);
				gui.gui_eventDaysGridClicked(cellForEvent);
		}}); /*
		gui.loadDataButton.addClickHandler(new ClickHandler(){
			  public void onClick(ClickEvent event) {
				  gui.gui_eventLoadDataButtonClicked();
		}}); /**/
	  }	  
}
