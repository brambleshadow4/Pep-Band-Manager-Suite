package pepband3.gui.extra;

import java.util.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.gui.model.*;

public class BandTableFilter extends RowFilter<BandTableModel,Integer> {
	
	private EnumSet<DataField> comboOptions;
	private HashSet<String> comboSet;
	
	private String[] comboFilter;
	private String firstName;
	private String lastName;
	private String nickName;
	private Integer classYear;
	private Instrument instrument;
	private Integer maxPoints, minPoints;
	
	private Band band;
	
	public BandTableFilter(Band paramBand) {
		band = paramBand;
		
		comboOptions = EnumSet.noneOf(DataField.class);
		comboSet = new HashSet<String>();
		
		comboFilter = null;
		firstName = null;
		lastName = null;
		nickName = null;
		classYear = null;
		instrument = null;
		maxPoints = null;
		minPoints = null;
	}
	
	public EnumSet<DataField> getOptions() {
		return comboOptions;
	}
	
	public boolean include(RowFilter.Entry entry) {
		Member member = (Member)((BandTableModel)entry.getModel()).getValueAt((Integer)entry.getIdentifier(),0);
		
		if (comboFilter != null) {
			prepareComboSet(member);
			entryLoop: for (String entryPart : comboFilter) {
				if (entryPart.trim().isEmpty()) {
					continue entryLoop;
				} else {
					setLoop: for (String setPart : comboSet) {
						if (setPart.trim().isEmpty()) {
							continue setLoop;
						} else if (setPart.contains(entryPart)) {
							continue entryLoop;
						}
					}
					return false;
				}
			}
		} else if (firstName != null && !member.getFirstName().toLowerCase().startsWith(firstName.toLowerCase())) {
			return false;
		}
		if (lastName != null && !member.getLastName().toLowerCase().startsWith(lastName.toLowerCase())) {
			return false;
		}
		if (nickName != null && !member.getNickName().toLowerCase().startsWith(nickName.toLowerCase())) {
			return false;
		}
		if (nickName != null && !member.getNickName().toLowerCase().startsWith(nickName.toLowerCase())) {
			return false;
		}
		if (classYear != null && !member.getClassYear().equals(classYear)) {
			return false;
		}
		if (instrument != null && !member.getInstrument().equals(instrument)) {
			return false;
		}
		if (maxPoints != null && member.getPoints(band) > maxPoints) {
			return false;
		}
		if (minPoints != null && member.getPoints(band) < minPoints) {
			return false;
		}
		return true;
	}
	
	private void prepareComboSet(Member member) {
		comboSet.clear();
		if (comboOptions.contains(DataField.FIRST_NAME)) {
			comboSet.add(member.getFirstName().toLowerCase());
		}
		if (comboOptions.contains(DataField.LAST_NAME)) {
			comboSet.add(member.getLastName().toLowerCase());
		}
		if (comboOptions.contains(DataField.NICK_NAME)) {
			comboSet.add(member.getNickName().toLowerCase());
		}
		if (comboOptions.contains(DataField.NET_ID)) {
			comboSet.add(member.getNetID().toLowerCase());
		}
		if (comboOptions.contains(DataField.CLASS_YEAR)) {
			comboSet.add(member.getClassYear().toString().toLowerCase());
		}
		if (comboOptions.contains(DataField.INSTRUMENT)) {
			comboSet.add(member.getInstrument().getName().toLowerCase());
		}
	}
	
	public void setClassYear(Integer value) {
		classYear = value;
	}
	
	public void setComboFitler(String value) {
		if (value == null || value.trim().isEmpty()) {
			comboFilter = null;
		} else {
			comboFilter = value.toLowerCase().split(",|\"|\\s");
		}
	}
	
	public void setFirstName(String value) {
		firstName = value;
	}
	
	public void setInstrument(Instrument value) {
		instrument = value;
	}
	
	public void setLastName(String value) {
		lastName = value;
	}
	
	public void setMaxPoints(Integer value) {
		maxPoints = value;
	}
	
	public void setMinPoints(Integer value) {
		minPoints = value;
	}
	
	public void setNickName(String value) {
		nickName = value;
	}
}