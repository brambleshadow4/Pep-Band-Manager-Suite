package pepband3.gui.extra;

import pepband3.data.*;

public enum DataField {
	
	// The following are used by the All Data table
	DATA_TYPE,
	ID,
	
	// The following are used only by JTables such as BandTables and AdditionTabs
	FULL_NAME,
	NAME_FIRST_LAST,
	POINTS,
	
	// The following are used by both JTables and DataEvents
	INSTRUMENT,
	NICK_NAME,
	NAME,
	CLASS_YEAR,
	
	// The following are used on by DataEvents
	DATE,
	LOCATION,
	POINT_VALUE,
	EVENT_TYPE,
	FIRST_NAME,
	LAST_NAME,
	CURRENT_SEASON,
	ICON_NAME,
	INDEX,
	HAS_LOCATION,
	STARTING_DATE,
	NET_ID,
	MIDDLE_NAME,
	SEX;
	
	public String toString() {
		switch(this) {
			case INSTRUMENT :	return "Instrument";
			case NICK_NAME : 	return "Nickname";
			case NAME : 		return "Name";
			case FULL_NAME : 	return "Full Name";
			case POINTS : 		return "Season Points";
			case CLASS_YEAR : 	return "Class Year";
			case NET_ID:		return "NetID";
			case NAME_FIRST_LAST:	return "Name";
			default :			return name();
		}
	}
	
	public static DataField convertClass(Class value) {
		if (value == Instrument.class) {
			return INSTRUMENT;
		} else if (value == Location.class) {
			return LOCATION;
		} else if (value == EventType.class) {
			return EVENT_TYPE;
		} else {
			return null;
		}
	}
}