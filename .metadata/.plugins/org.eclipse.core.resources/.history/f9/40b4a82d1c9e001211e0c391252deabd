package ru.osm.dkiselev.atlasgenerator;

import java.awt.geom.Area;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.attribute.text.TextAnchorTypeAttribute;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.simple.TextDocument;
import org.w3c.dom.Node;

@SuppressWarnings("deprecation")
public class ODFGenerator {
	public static void main(String[] args) {
		
		String scale = args[1];
		String folder = args[0];
		
		MapPageTemplate template = new MapPageTemplate(Integer.valueOf(scale), new File(folder + "/map-page.ott"));
		
		File pagesDir = new File(folder + "/pages");
		if(!pagesDir.exists()) {
			pagesDir.mkdir();
		}
		
		try {
			
			template.parse();
			
			PolygonFileReader polygonFileReader = new PolygonFileReader(new File(args[2]));
			Area covereage = polygonFileReader.loadPolygon();
			GridGenerator gridGenerator = new GridGenerator(covereage);
			gridGenerator.generate(template.getScaledMapWidthM(), template.getScaledMapHeightM());
			
			MapRasterDataSource rds = new MapRasterDataSource(300, 
					"http://localhost/cgi-bin/qgis_mapserv.fcgi?map=/home/dkiselev/osm/walking-papers/bw.qgs",
					template.getWidthPX(), template.getHeightPX());
			
			for(GridCell c : gridGenerator.listCells() ) {
				byte[] content = rds.loadImage(c);
				
				MapPageTemplate newDocTemplate = insertImage(content, template, "map_page" + c.getPn() + ".png");
				insertPageRefs(newDocTemplate, c, gridGenerator.getGrid(), template);
				
				String savePath = folder + "/pages/" + intToString(c.getPn(), 3) + ".odt";
				newDocTemplate.getDocument().save(savePath);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	static String intToString(int num, int digits) {
	    assert digits > 0 : "Invalid number of digits";

	    // create variable length array of zeros
	    char[] zeros = new char[digits];
	    Arrays.fill(zeros, '0');
	    // format number as String
	    DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

	    return df.format(num);
	}

	private static void insertPageRefs(MapPageTemplate newDocTemplate,
			GridCell c, GridCell[][] gridCells, MapPageTemplate template) throws Exception {

		setParagrafValue(newDocTemplate, String.valueOf(c.getPn()), MapPageTemplate.PAGE_NUMBER_FRAME);

		GridCell topCell = getCellSafe(c.getI(), c.getJ() + 1, gridCells);
		if(topCell != null) {
			setParagrafValue(newDocTemplate, String.valueOf(topCell.getPn()), MapPageTemplate.TOP_PAGE_REF_FRAME);
		}
		
		GridCell bottomCell = getCellSafe(c.getI(), c.getJ() - 1, gridCells);
		if(bottomCell != null) {
			setParagrafValue(newDocTemplate, String.valueOf(bottomCell.getPn()), MapPageTemplate.BOTTOM_PAGE_REF_FRAME);
		}
		
		GridCell leftCell = getCellSafe(c.getI() - 1, c.getJ(), gridCells);
		if(leftCell != null) {
			setParagrafValue(newDocTemplate, String.valueOf(leftCell.getPn()), MapPageTemplate.LEFT_PAGE_REF_FRAME);
		}
		
		GridCell rightCell = getCellSafe(c.getI() + 1, c.getJ(), gridCells);
		if(rightCell != null) {
			setParagrafValue(newDocTemplate, String.valueOf(rightCell.getPn()), MapPageTemplate.RIGHT_PAGE_REF_FRAME);
		}
	}

	private static GridCell getCellSafe(int i, int j, GridCell[][] gridCells) {
		
		if(i < 0 || i >= gridCells.length) {
			return null;
		}

		if(j < 0 || j >= gridCells[i].length) {
			return null;
		}
		
		return gridCells[i][j];
	}

	private static void setParagrafValue(MapPageTemplate newDocTemplate,
			String value, String frameName) throws Exception,
			XPathExpressionException {
		
		TextDocument newTextDocument = newDocTemplate.getDocument();
		OdfContentDom contentDom = newTextDocument.getContentDom();
		XPath xpath = contentDom.getXPath();

		Node pageNumberFrame = newDocTemplate.getFrameByName().get(frameName);
		if(pageNumberFrame != null) {
			TextPElement lastPara = (TextPElement) xpath.evaluate("//draw:frame[@draw:name='"+frameName+"']//text:p[last()]", pageNumberFrame, XPathConstants.NODE);
			lastPara.setTextContent(value);
		}
	}


	private static MapPageTemplate insertImage(byte[] content, MapPageTemplate template, String imgName) throws Exception {
		
		template = template.withNewDocument();
		TextDocument newTextDocument = template.getDocument();
		
		OdfContentDom contentDom = newTextDocument.getContentDom();
		OdfDrawFrame drawFrame = contentDom.newOdfElement(OdfDrawFrame.class);
		
		XPath xpath = contentDom.getXPath();		
		Node mapFrame = template.getFrameByName().get(MapPageTemplate.MAP_FRAME);
		TextPElement lastPara = (TextPElement) xpath.evaluate("//draw:frame[@draw:name='"+MapPageTemplate.MAP_FRAME+"']//text:p[last()]", mapFrame, XPathConstants.NODE);
		if (lastPara == null) {
			throw new RuntimeException("Map frame doesn't contains paragraph.");
		}
		lastPara.appendChild(drawFrame);

		drawFrame.setSvgWidthAttribute((template.getWidthMM() / 10) + "cm");
		drawFrame.setSvgHeightAttribute((template.getHeightMM() / 10) + "cm");
		drawFrame.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PARAGRAPH.toString());
		drawFrame.setStyleRelHeightAttribute("100%");
		drawFrame.setStyleRelWidthAttribute("100%");

		OdfDrawImage image = (OdfDrawImage) drawFrame.newDrawImageElement();
		image.newImage(new ByteArrayInputStream(content), "Pictures/" + imgName, "image/png");
		
		return template;
	}
}
