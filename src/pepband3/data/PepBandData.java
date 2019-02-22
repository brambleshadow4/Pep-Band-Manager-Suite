package pepband3.data;

import java.io.*;
import java.util.*;

import pepband3.data.extra.*;

public abstract class PepBandData implements Serializable {
	
	private static final long serialVersionUID = 112200776633111122l;
	
	private LinkedList<ForwardingListener> listeners;
	
	private Integer id;
	
	public PepBandData(Integer value) {
		listeners = new LinkedList<ForwardingListener>();
		setID(value);
	}
	
	public void addDataListener(ForwardingListener value) {
		listeners.add(value);
	}
	
	public void awakeAfterUnmarshalling() {
		
	}
	
	public int compareTo(PepBandData other) {
		return getID().compareTo(other.getID());
	}
	
	public boolean equals(Object otherObject) {
		if (otherObject instanceof PepBandData) {
			PepBandData other = (PepBandData)otherObject;
			return getID().equals(other.getID()) && getClass().equals(otherObject.getClass());
		} else {
			return false;
		}
	}
	
	protected void fireEventOccured(SourceEvent e) {
		for (DataListener listener : getDataListeners()) {
			listener.eventOccured(e);
		}
	}
	
	public LinkedList<ForwardingListener> getDataListeners() {
		return listeners;
	}
	
	public Integer getID() {
		return id;
	}
	
	public void prepareForMarshalling() {
		listeners.clear();
	}
	
	public void removeDataListener(ForwardingListener value) {
		listeners.remove(value);
	}
	
	public void setID(Integer value) {
		if (value != null && value >= 0) {
			id = value;
		} else {
			throw new NullPointerException("ID FOR PEP BAND DATA CANNOT BE NULL OR LESS THAN ZERO");
		}
	}
	
	public String toString() {
		return "Pep Band Data | Abstract Pep Band Data | ID: " + getID();
	}
}