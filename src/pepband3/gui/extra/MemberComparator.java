package pepband3.gui.extra;

import java.util.*;

import pepband3.data.*;

public class MemberComparator implements Comparator<Member> {
	
	private DataField sortMode;
	private Band band;
	
	public MemberComparator(Band paramBand) {
		this(DataField.FULL_NAME, paramBand);
	}
	
	public MemberComparator(DataField paramMode, Band paramBand) {
		setSortMode(paramMode);
		band = paramBand;
	}
	
	public int compare(Member one, Member two) {
		switch (sortMode) {
			case INSTRUMENT			: return compareInstrument(one,two);
			case NICK_NAME			: return compareNickName(one,two);
			case POINTS				: return comparePoints(one,two);
			case NAME				: return compareName(one,two);
			case FULL_NAME			: return compareFullName(one,two);
			case CLASS_YEAR			: return compareClassYear(one,two);
			case NET_ID				: return compareNetID(one,two);
			case LAST_NAME			: return compareLastName(one,two);
			case FIRST_NAME			: return compareFirstName(one,two);
			case NAME_FIRST_LAST	: return comapreNameFirstLast(one, two);
			default					: return compareInstrument(one,two);
		}
	}
	
	public int compareClassYear(Member one, Member two) {
		int level1 = one.getClassYear().compareTo(two.getClassYear());
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			return compareInstrument(one,two);
		}
	}
	
	public int compareFirstName(Member one, Member two) {
		int level1 = one.getFirstName().compareTo(two.getFirstName());
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			return one.getLastName().compareTo(two.getLastName());
		}
	}
	
	public int compareFullName(Member one, Member two) {
		int level1 = one.getFullName().compareTo(two.getFullName());
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			int level2 = one.getInstrument().compareTo(two.getInstrument());
			if (level2 != 0) {
				return level2;
			} else {
				return two.getPoints(band).compareTo(one.getPoints(band));
			}
		}
	}
	
	public int compareInstrument(Member one, Member two) {
		int level1 = one.getInstrument().compareTo(two.getInstrument());
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			int level2 = two.getPoints(band).compareTo(one.getPoints(band));
			if (level2 != 0) {
				return level2;
			} else {
				return one.getFullName().compareTo(two.getFullName());
			}
		}
	}
	
	public int compareLastName(Member one, Member two) {
		int level1 = one.getLastName().compareTo(two.getLastName());
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			return one.getFirstName().compareTo(two.getFirstName());
		}
	}
	
	public int compareName(Member one, Member two) {
		int level1 = one.getName().compareTo(two.getName());
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			int level2 = one.getInstrument().compareTo(two.getInstrument());
			if (level2 != 0) {
				return level2;
			} else {
				return two.getPoints(band).compareTo(one.getPoints(band));
			}
		}
	}
	
	public int comapreNameFirstLast(Member one, Member two) {
		int level1 = one.getNameFirstLast().compareTo(two.getNameFirstLast());
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			int level2 = one.getInstrument().compareTo(two.getInstrument());
			if (level2 != 0) {
				return level2;
			} else {
				return two.getPoints(band).compareTo(one.getPoints(band));
			}
		}
	}
	
	public int compareNetID(Member one, Member two) {
		if (one.equals(two)) {
			return 0;
		} else {
			return one.getNetID().compareTo(two.getNetID());
		}
	}
	
	public int compareNickName(Member one, Member two) {
		int level1 = one.getNickName().compareTo(two.getNickName());
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			int level2 = one.getInstrument().compareTo(two.getInstrument());
			if (level2 != 0) {
				return level2;
			} else {
				return two.getPoints(band).compareTo(one.getPoints(band));
			}
		}
	}
	
	public int comparePoints(Member one, Member two) {
		int level1 = two.getPoints(band).compareTo(one.getPoints(band));
		if (one.equals(two)) {
			return 0;
		} else if (level1 != 0) {
			return level1;
		} else {
			int level2 = one.getInstrument().compareTo(two.getInstrument());
			if (level2 != 0) {
				return level2;
			} else {
				return one.getFullName().compareTo(two.getFullName());
			}
		}
	}
	
	public boolean equals(Object otherObject) {
		if (otherObject instanceof MemberComparator) {
			MemberComparator other = (MemberComparator)otherObject;
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
			throw new NullPointerException("MEMBER COMPARATOR CANNOT HAVE A NULL SORT MODE");
		}
	}
}