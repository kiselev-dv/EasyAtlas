package ru.osm.dkiselev.atlasgenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.simple.TextDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MapPageTemplate {

	private static final double INCHES_TO_CM = 2.54;

	private static final String FRAME_NAME_ATTR_NAME = "draw:name";
	private static final String FRAME_ELEMENT_NAME = "draw:frame";
	
	public static final String MAP_FRAME = "map_frame";
	public static final String PAGE_NUMBER_FRAME = "page-number";

	public static final String TOP_PAGE_REF_FRAME = "top-page-ref";
	public static final String BOTTOM_PAGE_REF_FRAME = "bottom-page-ref";
	public static final String LEFT_PAGE_REF_FRAME = "left-page-ref";
	public static final String RIGHT_PAGE_REF_FRAME = "right-page-ref";
	
	
	private static final Logger logger = Logger.getLogger(MapPageTemplate.class
			.getName());

	private int scaleFactor;
	private int DPI = 300;
	private File templateFile;

	private double widthMM;
	private double heightMM;

	private double scaledMapWidthM;
	private double scaledMapHeightM;

	private int widthPX;
	private int heightPX;
	
	private TextDocument document = null; 
	private Map<String, Node> frameByName = null; 

	public static class SizeAttrParseError extends Exception {
		private static final long serialVersionUID = -434643189709162542L;

		public SizeAttrParseError(String string) {
			super(string);
		}
	}

	public MapPageTemplate(int scaleFactor, File templateFile) {
		super();
		this.scaleFactor = scaleFactor;
		this.templateFile = templateFile;
	}

	public void parse() throws Exception {
			
			loadFrames();
			parseAttributes();
	}

	private void parseAttributes() throws SizeAttrParseError {
		Node mapFrame = getFrameByName().get(MAP_FRAME);
		if(mapFrame == null) {
			logger.log(Level.SEVERE, "Fframe with name {0} not found in document.", MAP_FRAME);
			return;
		}
		
		String width = mapFrame.getAttributes().getNamedItem("svg:width").getTextContent();
		String height = mapFrame.getAttributes().getNamedItem("svg:height").getTextContent();
		
		widthMM = parseSize(width);
		heightMM = parseSize(height);
		
		scaledMapWidthM = widthMM * scaleFactor / 1000;
		scaledMapHeightM = heightMM * scaleFactor / 1000;
		
		widthPX = new Double(DPI / INCHES_TO_CM * widthMM / 10).intValue();  
		heightPX = new Double(DPI / INCHES_TO_CM * heightMM / 10).intValue();  

		logger.log(Level.INFO, "Map frame width: {0}mm, height: {1}mm", new Object[]{widthMM, heightMM});
		logger.log(Level.INFO, "Scale: 1/{0}", scaleFactor);
		logger.log(Level.INFO, "Map scaled width: {0}m, height: {1}m", new Object[]{scaledMapWidthM, scaledMapHeightM});
		logger.log(Level.INFO, "Map width: {0}px, height: {1}px at {2} dpi.", new Object[]{widthPX, heightPX, DPI});
	}

	private void loadFrames() throws Exception {
		document = TextDocument.loadDocument(templateFile);
		OfficeTextElement contentRoot = document.getContentRoot();
		NodeList frames = contentRoot.getElementsByTagName(FRAME_ELEMENT_NAME);
		
		frameByName = new HashMap<String, Node>();
		
		for(int i = 0; i < frames.getLength(); i++)
		{
			Node item = frames.item(i);
			String name = item.getAttributes().getNamedItem(FRAME_NAME_ATTR_NAME).getTextContent();
			
			getFrameByName().put(name, item);
		}
	}
	
	public MapPageTemplate withNewDocument() throws Exception {
		MapPageTemplate n = new MapPageTemplate(scaleFactor, templateFile);
		n.loadFrames();
		
		n.widthMM = widthMM;
		n.heightMM = heightMM;
		
		n.scaledMapWidthM = scaledMapWidthM;
		n.scaledMapHeightM = scaledMapHeightM;
		
		n.widthPX = widthPX;  
		n.heightPX = heightPX;
		
		return n;
	}

	public static double parseSize(String s) throws SizeAttrParseError {

		if (s == null) {
			throw new SizeAttrParseError("Attribute string is null");
		}

		if (s.endsWith("cm")) {
			return Double.valueOf(s.substring(0, s.length() - 2)) * 10;
		}
		if (s.endsWith("mm")) {
			return Double.valueOf(s.substring(0, s.length() - 2));
		}
		if (s.endsWith("m")) {
			return Double.valueOf(s.substring(0, s.length() - 1)) * 1000;
		}

		throw new SizeAttrParseError("Can't find dimensions of " + s);
	}

	public int getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(int scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public int getDPI() {
		return DPI;
	}

	public void setDPI(int dPI) {
		DPI = dPI;
	}

	public double getWidthMM() {
		return widthMM;
	}

	public double getHeightMM() {
		return heightMM;
	}

	public double getScaledMapWidthM() {
		return scaledMapWidthM;
	}

	public double getScaledMapHeightM() {
		return scaledMapHeightM;
	}

	public int getWidthPX() {
		return widthPX;
	}

	public int getHeightPX() {
		return heightPX;
	}

	public TextDocument getDocument() {
		return document;
	}

	public Map<String, Node> getFrameByName() {
		return frameByName;
	}

	public File getTemplateFile() {
		return templateFile;
	}
	
}
