package pepband3.data.extra;

import java.util.*;

import pepband3.gui.extra.*;

public abstract class SourceEvent {
	
	private Object owner;
	
	public SourceEvent(Object owner) {
		this.owner = owner;
	}
	
	public Object getOwner() {
		return owner;
	}
	
	public Class getOwnerClass() {
		return owner.getClass();
	}
	
	public boolean isField() {
		return this instanceof Field;
	}
	
	public boolean isList() {
		return this instanceof List;
	}
	
	public static class List extends SourceEvent {
		
		public static final int ORDER = 0;
		public static final int ADD = 1;
		public static final int REMOVE = 2;
		
		private Object[] elements;
		private Integer[] indecies;
		private int type;
		
		public List(Object owner, Object[] elements, Integer[] indecies, int type) {
			super(owner);
			this.elements = elements;
			this.indecies = indecies;
			this.type = type;
			Arrays.sort(elements);
			Arrays.sort(indecies);
		}
		
		public List(Object owner, Object element, Integer index, int type) {
			super(owner);
			this.elements = new Object[1];
			this.elements[0] = element;
			this.indecies = new Integer[1];
			this.indecies[0] = index;
			this.type = type;
		}
		
		public boolean containsElement(Object value) {
			for (Object element : elements) {
				if (element.equals(value)) {
					return true;
				}
			}
			return false;
		}
		
		public Object getElement() {
			if (elements != null && elements.length > 0) {
				return elements[0];
			} else {
				return null;
			}
		}
		
		public Object[] getElements() {
			return elements;
		}
		
		public ArrayList<Object> getElementsAsList() {
			ArrayList<Object> list = new ArrayList<Object>();
			for (Object element : elements) {
				list.add(element);
			}
			return list;
		}
		
		public Integer getIndex() {
			if (indecies != null && indecies.length > 0) {
				return indecies[0];
			} else {
				return null;
			}
		}
		
		public Integer[] getIndecies() {
			return indecies;
		}
		
		public Integer getMaxIndex() {
			if (indecies != null && indecies.length > 0) {
				return indecies[indecies.length - 1];
			} else {
				return new Integer(-1);
			}
		}
		
		public Integer getMinIndex() {
			if (indecies != null && indecies.length > 0) {
				return indecies[0];
			} else {
				return new Integer(-1);
			}
		}
		
		public int getType() {
			return type;
		}
		
		public boolean isMultiIndex() {
			return indecies != null && indecies.length > 1;
		}
		
		public boolean isSingleIndex() {
			return indecies != null && indecies.length == 1;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SourceEvent.List" + "|");
			builder.append("Owner: " + getOwner() + "|");
			builder.append("Type: " + getType() + "|");
			builder.append("Size: " + indecies.length + "|");
			builder.append("Element: " + getElement() + "|");
			builder.append("Index: " + getIndex() + "|");
			return builder.toString();
		}
	}
	
	public static class Field extends SourceEvent {
		
		private DataField field;
		private Object oldValue;
		private Object newValue;
		
		public Field(Object owner, DataField field, Object oldValue, Object newValue) {
			super(owner);
			this.field = field;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		
		public DataField getField() {
			return field;
		}
		
		public Object getNewValue() {
			return newValue;
		}
		
		public Object getOldValue() {
			return oldValue;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SourceEvent.Field" + "|");
			builder.append("Owner: " + getOwner() + "|");
			builder.append("Field: " + getField() + "|");
			builder.append("Old: " + getOldValue() + "|");
			builder.append("New: " + getNewValue() + "|");
			return builder.toString();
		}
	}
}