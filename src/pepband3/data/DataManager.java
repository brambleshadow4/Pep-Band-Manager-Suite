package pepband3.data;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import pepband3.IO;
import pepband3.data.extra.DataListener;
import pepband3.data.extra.ForwardingListener;
import pepband3.data.extra.SourceEvent;
import pepband3.gui.extra.DataField;

public class DataManager implements Serializable {
	
	public static final int GOOD = 0;
	public static final int IDS_INIT = 1;
	public static final int IDS_RESET = 2;
	public static final int SEASONS_INIT = 4;
	public static final int CURRENT_SEASON_SET = 8;
	public static final int DEFAULT_INSTRUMENT_INSTALL = 16;
	public static final int DEFAULT_EVENT_TYPE_INSTALL = 32;
	public static final int DEFAULT_LOCATION_INSTALL = 64;
	public static final int DETACHED_INSTRUMENT_ATTACHED = 128;
	public static final int DETACHED_EVENT_TYPE_ATTACHED = 256;
	public static final int DETACHED_LOCATION_ATTACHED = 512;
	public static final int DETACHED_MEMBERS_ATTACHED = 1024;
	
	private static final IDComparator ID_COMPARATOR = new IDComparator();
	private static final MasterComparator MASTER_COMPARATOR = new MasterComparator();
	
	private static DataManager dataManager;
	private static final long serialVersionUID = 112200776633111177l;
	
	private final String w1 = "W A R N I N G ! ! !";
	private final String w2 = "Heedlessly altering the contents of this file may hinder (and most likely prevent) the program from loading the data.";
	private final String w3 = "Sufficient alteration and/or damage to this file may result in the perminant loss of all information stored herein";
	
	private LinkedList<DataListener> listeners;
	private ArrayList<Instrument> instruments;
	private ArrayList<EventType> eventTypes;
	private ArrayList<Location> locations;
	private ArrayList<Season> seasons;
	
	private Season currentSeason;
	private Integer nextDataID;
	
	private transient ForwardingListener dataListener;
	
	private transient int maintenanceResults;
	
	private DataManager(int startID) {
		listeners = new LinkedList<DataListener>();
		
		seasons = new ArrayList<Season>();
		eventTypes = new ArrayList<EventType>();
		instruments = new ArrayList<Instrument>();
		locations = new ArrayList<Location>();
		
		nextDataID = new Integer(startID);
	}
	
	public void addDataListener(DataListener value) {
		listeners.add(value);
	}
	
	public boolean addInstrument(Instrument value) {
		if (value != null) {
			if (!instruments.contains(value)) {
				instruments.add(value);
				value.addDataListener(dataListener);
				fireEventOccured(new SourceEvent.List(this, value, instruments.size() - 1, SourceEvent.List.ADD));
				return true;
			} else {
				return false;
			}
		} else {
			throw new NullPointerException("CANNOT ADD A NULL INSTRUMENT TO DATA MANAGER");
		}
	}
	
	public boolean addLocation(Location value) {
		if (value != null) {
			if (!locations.contains(value)) {
				locations.add(value);
				value.addDataListener(dataListener);
				fireEventOccured(new SourceEvent.List(this, value, locations.size() - 1, SourceEvent.List.ADD));
				return true;
			} else {
				return false;
			}
		} else {
			throw new NullPointerException("CANNOT ADD A NULL LOCATION TO DATA MANAGER");
		}
	}
	
	public boolean addSeason(Season value) {
		if (value != null) {
			int index = Collections.binarySearch(seasons, value);
			if (index < 0) {
				seasons.add((-index) - 1, value);
				value.addDataListener(dataListener);
				fireEventOccured(new SourceEvent.List(this, value, (-index) - 1, SourceEvent.List.ADD));
				return true;
			} else {
				return false;
			}
		} else {
			throw new NullPointerException("CANNOT ADD A NULL SEASON TO DATA MANAGER");
		}
	}
	
	public boolean addEventType(EventType value) {
		if (value != null) {
			if (!eventTypes.contains(value)) {
				eventTypes.add(value);
				value.addDataListener(dataListener);
				fireEventOccured(new SourceEvent.List(this, value, eventTypes.size() - 1, SourceEvent.List.ADD));
				return true;
			} else {
				return false;
			}
		} else {
			throw new NullPointerException("CANNOT ADD A NULL EVENT TYPE TO DATA MANAGER");
		}
	}
	
	private void awakeAfterUnmarshalling() {
		HashMap<Class<?>, List<? extends PepBandData>> collectionMap = new HashMap<Class<?>, List<? extends PepBandData>>();
		collectionMap.put(Instrument.class, instruments);
		collectionMap.put(Location.class, locations);
		collectionMap.put(EventType.class, eventTypes);
		dataListener = new ForwardingListener(listeners, collectionMap);
		
		for (Instrument instrument : getInstruments()) {
			instrument.addDataListener(dataListener);
			instrument.awakeAfterUnmarshalling();
		}
		for (EventType eventType : getEventTypes()) {
			eventType.addDataListener(dataListener);
			eventType.awakeAfterUnmarshalling();
		}
		for (Location location : getLocations()) {
			location.addDataListener(dataListener);
			location.awakeAfterUnmarshalling();
		}
		for (Season season : getSeasons()) {
			season.addDataListener(dataListener);
			season.awakeAfterUnmarshalling();
			for (Member member: season.getMembers()) {
				member.awakeAfterUnmarshalling();
			}
			for (PepBandEvent event : season.getEvents()) {
				event.awakeAfterUnmarshalling();
			}
		}
	}
	
	public void changeIndex(PepBandData element, Integer increment) {
		if (element != null && increment != null && increment != 0) {
			if (element instanceof EventType) {
				int index = eventTypes.indexOf(element);
				if (index + increment >= 0 && index + increment < eventTypes.size()) {
					eventTypes.add(index + increment,eventTypes.remove(index));
					int numChanged = Math.abs(increment) + 1;
					int firstChanged = Math.min(index, index + increment);
					Object[] elements = new Object[numChanged];
					Integer[] indecies = new Integer[numChanged];
					for (index = 0; index < numChanged; index++) {
						elements[index] = eventTypes.get(firstChanged + index);
						indecies[index] = firstChanged + index;
					}
					fireEventOccured(new SourceEvent.List(this, elements, indecies, SourceEvent.List.ORDER));
				}
			} else if (element instanceof Instrument) {
				int index = instruments.indexOf(element);
				if (index + increment >= 0 && index + increment < instruments.size()) {
					instruments.add(index + increment,instruments.remove(index));
					int numChanged = Math.abs(increment) + 1;
					int firstChanged = Math.min(index, index + increment);
					Object[] elements = new Object[numChanged];
					Integer[] indecies = new Integer[numChanged];
					for (index = 0; index < numChanged; index++) {
						elements[index] = instruments.get(firstChanged + index);
						indecies[index] = firstChanged + index;
					}
					fireEventOccured(new SourceEvent.List(this, elements, indecies, SourceEvent.List.ORDER));
				}
			} else if (element instanceof Location) {
				int index = locations.indexOf(element);
				if (index + increment >= 0 && index + increment < locations.size()) {
					locations.add(index + increment,locations.remove(index));
					int numChanged = Math.abs(increment) + 1;
					int firstChanged = Math.min(index, index + increment);
					Object[] elements = new Object[numChanged];
					Integer[] indecies = new Integer[numChanged];
					for (index = 0; index < numChanged; index++) {
						elements[index] = locations.get(firstChanged + index);
						indecies[index] = firstChanged + index;
					}
					fireEventOccured(new SourceEvent.List(this, elements, indecies, SourceEvent.List.ORDER));
				}
			}
		} else {
			throw new NullPointerException("CANNOT CHANGE INDEX IN DATA MANAGER DUE TO NULL");
		}
	}
	
	private void checkIDs() {
		ArrayList<PepBandData> allData = getAllData();
		if (nextDataID == -1) {
			if (allData.isEmpty()) {
				nextDataID = 0;
				maintenanceResults += IDS_INIT;
			} else {
				resetIDs();
				maintenanceResults += IDS_RESET + IDS_INIT;
			}
		} else {
			if (allData.get(0).getID() >= 0 && allData.get(allData.size() - 1).getID() < nextDataID) {
				maintenanceResults += GOOD;
			} else {
				resetIDs();
				maintenanceResults += IDS_RESET;
			}
		}
	}
	
	private void checkData() {
		if (instruments.isEmpty()) {
			installDefaultInstruments();
			maintenanceResults += DEFAULT_INSTRUMENT_INSTALL;
		}
		if (eventTypes.isEmpty()) {
			installDefaultEventTypes();
			maintenanceResults += DEFAULT_EVENT_TYPE_INSTALL;
		}
		if (locations.isEmpty()) {
			installDefaultLocations();
			maintenanceResults += DEFAULT_LOCATION_INSTALL;
		}
		ArrayList<Instrument> detachedInstruments = new ArrayList<Instrument>();
		ArrayList<EventType> detachedEventTypes = new ArrayList<EventType>();
		ArrayList<Location> detachedLocations = new ArrayList<Location>();
		boolean detachedMembers = false;
		for (Season season : seasons) {
			for (Member member : season.getMembers()) {
				checkMemberFields(member);
				if (!instruments.contains(member.getInstrument())) {
					detachedInstruments.add(member.getInstrument());
				}
			}
			for (PepBandEvent event : season.getEvents()) {
				if (!eventTypes.contains(event.getEventType())) {
					detachedEventTypes.add(event.getEventType());
				}
				if (!locations.contains(event.getLocation())) {
					detachedLocations.add(event.getLocation());
				}
				for (Member member : event.getMembers()) {
					if (!season.getMembers().contains(member)) {
						season.addMember(member);
						detachedMembers = true;
					}
				}
			}
		}
		if (!detachedInstruments.isEmpty()) {
			maintenanceResults += DETACHED_INSTRUMENT_ATTACHED;
			for (Instrument instrument : detachedInstruments) {
				addInstrument(instrument);
			}
		}
		if (!detachedEventTypes.isEmpty()) {
			maintenanceResults += DETACHED_EVENT_TYPE_ATTACHED;
			for (EventType eventType : detachedEventTypes) {
				addEventType(eventType);
			}
		}
		if (!detachedLocations.isEmpty()) {
			maintenanceResults += DETACHED_LOCATION_ATTACHED;
			for (Location location : detachedLocations) {
				addLocation(location);
			}
		}
		if (detachedMembers) {
			maintenanceResults += DETACHED_MEMBERS_ATTACHED;
		}
	}
	
	private void checkSeasons() {
		if (seasons.isEmpty()) {
			if (currentSeason == null) {
				maintenanceResults += SEASONS_INIT;
			} else {
				maintenanceResults += SEASONS_INIT + CURRENT_SEASON_SET;
			}
			Calendar date = Calendar.getInstance();
			Season season = new Season(dataManager.getDataID(), new ArrayList<Member>(), date.get(Calendar.YEAR), date.getTime(), new ArrayList<PepBandEvent>());
			dataManager.addSeason(season);
			dataManager.setCurrentSeason(season);
		} else {
			if (currentSeason == null) {
				setCurrentSeason(seasons.get(seasons.size() - 1));
				maintenanceResults += CURRENT_SEASON_SET;
			} else {
				maintenanceResults += GOOD;
			}
		}
	}
	
	/**
	 * Checks field of the provided Member for null values in new properties.
	 * 
	 * @param member the Member to check
	 */
	private void checkMemberFields(Member member) {
		if (member.getMiddleName() == null) {
			member.setMiddleName("");
		}
		if (member.getSex() == null) {
			member.setSex(Member.Sex.OTHER);
		}
	}
	
	private DataManager cloneDataManager() {
		synchronized(this) {
			/* Make deep copies of all data and put them into maps with the original mapping to the clone */
			TreeMap<Instrument,Instrument> instrumentMap = new TreeMap<Instrument,Instrument>();
			TreeMap<EventType,EventType> eventTypeMap = new TreeMap<EventType,EventType>();
			TreeMap<Location,Location> locationMap = new TreeMap<Location,Location>();
			TreeMap<Member,Member> memberMap = new TreeMap<Member,Member>();
			TreeMap<PepBandEvent,PepBandEvent> eventMap = new TreeMap<PepBandEvent,PepBandEvent>();
			TreeMap<Season,Season> seasonMap = new TreeMap<Season,Season>();
			for (Instrument instrument : instruments) {
				instrumentMap.put(instrument, instrument.cloneData());
			}
			for (EventType eventType : eventTypes) {
				eventTypeMap.put(eventType, eventType.cloneData());
			}
			for (Location location : locations) {
				locationMap.put(location, location.cloneData());
			}
			for (Season season : seasons) {
				for (Member member : season.getMembers()) {
					memberMap.put(member, member.cloneData(instrumentMap));
				}
			}
			for (Season season : seasons) {
				for (PepBandEvent event : season.getEvents()) {
					eventMap.put(event, event.cloneData(memberMap, eventTypeMap, locationMap));
				}
			}
			for (Season season : seasons) {
				seasonMap.put(season, season.cloneData(memberMap, eventMap));
			}
			/* Create a data manager clone of this one */
			DataManager clonedManager = new DataManager(nextDataID);
			for (Instrument data : instrumentMap.values()) {
				clonedManager.addInstrument(data);
			}
			for (EventType data : eventTypeMap.values()) {
				clonedManager.addEventType(data);
			}
			for (Location data : locationMap.values()) {
				clonedManager.addLocation(data);
			}
			for (Season data : seasonMap.values()) {
				clonedManager.addSeason(data);
			}
			clonedManager.setCurrentSeason(seasonMap.get(getCurrentSeason()));
			clonedManager.prepareForMarshalling();
			return clonedManager;
		}
	}
	
	public static DataManager create() {
		return new DataManager(-1);
	}
	
	private void fireEventOccured(SourceEvent e) {
		for (DataListener listener : getDataListeners()) {
			listener.eventOccured(e);
		}
	}
	
	public ArrayList<PepBandData> getAllData() {
		ArrayList<PepBandData> allData = new ArrayList<PepBandData>();
		for (Instrument instrument : getInstruments()) {
			allData.add(instrument);
		}
		for (EventType eventType : getEventTypes()) {
			allData.add(eventType);
		}
		for (Location location : getLocations()) {
			allData.add(location);
		}
		for (Season season : getSeasons()) {
			allData.add(season);
			for (Member member: season.getMembers()) {
				allData.add(member);
			}
			for (PepBandEvent event : season.getEvents()) {
				allData.add(event);
			}
		}
		Collections.sort(allData, ID_COMPARATOR);
		return allData;
	}
	
	public static DataManager getDataManager() {
		return dataManager;
	}
	
	public Season getCurrentSeason() {
		if (currentSeason == null) {
			throw new IllegalStateException("CURRENT SEASON HAS NOT BEEN SET");
		} else {
			return currentSeason;
		}
	}
	
	public PepBandData getData(Integer id) {
		for (Instrument instrument : getInstruments()) {
			if (instrument.getID().equals(id)) {
				return instrument;
			}
		}
		for (EventType eventType : getEventTypes()) {
			if (eventType.getID().equals(id)) {
				return eventType;
			}
		}
		for (Location location : getLocations()) {
			if (location.getID().equals(id)) {
				return location;
			}
		}
		for (Season season : getSeasons()) {
			if (season.getID().equals(id)) {
				return season;
			}
			for (Member member: season.getMembers()) {
				if (member.getID().equals(id)) {
					return member;
				}
			}
			for (PepBandEvent event : season.getEvents()) {
				if (event.getID().equals(id)) {
					return event;
				}
			}
		}
		return null;
	}
	
	public Integer getDataID() {
		if (nextDataID == -1) {
			throw new IllegalStateException("CANNOT PRODUCE A DATA ID BECAUSE THE COUNTER HAS NOT YET BEEN INITITIALIZED");
		} else {
			Integer dataID = new Integer(nextDataID);
			nextDataID++;
			return dataID;
		}
	}
	
	public LinkedList<DataListener> getDataListeners() {
		return listeners;
	}
	
	public TreeMap<String, LinkedList<Member>> getDuplicateNetIDs() {
		TreeMap<String, LinkedList<Member>> duplicateNetIDMap = new TreeMap<String, LinkedList<Member>>();
		LinkedList<String> singleIDList = new LinkedList<String>();
		for (Season season : getSeasons()) {
			for (Member member : season.getMembers()) {
				String netID = member.getNetID().toLowerCase();
				LinkedList<Member> list = duplicateNetIDMap.get(netID);
				if (list == null) {
					list = new LinkedList<Member>();
					list.add(member);
					duplicateNetIDMap.put(netID, list);
					singleIDList.add(netID);
				} else {
					if (!list.contains(member)) {
						list.add(member);
						singleIDList.remove(netID);
					}
				}
			}
		}
		for (String netID : singleIDList) {
			duplicateNetIDMap.remove(netID);
		}
		return duplicateNetIDMap;
	}
	
	public ArrayList<EventType> getEventTypes() {
		return eventTypes;
	}
	
	public ArrayList<Instrument> getInstruments() {
		return instruments;
	}
	
	public ArrayList<Location> getLocations() {
		return locations;
	}
	
	public Member getMemberWithNetID(String netID) {
		for (Season season: seasons) {
			for (Member member : season.getMembers()) {
				if (member.getNetID().toLowerCase().equals(netID.toLowerCase())) {
					return member;
				}
			}
		}
		return null;
	}
	
	public PepBandEvent getNewEvent() {
		Integer id = getDataID();
		String name = "New Pep Band Event";
		Date date = Calendar.getInstance().getTime();
		if (date.before(getCurrentSeason().getStartingDate())) {
			date.setTime(getCurrentSeason().getStartingDate().getTime());
		}
		Integer value = new Integer(2);
		EventType type = getEventTypes().get(0);
		Location location = getLocations().get(0);
		ArrayList<Member> members = new ArrayList<Member>();
		PepBandEvent event = new PepBandEvent(id, name, date, value, type, location, members);
		getCurrentSeason().addEvent(event);
		return event;
	}
	
	public int getMaintenanceResults() {
		return maintenanceResults;
	}
	
	public Season getSeasonForBand(Band value) {
		if (value instanceof Season) {
			return (Season)value;
		} else if (value instanceof PepBandEvent) {
			return ((PepBandEvent)value).getSeason();
		} else {
			return null;
		}
	}
	
	public ArrayList<Season> getSeasons() {
		return seasons;
	}
	
	public int indexOfEventType(EventType value) {
		return eventTypes.indexOf(value);
	}
	
	public int indexOfInstrument(Instrument value) {
		return instruments.indexOf(value);
	}
	
	public int indexOfLocation(Location value) {
		return locations.indexOf(value);
	}
	
	public int indexOfSeason(Season value) {
		return seasons.indexOf(value);
	}
	
	private void installDefaultEventTypes() {
		addEventType(new EventType(getDataID(),"Rehearsal","music",false));
		addEventType(new EventType(getDataID(),"Election","time",false));
		addEventType(new EventType(getDataID(),"Point Adjustment","adjustment",false));
		addEventType(new EventType(getDataID(),"Field Hockey","fieldball",true));
		addEventType(new EventType(getDataID(),"Football","football",true));
		addEventType(new EventType(getDataID(),"Men's Basketball","basketball",true));
		addEventType(new EventType(getDataID(),"Men's Hockey","hockey",true));
		addEventType(new EventType(getDataID(),"Men's Lacrosse","lacrosse",true));
		addEventType(new EventType(getDataID(),"Men's Soccer","soccer",true));
		addEventType(new EventType(getDataID(),"Miscellaneous","question",false));
		addEventType(new EventType(getDataID(),"Sprint Football","football",true));
		addEventType(new EventType(getDataID(),"Women's Basketball","basketball",true));
		addEventType(new EventType(getDataID(),"Women's Hockey","hockey",true));
		addEventType(new EventType(getDataID(),"Women's Lacrosse","lacrosse",true));
		addEventType(new EventType(getDataID(),"Women's Soccer","soccer",true));
		addEventType(new EventType(getDataID(),"Wrestling","boxing",true));
	}
	
	private void installDefaultInstruments() {
		addInstrument(new Instrument(getDataID(),"Flute","gclef"));
		addInstrument(new Instrument(getDataID(),"Clarinet","gclef"));
		addInstrument(new Instrument(getDataID(),"Sax","gclef"));
		addInstrument(new Instrument(getDataID(),"Trumpet","gclef"));
		addInstrument(new Instrument(getDataID(),"Horn","gclef"));
		addInstrument(new Instrument(getDataID(),"Bone","fclef"));
		addInstrument(new Instrument(getDataID(),"Tuba","fclef"));
		addInstrument(new Instrument(getDataID(),"Percussion","neutral"));
		addInstrument(new Instrument(getDataID(),"Other","stick"));
	}
	
	private void installDefaultLocations() {
		addLocation(new Location(getDataID(),"Home","home"));
		addLocation(new Location(getDataID(),"Away","away"));
		addLocation(new Location(getDataID(),"Playoff","playoff"));
	}
	
	public static void loadDataResources() {
		DataManager loadedManager = IO.loadData();
		if (loadedManager != null) {
			dataManager = loadedManager;
			dataManager.awakeAfterUnmarshalling();
			dataManager.performMaintenance();
		} else {
			dataManager = new DataManager(-1);
			dataManager.awakeAfterUnmarshalling();
			dataManager.performMaintenance();
		}
	}
	
	public void mergeInstrument(Instrument value, Instrument into) {
		if (value != null && into != null) {
			for (Season season : seasons) {
				for (Member member : season.getMembers()) {
					if (member.getInstrument().equals(value)) {
						member.setInstrument(into);
					}
				}
			}
			int oldIndex = instruments.indexOf(value);
			instruments.remove(value);
			fireEventOccured(new SourceEvent.List(this, value, oldIndex, SourceEvent.List.REMOVE));
			value.removeDataListener(dataListener);
		} else {
			throw new NullPointerException("CANNOT MERGE INSTRUMENTS DUE TO NULL");
		}
	}
	
	public void mergeLocation(Location value, Location into) {
		if (value != null && into != null) {
			for (Season season : seasons) {
				for (PepBandEvent event : season.getEvents()) {
					if (event.getLocation().equals(value)) {
						event.setLocation(into);
					}
				}
			}
			int oldIndex = locations.indexOf(value);
			locations.remove(value);
			fireEventOccured(new SourceEvent.List(this, value, oldIndex, SourceEvent.List.REMOVE));
			value.removeDataListener(dataListener);
		} else {
			throw new NullPointerException("CANNOT MERGE LOCATIONS DUE TO NULL");
		}
	}
	
	public void mergeEventType(EventType value, EventType into) {
		if (value != null && into != null) {
			for (Season season : seasons) {
				for (PepBandEvent event : season.getEvents()) {
					if (event.getEventType().equals(value)) {
						event.setEventType(into);
					}
				}
			}
			int oldIndex = eventTypes.indexOf(value);
			eventTypes.remove(value);
			fireEventOccured(new SourceEvent.List(this, value, oldIndex, SourceEvent.List.REMOVE));
			value.removeDataListener(dataListener);
		} else {
			throw new NullPointerException("CANNOT MERGE EVENT TYPES DUE TO NULL");
		}
	}
	
	private void performMaintenance() {
		maintenanceResults = GOOD;
		checkIDs();
		checkSeasons();
		checkData();
	}
	
	private void prepareForMarshalling() {
		listeners.clear();
		for (Instrument instrument : getInstruments()) {
			instrument.prepareForMarshalling();
		}
		for (EventType eventType : getEventTypes()) {
			eventType.prepareForMarshalling();
		}
		for (Location location : getLocations()) {
			location.prepareForMarshalling();
		}
		for (Season season : getSeasons()) {
			season.prepareForMarshalling();
			for (Member member: season.getMembers()) {
				member.prepareForMarshalling();
			}
			for (PepBandEvent event : season.getEvents()) {
				event.prepareForMarshalling();
			}
		}
	}
	
	public void removeDataListener(DataListener value) {
		listeners.remove(value);
	}
	
	public boolean removeSeason(Season value) {
		if (value != null) {
			int oldIndex = seasons.indexOf(value);
			boolean success = seasons.remove(value);
			if (success) {
				value.removeDataListener(dataListener);
				fireEventOccured(new SourceEvent.List(this, value, oldIndex, SourceEvent.List.REMOVE));
			}
			return success;
		} else {
			throw new NullPointerException("CANNOT REMOVE A NULL SEASON FROM DATA MANAGER");
		}
	}
	
	public void resetIDs() {
		ArrayList<PepBandData> allData = getAllData();
		Collections.sort(allData, MASTER_COMPARATOR);
		nextDataID = 0;
		for (PepBandData data : allData) {
			data.setID(getDataID());
		}
	}

	public void save(final File file) {
		prepareForMarshalling();
		IO.saveData(dataManager, true);
	}
	
	public static String saveDataResources(boolean shutdown) {
		if (shutdown) {
			dataManager.prepareForMarshalling();
			return IO.saveData(dataManager, true);
		} else {
			DataManager clonedManager = null;
			try {
				clonedManager = dataManager.cloneDataManager();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			if (clonedManager != null) {
				return IO.saveData(clonedManager, false);
			} else {
				return "Could not save data due to cloning failure";
			}
		}
	}
	
	public void setCurrentSeason(Season value) {
		if (value != null) {
			Season oldVal = currentSeason;
			currentSeason = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.CURRENT_SEASON, oldVal, value));
		} else {
			throw new NullPointerException("CURRENT SEASON IN DATA MANAGER CANNOT BE NULL");
		}
	}
	
	public String toString() {
		if (this == dataManager) {
			return "True DataManager";
		} else {
			return "False DataManager";
		}
	}
	
	private static class IDComparator implements Comparator<PepBandData> {
		public int compare(PepBandData one, PepBandData two) {
			return one.getID().compareTo(two.getID());
		}
	}
	
	private static class MasterComparator implements Comparator<PepBandData> {
		
		private final HashMap<Class<?>, Integer> PRIORITY_MAP = new HashMap<Class<?>, Integer>();
		
		public MasterComparator() {
			PRIORITY_MAP.put(Instrument.class, new Integer(0));
			PRIORITY_MAP.put(EventType.class, new Integer(1));
			PRIORITY_MAP.put(Location.class, new Integer(2));
			PRIORITY_MAP.put(Member.class, new Integer(3));
			PRIORITY_MAP.put(PepBandEvent.class, new Integer(4));
			PRIORITY_MAP.put(Season.class, new Integer(5));
		}
		
		public int compare(PepBandData one, PepBandData two) {
			if (one.equals(two)) {
				return 0;
			} else {
				Integer onePriority = PRIORITY_MAP.get(one.getClass());
				Integer twoPriority = PRIORITY_MAP.get(two.getClass());
				if (onePriority != null && twoPriority != null) {
					int level1 = onePriority.compareTo(twoPriority);
					if (level1 != 0) {
						return level1;
					} else {
						if (one instanceof Instrument && two instanceof Instrument) {
							Instrument dataOne = (Instrument) one;
							Instrument dataTwo = (Instrument) two;
							return dataOne.compareTo(dataTwo);
						} else if (one instanceof EventType && two instanceof EventType) {
							EventType dataOne = (EventType) one;
							EventType dataTwo = (EventType) two;
							return dataOne.compareTo(dataTwo);
						} else if (one instanceof Location && two instanceof Location) {
							Location dataOne = (Location) one;
							Location dataTwo = (Location) two;
							return dataOne.compareTo(dataTwo);
						} else if (one instanceof Member && two instanceof Member) {
							Member dataOne = (Member) one;
							Member dataTwo = (Member) two;
							return dataOne.compareTo(dataTwo);
						} else if (one instanceof PepBandEvent && two instanceof PepBandEvent) {
							PepBandEvent dataOne = (PepBandEvent) one;
							PepBandEvent dataTwo = (PepBandEvent) two;
							return dataOne.compareTo(dataTwo);
						} else if (one instanceof Season && two instanceof Season) {
							Season dataOne = (Season) one;
							Season dataTwo = (Season) two;
							return dataOne.compareTo(dataTwo);
						} else {
							return one.getID().compareTo(two.getID());
						}
					}
				} else {
					return one.getID().compareTo(two.getID());
				}
			}
		}
	}
}