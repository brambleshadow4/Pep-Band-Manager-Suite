package pepband3.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TreeMap;

import pepband3.data.extra.SourceEvent;
import pepband3.gui.extra.DataField;

public class Member extends PepBandData implements Comparable<Member> {
	
	private String firstName;
	private String middleName;
	private String lastName;
	private String nickName;
	private Integer classYear;
	private Instrument instrument;
	private Sex sex;
	private String netID;
	
	public Member(Integer paramID, String paramFirstName, String paramLastName, String paramNickName, Integer paramClassYear, Instrument paramInstrument, String paramNetID, String middleName, Sex sex) {
		super(paramID);
		setFirstName(paramFirstName);
		setLastName(paramLastName);
		setNickName(paramNickName);
		setClassYear(paramClassYear);
		setInstrument(paramInstrument);
		setNetID(paramNetID);
		setMiddleName(middleName);
		setSex(sex);
	}
	
	public void awakeAfterUnmarshalling() {
		super.awakeAfterUnmarshalling();
	}
	
	public Member cloneData(TreeMap<Instrument,Instrument> instrumentMap) {
		return new Member(
				new Integer(getID()),
				new String(getFirstName()),
				new String(getLastName()),
				new String(getNickName()),
				new Integer(getClassYear()),
				instrumentMap.get(getInstrument()),
				new String(getNetID()),
				new String (getMiddleName()),
				getSex());
	}
	
	public int compareTo(Member other) {
		if (equals(other)) {
			return 0;
		} else {
			return getID().compareTo(other.getID());
		}
	}
	
	public Integer getClassYear() {
		return classYear;
	}
	
	public ArrayList<PepBandEvent> getEvents(Season season) {
		ArrayList<PepBandEvent> events = new ArrayList<PepBandEvent>();
		for (PepBandEvent event : season.getEvents()) {
			if (event.getMembers().contains(this)) {
				int index = Collections.binarySearch(events,event);
				if (index < 0) {
					events.add((-index) - 1, event);
				}
			}
		}
		return events;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getFullName() {
		if (nickName.isEmpty()) {
			return firstName + " " + lastName;
		} else {
			return firstName + " \"" + nickName + "\" " + lastName;
		}
	}
	
	public Instrument getInstrument() {
		return instrument;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public Integer getLifeTimePoints() {
		Integer lifeTimePoints = new Integer(0);
		for (Season season : getSeasons()) {
			lifeTimePoints += getPoints(season);
		}
		return lifeTimePoints;
	}
	
	public String getName() {
		return lastName + ", " + firstName;
	}
	
	public String getNameFirstLast() {
		return firstName + " " + lastName;
	}
	
	public String getNetID() {
		return netID;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public String getMiddleName() {
		return middleName;
	}

	public Sex getSex() {
		return sex;
	}
	
	public Integer getPoints(Band band) {
		if (band != null) {
			Integer points = new Integer(0);
			Season season = DataManager.getDataManager().getSeasonForBand(band);
			if (season != null && season.getMembers().contains(this)) {
				for (PepBandEvent event : season.getEvents()) {
					if (event.getMembers().contains(this) && event.getDate().before(Calendar.getInstance().getTime())) {
						points += event.getPointValue();
					}
				}
			}
			return points;
		} else {
			throw new NullPointerException("MEMBER CANNOT CALCULATE POINTS FOR A NULL BAND");
		}	
	}
	
	public Integer getPointsBefore(Season season, Date endDate) {
		if (endDate.after(Calendar.getInstance().getTime())) {
			endDate = Calendar.getInstance().getTime();
		}
		Integer points = new Integer(0);
		for (PepBandEvent event : season.getEvents()) {
			if (event.getMembers().contains(this) && event.getDate().before(endDate)) {
				points += event.getPointValue();
			}
		}
		return points;
	}
	
	public ArrayList<Season> getSeasons() {
		ArrayList<Season> seasons = new ArrayList<Season>();
		for (Season season : DataManager.getDataManager().getSeasons()) {
			if (season.getMembers().contains(this)) {
				int index = Collections.binarySearch(seasons,season);
				if (index < 0) {
					seasons.add((-index) - 1, season);
				}
			}
		}
		return seasons;
	}
	
	public void setClassYear(Integer value) {
		if (value != null) {
			Integer oldVal = classYear;
			classYear = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.CLASS_YEAR, oldVal, value));
		} else {
			throw new NullPointerException("CLASS YEAR OF MEMBER CANNOT BE NULL");
		}
	}
	
	public void setFirstName(String value) {
		if (value != null) {
			String oldVal = firstName;
			firstName = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.FIRST_NAME, oldVal, value));
		} else {
			throw new NullPointerException("FIRST NAME OF MEMBER CANNOT BE NULL");
		}
	}
	
	public void setInstrument(Instrument value) {
		if (value != null) {
			Instrument oldVal = instrument;
			instrument = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.INSTRUMENT, oldVal, value));
		} else {
			throw new NullPointerException("INSTRUMENT OF MEMBER CANNOT BE NULL");
		}
	}
	
	public void setLastName(String value) {
		if (value != null) {
			String oldVal = lastName;
			lastName = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.LAST_NAME, oldVal, value));
		} else {
			throw new NullPointerException("LAST NAME OF MEMBER CANNOT BE NULL");
		}
	}
	
	public void setNetID(String value) {
		if (value != null) {
			String oldVal = netID;
			netID = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.NET_ID, oldVal, value));
		} else {
			throw new NullPointerException("NET ID OF MEMBER CANNOT BE NULL");
		}
	}
	
	public void setNickName(String value) {
		if (value != null) {
			String oldVal = nickName;
			nickName = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.NICK_NAME, oldVal, value));
		} else {
			throw new NullPointerException("NICKNAME OF MEMBER CANNOT BE NULL");
		}
	}

	public void setMiddleName(String value) {
		if (value != null) {
			String oldVal = middleName;
			middleName = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.MIDDLE_NAME, oldVal, value));
		} else {
			throw new NullPointerException("MIDDLE_NAME OF MEMBER CANNOT BE NULL");
		}
	}

	public void setSex(Sex value) {
		if (value != null) {
			Sex oldVal = sex;
			sex = value;
			fireEventOccured(new SourceEvent.Field(this, DataField.SEX, oldVal, value));
		} else {
			throw new NullPointerException("SEX OF MEMBER CANNOT BE NULL");
		}
	}
	
	public String toString() {
		return getFullName();
	}
	
	public static enum Sex {
		MALE,
		FEMALE,
		OTHER;
		
		public String getName() {
			switch(this) {
			case MALE:
				return "Male";
			case FEMALE:
				return "Female";
			case OTHER:
				return "Other";
			default:
				throw new IllegalArgumentException("Member Sex name case missing");
			}
		}
	}
}