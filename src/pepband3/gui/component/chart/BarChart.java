package pepband3.gui.component.chart;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.extra.*;

public class BarChart extends JPanel {
	
	private static final int STROKE_WIDTH = 3;
	private static final double LABEL_DISTANCE = 3.0 / 4.0;
	private static final String EMPTY_LABEL = "Empty Band";
	
	private DataField dataField;
	private Font[] fonts;
	private TreeMap<Comparable,Integer> data;
	private int totalValue;
	private double largestPercentage;
	private Comparable highlight;
	
	public BarChart() {
		initialize();
	}
	
	public BarChart(DataField fieldValue, TreeMap<? extends Comparable,Integer> value) {
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
		data = new TreeMap<Comparable,Integer>();
		fonts = new Font[2];
		fonts[0] = new Font("Sans-serif",Font.BOLD,14);
		fonts[1] = new Font("Sans-serif",Font.ITALIC,12);
		totalValue = 0;
		setHighlight(null);
		
		MouseAdapter adapter = new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				int numBars = data.size();
				int barHeight = (int)(1.0 * (getHeight() - 2 * STROKE_WIDTH) / numBars);
				int yValue = STROKE_WIDTH;
				for (Comparable key : data.keySet()) {
					double percentage = 1.0 * data.get(key) / totalValue;
					int barWidth = (int) Math.round(percentage / largestPercentage * (getWidth() - 2 * STROKE_WIDTH));
					if (e.getY() >= yValue && e.getY() < yValue + barHeight) {
						if (e.getX() >= 0 && e.getX() < barWidth) {
							setHighlight(key);
						} else {
							setHighlight(null);
						}
						break;
					} else {
						yValue += barHeight;
					}
				}
			}
			
			public void mouseExited(MouseEvent e) {
				setHighlight(null);
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
		
		if (largestPercentage == 0.0) {
			g.setFont(fonts[0]);
			Rectangle2D stringSize = g.getFontMetrics().getStringBounds(EMPTY_LABEL,g);
			int labelWidth = (int)stringSize.getWidth();
			int labelHeight = (int)stringSize.getHeight();
			int xloc = (getWidth() - labelWidth) / 2;
			int yloc = (getHeight() + labelHeight) / 2;
			g.drawString(EMPTY_LABEL,xloc,yloc);
		}
		
		int numBars = data.size();
		int barHeight = (int)(1.0 * (getHeight() - 2 * STROKE_WIDTH) / numBars);
		int yValue = STROKE_WIDTH;
		for (Comparable key : data.keySet()) {
			double percentage = 1.0 * data.get(key) / totalValue;
			int barWidth = (int) Math.round(percentage / largestPercentage * (getWidth() - 2 * STROKE_WIDTH));
			
			if (highlight != null && key.equals(highlight)) {
				g.setPaint(new GradientPaint(STROKE_WIDTH,yValue,PieChart.COLORS.get(key).brighter().brighter(),barWidth,yValue,PieChart.COLORS.get(key).brighter()));
			} else {
				g.setPaint(new GradientPaint(STROKE_WIDTH,yValue,PieChart.COLORS.get(key).brighter(),barWidth,yValue,PieChart.COLORS.get(key)));
			}
			g.fillRect(STROKE_WIDTH,yValue,barWidth,barHeight);
			
			yValue += barHeight;
		}
		yValue = STROKE_WIDTH;
		for (Comparable key : data.keySet()) {
			double percentage = 1.0 * data.get(key) / totalValue;
			int barWidth = (int) Math.round(percentage / largestPercentage * (getWidth() - 2 * STROKE_WIDTH));
			
			Stroke oldStroke = g.getStroke();
			g.setStroke(new BasicStroke(STROKE_WIDTH,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			
			g.setColor(Color.BLACK);
			g.drawRect(STROKE_WIDTH,yValue,barWidth,barHeight);
			
			g.setStroke(oldStroke);
			
			if (data.get(key) > 0) {
				String label = getKeyName(key);
				String value = data.get(key).toString();
				
				g.setFont(fonts[1]);
				Rectangle2D stringSize = g.getFontMetrics().getStringBounds(label,g);
				int labelWidth = (int)stringSize.getWidth();
				int labelHeight = (int)stringSize.getHeight();
				
				g.setFont(fonts[0]);
				stringSize = g.getFontMetrics().getStringBounds(value,g);
				int valueWidth = (int)stringSize.getWidth();
				int valueHeight = (int)stringSize.getHeight();
				
				int labelYBase = yValue + (barHeight - STROKE_WIDTH + labelHeight) / 2;
				int valueYBase = yValue + (barHeight - STROKE_WIDTH + valueHeight) / 2;
				
				g.setFont(fonts[1]);
				if (labelWidth < barWidth) {
					g.drawString(label, 2 * STROKE_WIDTH, labelYBase);
				}
				
				g.setFont(fonts[0]);
				if (percentage / largestPercentage == 1.0) {
					g.drawString(value, 2 * STROKE_WIDTH + barWidth - valueWidth - 2 * STROKE_WIDTH, valueYBase);
				} else {
					g.drawString(value, 2 * STROKE_WIDTH + barWidth + 2 * STROKE_WIDTH, valueYBase);
				}
			}
			
			yValue += barHeight;
		}
		
		g.dispose();
	}
	
	public void setData(DataField fieldValue, TreeMap<? extends Comparable,Integer> value) {
		dataField = fieldValue;
		if (value == null) {
			data.clear();
			setHighlight(null);
		} else {
			data.clear();
			data.putAll(value);
			for (Comparable key : data.keySet()) {
				if (!PieChart.COLORS.containsKey(key)) {
					PieChart.COLORS.put(key,new Color((int)(255 * Math.random()),(int)(255 * Math.random()),(int)(255 * Math.random())));
				}
			}
		}
		totalValue = 0;
		largestPercentage = 0.0;
		for (Comparable key : data.keySet()) {
			totalValue += data.get(key);
		}
		for (Comparable key : data.keySet()) {
			double percentage = 1.0 * data.get(key) / totalValue;
			if (percentage > largestPercentage) {
				largestPercentage = percentage;
			}
		}
		repaint();
	}
	
	private void setHighlight(Comparable value) {
		Comparable oldValue = highlight;
		if (value == null) {
			setCursor(PieChart.DEFAULT_CURSOR);
		} else {
			setCursor(PieChart.HAND_CURSOR);
		}
		highlight = value;
		if (value != oldValue) {
			repaint();
		}
	}
	
	public void uninstall() {
		
	}
}