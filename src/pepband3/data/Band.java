package pepband3.data;

import java.util.*;

import pepband3.gui.extra.*;
import pepband3.data.extra.*;

public abstract class Band extends PepBandData {
	
	private transient ForwardingListener dataListener;
	
	private ArrayList<Member> members;
	
	public Band(Integer paramID, ArrayList<Member> paramMembers) {
		super(paramID);
		initializeListeners();
		setMembers(paramMembers);
	}
	
	public boolean addMember(Member value) {
		if (value != null) {
			int index = Collections.binarySearch(members,value);
			if (index < 0) {
				members.add((-index) - 1, value);
				fireEventOccured(new SourceEvent.List(this, value, (-index) - 1, SourceEvent.List.ADD));
				if (this instanceof PepBandEvent) {
					value.fireEventOccured(new SourceEvent.Field(value, DataField.POINTS, new Integer(-1), new Integer(-1)));
				} else if (this instanceof Season) {
					value.addDataListener(dataListener);
				}
				return true;
			} else {
				return false;
			}
		} else {
			throw new NullPointerException("CANNOT ADD A NULL MEMBER TO BAND");
		}
	}
	
	public void awakeAfterUnmarshalling() {
		super.awakeAfterUnmarshalling();
		initializeListeners();
	}
	
	public int compareTo(Band other) {
		if (equals(other)) {
			return 0;
		} else {
			return super.compareTo(other);
		}
	}
	
	public ArrayList<Integer> getClassYears() {
		ArrayList<Integer> classYears = new ArrayList<Integer>();
		for (Member member : members) {
			int index = Collections.binarySearch(classYears, member.getClassYear());
			if (index < 0) {
				classYears.add((-index) - 1, member.getClassYear());
			}
		}
		return classYears;
	}
	
	public ArrayList<Instrument> getInstruments() {
		ArrayList<Instrument> instruments = new ArrayList<Instrument>();
		for (Member member : members) {
			int index = Collections.binarySearch(instruments, member.getInstrument());
			if (index < 0) {
				instruments.add((-index) - 1, member.getInstrument());
			}
		}
		return instruments;
	}
	
	public Member getMember(Integer idValue) {
		for (Member member : getMembers()) {
			if (member.getID().equals(idValue)) {
				return member;
			}
		}
		return null;
	}
	
	public ArrayList<Member> getMembers() {
		return members;
	}
	
	public ArrayList<Member> getMembersOfInstrument(Instrument instrument) {
		ArrayList<Member> membersOfInstrument = new ArrayList<Member>();
		for (Member member : getMembers()) {
			if (member.getInstrument().equals(instrument)) {
				int index = Collections.binarySearch(membersOfInstrument, member);
				if (index < 0) {
					membersOfInstrument.add((-index) - 1, member);
				}
			}
		}
		return membersOfInstrument;
	}
	
	public int indexOfMember(Member value) {
		return members.indexOf(value);
	}
	
	private void initializeListeners() {
		if (this instanceof Season) {
			dataListener = new ForwardingListener(getDataListeners());
			if (members != null) {
				for (Member member : members) {
					member.addDataListener(dataListener);
				}
			}
		}
	}
	
	public boolean removeMember(Member value) {
		if (value != null) {
			int oldIndex =  indexOfMember(value);
			boolean success = members.remove(value);
			if (success) {
				fireEventOccured(new SourceEvent.List(this, value, oldIndex, SourceEvent.List.REMOVE));
				if (this instanceof PepBandEvent) {
					value.fireEventOccured(new SourceEvent.Field(value, DataField.POINTS, new Integer(-1), new Integer(-1)));
				} else if (this instanceof Season) {
					value.removeDataListener(dataListener);
				}
			}
			return success;
		} else {
			throw new NullPointerException("CANNOT REMOVE A NULL MEMBER FROM BAND");
		}
	}
	
	private void setMembers(ArrayList<Member> value) {
		if (value != null) {
			if (members == null) {
				members = new ArrayList<Member>();
			} else {
				if (!members.isEmpty()) {
					ArrayList<Member> oldMembers = new ArrayList<Member>(members);
					for (Member member : oldMembers) {
						removeMember(member);
					}
					members.clear();
				}
			}
			for (Member member : value) {
				addMember(member);
			}
		} else {
			throw new NullPointerException("MEMBERS FOR BAND CANNOT BE NULL");
		}
	}
	
	public Integer size() {
		return members.size();
	}
	
	public String toString() {
		return "Pep Band Data | Abstract Band";
	}
}