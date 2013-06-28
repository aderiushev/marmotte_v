package com.vdbs;

import java.awt.*; 
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.List;
import javax.swing.event.*;
import javax.swing.UIManager.*;
import java.net.URI;

public class InfoPage extends UserInterface implements ActionListener {

    private String text = "<html>" +
                                "<head>" +
                                "<style>" +
                                    "html,body {font-family:Verdana;font-size:13px;padding:0;margin:0;color:#666}" +
                                    ".container {width:" + (Common.SIZE_WIDTH - 150) + "px}" +
                                    "p {padding-bottom:15px; text-align:center;}" +
                                "</style>" +
                                "<body>" +
                                    "<div class='container'>" +
                                        "<p>" +
                                            "Добро пожаловать на справочную страничку!" +
                                        "</p>" +
                                        "<p>" +
                                            "Ниже содержится ссылка на страничку с информацией, необходимой для начала работы с приложением." +
                                        "</p>" +
                                        "<p>" +
                                            "Автор: Joe Black, derushev.alexey@gmail.com" +
                                        "</p>" +
                                        "<p>" +
                                            "GNU GPL License." +
                                        "</p>" +
                                    "</div>" +
                                "</body" +
                            "</html>";


    public InfoPage() {

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setSize(new Dimension(Common.SIZE_WIDTH, Common.SIZE_HEIGHT));

        //container.setSize(new Dimension(300, 200));
        JLabel text = new JLabel(this.getText());
        text.setHorizontalAlignment(JLabel.CENTER);
        text.setHorizontalTextPosition(JLabel.CENTER);
        JScrollPane scrollPane = new JScrollPane(text);

        container.add(scrollPane);

        /**********/
        UserInterface.getContainer().add(this.setHeader(), BorderLayout.NORTH);
        UserInterface.getContainer().add(container, BorderLayout.CENTER);
        UserInterface.getContainer().add(this.setFooter(), BorderLayout.SOUTH);
        /**********/

        UserInterface.baseJFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Назад..")) {
            UserInterface.clear();
            new IndexPage();
        }
        if (e.getActionCommand().equals("Открыть документацию..")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    URI url = new URI(Common.DOC_URL);
                    Desktop.getDesktop().browse(url);
                } catch (Exception ex) {}
            } else {}
        }
    }

    public JPanel setHeader() {
        JPanel header = new JPanel();
        header.add(new JLabel("<html><h2>Info page</h2></html>"));
        
        return header;
    }

    public JPanel setFooter() {
        JPanel footer = super.setFooter();
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;  
        JButton linkButton = new JButton("Открыть документацию.."); 
        linkButton.addActionListener(this);
        footer.add(linkButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;  
        JButton btnBack = new JButton("Назад..");
        btnBack.addActionListener(this);
        footer.add(btnBack, gbc);

        return footer;
    }

    public String getText() {
        return this.text;
    }


}