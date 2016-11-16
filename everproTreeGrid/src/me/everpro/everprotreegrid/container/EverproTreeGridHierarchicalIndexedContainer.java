package me.everpro.everprotreegrid.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import me.everpro.event.EverproItemSetChangeEvent;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.ItemSetChangeEvent;

public class EverproTreeGridHierarchicalIndexedContainer implements Container.Indexed, Container.ItemSetChangeNotifier {

	private Hierarchical hierachical      = null;
	private ArrayList    expandedIds      = null;
	private ArrayList    indexedArrayList = null;
	
	public EverproTreeGridHierarchicalIndexedContainer(Hierarchical hierachical){
		this.hierachical = hierachical;
		indexedArrayList = new ArrayList();
		expandedIds = new ArrayList();
		if(hierachical instanceof Container.ItemSetChangeNotifier){
			((Container.ItemSetChangeNotifier)hierachical).addItemSetChangeListener(new ItemSetChangeListener() {
				@Override
				public void containerItemSetChange(ItemSetChangeEvent event) {
					if(event instanceof EverproItemSetChangeEvent){
						EverproItemSetChangeEvent everproEvent = (EverproItemSetChangeEvent) event;
						if(everproEvent.getEventID() == EverproItemSetChangeEvent.ADD_EVENT){
							Object itemId = everproEvent.getItemId();
							setCollapsedId(itemId, false);
						}
					}
					rebuildIndexedArrayList();
					EverproTreeGridHierarchicalIndexedContainer.this.notifyItemSetChange();
				}
			});
		}
	}
	
	public void dispose(){
		hierachical = null;
		if(expandedIds != null){
			expandedIds.clear();
			expandedIds = null;
		}
	}
	
	private void setCollapsedId(Object itemId, boolean collapsed){
		if(collapsed){
			expandedIds.remove(itemId);
		}else{
			expandedIds.add(itemId);
		}
	}
	
	public boolean isCollapsedId(Object itemId){
		return !expandedIds.contains(itemId);
	}
	
	public void toggleCollapseState(Object itemId){
		setCollapsedId(itemId, !isCollapsedId(itemId));
		rebuildIndexedArrayList();
	}
	
	private boolean passesAllFilters(Object itemId){
		boolean passes = true;
		if(hierachical != null && hierachical instanceof Container.Filterable){
			Item item = null;
			Container.Filterable filterable = (Container.Filterable) hierachical;
			Collection<Filter> filters = filterable.getContainerFilters();
			Iterator<Filter> iter = filters != null ? filters.iterator() : null;
			while(iter != null && iter.hasNext() && passes){
				Filter filter = iter.next();
				if(filter != null){
					if(item == null) item = hierachical.getItem(itemId);
					if(!filter.passesFilter(itemId, item)){
						passes = false;
					}
				}
			}
		}
		return passes; 
	}
	
	private void rebuildIndexedArrayList(Collection itemIds){
		Iterator iter = itemIds.iterator();
		while(iter != null && iter.hasNext()){
			Object itemId = iter.next();
			if(passesAllFilters(itemId)){
				indexedArrayList.add(itemId);
				if(!isCollapsedId(itemId)){
					Collection childrenItemIds = hierachical.getChildren(itemId);
					if(childrenItemIds != null){
						rebuildIndexedArrayList(childrenItemIds);
					}
				}
			}
		}
	}
	
	public void rebuildIndexedArrayList(){
		indexedArrayList.clear();

		Collection roots = hierachical.rootItemIds();
		rebuildIndexedArrayList(roots);
		notifyItemSetChange();
	}
	
	public Object nextItemId(Object itemId) {
		int index = indexedArrayList.indexOf(itemId);
		return (index+1 < indexedArrayList.size()) ? indexedArrayList.get(index+1) : null;
	}

	public Object prevItemId(Object itemId) {
		int index = indexedArrayList.indexOf(itemId);
		return (index-1 >= 0) ? indexedArrayList.get(index-1) : null;
	}

	public Object firstItemId() {
		return (indexedArrayList.size() > 0) ? indexedArrayList.get(0) : null;
	}

	public Object lastItemId() {
		return (indexedArrayList.size() > 0) ? indexedArrayList.get(indexedArrayList.size() - 1) : null;
	}

	public boolean isFirstId(Object itemId) {
		return firstItemId() == itemId;
	}

	public boolean isLastId(Object itemId) {
		return lastItemId() == itemId;
	}

	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public Item getItem(Object itemId) {
		return hierachical.getItem(itemId);
	}

	public Collection<?> getContainerPropertyIds() {
		return hierachical.getContainerPropertyIds();
	}

	public Collection<?> getItemIds() {
		return hierachical.getItemIds();
	}

	public Property getContainerProperty(Object itemId, Object propertyId) {
		return hierachical.getContainerProperty(itemId, propertyId);
	}

	public Class<?> getType(Object propertyId) {
		return hierachical.getType(propertyId);
	}

	public int size() {
		return indexedArrayList.size();
	}

	public boolean containsId(Object itemId) {
		return hierachical.containsId(itemId);
//		boolean contains = false;
//		for(int i=0; i<indexedArrayList.size() && !contains; i++){
//			contains = itemId == indexedArrayList.get(i);
//		}
//		return contains;
	}

	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public int indexOfId(Object itemId) {
		return indexedArrayList.indexOf(itemId);
	}

	public Object getIdByIndex(int index) {
		return (index >= 0 && index < indexedArrayList.size()) ? indexedArrayList.get(index) : null;
	}

	public List<?> getItemIds(int startIndex, int numberOfItems) {
		int toIndex = startIndex+numberOfItems;
		if(toIndex > indexedArrayList.size()) toIndex = indexedArrayList.size(); 
		return (toIndex >= startIndex && startIndex >= 0) ? indexedArrayList.subList(startIndex, toIndex) : null;
	}

	public Object addItemAt(int index) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public Hierarchical getHierachical() {
		return hierachical;
	}
	
	// -------------------------------
	// Container.ItemSetChangeNotifier 
	// -------------------------------
	private ArrayList<ItemSetChangeListener> itemSetChangeListenerArray = null;
	
	public void addItemSetChangeListener(ItemSetChangeListener listener) {
		if(itemSetChangeListenerArray == null){
			itemSetChangeListenerArray = new ArrayList<ItemSetChangeListener>(); 
		}
		itemSetChangeListenerArray.add(listener);
	}

	@Deprecated
	public void addListener(ItemSetChangeListener listener) {
		addItemSetChangeListener(listener);
	}

	public void removeItemSetChangeListener(ItemSetChangeListener listener) {
		if(itemSetChangeListenerArray != null){
			itemSetChangeListenerArray.remove(listener);
		}
	}

	@Deprecated
	public void removeListener(ItemSetChangeListener listener) {
		removeItemSetChangeListener(listener);
	}
  
	public void notifyItemSetChange(){
		if(itemSetChangeListenerArray != null){
			for(int i=0; i<itemSetChangeListenerArray.size(); i++){
				ItemSetChangeListener listener = itemSetChangeListenerArray.get(i);
				ItemSetChangeEvent event = new ItemSetChangeEvent() {
					public Container getContainer() {
						return EverproTreeGridHierarchicalIndexedContainer.this;
					}
				};
				listener.containerItemSetChange(event);
			}
		}
	}
	// -------------------------------
}
