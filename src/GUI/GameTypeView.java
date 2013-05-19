package GUI;

import Enums.GameLevel;
import Enums.ReversiType;
import Enums.TableSize;
import Reversi.Controller;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

public class GameTypeView extends JFrame implements ActionListener, ItemListener {

    //static JFrame frame = new JFrame("Reversi");
    Controller ctrl = null;
    private JPanel framePanel = new JPanel(new BorderLayout());
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
    private JPanel gameButtonsPanel;
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
    JButton loadGameButton;
    private Choice serverList;
    String name, choosedServer;
    private JFileChooser fc;

    public GameTypeView(Controller ctrl) {

        super("Reversi");
        this.ctrl = ctrl;

        fc = new JFileChooser();  //fájl betöltéshez kell

        //Create the radio buttons:
        {
            hardButton = new JRadioButton(hard);
            easyButton = new JRadioButton(easy);
            serverButton = new JRadioButton(server);
            clientButton = new JRadioButton(client);
            tableSizeButton8 = new JRadioButton(tableSizeString8);
            tableSizeButton10 = new JRadioButton(tableSizeString10);
            tableSizeButton12 = new JRadioButton(tableSizeString12);
            singlePlayerButton = new JRadioButton(singlePlayerString);
            multiPlayerButton = new JRadioButton(multiPlayerString);
        }

        //set action commands:   
        {
            serverButton.setActionCommand(server);
            clientButton.setActionCommand(client);
            singlePlayerButton.setActionCommand(singlePlayerString);
            multiPlayerButton.setActionCommand(multiPlayerString);
            tableSizeButton8.setActionCommand(tableSizeString8);
            tableSizeButton10.setActionCommand(tableSizeString10);
            tableSizeButton12.setActionCommand(tableSizeString12);
            hardButton.setActionCommand(hard);
            easyButton.setActionCommand(easy);
        }

        //set selected and visible button:
        {
            serverButton.setSelected(true);
            serverButton.setEnabled(false);
            clientButton.setEnabled(false);
            tableSizeButton8.setSelected(true);
            singlePlayerButton.setSelected(true);
            easyButton.setSelected(true);
        }

        // set tool tip texts:
        {
            hardButton.setToolTipText("Nincs esélyed!");
            easyButton.setToolTipText("Talán van esélyed!");

        }

        //Group the radio buttons. Only one button is selectable in each group.
        {
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
        }


        //Register a listener for the radio buttons.
        {
            singlePlayerButton.addActionListener(this);
            multiPlayerButton.addActionListener(this);
            hardButton.addActionListener(this);
            easyButton.addActionListener(this);
            tableSizeButton8.addActionListener(this);
            tableSizeButton10.addActionListener(this);
            tableSizeButton12.addActionListener(this);
            serverButton.addActionListener(this);
            clientButton.addActionListener(this);
        }

        //Create the panels for the buttons:
        JPanel modePanel = new JPanel(new GridLayout(5, 1));
        {
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
        }

        JPanel tablePanel = new JPanel(new GridLayout(5, 1));
        {
            tablePanel.add(new Label("Táblaméret:"));
            tablePanel.add(tableSizeButton8);
            tablePanel.add(tableSizeButton10);
            tablePanel.add(tableSizeButton12);

            userNameInputPanel = new JPanel();
            nameField = new TextField(20);
            nameField.setText("Béla");
            userNameInputPanel.add(new Label("Név:"));
            userNameInputPanel.add(nameField);
            userNameInputPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            framePanel.add(userNameInputPanel, BorderLayout.NORTH);

            serverNameInputPanel = new JPanel();
            serverNameInputPanel.setLayout(new GridLayout(2, 2));
            serverNameInputPanel.add(new Label("Szerver név:"));
            serverNameField = new TextField(20);
            serverNameField.setText("Skynet");
            serverNameInputPanel.add(serverNameField);
            serverNameField.setEnabled(false);
        }

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

        framePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));//az egész ablakon belül 20 pixel keret

        {
            startButton = new JButton("Start");
            startButton.setVerticalTextPosition(AbstractButton.CENTER);
            startButton.setHorizontalTextPosition(AbstractButton.LEADING);
            startButton.setMnemonic(KeyEvent.VK_D);
            startButton.setActionCommand("start");
            startButton.addActionListener(this);
            startButton.setToolTipText("Let the game begin!");
        }

        loadGameButton = new JButton("Betöltés");
        loadGameButton.setVerticalTextPosition(AbstractButton.CENTER);
        loadGameButton.setHorizontalTextPosition(AbstractButton.LEADING);
        // loadGameButton.setActionCommand("start");
        loadGameButton.addActionListener(this);
        loadGameButton.setToolTipText("Mentett játék folytatása.");
        //loadGameButton.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        mainPanel.add(serverNameInputPanel, BorderLayout.AFTER_LAST_LINE);

        gameButtonsPanel = new JPanel(new BorderLayout(0, 5));
        gameButtonsPanel.add(startButton, BorderLayout.NORTH);
        gameButtonsPanel.add(loadGameButton, BorderLayout.SOUTH);

        framePanel.add(mainPanel, BorderLayout.CENTER);
        framePanel.add(gameButtonsPanel, BorderLayout.SOUTH);
        //startButton.setSize(5, 5);
        serverNameInputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        //Create and set up the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //newContentPane.setOpaque(true); //content panes must be opaque
        setContentPane(framePanel);

        setPreferredSize(new Dimension(400, 390));

        //Display the window.
        pack();
        setVisible(true);

    }

    @Override
    public void itemStateChanged(ItemEvent e) {  //a legördülő listában másik elemet választunk ki
        if (e.getSource().equals(serverList)) {
            choosedServer = serverList.getSelectedItem();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "start":
                // ez hívódik meg a startgombra kattintáskor
                dispose();  //jelenlegi ablak becsukása
                //Controller c = new Control();
                //GamePlayView g = new GamePlayView();
                // ctrl.startSinglePlayerGame(tableSize);
                name = nameField.getText();

                // inditani kell a jatekot a kivalasztott opcioknak megfeleloen
                if (singlePlayerButton.isSelected()) {
                    // start single game
                    GameLevel level = null;
                    TableSize size = null;

                    // get game level
                    if (easyButton.isSelected()) {
                        level = GameLevel.EASY;
                    } else {
                        level = GameLevel.HARD;
                    }

                    // get table size
                    if (tableSizeButton8.isSelected()) {
                        size = TableSize.SMALL;
                    } else if (tableSizeButton10.isSelected()) {
                        size = TableSize.MEDIUM;
                    } else {
                        size = TableSize.BIG;
                    }

                    ctrl.startSingleGame(level, size, name);

                } else {
                    // start multiplayer game
                    TableSize size = null;
                    // get table size
                    if (tableSizeButton8.isSelected()) {
                        size = TableSize.SMALL;
                    } else if (tableSizeButton10.isSelected()) {
                        size = TableSize.MEDIUM;
                    } else {
                        size = TableSize.BIG;
                    }
                    
                    if (serverButton.isSelected()) {
                        // start a new server with selected name
                        String serverName = serverNameField.getText();
                        ctrl.startServerGame(size, serverName, name);
                    } else {
                        // start a new client with choosen server
                        String choosenServer = serverList.getSelectedItem();
                        ctrl.startClientGame(name, choosenServer);
                    }
                }


                break;
                
            case "Kétszemélyes":
                // ez hívódik meg ha a kétszemélyes gombra kattintunk
                hardButton.setEnabled(false);
                easyButton.setEnabled(false);
                serverButton.setEnabled(true);
                clientButton.setEnabled(true);
                if (clientButton.isSelected()) { //ha ki van jelölve a kliens gomba kkor a táblaméret inaktív
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
                ctrl.startNetworkCommunicator(ReversiType.SERVER, "ServerGame");
                break;
            case "Egyszemélyes":
                hardButton.setEnabled(true);
                easyButton.setEnabled(true);
                serverButton.setEnabled(false);
                clientButton.setEnabled(false);
                tableSizeButton8.setEnabled(true);
                tableSizeButton10.setEnabled(true);
                tableSizeButton12.setEnabled(true);
                serverList.setEnabled(false);
                ctrl.stopNetworkCommunicator();
                break;
            case "Kliens":
                tableSizeButton8.setEnabled(false);
                tableSizeButton10.setEnabled(false);
                tableSizeButton12.setEnabled(false);
                serverList.setEnabled(true);
                serverNameField.setEnabled(false);
                ctrl.startNetworkCommunicator(ReversiType.CLIENT, null);
                break;
            case "Szerver":
                tableSizeButton8.setEnabled(true);
                tableSizeButton10.setEnabled(true);
                tableSizeButton12.setEnabled(true);
                serverList.setEnabled(false);
                serverNameField.setEnabled(true);
                ctrl.startNetworkCommunicator(ReversiType.SERVER, "ServerGame");
                break;
            case "Betöltés":
                int returnVal = fc.showOpenDialog(GameTypeView.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    System.out.println("Opening: " + file.getName() + ".");
                    dispose();// ablak becsukása
                    //todo: játék betöltése és indítása

                } else { // Béla mégsem akar betölteni semmit, így visszatérünk az előző ablakhoz
                    System.out.println("Open command cancelled by user.");
                }
                break;
        }

    }
}