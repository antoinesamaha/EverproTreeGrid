package me.everpro.everprotreegrid;

import java.util.Locale;

import me.everpro.everprotreegrid.container.EverproTreeGridHierarchicalIndexedContainer;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Container.Hierarchical;
import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.renderers.ClickableRenderer;
import com.vaadin.v7.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.v7.ui.renderers.ClickableRenderer.RendererClickListener;

public class EverproTreeGrid extends Grid {

	private Object itemIdProperty  = null;
	private Object displayProperty = null;
	private IEverproTreeGridNodeStyleGenerator nodeStyleGenerator = null;
	
	public EverproTreeGrid(Object itemIdProperty, Object displayProperty){
		this.displayProperty  = displayProperty;
		this.itemIdProperty = itemIdProperty;
		Column column = addColumn(itemIdProperty);
		setTreeRendererColumn(column);
		column.setEditable(false);
		
		setCellStyleGenerator(new CellStyleGenerator() {
			@Override
			public String getStyle(CellReference cellReference) {
				return getCellStyle(cellReference);
			}
		});
	}
	
	public IEverproTreeGridNodeStyleGenerator getNodeStyleGenerator() {
		return nodeStyleGenerator;
	}

	public void setNodeStyleGenerator(IEverproTreeGridNodeStyleGenerator nodeStyleGenerator) {
		this.nodeStyleGenerator = nodeStyleGenerator;
	}

	/**
	 * @param cellReference
	 * @return the style name required for this cell. In your css the style name should be prefixed with:
	 * v-grid-cell. example: if you return "gray" your css style should be .v-grid-row.gray
	 *  
	 */
	protected String getCellStyle(CellReference cellReference){
		String style = null;
		if(cellReference.getValue() instanceof Integer || cellReference.getValue() instanceof Double){
			style = "numeric";
		}
		
		return style;
	}
	
	protected Object convertValueToItemId(String value){
		return value;
	}
	
	protected Object getDisplayProperty(){
		return displayProperty;
	}

	protected Object getItemIdProperty(){
		return itemIdProperty;
	}

	@Override
	public Indexed getContainerDataSource() {
		Container container = super.getContainerDataSource();
		if(container instanceof EverproTreeGridHierarchicalIndexedContainer){
			
		}else if(container instanceof Hierarchical){
			setContainerDataSource((Hierarchical) container);
			container = getContainerDataSource();
		}

		return (Indexed) container;
	}
	
	public Indexed getContainerDataSource_WhenIndexed() {
		Container container = getContainerDataSource();
		
		if(			!(container instanceof EverproTreeGridHierarchicalIndexedContainer)
				&&  !(container instanceof Hierarchical)){
			container = null;
		}

		return (Indexed) container;
	}

	public Hierarchical getHierarchicalContainerDataSource() {
		EverproTreeGridHierarchicalIndexedContainer indexed = (EverproTreeGridHierarchicalIndexedContainer) getContainerDataSource();
		return indexed != null ? indexed.getHierachical() : null;
	}
	
	public void setContainerDataSource(Hierarchical hierarchicalContainer) {
		EverproTreeGridHierarchicalIndexedContainer indexed = new EverproTreeGridHierarchicalIndexedContainer(hierarchicalContainer);
		indexed.rebuildIndexedArrayList();
		setContainerDataSource(indexed);
		adjustTreeNodeEditableField();
	}

	/**
	 *  
	 * @param itemId
	 * @return the display String to be displayed as node title
	 */
	protected String getNodeTitleForItemId(Object itemId){
		Container indexed = getContainerDataSource();
		Property displayProperty = indexed != null ? indexed.getContainerProperty(itemId, getDisplayProperty()) : null;
		String displayValue = displayProperty != null ? (String) displayProperty.getValue() : "";
		return displayValue;
	}

	protected void setNodeTitleForItemId(Object itemId, String value){
		Container indexed = getContainerDataSource();
		Property displayProperty = indexed != null ? indexed.getContainerProperty(itemId, getDisplayProperty()) : null;
		if(displayProperty != null){
			displayProperty.setValue(value);
		}
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

			public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
				return "Not Implemented";
			}

			public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
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
				String displayValue = getNodeTitleForItemId(itemId);

				//Style Name
				String style = getNodeStyleName(itemId);
				
				return EverproTreeButtonRenderer.encode(displayValue, style, level*20);
			}

			public Class<String> getModelType() {
				return String.class;
			}

			public Class<String> getPresentationType() {
				return String.class;
			}
		});
		
		adjustTreeNodeEditableField();
	}

	/**
	 * You can override this method if you need to have your own styles for the node renderer
	 * 
	 * @param itemId
	 * @return
	 */
	public String getDefaultNodeStyleName(Object itemId){
		String style = "v-foc-gridtree-node-leaf";
		Hierarchical hierarchical = getHierarchicalContainerDataSource();
		if(hierarchical != null && hierarchical.hasChildren(itemId)){
			EverproTreeGridHierarchicalIndexedContainer indexed = (EverproTreeGridHierarchicalIndexedContainer) getContainerDataSource();
			if(indexed != null){
				if(indexed.isCollapsedId(itemId)){
					style = "v-foc-gridtree-node-collapsed";
				}else{
					style = "v-foc-gridtree-node-expanded";
				}
			}
		}
		
		return style;
	}

	/**
	 * You can override this method if you need to have your own styles for the node renderer
	 * 
	 * @param itemId
	 * @return
	 */
	public String getNodeStyleName(Object itemId){
		String style = getDefaultNodeStyleName(itemId);
		if(getNodeStyleGenerator() != null){
			style = getNodeStyleGenerator().getStyleName(itemId, style);
		}
		return style;
	}
	
	public void adjustTreeNodeEditableField(){
		Column refColumn = getColumn(itemIdProperty);
		if(refColumn != null){
			NodeTitleTextField texField = new NodeTitleTextField(); 
			texField.setConverter(new Converter<String, String>(){
				
				@Override
				public String convertToModel(String value, Class targetType, Locale locale) throws ConversionException {
					RefValue refValue = decomposeString(value); 
//					Object itemId = convertValueToItemId(this.itemIdValue);
//					setNodeTitleForItemId(itemId, value);
					return refValue.getRef();
				}
	
				@Override
				public String convertToPresentation(String value, Class targetType, Locale locale) throws ConversionException {
					Object itemId = convertValueToItemId(value);
					String displayValue = getNodeTitleForItemId(itemId);
					return composeString(value, displayValue);
				}
	
				@Override
				public Class getModelType() {
					return String.class;
				}
	
				@Override
				public Class getPresentationType() {
					return String.class;
				}
			});
			refColumn.setEditorField(texField);
		}
	}
	
	public class NodeTitleTextField extends TextField {
		
		private String refPart = null;  
		
    @Override
    public void clear() {
    	refPart = null;
      setValue("");
    }

		@Override
		public String getInternalValue() {
			String value = super.getInternalValue();
//			if(refPart != null){
//				value = refPart+"|"+value;
//			}
			return value;
		}

		@Override
		protected void setInternalValue(String newFieldValue){
			RefValue refValue = decomposeString(newFieldValue);
			if(refValue != null && refValue.getRef() != null && !refValue.getRef().isEmpty()){
				refPart = refValue.getRef();
				newFieldValue = refValue.getValue();
			}
			super.setInternalValue(newFieldValue);
		}
		
    @Override
    public void commit() throws com.vaadin.v7.data.Buffered.SourceException, InvalidValueException {
			if(refPart != null && !refPart.isEmpty()){
				Object itemId = convertValueToItemId(refPart);
				if(itemId != null){
					setNodeTitleForItemId(itemId, getValue());
				}
			}
			super.commit();
    }
		
		public String getRefPart(){
			return refPart;
		}
	}
	
	public static class RefValue {
		private String ref = null;
		private String value = null;
		
		public RefValue(String ref, String value){
			this.ref = ref;
		  this.value = value;
		}

		public String getRef() {
			return ref;
		}

		public String getValue() {
			return value;
		}
	}

	public static String composeString(String ref, String value){
		String composed ; 
		if(ref != null){
			composed = ref+"|"+value;
		}else{
			composed = value;
		}
		return composed;
	}
	
	public static RefValue decomposeString(String composite){
		String refPart = null;
		int pipeIndex = composite.indexOf('|');
		if(pipeIndex > 0){
			refPart = composite.substring(0, pipeIndex);
			composite = composite.substring(pipeIndex+1);
		}
		
		RefValue refValue = new RefValue(refPart, composite);
		return refValue;
	}
}
