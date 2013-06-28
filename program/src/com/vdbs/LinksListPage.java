package com.vdbs;

import java.awt.*; 
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.event.*;
import javax.swing.UIManager.*;

public class LinksListPage extends UserInterface implements ActionListener, ListSelectionListener {

    private JFrame jframeLLP;
    private JPanel containerLLP;
    private JTable jtable;
    private int selectedLinkId;
    private int selectedRow;
    final static int SIZE_WIDTH         = 250;
    final static int SIZE_HEIGHT        = 450;
    final static int LINK_ID_COLUMN     = 0;
    final static int LINK_NAME_COLUMN   = 1;

    private List<String> contextMenuItems = new ArrayList();


    public LinksListPage() {
        this.setJFrameLLP(new JFrame("Связи проекта: " + this.getBi().getProject().getName()));
        this.getJFrameLLP().setSize(LinksListPage.SIZE_WIDTH, LinksListPage.SIZE_HEIGHT);

        this.setContainerLLP(new JPanel());
        this.getContainerLLP().setLayout(new BorderLayout());

        this.getJFrameLLP().add(this.getContainerLLP());

        this.getJFrameLLP().setVisible(true);

        this.getContextMenuItems().add("Переименовать");
        this.getContextMenuItems().add("Удалить");

        String[] tblColNames = {
                                    "ID связи",
                                    "Название"
                                };

        List links = this.getBi().getProject().getConnections();


        List usedLinksIds = new ArrayList();
        int linksSize = links.size();
        Object[][] tblData = new Object[linksSize][linksSize];
        // UGLY START
        // getting unique values of array
        int j = 0;
        for (int i = 0; i < linksSize; i++) {
            String tmpId = String.valueOf(this.getBi().getProject().getNodeAttribute(links.get(i), "link_id"));
            if (!usedLinksIds.contains(tmpId)) {
                String[] tmpRow = { tmpId, String.valueOf(this.getBi().getProject().getNodeText(links.get(i))) };
                tblData[j++] = tmpRow;
                usedLinksIds.add(tmpId);
            }
        }

        Object[][] filteredTblData = new Object[j][j];
        for (int k = 0; k < j; k++) {
            filteredTblData[k] = tblData[k];
        }
        // UGLY END
        JPanel tblContainer = new JPanel();
        this.setJTable(this.createScrollableTable(filteredTblData, tblColNames, tblContainer));

        this.getContainerLLP().add(tblContainer, BorderLayout.CENTER);
        this.getContainerLLP().add(this.setFooter(), BorderLayout.SOUTH);

        ListSelectionModel selectionModel = this.getJTable().getSelectionModel();
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
                                renameLink();
                            }
                            if (e.getActionCommand().equals("Удалить")) {
                                removeLink();
                            }

                        }
                    };
                    JPopupMenu popup = UserInterface.createContextMenu(getContextMenuItems(), menuListener);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        
        });
        

    }

    private void removeLink() {
        Object[] options = {"Ок", "Отмена"};
        int n = JOptionPane.showOptionDialog(this.getJFrameLLP(),
            new JLabel("Вы действительно хотите удалить связь: " + this.getJTable().getValueAt(this.getSelectedRow(), LinksListPage.LINK_NAME_COLUMN).toString() + "?"),
            "Удаление связи",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,     //do not use a custom Icon
            options,  //the titles of buttons
            options[0]
        ); //default button title  

        if (n == 1)
            return;
        else {
            if (!this.getBi().getProject().removeLink(this.getselectedLinkId())) {
                Common.error("Ошибка", "У связи есть обьекты. Удаление невозможно.");
                return;
            }
            else {
                DefaultTableModel model = (DefaultTableModel) this.getJTable().getModel();
                model.removeRow(this.getSelectedRow());
            }
        }   
    }  

    private void renameLink() {
        Object[] options = {"Ок", "Отмена"};
        JPanel dialogJp = new JPanel();
        dialogJp.setLayout(new BorderLayout());
        JLabel dialogJlabel = new JLabel("Введите новое названиен связи:");
        JTextField dialogJtext = new JTextField(this.getJTable().getValueAt(this.getSelectedRow(), LinksListPage.LINK_NAME_COLUMN).toString(), 10);
        dialogJp.add(dialogJlabel, BorderLayout.NORTH);
        dialogJp.add(dialogJtext, BorderLayout.CENTER);
        int n = JOptionPane.showOptionDialog(this.getJFrameLLP(),
            dialogJp,
            "Переименование связи",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,     //do not use a custom Icon
            options,  //the titles of buttons
            options[0]
        ); //default button title  

        if (n == 1)
            return;
        else {
            String linkName = dialogJtext.getText().trim();
            if (!linkName.equals("")) {
                if (!linkName.equals(this.getJTable().getValueAt(this.getSelectedRow(), LinksListPage.LINK_NAME_COLUMN).toString()))
                    if (!this.getBi().getProject().renameLink(this.getselectedLinkId(), linkName)) {
                        Common.error("Ошибка", "Связь с таким названием уже существует.");
                        return;
                    }
                    else {
                        DefaultTableModel model = (DefaultTableModel) this.getJTable().getModel();
                        model.setValueAt(linkName, this.getSelectedRow(), LinksListPage.LINK_NAME_COLUMN);
                        UserInterface.clear();
                        new ModerationPage();
                    }
            }
            else {
                Common.error("Ошибка", "Не запонено название связи.");
                return;                   
            }
        }      
    }

    public void valueChanged(ListSelectionEvent e) {  
        this.setSelectedRow(this.getJTable().getSelectedRow());
        if (this.getSelectedRow() == -1)
            return;

       this.setselectedLinkId(Integer.parseInt(this.getJTable().getValueAt(this.getSelectedRow(), LinksListPage.LINK_ID_COLUMN).toString()));
    } 

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("Создать связь")) {;
           Object[] options = {"Ок", "Отмена"};
            JPanel dialogJp = new JPanel();
            dialogJp.setLayout(new GridLayout(2, 1));
            JLabel oNameLabel = new JLabel("Введите название связи:");
            JTextField oNameTF = new JTextField("", 10);
            dialogJp.add(oNameLabel);
            dialogJp.add(oNameTF);

            int n = JOptionPane.showOptionDialog(UserInterface.baseJFrame,
                dialogJp,
                "Создание новой связи",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]
            ); //default button title    

            if (n == 0) {
                if (!oNameTF.getText().equals("")) {
                    this.getBi().getProject().getTempLinkNames().add(oNameTF.getText());
                    DefaultTableModel model = (DefaultTableModel) this.getJTable().getModel();
                    model.addRow(new Object[]{ -1, oNameTF.getText() });
                }
            }   

            return;      
        }

    }

    public void setJFrameLLP(JFrame val) {
        this.jframeLLP = val;
    }

    public JFrame getJFrameLLP() {
        return this.jframeLLP;
    }

    public void setContainerLLP(JPanel val) {
        this.containerLLP = val;
    }

    public JPanel getContainerLLP() {
        return this.containerLLP;
    }

    public void setJTable(JTable val) {
        this.jtable = val;
    }

    public JTable getJTable() {
        return this.jtable;
    }

    private List getContextMenuItems() {
        return this.contextMenuItems;
    }

    public int getselectedLinkId() {
        return this.selectedLinkId;
    }

    public int getSelectedRow() {
        return this.selectedRow;
    }

    public void setselectedLinkId(int val) {
        this.selectedLinkId = val;
    }

    public void setSelectedRow(int val) {
        this.selectedRow = val;
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

        ListSelectionModel selectionModel = tmpTable.getSelectionModel();  
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  
        
        JScrollPane scrollPane = new JScrollPane(tmpTable);
        scrollPane.setPreferredSize(new Dimension(250, 400));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(20, 0));


        container.add(scrollPane);

        container.setPreferredSize(new Dimension(250,400));
        container.updateUI();
        
        UserInterface.baseJFrame.setVisible(true);

        return tmpTable;
    }

    @Override
    public JPanel setFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;  
        
        JButton addLinkButton = new JButton("Создать связь");
        addLinkButton.addActionListener(this);
        footer.add(addLinkButton, gbc);

        return footer;
    }



}