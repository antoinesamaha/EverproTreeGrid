package me.everpro.everprotreegrid;

import com.vaadin.v7.ui.renderers.ButtonRenderer;

public class EverproTreeButtonRenderer extends ButtonRenderer {

	public EverproTreeButtonRenderer() {
		super();
	}

	public EverproTreeButtonRenderer(com.vaadin.v7.ui.renderers.ClickableRenderer.RendererClickListener listener, String nullRepresentation) {
		super(listener, nullRepresentation);
	}

	public EverproTreeButtonRenderer(com.vaadin.v7.ui.renderers.ClickableRenderer.RendererClickListener listener) {
		super(listener);
	}

	public EverproTreeButtonRenderer(String nullRepresentation) {
		super(nullRepresentation);
	}
	
  public static String encode(String text, String style, int indentationInPixels){
  	return text+"|"+style+"|"+indentationInPixels;
  }

}
