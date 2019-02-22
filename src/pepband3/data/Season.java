package pepband3.data;

import java.util.*;

import pepband3.data.extra.*;
import pepband3.gui.extra.*;

public class Season extends Band implements Comparable<Season> {
	
	private transient ForwardingListener dataListener;
	
	private Integer startingYear;
	private Date startingDate;
	private ArrayList<PepBandEvent> events;
	
	public Season(Integer paramID, ArrayList<Member> paramMembers, Integer paramStartingYear, Date paramStartingDate,
									ArrayList<PepBandEvent> paramEvents) {
		super(paramID,paramMembers);
		initializeListeners();
		setStartingYear(paramStartingYear);
		setStartingDate(paramStartingDate);
		setEvents(paramEvents);
	}
	
	public boolean addEvent(PepBandEvent value) {
		if (value != null) {
			int index = Collections.binarySearch(events,value);
			if (index < 0) {
				events.add((-index) - 1, value);
				value.addDataListener(dataListener);
				fireEventOccured(new SourceEvent.List(this, value, (-index) - 1, SourceEvent.List.ADD));
				return true;
			} else {
				return false;
			}
		} else {
			throw new NullPointerException("CANNOT ADD A NULL EVENT TO SEASON");
		}
	}
	
	public void awakeAfterUnmarshalling() {
		super.awakeAfterUnmarshalling();
		initializeListeners();
	}
	
	public Season cloneData(TreeMap<Member,Member> memberMap, TreeMap<PepBandEvent,PepBandEvent> eventMap) {
		Integer clonedID = new Integer(getID());
		Integer clonedStartingYear = new Integer(getStartingYear());
		Date clonedStartingDate = new Date(getStartingDate().getTime());
		ArrayList<PepBandEvent> clonedEventList = new ArrayList<PepBandEvent>();
		for (PepBandEvent event : getEvents()) {
			clonedEventList.add(eventMap.get(event));
		}
		Collections.sort(clonedEventList);
		ArrayList<Member> clonedMemberList = new ArrayList<Member>();
		for (Member member : getMembers()) {
			clonedMemberList.add(memberMap.get(member));
		}
		Collections.sort(clonedMemberList);
		return new Season(clonedID, clonedMemberList, clonedStartingYear, clonedStartingDate, clonedEventList);
	}
	
	public int compareTo(Season other) {
		if (equals(other)) {
			return 0;
		} else {
			return getStartingYear().compareTo(other.getStartingYear());
		}
	}
	
	public ArrayList<PepBandEvent> getEventsOfType(EventType value) {
		ArrayList<PepBandEvent> eventList = new ArrayList<PepBandEvent>();
		for (PepBandEvent event : events) {
			if (event.getEventType().equals(value)) {
				int index = Collections.binarySearch(eventList,event);
				if (index < 0) {
					eventList.add((-index) - 1, event);
				}
			}
		}
		return eventList;
	}
	
	public ArrayList<PepBandEvent> getEvents() {
		return events;
	}
	
	public ArrayList<EventType> getEventTypes() {
		ArrayList<EventType> eventTypes = new ArrayList<EventType>();
		for (PepBandEvent event : events) {
			int index = Collections.binarySearch(eventTypes, event.getEventType());
			if (index < 0) {
				eventTypes.add((-index) - 1, event.getEventType());
			}
		}
		return eventTypes;
	}
	
	public ArrayList<Location> getLocations() {
		ArrayList<Location> locations = new ArrayList<Location>();
		for (PepBandEvent event : events) {
			if (event.getEventType().getHasLocation()) {
				int index = Collections.binarySearch(locations, event.getLocation());
				if (index < 0) {
					locations.add((-index) - 1, event.getLocation());
				}
			}
		}
		return locations;
	}
	
	public String getName() {
		return getStartingYear() + " - " + (getStartingYear() + 1) + " Season";
	}
	
	public Date getStartingDate() {
		return startingDate;
	}
	
	public Integer getStartingYear() {
		return startingYear;
	}
	
	public int indexOfEvent(PepBandEvent value) {
		return events.indexOf(value);
	}
	
	private void initializeListeners() {
		HashMap<Class<?>, List<? extends PepBandData>> collectionMap = new HashMap<Class<?>, List<? extends PepBandData>>();
		collectionMap.put(PepBandEvent.class, events);
		dataListener = new ForwardingListener(getDataListeners(), collectionMap);
		if (events != null) {
			for (PepBandEvent event : events) {
				event.addDataListener(dataListener);
			}
		}
	}
	
	public boolean removeEvent(PepBandEvent value) {
		if (value != null) {
			int oldIndex = indexOfEvent(value);
			boolean success = events.remove(value);
			if (success) {
				value.removeDataListener(dataListener);
				fireEventOccured(new SourceEvent.List(this, value, oldIndex, SourceEvent.List.REMOVE));
			}
			return success;
		} else {
			throw new NullPointerException("CANNOT REMOVE A NULL EVENT FROM SEASON");
		}
	}
	
	private void setEvents(ArrayList<PepBandEvent> value) {
		if (value != null) {
			if (events == null) {
				events = new ArrayList<PepBandEvent>();
			} else {
				if (!events.isEmpty()) {
					ArrayList<PepBandEvent> oldEvents = new ArrayList<PepBandEvent>(events);
					for (PepBandEvent event : oldEvents) {
						removeEvent(event);
					}
					events.clear();
				}
			}
			for (PepBandEvent event : value) {
				addEvent(event);
			}
		} else {
			throw new NullPointerException("EVENTS FOR SEASON CANNOT BE NULL");
		}
	}
	
	public void setStartingDate(Date value) {
		if (value != null) {
			Date oldVal = startingDate;
			startingDate = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.STARTING_DATE, oldVal, value));
		} else {
			throw new NullPointerException("STARTING DATE FOR SEASON CANNOT BE NULL");
		}
	}
	
	private void setStartingYear(Integer value) {
		if (value != null) {
			startingYear = value;
		} else {
			throw new NullPointerException("STARTING YEAR FOR SEASON CANNOT BE NULL");
		}
	}
	
	public String toString() {
		return getName();
	}
}