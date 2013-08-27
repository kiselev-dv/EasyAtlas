package ru.osm.dkiselev.atlasgenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import ru.osm.dkiselev.atlasgenerator.configs.Config;
import ru.osm.dkiselev.atlasgenerator.util.BBOX;
import ru.osm.dkiselev.atlasgenerator.util.Calculator;

public class MapRasterDataSource {
	
	public static final String MAP_RASTER_SOURCE_SECTION_NAME = "MapRasterSource";

	private static final String URL_OPTION_NAME = "URL";
	private static final String DPI_OPTION_NAME = "DPI";
	
	private int dpi;
	private String wmsURL = null;
	private int widthPX;
	private int heightPX;
	
	private static final double INCHES_TO_CM = 2.54;
	
	private static final Logger logger = Logger.getLogger(MapRasterDataSource.class.getName());
	
	public MapRasterDataSource(double widthMM, double heightMM) {
		dpi = Config.get(MAP_RASTER_SOURCE_SECTION_NAME, DPI_OPTION_NAME, Integer.class);
		wmsURL = Config.get(MAP_RASTER_SOURCE_SECTION_NAME, URL_OPTION_NAME);
		
		widthPX = new Double(dpi / INCHES_TO_CM * widthMM / 10).intValue();  
		heightPX = new Double(dpi / INCHES_TO_CM * heightMM / 10).intValue();  
		logger.log(Level.INFO, "Map width: {0}px, height: {1}px at {2} dpi.", new Object[]{widthPX, heightPX, dpi});
	}

	public byte[] loadImage(GridCell c) {
		
		String url = getURL(c);
		logger.info("Load image: " + url);
		
		try {
			InputStream is = new URL(url).openStream();
			return IOUtils.toByteArray(is);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private String getURL(GridCell c) {
		StringBuilder sb = new StringBuilder(wmsURL);
		
		if(!wmsURL.endsWith("?")) {
			sb.append("?");
		}
		
		BBOX bbox = Calculator.forwardMercator(c.getBbox());
		
		sb.append("&bbox=");
		sb.append(bbox.getLeft());
		sb.append(",");
		sb.append(bbox.getBottom());
		sb.append(",");
		sb.append(bbox.getRight());
		sb.append(",");
		sb.append(bbox.getTop());
		
		sb.append("&dpi=").append(dpi);
		
		Map<String, String> options = loadWMSOptions();
		for(Entry<String, String> entry : options.entrySet())
		{
			try {
				sb.append("&").append(URLEncoder.encode(entry.getKey(), "utf-8")).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		sb.append("&height=").append(heightPX);
		sb.append("&width=").append(widthPX);
		
		return sb.toString();
	}

	
	private Map<String, String> loadWMSOptions() {
		
		Map<String, String> opts = new HashMap<String, String>();
		
		opts.put("service", "WMS");
		opts.put("request", "GetMap");
		opts.put("version", "1.1.1");
		opts.put("layers", "");
		opts.put("format", "image/png");
		opts.put("srs", "EPSG:3857");
		
		opts.putAll(Config.getMapByPrefix("MapRasterSource", "WMSOpt_"));
		
		return opts;
	}
	
	
}
