package com.vdbs;

import java.awt.*; 
import java.applet.*; 
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.Set;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import javax.swing.event.*;
import java.util.ArrayList;


public class ProjectListPage extends UserInterface implements ActionListener, ListSelectionListener {
	//public DBFuncs dbf = null;
	public JTable jtable;
    final static int _PROJECTNUMBERCELL = 0;
    final static int _PROJECTNAMECELL = 1;
    private int _selectedRow;
    private String _selectedProjectName;
    private List<String> contextMenuItems = new ArrayList();
    private List projects = new ArrayList();
    public ProjectListPage() {

        this.getContextMenuItems().add("Переименовать");
        this.getContextMenuItems().add("Просмотр..");
        this.getContextMenuItems().add("Импорт");
        this.getContextMenuItems().add("Подробнее..");
        this.getContextMenuItems().add("Удалить");

        this.setProjects(FSFuncs.getFileNamesOfDir(Common.PROJECTS_DATA_PATH, "xml", true));
        
        if (this.getProjects() == null) {
            UserInterface.clear();
            new IndexPage();
            return;
        }

		String[] tblColNames = {
									"№",
			                        "Проект"
			                    };
        
		int pListSize = this.getProjects().size();
		Object[][] tblData = new Object[pListSize][pListSize];
       	for (int i = 0; i < pListSize; i++) {
            String[] tmpRow = { String.valueOf(i), String.valueOf(this.getProjects().get(i)) };
            tblData[i] = tmpRow;
        }

        JPanel mainContainer = new JPanel();
        this.jtable = this.createScrollableTable(tblData, tblColNames, mainContainer);
        this.recalculateRowNums(this.jtable, this._PROJECTNUMBERCELL);
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
                if (rowindex < 0)
                    return;

                if (e.getComponent() instanceof JTable) {
                    ActionListener menuListener = new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (e.getActionCommand().equals("Переименовать")) {
                                renameProject();
                            }
                            if (e.getActionCommand().equals("Просмотр..")) {
                                buildView();
                            }
                            if (e.getActionCommand().equals("Подробнее..")) {
                                getDetails();
                            }
                            if (e.getActionCommand().equals("Удалить")) {
                                removeProject();
                            }
                            if (e.getActionCommand().equals("Импорт")) {
                                importProject();
                            }
                        }
                    };
                    JPopupMenu popup = UserInterface.createContextMenu(getContextMenuItems(), menuListener);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        
        });
        

        /**********/
        this.getContainer().add(mainContainer, BorderLayout.CENTER);
        this.getContainer().add(this.setHeader(), BorderLayout.NORTH);
        this.getContainer().add(this.setFooter(), BorderLayout.SOUTH);
        /**********/
    }


    public void valueChanged(ListSelectionEvent e) {  
        this.setSelectedRow(this.jtable.getSelectedRow());
        if (this.getSelectedRow() == -1)
            return;
        this.setSelectedProjectName(this.jtable.getValueAt(this.getSelectedRow(), this._PROJECTNAMECELL).toString());
    } 

	public void actionPerformed(ActionEvent e) {
       String cmd = e.getActionCommand();
        if (cmd.equals("Назад..")) {
            UserInterface.clear();
            new IndexPage();
        }
        else if (cmd.equals("Создать проект")) { 
            Object[] options = {"Ок", "Отмена"};
            JPanel dialogJp = new JPanel();
            dialogJp.setLayout(new BorderLayout());
            JLabel dialogJlabel = new JLabel("Введите название проекта:");
            JTextField dialogJtext = new JTextField("", 10);
            dialogJp.add(dialogJlabel, BorderLayout.NORTH);
            dialogJp.add(dialogJtext, BorderLayout.CENTER);
            int n = JOptionPane.showOptionDialog(UserInterface.baseJFrame,
                dialogJp,
                "Создание нового проекта",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]
            ); //default button title  

            if (n == 1)
                return;
            else {
                String pName = dialogJtext.getText().trim();
                if (!pName.equals("")) {
                    if (XMLFuncs.createProject(pName)) {
                        DefaultTableModel tm = (DefaultTableModel)this.jtable.getModel();
                        int nextIndex = tm.getRowCount() + 1;
                        tm.addRow(new Object[]{nextIndex , pName});
                        this.jtable.updateUI();
                        this.jtable.revalidate();
                        this.recalculateRowNums(this.jtable, this._PROJECTNUMBERCELL);
                    }
                }
                else {
                    Common.error("Ошибка", "Пожалуйста, выберите проект");
                    return;                   
                }
                
            }
        }
    }

    private void buildView() {
        if (this.getSelectedProjectName() != null) {
            /* ************** */
            this.getBi().getProject().p_init(this.getSelectedProjectName());
            /* ************** */
            Visual v = new Visual();
            v.paintItBlack();
        }
        else {
            Common.error("Ошибка", "Пожалуйста, выберите проект");
            return;
        }        
    }

    private void renameProject() {
        Object[] options = {"Ок", "Отмена"};
        JPanel dialogJp = new JPanel();
        dialogJp.setLayout(new BorderLayout());
        JLabel dialogJlabel = new JLabel("Введите новое название проекта:");
        JTextField dialogJtext = new JTextField(this.getSelectedProjectName(), 10);
        dialogJp.add(dialogJlabel, BorderLayout.NORTH);
        dialogJp.add(dialogJtext, BorderLayout.CENTER);
        int n = JOptionPane.showOptionDialog(UserInterface.baseJFrame,
            dialogJp,
            "Переименование проекта",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,     //do not use a custom Icon
            options,  //the titles of buttons
            options[0]
        ); //default button title  

        if (n == 1)
            return;
        else {
            String itemName = dialogJtext.getText().trim();
            if (!itemName.equals("")) {
                if (!itemName.equals(this.getSelectedProjectName()))
                    if (!this.getBi().getProject().renameProject(this.getSelectedProjectName(), itemName)) {
                        Common.error("Ошибка", "Имя проекта должно быть уникальным");
                        return;
                    }
                    else {
                        DefaultTableModel model = (DefaultTableModel) this.jtable.getModel();
                        model.setValueAt(itemName, this.getSelectedRow(), ItemsListPage.ITEM_NAME_COLUMN);
                        //UserInterface.clear();
                        //new ModerationPage();
                    }
            }
            else {
                Common.error("Ошибка", "Пожалуйста, выберите проект");
                return;                   
            }
        }      
    }

    private void getDetails() {
        if (this.getSelectedProjectName() != null) {
            /* ************** */
            this.getBi().getProject().p_init(this.getSelectedProjectName());
            /* ************** */
            UserInterface.clear();
            new ModerationPage();
        }
        else {
            Common.error("Ошибка", "Пожалуйста, выберите проект");
            return;
        }
    }

    private void removeProject() {
        if (this.getSelectedProjectName() != null) {
            if (FSFuncs.dropProject(this.getSelectedProjectName())) {
                DefaultTableModel tm = (DefaultTableModel)this.jtable.getModel();
                tm.removeRow(this.jtable.convertRowIndexToModel(this.getSelectedRow()));
                
                this.jtable.updateUI();
                this.jtable.revalidate();
                this.jtable.clearSelection();
                this.recalculateRowNums(this.jtable, this._PROJECTNUMBERCELL);
           }
        }
        else {
            Common.error("Ошибка", "Пожалуйста, выберите проект");
            return;
        }
    }

    private void importProject() {
        if (this.getSelectedProjectName() != null) {
            /* ************** */
            this.getBi().getProject().p_init(this.getSelectedProjectName());
            /* ************** */
            Importer i = new Importer();
            i.setDelimeter(" # "); // important. with spaces!
            i.setOrderList(Arrays.asList("target", "link", "source"));
            if (i.fileChooser()) {
                i.doImport();
            }

        }
        else {
            Common.error("Ошибка", "Пожалуйста, выберите проект");
            return;
        }
    }

    public String getSelectedProjectName() {
        return this._selectedProjectName;
    }

    public int getSelectedRow() {
        return this._selectedRow;
    }

    public void setSelectedProjectName(String val) {
        this._selectedProjectName = val;
    }

    public void setSelectedRow(int val) {
        this._selectedRow = val;
    }

    private List getContextMenuItems() {
        return this.contextMenuItems;
    }

    public List getProjects() {
        return this.projects;
    }

    public void setProjects(List val) {
        this.projects = val;
    }

    public JPanel setHeader() {
        JPanel header = new JPanel();
        header.add(new JLabel("<html><h2>Список проектов:</h2></html>"));

        return header;
    }

    public JPanel setFooter() {
        JPanel footer = super.setFooter();
        GridBagConstraints gbc = new GridBagConstraints();
        
        JButton btnCreate = new JButton("Создать проект");
        btnCreate.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        footer.add(btnCreate, gbc);

        JButton btnBack = new JButton("Назад..");
        btnBack.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 1;
        footer.add(btnBack, gbc);

        return footer;
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
        
        return tmpTable;
    }


}
