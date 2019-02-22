package pepband3;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

import pepband3.data.*;
import pepband3.gui.*;

public class BackgroundManager {
	
	private static final int TIMEOUT = 5;
	private static final String KILL_STRING = "KILL_TIME_HAS_COME";
	private static final String DATE_EXPRESSION = "EEEEE, MMMMM d, yyyy GGGGG, h:mm:ss aaaaa zzzz";
	private static final File OUTPUT_FILE = new File("Pep Band Log.txt");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_EXPRESSION);
	
	private boolean outRunning;
	private boolean errRunning;
	
	private PipedOutputStream sysOutSource;
	private PipedOutputStream sysErrSource;
	private PipedInputStream sysOutSink;
	private PipedInputStream sysErrSink;
	private BufferedReader sysOutReader;
	private BufferedReader sysErrReader;
	private PrintStream sysOut;
	private PrintStream sysErr;
	private PrintStream fileOut;
	
	private Runnable shutdownRunnable;
	private Runnable saveRunnable;
	private Runnable streamOutRunnable;
	private Runnable streamErrRunnable;
	private Thread shutdownThread;
	private ThreadFactory saveThreadFactory;
	private ThreadFactory streamThreadFactory;
	private ScheduledExecutorService saveService;
	private ExecutorService streamService;
	private ScheduledFuture saveTask;
	private Future streamOutTask;
	private Future streamErrTask;
	
	public BackgroundManager() {
		
		try {
			sysOut = System.out;
			sysErr = System.err;
			
			fileOut = new PrintStream(new FileOutputStream(OUTPUT_FILE, true));
			fileOut.println("\n-------------------- Pep Band Manager Suite 2.0.4 --------------------");
			fileOut.println(DATE_FORMAT.format(Calendar.getInstance().getTime()));
			
			sysOutSource = new PipedOutputStream();
			sysOutSink = new PipedInputStream();
			sysOutSink.connect(sysOutSource);
			
			sysErrSource = new PipedOutputStream();
			sysErrSink = new PipedInputStream();
			sysErrSink.connect(sysErrSource);
			
			sysOutReader = new BufferedReader(new InputStreamReader(sysOutSink));
			sysErrReader = new BufferedReader(new InputStreamReader(sysErrSink));
			
			System.setOut(new PrintStream(sysOutSource, true));
			System.setErr(new PrintStream(sysErrSource, true));
			
			shutdownRunnable = new Runnable() {
				public void run() {
					System.out.println("\nShuting Down");
					IO.setVocal(false);
					killSaveService();
					String propMessage = IO.saveProperties();
					System.out.println(propMessage == null ? "Properties saved" : propMessage);
					String dataMessage = DataManager.saveDataResources(true);
					System.out.println(dataMessage == null ? "Data saved determinately" : dataMessage);
					killStreamService();
					System.out.println("Adios");
				}
			};
			
			saveRunnable = new Runnable() {
				public void run() {
					String propMessage = IO.saveProperties();
					System.out.println(propMessage == null ? "Properties saved" : propMessage);
					String dataMessage = DataManager.saveDataResources(false);
					System.out.println(dataMessage == null ? "Data cloned and saved" : dataMessage);
					try {
						Tools.setProperty("Preferred Screen Device",Tools.getProgramRoot().getGraphicsConfiguration().getDevice().getIDstring());
						Tools.setProperty("Extended State", Integer.toString(Tools.getProgramRoot().getExtendedState()));
					} catch (Exception exc) {
						System.err.println("Could not commit screen device & extended state properties");
						exc.printStackTrace();
					}
				}
			};
			
			streamOutRunnable = new Runnable() {
				public void run() {
					try {
						while (outRunning || sysOutReader.ready()) {
							try {
								String line = sysOutReader.readLine();
								if (!line.equals(KILL_STRING)) {
									sysOut.println(line);
									fileOut.println(line);
								}
							} catch (Exception exc) { /* Oh well */ }
						}
						sysOut.flush();
						fileOut.flush();
						sysOutReader.close();
					} catch (Exception exc2) { /* Oh well */ }
					System.setOut(sysOut);
				}
			};
			
			streamErrRunnable = new Runnable() {
				public void run() {
					try {
						while (errRunning || sysErrReader.ready()) {
							try {
								String line = sysErrReader.readLine();
								if (!line.equals(KILL_STRING)) {
									sysErr.println(line);
									fileOut.println(line);
								}
							} catch (Exception exc) { /* Oh well */ }
						}
						sysErr.flush();
						fileOut.flush();
						sysErrReader.close();
					} catch (Exception exc2) { /* Oh well */ }
					System.setErr(sysErr);
				}
			};
			
			shutdownThread = new Thread(shutdownRunnable);
			shutdownThread.setName("SHUTDOWN THREAD");
			shutdownThread.setPriority(Thread.NORM_PRIORITY);
			
			saveThreadFactory = new ThreadFactory() {
				public Thread newThread(Runnable runnable) {
					Thread thread = new Thread(runnable);
					thread.setName("SAVE THREAD");
					thread.setPriority(3);
					return thread;
				}
			};
			
			streamThreadFactory = new ThreadFactory() {
				public Thread newThread(Runnable runnable) {
					Thread thread = new Thread(runnable);
					thread.setName("STREAM THREAD");
					thread.setPriority(3);
					return thread;
				}
			};
			
			saveService = Executors.newSingleThreadScheduledExecutor(saveThreadFactory);
			streamService = Executors.newFixedThreadPool(2, streamThreadFactory);
			
			installShutdownHook();
			start();
		} catch (Exception exc) {
			System.err.println("Could not construct the background manager");
			exc.printStackTrace();
			System.exit(1);
		}
	}
	
	public void installShutdownHook() {
		Runtime.getRuntime().addShutdownHook(shutdownThread);
	}
	
	public void start() {
		saveTask = saveService.scheduleAtFixedRate(saveRunnable, 1, 1, TimeUnit.MINUTES);
		
		outRunning = true;
		streamOutTask = streamService.submit(streamOutRunnable);
		errRunning = true;
		streamErrTask = streamService.submit(streamErrRunnable);
	}
	
	public void killSaveService() {
		/* End save task and allow it to peacefully finish what it's doing */
		saveTask.cancel(false);
		
		/* Shutdown the save service and its thread, waiting until it has ended */
		saveService.shutdownNow();
		try {
			saveService.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void killStreamService() {
		/* Trigger stream while loops to break casually close the reader streams */
		outRunning = false;
		System.out.println(KILL_STRING);
		errRunning = false;
		System.err.println(KILL_STRING);

		/* To be safe, cancel the tasks */
		streamOutTask.cancel(false);
		streamErrTask.cancel(false);
		
		/* Shutdown the stream service and its 2 threads, waiting until it has ended */
		streamService.shutdownNow();
		try {
			streamService.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void uninstallShutdownHook() {
		Runtime.getRuntime().removeShutdownHook(shutdownThread);
	}
}