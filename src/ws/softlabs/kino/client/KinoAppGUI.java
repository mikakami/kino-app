package ws.softlabs.kino.client;

/*
 *  http://code.google.com/p/tatami/ - DoJo on GWT
 *  
 */

import java.util.Date;
import java.util.List;

import ws.softlabs.lib.kino.model.client.*;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("unused")
public class KinoAppGUI {

	/* ==== PROTECTED FIELDS ==== */ 
    /* Data model */
    protected KinoServiceDelegate kinoService; 
    private   boolean			  processing = false;
    
    /* data holders */
    private List<Theater> 		theaters;
    private List<String>		days;
    private List<Object>		shows;
    
    private Theater 			currentTheater;
    private String				currentDay;
    private Show				currentShow;
    
    /* === GUI elements === */
    /*  grids */
    protected Grid 				theatersGrid;
    protected Grid				daysGrid;    
    protected Grid				showGrid;

    /* panels */
    //protected HorizontalPanel 	headerPanel;
    protected HorizontalPanel 	statusPanel;
    protected VerticalPanel		theatersPanel;
    protected VerticalPanel   	toolsPanel;
    protected HorizontalPanel 	footerPanel;
    protected HorizontalPanel	contentsPanel;
    protected VerticalPanel		showsPanel;
    
    /* widgets */
    protected Hyperlink			loadDataButton;
    protected Label				status;    
    protected Label				title;    
    
    /* dialogs */
    protected Image				loadingImage;
    
    /* === PUBLIC METHODS === */
	public void init() {
	    //loadingDialog = new LoadingDialogBox();
	    createHeader();
	    createStatus();
	    createTheaters();
	    createToolbar();
	    createFooter();
	    createMainContent(); 
	    
		placeWidgets();	  		
		
		createLogger();
	}		

	/* ==== GUI EVENTS ==== */
	public void gui_eventLoadDataButtonClicked() {
		if ( processing ) return;
		status.setText("");
		//status.setText("loading data...please wait...");
		//loadingDialog.center();
		kinoService.loadDataFromInternet();
		processing = true;
	}	
	public void gui_eventTheaterGridClicked(Cell cellClicked) {
		if ( processing ) return;		
		if ((this.theaters.get(cellClicked.getRowIndex())).equals(currentTheater))
			return;
		status.setText("");
		currentTheater = this.theaters.get(cellClicked.getRowIndex());
		title.setText(currentTheater.getName());
		hide(contentsPanel);
		hide(showsPanel);
		showDIVs();
		showGrid.clear();
		show(loadingImage);
		show(statusPanel);
		kinoService.listDays(currentTheater);
		processing = true;
	}	
	public void gui_eventDaysGridClicked(Cell cellClicked) {
		if ( processing ) return;		
		status.setText("");
		selectDay(cellClicked.getRowIndex());
		currentDay = this.days.get(cellClicked.getRowIndex());
		hide(showsPanel);
		show(loadingImage);
		kinoService.listShows(currentTheater, currentDay);
		processing = true;
	}

	/* ==== SERVICE CALLBACKS ==== */
	/* theater */
	public void service_eventListTheatersFailed(Throwable caught) {
		processing = false;
		hide(loadingImage);
		status.setText("cant load theaters list");
	}
	public void service_eventListTheatersSuccess(List<Theater> result) {
		status.setText("");
		processing = false;		
		hide(loadingImage);
		theaters = result;

		if (theaters != null) {
			int row = 0;
			theatersGrid.resizeRows(theaters.size());
			for(Theater t : theaters) {
				theatersGrid.setWidget(row, 0, new Hyperlink(t.getName(), ""));
				row++;
			}
		}
		else {
			status.setText( "no data in response to 'listTheaters'");
		}
		hide(loadingImage);
	}
	/* days */
	public void service_eventListDaysFailed(Throwable caught) {
		status.setText("");
		processing = false;		
		hide(loadingImage);
	}
	public void service_eventListDaysSuccess(List<String> result) {
		status.setText("");
		processing = false;		
		days = result;
		if (days != null) {
			int row = 0;
			daysGrid.resizeRows(days.size());
			for(String d : days) {
				daysGrid.setWidget(row, 0, new Hyperlink(d, ""));
				row++;
			}
			currentDay = this.days.get(0);
			selectDay(0);
		}
		else {
			status.setText( "no data in response to 'listDays'");
			currentDay = null;
		}	
		hide(showsPanel);
		show(loadingImage);
		kinoService.listShows(currentTheater, currentDay);
		show(contentsPanel);
	}
	/* shows */
	public void service_eventListShowsFailed(Throwable caught) {
		processing = false;		
		hide(loadingImage);
		status.setText("failure loading shows for " + currentTheater.getName());
	}
	public void service_eventListShowsSuccess(List<Object> result) {
		status.setText("");
		processing = false;		
		shows = result;
		if(shows != null) {
			int row = 0;
			showGrid.resize(shows.size(), 7);
			for(Object o : shows) {
				if (o instanceof Hall) {
					Label label = new Label(((Hall)o).getName());
					label.setStylePrimaryName("shows00");
					label.setWidth("100%");
					label.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
					showGrid.setWidget(row, 0, new Label(""));;
					showGrid.setWidget(row, 1, label);;
					showGrid.setWidget(row, 2, new Label(""));;
					showGrid.setWidget(row, 3, new Label(""));;
					showGrid.setWidget(row, 4, new Label(""));;
					showGrid.setWidget(row, 5, new Label(""));;
					showGrid.setWidget(row, 6, new Label(""));;
					row++;
				} else if (o instanceof Show) {
					Label     label = null;
					Hyperlink link  = null;
					Show show = (Show)o;
					label = new Label(show.getTimeString());
					label.setStylePrimaryName("shows01");
					showGrid.setWidget(row, 0, label);
					/*link = new Hyperlink(show.getMovie().getName(), show.getMovie().getUrl());
					link.setStylePrimaryName("shows02");
					showGrid.setWidget(row, 1, link);*/
					label = new Label(show.getMovie().getName());
					label.setStylePrimaryName("shows021");
					showGrid.setWidget(row, 1, label);
					
					int col = 2;
					for(Integer i : show.getPrice()) {
						if (i != null) {
							label = new Label((new Integer(i)).toString());
							label.setStylePrimaryName("shows03");
							showGrid.setWidget(row, col, label);
						}
						col++;
					}							
					row++;
				}
 			}
			showsPanel.setVisible(true);
		}
		else 
			status.setText( "no data in response to 'listShows'");
		hide(loadingImage);
	}
	/* dataLoad */ /*
	public void service_eventLoadDataFailed(Throwable caught) {
		processing = false;
		status.setText("data load failed...");
		//loadingDialog.hide();
		kinoService.listTheaters();
	}
	public void service_eventLoadDataSuccessful() {
		processing = false;
		//status.setText("data successfully loaded...");
		status.setText("");
		//loadingDialog.hide();
		kinoService.listTheaters();
	}/**/
	
	/* ==== PRIVATE METHODS ==== */
    private void placeWidgets() {
        //RootPanel.get(KINO_APP_HEADER_ROOT_PANEL).add(headerPanel);
        RootPanel.get(KINO_APP_STATUS_ROOT_PANEL).add(statusPanel);
        RootPanel.get(KINO_APP_THEATERS_ROOT_PANEL).add(theatersPanel);
        RootPanel.get(KINO_APP_SHOWS_ROOT_PANEL).add(contentsPanel);
        RootPanel.get(KINO_APP_TOOLS_ROOT_PANEL).add(toolsPanel);
        RootPanel.get(KINO_APP_FOOTER_ROOT_PANEL).add(footerPanel);
	} 	
    private void createHeader() {
//		headerPanel	= new HorizontalPanel();
//		headerPanel.setVisible(true);
//		loadingImage = new Image("images/rotation.gif");
//		headerPanel.add(loadingImage);
//		loadingPanel = new HorizontalPanel();
//		loadingImage = new Image("images/rotation.gif");
//		loadingPanel.add(loadingImage);
//		loadingPanel.setVisible(true);
    }
    private void createStatus() {
	    status		 = new Label("");
	    title		 = new Label("");
	    loadingImage = new Image("images/rotation.gif");
	    statusPanel	 = new HorizontalPanel();
	    
	    statusPanel.add(loadingImage);
	    statusPanel.add(status);
	    statusPanel.add(title);
	    
	    title.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
	    status.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
 	    statusPanel.setWidth("100%");

	    loadingImage.setSize("15", "15");
	    title.setStylePrimaryName("title");	    
	    status.setStyleName("status");	    
	    statusPanel.addStyleName("panel");	    
	    
	    hide(statusPanel);
}
    private void createTheaters() {
	    theatersPanel = new VerticalPanel();
	    theatersGrid  = new Grid(1,1);
	    theatersPanel.add(theatersGrid);    	
	    theatersPanel.setVisible(true);
    }
    private void createToolbar() {
	    toolsPanel	   = new VerticalPanel();
	    //loadDataButton = new Hyperlink("Load Data", "");
	    //toolsPanel.add(new HTML("<span id=font03></span><br>"));
	    //toolsPanel.add(loadDataButton);
	    //toolsPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
	    //show(toolsPanel);
    }
    private void createFooter() {
	    footerPanel	= new HorizontalPanel();
	    hide(footerPanel);
    }
    private void createMainContent() {
    	contentsPanel = new HorizontalPanel();
    	showsPanel	  = new VerticalPanel();
    	daysGrid	  = new Grid(1, 1);  
    	showGrid	  = new Grid(1, 6);
    	
    	daysGrid.setStyleName("daysGrid");
    	showGrid.setStyleName("showsGrid");
    	
     	daysGrid.setWidth("250px");
    	contentsPanel.setWidth("100%");
    	showsPanel.setWidth("100%");

    	contentsPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
    	showsPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);

    	contentsPanel.add(showsPanel);
    	contentsPanel.add(daysGrid);    	
    	showsPanel.add(showGrid);
    }    
    private void hide(Widget widget) {
    	widget.setVisible(false);
    }
    private void show(Widget widget) {
    	widget.setVisible(true);
    }
    private void selectDay(int idx) {
		for(int i=0; i < days.size(); i++)
			daysGrid.getWidget(i, 0).removeStyleName("selected");
		daysGrid.getWidget(idx, 0).addStyleName("selected");    	
    }
    private void showDIVs() {
    	Element e = null;
    	e = DOM.getElementById("container_shows");
    	if (e != null)
    		DOM.setStyleAttribute(e, "display", "block");
    	e = DOM.getElementById("toolbar");
    	if (e != null)
    		DOM.setStyleAttribute(e, "display", "block");		
//    	e = DOM.getElementById("footer");
//    	if (e != null)
//    		DOM.setStyleAttribute(e, "display", "block");
    }    
    private void createLogger() { /*
    	final HTML html = new HTML();
    	footerPanel.add(html);
    	logger = Logger.getLogger(""); 
    	// add the html widget somewhere in your code.
    	logger.addHandler(new Handler() {
    	  {
    	    // set the formatter, in this case HtmlLogFormatter
    	    setFormatter(new HtmlLogFormatter(true));
    	    setLevel(Level.ALL);
    	  }
    	  @Override
    	  public void publish(LogRecord record) {
    	    if (!isLoggable(record)) {
    	      Formatter formatter = getFormatter();
    	      String msg = formatter.format(record);

    	      html.setHTML(msg);
    	    }
    	  }
    	  public void flush() {
			// TODO Auto-generated method stub
    	  }
    	  public void close() throws SecurityException {
			// TODO Auto-generated method stub
    	  }
    	});   
    	logger.setLevel(Level.INFO); /**/
    }
    
    /* ==== PRIVATE STATIC FIELDS ==== */    
	private static final String KINO_APP_HEADER_ROOT_PANEL   = "header";
	private static final String KINO_APP_STATUS_ROOT_PANEL   = "status";
    private static final String KINO_APP_THEATERS_ROOT_PANEL = "theaters_list";
	private static final String KINO_APP_SHOWS_ROOT_PANEL    = "shows_list";
    private static final String KINO_APP_TOOLS_ROOT_PANEL    = "toolbar";
	private static final String KINO_APP_FOOTER_ROOT_PANEL   = "footer";
	private static final String LOADING_PANEL   			 = "loading";
}
