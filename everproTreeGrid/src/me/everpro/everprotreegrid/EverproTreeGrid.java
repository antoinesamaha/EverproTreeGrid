package me.everpro.everprotreegrid;

import java.util.Locale;

import me.everpro.everprotreegrid.container.EverproTreeGridHierarchicalIndexedContainer;

import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;

public class EverproTreeGrid extends Grid {

	private String propertyIdForTreeColumn = null;
	
	public EverproTreeGrid(String propertyIdForItemIdValue, String propertyIdForTreeColumn){
		this.propertyIdForTreeColumn = propertyIdForTreeColumn;
		Column column = addColumn(propertyIdForItemIdValue);
		setTreeRendererColumn(column);
	}
	
	protected Object convertValueToItemId(String value){
		return value;
	}
	
	protected String getPropertyIdForTreeColumn(){
		return propertyIdForTreeColumn;
	}

	public Hierarchical getHierarchicalContainerDataSource() {
		EverproTreeGridHierarchicalIndexedContainer indexed = (EverproTreeGridHierarchicalIndexedContainer) getContainerDataSource();
		return indexed != null ? indexed.getHierachical() : null;
	}
	
	public void setContainerDataSource(Hierarchical hierarchicalContainer) {
		EverproTreeGridHierarchicalIndexedContainer indexed = new EverproTreeGridHierarchicalIndexedContainer(hierarchicalContainer);
		indexed.rebuildIndexedArrayList();
		setContainerDataSource(indexed);
	}
	
	private void setTreeRendererColumn(Column col){
		RendererClickListener listener = new ClickableRenderer.RendererClickListener() {
			public void click(RendererClickEvent event) {
				EverproTreeGridHierarchicalIndexedContainer indexed = (EverproTreeGridHierarchicalIndexedContainer) getContainerDataSource();
				indexed.toggleCollapseState(event.getItemId());
				indexed.rebuildIndexedArrayList();
				/*
				Bkdn bkdn = (Bkdn) GridTest_BkdnList.this.bkdnList.searchByReference((Integer) event.getItemId());
				Globals.showNotification("Just clicked a button : "+bkdn.getCode(), "YEsss!", IFocEnvironment.TYPE_HUMANIZED_MESSAGE);
				*/
			}
		};
		
		EverproTreeButtonRenderer buttonRenderer = new EverproTreeButtonRenderer(listener);
		col.setRenderer(buttonRenderer, new Converter<String, String>(){

			public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
				return "Not Implemented";
			}

			public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
//				Object itemId = Integer.valueOf(value);
				Hierarchical hierarchical = getHierarchicalContainerDataSource();
				
				Object itemId = convertValueToItemId(value);
				
				//Level
				int level = 0; 
				Object parentId = hierarchical.getParent(itemId);
				while(parentId != null){
					level++;
					parentId = hierarchical.getParent(parentId);
				}

				//Display Value
				EverproTreeGridHierarchicalIndexedContainer indexed = (EverproTreeGridHierarchicalIndexedContainer) getContainerDataSource();
				Property displayProperty = indexed.getContainerProperty(itemId, getPropertyIdForTreeColumn());
				String displayValue = (String) displayProperty.getValue();

				//Style Name
				String style = "v-foc-gridtree-node-leaf";
				if(hierarchical.hasChildren(itemId)){
					if(indexed.isCollapsedId(itemId)){
						style = "v-foc-gridtree-node-collapsed";
					}else{
						style = "v-foc-gridtree-node-expanded";
					}
				}
				
				return EverproTreeButtonRenderer.encode(displayValue, style, level*20);
			}

			public Class<String> getModelType() {
				return String.class;
			}

			public Class<String> getPresentationType() {
				return String.class;
			}
		});

	}
}
