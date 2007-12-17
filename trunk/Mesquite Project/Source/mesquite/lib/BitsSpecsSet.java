/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison.Version 2.01, December 2007.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;/*==========================  Mesquite Basic Class Library    ==========================*//*===  the basic classes used by the trunk of Mesquite and available to the modules/* ======================================================================== *//** A specset with bit information.*/public abstract class BitsSpecsSet extends SpecsSet  {	Bits myBits;	public BitsSpecsSet(String name, int numParts){		super(name, numParts);		myBits = new Bits(numParts);	}	public String getTypeName(){		return "Bits specificaton set";	}		public String toString(int ic){		if (isSelected(ic))			return "true";		else			return "false";	} 	/*.................................................................................................................*/ 	/** Sets the value for part "part" to be the same as that at part "otherPart" in the incoming specsSet*/	public void equalizeSpecs(SpecsSet other, int otherPart, int part){		if (other instanceof BitsSpecsSet){			setSelected(part, ((BitsSpecsSet)other).isSelected(otherPart));		}	} 	/*.................................................................................................................*/	/** Returns a string that simply lists the on bits, NOT zero-based. */	public String getListOfOnBits(String delimiter){		StringBuffer sb= new StringBuffer();		boolean first = true;		for (int i=0; i<getNumberOfParts(); i++)			if (isPresent(i))				if (first){					sb.append("" + (i+1));					first = false;				}				else					sb.append(delimiter + (i+1));		return sb.toString();	} 	/*.................................................................................................................*/	/** Returns bits. */	public Bits getBits(){		return myBits;	}	/*.................................................................................................................*/	/** Returns if part ic is on. */	public boolean isSelected(int ic){		return myBits.isBitOn(ic);	}	/*.................................................................................................................*/	/** Returns if part ic is on. */	public boolean isPresent(int ic){		return myBits.isBitOn(ic);	}	/** Returns if any part are on */	public boolean anySelected(){		return myBits.anyBitsOn();			}	/** Returns how many are on */	public int numberSelected(){		return myBits.numBitsOn();			}	/** Set bits of this to be the same as passed bits */	public void setSelectedBits(Bits bits){		setDirty(true);		if (bits!=null)			bits.copyBits(myBits);	}	/** Set part ic to either on or not according to boolean */	public void setSelected(int ic, boolean select){		setDirty(true);		if (select)			myBits.setBit(ic);		else			myBits.clearBit(ic);	}	/** Deselect all part */	public void deselectAll(){		setDirty(true);		myBits.clearAllBits();	}	/** Select all parts */	public void selectAll(){		setDirty(true);		myBits.setAllBits();	} 	/*.................................................................................................................*/	/** Add num parts just after "starting" (filling with default values)  */  	public boolean addParts(int starting, int num){  		setDirty(true); 		myBits.addParts(starting, num); //TODO: set default (for inclusion, set to on!!!) 		numParts = myBits.getSize();		return true;	}	/*.................................................................................................................*/	/** Delete num parts from and including "starting"  */	public boolean deleteParts(int starting, int num){ 		setDirty(true); 		myBits.deleteParts(starting, num); 		numParts = myBits.getSize();		return true;	}	/*.................................................................................................................*/ 	/** */	public boolean moveParts(int starting, int num, int justAfter){  		setDirty(true); 		myBits.moveParts(starting, num, justAfter);		return true;	}	/*.................................................................................................................*/ 	/** */	public boolean swapParts(int first, int second){  		setDirty(true); 		myBits.swapParts(first, second);		return true;	}}