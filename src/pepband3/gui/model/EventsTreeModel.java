package pepband3.gui.model;

import java.util.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.extra.*;

public class EventsTreeModel implements TreeModel {
	
	private LinkedList<TreeModelListener> listeners;
	
	private DataListener dataListener;
	private Season season;
	
	public EventsTreeModel(Season paramSeason) {
		initialize();
		setSeason(paramSeason);
	}
	
	public void addTreeModelListener(TreeModelListener value) {
		listeners.add(value);
	}
	
	protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		for (TreeModelListener listener : listeners) {
			listener.treeNodesChanged(new TreeModelEvent(source,path,childIndices,children));
		}
	}
	
	protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
		for (TreeModelListener listener : listeners) {
			listener.treeNodesInserted(new TreeModelEvent(source,path,childIndices,children));
		}
	}
	
	protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
		for (TreeModelListener listener : listeners) {
			listener.treeNodesRemoved(new TreeModelEvent(source,path,childIndices,children));
		}
	}
	
	protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children)  {
		for (TreeModelListener listener : listeners) {
			listener.treeStructureChanged(new TreeModelEvent(source,path,childIndices,children));
		}
	}
	
	public Object getChild(Object parent, int index) {
		if (parent instanceof Season) {
			return DataManager.getDataManager().getEventTypes().get(index);
		} else if (parent instanceof EventType) {
			return season.getEventsOfType((EventType)parent).get(index);
		} else {
			return null;
		}
	}
	
	public int getChildCount(Object parent) {
		if (parent instanceof Season) {
			return DataManager.getDataManager().getEventTypes().size();
		} else if (parent instanceof EventType) {
			return season.getEventsOfType((EventType)parent).size();
		} else {
			return 0;
		}
	}
	
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof Season && child instanceof EventType) {
			return DataManager.getDataManager().getEventTypes().indexOf((EventType)child);
		} else if (parent instanceof EventType && child instanceof PepBandEvent) {
			return season.getEventsOfType((EventType)parent).indexOf((PepBandEvent)child);
		} else {
			return -1;
		}
	}
	
	public Object getRoot() {
		return season;
	}
	
	public boolean isLeaf(Object node) {
		return node instanceof PepBandEvent;
	}
	
	public void removeTreeModelListener(TreeModelListener value) {
		listeners.remove(value);
	}
	
	public void valueForPathChanged(TreePath path, Object newValue) {
		System.out.println("WTF?");
	}
	
	private void initialize() {
		listeners = new LinkedList<TreeModelListener>();
		dataListener = new DataListener() {
			public void eventOccured(SourceEvent sourceEvent) {
				if (sourceEvent.isField()) {
					SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					if (fieldEvent.getOwner() instanceof PepBandEvent) {
						if (fieldEvent.getField() == DataField.EVENT_TYPE && fieldEvent.getOldValue() instanceof EventType && fieldEvent.getNewValue() instanceof EventType) {
							Object[] path = {season, (EventType)fieldEvent.getOldValue()};
							fireTreeStructureChanged(this, path, null, null);
							Object[] path2 = {season, (EventType)fieldEvent.getNewValue()};
							fireTreeStructureChanged(this, path2, null, null);
						} else if (fieldEvent.getField() != DataField.POINT_VALUE) {
							PepBandEvent event = (PepBandEvent)fieldEvent.getOwner();
							Object[] path = {season, event.getEventType()};
							fireTreeStructureChanged(this, path, null, null);
						}
					} else if (fieldEvent.getOwner() instanceof EventType) {
							Object[] path = {season};
							fireTreeNodesChanged(this, path, null, null);
					} else if (fieldEvent.getField() == DataField.ICON_NAME && fieldEvent.getOwner() instanceof Location) {
						for (EventType eventType : DataManager.getDataManager().getEventTypes()) {
							Object[] path = {season, eventType};
							fireTreeNodesChanged(this, path, null, null);
						}
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (listEvent.getOwner().equals(season) && listEvent.getElement() instanceof PepBandEvent && listEvent.getType() == SourceEvent.List.ADD) {
						PepBandEvent event = (PepBandEvent)listEvent.getElement();
						Object[] path = {season, event.getEventType()};
						fireTreeStructureChanged(this, path, null, null);
					} else if (listEvent.getOwner().equals(season) && listEvent.getElement() instanceof PepBandEvent && listEvent.getType() == SourceEvent.List.REMOVE) {
						PepBandEvent event = (PepBandEvent)listEvent.getElement();
						Object[] path = {season, event.getEventType()};
						fireTreeStructureChanged(this, path, null, null);
					} else if (listEvent.getOwner() instanceof DataManager && listEvent.getElement() instanceof EventType && listEvent.getType() == SourceEvent.List.ADD) {
						Object[] path = {season};
						fireTreeStructureChanged(this, path, null, null);
					} else if (listEvent.getOwner() instanceof DataManager && listEvent.getElement() instanceof EventType && listEvent.getType() == SourceEvent.List.REMOVE) {
						Object[] path = {season};
						fireTreeStructureChanged(this, path, null, null);
					} else if (listEvent.getOwner() instanceof DataManager && listEvent.getElement() instanceof EventType && listEvent.getType() == SourceEvent.List.ORDER) {
						Object[] path = {season};
						fireTreeStructureChanged(this, path, null, null);
					}
				}
			}
		};
		DataManager.getDataManager().addDataListener(dataListener);
	}
	
	public void setSeason(Season value) {
		if (value != null) {
			season = value;
			Season[] path = {season};
			fireTreeStructureChanged(this, path, null, null);
		} else {
			throw new NullPointerException("DO NOT SET EVENTS TREE MODEL'S SEASON TO NULL");
		}
	}
	
	public void uninstall() {
		DataManager.getDataManager().removeDataListener(dataListener);
	}
}