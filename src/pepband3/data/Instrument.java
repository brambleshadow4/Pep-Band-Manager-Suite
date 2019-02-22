package pepband3.data;

import pepband3.data.extra.*;
import pepband3.gui.extra.*;

public class Instrument extends PepBandData implements Comparable<Instrument> {
	
	private String name;
	private String iconName;
	
	public Instrument(Integer paramID, String paramName, String paramIconName) {
		super(paramID);
		setName(paramName);
		setIconName(paramIconName);
	}
	
	public void awakeAfterUnmarshalling() {
		super.awakeAfterUnmarshalling();
	}
	
	public Instrument cloneData() {
		return new Instrument(new Integer(getID()), new String(getName()), new String(getIconName()));
	}
	
	public int compareTo(Instrument other) {
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
		return DataManager.getDataManager().indexOfInstrument(this);
	}
	
	public String getName() {
		return name;
	}
	
	public void setIconName(String value) {
		if (value != null) {
			String oldVal = iconName;
			iconName = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.ICON_NAME, oldVal, value));
		} else {
			throw new NullPointerException("ICON NAME FOR INSTRUMENT CANNOT BE NULL");
		}
	}
	
	public void setName(String value) {
		if (value != null) {
			String oldVal = name;
			name = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.NAME, oldVal, value));
		} else {
			throw new NullPointerException("NAME FOR INSTRUMENT CANNOT BE NULL");
		}
	}
	
	public String toString() {
		return getName();
	}
}