package me.everpro.everprotreegrid.client.everprotreebuttonrenderer;

import com.google.gwt.user.client.ui.Button;
import com.vaadin.client.renderers.ButtonRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;

public class EverproTreeButtonRenderer extends ButtonRenderer {

  @Override
  public void render(RendererCellReference cell, String text, Button button) {
  	if(text == null) text="";
  	if(button != null){
	  	int indexOfPipe = text.indexOf('|');
	  	String display = text;
	
	  	//Display
	  	if(indexOfPipe >= 0){
	  		display = text.substring(0, indexOfPipe);
	  	}
	  	button.setText(display);
	  	
	  	//Style
	  	if(indexOfPipe >= 0){
	  		int level = 0;
	  		String style = null;
	  		
		  	text = text.substring(indexOfPipe+1);
		  	if(text.length() > 0){
			  	style = text;
			  	indexOfPipe = text.indexOf('|');
			  	if(indexOfPipe >= 0){
			  		style = text.substring(0, indexOfPipe);
			  	}
			  	
			  	if(indexOfPipe >= 0){
				  	text = text.substring(indexOfPipe+1);
				  	if(text.length() > 0){
				  		try{
				  			level = Integer.valueOf(text);
				  		}catch(Exception e){
				  			e.printStackTrace();
				  		}
				  	}
			  	}
		  	}
		  	if(style != null) button.setStyleName(style);
		  	button.getElement().getStyle().setProperty("margin-left", level+"px");
	  	}
  	}
  }
}