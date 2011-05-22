package ws.softlabs.kino.client.gui;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ListDialogBox extends DialogBox {

    private class crossHandler implements ClickHandler
    {  	@Override
    	public void onClick(ClickEvent event) {
    		hide();
    	}
    }

    crossHandler crosshandler = new crossHandler();
    
	public ListDialogBox(List<String> strings) {
		ScrollPanel   sPanel = new ScrollPanel();
		VerticalPanel vPanel = new VerticalPanel();

		vPanel.add(new HTML("<br>"));
	
		if (strings != null) {
			for (String s : strings) {
				vPanel.add(new Label(s));
			}
		}		
		
		Button close = new Button("close");
		close.addClickHandler(crosshandler);
		
		vPanel.add(new HTML("<br>"));
		vPanel.add(new HTML("<hr>"));
		vPanel.add(new HTML("<br>"));
		vPanel.add(close);
		
		sPanel.setVisible(true);
		sPanel.add(vPanel);
		//sPanel.setHeight("100%");
		this.add(sPanel);
		this.setText("RAW data from DB");
	}	
}
