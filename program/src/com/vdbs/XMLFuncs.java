package com.vdbs;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.SAXReader;
import org.dom4j.XPath;
import org.dom4j.tree.DefaultAttribute;
import java.io.FileWriter;
import java.io.File;

import java.util.List;


class XMLFuncs {
	public static Document getXMLStruct(File file) {
		try {
			    SAXReader reader = new SAXReader();
        		Document doc = reader.read(file);

        		return doc;
		}
		catch(Exception e) {
			Common.error("XML From File Exception", e.getMessage());

			return null;
		}

	}

	public static String getNodeText(Element node, Document doc) {
		return String.valueOf(node.getText());
	}


	public static boolean createProject(String name) {
		try {
			Document doc = XMLFuncs.createBaseStruct(name);
			XMLFuncs.writeXML(Common.PROJECTS_DATA_PATH + name + Common.PROJECT_DOT_EXT, doc);
			return true;
		}
		catch(Exception e) {
			Common.error("Creating project Exception", e.getMessage());
			return false;
		}
	}

    public static Document createBaseStruct(String name) {
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("project");
        root.addElement("content");
        Element metaNode = root.addElement("meta");
        metaNode.addElement("name").setText(name);
        metaNode.addElement("create_time").setText(Bi.getCurrentTime());


        return doc;
    }

    public static void writeXML(String filename, Document doc) {
 		try {			
 			OutputFormat format = OutputFormat.createPrettyPrint();
	 		XMLWriter writer = new XMLWriter(new FileWriter(filename), format);
	        writer.write(doc);
	        writer.close();
	    }
		catch(Exception e) {
			Common.error("Creating Project Exception", e.getMessage());
		}
    }


    public static void traceXML(Document doc) {
    	try {
			OutputFormat format = OutputFormat.createPrettyPrint();
	        XMLWriter writer = new XMLWriter(System.out, format);
	        writer.write(doc);
	    }
		catch(Exception e) {
			Common.error("Console Trace Exception", e.getMessage());
		}
    }

	public static List getXPATHNodes(String path, Document doc) {
		try {
			XPath xpathSelector = DocumentHelper.createXPath(path);
    		return xpathSelector.selectNodes(doc);
		}
		catch(Exception e) {
			Common.error("Exception", "Failed on getting Data From XML");
			return null;
		}
	}

	public static Element selectSingleNode(String path, Document doc) {
		try {
			return (Element)doc.selectSingleNode(path);
		}
		catch(Exception e) {
			Common.error("Exception", "Failed on gettin Single Node");
			return null;			
		}
	}

	public static void setValueToNode(String path, Document doc, String text) {
		try {
			XMLFuncs.selectSingleNode(path, doc).setText(text);
		}
		catch(Exception e) {
			Common.error("Exception", "Cant set Text to Node");
		}
	}

	public static Element createElement(Element el, String elName, String elText) {
		try {
			Element result = el.addElement(elName);
			if (elText.length() > 0)
				result.addText(elText);

			return result;
		}
		catch(Exception e) {
			Common.error("Exception", "Faild on creation Element on Node");
			return null;
		}
	}

	public static void setAttribute(Element el, String attr_name, String attr_val) {
		try {
			el.addAttribute(attr_name, attr_val);
		}
		catch(Exception e) {
			Common.error("Exception", "Faild on adding Attribute to Node");
		}
	}

	public static String getAttribute(String path, Document doc, String attr_name) {
		try {
			Element el = XMLFuncs.selectSingleNode(path, doc);
			return (String)el.attribute(attr_name).getValue();
		}
		catch(Exception e) {
		//	Common.error("Exception", "Failed getting Attribute");
			return null;
		}
	}

	public static String getNodeAttribute(Element el, Document doc, String attr_name) {
		try {
			return (String)el.attribute(attr_name).getValue();
		}
		catch(Exception e) {
		//	Common.error("Exception", "Failed getting Attribute");
			return null;
		}
	}

	public static List getSetIdOnObj(String path, Document doc) {
		try {
			/*
			List<Element> sets = XMLFuncs.getXPATHNodes(path, doc);
			for (int i = 0; i < sets.size(); i++) {
				result.add((int)sets.get(i).attribute(attr_name).getValue());
			}
			*/
			return XMLFuncs.getXPATHNodes(path, doc);
		}
		catch(Exception e) {
		//	Common.error("Exception", "Failed getting Attribute");
			return null;
		}

	}
}