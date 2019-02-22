package pepband3.data;

import pepband3.data.extra.*;
import pepband3.gui.extra.*;

public class EventType extends PepBandData implements Comparable<EventType> {
	
	private String name;
	private String iconName;
	private Boolean hasLocation;
	
	public EventType(Integer paramID, String paramName, String paramIconName, Boolean paramHasLocation) {
		super(paramID);
		setName(paramName);
		setIconName(paramIconName);
		setHasLocation(paramHasLocation);
	}
	
	public void awakeAfterUnmarshalling() {
		super.awakeAfterUnmarshalling();
	}
	
	public EventType cloneData() {
		return new EventType(new Integer(getID()), new String(getName()), new String(getIconName()), new Boolean(getHasLocation()));
	}
	
	public int compareTo(EventType other) {
		if (equals(other)) {
			return 0;
		} else {
			int level1 = getIndex().compareTo(other.getIndex());
			if (level1 != 0) {
				return level1;
			} else {
				int level2 = getName().compareTo(other.getName());
				if (level2 != 0) {
					return level2;
				} else {
					return super.compareTo(other);
				}
			}
		}
	}
	
	public Boolean getHasLocation() {
		return hasLocation;
	}
	
	public String getIconName() {
		return iconName;
	}
	
	public Integer getIndex() {
		return DataManager.getDataManager().indexOfEventType(this);
	}
	
	public String getName() {
		return name;
	}
	
	public void setHasLocation(Boolean value) {
		if (value != null) {
			Boolean oldVal = hasLocation;
			hasLocation = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.HAS_LOCATION, oldVal, value));
		} else {
			throw new NullPointerException("HAS LOCATION FOR EVENT TYPE CANNOT BE NULL");
		}
	}
	
	public void setIconName(String value) {
		if (value != null) {
			String oldVal = iconName;
			iconName = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.ICON_NAME, oldVal, value));
		} else {
			throw new NullPointerException("ICON NAME FOR EVENT TYPE CANNOT BE NULL");
		}
	}
	
	public void setName(String value) {
		if (value != null) {
			String oldVal = name;
			name = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.NAME, oldVal, value));
		} else {
			throw new NullPointerException("NAME FOR EVENT TYPE CANNOT BE NULL");
		}
	}
	
	public String toString() {
		return getName();
	}
}