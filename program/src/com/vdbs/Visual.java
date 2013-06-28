package com.vdbs;

import java.awt.*; 
import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import javax.swing.event.*;
import java.util.Set;
import java.util.Iterator;
// jgrapht lib
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import java.awt.geom.Rectangle2D;

import org.dom4j.Element;

public class Visual extends Base {

    private  Color bgColor 				= Color.decode("#FAFBFF");
    private  int vertexWidth 			= 100;
	private  int vertexHeight			= 40;
	private List vertexPositionList		= null;
	private int jFrameW					= 600;
	private int jFrameH					= 400;
	private int deltaX					= 50;
	private int deltaY					= 50;
	private List data 					= new ArrayList();
    
    private JFrame visualJFrame;
    private JGraphModelAdapter jgAdapter;

	public Visual() {

        this.setVisualJFrame(new JFrame(this.getBi().getProject().getName()));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setJFrameW(screenSize.width);
        this.setJFrameH(screenSize.height);
        this.getVisualJFrame().setSize(this.getJFrameW(), this.getJFrameH());
        this.getVisualJFrame().setVisible(true);
	}

	public void paintItBlack() {
		// create a JGraphT graph
        ListenableGraph g = new ListenableDirectedGraph(DefaultEdge.class);
        this.setJgAdapter(new JGraphModelAdapter(g));

        JGraph jgraph = new JGraph(this.getJgAdapter());

        this.adjustDisplaySettings(jgraph);
        this.getVisualJFrame().getContentPane().add(jgraph);

        List sets = new ArrayList();
        if (this.getData().size() == 0)
        	sets = this.getBi().getProject().getSets();
        else
        	sets = this.getData();

    	List usedObjects = new ArrayList();
    	List usedLinks = new ArrayList();

    	for (int i = 0; i < sets.size(); i++) {
    		int set_id = Integer.parseInt(this.getBi().getProject().getNodeAttribute(sets.get(i), "set_id"));
    		String o1Text = this.getBi().getProject().getObject1(set_id).getText();
    		String o2Text = this.getBi().getProject().getObject2(set_id).getText();
    		String cText = this.getBi().getProject().getConnection(set_id).getText();
    		
    		if (!usedObjects.contains(o1Text)) {
    			g.addVertex(o1Text);
    		}
    		if (!usedObjects.contains(o2Text)) {
    			g.addVertex(o2Text);
    		}

    		// sick one!!! to prevent same-label exception :(
    		String pseudo = "";
    		for(int j = 0; j < i; j++) {
    			// adding non-printable char to fail equals of strings
    			pseudo += "\u200e";
    		}
    		// omg.
    		
    		g.addEdge(o2Text, o1Text, cText + pseudo);

    		List vPos = null;

			if (!usedObjects.contains(o1Text)) {
	    		vPos = this.getNextVertexPosition();  		
	    		positionVertexAt(o1Text, (int)vPos.get(0), (int)vPos.get(1));
    		}
    		// then set position to both vertexes
    		if (!o1Text.equals(o2Text) && !usedObjects.contains(o2Text)) {
    			vPos = this.getNextVertexPosition();
				positionVertexAt(o2Text, (int)vPos.get(0), (int)vPos.get(1));
    		}

    		if (!usedObjects.contains(o1Text)) {
    			usedObjects.add(o1Text);
    		}
    		if (!usedObjects.contains(o2Text)) {
    			usedObjects.add(o2Text);
    		}
	   		if (!usedLinks.contains(cText)) {
	    		usedLinks.add(cText);
	    	}
    	}
	}

	private List getNextVertexPosition() {
		List resultList = new ArrayList();
		List currentList = this.getCurrentVertexPosition();
		// first vertex
		if (currentList == null) {
			resultList.add(0);
			resultList.add(0);
		}
		else {
			int currentX = (int)currentList.get(0);
			int currentY = (int)currentList.get(1);
			// calculate new Vertex X
			if (currentX + this.getVertexWidth() + this.getDeltaY() <= this.getJFrameW() - this.getVertexWidth()) {
				resultList.add(currentX + this.getDeltaY() + this.getVertexWidth());
				resultList.add(currentY);
			}
			else {
				resultList.add(0);
				resultList.add(currentY + this.getDeltaY() + this.getVertexHeight());
			}
		}

		this.setCurrentVertexPosition(resultList);

		return resultList;
	}

	private List getCurrentVertexPosition() {
		return vertexPositionList;
	}

	private void setCurrentVertexPosition(List val) {
		this.vertexPositionList = val;
	}

	private void adjustDisplaySettings(JGraph jg) {
        jg.setBackground(this.getBgColor());
    }

	private void positionVertexAt(Object vertex, int x, int y) {
        DefaultGraphCell  cell 			= this.getJgAdapter().getVertexCell(vertex);
        Map attr 						= cell.getAttributes();
        Rectangle2D b 					= GraphConstants.getBounds(attr);

        GraphConstants.setBounds(attr, new Rectangle(x, y, this.getVertexWidth(), this.getVertexHeight()));

        Map cellAttr = new HashMap();
        cellAttr.put(cell, attr);
        this.getJgAdapter().edit(cellAttr, null, null, null);

	}


	// GETTERS && SETTERS

	public void setData(List val) {
		this.data = val;
	}

	public List getData() {
		return this.data;
	}

	private int getDeltaX() {
		return this.deltaX;
	}

	private int getDeltaY() {
		return this.deltaY;
	}

	public JFrame getVisualJFrame() {
		return this.visualJFrame;
	}

	public void setVisualJFrame(JFrame val) {
		this.visualJFrame = val;
	}

	public int getJFrameH() {
		return this.jFrameH;
	}

	public void setJFrameH(int val) {
		this.jFrameH = val;
	}

	public int getJFrameW() {
		return this.jFrameW;
	}

	public void setJFrameW(int val) {
		this.jFrameW = val;
	}

	public int getVertexWidth() {
		return this.vertexWidth;
	}

	public int getVertexHeight() {
		return this.vertexHeight;
	}

	public void setVertexWidth(int val) {
		this.vertexWidth = val;
	}

	public void setVertexHeight(int val) {
		this.vertexHeight = val;
	}	

	public Color getBgColor() {
		return this.bgColor;
	}

	public void setBgColor(Color val) {
		this.bgColor = val;
	}

	public JGraphModelAdapter getJgAdapter() {
		return this.jgAdapter;
	}

	public void setJgAdapter(JGraphModelAdapter val) {
		this.jgAdapter = val;
	}
}

	
