package pepband3.gui.extra;

import java.util.*;

import pepband3.data.*;

public class EventComparator implements Comparator<PepBandEvent> {
	
	private DataField sortMode;
	
	public EventComparator(DataField paramSortMode) {
		setSortMode(paramSortMode);
	}
	
	public int compare(PepBandEvent one, PepBandEvent two) {
		switch (sortMode) {
			case DATE		: return compareDate(one,two);
			case EVENT_TYPE	: return compareEventType(one,two);
			default			: return compareDate(one,two);
		}
	}
	
	public int compareDate(PepBandEvent one, PepBandEvent two) {
		int level1 = one.getDate().compareTo(two.getDate());
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			int level2 = one.getEventType().compareTo(two.getEventType());
			if (level2 != 0) {
				return level2;
			} else {
				int level3 = one.getLocation().compareTo(two.getLocation());
				if (level3 != 0) {
					return level3;
				} else {
					return one.getPointValue().compareTo(two.getPointValue());
				}
			}
		}
	}
	
	public int compareEventType(PepBandEvent one, PepBandEvent two) {
		int level1 = one.getEventType().compareTo(two.getEventType());
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			int level2 = one.getDate().compareTo(two.getDate());
			if (level2 != 0) {
				return level2;
			} else {
				int level3 = one.getLocation().compareTo(two.getLocation());
				if (level3 != 0) {
					return level3;
				} else {
					return one.getPointValue().compareTo(two.getPointValue());
				}
			}
		}
	}
	
	public boolean equals(Object otherObject) {
		if (otherObject instanceof EventComparator) {
			EventComparator other = (EventComparator)otherObject;
			return getSortMode().equals(other.getSortMode());
		} else {
			return false;
		}
	}
	
	public DataField getSortMode() {
		return sortMode;
	}
	
	public void setSortMode(DataField value) {
		if (value != null) {
			sortMode = value;
		} else {
			throw new NullPointerException("EVENT COMPARATOR CANNOT HAVE A NULL SORT MODE");
		}
	}
}