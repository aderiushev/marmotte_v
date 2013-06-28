package com.vdbs;

import java.awt.*; 
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.List;
import javax.swing.event.*;
import javax.swing.UIManager.*;

public class SettingsPage extends UserInterface implements ActionListener {


    public SettingsPage() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel projectPathLabel = new JLabel("Путь к папке с проектами:");
        settingsPanel.add(projectPathLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JLabel projectPath = new JLabel("<html><strong style='color:blue'>" + Common.PROJECTS_DATA_PATH + "</strong></html>");
        settingsPanel.add(projectPath, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        JButton projectPathButton = new JButton("Изменить..");
        projectPathButton.addActionListener(this);
        settingsPanel.add(projectPathButton, gbc);
        
            
        /**********/
        UserInterface.getContainer().add(this.setHeader(), BorderLayout.NORTH);
        UserInterface.getContainer().add(settingsPanel, BorderLayout.CENTER);
        UserInterface.getContainer().add(this.setFooter(), BorderLayout.SOUTH);
        /**********/

        UserInterface.baseJFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String itemChosen = e.getActionCommand();

        if (itemChosen.equals("Назад..")) {
            UserInterface.clear();
            new IndexPage();
        }
        if (itemChosen.equals("Изменить..")) {
            if (this.changeDataPath()){
                UserInterface.clear();
                new SettingsPage();
            }
        }
    }


    public JPanel setHeader() {
        JPanel header = new JPanel();
        header.add(new JLabel("<html><h2>Настройки</h2></html>"));
        
        return header;
    }

    public JPanel setFooter() {
        JPanel footer = super.setFooter();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        JButton btnBack = new JButton("Назад..");
        btnBack.addActionListener(this);
        footer.add(btnBack, gbc);

        return footer;
    }

    public boolean changeDataPath() {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int status = fileChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            try {
                Common.PROJECTS_DATA_PATH = fileChooser.getSelectedFile().getCanonicalPath() + "/";
                return true;
            }
            catch(Exception e) {

            }
                

        }
        
        return false;
    }
}