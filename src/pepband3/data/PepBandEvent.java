package pepband3.data;

import java.text.*;
import java.util.*;

import pepband3.data.extra.*;
import pepband3.gui.extra.*;

public class PepBandEvent extends Band implements Comparable<PepBandEvent> {
	
	private String name;
	private Date date;
	private Integer pointValue;
	private EventType eventType;
	private Location location;
	
	public PepBandEvent(Integer paramID, String paramName, Date paramDate, Integer paramPointValue,
												EventType paramEventType, Location paramLocation, ArrayList<Member> paramMembers) {
		super(paramID, paramMembers);
		setName(paramName);
		setDate(paramDate);
		setPointValue(paramPointValue);
		setEventType(paramEventType);
		setLocation(paramLocation);
	}
	
	public void awakeAfterUnmarshalling() {
		super.awakeAfterUnmarshalling();
	}
	
	/* This method creates a deep copy using the mappings provided. It is to be used for autosaving */
	public PepBandEvent cloneData(TreeMap<Member,Member> memberMap, TreeMap<EventType,EventType> eventTypeMap, TreeMap<Location,Location> locationMap) {
		Integer clonedID = new Integer(getID());
		String clonedName = new String(getName());
		Date clonedDate = new Date(getDate().getTime());
		Integer clonedPointValue = new Integer(getPointValue());
		EventType clonedEventType = eventTypeMap.get(getEventType());
		Location clonedLocation = locationMap.get(getLocation());
		ArrayList<Member> clonedMemberList = new ArrayList<Member>();
		for (Member member : getMembers()) {
			clonedMemberList.add(memberMap.get(member));
		}
		Collections.sort(clonedMemberList);
		return new PepBandEvent(clonedID, clonedName, clonedDate, clonedPointValue, clonedEventType, clonedLocation, clonedMemberList);
	}
	
	/* This method creates a shallow copy using the instance's own data. It is to be used for cloning an event in the season window */
	public PepBandEvent cloneEvent() {
		Integer clonedID = DataManager.getDataManager().getDataID();
		String clonedName = new String(getName() + " Clone");
		Date clonedDate = new Date(getDate().getTime());
		Integer clonedPointValue = new Integer(getPointValue());
		EventType clonedEventType = getEventType();
		Location clonedLocation = getLocation();
		ArrayList<Member> clonedMemberList = new ArrayList<Member>(getMembers());
		Collections.sort(clonedMemberList);
		return new PepBandEvent(clonedID, clonedName, clonedDate, clonedPointValue, clonedEventType, clonedLocation, clonedMemberList);
	}
	
	public int compareTo(PepBandEvent other) {
		if (equals(other)) {
			return 0;
		} else {
			int level1 = getDate().compareTo(other.getDate());
			if (level1 != 0) {
				return level1;
			} else {
				int level2 = getEventType().compareTo(other.getEventType());
				if (level2 != 0) {
					return level2;
				} else {
					int level3 = getLocation().compareTo(other.getLocation());
					if (level3 != 0) {
						return level3;
					} else {
						int level4 = getName().compareTo(other.getName());
						if (level4 != 0) {
							return level4;
						} else {
							return 0;
						}
					}
				}
			}
		}
	}
	
	public Date getDate() {
		return date;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getPointValue() {
		return pointValue;
	}
	
	public EventType getEventType() {
		return eventType;
	}
	
	public Season getSeason() {
		DataManager dataManager = DataManager.getDataManager();
		for (Season season : dataManager.getSeasons()) {
			if (season.getEvents().contains(this)) {
				return season;
			}
		}
		return null;
	}
	
	public void setDate(Date value) {
		if (value != null) {
			Date oldVal = date;
			date = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.DATE, oldVal, value));
			if (oldVal != null && !getMembers().isEmpty()) {
				Date currentDate = Calendar.getInstance().getTime();
				boolean dateCheck1 = value.before(currentDate) && oldVal.after(currentDate);
				boolean dateCheck2 = value.after(currentDate) && oldVal.before(currentDate);
				if (dateCheck1) {
					fireEventOccured(new SourceEvent.Field(this, DataField.POINT_VALUE, pointValue, pointValue));
				} else if (dateCheck2) {
					fireEventOccured(new SourceEvent.Field(this, DataField.POINT_VALUE, pointValue, pointValue));
				}
			}
		} else {
			throw new NullPointerException("DATE OF PEP BAND EVENT CANNOT BE NULL");
		}
	}
	
	public void setLocation(Location value) {
		if (value != null) {
			Location oldVal = location;
			location = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.LOCATION, oldVal, value));
		} else {
			throw new NullPointerException("LOCATION OF PEP BAND EVENT CANNOT BE NULL");
		}
	}
	
	public void setName(String value) {
		if (value != null) {
			String oldVal = name;
			name = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.NAME, oldVal, value));
		} else {
			throw new NullPointerException("NAME OF PEP BAND EVENT CANNOT BE NULL");
		}
	}
	
	public void setPointValue(Integer value) {
		if (value != null) {
			Integer oldVal = pointValue == null ? 0 : pointValue;
			pointValue = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.POINT_VALUE, oldVal, value));
		} else {
			throw new NullPointerException("POINT VALUE OF PEP BAND EVENT CANNOT BE NULL");
		}
	}
	
	public void setEventType(EventType value) {
		if (value != null) {
			EventType oldVal = eventType;
			eventType = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.EVENT_TYPE, oldVal, value));
		} else {
			throw new NullPointerException("EVENT TYPE OF PEP BAND EVENT CANNOT BE NULL");
		}
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Event | Name: " + getName() + " ");
		builder.append("| Type: " + getEventType() + " ");
		if (getEventType().getHasLocation()) {
			builder.append("| Location: " + getLocation() + " ");
		}
		builder.append("| Date: " + DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(getDate()) + " ");
		builder.append("| Point Value: " + getPointValue() + " ");
		return builder.toString();
	}
}