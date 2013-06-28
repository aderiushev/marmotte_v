package com.vdbs;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;


public class Project extends Bi {
	private String name;
	private Document doc;
	private String filepath;

    private List tempObjectNames        = new ArrayList();
    private List tempLinkNames          = new ArrayList();

    final static String LINK_NODE 		= "link";
	final static String TARGET_NODE 	= "target";
	final static String SOURCE_NODE 	= "source";
	final static String CONTENT_NODE 	= "content";
	final static String ROOT_NODE		= "project";
	final static String META_NODE		= "meta";

	public Project() {

	}

    public List getTempObjectNames() {
        return this.tempObjectNames;
    }

    public List getTempLinkNames() {
        return this.tempLinkNames;
    }

	public Project setName(String val) {
		this.name = val;
		return this;
	}


	public String getName() {
		return this.name;
	}

	public Project setDoc(Document val) {
		this.doc = val;
		return this;
	}

	public Document getDoc() {
		return this.doc;
	}

	private String getRootNode() {
		return "/" + Project.ROOT_NODE;
	}

	private String getContentNode() {
		return "/" + Project.ROOT_NODE + "/" + Project.CONTENT_NODE;
	}

	public boolean p_init(String name) {
        try {
        	this.setFilePath(Common.PROJECTS_DATA_PATH + name + Common.PROJECT_DOT_EXT);
        	this.setName(name);
            this.setDoc(
                XMLFuncs.getXMLStruct(
                    FSFuncs.getFile(
                        this.getName()
                    )
                )
            );

            return true;
        }
        catch(Exception e) {
            Common.error("Error Initing Project", "Check the instance");
            return false;
        }
	}

	public String getNodeText(Object node) {
		return XMLFuncs.getNodeText((Element)node, this.getDoc());
	}

	public String getNodeAttribute(Object node, String attr) {
		return XMLFuncs.getNodeAttribute((Element)node, this.getDoc(), attr);
	}

	public void setFilePath(String val) {
		this.filepath = val;
	}

	public String getFilePath() {
		return this.filepath;
	}


	public List getSets() {
		return XMLFuncs.getXPATHNodes(this.getContentNode() + "/set", this.getDoc());
	}

	public int getObjectId(String object) {
		String result =
						XMLFuncs.getAttribute(
							this.getContentNode() + "/set/" + Project.SOURCE_NODE + "[text()='" + object + "']|" + this.getContentNode() + "/set/" + Project.TARGET_NODE + "[text()='" + object + "']", 
							this.getDoc(), 
							"object_id"
						);

		if (result == null)
			return -1;
		else
			return Integer.parseInt(result);
	}

	public int getConnectionId(String object) {
		String result =
						XMLFuncs.getAttribute(
							this.getContentNode() + "/set/" + Project.LINK_NODE + "[text()='" + object + "']", 
							this.getDoc(), 
							"link_id"
						);
		if (result == null)
			return -1;
		else
			return Integer.parseInt(result);				
	}

	public int getSetIdOnIndex(int set_index) {
		return Integer.parseInt(XMLFuncs.getAttribute(this.getContentNode() + "/set[" + (++set_index) + "]", this.getDoc(), "set_id"));	
	}

	public Element getObject1(int set_id) {
		return (Element)XMLFuncs.selectSingleNode(this.getContentNode() + "/set[@set_id='" + set_id  + "']/" + Project.SOURCE_NODE, this.getDoc());
	}

	public Element getObject2(int set_id) {
		return (Element)XMLFuncs.selectSingleNode(this.getContentNode() + "/set[@set_id='" + set_id  + "']/" + Project.TARGET_NODE, this.getDoc());
	}

	public Element getConnection(int set_id) {
		return (Element)XMLFuncs.selectSingleNode(this.getContentNode() + "/set[@set_id='" + set_id  + "']/" + Project.LINK_NODE, this.getDoc());
	}

	public List getObjects() {
		return XMLFuncs.getXPATHNodes(this.getContentNode() + "/set/" + Project.SOURCE_NODE + "|" + this.getContentNode() + "/set/" + Project.TARGET_NODE, this.getDoc());
	}

	public List getConnections() {
		return XMLFuncs.getXPATHNodes(this.getContentNode() + "/set/" + Project.LINK_NODE, this.getDoc());
	}

	public Object[] getObjectNames(List tempObjects) {
        List objects = new ArrayList<String>(this.getObjects());
        List resultListObj = new ArrayList<String>();
        resultListObj.add("Choose..");

        if (tempObjects.size() > 0)
        	resultListObj.addAll(tempObjects);

        for (int i = 0; i < objects.size(); i++) {
            Element tmpEl = (Element)objects.get(i);
            resultListObj.add(tmpEl.getText());
        }

        List uniqueList = Common.getUniqueList(resultListObj);

        String[] tmpArrayObj = new String[uniqueList.size()];

        return uniqueList.toArray(tmpArrayObj);
	}
	
	public Object[] getConnectionNames(List tempLinks) {
        List connections = new ArrayList<String>(this.getConnections());
        List resultListConn = new ArrayList<String>();
        resultListConn.add("Choose..");

        if (tempLinks.size() > 0)
        	resultListConn.addAll(tempLinks);

        for (int i = 0; i < connections.size(); i++) {
            Element tmpEl = (Element)connections.get(i);
            resultListConn.add(tmpEl.getText());
        }

        List uniqueList = Common.getUniqueList(resultListConn);
        
        String[] tmpArrayConn = new String[uniqueList.size()];

        return uniqueList.toArray(tmpArrayConn);
	}

	public boolean checkExist(String o1, String o2, String c1) {
		// TODO: DO THIS!
		return false;
	}

	public void addSet(Object[] setItems) {
		Element tmpEl = (Element)XMLFuncs.selectSingleNode(this.getContentNode(), this.getDoc());
		int set_id = this.getMaxSetId() + 1;
		Element setNode = XMLFuncs.createElement(tmpEl, "set", "");
		XMLFuncs.setAttribute(setNode, "set_id", String.valueOf(set_id));
		
		// source adding
		Element o1 = XMLFuncs.createElement(setNode, Project.SOURCE_NODE, String.valueOf(setItems[0]));
		int item1_id = this.getObjectId(String.valueOf(setItems[0]));
		if (item1_id == -1) {
			// have no such item yet.
			item1_id = this.getMaxItemId() + 1;
		}
		XMLFuncs.setAttribute(o1, "object_id", String.valueOf(item1_id));
		
		// target adding
		Element o3 = XMLFuncs.createElement(setNode, Project.TARGET_NODE, String.valueOf(setItems[1]));
		int item2_id = this.getObjectId(String.valueOf(setItems[1]));
		if (item2_id == -1) {
			// have no such item yet.
			item2_id = this.getMaxItemId() + 1;
		}
		XMLFuncs.setAttribute(o3, "object_id", String.valueOf(item2_id));

		// link adding
		Element o2 = XMLFuncs.createElement(setNode, Project.LINK_NODE, String.valueOf(setItems[2]));
		int link_id = this.getConnectionId(String.valueOf(setItems[2]));
		if (link_id == -1) {
			// have no such item yet.
			link_id = this.getMaxLinkId() + 1;
		}
		XMLFuncs.setAttribute(o2, "link_id", String.valueOf(link_id));

		if (setNode != null)
			XMLFuncs.writeXML(this.getFilePath(), this.getDoc());
	}

	public int getMaxSetId() {
		List<Element> sets = new ArrayList<Element>(XMLFuncs.getXPATHNodes(this.getRootNode() + "/content/set", this.getDoc()));
		int result = 0;
		if (sets.size() == 0)
			return result;
		else {
			for(int i = 0; i < sets.size(); i++) {
				Element tmpEl = sets.get(i);
				String tmpAttr = XMLFuncs.getAttribute(tmpEl.getUniquePath(), this.getDoc(), "set_id");
				if (tmpAttr != null)
					if (Integer.parseInt(tmpAttr) > result)
						result = Integer.parseInt(tmpAttr);
			}
		}

		return result;
	}

	public int getMaxItemId() {
		List items = XMLFuncs.getXPATHNodes(this.getContentNode() + "/set/" + Project.SOURCE_NODE + "|" + this.getRootNode() + "/content/set/" + Project.TARGET_NODE, this.getDoc());

		int result = 0;
		if (items.size() == 0)
			return result;
		else {
			for(int i = 0; i < items.size(); i++) {
				Element tmpEl = (Element)items.get(i);
				String tmpAttr = XMLFuncs.getAttribute(tmpEl.getUniquePath(), this.getDoc(), "object_id");
				if (tmpAttr != null)
					if (Integer.parseInt(tmpAttr) > result)
						result = Integer.parseInt(tmpAttr);
			}
		}

		return result;
	}

	public int getMaxLinkId() {
		List<Element> links = new ArrayList<Element>(XMLFuncs.getXPATHNodes(this.getRootNode() + "/content/set/" + Project.LINK_NODE, this.getDoc()));
		int result = 0;
		if (links.size() == 0)
			return result;
		else {
			for(int i = 0; i < links.size(); i++) {
				Element tmpEl = links.get(i);
				String tmpAttr = XMLFuncs.getAttribute(tmpEl.getUniquePath(), this.getDoc(), "link_id");
				if (tmpAttr != null)
					if (Integer.parseInt(tmpAttr) > result)
						result = Integer.parseInt(tmpAttr);
			}
		}

		return result;
	}

	public void removeSet(int set_id) {
		XMLFuncs.selectSingleNode(this.getContentNode() + "/set[@set_id='" + set_id  + "']", this.getDoc()).detach();
		XMLFuncs.writeXML(this.getFilePath(), this.getDoc());
	}

	public void editSet(int set_id, Object[] data) {
		// editing object1
		Element el1 = this.getObject1(set_id);
		int item1_id = this.getObjectId(String.valueOf(data[0]));
		if (item1_id == -1) {
			// have no such item yet.
			item1_id = this.getMaxItemId() + 1;
		}
		el1.setText(String.valueOf(data[0]));
		XMLFuncs.setAttribute(el1, "object_id", String.valueOf(item1_id));

		// editing object2
		Element el2 = this.getObject2(set_id);
		int item2_id = this.getObjectId(String.valueOf(data[1]));
		if (item2_id == -1) {
			// have no such item yet.
			item2_id = this.getMaxItemId() + 1;
		}
		el2.setText(String.valueOf(data[1]));
		XMLFuncs.setAttribute(el2, "object_id", String.valueOf(item2_id));

		// editing connection
		Element link = this.getConnection(set_id);
		int link_id = this.getConnectionId(String.valueOf(data[2]));
		if (link_id == -1) {
			// have no such item yet.
			link_id = this.getMaxLinkId() + 1;
		}
		link.setText(String.valueOf(data[2]));
		XMLFuncs.setAttribute(link, "link_id", String.valueOf(link_id));

		XMLFuncs.writeXML(this.getFilePath(), this.getDoc());
	}

	public boolean renameItem(int itemId, String itemName) {
		// check on same-named items
		List sameNamedItems = XMLFuncs.getXPATHNodes(this.getContentNode() + "/set/" + Project.SOURCE_NODE + "[text()='" + itemName + "']" + "|" + this.getRootNode() + "/content/set/" + Project.TARGET_NODE + "[text()='" + itemName + "']", this.getDoc());
	
		if (sameNamedItems.size() > 0)
			return false;

		List items = XMLFuncs.getXPATHNodes(this.getContentNode() + "/set/" + Project.SOURCE_NODE + "[@object_id=" + itemId + "]" + "|" + this.getRootNode() + "/content/set/" + Project.TARGET_NODE + "[@object_id=" + itemId + "]", this.getDoc());
		for (int i = 0; i < items.size(); i++) {
			Element tmpEl = (Element)items.get(i);
			tmpEl.setText(itemName);
		}

		XMLFuncs.writeXML(this.getFilePath(), this.getDoc());

		return true;
	}

	public boolean renameLink(int linkId, String linkName) {
		// check on same-named items
		List sameNamedLinks = XMLFuncs.getXPATHNodes(this.getContentNode() + "/set/" + Project.LINK_NODE + "[text()='" + linkName + "']", this.getDoc());
	
		if (sameNamedLinks.size() > 0)
			return false;

		List links = XMLFuncs.getXPATHNodes(this.getContentNode() + "/set/" + Project.LINK_NODE + "[@link_id=" + linkId + "]", this.getDoc());
		for (int i = 0; i < links.size(); i++) {
			Element tmpEl = (Element)links.get(i);
			tmpEl.setText(linkName);
		}

		XMLFuncs.writeXML(this.getFilePath(), this.getDoc());

		return true;
	}

	public boolean removeItem(int itemId) {
		// check on same-named items
		List sameNamedItems = XMLFuncs.getXPATHNodes(this.getContentNode() + "/set/" + Project.SOURCE_NODE + "[@object_id='" + itemId + "']" + "|" + this.getRootNode() + "/content/set/" + Project.TARGET_NODE + "[@object_id='" + itemId + "']", this.getDoc());
	
		if (sameNamedItems.size() > 0)
			return false;	

		// TODO: подумать. По сути сейчас удалять итемы нельзя, если они есть в сетах.
		// т.е. можно только удалить сет со всеми итемами.
		return true;	
	}

	public boolean removeLink(int linkId) {
		// check on same-named items
		List sameNamedLinks = XMLFuncs.getXPATHNodes(this.getContentNode() + "/set/" + Project.LINK_NODE + "[@link_id='" + linkId + "']", this.getDoc());
	
		if (sameNamedLinks.size() > 0)
			return false;	

		// TODO: подумать. По сути сейчас удалять итемы нельзя, если они есть в сетах.
		// т.е. можно только удалить сет со всеми итемами.
		return true;	
	}

	public boolean renameProject(String oldName, String newName) {
		List projectsList = FSFuncs.getFileNamesOfDir(Common.PROJECTS_DATA_PATH, "xml", true);
		if (projectsList.contains(newName))
			return false; 

		if (FSFuncs.renameProjectFiles(oldName, newName)) {
			this.p_init(newName);
			XMLFuncs.setValueToNode(
									Project.ROOT_NODE + "/" + Project.META_NODE + "/name", 
									this.getDoc(), 
									this.getName()
								);
		
			XMLFuncs.writeXML(this.getFilePath(), this.getDoc());
		}


		return true;
	}

	private List getSetIdsOnObjectsInside(List objIds) {
		List result = new ArrayList();
		
		for (int i = 0; i < objIds.size(); i++) {
			String path = this.getContentNode() + "/set/" + Project.SOURCE_NODE + "[@object_id='" + objIds.get(i) + "']" + "|" + this.getContentNode() + "/set/" + Project.TARGET_NODE + "[@object_id='" + objIds.get(i) + "']";

			List<Element> els = XMLFuncs.getXPATHNodes(path, this.getDoc());
			
			for (int j = 0; j < els.size(); j++) {
				String value = els.get(j).getParent().attribute("set_id").getText();
				if (!result.contains(value))
					result.add(value);
			}
			
		}
		

		return result;
	}

	public List getGroups() {
		List groups = new ArrayList<List>();
		List items = this.getObjects();
		List setGroups = new ArrayList<List>();
		List usedItems = new ArrayList();

		// step 1. get groups of sets
		for (int i = 0; i < items.size(); i++) {
			Element tmpEl1 = (Element)items.get(i);
			Element tmpEl2 = (Element)items.get(++i);
			int tmpEl1Id = Integer.parseInt(XMLFuncs.getNodeAttribute(tmpEl1, this.getDoc(), "object_id"));
			int tmpEl2Id = Integer.parseInt(XMLFuncs.getNodeAttribute(tmpEl2, this.getDoc(), "object_id"));
			List objIds = new ArrayList();
			
			objIds.add(tmpEl1Id);
			objIds.add(tmpEl2Id);

			setGroups.add(this.getSetIdsOnObjectsInside(objIds));
		}

		// step 2. по всем группам
		List resultSetGroups = new ArrayList();
		while (setGroups.size() > 0) {
			// смысл такой. берем первый сет и удаляем его из исходного	
			List tmpG = (List)setGroups.get(0);
			setGroups.remove(0);
			// бежим по всем эл-там выбранного сета
			for (int m = 0; m < tmpG.size(); m++) {
				// и снова по всем сетам
				for (int i = 0; i < setGroups.size(); i++) {
					List tmpG2 = (List)setGroups.get(i);
					// если находим в сете элемент из 1го сета - дописываем к 1му сету.
					// а найденный - удаляем
					if (tmpG2.contains(tmpG.get(m))) {
						tmpG.addAll(tmpG2);
						setGroups.remove(tmpG2);
					}	
				}
			}
			// записываем в общий массив уникальные значения сетов
			resultSetGroups.add(Common.getUniqueList(tmpG));
		}
		return this.sortGroups(resultSetGroups);
	}

	/**
	 * @desc Возвращает список групп, отсортированных по возрастанию числа элементов
	 * @return List
	 */
	public List sortGroups(List unsortedGroups) {
		List result = new ArrayList<List>();
		int tmpSize = 0;
		List toAdd = new ArrayList();
		int indexToDelete = -1;
			
		while (unsortedGroups.size() > 0) {
			//ListIterator li = unsortedGroups.listIterator();
			indexToDelete = -1;
			for (Object groupObject : unsortedGroups) {
				List group = (List)groupObject;
				if (group.size() > tmpSize) {
					tmpSize = group.size();
					toAdd = group;
					indexToDelete = unsortedGroups.indexOf(group);
				}
				
			}
			
			if (indexToDelete != -1) {
				unsortedGroups.remove(indexToDelete);
				result.add(toAdd);
				tmpSize = 0;
			}
			
		}

		Collections.reverse(result);
		return result;
	}

	public List getObjectsOfSet(Object set) {
		List objects = new ArrayList();

			String tmp1 = String.valueOf(set);
			int tmp2 = Integer.parseInt(tmp1);
			objects.add(this.getObject1(tmp2).attribute("object_id").getText());
			objects.add(this.getObject2(tmp2).attribute("object_id").getText());
		

		return objects;
	}

	public Element getSetOnId(int set_id) {
		return (Element)XMLFuncs.selectSingleNode(this.getContentNode() + "/set[@set_id='" + set_id  + "']", this.getDoc());
	}

	public List getSetsOnIds(List set_ids) {
		List result = new ArrayList();
		for (int i = 0; i < set_ids.size(); i++) {
			result.add(
						this.getSetOnId(
											Integer.parseInt(
													String.valueOf(
																	set_ids.get(i)
																)
															)
										)
			);
		}

		return result;
	}

}