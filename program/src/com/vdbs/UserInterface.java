package com.vdbs;

import java.awt.*; 
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.List;
import javax.swing.event.*;
import javax.swing.UIManager.*;

public class UserInterface extends Base implements ActionListener {
    public static JFrame baseJFrame;
    public static JPopupMenu contextMenu;
    private static JPanel container;


    public UserInterface() {
        try {
            // Setting up LookAndFeel
            for (LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

        }
        catch (Exception e) {

        }
    }

    public static void clear() {
        UserInterface.getContainer().removeAll(); // clear all
        UserInterface.getContainer().updateUI();
        UserInterface.getContainer().revalidate();
        UserInterface.baseJFrame.setVisible(true);
    }

    public static JPopupMenu createContextMenu(List<String> items, ActionListener al) {
        UserInterface.contextMenu = new JPopupMenu();

        for (int i = 0; i < items.size(); i++) {
            JMenuItem tmpItem = new JMenuItem(items.get(i));
            tmpItem.addActionListener(al);
            UserInterface.contextMenu.add(tmpItem);
        }

        UserInterface.contextMenu.setVisible(true);

        return UserInterface.contextMenu;
    }



    public JPanel setHeader() {
        JPanel header = new JPanel();
        header.add(new JLabel("<html><h2>Marmotte Visual</h2></html>"));
        
        return header;
    }

    public JPanel setFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        footer.add(new JLabel("ver. " + Common.VERSION + " | 2013"), gbc);

        return footer;
    }

    public void actionPerformed(ActionEvent e) {

    }

    public JFrame createBaseStruct() {
        UserInterface.baseJFrame = new JFrame(Common.TITLE);
        UserInterface.baseJFrame.setSize(Common.SIZE_WIDTH, Common.SIZE_HEIGHT);
        UserInterface.baseJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //выход из приложения по нажатию клавиши ESC

        UserInterface.setContainer(new JPanel());
        UserInterface.getContainer().setLayout(new BorderLayout());

        UserInterface.baseJFrame.add(UserInterface.getContainer());
        UserInterface.baseJFrame.setVisible(true);

        return UserInterface.baseJFrame;
    }

    public void createOptionButtons(String[] btnsList) {
        JPanel jp2 = new JPanel();

        for (int i = 0; i < btnsList.length; i++) {
            JButton tmpBtn = new JButton(btnsList[i].toString());
            tmpBtn.addActionListener(this);
            jp2.add(tmpBtn);
        }

        UserInterface.getContainer().add(jp2, BorderLayout.SOUTH);
        UserInterface.baseJFrame.setVisible(true);
    }

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

       // tmpTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //tmpTable.getColumnModel().getColumn(0).setPreferredWidth(50);
       // tmpTable.setPreferredSize(new Dimension(380, 200));
        //tmpTable.getColumnModel().getColumn(1).setPreferredWidth(350);

        ListSelectionModel selectionModel = tmpTable.getSelectionModel();  
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  
        //selectionModel.addListSelectionListener(this);  
        
        JScrollPane scrollPane = new JScrollPane(tmpTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(20, 0));


        container.add(scrollPane);
        //UserInterface.baseJFrame.getContentPane().add(jp1, BorderLayout.CENTER);
        container.setPreferredSize(new Dimension(400,200));
        container.updateUI();
        
        UserInterface.baseJFrame.setVisible(true);

        return tmpTable;
    }

    public void recalculateRowNums(JTable jtable, int col) {
        for (int i = 0; i < jtable.getRowCount(); i++) {
            jtable.setValueAt( String.valueOf(i + 1), i, col);
        }
    }

    public static void setContainer(JPanel val) {
        UserInterface.container = val;
    }

    public static JPanel getContainer() {
        return UserInterface.container;
    }


}