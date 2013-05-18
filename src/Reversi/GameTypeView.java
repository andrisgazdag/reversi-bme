/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reversi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Alex
 */
public class GameTypeView extends JPanel
        implements ActionListener, ItemListener {

    static JFrame frame = new JFrame("Reversi");
    Controller ctrl = null;
    static String singlePlayerString = "Egyszemélyes";
    static String multiPlayerString = "Kétszemélyes";
    static String hard = "Nehéz";
    static String easy = "Könnyű";
    static String server = "Szerver";
    static String client = "Kliens";
    static String tableSizeString8 = "8x8";
    static String tableSizeString10 = "10x10";
    static String tableSizeString12 = "12x12";
    private TextField nameField;
    private TextField serverNameField;
    private JPanel mainPanel;
    private JPanel levelPanel;
    private JPanel buttonsPanel;
    private JPanel userNameInputPanel;
    private JPanel serverNameInputPanel;
    protected JButton startButton;
    JRadioButton hardButton;
    JRadioButton easyButton;
    JRadioButton serverButton;
    JRadioButton clientButton;
    JRadioButton tableSizeButton8;
    JRadioButton tableSizeButton10;
    JRadioButton tableSizeButton12;
    JRadioButton singlePlayerButton;
    JRadioButton multiPlayerButton;
    private Choice serverList;
    String name, choosedServer;

    public GameTypeView() {
        super(new BorderLayout());

        //Create the radio buttons:
        hardButton = new JRadioButton(hard);
        easyButton = new JRadioButton(easy);
        serverButton = new JRadioButton(server);
        clientButton = new JRadioButton(client);
        tableSizeButton8 = new JRadioButton(tableSizeString8);
        tableSizeButton10 = new JRadioButton(tableSizeString10);
        tableSizeButton12 = new JRadioButton(tableSizeString12);
        singlePlayerButton = new JRadioButton(singlePlayerString);
         multiPlayerButton = new JRadioButton(multiPlayerString);
         
        //set action commands:   
        serverButton.setActionCommand(server);
        clientButton.setActionCommand(client);
        singlePlayerButton.setActionCommand(singlePlayerString);
        multiPlayerButton.setActionCommand(multiPlayerString);
        tableSizeButton8.setActionCommand(tableSizeString8);
        tableSizeButton10.setActionCommand(tableSizeString10);
        tableSizeButton12.setActionCommand(tableSizeString12);
        hardButton.setActionCommand(hard);
        easyButton.setActionCommand(easy);
        
        //set selected and visible button:
        serverButton.setSelected(true);
        serverButton.setEnabled(false);
        clientButton.setEnabled(false);
        tableSizeButton8.setSelected(true);
        singlePlayerButton.setSelected(true);
        easyButton.setSelected(true);

        // set tool tip texts:
        hardButton.setToolTipText("Nincs esélyed!");
        easyButton.setToolTipText("Talán van esélyed!");
        

        //Group the radio buttons. Only one button is selectable in each group.
        ButtonGroup groupModeSingle = new ButtonGroup();
        groupModeSingle.add(singlePlayerButton);
        groupModeSingle.add(multiPlayerButton);

        ButtonGroup groupModeMulti = new ButtonGroup();
        groupModeMulti.add(serverButton);
        groupModeMulti.add(clientButton);

        ButtonGroup groupLevel = new ButtonGroup();
        groupLevel.add(hardButton);
        groupLevel.add(easyButton);

        ButtonGroup groupSize = new ButtonGroup();
        groupSize.add(tableSizeButton8);
        groupSize.add(tableSizeButton10);
        groupSize.add(tableSizeButton12);


        //Register a listener for the radio buttons.
        singlePlayerButton.addActionListener(this);
        multiPlayerButton.addActionListener(this);
        hardButton.addActionListener(this);
        easyButton.addActionListener(this);
        tableSizeButton8.addActionListener(this);
        tableSizeButton10.addActionListener(this);
        tableSizeButton12.addActionListener(this);
        serverButton.addActionListener(this);
        clientButton.addActionListener(this);

        //Create the panels for the buttons:
        JPanel modePanel = new JPanel(new GridLayout(5, 1));
        modePanel.add(new Label("Mód:"));
        modePanel.add(singlePlayerButton);
        modePanel.add(multiPlayerButton);
        modePanel.add(serverButton);
        modePanel.add(clientButton);

        levelPanel = new JPanel();
        levelPanel.setLayout(new GridLayout(5, 1));
        Label levelLabel = new Label("Szint:");
        levelPanel.add(levelLabel);
        levelPanel.add(easyButton);
        levelPanel.add(hardButton);
        levelPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));


        JPanel tablePanel = new JPanel(new GridLayout(5, 1));
        tablePanel.add(new Label("Táblaméret:"));
        tablePanel.add(tableSizeButton8);
        tablePanel.add(tableSizeButton10);
        tablePanel.add(tableSizeButton12);
        //tablePanel.setBackground(Color.LIGHT_GRAY);
        //  mainPanel.add(radioPanel3);


        //add(radioPanel, FlowLayout.CENTER);
        // add(picture, Bo1rderLayout.CENTER);
        userNameInputPanel = new JPanel();
        nameField = new TextField(20);
        nameField.setText("Béla");
        userNameInputPanel.add(new Label("Név:"));
        userNameInputPanel.add(nameField);
        userNameInputPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(userNameInputPanel, BorderLayout.NORTH);

        serverNameInputPanel = new JPanel();
        serverNameInputPanel.setLayout(new GridLayout(2, 2));
        serverNameInputPanel.add(new Label("Szerver név:"));
        serverNameField = new TextField(20);
        serverNameField.setText("Skynet");
        serverNameInputPanel.add(serverNameField);
        serverNameField.setEnabled(false);

        mainPanel = new JPanel(new BorderLayout());
        buttonsPanel = new JPanel(new BorderLayout());
        
        buttonsPanel.add(modePanel, BorderLayout.WEST);
        buttonsPanel.add(levelPanel, BorderLayout.CENTER);
        buttonsPanel.add(tablePanel, BorderLayout.EAST);
        mainPanel.add(buttonsPanel, BorderLayout.NORTH);

        serverList = new Choice();
        serverList.add("List of avaliable servers");
        for (int i = 1; i < 5; i++) {
            serverList.add("" + i);
        }
        serverList.select(0);
        serverList.addItemListener(this);
        serverList.setEnabled(false);
        serverNameInputPanel.add(new Label("Szerverek:"));
        serverNameInputPanel.add(serverList);

        //this.setVisible(true);

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));//az egész ablakon belül 20 pixel keret

        startButton = new JButton("Start");
        startButton.setVerticalTextPosition(AbstractButton.CENTER);
        startButton.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        startButton.setMnemonic(KeyEvent.VK_D);
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
        startButton.setToolTipText("Let the game begin!");

        //startButton.setAlignmentY(0);
        //serverNameInputPanel.add(startButton);
        //serverNameInputPanel.setBackground(Color.red);
        mainPanel.add(serverNameInputPanel, BorderLayout.AFTER_LAST_LINE);
        add(mainPanel, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);
        //startButton.setSize(5, 5);
        serverNameInputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        
        //

    }

    public void itemStateChanged(ItemEvent e) {  //a legördülő listában másik elemet választunk ki
        if (e.getSource().equals(serverList)) {
            choosedServer = serverList.getSelectedItem();
        }
    }

    public void setController(Controller c) {
        ctrl = c;
    }

    public void actionPerformed(ActionEvent e) {
        if ("start".equals(e.getActionCommand())) {


            frame.dispose();
            //Controller c = new Control();
            //GamePlayView g = new GamePlayView();
            // ctrl.startSinglePlayerGame(tableSize);


            //c.setGUI(g);
        } else if ("Kétszemélyes".equals(e.getActionCommand())) {
            hardButton.setEnabled(false);
            easyButton.setEnabled(false);
            serverButton.setEnabled(true);
            clientButton.setEnabled(true);
            if (clientButton.isSelected()) {
                tableSizeButton8.setEnabled(false);
                tableSizeButton10.setEnabled(false);
                tableSizeButton12.setEnabled(false);
                serverList.setEnabled(true);
            } else {
                tableSizeButton8.setEnabled(true);
                tableSizeButton10.setEnabled(true);
                tableSizeButton12.setEnabled(true);
                serverNameField.setEnabled(true);
            }

        } else if ("Egyszemélyes".equals(e.getActionCommand())) {
            hardButton.setEnabled(true);
            easyButton.setEnabled(true);
            serverButton.setEnabled(false);
            clientButton.setEnabled(false);
            tableSizeButton8.setEnabled(true);
            tableSizeButton10.setEnabled(true);
            tableSizeButton12.setEnabled(true);
            serverList.setEnabled(false);

        } else if ("Kliens".equals(e.getActionCommand())) {
            tableSizeButton8.setEnabled(false);
            tableSizeButton10.setEnabled(false);
            tableSizeButton12.setEnabled(false);
            serverList.setEnabled(true);
            serverNameField.setEnabled(false);
        } else if ("Szerver".equals(e.getActionCommand())) {
            tableSizeButton8.setEnabled(true);
            tableSizeButton10.setEnabled(true);
            tableSizeButton12.setEnabled(true);
            serverList.setEnabled(false);
            serverNameField.setEnabled(true);
        }

        /* else
         {
         startButton.setEnabled(true);
         }*/
    }

    public static void createAndShowGUI() {
        //Create and set up the window.

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new GameTypeView();
        //newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
