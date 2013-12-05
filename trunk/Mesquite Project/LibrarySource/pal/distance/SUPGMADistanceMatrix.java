// SUPGMADistanceMatrix.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.distance;

import pal.misc.*;

/**
 * Corrects distances in a distance matrix such that all tips appear
 * contemporaneous, given a time/date and rate information for the 
 * taxa.
 *
 * @version $Id: SUPGMADistanceMatrix.java,v 1.2 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Alexei Drummond
 */
public class SUPGMADistanceMatrix extends DistanceMatrix {
    

	/**
	 * Uses date/time information and a constant rate to correct distance matrices.
	 */
	public SUPGMADistanceMatrix(DistanceMatrix raw, TimeOrderCharacterData tocd, double rate) {
	
		super(raw);
	
		double[] tips = new double[tocd.getIdGroup().getIdCount()];
		for (int i = 0; i < tips.length; i++) {
			if (tocd.hasTimes()) {
				tips[i] = rate * tocd.getTime(i);
			} else {
				tips[i] = 0.0;
			}
	
			for (int j = 0; j < tips.length; j++) {
				if (i != j) {
					addDistance(i, j, tips[i]);
				}
			}
		}		
	}
}

