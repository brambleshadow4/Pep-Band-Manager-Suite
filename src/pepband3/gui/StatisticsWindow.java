package pepband3.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.component.chart.*;
import pepband3.gui.extra.*;

public class StatisticsWindow extends InternalWindow {
	
	private static final PointRange[] pointRanges = {new PointRange(Integer.MIN_VALUE,-1),new PointRange(0,9),new PointRange(10,19),new PointRange(20,29),new PointRange(30,39),
													new PointRange(40,49),new PointRange(50,59),new PointRange(60,69),new PointRange(70,79),
													new PointRange(80,89),new PointRange(90,99),new PointRange(100,Integer.MAX_VALUE)};
	private JPanel mainPanel;
	private JPanel buttonPanel;
	private JPanel instrumentPanel;
	private PieChart instrumentChart;
	private JLabel instrumentLabel;
	private JPanel classYearPanel;
	private PieChart classYearChart;
	private JLabel classYearLabel;
	private JPanel pointsPanel;
	private BarChart pointsChart;
	private JLabel pointsLabel;
	private JButton openButton;
	private JButton recolorButton;
	
	private RunnableAction openAction, recolorAction;
	
	private DataListener dataListener;
	
	private Band band;
	
	public StatisticsWindow(Band paramBand) {
		super("", true, true, true, true);
		if (paramBand instanceof PepBandEvent) {
			setTitle("Statistics - " + ((PepBandEvent)paramBand).getName());
		} else if (paramBand instanceof Season) {
			setTitle("Statistics - Season of " + ((Season)paramBand).getStartingYear() + " - " + (1 + ((Season)paramBand).getStartingYear()));
		} else {
			setTitle("Statistics for an unrecognized type of band");
		}
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setFrameIcon(Tools.getIcon("statistics16"));
		
		band = paramBand;
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		openAction = new RunnableAction("Open Source","Opening source of statistics window") {
			public void act() {
				InternalTableWindow window = getSource();
				window.display();
				if (window instanceof SeasonWindow) {
					((SeasonWindow)window).viewBandTable();
				}
			}
		};
		recolorAction = new RunnableAction("Recolor","Recoloring all statistics windows") {
			public void act() {
				PieChart.recolor();
				Tools.getDesktopPane().repaintAllStatisticsWindows();
				Tools.getDesktopPane().repaintAllMemberWindows();
			}
		};
		
		openAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_O);
		openAction.putValue(Action.LONG_DESCRIPTION,"Open the window displaying the band that is the source of this statistics window");
		openAction.putValue(Action.SHORT_DESCRIPTION,"Open source window");
		
		recolorAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_R);
		recolorAction.putValue(Action.LONG_DESCRIPTION,"Randomly recolor all statistics windows");
		recolorAction.putValue(Action.SHORT_DESCRIPTION,"Recolor all statistics windows");
		
		if (band instanceof Season) {
			openAction.putValue(Action.SMALL_ICON,Tools.getIcon("open16"));
		} else if (band instanceof PepBandEvent) {
			openAction.putValue(Action.SMALL_ICON,Tools.getEventIcon(((PepBandEvent)band).getEventType().getIconName() + "16"));
		}
	}
	
	private void a2Components() {
		setMinimumSize(new Dimension(700,300));
		setPreferredSize(new Dimension(700,300));
		setTransferHandler(new DataTransferHandler());
		
		mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		
		instrumentPanel = new JPanel();
		instrumentPanel.setOpaque(false);
		instrumentChart = new PieChart();
		instrumentLabel = new JLabel();
		instrumentLabel.setFont(new Font("Sans-serif",Font.BOLD,16));
		instrumentLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		classYearPanel = new JPanel();
		classYearPanel.setOpaque(false);
		classYearChart = new PieChart();
		classYearLabel = new JLabel();
		classYearLabel.setFont(new Font("Sans-serif",Font.BOLD,16));
		classYearLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		pointsPanel = new JPanel();
		pointsPanel.setOpaque(false);
		pointsChart = new BarChart();
		pointsLabel = new JLabel();
		pointsLabel.setFont(new Font("Sans-serif",Font.BOLD,16));
		pointsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		openButton = new JButton(openAction);
		recolorButton = new JButton(recolorAction);
	}
	
	private void a3Listeners() {
		dataListener = new DataListener() {
			public void eventOccured(SourceEvent sourceEvent) {
				if (sourceEvent.isField()) {
					SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					if (fieldEvent.getField() == DataField.INSTRUMENT && band.getMembers().contains(fieldEvent.getOwner())) {
						reevaluateInstruments();
					} else if (fieldEvent.getField() == DataField.CLASS_YEAR && band.getMembers().contains(fieldEvent.getOwner())) {
						reevaluateClassYears();
					} else if (fieldEvent.getField() == DataField.POINTS && band.getMembers().contains(fieldEvent.getOwner())) {
						reevaluatePoints();
					} else if (fieldEvent.getField() == DataField.POINT_VALUE && !Collections.disjoint(((PepBandEvent) fieldEvent.getOwner()).getMembers(), band.getMembers())) {
						reevaluatePoints();
					} else if (fieldEvent.getField() == DataField.NAME && fieldEvent.getOwner().equals(band)) {
						setTitle("Statistics - " + ((PepBandEvent) band).getName());
					} else if (fieldEvent.getField() == DataField.EVENT_TYPE && fieldEvent.getOwner().equals(band)) {
						openAction.putValue(Action.SMALL_ICON,Tools.getEventIcon(((PepBandEvent) band).getEventType().getIconName() + "16"));
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (listEvent.getOwner().equals(band) && (listEvent.getType() == SourceEvent.List.ADD || listEvent.getType() == SourceEvent.List.REMOVE)) {
						reevaluateAll();
					} else if (listEvent.getOwner().equals(DataManager.getDataManager().getSeasonForBand(band)) && listEvent.getElement() instanceof PepBandEvent && listEvent.getType() == SourceEvent.List.REMOVE) {
						reevaluatePoints();
					}
				}
			}
		};
		DataManager.getDataManager().addDataListener(dataListener);
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout());
		mainPanel.setLayout(new GridLayout(1,3,GUIManager.INS,GUIManager.INS));
		buttonPanel.setLayout(new FlowLayout());
		
		instrumentPanel.setLayout(new BorderLayout());
		instrumentPanel.add(instrumentChart,BorderLayout.CENTER);
		instrumentPanel.add(instrumentLabel,BorderLayout.NORTH);
		
		classYearPanel.setLayout(new BorderLayout());
		classYearPanel.add(classYearChart,BorderLayout.CENTER);
		classYearPanel.add(classYearLabel,BorderLayout.NORTH);
		
		pointsPanel.setLayout(new BorderLayout());
		pointsPanel.add(pointsChart,BorderLayout.CENTER);
		pointsPanel.add(pointsLabel,BorderLayout.NORTH);
			
		mainPanel.add(instrumentPanel);
		mainPanel.add(classYearPanel);
		mainPanel.add(pointsPanel);
		
		buttonPanel.add(openButton);
		buttonPanel.add(recolorButton);
		
		add(mainPanel,BorderLayout.CENTER);
		add(buttonPanel,BorderLayout.SOUTH);
	}
	
	private void a5Initialize() {
		reevaluateAll();
	}
	
	protected void closingOperations() {
		instrumentChart.uninstall();
		classYearChart.uninstall();
		pointsChart.uninstall();
		DataManager.getDataManager().removeDataListener(dataListener);
		super.closingOperations();
	}
	
	public Band getBand() {
		return band;
	}
	
	public InternalTableWindow getSource() {
		if (band instanceof PepBandEvent) {
			return Tools.getDesktopPane().addEventWindow((PepBandEvent)band);
		} else if (band instanceof Season) {
			return Tools.getDesktopPane().addSeasonWindow((Season)band);
		} else {
			return null;
		}
	}
	
	private void reevaluateAll() {
		ArrayList<Member> memberList = new ArrayList<Member>(band.getMembers());
		Collections.sort(memberList, new MemberComparator(DataField.POINTS, band));
		
		TreeMap<Instrument,Integer> instrumentMap = new TreeMap<Instrument,Integer>();
		TreeMap<Integer,Integer> classYearMap = new TreeMap<Integer,Integer>();
		TreeMap<PointRange,Integer> pointsMap = new TreeMap<PointRange,Integer>();
		for (PointRange range : pointRanges) {
			pointsMap.put(range,new Integer(0));
		}
			
		for (Member member : memberList) {
			if (instrumentMap.containsKey(member.getInstrument())) {
				instrumentMap.put(member.getInstrument(),new Integer(instrumentMap.get(member.getInstrument()) + 1));
			} else {
				instrumentMap.put(member.getInstrument(),new Integer(1));
			}
			
			if (classYearMap.containsKey(member.getClassYear())) {
				classYearMap.put(member.getClassYear(),new Integer(classYearMap.get(member.getClassYear()) + 1));
			} else {
				classYearMap.put(member.getClassYear(),new Integer(1));
			}
			
			PointRange pointRange = null;
			for (PointRange range : pointRanges) {
				if (member.getPoints(band) >= range.getStartValue() && member.getPoints(band) <= range.getEndValue()) {
					pointRange = range;
					break;
				}
			}
			pointsMap.put(pointRange,new Integer(pointsMap.get(pointRange) + 1));
		}
		
		instrumentChart.setData(DataField.INSTRUMENT,instrumentMap);
		instrumentLabel.setText("Instrumentation");
		
		classYearChart.setData(DataField.CLASS_YEAR,classYearMap);
		classYearLabel.setText("Class Years");
		
		pointsChart.setData(DataField.POINTS,pointsMap);
		pointsLabel.setText("Points");
	}
	
	private void reevaluateClassYears() {
		ArrayList<Member> memberList = new ArrayList<Member>(band.getMembers());
		Collections.sort(memberList, new MemberComparator(DataField.POINTS, band));
		TreeMap<Integer,Integer> classYearMap = new TreeMap<Integer,Integer>();
		for (Member member : memberList) {
			if (classYearMap.containsKey(member.getClassYear())) {
				classYearMap.put(member.getClassYear(),new Integer(classYearMap.get(member.getClassYear()) + 1));
			} else {
				classYearMap.put(member.getClassYear(),new Integer(1));
			}
		}
		classYearChart.setData(DataField.CLASS_YEAR,classYearMap);
		classYearLabel.setText("Class Years");
	}
	
	private void reevaluateInstruments() {
		ArrayList<Member> memberList = new ArrayList<Member>(band.getMembers());
		Collections.sort(memberList, new MemberComparator(DataField.POINTS, band));
		TreeMap<Instrument,Integer> instrumentMap = new TreeMap<Instrument,Integer>();
		for (Member member : memberList) {
			if (instrumentMap.containsKey(member.getInstrument())) {
				instrumentMap.put(member.getInstrument(),new Integer(instrumentMap.get(member.getInstrument()) + 1));
			} else {
				instrumentMap.put(member.getInstrument(),new Integer(1));
			}
		}
		instrumentChart.setData(DataField.INSTRUMENT,instrumentMap);
		instrumentLabel.setText("Instrumentation");
	}
	
	private void reevaluatePoints() {
		ArrayList<Member> memberList = new ArrayList<Member>(band.getMembers());
		Collections.sort(memberList, new MemberComparator(DataField.POINTS, band));
		TreeMap<PointRange,Integer> pointsMap = new TreeMap<PointRange,Integer>();
		for (PointRange range : pointRanges) {
			pointsMap.put(range,new Integer(0));
		}
		for (Member member : memberList) {
			PointRange pointRange = null;
			for (PointRange range : pointRanges) {
				if (member.getPoints(band) >= range.getStartValue() && member.getPoints(band) <= range.getEndValue()) {
					pointRange = range;
					break;
				}
			}
			pointsMap.put(pointRange,new Integer(pointsMap.get(pointRange) + 1));
		}
		pointsChart.setData(DataField.POINTS,pointsMap);
		pointsLabel.setText("Points");
	}
	
	public static class PointRange implements Comparable<PointRange> {
		
		private int startValue;
		private int endValue;
		
		public PointRange(int paramStartValue, int paramEndValue) {
			if (paramStartValue < paramEndValue) {
				startValue = paramStartValue;
				endValue = paramEndValue;
			} else {
				throw new IllegalArgumentException("START VALUE MUST BE LESS THAN END VALUE IN POINT RANGE");
			}
		}
		
		public int compareTo(PointRange other) {
			Integer thisStart = new Integer(startValue);
			Integer otherStart = new Integer(other.getStartValue());
			return thisStart.compareTo(otherStart);
		}
		
		public int getEndValue() {
			return endValue;
		}
		
		public int getRange() {
			return endValue - startValue;
		}
		
		public int getStartValue() {
			return startValue;
		}
		
		public void setStartValue(int value) {
			if (value < endValue) {
				startValue = value;
			}
		}
		
		public void setEndValue(int value) {
			if (value > startValue) {
				endValue = value;
			}
		}
		
		public String toString() {
			if (startValue == Integer.MIN_VALUE) {
				return "<= " + endValue;
			} else if (endValue == Integer.MAX_VALUE) {
				return ">= " + startValue;
			} else {
				return startValue + " to " + endValue;
			}
		}
	}
}