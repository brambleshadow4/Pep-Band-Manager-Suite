package pepband3.data.extra;

import java.util.*;

import pepband3.data.*;

public class ForwardingListener implements DataListener {
	
	private static final PepBandDataComparator COMPARATOR = new PepBandDataComparator();
	
	private LinkedList<? extends DataListener> dataListeners;
	private HashMap<Class<?>, List<? extends PepBandData>> collections;
	
	public ForwardingListener(LinkedList<? extends DataListener> dataListeners) {
		this.dataListeners = dataListeners;
		this.collections = null;
	}
	
	public ForwardingListener(LinkedList<? extends DataListener> dataListeners, HashMap<Class<?>, List<? extends PepBandData>> collections) {
		this.dataListeners = dataListeners;
		this.collections = collections;
	}
	
	public void eventOccured(SourceEvent event) {
		if (collections != null) {
			List<? extends PepBandData> collection = collections.get(event.getOwner().getClass());
			if (collection != null) {
				Collections.sort(collection, COMPARATOR);
			}
		}
		for (DataListener listener : dataListeners) {
			listener.eventOccured(event);
		}
	}
	
	private static class PepBandDataComparator implements Comparator<PepBandData> {
		
		public int compare(PepBandData one, PepBandData two) {
			if (one.getClass().equals(two.getClass())) {
				if (one instanceof EventType) {
					EventType uno = (EventType)one;
					EventType dos = (EventType)two;
					return uno.compareTo(dos);
				} else if (one instanceof Location) {
					Location uno = (Location)one;
					Location dos = (Location)two;
					return uno.compareTo(dos);
				} else if (one instanceof Instrument) {
					Instrument uno = (Instrument)one;
					Instrument dos = (Instrument)two;
					return uno.compareTo(dos);
				} else if (one instanceof Season) {
					Season uno = (Season)one;
					Season dos = (Season)two;
					return uno.compareTo(dos);
				} else {
					return one.compareTo(two);
				}
			} else {
				return one.compareTo(two);
			}
		}
		
		public boolean equals(Object other) {
			if (other instanceof PepBandDataComparator) {
				return true;
			} else {
				return false;
			}
		}
	}
}