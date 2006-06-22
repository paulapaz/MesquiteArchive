/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import java.awt.event.*;import java.awt.image.*;/* ======================================================================== *//** This class provides general graphics utilities */public class GraphicsUtil {	/* ............................................................................................................... */	public static void shimmerVerticalOn(Graphics g, Panel panel, int top, int bottom, int x) {		if (g==null && panel==null)		if (!MesquiteInteger.isCombinable(x))			return;		Graphics mg = g;		if (mg==null)			mg = panel.getGraphics();		if (mg == null)			return;//		mg.setColor(Color.black);		mg.setXORMode(Color.white);		mg.drawLine(x, top, x, bottom);//		mg.drawLine(x+1, top, x+1, bottom);		if (g==null)			mg.dispose();	}	/* ............................................................................................................... */	public static void shimmerHorizontalOn(Graphics g, Panel panel, int left, int right, int y) {		if (g==null && panel==null)		if (!MesquiteInteger.isCombinable(y))			return;		Graphics mg = g;		if (mg==null)			mg = panel.getGraphics();		if (mg == null)			return;		mg.setXORMode(Color.white);		mg.drawLine(left, y, right, y);		if (g==null)			mg.dispose();	}	/* -------------------------------------------------*/	public static void fillTransparentSelectionRectangle (Graphics g, int x, int y, int w, int h) {		if (MesquiteWindow.Java2Davailable) {			ColorDistribution.setTransparentGraphics(g,0.3f);					g.setColor(Color.black);			g.fillRect(x,y,w, h);			ColorDistribution.setOpaqueGraphics(g);				}	}	/* -------------------------------------------------*/	public static void shadeRectangle (Graphics g, int x, int y, int w, int h, Color color) {		if (MesquiteWindow.Java2Davailable) {			ColorDistribution.setTransparentGraphics(g,0.2f);					g.setColor(color);			g.fillRect(x,y,w, h);			ColorDistribution.setOpaqueGraphics(g);				}	}	/* -------------------------------------------------*/	public static void darkenRectangle (Graphics g, int x, int y, int w, int h) {		if (MesquiteWindow.Java2Davailable) {			ColorDistribution.setTransparentGraphics(g,0.2f);					g.setColor(Color.black);			g.fillRect(x,y,w, h);			ColorDistribution.setOpaqueGraphics(g);				}	}/* -------------------------------------------------*/	public static void setFontName (String name, Graphics g) {		Font curFont = g.getFont(); 		Font fontToSet = new Font (name, curFont.getStyle(), curFont.getSize()); 		if (fontToSet!= null) { 			curFont = fontToSet; 			g.setFont(curFont); 		}    }	/* -------------------------------------------------*/	public static void setFontStyle (int style, Graphics g) {		Font curFont = g.getFont(); 		Font fontToSet = new Font (curFont.getName(), style, curFont.getSize()); 		if (fontToSet!= null) { 			curFont = fontToSet; 			g.setFont(curFont); 		}    	 }	/* -------------------------------------------------*/    	 public static void setFontSize (int size, Graphics g) {		Font curFont = g.getFont(); 		Font fontToSet = new Font (curFont.getName(), curFont.getStyle(), size); 		if (fontToSet!= null) { 			curFont = fontToSet; 			g.setFont(curFont); 		}    	 }	/* -------------------------------------------------*/	public static void fillOval(Graphics g, int x, int y, int w, int h, boolean threeD){		try {			if (threeD){				Color c = g.getColor();				Color current = c;				current = ColorDistribution.darker(current, 0.75);				while (w>0) {					g.setColor(current);					g.fillOval(x,y,w,h);					x++;					y++;					w-=4;					h-=4;					current = ColorDistribution.brighter(current, 0.75);				}				if (c!=null) g.setColor(c);			}			else				g.fillOval(x,y,w,h); 		}		catch(NullPointerException e){			MesquiteMessage.warnProgrammer("npe in fill oval x " + x + " y " + y + " w " + w + " h " + h);		}	}	/* -------------------------------------------------*/	public static void fillArc(Graphics g, int x, int y, int w, int h, int startAngle, int arcAngle, boolean threeD){		if (arcAngle < 1)			return;		if (MesquiteTrunk.isWindows()){ //this is workaround to Windows problem by which goes all black if too close to 0 or 360			int spotsize = MesquiteInteger.maximum(w, h);			if (3.14*spotsize*(360-arcAngle)/360<1){				fillOval(g, x, y, w, h, threeD);				return;			}			if (3.14*spotsize*arcAngle/360<1)				return;		}		if (threeD){			Color c = g.getColor();			Color current = c;//TODO: needs to define a polygon that clips to prevent upward curved edges on left side			current = ColorDistribution.darker(current, 0.75);			while (w>0) {				g.setColor(current);				g.fillArc(x,y,w,h, startAngle, arcAngle);				x++;				y++;				w-=4;				h-=4;				current = ColorDistribution.brighter(current, 0.75);			}			if (c!=null) g.setColor(c);		}		else			g.fillArc(x,y,w,h, startAngle, arcAngle); 	}	}