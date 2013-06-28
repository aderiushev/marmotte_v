package com.vdbs;

import java.awt.*; 
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.event.*;
import javax.swing.UIManager.*;

public class ItemsListPage extends UserInterface implements ActionListener, ListSelectionListener {

    private JFrame jframeILP;
    private JPanel containerILP;
    private JTable jtable;
    private int selectedItemId;
    private int selectedRow;
    final static int SIZE_WIDTH         = 250;
    final static int SIZE_HEIGHT        = 450;
    final static int ITEM_ID_COLUMN     = 0;
    final static int ITEM_NAME_COLUMN   = 1;

    private List<String> contextMenuItems = new ArrayList();


    public ItemsListPage() {
        this.setJFrameILP(new JFrame("Обьекты проекта: " + this.getBi().getProject().getName()));
        this.getJFrameILP().setSize(ItemsListPage.SIZE_WIDTH, ItemsListPage.SIZE_HEIGHT);

        this.setContainerILP(new JPanel());
        this.getContainerILP().setLayout(new BorderLayout());

        this.getJFrameILP().add(this.getContainerILP());

        this.getJFrameILP().setVisible(true);

        this.getContextMenuItems().add("Переименовать");
        this.getContextMenuItems().add("Удалить");

        String[] tblColNames = {
                                    "ID обьекта",
                                    "Название"
                                };

        List items = this.getBi().getProject().getObjects();


        List usedItemIds = new ArrayList();
        int itemsSize = items.size();
        Object[][] tblData = new Object[itemsSize][itemsSize];
        // UGLY START
        // getting unique values of array
        int j = 0;
        for (int i = 0; i < itemsSize; i++) {
            String tmpId = String.valueOf(this.getBi().getProject().getNodeAttribute(items.get(i), "object_id"));
            if (!usedItemIds.contains(tmpId)) {
                String[] tmpRow = { tmpId, String.valueOf(this.getBi().getProject().getNodeText(items.get(i))) };
                tblData[j++] = tmpRow;
                usedItemIds.add(tmpId);
            }
        }

        Object[][] filteredTblData = new Object[j][j];
        for (int k = 0; k < j; k++) {
            filteredTblData[k] = tblData[k];
        }
        // UGLY END
        JPanel tblContainer = new JPanel();
        this.setJTable(this.createScrollableTable(filteredTblData, tblColNames, tblContainer));

        this.getContainerILP().add(tblContainer, BorderLayout.CENTER);
        this.getContainerILP().add(this.setFooter(), BorderLayout.SOUTH);

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
                                renameItem();
                            }
                            if (e.getActionCommand().equals("Удалить")) {
                                removeItem();
                            }

                        }
                    };
                    JPopupMenu popup = UserInterface.createContextMenu(getContextMenuItems(), menuListener);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        
        });
        

    }

    private void removeItem() {
        Object[] options = {"Ок", "Отмена"};

        int n = JOptionPane.showOptionDialog(this.getJFrameILP(),
            new JLabel("Вы действительно хотите удалить обьект: " + this.getJTable().getValueAt(this.getSelectedRow(), ItemsListPage.ITEM_NAME_COLUMN).toString() + "?"),
            "Удаление обьекта",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,     //do not use a custom Icon
            options,  //the titles of buttons
            options[0]
        ); //default button title  

        if (n == 1)
            return;
        else {
            if (!this.getBi().getProject().removeItem(this.getSelectedItemId())) {
                Common.error("Ошибка", "У обьекта есть связи. Удаление невозможно.");
                return;
            }
            else {
                DefaultTableModel model = (DefaultTableModel) this.getJTable().getModel();
                model.removeRow(this.getSelectedRow());
            }
        }   
    }  

    private void renameItem() {
        Object[] options = {"Ок", "Отмена"};
        JPanel dialogJp = new JPanel();
        dialogJp.setLayout(new BorderLayout());
        JLabel dialogJlabel = new JLabel("Введите новое название обьекта:");
        JTextField dialogJtext = new JTextField(this.getJTable().getValueAt(this.getSelectedRow(), ItemsListPage.ITEM_NAME_COLUMN).toString(), 10);
        dialogJp.add(dialogJlabel, BorderLayout.NORTH);
        dialogJp.add(dialogJtext, BorderLayout.CENTER);
        int n = JOptionPane.showOptionDialog(this.getJFrameILP(),
            dialogJp,
            "Переименование обьекта",
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
                if (!itemName.equals(this.getJTable().getValueAt(this.getSelectedRow(), ItemsListPage.ITEM_NAME_COLUMN).toString()))
                    if (!this.getBi().getProject().renameItem(this.getSelectedItemId(), itemName)) {
                        Common.error("Ошибка", "Обьект с таким названием уже существует.");
                        return;
                    }
                    else {
                        DefaultTableModel model = (DefaultTableModel) this.getJTable().getModel();
                        model.setValueAt(itemName, this.getSelectedRow(), ItemsListPage.ITEM_NAME_COLUMN);
                        UserInterface.clear();
                        new ModerationPage();
                    }
            }
            else {
                Common.error("Ошибка", "Не заполнено название обьекта.");
                return;                   
            }
        }      
    }

    public void valueChanged(ListSelectionEvent e) {  
        this.setSelectedRow(this.getJTable().getSelectedRow());
        if (this.getSelectedRow() == -1)
            return;

       this.setSelectedItemId(Integer.parseInt(this.getJTable().getValueAt(this.getSelectedRow(), ItemsListPage.ITEM_ID_COLUMN).toString()));
    } 

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("Создать обьект")) {;
           Object[] options = {"Ок", "Отмена"};
            JPanel dialogJp = new JPanel();
            dialogJp.setLayout(new GridLayout(2, 1));
            JLabel oNameLabel = new JLabel("Введите название обьекта:");
            JTextField oNameTF = new JTextField("", 10);
            dialogJp.add(oNameLabel);
            dialogJp.add(oNameTF);

            int n = JOptionPane.showOptionDialog(UserInterface.baseJFrame,
                dialogJp,
                "Создание нового обьекта",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]
            ); //default button title    

            if (n == 0) {
                if (!oNameTF.getText().equals("")) {
                    this.getBi().getProject().getTempObjectNames().add(oNameTF.getText());
                    DefaultTableModel model = (DefaultTableModel) this.getJTable().getModel();
                    model.addRow(new Object[]{ -1, oNameTF.getText() });
                }
            }   

            return;      
        }

    }

    public void setJFrameILP(JFrame val) {
        this.jframeILP = val;
    }

    public JFrame getJFrameILP() {
        return this.jframeILP;
    }

    public void setContainerILP(JPanel val) {
        this.containerILP = val;
    }

    public JPanel getContainerILP() {
        return this.containerILP;
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

    public int getSelectedItemId() {
        return this.selectedItemId;
    }

    public int getSelectedRow() {
        return this.selectedRow;
    }

    public void setSelectedItemId(int val) {
        this.selectedItemId = val;
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

        JButton addItemButton = new JButton("Создать обьект");
        addItemButton.addActionListener(this);
        footer.add(addItemButton, gbc);

        return footer;
    }



}