package pepband3.data;

import pepband3.data.extra.*;
import pepband3.gui.extra.*;

public class Location extends PepBandData implements Comparable<Location> {
	
	private String name;
	private String iconName;
	
	public Location(Integer paramID, String paramName, String paramIconName) {
		super(paramID);
		setName(paramName);
		setIconName(paramIconName);
	}
	
	public void awakeAfterUnmarshalling() {
		super.awakeAfterUnmarshalling();
	}
	
	public Location cloneData() {
		return new Location(new Integer(getID()), new String(getName()), new String(getIconName()));
	}
	
	public int compareTo(Location other) {
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
	
	public String getIconName() {
		return iconName;
	}
	
	public Integer getIndex() {
		return DataManager.getDataManager().indexOfLocation(this);
	}
	
	public String getName() {
		return name;
	}
	
	public void setIconName(String value) {
		if (value != null) {
			String oldVal = name;
			iconName = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.ICON_NAME, oldVal, value));
		} else {
			throw new NullPointerException("ICON NAME FOR LOCATION CANNOT BE NULL");
		}
	}
	
	public void setName(String value) {
		if (value != null) {
			String oldVal = name;
			name = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.NAME, oldVal, value));
		} else {
			throw new NullPointerException("NAME FOR LOCATION CANNOT BE NULL");
		}
	}
	
	public String toString() {
		return getName();
	}
}