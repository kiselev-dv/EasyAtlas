package ru.osm.dkiselev.atlasgenerator;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GridGenerator {
	
	private static final int COVERED_UNTIL_DROP_PERCENTS = 10;
	private static final int COVERED_UNTIL_RELLOCATE_PERCENTS = 40;
	private static final int OVERLAP_PECENTS = 10;
	
	private Area coverage = null;
	private GridCell[][] grid = null;
	
	private static final Logger logger = Logger.getLogger(GridGenerator.class.getName());
	private static final int PAGE_NUMBERS_OFFSET = 0;
	
	public GridGenerator(Area coverage) {
		super();
		this.coverage = coverage;
	}

	public GridCell[][] getGrid() {
		return grid;
	}
	
	public void generate(double bboxWidthM, double bboxHeightM)
	{
		Rectangle2D coverageBox = coverage.getBounds2D();
		Point basePoint = new Point(coverageBox.getMinX(), coverageBox.getMinY());
		
		int columns = 0;
		Point p = basePoint;
		do {
			p = Calculator.addOffset(p, bboxWidthM, 0);
			columns++;
		}while(p.getLon() < coverageBox.getMaxX() && columns < 100);

		int rows = 0;
		p = basePoint;
		do {
			p = Calculator.addOffset(p, 0, bboxHeightM);
			rows++;
		}while(p.getLat() < coverageBox.getMaxY() && rows < 100);
		
		logger.log(Level.INFO, "Grid size {0}х{1}", new Object[]{columns, rows});

		if(columns * rows > 200) {
			logger.severe("Atlas is too large.");
			return;
		}
		
		grid = new GridCell[columns][rows];
		
		p = basePoint;
		for(int j = 0; j < rows; j++) {
			for(int i = 0; i < columns; i++) {
				
				Point offsetted = Calculator.addOffset(p, bboxWidthM, bboxHeightM);
				
				GridCell gridCell = new GridCell(new BBOX(offsetted.getLat(), p.getLat(), p.getLon(), offsetted.getLon()));
				gridCell.setI(i);
				gridCell.setJ(j);
				
				grid[i][j] = gridCell;
				
				p = Calculator.addOffset(p, bboxWidthM, 0);
			}
			p.setLon(basePoint.getLon());
			p = Calculator.addOffset(p, 0, bboxHeightM);
		}
		
		for(int j = 0; j < rows; j++) {
			for(int i = 0; i < columns; i++) {
				BBOX bbox = grid[i][j].getBbox();
				Area area = new Area(new Rectangle2D.Double(
						bbox.getLeft(), 
						bbox.getBottom(), 
						bbox.getRight() - bbox.getLeft(),
						bbox.getTop() - bbox.getBottom()));
				
				double full = Calculator.getArea(area);
				area.intersect(coverage);
				
				double occupied = Calculator.getArea(area);
				double ratio = occupied/full;
				
				if(ratio * 100 < COVERED_UNTIL_DROP_PERCENTS) {
					grid[i][j] = null;
					
					logger.log(Level.INFO, "Drop grid cell {0},{1}. Because less then {2}% area is covered.", 
							new Object[]{i, j, COVERED_UNTIL_DROP_PERCENTS});
					
					continue;
				}
				
				if(ratio * 100 < COVERED_UNTIL_RELLOCATE_PERCENTS) {
					
					BBOX cellToMove = grid[i][j].getBbox();
					
					Point curentCenter = Calculator.getCenterPoint(cellToMove);
					Point newCenter = Calculator.getCenterPoint(area);
					
					double dx = newCenter.getLon() - curentCenter.getLon();
					double dy = newCenter.getLat() - curentCenter.getLat();
					
					cellToMove.move(dx, dy);
					
					logger.log(Level.INFO, "Grid cell {0},{1} was rellocated to fit more coverage.", 
							new Object[]{i, j});
					
					continue;
				}
				
			}
		}
		
		int page = 1 + PAGE_NUMBERS_OFFSET;
		for(int j = rows - 1; j >= 0; j--) {
			for(int i = 0; i < columns; i++) {
				if(grid[i][j] != null) {
					grid[i][j].setPn(page++);
				}
			}
		}	
		
	}
	
	public List<GridCell> listCells() {
		
		List<GridCell> r = new ArrayList<GridCell>();
		
		for (int i = 0; i < grid.length; i++ ) {
			for (int j = 0; j < grid[i].length; j++) {
				if(grid[i][j] != null) {
					r.add(grid[i][j]);
				}
			}
		}
		
		return r;
	}
	
}
