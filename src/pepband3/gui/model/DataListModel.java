package pepband3.gui.model;

import javax.swing.*;
import pepband3.data.*;

public class DataListModel extends AbstractListModel {
	
	private Class type;
	
	public DataListModel(Class paramType) {
		initialize(paramType);
	}
	
	public void fireContentsChanged(Object source, int index0, int index1) {
		super.fireContentsChanged(source,index0,index1);
	}
	
	public void fireIntervalAdded(Object source, int index0, int index1) {
		super.fireIntervalAdded(source,index0,index1);
	}
	
	public void fireIntervalRemoved(Object source, int index0, int index1) {
		super.fireIntervalRemoved(source,index0,index1);
	}
	
	private void initialize(Class paramType) {
		if (paramType != null) {
			type = paramType;
		} else {
			throw new NullPointerException("CANNOT INITIALIZE DATA LIST MODEL TO HAVE NULL TYPE");
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
}