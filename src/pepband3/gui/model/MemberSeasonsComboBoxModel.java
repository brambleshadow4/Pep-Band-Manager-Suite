package pepband3.gui.model;

import java.util.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.data.extra.*;

public class MemberSeasonsComboBoxModel extends AbstractListModel implements ComboBoxModel {
	
	private DataListener dataListener;
	private Member member;
	private Object selectedItem;
	
	public MemberSeasonsComboBoxModel(Member paramMember) {
		initialize(paramMember);
		if (getSize() > 0) {
			setSelectedItem(getElementAt(getSize() - 1));
		}
	}
	
	private void initialize(Member paramMember) {
		if (paramMember != null) {
			member = paramMember;
			dataListener = new DataListener() {
				public void eventOccured(SourceEvent sourceEvent) {
					if (sourceEvent.isField()) {
						SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					} else if (sourceEvent.isList()) {
						SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
						if (listEvent.getOwner() instanceof Season && listEvent.containsElement(member)) {
							fireContentsChanged(member, 0, 0);
						}
					}
				}
			};
			DataManager.getDataManager().addDataListener(dataListener);
		} else {
			throw new NullPointerException("CANNOT INITIALIZE MEMBER TO NULL IN MEMBER SEASONS COMBO BOX MODEL");
		}
	}
	
	public Object getElementAt(int index) {
		ArrayList<Season> seasons = member.getSeasons();
		if (seasons == null || seasons.isEmpty()) {
			return null;
		} else {
			return seasons.get(index);
		}
	}
	
	public Object getSelectedItem() {
		return selectedItem;
	}
	
	public int getSize() {
		ArrayList<Season> seasons = member.getSeasons();
		if (seasons == null || seasons.isEmpty()) {
			return 0;
		} else {
			return seasons.size();
		}
	}
	
	public void setSelectedItem(Object anItem) {
		selectedItem = anItem;
		fireContentsChanged(this, 0, 0);
	}
	
	public void uninstall() {
		DataManager.getDataManager().removeDataListener(dataListener);
	}
}