package pepband3.gui.component;

import java.awt.*;
import java.util.concurrent.*;
import javax.swing.*;

public class LoadIcon extends JPanel {
	
	private static final int POINTS = 12;
	private Stroke liteStroke, boldStroke;
	private int currentPoint;
	private ScheduledExecutorService timer;
	private Runnable repaintRunnable;
	private ScheduledFuture futureTask;
	
	public LoadIcon() {
		liteStroke = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
		boldStroke = new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
		currentPoint = 0;
		timer = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r,"LOAD_ICON REPAINT THREAD");
				thread.setPriority(Thread.NORM_PRIORITY);
				return thread;
			}
		});
		repaintRunnable = new Runnable() {
			public void run() {
				repaint();
			}
		};
		setMinimumSize(new Dimension(32,32));
		setPreferredSize(new Dimension(16,16));
		setOpaque(false);
	}
	
	protected void paintComponent(Graphics gfx) {
		Graphics2D g = (Graphics2D)gfx;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING ,RenderingHints.VALUE_ANTIALIAS_ON );
		g.setRenderingHint(RenderingHints.KEY_RENDERING  ,RenderingHints.VALUE_RENDER_QUALITY );
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL  ,RenderingHints.VALUE_STROKE_NORMALIZE );
		
		int radius = Math.min(getWidth(),getHeight()) / 2;
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		
		for (int index = 0; index < POINTS; index++) {
			double angle =1.0 * index * 2 * Math.PI / POINTS;
			int dx = (int) (Math.cos(angle) * radius);
			int dy = (int) (Math.sin(angle) * radius);
			int dxs = (int) (Math.cos(angle) * radius / 2);
			int dys = (int) (Math.sin(angle) * radius / 2);
			
			int distanceToCurrent = (index - currentPoint < 0) ? index - currentPoint + 12 : index - currentPoint;
			int alpha = (int) (255 - 255 * distanceToCurrent / (POINTS - 1));
			g.setStroke(boldStroke);
			g.setPaint(new GradientPaint(centerX + dxs, centerY + dys, new Color(0, 0, 0, alpha), centerX + dx, centerY - dy, new Color(0, 0, 150, alpha)));
			g.drawLine(centerX + dxs, centerY - dys, centerX + dx, centerY - dy);
		}
		
		currentPoint--;
		if (currentPoint < 0) {
			currentPoint = POINTS - 1;
		}
		g.dispose();
	}
	
	public void killTimer() {
		timer.shutdown();
	}
	
	public void setVisible(boolean visible) {
		if (visible) {
			futureTask = timer.scheduleAtFixedRate(repaintRunnable,0,75,TimeUnit.MILLISECONDS);
		} else {
			if (futureTask != null) {
				futureTask.cancel(false);
			}
		}
		super.setVisible(visible);
	}
}