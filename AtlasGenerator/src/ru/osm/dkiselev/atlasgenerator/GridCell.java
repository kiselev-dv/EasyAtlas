package ru.osm.dkiselev.atlasgenerator;

import ru.osm.dkiselev.atlasgenerator.util.BBOX;

public class GridCell {
	
	private BBOX bbox;
	private int i;
	private int j;
	private int pn;

	public GridCell (BBOX bbox) {
		this.bbox = bbox;
	}
	
	public BBOX getBbox() {
		return bbox;
	}
	
	public void setBbox(BBOX bbox) {
		this.bbox = bbox;
	}
	
	public int getI() {
		return i;
	}
	
	public void setI(int i) {
		this.i = i;
	}
	
	public int getJ() {
		return j;
	}
	
	public void setJ(int j) {
		this.j = j;
	}
	
	public int getPn() {
		return pn;
	}
	
	public void setPn(int pn) {
		this.pn = pn;
	}

}
