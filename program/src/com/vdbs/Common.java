package com.vdbs;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

public class Common {
	public static String PROJECTS_DATA_PATH 			= "../data/";
	final static String PROJECT_EXT 				= "xml";
	final static String PROJECT_DOT_EXT				= ".xml";
	
	final static String TITLE 						= "Marmotte Visual ver. " + Common.VERSION;
	final static String VERSION 					= "0.95";
	final static int SIZE_WIDTH 					= 450;
	final static int SIZE_HEIGHT 					= 400;
	final static String DOC_URL 					= "http://java.sun.com";


	public static void error(String title, String content) {
			JOptionPane.showMessageDialog(
											UserInterface.baseJFrame,
										    content,
										    title,
										    JOptionPane.ERROR_MESSAGE
										);

	}

	public static List getUniqueList(List list) {
		List resultList = new ArrayList();

		for(int i = 0; i < list.size(); i++) {
			if (!resultList.contains(list.get(i)) && !list.get(i).equals(""))
				resultList.add(list.get(i));
		}

		return resultList;
	}

	public static Object[] addElement(Object[] base, Object added) {
	    Object[] result = Arrays.copyOf(base, base.length + 1);
	    result[base.length] = added;
	    return result;
	}

}
