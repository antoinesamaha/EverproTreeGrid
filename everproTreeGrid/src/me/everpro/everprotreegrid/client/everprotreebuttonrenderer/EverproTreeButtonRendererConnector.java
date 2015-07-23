package me.everpro.everprotreegrid.client.everprotreebuttonrenderer;

import com.vaadin.client.connectors.ButtonRendererConnector;
import com.vaadin.shared.ui.Connect;

@Connect(me.everpro.everprotreegrid.EverproTreeButtonRenderer.class)
public class EverproTreeButtonRendererConnector extends ButtonRendererConnector {

  @Override
  public EverproTreeButtonRenderer getRenderer() {
      return (EverproTreeButtonRenderer) super.getRenderer();
  }

}

