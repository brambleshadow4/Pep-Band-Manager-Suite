package pepband3.gui.model;

import javax.swing.*;
import pepband3.data.*;
import pepband3.data.extra.*;

public class DataComboBoxModel extends AbstractListModel implements ComboBoxModel {
	
	private DataListener dataListener;
	private Class type;
	private Object selectedItem;
	
	public DataComboBoxModel(Class paramType) {
		initialize(paramType);
		if (getSize() > 0) {
			setSelectedItem(getElementAt(0));
		}
	}
	
	private void initialize(Class paramType) {
		if (paramType != null) {
			type = paramType;
			dataListener = new DataListener() {
				public void eventOccured(SourceEvent sourceEvent) {
					if (sourceEvent.isField()) {
						SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
						if (fieldEvent.getOwner().getClass().equals(type)) {
							fireContentsChanged(fieldEvent.getOwner(), 0, getSize());
						}
					} else if (sourceEvent.isList()) {
						SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
						if (listEvent.getElement().getClass().equals(type)) {
							fireContentsChanged(listEvent.getOwner(), 0, getSize());
							if (listEvent.getType() == SourceEvent.List.REMOVE && listEvent.containsElement(selectedItem) && !type.equals(EventType.class) && !type.equals(Location.class)) {
								setSelectedItem(getSize() > 0 ? getElementAt(0) : null);
							}
						}
					}
				}
			};
			DataManager.getDataManager().addDataListener(dataListener);
		} else {
			throw new NullPointerException("CANNOT INITIALIZE EVENT COMBO BOX TO HAVE NULL TYPE");
		}
	}
	
	public Object getElementAt(int index) {
		DataManager dataManager = DataManager.getDataManager();
		if (type == Instrument.class) {
			return dataManager.getInstruments().get(index);
		} else if (type == Location.class) {
			return dataManager.getLocations().get(index);
		} else if (type == EventType.class) {
			return dataManager.getEventTypes().get(index);
		} else if (type == Season.class) {
			return dataManager.getSeasons().get(index);
		} else {
			return null;
		}
	}
	
	public Object getSelectedItem() {
		return selectedItem;
	}
	
	public int getSize() {
		DataManager dataManager = DataManager.getDataManager();
		if (type == Instrument.class) {
			return dataManager.getInstruments().size();
		} else if (type == Location.class) {
			return dataManager.getLocations().size();
		} else if (type == EventType.class) {
			return dataManager.getEventTypes().size();
		} else if (type == Season.class) {
			return dataManager.getSeasons().size();
		} else {
			return 0;
		}
	}
	
	public void setSelectedItem(Object anItem) {
		selectedItem = anItem;
		fireContentsChanged(this, 0, 0);
	}
	
	public void uninstall() {
		DataManager.getDataManager().removeDataListener(dataListener);
	}
}