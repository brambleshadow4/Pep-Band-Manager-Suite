package pepband3.gui.component.chart;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.*;
import pepband3.gui.extra.*;

public class HistoryChart extends JPanel {
	
	private static final boolean CENTER_POINT_ZOOM = false;
	
	private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MMMMM");
	private static final TreeMap<Integer,Color> COLOR_MAP = new TreeMap<Integer,Color>();
	private static final TreeMap<Integer,Font> FONT_MAP = new TreeMap<Integer,Font>();
	private static final int FONT_MAX = 16;
	private static final int FONT_MIN = 4;
	private static final int FONT_INC = 2;
	
	static {
		COLOR_MAP.put(Calendar.JANUARY, new Color(0,25,125));
		COLOR_MAP.put(Calendar.FEBRUARY, new Color(255,50,150));
		COLOR_MAP.put(Calendar.MARCH, new Color(0,0,100));
		COLOR_MAP.put(Calendar.APRIL, new Color(50,200,100));
		COLOR_MAP.put(Calendar.MAY, new Color(0,255,0));
		COLOR_MAP.put(Calendar.JUNE, new Color(255,255,25));
		COLOR_MAP.put(Calendar.JULY, new Color(225,175,0));
		COLOR_MAP.put(Calendar.AUGUST, new Color(0,175,225));
		COLOR_MAP.put(Calendar.SEPTEMBER, new Color(0,150,50));
		COLOR_MAP.put(Calendar.OCTOBER, new Color(255,60,0));
		COLOR_MAP.put(Calendar.NOVEMBER, new Color(100,60,20));
		COLOR_MAP.put(Calendar.DECEMBER, new Color(60,20,60));
		
		for (int size = FONT_MIN; size <= FONT_MAX; size += FONT_INC) {
			FONT_MAP.put(size, new Font("Sans-serif", Font.BOLD, size));
		}
	}
	
	private static final int TODAY_WIDTH = 16;
	private static final int ROLL_OVER_TOLERANCE = 16;
	
	private static final int BAR_STROKE_WIDTH = 2;
	private static final int CHART_STROKE_WIDTH = 7;
	private static final int MONTH_STROKE_WIDTH = 3;
	private static final int DAY_STROKE_WIDTH = 1;
	
	private static final Stroke BAR_STROKE = new BasicStroke(BAR_STROKE_WIDTH,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	private static final Stroke CHART_STROKE = new BasicStroke(CHART_STROKE_WIDTH,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND);
	private static final Stroke MONTH_STROKE = new BasicStroke(MONTH_STROKE_WIDTH,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	private static final Stroke DAY_STROKE = new BasicStroke(DAY_STROKE_WIDTH,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	
	private static final Color GRID_COLOR = new Color(255,255,255,25);
	private static final Color MONTH_COLOR = new Color(35,35,35,255);
	private static final Color DAY_TEXT_COLOR = new Color(255,255,255,75);
	
	private static final int POINT_CAP = 200;
	private static final int POINT_BAR_WIDTH = 32;
	private static final int CALENDAR_BAR_HEIGHT = 32;
	
	private final ArrayList<MonthRibbon> ribbonList = new ArrayList<MonthRibbon>();
	private final ArrayList<EventPoint> eventPointList = new ArrayList<EventPoint>();
	
	private DefaultBoundedRangeModel calendarModel;
	private DefaultBoundedRangeModel pointModel;
	private boolean pointGrid;
	private boolean calendarGrid;
	private Calendar rollOverCalendar;
	private EventPoint rollOverEventPoint;
	private int configuredWidth;
	private int configuredHeight;
	
	private ArrayList<PepBandEvent> events;
	private Season season;
	private Member member;
	
	private DataListener dataListener;
	
	public HistoryChart(Member paramMember) {
		if (paramMember != null) {
			member = paramMember;
		} else {
			throw new NullPointerException("HISTORY CHART CANNOT HAVE NULL MEMBER");
		}
		initialize();
	}
	
	private void centerOnX(int clickX) {
		int oldModelValue = calendarModel.getValue();
		int daysFromXToSeasonStart = (int) Math.floor(getDateDifference(getCalendarStart(), convertXToCalendar(clickX)));
		calendarModel.setValue(daysFromXToSeasonStart - calendarModel.getExtent() / 2);
		if (oldModelValue != calendarModel.getValue()) {
			recomputeAll();
			repaint();
		}
	}
	
	private double convertCalendarToX(Calendar calendar) {
		int pixelLeft = POINT_BAR_WIDTH;
		double dayWidth = getDateDifference(getCalendarStart(), calendar) - calendarModel.getValue();
		if (dayWidth < 0) {
			dayWidth = 0;
		} else if (dayWidth > calendarModel.getExtent()) {
			dayWidth = calendarModel.getExtent();
		}
		return pixelLeft + convertDaysToWidth(dayWidth);
	}
	
	private double convertDateToX(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return convertCalendarToX(calendar);
	}
	
	private double convertDaysToWidth(double dayWidth) {
		int pixelWidth = getWidth() - POINT_BAR_WIDTH;
		double dayPercentage = 1.0 * dayWidth / calendarModel.getExtent();
		return dayPercentage * pixelWidth;
	}
	
	private double convertHeightToPoints(int pixelHeight) {
		return 1.0 * pointModel.getExtent() * pixelHeight / (getHeight() - CALENDAR_BAR_HEIGHT);
	}
	
	private double convertPointValueToY(int pointValue) {
		int pixelBottom = getHeight() - CALENDAR_BAR_HEIGHT;
		int pointHeight = pointValue - pointModel.getValue();
		if (pointHeight < 0) {
			pointHeight = 0;
		} else if (pointHeight > pointModel.getExtent()) {
			pointHeight = pointModel.getExtent();
		}
		return pixelBottom - convertPointsToHeight(pointHeight);
	}
	
	private double convertPointsToHeight(int pointHeight) {
		int pixelHeight = getHeight() - CALENDAR_BAR_HEIGHT;
		double pointPercentage = 1.0 * pointHeight / pointModel.getExtent();
		return pointPercentage * pixelHeight;
	}
	
	private double convertWidthToDays(int pixelWidth) {
		return 1.0 * calendarModel.getExtent() * pixelWidth / (getWidth() - POINT_BAR_WIDTH);
	}
	
	private Calendar convertXToCalendar(int x) {
		int calendarX = x - POINT_BAR_WIDTH;
		double percentOfWidth = 1.0 * calendarX / (getWidth() - POINT_BAR_WIDTH);
		double daysPastStart = percentOfWidth * calendarModel.getExtent();
		Calendar calendarAtX = getCalendarStart();
		calendarAtX.add(Calendar.DAY_OF_YEAR, calendarModel.getValue());
		calendarAtX.add(Calendar.DAY_OF_YEAR, (int) Math.round(daysPastStart));
		return calendarAtX;
	}
	
	private double convertYToPointValue(int y) {
		double percentOfHeight = 1.0 - y / (getHeight() - CALENDAR_BAR_HEIGHT);
		double pointsPastStart = percentOfHeight * pointModel.getExtent();
		return pointModel.getValue() + pointsPastStart;
	}
	
	public Calendar getCalendarEnd() {
		Calendar end = getCalendarStart();
		end.add(Calendar.DAY_OF_YEAR, calendarModel.getMaximum());
		return end;
	}
	
	public Calendar getCalendarLeft() {
		Calendar left = getCalendarStart();
		left.add(Calendar.DAY_OF_YEAR, calendarModel.getValue());
		return left;
	}
	
	public int getCalendarInterval() {
		return (int) Math.max(1, Math.ceil(calendarModel.getExtent() / 10));
	}
	
	public Calendar getCalendarRight() {
		Calendar right = getCalendarStart();
		right.add(Calendar.DAY_OF_YEAR, calendarModel.getValue() + calendarModel.getExtent());
		return right;
	}
	
	public Calendar getCalendarStart() {
		Calendar start = Calendar.getInstance();
		start.setTime(season.getStartingDate());
		setCalendarToMidnight(start);
		return start;
	}
	
	public double getDateDifference(Date first, Date second) {
		Calendar firstCalendar = Calendar.getInstance();
		firstCalendar.setTime(first);
		Calendar secondCalendar = Calendar.getInstance();
		secondCalendar.setTime(second);
		return getDateDifference(firstCalendar, secondCalendar);
	}
	
	public double getDateDifference(Calendar first, Calendar second) {
		Calendar firstCalendar = Calendar.getInstance();
		firstCalendar.setTime(first.getTime());
		Calendar secondCalendar = Calendar.getInstance();
		secondCalendar.setTime(second.getTime());
		firstCalendar.set(Calendar.DST_OFFSET, 0);
		secondCalendar.set(Calendar.DST_OFFSET, 0);
		
		long milliDifference = secondCalendar.getTimeInMillis() - firstCalendar.getTimeInMillis();
		long dayDifference = TimeUnit.MILLISECONDS.toDays(milliDifference);
		long milliLeftOver = milliDifference - TimeUnit.DAYS.toMillis(dayDifference);
		long milliInDay = TimeUnit.DAYS.toMillis(1);
		return dayDifference + 1.0 * milliLeftOver / milliInDay;
	}
	
	public int getFontSizeForHeight(String string, Graphics2D g, int heightLimit) {
		int fontSize = FONT_MAX + FONT_INC;
		int stringHeight = 2 * heightLimit;
		while (stringHeight > heightLimit && fontSize >= FONT_MIN) {
			fontSize -= FONT_INC;
			g.setFont(FONT_MAP.get(fontSize));
			Rectangle2D stringSize = g.getFontMetrics().getStringBounds(string, g);
			stringHeight = (int) stringSize.getHeight();
		}
		return fontSize;
	}
	
	public int getFontSizeForWidth(String string, Graphics2D g, int widthLimit) {
		int fontSize = FONT_MAX + FONT_INC;
		int stringWidth = 2 * widthLimit;
		while (stringWidth > widthLimit && fontSize >= FONT_MIN) {
			fontSize -= FONT_INC;
			g.setFont(FONT_MAP.get(fontSize));
			Rectangle2D stringSize = g.getFontMetrics().getStringBounds(string, g);
			stringWidth = (int) stringSize.getWidth();
		}
		return fontSize;
	}
	
	private Graphics2D getGraphics2D(Graphics gfx) {
		Graphics2D g = (Graphics2D)gfx;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING ,RenderingHints.VALUE_ANTIALIAS_ON );
		g.setRenderingHint(RenderingHints.KEY_RENDERING  ,RenderingHints.VALUE_RENDER_QUALITY );
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL  ,RenderingHints.VALUE_STROKE_NORMALIZE );
		return g;
	}
	
	public int getPointInterval() {
		if (pointModel.getExtent() <= 10) {
			return 1;
		} else if (pointModel.getExtent() <= 50) {
			return 5;
		} else if (pointModel.getExtent() <= 100) {
			return 10;
		} else if (pointModel.getExtent() <= 150) {
			return 15;
		} else if (pointModel.getExtent() <= 200) {
			return 20;
		} else if (pointModel.getExtent() <= POINT_CAP) {
			return 25;
		} else {
			return POINT_CAP;
		}
	}
	
	private void initialize() {
		events = new ArrayList<PepBandEvent>();
		pointModel = new DefaultBoundedRangeModel(0, POINT_CAP, 0, POINT_CAP);
		calendarModel = new DefaultBoundedRangeModel(0, 365, 0, 365);
		pointGrid = true;
		calendarGrid = true;
		rollOverCalendar = null;
		rollOverEventPoint = null;
		configuredWidth = -1;
		configuredHeight = -1;
		
		setCursor(Tools.getCursor("handgrab"));
		setOpaque(true);
		
		dataListener = new DataListener() {
			public void eventOccured(SourceEvent sourceEvent) {
				if (sourceEvent.isField()) {
					SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					if (fieldEvent.getField() == DataField.STARTING_DATE && fieldEvent.getOwner().equals(season)) {
						updateBounds(false);
					} else if (fieldEvent.getOwner() instanceof PepBandEvent && ((PepBandEvent) fieldEvent.getOwner()).getMembers().contains(member)) {
						if (fieldEvent.getField() == DataField.NAME) {
							repaint();
						} else if (fieldEvent.getField() == DataField.EVENT_TYPE) {
							for (EventPoint eventPoint : eventPointList) {
								eventPoint.updateIcons();
							}
							repaint();
						} else if (fieldEvent.getField() == DataField.POINT_VALUE) {
							updateBounds(false);
						} else if (fieldEvent.getField() == DataField.DATE) {
							Collections.sort(events);
							updateBounds(false);
						}
					} else if (fieldEvent.getField() == DataField.ICON_NAME && fieldEvent.getOwner() instanceof EventType) {
						for (EventPoint eventPoint : eventPointList) {
							eventPoint.updateIcons();
						}
						repaint();
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (listEvent.getOwner() instanceof PepBandEvent && (listEvent.getType() == SourceEvent.List.ADD || listEvent.getType() == SourceEvent.List.REMOVE) && listEvent.containsElement(member)) {
						events = member.getEvents(season);
						updateBounds(false);
					} else if (listEvent.getOwner().equals(season) && listEvent.getElement() instanceof PepBandEvent && listEvent.getType() == SourceEvent.List.REMOVE && !Collections.disjoint(events, listEvent.getElementsAsList())) {
						events = member.getEvents(season);
						updateBounds(false);
					}
				}
			}
		};
		DataManager.getDataManager().addDataListener(dataListener);
		final HistoryChart historyChart = this;
		MouseAdapter mouseAdapter = new MouseAdapter() {
			int pressX = 0;
			int pressY = 0;
			int pressCalendarValue = 0;
			int pressPointValue = 0;
			
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
					if (rollOverEventPoint != null) {
						Tools.getDesktopPane().addEventWindow(rollOverEventPoint.getEvent()).display();
					}
				} else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					if (rollOverEventPoint == null && e.getY() < getHeight() - CALENDAR_BAR_HEIGHT && e.getX() > POINT_BAR_WIDTH) {
						centerOnX(e.getX());
					} else if (e.getY() > getHeight() - CALENDAR_BAR_HEIGHT && e.getX() > POINT_BAR_WIDTH) {
						zoomToMonth(e.getX());
					}
				} else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON2) {
					System.out.println(historyChart.toString());
				}
			}
			
			public void mouseDragged(MouseEvent e) {
				setCursor(Tools.getCursor("handgrabbed"));
				shiftCalendar(pressCalendarValue, pressX - e.getXOnScreen());
				shiftPoints(pressPointValue, pressY - e.getYOnScreen());
			}
			
			public void mouseEntered(MouseEvent e) {
				
			}
			
			public void mouseExited(MouseEvent e) {
				setRollOverCalendar(null);
				setRollOverEventPoint(null);
			}
			
			public void mouseMoved(MouseEvent e) {
				EventPoint foundPoint = null;
				for (int index = 0; index < eventPointList.size(); index++) {
					EventPoint eventPoint = eventPointList.get(index);
					if (Math.abs(eventPoint.getEventX() - e.getX()) < ROLL_OVER_TOLERANCE && Math.abs(eventPoint.getEventYBefore() - e.getY()) < ROLL_OVER_TOLERANCE) {
						foundPoint = eventPoint;
						break;
					}
				}
				setRollOverEventPoint(foundPoint);
				if (e.getY() < getHeight() - CALENDAR_BAR_HEIGHT && e.getX() > POINT_BAR_WIDTH) {
					setRollOverCalendar(null);
				} else if (e.getY() > getHeight() - CALENDAR_BAR_HEIGHT && e.getX() > POINT_BAR_WIDTH) {
					setRollOverCalendar(convertXToCalendar(e.getX()));
				} else {
					setRollOverCalendar(null);
				}
			}
			
			public void mousePressed(MouseEvent e) {
				pressX = e.getXOnScreen();
				pressY = e.getYOnScreen();
				pressCalendarValue = calendarModel.getValue();
				pressPointValue = pointModel.getValue();
				calendarModel.setValueIsAdjusting(true);
				pointModel.setValueIsAdjusting(true);
				if (rollOverCalendar == null && rollOverEventPoint == null) {
					setCursor(Tools.getCursor("handgrabbed"));
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				calendarModel.setValueIsAdjusting(false);
				pointModel.setValueIsAdjusting(false);
				setRollOverCalendar(rollOverCalendar);
				setRollOverEventPoint(rollOverEventPoint);
			}
			
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rotation = e.getWheelRotation();
				if (e.isControlDown()) {
					zoomPoints(rotation);
					zoomCalendar(rotation);
				} else if (e.isShiftDown()) {
					zoomPoints(rotation);
				} else {
					zoomCalendar(rotation);
				}
			}
		};
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);
	}
	
	protected void paintBackground(Graphics gfx) {
		Graphics2D g = getGraphics2D(gfx);
		
		g.setColor(Color.BLACK);
		g.fillRect(0,0,getWidth(),getHeight());
		
		g.dispose();
	}
	
	protected void paintCalendarBar(Graphics gfx) {
		Graphics2D g = getGraphics2D(gfx);
		
		if (season == null) {
			
		} else {
			// INITIALIZE
			Calendar current = Calendar.getInstance();
			
			// DETERMINE FONT SIZE OF MONTH RIBBON TEXT
			int monthFontSize = FONT_MAX;
			if (ribbonList.size() == 0) {
				monthFontSize = FONT_MAX;
			} else if (ribbonList.size() == 1) {
				current.set(Calendar.MONTH,ribbonList.get(0).getMonth());
				String monthString = MONTH_FORMAT.format(current.getTime());
				monthFontSize = getFontSizeForWidth(monthString, g, (int) ribbonList.get(0).getRibbonWidth());
			} else if (ribbonList.size() == 2) {
				current.set(Calendar.MONTH,ribbonList.get(0).getMonth());
				String monthString1 = MONTH_FORMAT.format(current.getTime());
				int size1 = getFontSizeForWidth(monthString1, g, (int) ribbonList.get(0).getRibbonWidth());
				current.set(Calendar.MONTH,ribbonList.get(1).getMonth());
				String monthString2 = MONTH_FORMAT.format(current.getTime());
				int size2 = getFontSizeForWidth(monthString2, g, (int) ribbonList.get(1).getRibbonWidth());
				monthFontSize = Math.max(size1, size2);
			} else {
				for (int index = 0; index < ribbonList.size(); index++) {
					if (ribbonList.get(index).getDaysInMonth() == ribbonList.get(index).getDaysShown()) {
						current.set(Calendar.MONTH,ribbonList.get(index).getMonth());
						String monthString = MONTH_FORMAT.format(current.getTime());
						int requiredFontSize = getFontSizeForWidth(monthString, g, (int) ribbonList.get(1).getRibbonWidth() - BAR_STROKE_WIDTH);
						if (requiredFontSize < monthFontSize) {
							monthFontSize = requiredFontSize;
						}
					}
				}
			}
			
			// DO PAINTING
			for (int index = 0; index < ribbonList.size(); index++) {
				MonthRibbon monthRibbon = ribbonList.get(index);
				current.set(Calendar.MONTH, monthRibbon.getMonth());
				
				// COORDINATES AND SIZES
				Rectangle2D.Double ribbonRectangle = new Rectangle2D.Double(monthRibbon.getRibbonX(), getHeight() - CALENDAR_BAR_HEIGHT, monthRibbon.getRibbonWidth(), CALENDAR_BAR_HEIGHT);
				Point2D.Double ribbonNW = new Point2D.Double(ribbonRectangle.getX(), ribbonRectangle.getY());
				Point2D.Double ribbonSW = new Point2D.Double(ribbonRectangle.getX(), ribbonRectangle.getMaxY());
				
				// RIBBON AND BORDER
				g.setStroke(BAR_STROKE);
				if (rollOverCalendar != null && monthRibbon.getRibbonCalendar().get(Calendar.MONTH) == rollOverCalendar.get(Calendar.MONTH) && monthRibbon.getRibbonCalendar().get(Calendar.YEAR) == rollOverCalendar.get(Calendar.YEAR)) {
					g.setPaint(new GradientPaint(ribbonNW, COLOR_MAP.get(monthRibbon.getMonth()), ribbonSW, COLOR_MAP.get(monthRibbon.getMonth()).brighter().brighter()));
				} else {
					g.setPaint(new GradientPaint(ribbonNW, COLOR_MAP.get(monthRibbon.getMonth()).darker(), ribbonSW, COLOR_MAP.get(monthRibbon.getMonth()).brighter()));
				}
				g.fill(ribbonRectangle);
				g.setColor(Color.BLACK);
				g.draw(ribbonRectangle);
				
				// RIBBON MONTH TEXT
				g.setColor(Color.BLACK);
				g.setFont(FONT_MAP.get(monthFontSize));
				String monthString = MONTH_FORMAT.format(current.getTime());
				Rectangle2D stringSize = g.getFontMetrics().getStringBounds(monthString, g);
				int stringWidth = (int) stringSize.getWidth();
				int stringHeight = (int) stringSize.getHeight();
				if (stringWidth <= ribbonRectangle.getWidth()) {
					g.drawString(monthString, (float) (ribbonNW.getX() + (ribbonRectangle.getWidth() - stringWidth) / 2), (float) (getHeight() - (ribbonRectangle.getHeight() - stringHeight) / 2));
				}
			}
		}
		
		g.dispose();
	}
	
	protected void paintChart(Graphics gfx) {
		Graphics2D g = getGraphics2D(gfx);
		
		if (season == null) {
			
		} else if (events.isEmpty()) {
			
		} else {
			// INITIALIZE
			Path2D.Double path = new Path2D.Double();
			
			Calendar left = getCalendarLeft();
			Calendar right = getCalendarRight();
			Calendar today = Calendar.getInstance();
			
			// DETERMINE STARTING PATH POSITION
			double yTracker = convertPointValueToY(member.getPointsBefore(season, left.getTime()));
			if (today.equals(left) || today.before(left)) {
				// Don't paint a path
			} else {
				path.moveTo(POINT_BAR_WIDTH, yTracker);
			}
			
			// ENTER PATH AND PAINT PATH
			for (int index = 0; index < eventPointList.size(); index++) {
				EventPoint eventPoint = eventPointList.get(index);
				if (eventPoint.getEvent().getDate().before(today.getTime())) {
					path.lineTo(eventPoint.getEventX(), eventPoint.getEventYBefore());
					path.lineTo(eventPoint.getEventX(), yTracker = eventPoint.getEventYAfter());
				}
			}
			if (today.equals(left) || today.before(left)) {
				// Don't paint a path
			} else if (today.after(left) && today.before(right)) {
				path.lineTo(POINT_BAR_WIDTH + convertDaysToWidth(getDateDifference(left, today)), yTracker);
			} else if (today.after(right) || today.equals(right)) {
				path.lineTo(POINT_BAR_WIDTH + convertDaysToWidth(calendarModel.getExtent()), yTracker);
			}
			g.setStroke(CHART_STROKE);
			g.setColor(Color.RED);
			g.draw(path);
			
			// PAINT EVENT ICONS
			for (int index = 0; index < eventPointList.size(); index++) {
				EventPoint eventPoint = eventPointList.get(index);
				if (eventPoint != rollOverEventPoint) {
					ImageIcon eventIcon = eventPoint.getIcon();
					eventIcon.paintIcon(this, g, (int) eventPoint.getEventX() - eventIcon.getIconWidth() / 2, (int) eventPoint.getEventYBefore() - eventIcon.getIconHeight() / 2);
				}
			}
			
			// PAINT ROLL OVER EVENT ICON AND TEXT
			if (rollOverEventPoint != null) {
				ImageIcon eventIcon = rollOverEventPoint.getBigIcon();
				eventIcon.paintIcon(this, g, (int) rollOverEventPoint.getEventX() - eventIcon.getIconWidth() / 2, (int) rollOverEventPoint.getEventYBefore() - eventIcon.getIconHeight() / 2);
				g.setFont(FONT_MAP.get(12));
				g.setColor(Color.WHITE);
				String eventString = rollOverEventPoint.getEvent().getName();
				Rectangle2D stringSize = g.getFontMetrics().getStringBounds(eventString, g);
				int stringWidth = (int) stringSize.getWidth();
				int stringHeight = (int) stringSize.getHeight();
				int xDraw = (int) rollOverEventPoint.getEventX() + eventIcon.getIconWidth() / 2;
				int yDraw = (int) rollOverEventPoint.getEventYBefore() - eventIcon.getIconHeight() / 2;
				if (xDraw + stringWidth > getWidth()) {
					xDraw = (int) rollOverEventPoint.getEventX() - eventIcon.getIconWidth() / 2 - stringWidth;
				}
				if (yDraw - stringHeight < 0) {
					yDraw = (int) rollOverEventPoint.getEventYBefore() + eventIcon.getIconHeight() / 2 + stringHeight;
				}
				g.drawString(eventString, xDraw, yDraw);
			}
		}
		
		g.dispose();
	}
	
	protected void paintComponent(Graphics gfx) {
		Graphics2D g = getGraphics2D(gfx);
		
		if (configuredWidth != getWidth() || configuredHeight != getHeight()) {
			recomputeAll();
			configuredWidth = getWidth();
			configuredHeight = getHeight();
		}
		paintBackground(g.create());
		paintCalendarGrid(g.create());
		paintPointBar(g.create());
		paintCalendarBar(g.create());
		paintChart(g.create());
		
		g.dispose();
	}
	
	protected void paintCalendarGrid(Graphics gfx) {
		Graphics2D g = getGraphics2D(gfx);
		
		if (calendarGrid && season != null) {
			// DETERMINE WHETHER OR NOT TO SHOW DAYS AND WEEKS
			boolean showWeeks = false;
			boolean showDays = false;
			if (calendarModel.getExtent() < 6 * 30) {
				showWeeks = true;
			}
			if (calendarModel.getExtent() < 3 * 30) {
				showDays = true;
			}
			
			// INITIALIZE CALENDARS
			Calendar right = getCalendarRight();
			Calendar current = getCalendarLeft();
			Calendar today = Calendar.getInstance();
			
			// PAINT MONTH LINES
			while (current.before(right)) {
				if (current.get(Calendar.DAY_OF_MONTH) == 1) {
					double dateX = convertCalendarToX(current);
					Line2D.Double gridLine = new Line2D.Double(dateX, 0.0, dateX, getHeight() - CALENDAR_BAR_HEIGHT);
					g.setStroke(MONTH_STROKE);
					g.setColor(MONTH_COLOR);
					g.draw(gridLine);
				}
				current.add(Calendar.MONTH, 1);
				current.set(Calendar.DAY_OF_MONTH, 1);
			}
			
			current = getCalendarLeft();
			
			// PAINT DAY AND WEEK LINES
			if (showDays || showWeeks) {
				g.setFont(FONT_MAP.get(10));
				while (current.before(right)) {
					double dateX = convertCalendarToX(current);
					Line2D.Double gridLine = new Line2D.Double(dateX, 0.0, dateX, getHeight() - CALENDAR_BAR_HEIGHT);
					if (showWeeks && current.get(Calendar.DAY_OF_WEEK) == 1) {
						g.setStroke(DAY_STROKE);
						g.setColor(GRID_COLOR);
						g.draw(gridLine);
					}
					if (showDays) {
						g.setStroke(DAY_STROKE);
						g.setColor(GRID_COLOR);
						g.draw(gridLine);
						if (calendarModel.getExtent() <= 31) {
							String dayString = current.get(Calendar.DAY_OF_MONTH) + "";
							Rectangle2D stringSize = g.getFontMetrics().getStringBounds(dayString, g);
							float stringHeight = (float) stringSize.getHeight();
							g.setColor(DAY_TEXT_COLOR);
							g.drawString(dayString, (float) dateX + DAY_STROKE_WIDTH, stringHeight);
						}
					}
					if (showDays) {
						current.add(Calendar.DAY_OF_YEAR, 1);
					} else if (showWeeks) {
						current.add(Calendar.WEEK_OF_MONTH, 1);
						current.set(Calendar.DAY_OF_WEEK, current.getActualMinimum(Calendar.DAY_OF_WEEK));
					}
				}
			}
			
			current = getCalendarLeft();
			
			// DRAW TODAY
			if (today.after(current) && today.before(right)) {
				double dateX = convertCalendarToX(today);
				Rectangle2D.Double todayWave = new Rectangle2D.Double(dateX - TODAY_WIDTH, 0.0, TODAY_WIDTH, getHeight() - CALENDAR_BAR_HEIGHT);
				Point2D.Double todayNW = new Point2D.Double(todayWave.getX(), todayWave.getY());
				Point2D.Double todayNE = new Point2D.Double(todayWave.getMaxX(), todayWave.getY());
				g.setPaint(new GradientPaint(todayNW, new Color(200,0,0,0), todayNE, new Color(200,200,200,175)));
				g.fill(todayWave);
			}
		}
	}
	
	protected void paintPointBar(Graphics gfx) {
		Graphics2D g = getGraphics2D(gfx);
		
		// DRAW POINT BAR BACKDROP
		Polygon polygon = new Polygon();
		polygon.addPoint(0,0);
		polygon.addPoint(POINT_BAR_WIDTH,0);
		polygon.addPoint(POINT_BAR_WIDTH / 2, getHeight() - CALENDAR_BAR_HEIGHT);
		g.setPaint(new GradientPaint(0, 0, new Color(180, 0, 0), 0, getHeight() - CALENDAR_BAR_HEIGHT, Color.BLACK));
		g.fill(polygon);
		
		// DETERMINE INTERVAL AND FONT SIZE
		int interval = getPointInterval();
		double intervalHeight = convertPointsToHeight(interval);
		int fontSize = getFontSizeForHeight("HTB1", g, (int) Math.floor(intervalHeight));
		
		for (int pointVal = pointModel.getValue(); pointVal < pointModel.getValue() + pointModel.getExtent(); pointVal += interval) {
			double pointY = convertPointValueToY(pointVal);
			Line2D.Double trackLine = new Line2D.Double(0.0, pointY, POINT_BAR_WIDTH, pointY);
			
			// PAINT POINT TRACK MARK
			float[] fractions = {0f, 0.5f, 1.0f};
			Color[] colors = {Color.WHITE, Color.BLACK, Color.WHITE};
			g.setPaint(new LinearGradientPaint(trackLine.getP1(), trackLine.getP2(), fractions, colors));
			g.setStroke(BAR_STROKE);
			g.draw(trackLine);
			
			// PAINT POINT GRID
			if (pointGrid) {
				Line2D.Double gridLine = new Line2D.Double(POINT_BAR_WIDTH, pointY, getWidth(), pointY);
				g.setColor(GRID_COLOR);
				g.setStroke(DAY_STROKE);
				g.draw(gridLine);	
			}
			
			// PAINT POINT VALUE
			g.setFont(FONT_MAP.get(fontSize));
			Rectangle2D stringSize = g.getFontMetrics().getStringBounds(Integer.toString(pointVal), g);
			double stringWidth = stringSize.getWidth();
			double stringHeight = stringSize.getHeight();
			g.setColor(new Color(255,255,255,180));
			g.drawString(Integer.toString(pointVal), (float) ((POINT_BAR_WIDTH - stringWidth) / 2), (float) (pointY - 0.5 * BAR_STROKE_WIDTH - 0.125 * intervalHeight));
		}
		g.dispose();
	}
	
	public ArrayList<EventPoint> procureEventPointList() {
		eventPointList.clear();
		if (season != null && !events.isEmpty()) {
			Calendar left = getCalendarLeft();
			Calendar right = getCalendarRight();
			
			for (int index = 0; index < events.size(); index++) {
				PepBandEvent event = events.get(index);
				if (event.getDate().after(left.getTime()) && event.getDate().before(right.getTime())) {
					eventPointList.add(new EventPoint(event));
				}
			}
		}
		
		return eventPointList;
	}
	
	public ArrayList<MonthRibbon> procureRibbonList() {
		ribbonList.clear();
		if (season != null) {
			Calendar right = getCalendarRight();
			Calendar current = getCalendarLeft();
			
			while (current.before(right)) {
				ribbonList.add(new MonthRibbon(current));
				current.add(Calendar.MONTH, 1);
				current.set(Calendar.DAY_OF_MONTH, 1);
			}
		}
		return ribbonList;
	}
	
	public void recomputeAll() {
		procureRibbonList();
		procureEventPointList();
	}
	
	private void setCalendarToMidnight(Calendar value) {
		value.set(Calendar.HOUR_OF_DAY, value.getActualMinimum(Calendar.HOUR_OF_DAY));
		value.set(Calendar.MINUTE, value.getActualMinimum(Calendar.MINUTE));
		value.set(Calendar.SECOND, value.getActualMinimum(Calendar.SECOND));
		value.set(Calendar.MILLISECOND, value.getActualMinimum(Calendar.MILLISECOND));
	}
	
	private void setRollOverCalendar(Calendar value) {
		Calendar oldValue = rollOverCalendar;
		if (value == null && rollOverEventPoint == null) {
			setCursor(Tools.getCursor("handgrab"));
		} else {
			setCursor(Tools.getCursor("hand"));
		}
		rollOverCalendar = value;
		if (oldValue == null && value != null) {
			repaint();
		} else if (oldValue != null && value == null) {
			repaint();
		} else if (oldValue == null && value == null) {
			
		} else if (oldValue.get(Calendar.MONTH) != value.get(Calendar.MONTH) || oldValue.get(Calendar.YEAR) != value.get(Calendar.YEAR)) {
			repaint();
		}
	}
	
	private void setRollOverEventPoint(EventPoint value) {
		EventPoint oldValue = rollOverEventPoint;
		if (value == null && rollOverCalendar == null) {
			setCursor(Tools.getCursor("handgrab"));
		} else {
			setCursor(Tools.getCursor("hand"));
		}
		rollOverEventPoint = value;
		if (oldValue == null && value != null) {
			repaint();
		} else if (oldValue != null && value == null) {
			repaint();
		} else if (oldValue == null && value == null) {
			
		} else if (!oldValue.equals(value)) {
			repaint();
		}
	}
	
	public void setSeason(Season value) {
		season = value;
		events.clear();
		if (season != null) {
			events.addAll(member.getEvents(season));
			updateBounds(true);
		} else {
			calendarModel.setRangeProperties(0, 0, 0, 0, false);
			recomputeAll();
			repaint();
		}
	}
	
	private void shiftCalendar(int pressValue, int dx) {
		int oldModelValue = calendarModel.getValue();
		int daysToShift = (int) Math.round(convertWidthToDays(dx));
		calendarModel.setValue(pressValue + daysToShift);
		if (oldModelValue != calendarModel.getValue()) {
			recomputeAll();
			repaint();
		}
	}
	
	private void shiftPoints(int pressValue, int dy) {
		int oldModelValue = pointModel.getValue();
		int pointsToShift = (int) Math.round(convertHeightToPoints(dy));
		pointModel.setValue(pressValue - pointsToShift);
		if (oldModelValue != pointModel.getValue()) {
			procureEventPointList();
			repaint();
		}
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("History Chart\n");
		builder.append("Calendar Model:\n");
		builder.append(calendarModel.toString());
		builder.append("Month Ribbons:\n");
		for (MonthRibbon ribbon : ribbonList) {
			builder.append(ribbon.toString());
		}
		builder.append("End History Chart\n");
		return builder.toString();
	}
	
	public void uninstall() {
		DataManager.getDataManager().removeDataListener(dataListener);
	}
	
	public void updateBounds(boolean reset) {
		int maxExtent = 365;
		if (!events.isEmpty()) {
			int dateDifference = (int) Math.ceil(getDateDifference(getCalendarLeft().getTime(), events.get(events.size() - 1).getDate()));
			maxExtent = Math.max(maxExtent, dateDifference);
		}
		
		if (reset) {
			calendarModel.setRangeProperties(0, maxExtent, 0, maxExtent, false);
		} else {
			int value = Math.min(maxExtent - 1, calendarModel.getValue());
			int extent = Math.min(maxExtent, calendarModel.getExtent());
			if (value + extent > maxExtent) {
				extent = maxExtent - value;
			}
			calendarModel.setRangeProperties(value, extent, 0, maxExtent, false);
		}
		recomputeAll();
		repaint();
	}
	
	private void zoomCalendar(int rotation) {
		int oldModelValue = calendarModel.getValue();
		int oldModelExtent = calendarModel.getExtent();
		if (rotation < 0 && calendarModel.getExtent() <= 1) {
			calendarModel.setExtent(1);
		} else {
			int newExtent = Math.max(1, calendarModel.getExtent() + rotation * 2 * getCalendarInterval());
			int newLeadDay = calendarModel.getValue() - rotation * getCalendarInterval();
			
			calendarModel.setExtent(newExtent);
			calendarModel.setValue(newLeadDay);
		}
		if (oldModelValue != calendarModel.getValue() || oldModelExtent != calendarModel.getExtent()) {
			recomputeAll();
			repaint();
		}
	}
	
	private void zoomPoints(int rotation) {
		int oldModelValue = pointModel.getValue();
		int oldModelExtent = pointModel.getExtent();
		if (CENTER_POINT_ZOOM) {
			if (rotation < 0 && pointModel.getExtent() <= 1) {
				pointModel.setExtent(1);
			} else {
				int newExtent = Math.max(1, pointModel.getExtent() + rotation * 2 * getPointInterval());
				int newLowPoint = pointModel.getValue() - rotation * getPointInterval();
				
				pointModel.setExtent(newExtent);
				pointModel.setValue(newLowPoint);
			}
		} else {
			if (rotation > 0 && pointModel.getValue() + pointModel.getExtent() >= pointModel.getMaximum()) {
				int newExtent = Math.max(1, pointModel.getExtent() + rotation * 2 * getPointInterval());
				int newLowPoint = pointModel.getValue() - rotation * 2 * getPointInterval();
				
				pointModel.setValue(newLowPoint);
				pointModel.setExtent(newExtent);
			} else {
				int newExtent = Math.max(1, pointModel.getExtent() + rotation * 2 * getPointInterval());
				pointModel.setExtent(newExtent);
			}
		}
		if (oldModelValue != pointModel.getValue() || oldModelExtent != pointModel.getExtent()) {
			procureEventPointList();
			repaint();
		}
	}
	
	private void zoomToMonth(int clickX) {
		int oldModelValue = calendarModel.getValue();
		int oldModelExtent = calendarModel.getExtent();
		Calendar monthStart = convertXToCalendar(clickX);
		monthStart.set(Calendar.DAY_OF_MONTH, monthStart.getActualMinimum(Calendar.DAY_OF_MONTH));
		setCalendarToMidnight(monthStart);
		calendarModel.setExtent(1);
		calendarModel.setValue((int) Math.floor(getDateDifference(getCalendarStart(), monthStart)));
		calendarModel.setExtent(monthStart.getActualMaximum(Calendar.DAY_OF_MONTH));
		if (oldModelValue != calendarModel.getValue() || oldModelExtent != calendarModel.getExtent()) {
			recomputeAll();
			repaint();
		}
	}
	
	private class EventPoint {
		
		private PepBandEvent pointEvent;
		private ImageIcon icon;
		private ImageIcon bigIcon;
		
		private Integer pointsBeforeEvent;
		private Integer pointsAfterEvent;
		
		private Double eventYBefore;
		private Double eventYAfter;
		private Double eventX;
		
		public EventPoint(PepBandEvent event) {
			pointEvent = event;
			icon = Tools.getEventIcon(event.getEventType().getIconName() + "16");
			bigIcon = Tools.getEventIcon(event.getEventType().getIconName() + "32");
			pointsBeforeEvent = member.getPointsBefore(season, event.getDate());
			pointsAfterEvent = event.getDate().before(Calendar.getInstance().getTime()) ? pointsBeforeEvent + event.getPointValue() : pointsBeforeEvent;
			eventYBefore = convertPointValueToY(pointsBeforeEvent);
			eventYAfter = convertPointValueToY(pointsAfterEvent);
			eventX = convertDateToX(event.getDate());
		}
		
		public boolean equals(Object other) {
			return other instanceof EventPoint && ((EventPoint)other).getEvent().equals(pointEvent);
		}
		
		public ImageIcon getBigIcon() {
			return bigIcon;
		}
		
		public PepBandEvent getEvent() {
			return pointEvent;
		}
		
		public double getEventX() {
			return eventX;
		}
		
		public double getEventYAfter() {
			return eventYAfter;
		}
		
		public double getEventYBefore() {
			return eventYBefore;
		}
		
		public ImageIcon getIcon() {
			return icon;
		}
		
		public int getPointsAfterEvent() {
			return pointsAfterEvent;
		}
		
		public int getPointsBeforeEvent() {
			return pointsBeforeEvent;
		}
		
		public void updateIcons() {
			icon = Tools.getEventIcon(pointEvent.getEventType().getIconName() + "16");
			bigIcon = Tools.getEventIcon(pointEvent.getEventType().getIconName() + "32");
		}
		
	}
	
	private class MonthRibbon {
		
		private Calendar ribbonCalendar;
		
		private Integer month;
		private Integer daysInMonth;
		private Integer daysShown;
		private Integer firstDayShown;
		
		private Double ribbonWidth;
		private Double ribbonX;
		
		public MonthRibbon(Calendar current) {
			Calendar startChart = getCalendarLeft();
			Calendar endChart = getCalendarRight();
			
			Calendar endMonth = Calendar.getInstance();
			endMonth.setTime(current.getTime());
			endMonth.set(Calendar.DAY_OF_MONTH, endMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
			setCalendarToMidnight(endMonth);
			
			Calendar beginNextMonth = Calendar.getInstance();
			beginNextMonth.setTime(current.getTime());
			beginNextMonth.add(Calendar.MONTH, 1);
			beginNextMonth.set(Calendar.DAY_OF_MONTH, beginNextMonth.getActualMinimum(Calendar.DAY_OF_MONTH));
			setCalendarToMidnight(beginNextMonth);
			
			ribbonCalendar = Calendar.getInstance();
			ribbonCalendar.setTime(current.getTime());
			
			month = current.get(Calendar.MONTH);
			daysInMonth = current.getActualMaximum(Calendar.DAY_OF_MONTH);
			firstDayShown = current.get(Calendar.DAY_OF_MONTH);
			
			if (endMonth.after(endChart)) {
				daysShown = (int) Math.floor(getDateDifference(current, endChart));
			} else if (endMonth.equals(endChart)) {
				daysShown = (int) Math.floor(getDateDifference(current, endMonth));
			} else {
				daysShown = (int) Math.floor(getDateDifference(current, beginNextMonth));
			}
			
			ribbonWidth = convertDaysToWidth(daysShown);
			ribbonX = convertCalendarToX(current);
		}
		
		public int getDaysInMonth() {
			return daysInMonth;
		}
		
		public int getDaysShown() {
			return daysShown;
		}
		
		public int getFirstDayShown() {
			return firstDayShown;
		}
		
		public int getMonth() {
			return month;
		}
		
		public Calendar getRibbonCalendar() {
			return ribbonCalendar;
		}
		
		public double getRibbonWidth() {
			return ribbonWidth;
		}
		
		public double getRibbonX() {
			return ribbonX;
		}
		
		public String toString() {
			DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
			StringBuilder builder = new StringBuilder();
			builder.append("| Month Ribbon |" + "\n");
			builder.append("Calendar: " + dateFormat.format(ribbonCalendar.getTime()) + "\n");
			builder.append("Month: " + month + "\n");
			builder.append("# Days in Month: " + daysInMonth + "\n");
			builder.append("# Days Shown: " + daysShown + "\n");
			builder.append("First Day Shown: " + firstDayShown + "\n");
			builder.append("Ribbon Width: " + ribbonWidth + "\n");
			builder.append("Ribbon X: " + ribbonX + "\n\n");
			
			return builder.toString();
		}
	}
}