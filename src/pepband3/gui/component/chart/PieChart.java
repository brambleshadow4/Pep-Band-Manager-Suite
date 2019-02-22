package pepband3.gui.component.chart;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.*;
import pepband3.gui.extra.*;

public class PieChart extends JPanel {
	
	public static final HashMap<Object,Color> COLORS = new HashMap<Object,Color>();
	
	public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	
	private static final int STROKE_WIDTH = 3;
	private static final double LABEL_DISTANCE = 3.0 / 4.0;
	private static final double LABEL_DISTANCE_2 = 2.0 / 3.0;
	private static final String EMPTY_LABEL = "Empty Data Set";
	private static final boolean TEXT_ONLY = true;
	
	private DataField dataField;
	private Font[] fonts;
	private HashMap<Comparable,Integer> unsortedData;
	private TreeMap<Comparable,Integer> data;
	private TreeMap<Comparable,Integer> angles;
	private int totalValue;
	private Comparable highlight;
	
	private DataListener dataListener;
	
	public PieChart() {
		initialize();
	}
	
	public PieChart(DataField fieldValue, TreeMap<? extends Comparable,Integer> value) {
		initialize();
		setData(fieldValue, value);
	}
	
	private String getKeyName(Comparable key) {
		if (key instanceof Instrument) {
			return ((Instrument)key).getName();
		} else if (key instanceof Integer) {
			return ((Integer)key).toString();
		} else if (key instanceof EventType) {
			return ((EventType)key).getName();
		} else if (key instanceof Location) {
			return ((Location)key).getName();
		} else if (key instanceof StatisticsWindow.PointRange){
			return ((StatisticsWindow.PointRange)key).toString();
		} else {
			return key.toString();
		}
	}
	
	private ImageIcon getIconForKey(Comparable key) {
		int iconSize = 32;
		if (key instanceof EventType) {
			return Tools.getEventIcon(((EventType)key).getIconName() + iconSize);
		} else if (key instanceof Location) {
			return Tools.getLocationIcon(((Location)key).getIconName() + iconSize);
		} else {
			return null;
		}
	}
	
	private StatisticsWindow getParentStatisticsWindow() {
		Component component = getParent();
		while(component != null && !(component instanceof StatisticsWindow)) {
			component = component.getParent();
		}
		return (StatisticsWindow)component;
	}
	
	private void initialize() {
		setOpaque(false);
		dataField = null;
		unsortedData = new HashMap<Comparable,Integer>();
		data = new TreeMap<Comparable,Integer>();
		angles = new TreeMap<Comparable,Integer>();
		fonts = new Font[2];
		fonts[0] = new Font("Sans-serif",Font.BOLD,12);
		fonts[1] = new Font("Sans-serif",Font.ITALIC,10);
		totalValue = 0;
		setHighlight(null);
		
		dataListener = new DataListener() {
			public void eventOccured(SourceEvent sourceEvent) {
				if (sourceEvent.isField()) {
					SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					if (fieldEvent.getField() == DataField.NAME || (!TEXT_ONLY && fieldEvent.getField() == DataField.ICON_NAME)) {
						if (dataField == DataField.convertClass(fieldEvent.getOwner().getClass())) {
							repaint();
						}
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (dataField == DataField.convertClass(listEvent.getElement().getClass()) && unsortedData.containsKey(listEvent.getElement()) && listEvent.getType() == SourceEvent.List.ORDER) {
						setData(dataField, new HashMap<Comparable, Integer>(unsortedData));
					}
				}
			}
		};
		DataManager.getDataManager().addDataListener(dataListener);
		
		MouseAdapter adapter = new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				int radius = Math.max(1,Math.min((getWidth() - 2 * STROKE_WIDTH) / 2, (getHeight() - 2 * STROKE_WIDTH) / 2));
				int centerX = getWidth() / 2;
				int centerY = getHeight() / 2;
				int clickRadius = (int) Math.sqrt(Math.pow(e.getX() - centerX, 2) + Math.pow(e.getY() - centerY, 2));
				if (clickRadius <= radius) {
					int clickAngle = -1;
					if (e.getX() - centerX >= 0 && centerY - e.getY() >= 0) {
						clickAngle = (int) Math.round(Math.toDegrees(Math.atan((1.0 * centerY - e.getY()) / (e.getX() - centerX))));
					} else if (e.getX() - centerX < 0 && centerY - e.getY() >= 0) {
						clickAngle = 180 + (int) Math.round(Math.toDegrees(Math.atan((1.0 * centerY - e.getY()) / (e.getX() - centerX))));
					} else if (e.getX() - centerX < 0 && centerY - e.getY() < 0) {
						clickAngle = 180 + (int) Math.round(Math.toDegrees(Math.atan((1.0 * centerY - e.getY()) / (e.getX() - centerX))));
					} else {
						clickAngle = 360 + (int) Math.round(Math.toDegrees(Math.atan((1.0 * centerY - e.getY()) / (e.getX() - centerX))));
					}
					int beginAngle = 0;
					for (Comparable key : data.keySet()) {
						int endAngle = beginAngle + angles.get(key);
						if (clickAngle >= beginAngle && clickAngle < endAngle) {
							setHighlight(key);
							break;
						} else {
							beginAngle = endAngle;
						}
					}
				} else {
					setHighlight(null);
				}
			}
			
			public void mouseExited(MouseEvent e) {
				if (highlight != null) {
					setHighlight(null);
				}
			}
			
			public void mousePressed(MouseEvent e) {
				if (highlight != null) {
					StatisticsWindow parent = getParentStatisticsWindow();
					if (parent != null) {
						InternalWindow source = parent.getSource();
						if (source instanceof EventWindow) {
							EventWindow window = (EventWindow)source;
							int count = window.highlight(dataField,highlight);
						} else if (source instanceof SeasonWindow) {
							SeasonWindow window = (SeasonWindow)source;
							int count = window.highlight(dataField,highlight);
						}
						source.display(false);
						try {
							source.setIcon(false);
							source.setSelected(false);
							parent.setIcon(false);
							parent.setSelected(true);
							JDesktopPane desktop = Tools.getDesktopPane();
							if (desktop.getComponentZOrder(parent) != 0 || desktop.getComponentZOrder(source) != 1) {
								desktop.setComponentZOrder(parent,0);
								desktop.setComponentZOrder(source,1);
								desktop.repaint();
							}	
						} catch (Exception exc) {
							System.err.println("Could not properly order StatisticsWindow and its source during pie chart click");
							exc.printStackTrace();
						}
					}
				}
			}
		};
		addMouseMotionListener(adapter);
		addMouseListener(adapter);
	}
	
	protected void paintComponent(Graphics gfx) {
		Graphics2D g = (Graphics2D)gfx;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING ,RenderingHints.VALUE_ANTIALIAS_ON );
		g.setRenderingHint(RenderingHints.KEY_RENDERING  ,RenderingHints.VALUE_RENDER_QUALITY );
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL  ,RenderingHints.VALUE_STROKE_NORMALIZE );
		
		if (totalValue == 0) {
			g.setFont(fonts[0]);
			Rectangle2D stringSize = g.getFontMetrics().getStringBounds(EMPTY_LABEL,g);
			int labelWidth = (int)stringSize.getWidth();
			int labelHeight = (int)stringSize.getHeight();
			int xloc = (getWidth() - labelWidth) / 2;
			int yloc = (getHeight() + labelHeight) / 2;
			g.drawString(EMPTY_LABEL,xloc,yloc);
		}
		
		int diameter = Math.max(1,Math.min(getWidth() - 2 * STROKE_WIDTH, getHeight() - 2 * STROKE_WIDTH));
		int radius = Math.max(1,Math.min((getWidth() - 2 * STROKE_WIDTH) / 2, (getHeight() - 2 * STROKE_WIDTH) / 2));
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		int cornerX = centerX - radius;
		int cornerY = centerY - radius;
		int angle = 0;
		for (Comparable key : data.keySet()) {
			int arcAngle = angles.get(key);
			
			Point2D center = new Point2D.Float(centerX, centerY);
			float[] distanceArray = {0.0f, 1.0f};
			Color[] colorArray = new Color[2];
			if (highlight != null && key.equals(highlight)) {
				colorArray[0] = COLORS.get(key).brighter().brighter();
				colorArray[1] = COLORS.get(key).brighter();
			} else {
				colorArray[0] = COLORS.get(key).brighter();
				colorArray[1] = COLORS.get(key);
			}
			g.setPaint(new RadialGradientPaint(center, radius, center, distanceArray, colorArray, RadialGradientPaint.CycleMethod.NO_CYCLE));
			g.fillArc(cornerX,cornerY,diameter,diameter,angle,arcAngle);
			
			angle += arcAngle;
		}
		angle = 0;
		for (Comparable key : data.keySet()) {
			int arcAngle = angles.get(key);
			
			Stroke oldStroke = g.getStroke();
			g.setStroke(new BasicStroke(STROKE_WIDTH,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			
			g.setColor(Color.BLACK);
			g.drawArc(cornerX,cornerY,diameter,diameter,angle,arcAngle);
			
			float deltaX = (float)(radius * Math.cos(Math.toRadians(angle)));
			float deltaY = (float)(radius * Math.sin(Math.toRadians(angle)));
			g.drawLine(centerX, centerY, centerX + (int)deltaX, centerY - (int)deltaY);
			
			g.setStroke(oldStroke);
			if (TEXT_ONLY || !(key instanceof Location || key instanceof EventType)) {
				deltaX = (float)(LABEL_DISTANCE * radius * Math.cos(Math.toRadians(angle + arcAngle / 2)));
				deltaY = (float)(LABEL_DISTANCE * radius * Math.sin(Math.toRadians(angle + arcAngle / 2)));
			
				g.setFont(fonts[0]);
				String topString = data.get(key).toString();
				Rectangle2D stringSize = g.getFontMetrics().getStringBounds(topString,g);
				int topWidth = (int)stringSize.getWidth();
				int topHeight = (int)stringSize.getHeight();
				
				g.setFont(fonts[1]);
				String middleString = (int)(100.0 * data.get(key) / totalValue) + "%";
				stringSize = g.getFontMetrics().getStringBounds(middleString,g);
				int midWidth = (int)stringSize.getWidth();
				int midHeight = (int)stringSize.getHeight();
				
				g.setFont(fonts[1]);
				String bottomString = getKeyName(key);
				stringSize = g.getFontMetrics().getStringBounds(bottomString,g);
				int botWidth = (int)stringSize.getWidth();
				int botHeight = (int)stringSize.getHeight();
				
				g.setFont(fonts[0]);
				g.drawString(topString, centerX + deltaX - topWidth / 2,  centerY - deltaY - (int)(0.5 * midHeight));
				g.setFont(fonts[1]);
				g.drawString(middleString, centerX + deltaX - midWidth / 2,  centerY - deltaY + (int)(0.5 * midHeight));
				g.setFont(fonts[1]);
				g.drawString(bottomString, centerX + deltaX - botWidth / 2,  centerY - deltaY + (int)(0.5 * midHeight) + botHeight);
			} else {
				deltaX = (float)(LABEL_DISTANCE_2 * radius * Math.cos(Math.toRadians(angle + arcAngle / 2)));
				deltaY = (float)(LABEL_DISTANCE_2 * radius * Math.sin(Math.toRadians(angle + arcAngle / 2)));
				
				g.setFont(fonts[0]);
				String topString = data.get(key).toString();
				Rectangle2D stringSize = g.getFontMetrics().getStringBounds(topString,g);
				int topWidth = (int)stringSize.getWidth();
				int topHeight = (int)stringSize.getHeight();
				
				g.setFont(fonts[1]);
				String middleString = (int)(100.0 * data.get(key) / totalValue) + "%";
				stringSize = g.getFontMetrics().getStringBounds(middleString,g);
				int midWidth = (int)stringSize.getWidth();
				int midHeight = (int)stringSize.getHeight();
				
				ImageIcon icon = getIconForKey(key);
				int iconWidth = icon == null ? 0 : icon.getIconWidth();
				int iconHeight = icon == null ? 0 : icon.getIconHeight();
				
				g.setFont(fonts[0]);
				g.drawString(topString, centerX + deltaX - topWidth / 2, centerY - deltaY - (int)(0.5 * midHeight));
				g.setFont(fonts[1]);
				g.drawString(middleString, centerX + deltaX - midWidth / 2, centerY - deltaY + (int)(0.5 * midHeight));
				icon.paintIcon(this, g, centerX + (int) deltaX - iconWidth / 2, centerY - (int) (deltaY + 0.5 * midHeight));
			}
			
			angle += arcAngle;
		}
		
		g.dispose();
	}
	
	public static void recolor() {
		for (Object key : COLORS.keySet()) {
			COLORS.put(key,new Color((int)(255 * Math.random()),(int)(255 * Math.random()),(int)(255 * Math.random())));
		}
	}
	
	public void setData(DataField fieldValue, Map<? extends Comparable,Integer> value) {
		dataField = fieldValue;
		if (value == null) {
			unsortedData.clear();
			syncSortedData();
			angles.clear();
			totalValue = 0;
			setHighlight(null);
		} else {
			angles.clear();
			unsortedData.clear();
			unsortedData.putAll(value);
			syncSortedData();
			totalValue = 0;
			for (Comparable key : data.keySet()) {
				totalValue += data.get(key);
			}
			for (Comparable key : data.keySet()) {
				angles.put(key,(int) Math.round(1.0 * data.get(key) / totalValue * 360));
				if (!COLORS.containsKey(key)) {
					COLORS.put(key,new Color((int)(255 * Math.random()),(int)(255 * Math.random()),(int)(255 * Math.random())));
				}
			}
		}
		repaint();
	}
	
	private void setHighlight(Comparable value) {
		Comparable oldValue = highlight;
		if (value == null) {
			setCursor(DEFAULT_CURSOR);
		} else {
			setCursor(HAND_CURSOR);
		}
		highlight = value;
		if (value != oldValue) {
			repaint();
		}
	}
	
	private void syncSortedData() {
		data.clear();
		data.putAll(unsortedData);
	}
	
	public void uninstall() {
		DataManager.getDataManager().removeDataListener(dataListener);
	}
}