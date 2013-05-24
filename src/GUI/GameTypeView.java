package GUI;

import Enums.GameLevel;
import Enums.ReversiType;
import Enums.TableSize;
import Reversi.Controller;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.TextAction;

public class GameTypeView extends JFrame implements ActionListener/*, ItemListener */ {
    
    private static final Logger LOGGER = Logger.getLogger("Reversi");
    //static JFrame frame = new JFrame("Reversi");
    Controller ctrl = null;
    private JPanel framePanel = new JPanel(new BorderLayout());
    static String singlePlayerString = "Egyszemélyes";
    static String multiPlayerString = "Kétszemélyes";
    static String hard = "Nehéz";
    static String easy = "Könnyű";
    static String server = "Szerver";
    static String client = "Kliens";
    static String normal = "Normál";
    static String tableSizeString4 = "4x4";
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
    private JPanel functionButtonsPanel;
    protected JButton startButton;
    protected JButton refreshButton;
    JRadioButton hardButton;
    JRadioButton normalButton;
    JRadioButton easyButton;
    JRadioButton serverButton;
    JRadioButton clientButton;
    JRadioButton tableSizeButton4;
    JRadioButton tableSizeButton8;
    JRadioButton tableSizeButton10;
    JRadioButton tableSizeButton12;
    JRadioButton singlePlayerButton;
    JRadioButton multiPlayerButton;
    JButton loadGameButton;
    private Choice serverList;
    String name, choosenServer;
    private JFileChooser fc;
    
    public GameTypeView(final Controller ctrl) {
        
        super("Reversi");
        this.ctrl = ctrl;
        
        fc = new JFileChooser();  //fájl betöltéshez kell

        //Create the radio buttons:
        {
            hardButton = new JRadioButton(hard);
            normalButton = new JRadioButton(normal);
            easyButton = new JRadioButton(easy);
            serverButton = new JRadioButton(server);
            clientButton = new JRadioButton(client);
            tableSizeButton4 = new JRadioButton(tableSizeString4);
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
            tableSizeButton4.setActionCommand(tableSizeString4);
            tableSizeButton8.setActionCommand(tableSizeString8);
            tableSizeButton10.setActionCommand(tableSizeString10);
            tableSizeButton12.setActionCommand(tableSizeString12);
            hardButton.setActionCommand(hard);
            easyButton.setActionCommand(easy);
            normalButton.setActionCommand(normal);
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
            groupLevel.add(normalButton);
            
            ButtonGroup groupSize = new ButtonGroup();
            groupSize.add(tableSizeButton4);
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
            normalButton.addActionListener(this);
            tableSizeButton4.addActionListener(this);
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
            levelPanel.add(normalButton);
            levelPanel.add(hardButton);
            levelPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        }
        
        JPanel tablePanel = new JPanel(new GridLayout(5, 1));
        {
            tablePanel.add(new Label("Táblaméret:"));
            tablePanel.add(tableSizeButton4);
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
            serverNameField.setText(ctrl.getGameName());
            serverNameInputPanel.add(serverNameField);
            serverNameField.setEnabled(false);
            serverNameField.addActionListener(new TextAction(name) { // ha megvaltozik a szerver neve, akkor arrol a controller ertesul
                @Override
                public void actionPerformed(ActionEvent e) {
                    ctrl.setGameName(serverNameField.getText());
                }
            });
        }
        
        mainPanel = new JPanel(new BorderLayout());
        buttonsPanel = new JPanel(new BorderLayout());
        
        buttonsPanel.add(modePanel, BorderLayout.WEST);
        buttonsPanel.add(levelPanel, BorderLayout.CENTER);
        buttonsPanel.add(tablePanel, BorderLayout.EAST);
        mainPanel.add(buttonsPanel, BorderLayout.NORTH);

        /*{
         serverList = new Choice();
         //serverList.add("List of avaliable servers");

         LOGGER.log(Level.SEVERE, "Getting the server list...");
         String[] availableServers = ctrl.getAvailableServerList();
         LOGGER.log(Level.SEVERE, "Server list recived");
         if (availableServers.length == 0 || availableServers[0] == null) {
         serverList.add("No available server...");
         } else {
         for (String s : availableServers) {
         LOGGER.log(Level.INFO, "Available server: {0}", s);
         serverList.add(s);
         }
         }

         serverList.select(0);
         serverList.addItemListener(this);
         serverList.setEnabled(false);
         serverNameInputPanel.add(new Label("Szerverek:"));
         serverNameInputPanel.add(serverList);
         }*/
        
        framePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));//az egész ablakon belül 20 pixel keret

        {   // start button
            startButton = new JButton("Start");
            startButton.setVerticalTextPosition(AbstractButton.CENTER);
            startButton.setHorizontalTextPosition(AbstractButton.LEADING);
            startButton.setMnemonic(KeyEvent.VK_D);
            startButton.setActionCommand("start");
            startButton.addActionListener(this);
            startButton.setToolTipText("Let the game begin!");
        }

        //Dimension buttonSize = new Dimension(150, 25);

        /* {   // refresh button
         refreshButton = new JButton("Refresh");
         refreshButton.setVerticalTextPosition(AbstractButton.CENTER);
         refreshButton.setHorizontalTextPosition(AbstractButton.LEADING);
         refreshButton.setMnemonic(KeyEvent.VK_D);
         refreshButton.setActionCommand("refresh");
         refreshButton.addActionListener(this);
         refreshButton.setToolTipText("Refresh the server list!");
         refreshButton.setPreferredSize(buttonSize);
         refreshButton.setEnabled(false);
         }*/
        
        {   // load game
            loadGameButton = new JButton("Betöltés");
            loadGameButton.setVerticalTextPosition(AbstractButton.CENTER);
            loadGameButton.setHorizontalTextPosition(AbstractButton.LEADING);
            // loadGameButton.setActionCommand("start");
            loadGameButton.addActionListener(this);
            loadGameButton.setToolTipText("Mentett játék folytatása.");
            //loadGameButton.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
            //loadGameButton.setPreferredSize(buttonSize);
        }
        
        mainPanel.add(serverNameInputPanel, BorderLayout.AFTER_LAST_LINE);

        // create panels for the buttons
        //functionButtonsPanel = new JPanel(new FlowLayout()); // load and refresh buttons
        gameButtonsPanel = new JPanel(new BorderLayout(0, 5)); // start button

        //functionButtonsPanel.add(loadGameButton);
        // functionButtonsPanel.add(refreshButton);

        gameButtonsPanel.add(loadGameButton, BorderLayout.NORTH);
        gameButtonsPanel.add(startButton, BorderLayout.SOUTH);
        
        framePanel.add(mainPanel, BorderLayout.CENTER);
        framePanel.add(gameButtonsPanel, BorderLayout.SOUTH); // adding buttons to the main panel

        
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

    /*@Override
     public void itemStateChanged(ItemEvent e) {  //a legördülő listában másik elemet választunk ki
     if (e.getSource().equals(serverList)) {
     choosenServer = serverList.getSelectedItem();
     }
     }*/
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
                
                TableSize size;
                // get table size
                if (tableSizeButton4.isSelected()) {
                    size = TableSize.TINY;
                } else if (tableSizeButton8.isSelected()) {
                    size = TableSize.SMALL;
                } else if (tableSizeButton10.isSelected()) {
                    size = TableSize.MEDIUM;
                } else {
                    size = TableSize.BIG;
                }

                /////////////////////////////////DEBUG////////////////////////////                    
                //       size = TableSize.TINY;
/////////////////////////////////DEBUG//////////////////////////// 

                // inditani kell a jatekot a kivalasztott opcioknak megfeleloen
                if (singlePlayerButton.isSelected()) {
                    // start single game
                    GameLevel level;

                    // get game level
                    if (easyButton.isSelected()) {
                        level = GameLevel.EASY;
                    } else if (normalButton.isSelected()) {
                        level = GameLevel.NORMAL;
                    } else {
                        level = GameLevel.HARD;
                    }
                    
                    ctrl.startSingleGame(level, size, name);
                    
                } else {
                    // start multiplayer game      
                    if (serverButton.isSelected()) {
                        // start a new server with selected name
                        String serverName = serverNameField.getText();
                        ctrl.startServerGame(size, serverName, name);
                    } else {
                        // start a new client with choosen server
//                         String choosenServer = serverList.getSelectedItem();
                        dispose();
                        ctrl.showServers();

//                        ctrl.startClientGame(name, choosenServer);
                    }
                }
                break;
            
            case "Kétszemélyes":
                // ez hívódik meg ha a kétszemélyes gombra kattintunk
                hardButton.setEnabled(false);
                easyButton.setEnabled(false);
                normalButton.setEnabled(false);
                serverButton.setEnabled(true);
                clientButton.setEnabled(true);
                if (clientButton.isSelected()) { //ha ki van jelölve a kliens gomba kkor a táblaméret inaktív
                    setSizeBtnsEnabled(false);
                    // serverList.setEnabled(true);
                } else {
                    setSizeBtnsEnabled(true);
                    serverNameField.setEnabled(true);
                    ctrl.startNetworkCommunicator(ReversiType.SERVER);
                }
                //ctrl.startNetworkCommunicator(ReversiType.SERVER);
                //refreshButton.setEnabled(false);
                break;
            case "Egyszemélyes":
                hardButton.setEnabled(true);
                normalButton.setEnabled(true);
                easyButton.setEnabled(true);
                serverButton.setEnabled(false);
                clientButton.setEnabled(false);
                setSizeBtnsEnabled(true);
                //serverList.setEnabled(false);
                //ctrl.stopNetworkCommunicator();
                //refreshButton.setEnabled(false);
                break;
            case "Kliens":
                serverButton.setEnabled(false);//megakadályozzuk Bélát szegény gép kínzásában
                setSizeBtnsEnabled(false);
                //serverList.setEnabled(true);
                serverButton.setEnabled(true);
                serverNameField.setEnabled(false);
                //ctrl.startNetworkCommunicator(ReversiType.CLIENT);
                //refreshButton.setEnabled(true);
                break;
            case "Szerver":
                setSizeBtnsEnabled(true);
                // serverList.setEnabled(false);
                serverNameField.setEnabled(true);
                ctrl.startNetworkCommunicator(ReversiType.SERVER);
                // refreshButton.setEnabled(false);
                break;
            case "Betöltés":
                int returnVal = fc.showOpenDialog(GameTypeView.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    System.out.println("Opening: " + file.getName() + ".");
                    dispose();// ablak becsukása
                    //todo: játék betöltése és indítása
                    ctrl.loadGame(file);
                    
                } else { // Béla mégsem akar betölteni semmit, így visszatérünk az előző ablakhoz
                    System.out.println("Open command cancelled by user.");
                }
                break;
            /*case "refresh":
             serverList.removeAll();
             LOGGER.log(Level.OFF, "Getting the server list...");
             String[] availableServers = ctrl.getAvailableServerList();
             LOGGER.log(Level.OFF, "Server list recived");
             if (availableServers.length == 0 || availableServers[0] == null) {
             serverList.add("No available server...");
             } else {
             for (String s : availableServers) {
             LOGGER.log(Level.INFO, "Available server: {0}", s);
             serverList.add(s);
             }
             }
             break;*/
        }
        
    }

    void setSizeBtnsEnabled(boolean en) {
        tableSizeButton4.setEnabled(en);
        tableSizeButton8.setEnabled(en);
        tableSizeButton10.setEnabled(en);
        tableSizeButton12.setEnabled(en);
    }
}
