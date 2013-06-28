package com.vdbs;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.*; 
import java.util.List;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class ModerationGroupPage extends UserInterface implements ActionListener, ListSelectionListener {
    private Object[] connectionNames    = null;
    private Object[] objectNames        = null;

    private JComboBox item1Combo        = null;
    private JComboBox item2Combo        = null;
    private JComboBox linkCombo         = null;
    final static int SET_ID_COLUMN      = 0;
    final static int ITEM_1_COLUMN      = 1;
    final static int ITEM_2_COLUMN      = 2;
    final static int CONNECTION_COLUMN  = 3;
    private int selectedRow;
    private int selectedSet;
    private List groups                 = new ArrayList();
    private boolean allowShowPopup      = true;

    private JTable jtable;

    private List<String> contextMenuItems = new ArrayList();


    public ModerationGroupPage(List groups) {
        this.setGroups(groups);
        this.getContextMenuItems().add("Редактировать");
        this.getContextMenuItems().add("Удалить");
        this.getContextMenuItems().add("Просмотр..");

        String[] tblColNames = {
                                    "ID набора",
                                    "Обьект 1 (источник)",
                                    "Обьект 2",
                                    "Связь"
                                };

        int size = -1 + this.getGroups().size() + this.getBi().getProject().getSets().size();
        Object[][] tblData = new Object[size][size];
        int k = 0;
        for (int i = 0; i < this.getGroups().size(); i++) {
            List sets = this.getBi().getProject().getSetsOnIds((List)this.getGroups().get(i));
            for (int j = 0; j < sets.size(); j++) {
      
                int set_id = Integer.parseInt(this.getBi().getProject().getNodeAttribute(sets.get(j), "set_id"));

                String[] tmpRow = { 
                                        String.valueOf(set_id), 
                                        this.getBi().getProject().getObject1(set_id).getText(), 
                                        this.getBi().getProject().getObject2(set_id).getText(), 
                                        this.getBi().getProject().getConnection(set_id).getText() 
                                    };
                tblData[k++] = tmpRow;
            }

            if (k + 1 < size)
                tblData[k++] = new String[]{"", "", "", ""};
        }

        JPanel tblContainer = new JPanel();
        this.jtable = this.createScrollableTable(tblData, tblColNames, tblContainer);

        this.getContainer().add(tblContainer, BorderLayout.CENTER);
        this.getContainer().add(this.setHeader(), BorderLayout.NORTH);
        this.getContainer().add(this.setFooter(), BorderLayout.SOUTH);

        ListSelectionModel selectionModel = this.jtable.getSelectionModel();
        selectionModel.addListSelectionListener(this); 


       // Setting Popup Menu
        this.jtable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = jtable.rowAtPoint(e.getPoint());
                if (r >= 0 && r < jtable.getRowCount()) {
                    jtable.setRowSelectionInterval(r, r);
                } else {
                    jtable.clearSelection();
                }
                
                int rowindex = jtable.getSelectedRow();
                if (rowindex < 0 || !getAllowShowPopup())
                    return;

                if (e.getComponent() instanceof JTable) {
                    ActionListener menuListener = new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (e.getActionCommand().equals("Удалить")) {
                                removeSet();
                            }
                            if (e.getActionCommand().equals("Редактировать")) {
                                editSet();
                            }
                            if (e.getActionCommand().equals("Просмотр..")) {
                                buildGroupView();
                            }

                        }
                    };
                    JPopupMenu popup = UserInterface.createContextMenu(getContextMenuItems(), menuListener);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
    }

    private void removeSet() {
        this.getBi().getProject().removeSet(this.getSelectedSet());
        // TODO: remove correctly without creating new instance
        UserInterface.clear();
        new ModerationGroupPage(this.getBi().getProject().getGroups());
    }

    private void editSet() {
        this.setObjectNames(this.getBi().getProject().getObjectNames(this.getBi().getProject().getTempObjectNames()));
        this.setConnectionNames(this.getBi().getProject().getConnectionNames(this.getBi().getProject().getTempLinkNames()));

        String item1 = this.jtable.getValueAt(this.getSelectedRow(), this.ITEM_1_COLUMN).toString();
        String item2 = this.jtable.getValueAt(this.getSelectedRow(), this.ITEM_2_COLUMN).toString();
        String link = this.jtable.getValueAt(this.getSelectedRow(), this.CONNECTION_COLUMN).toString();

        boolean errorFlag = true;
        Object[] chosen = null;
        while(errorFlag) {

            chosen = this.showEditSetDialog(this.getObjectNames(), this.getConnectionNames(), item1, item2, link);
            
            if (chosen == null)
                return;

            String o1 = String.valueOf(chosen[0]);
            String o2 = String.valueOf(chosen[1]);
            String c1 = String.valueOf(chosen[2]);

            if (o1.equals("Выберите..") || o2.equals("Выберите..") || c1.equals("Выберите..")) {
                Common.error("Ошибка", "Поля не могут быть пустыми, пожалуйста, заполните");
                continue;
            }

            if (o1.equals(o2)) {
                Common.error("Ошибка", "Одинаковые обьекты использоваться не могут");
                continue;
            }

            if (this.getBi().getProject().checkExist(o1, o2, c1)) {
                Common.error("Ошибка", "Такой набор уже есть в базе");
                continue;
            }

            errorFlag = false;
        }

        // if we r here - all is correct and nice
        this.getBi().getProject().editSet(this.getSelectedSet(), chosen);
        // reload all (maybe heavy but 100% works)
        // TODO: rename correctly without creating new instance
        UserInterface.clear();
        new ModerationGroupPage(this.getBi().getProject().getGroups());
        
    }



    public void valueChanged(ListSelectionEvent e) {  
        this.setSelectedRow(this.jtable.getSelectedRow());
        if (this.getSelectedRow() == -1)
            return;

        String ss = this.jtable.getValueAt(this.getSelectedRow(), this.SET_ID_COLUMN).toString();
        if (ss.equals("")) {
            this.setAllowShowPopup(false);
            return;
        }

        this.setSelectedSet(Integer.parseInt(ss));
        this.setAllowShowPopup(true);
    } 

    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand();
        if (cmd.equals("Разбить на группы")) {
            UserInterface.clear();
            new ModerationPage();
        }
        if (cmd.equals("Назад..")) {
            UserInterface.clear();
            new ProjectListPage();
        }
        else if (cmd.equals("Список обьектов..")) {
            new ItemsListPage();
        }
        else if (cmd.equals("Список связей..")) {
            new LinksListPage();
        }
        else if (cmd.equals("Создать набор")) {

            this.setObjectNames(this.getBi().getProject().getObjectNames(this.getBi().getProject().getTempObjectNames()));
            this.setConnectionNames(this.getBi().getProject().getConnectionNames(this.getBi().getProject().getTempLinkNames()));

            
            boolean errorFlag = true;
            Object[] chosen = null;
            while(errorFlag) {

                chosen = this.showAddSetDialog(this.getObjectNames(), this.getConnectionNames());
                
                if (chosen == null)
                    return;

                String o1 = String.valueOf(chosen[0]);
                String o2 = String.valueOf(chosen[1]);
                String c1 = String.valueOf(chosen[2]);

                if (o1.equals("Выберите..") || o2.equals("Выберите..") || c1.equals("Выберите..")) {
                    Common.error("Ошибка", "Поля не могут быть пустыми, пожалуйста, заполните");
                    continue;
                }
 
                if (o1.equals(o2)) {
                    Common.error("Ошибка", "Обьект не может ссылаться сам на себя");
                    continue;
                }

                if (this.getBi().getProject().checkExist(o1, o2, c1)) {
                    Common.error("Ошибка", "Такой набор уже есть в базе");
                    continue;
                }

                errorFlag = false;
            }
            
            // if we r here - all is correct and nice
            this.getBi().getProject().addSet(chosen);
            // reload all (maybe heavy but 100% works)
            UserInterface.clear();
            new ModerationGroupPage(this.getBi().getProject().getGroups());
        }
    }

    public String[] showAddSetDialog(Object[] objectNames, Object[] connectionNames) {
        Object[] options = {"Ок", "Отмена"};
        JPanel dialogJp = new JPanel();
        dialogJp.setLayout(new GridLayout(5, 2));
        item1Combo = new JComboBox(objectNames);
        item2Combo = new JComboBox(objectNames);
        linkCombo = new JComboBox(connectionNames);

        dialogJp.add(new JLabel("Обьект 1 (источник):"));
        dialogJp.add(item1Combo);
        dialogJp.add(new JLabel("Обьект 2:"));
        dialogJp.add(item2Combo);
        dialogJp.add(new JLabel("Связь:"));
        dialogJp.add(linkCombo);
/*
        JButton plusItemBtn = new JButton("+ Object");
        dialogJp.add(plusItemBtn);
        plusItemBtn.addActionListener(this);
        
        JButton plusLinkBtn = new JButton("+ Link");
        dialogJp.add(plusLinkBtn);
        plusLinkBtn.addActionListener(this);
*/
        dialogJp.setPreferredSize(new Dimension(300, 80));
        int n = JOptionPane.showOptionDialog(UserInterface.baseJFrame,
            dialogJp,
            "Создание нового набора",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,     //do not use a custom Icon
            options,  //the titles of buttons
            options[0]
        ); //default button title  

        if (n == 0) {
            String[] result = { 
                            String.valueOf(item1Combo.getSelectedItem()),
                            String.valueOf(item2Combo.getSelectedItem()),
                            String.valueOf(linkCombo.getSelectedItem())
                        };   
            return result;
        } 
        else
            return null;
    }

    public String[] showEditSetDialog(Object[] objectNames, Object[] connectionNames, String chosen_item1, String chosen_item2, String chosen_connection) {
        Object[] options = {"Ок", "Отмена"};
        JPanel dialogJp = new JPanel();
        dialogJp.setLayout(new GridLayout(5, 2));
        item1Combo = new JComboBox(objectNames);
        item2Combo = new JComboBox(objectNames);
        linkCombo = new JComboBox(connectionNames);

        dialogJp.add(new JLabel("Обьект 1 (источник)"));
        dialogJp.add(item1Combo);
        item1Combo.setSelectedItem(chosen_item1);
        
        dialogJp.add(new JLabel("Обьект 2"));
        dialogJp.add(item2Combo);
        item2Combo.setSelectedItem(chosen_item2);

        dialogJp.add(new JLabel("Связь"));
        dialogJp.add(linkCombo);
        linkCombo.setSelectedItem(chosen_connection);

        dialogJp.setPreferredSize(new Dimension(300, 80));
        int n = JOptionPane.showOptionDialog(UserInterface.baseJFrame,
            dialogJp,
            "Редактирование набора",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,     //do not use a custom Icon
            options,  //the titles of buttons
            options[0]
        ); //default button title  

        if (n == 0) {
            String[] result = { 
                            String.valueOf(item1Combo.getSelectedItem()),
                            String.valueOf(item2Combo.getSelectedItem()),
                            String.valueOf(linkCombo.getSelectedItem())
                        };   
            return result;
        } 
        else
            return null;
    }


    private void updateItemCombos(Object[] items) {
        this.getItem1Combo().removeAllItems();
        this.getItem2Combo().removeAllItems();

        for (int i = 0; i < items.length; i++) {
           this.getItem1Combo().addItem(items[i]); 
           this.getItem2Combo().addItem(items[i]);
        }

        this.getItem1Combo().updateUI();
        this.getItem2Combo().updateUI();
    }

    private void updateLinkCombo(Object[] items) {
        this.getLinkCombo().removeAllItems();

        for (int i = 0; i < items.length; i++) {
           this.getLinkCombo().addItem(items[i]);
        }

        this.getLinkCombo().updateUI();
    }

    // GETTERS & SETTERS

    public JComboBox getItem1Combo() {
        return this.item1Combo;
    }

    public JComboBox getItem2Combo() {
        return this.item2Combo;
    }

    public JComboBox getLinkCombo() {
        return this.linkCombo;
    }

    public void setObjectNames(Object[] val) {
        this.objectNames = val;
    }

    public void setConnectionNames(Object[] val) {
        this.connectionNames = val;
    }

    public Object[] getObjectNames() {
        return this.objectNames;
    }

    public Object[] getConnectionNames() {
        return this.connectionNames;
    }


    public int getSelectedSet() {
        return this.selectedSet;
    }

    public int getSelectedRow() {
        return this.selectedRow;
    }

    public void setSelectedSet(int val) {
        this.selectedSet = val;
    }

    public void setSelectedRow(int val) {
        this.selectedRow = val;
    }

    public JPanel setHeader() {
        JPanel header = new JPanel();
        header.add(new JLabel("<html><h2>Наборы проекта: " + this.getBi().getProject().getName() + " project:</h2></html>"));
        
        return header;
    }

    private List getContextMenuItems() {
        return this.contextMenuItems;
    }

    public void setGroups(List val) {
        this.groups = val;
    }

    public List getGroups() {
        return this.groups;
    }

    public void setAllowShowPopup(boolean val) {
        this.allowShowPopup = val;
    }

    public boolean getAllowShowPopup() {
        return this.allowShowPopup;
    }

    public JPanel setFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;        
        JCheckBox groupCheckbox = new JCheckBox("Разбить на группы");
        groupCheckbox.addActionListener(this);
        footer.add(groupCheckbox, gbc);

        JButton btnCreate = new JButton("Создать набор");
        btnCreate.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 1;
        footer.add(btnCreate, gbc);

        JButton btnAddObject = new JButton("Список обьектов..");
        btnAddObject.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 1;
        footer.add(btnAddObject, gbc);

      
        JButton btnAddLink = new JButton("Список связей..");
        btnAddLink.addActionListener(this);
        gbc.gridx = 2;
        gbc.gridy = 1;
        footer.add(btnAddLink, gbc);

        JButton btnBack = new JButton("Назад..");
        btnBack.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 2;
        footer.add(btnBack, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        footer.add(new JLabel("ver. " + Common.VERSION + " | 2013"), gbc);

        return footer;
    }

    private void buildGroupView() {
        String targetSet = String.valueOf(this.getSelectedSet());
        List result = new ArrayList();

        for (int i = 0; i < this.getGroups().size(); i++) {
            List tmpList = (List)this.getGroups().get(i);
            if (tmpList.contains(targetSet)) {
                result = (List)this.getGroups().get(i);
                break;
            }
        }

        Visual v = new Visual();
        v.setData(this.getBi().getProject().getSetsOnIds(result));
        v.paintItBlack();
       
    }

    @Override
    public JTable createScrollableTable(Object[][] tblData, String[] tblColNames, JPanel container) {
        if (container == null) {
            container = new JPanel();
        }
        int rowIndexSelected;
        DefaultTableModel tm = new DefaultTableModel(tblData, tblColNames);
        JTable tmpTable = new JTable(tm) {
            int rowIndexSelected;
            // Overriding JTable method
            public boolean isCellEditable(int rowIndex, int colIndex) {
                //_HEIGHT cancel editing at all
                return false;
            }

        };


        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        ListSelectionModel selectionModel = tmpTable.getSelectionModel();  
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    
        
        JScrollPane scrollPane = new JScrollPane(tmpTable);
        scrollPane.setMaximumSize(new Dimension(400, 200));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(20, 0));

        container.add(scrollPane);


        container.setPreferredSize(new Dimension(400,200));

        return tmpTable;
    }


}

    
