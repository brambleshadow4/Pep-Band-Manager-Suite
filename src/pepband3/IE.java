package pepband3;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JTable;

import pepband3.gui.component.preview.EventPreviewTable;
import pepband3.gui.component.preview.HistoryPreviewTable;
import pepband3.gui.component.preview.PreviewTable;
import pepband3.gui.component.preview.RosterPreviewTable;
import pepband3.gui.component.preview.SeasonPreviewTable;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class IE {
	
	private static final Font PDF_HEADER_FONT = new Font("Calibri",Font.PLAIN,16);
	private static final Font PDF_FONT = new Font("Calibri",Font.PLAIN,12);
	
	private static PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();
	
	private static void addMetaData(EventPreviewTable previewTable, Document document) {
		document.addTitle(previewTable.getEvent().getName() + " - Event PDF Printout");
		document.addSubject("Event Properties & Attending Members");
		document.addCreator("Pep Band Manager Suite 2.0");
		document.addAuthor("The Cornell University Pep Band Manager");
		if (previewTable.getEvent().getEventType().getHasLocation()) {
			document.addKeywords("Pep Band, Event, " + previewTable.getEvent().getEventType().getName() + ", " + previewTable.getEvent().getLocation().getName());
		} else {
			document.addKeywords("Pep Band, Event, " + previewTable.getEvent().getEventType().getName());
		}
	}
	
	private static void addMetaData(HistoryPreviewTable previewTable, Document document) {
		document.addTitle(previewTable.getMember().getFullName() + " - " + previewTable.getSeason().getName() + " - History PDF Printout");
		document.addSubject("Events Member Attended");
		document.addCreator("Pep Band Manager Suite 2.0");
		document.addAuthor("The Cornell University Pep Band Manager");
		document.addKeywords("Pep Band, " + previewTable.getMember().getFullName() + ", " + previewTable.getSeason().getName());
	}
	
	private static void addMetaData(RosterPreviewTable previewTable, Document document) {
		if (previewTable.isPointsPreview()) {
			document.addTitle(previewTable.getSeason().getName() + " - Points PDF Printout");
			document.addSubject("Band Members & Points");
			document.addKeywords("Pep Band, Points, " + previewTable.getSeason().getName());
		} else if (previewTable.isRosterPreview()) {
			document.addTitle(previewTable.getSeason().getName() + " - Roster PDF Printout");
			document.addSubject("Season Roster");
			document.addKeywords("Pep Band, Roster, " + previewTable.getSeason().getName());
		}
		document.addCreator("Pep Band Manager Suite 2.0");
		document.addAuthor("The Cornell University Pep Band Manager");
	}
	
	private static void addMetaData(SeasonPreviewTable previewTable, Document document) {
		document.addTitle(previewTable.getSeason().getName() + " - Events PDF Printout");
		document.addSubject("Season Events");
		document.addCreator("Pep Band Manager Suite 2.0");
		document.addAuthor("The Cornell University Pep Band Manager");
		document.addKeywords("Pep Band, Events, " + previewTable.getSeason().getName());
	}
	
	public static String exportToPDF(PreviewTable previewTable, File exportFile) {
		String exceptionString = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(exportFile);
			try {
				int leftMargin = (int) pageFormat.getImageableX();
				int rightMargin = (int) (pageFormat.getWidth() - pageFormat.getImageableWidth() - leftMargin);
				int topMargin = (int) pageFormat.getImageableY();
				int bottomMargin = (int) (pageFormat.getHeight() - pageFormat.getImageableHeight() - topMargin);
				Document document = new Document(PageSize.getRectangle(pageFormat.getWidth() + " " + pageFormat.getHeight()), leftMargin, rightMargin, topMargin, bottomMargin);
				PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);
				
				if (previewTable instanceof EventPreviewTable) {
					addMetaData((EventPreviewTable) previewTable, document);
				} else if (previewTable instanceof RosterPreviewTable) {
					addMetaData((RosterPreviewTable) previewTable, document);
				} else if (previewTable instanceof SeasonPreviewTable) {
					addMetaData((SeasonPreviewTable) previewTable, document);
				} else if (previewTable instanceof HistoryPreviewTable) {
					addMetaData((HistoryPreviewTable) previewTable, document);
				}
				
				document.open();
				float pageWidth = document.getPageSize().getWidth() - leftMargin - rightMargin;
				float pageHeight = document.getPageSize().getHeight() - topMargin - bottomMargin;
				
				PdfContentByte content = writer.getDirectContent();
				PdfTemplate template = content.createTemplate(pageWidth, pageHeight);
				
				Graphics2D g = template.createGraphics(pageWidth, pageHeight);
				
				JTable headerTable = previewTable.getHeaderTable();
				JTable memberTable = previewTable.getMemberTable();
				boolean opacity = headerTable.isOpaque();
				headerTable.setOpaque(false);
				memberTable.setOpaque(false);
				
				headerTable.print(g);
				g.translate(0, headerTable.getHeight());
				
				int heightPrinted = 0;
				int startingY = headerTable.getHeight();
				
				while (heightPrinted < memberTable.getHeight()) {
					int heightToPrint = (int) pageHeight - startingY;
					int endRow = memberTable.rowAtPoint(new Point(0, heightPrinted + heightToPrint));
					if (endRow != -1) {
						heightToPrint = (int) memberTable.getCellRect(endRow, 0, true).getY() - heightPrinted;
					}
					
					g.setClip(0, heightPrinted, (int) pageWidth, heightToPrint);
					memberTable.print(g);
					g.dispose();
					
					content.addTemplate(template, leftMargin, topMargin);
					
					heightPrinted += heightToPrint;
					startingY = 0;
					if (heightPrinted < memberTable.getHeight()) {
						document.newPage();
						template = content.createTemplate(pageWidth, pageHeight);
						g = template.createGraphics(pageWidth, pageHeight);
						g.translate(0, -1 * heightPrinted);
					}
				}
				
				headerTable.setOpaque(opacity);
				memberTable.setOpaque(opacity);
				
				document.close();
				writer.close();
			} catch (Exception exc) {
				exceptionString = "IE failed to export " + "event" + ": unexpected exception";
				exc.printStackTrace();
			}
			fileOutputStream.close();
		} catch (FileNotFoundException exc) {
			exceptionString = "IE failed to export " + "event" + ": file could not be created, or exists and could not be written to";
			exc.printStackTrace();
		} catch (SecurityException exc) {
			exceptionString = "IE failed to export " + "event" + ": security would not allow access to file";
			exc.printStackTrace();
		} catch (Exception exc) {
			exceptionString = "IE failed to export " + "event" + ": unexpected exception";
			exc.printStackTrace();
		}
		return exceptionString;
	}
	
	public static PageFormat getPageFormat() {
		return pageFormat;
	}
	
	public static PrinterJob getPrinterJob(PreviewTable previewTable) {
		JTable headerTable = previewTable.getHeaderTable();
		JTable memberTable = previewTable.getMemberTable();
		int pageCount = (int) Math.ceil((headerTable.getHeight() + memberTable.getHeight()) / getPageFormat().getImageableHeight());
		Book printBook = new Book();
		printBook.append(new BandPrintable(headerTable, memberTable), getPageFormat(), pageCount);
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		printerJob.setPageable(printBook);
		return printerJob;
	}
	
	public static void setPageFormat(PageFormat value) {
		pageFormat = value;
	}
	
	private static class BandPrintable implements Printable {
		
		private JTable headerTable;
		private JTable memberTable;
		
		public BandPrintable(JTable headerTable, JTable memberTable) {
			this.headerTable = headerTable;
			this.memberTable = memberTable;
		}
		
		public int print(Graphics g, PageFormat pageFormat, int page) throws PrinterException {
			g.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
			
			int heightPrinted = (int) Math.max(0, page * (int) pageFormat.getImageableHeight() - headerTable.getHeight());
			int startRow = memberTable.rowAtPoint(new Point(0, heightPrinted));
			if (startRow != -1) {
				heightPrinted = (int) memberTable.getCellRect(startRow, 0, true).getY();
			}
			int startingY = 0;
			if (page == 0) {
				headerTable.print(g);
				g.translate(0, headerTable.getHeight());
				startingY = headerTable.getHeight();
			} else if (heightPrinted >= memberTable.getHeight()) {
				return NO_SUCH_PAGE;
			}
			
			int heightToPrint = (int) pageFormat.getImageableHeight() - startingY;
			int endRow = memberTable.rowAtPoint(new Point(0, heightPrinted + heightToPrint));
			if (endRow != -1) {
				heightToPrint = (int) memberTable.getCellRect(endRow, 0, true).getY() - heightPrinted;
			}
			
			g.translate(0, -1 * heightPrinted);
			g.setClip(0, heightPrinted, (int) pageFormat.getImageableWidth(), heightToPrint);
			memberTable.print(g);
			
			return PAGE_EXISTS;
		}
	}
}