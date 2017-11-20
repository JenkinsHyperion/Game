package testEntities;

import engine.TestBoard;
import testEntities.PlantSegment.GrowingSegment;

public class DavePlant extends PlantSegment.TreeGenome{

	public DavePlant(PlantSegment plantSegment, String treeName) {
		plantSegment.super(treeName);
	}

	@Override
	public GrowingSegment createBranch(int x, int y, int maxGrowth, TestBoard board) {
		
		return null;
	}
	
	
	
}
