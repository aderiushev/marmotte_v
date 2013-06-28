package com.vdbs;

import java.awt.*; 
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.List;
import javax.swing.event.*;
import javax.swing.UIManager.*;

public class IndexPage extends UserInterface implements ActionListener {


    public IndexPage() {
        JPanel menu = new JPanel();
        menu.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        JButton btn1 = new JButton("Проекты..");
        JButton btn2 = new JButton("Настройки");
        JButton btn3 = new JButton("О программе");

        btn1.addActionListener(this);
        btn2.addActionListener(this);
        btn3.addActionListener(this);
        
        btn1.setPreferredSize(new Dimension(120, 80));
        btn2.setPreferredSize(new Dimension(120, 80));
        btn3.setPreferredSize(new Dimension(120, 80));

        menu.add(btn1);
        menu.add(btn2);
        menu.add(btn3);
        
            
        /**********/
        UserInterface.getContainer().add(this.setHeader(), BorderLayout.NORTH);
        UserInterface.getContainer().add(menu, BorderLayout.CENTER);
        UserInterface.getContainer().add(this.setFooter(), BorderLayout.SOUTH);
        /**********/

        UserInterface.baseJFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String itemChosen = e.getActionCommand();

        if (itemChosen.equals("Проекты..")) {
            UserInterface.clear();
            new ProjectListPage();
        }
        if (itemChosen.equals("О программе")) {
            UserInterface.clear();
            new InfoPage();
        }
        if (itemChosen.equals("Настройки")) {
            UserInterface.clear();
            new SettingsPage();
        }

    }
}