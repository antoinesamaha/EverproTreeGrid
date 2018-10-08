package me.everpro.event;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;

public class EverproItemSetChangeEvent implements ItemSetChangeEvent {

	public static final int MODIFICATION_EVENT = 0;
	public static final int ADD_EVENT          = 1;
	public static final int DELETE_EVENT       = 2;
	
	private Container container = null;
	private Object    itemId    = null;
	private int       eventID   = 0; 
	
	public EverproItemSetChangeEvent(Container container, int eventID, Object itemId){
		this.container = container;
		this.eventID   = eventID;
		this.itemId    = itemId;
	}
	
	public void dispose(){
		container = null;
		itemId = null;
	}
	
	@Override
	public Container getContainer() {
		return container;
	}

	/**
	 * @return the object id where we added a new item. In case of a tree this itemId would be the parent Node.
	 */
	public Object getItemId() {
		return itemId;
	}

	public int getEventID() {
		return eventID;
	}
}
