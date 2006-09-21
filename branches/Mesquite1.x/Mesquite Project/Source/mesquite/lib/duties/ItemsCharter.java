/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib.duties;import java.awt.*;import mesquite.lib.*;/* ======================================================================== *//***/public abstract class ItemsCharter extends MesquiteModule  {   	 public Class getDutyClass() {   	 	return ItemsCharter.class;   	 }   	 public String[] getDefaultModule() {   	 	return new String[] {"#ItemValuesChart"};   	 }	/*...................................................................................................................*/	public abstract void doCounts(CommandRecord commandRec, int firstItem, int lastItem, boolean fullCount);	/*...................................................................................................................*/	public void doCounts(CommandRecord commandRec) {		doCounts(commandRec,MesquiteInteger.unassigned,MesquiteInteger.unassigned, true);	}	/*.................................................................................................................*/	public abstract void incrementSuspension(CommandRecord commandRec);	/*.................................................................................................................*/	public abstract void decrementSuspension(CommandRecord commandRec);	/*.................................................................................................................*/ 	public abstract ChartWindow makeChartWindow(MesquiteModule requester, CommandRecord commandRec);	/*...................................................................................................................*/	public abstract void setTaxa(Taxa taxa);	/*...................................................................................................................*/	public void setAuxiliary(Object object, boolean useAsFirstParameter){	}	/*...................................................................................................................*/	public abstract void setItemsSource( ItemsSource itemsSourceTask);	/*...................................................................................................................*/	public abstract void setNumberTask(NumberForItem numberTask);	/*.................................................................................................................*/	public abstract void setDefaultNumberOfItems(int def);   	public boolean isSubstantive(){   		return false;     	}}