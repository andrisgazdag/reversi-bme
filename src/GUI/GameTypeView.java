package GUI;

import Enums.GameLevel;
import Enums.ReversiType;
import Enums.TableSize;
import Reversi.Controller;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.TextAction;

/**
 * TODO: what does this do???
 *
 * @author Alex
 */
public class GameTypeView extends JFrame implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger("Reversi");
    private Controller ctrl = null;
    private JPanel framePanel = new JPanel(new BorderLayout()); //main panel in window
    //strings to be displayed
    private String singlePlayerString = "Egyszemélyes";
    private String multiPlayerString = "Kétszemélyes";
    private String hard = "Nehéz";
    private String easy = "Könnyű";
    private String server = "Szerver";
    private String client = "Kliens";
    private String normal = "Normál";
    private String tableSizeString4 = "4x4";
    private String tableSizeString8 = "8x8";
    private String tableSizeString10 = "10x10";
    private String tableSizeString12 = "12x12";
    //Input text fields
    private TextField nameField;
    private TextField serverNameField;
    //Panels:
    private JPanel mainPanel;
    private JPanel levelPanel;
    private JPanel buttonsPanel;
    private JPanel userNameInputPanel;
    private JPanel serverNameInputPanel;
    private JPanel gameButtonsPanel;
    //Buttons:
    private JButton startButton;
    private JRadioButton hardButton;
    private JRadioButton normalButton;
    private JRadioButton easyButton;
    private JRadioButton serverButton;
    private JRadioButton clientButton;
    private JRadioButton tableSizeButton4;
    private JRadioButton tableSizeButton8;
    private JRadioButton tableSizeButton10;
    private JRadioButton tableSizeButton12;
    private JRadioButton singlePlayerButton;
    private JRadioButton multiPlayerButton;
    private JButton loadGameButton;
    private String name;
    private JFileChooser fc;

    public GameTypeView(final Controller ctrl) {

        super("Reversi");
        this.ctrl = ctrl;
        //FileChooser to save/load a game
        fc = new JFileChooser();

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

        //set selected and visible buttons:
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

        //Create the panels for the buttons: (and set the layout of the buttons)
        JPanel modePanel = new JPanel(new GridLayout(5, 1));
        {
            modePanel.add(new Label("Mód:")); //add Label to buttongroup
            modePanel.add(singlePlayerButton); //add buttons to group
            modePanel.add(multiPlayerButton);
            modePanel.add(serverButton);
            modePanel.add(clientButton);
        }

        levelPanel = new JPanel(); //create a new panel for the levelbuttons
        {
            levelPanel.setLayout(new GridLayout(5, 1)); //setup the layout manager
            Label levelLabel = new Label("Szint:");//add Label to buttongroup
            levelPanel.add(levelLabel);
            levelPanel.add(easyButton);//add buttons to group
            levelPanel.add(normalButton);
            levelPanel.add(hardButton);
            levelPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // create empty border to seperate from other objects
        }

        //Create the panels for the buttons: (and set the layout of the buttons)
        JPanel tablePanel = new JPanel(new GridLayout(5, 1));
        {
            tablePanel.add(new Label("Táblaméret:"));
            tablePanel.add(tableSizeButton4);
            tablePanel.add(tableSizeButton8);
            tablePanel.add(tableSizeButton10);
            tablePanel.add(tableSizeButton12);

            userNameInputPanel = new JPanel(); //create an input panel for the user name
            nameField = new TextField(20);
            nameField.setText("Béla"); // setup default user name
            userNameInputPanel.add(new Label("Név:"));
            userNameInputPanel.add(nameField);
            userNameInputPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));// create empty border to seperate from other objects
            framePanel.add(userNameInputPanel, BorderLayout.NORTH); //place this panel at the top of the window
        }

        //create an input panel for the servername
        serverNameInputPanel = new JPanel();
        {
            serverNameInputPanel.setLayout(new GridLayout(2, 2));//setup the layout manager
            serverNameInputPanel.add(new Label("Szerver név:")); //add input panel label
            serverNameField = new TextField(20);
            serverNameField.setText(ctrl.getGameName()); //setup default name
            serverNameInputPanel.add(serverNameField); //add this panel to subpanel
            serverNameField.setEnabled(false); //by default this is not editable, because the default mode is single game
            serverNameField.addActionListener(new TextAction(name) { // let the controller know if the servername is changed
                @Override
                public void actionPerformed(ActionEvent e) {
                    ctrl.setGameName(serverNameField.getText());
                }
            });
        }

        //create new spanel for buttons subpanel and serverNameInputPanel
        mainPanel = new JPanel(new BorderLayout());
        buttonsPanel = new JPanel(new BorderLayout()); //set layout manager of the panel
        buttonsPanel.add(modePanel, BorderLayout.WEST); //place the subpanels in this panel
        buttonsPanel.add(levelPanel, BorderLayout.CENTER);
        buttonsPanel.add(tablePanel, BorderLayout.EAST);
        mainPanel.add(buttonsPanel, BorderLayout.NORTH); //place this panel in mainPanel

        mainPanel.add(serverNameInputPanel, BorderLayout.AFTER_LAST_LINE); //place serverNameInputPanel in mainPanel
        framePanel.add(mainPanel, BorderLayout.CENTER);  //place mainPanel in the center of the window
        //create an empty border inside of the window
        framePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        {   // create start button
            startButton = new JButton("Start");
            startButton.setVerticalTextPosition(AbstractButton.CENTER);
            startButton.setHorizontalTextPosition(AbstractButton.LEADING);
            startButton.setActionCommand("start");
            startButton.addActionListener(this);
            startButton.setToolTipText("Let the game begin!");
        }

        {   //create load game
            loadGameButton = new JButton("Betöltés");
            loadGameButton.setVerticalTextPosition(AbstractButton.CENTER);
            loadGameButton.setHorizontalTextPosition(AbstractButton.LEADING);
            loadGameButton.addActionListener(this);
            loadGameButton.setToolTipText("Mentett játék folytatása.");
        }

        gameButtonsPanel = new JPanel(new BorderLayout(0, 5)); // new panel for the load,start buttons
        gameButtonsPanel.add(loadGameButton, BorderLayout.NORTH); //place the buttons
        gameButtonsPanel.add(startButton, BorderLayout.SOUTH);

        framePanel.add(gameButtonsPanel, BorderLayout.SOUTH); // adding buttons to the main panel
        // create empty border to seperate from other objects
        serverNameInputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0)); //

        //Create and set up the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(framePanel);
        setPreferredSize(new Dimension(400, 390));
        //Display the window.
        pack();
        setResizable(false); //do not resize the window!
        setVisible(true);
    }

    /**
     * Button click handler
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "start": //user clicked on the start button so let the game begin!
//                dispose();  //close this window
                name = nameField.getText(); // set the username
                //get selected table size:
                TableSize size;
                if (tableSizeButton4.isSelected()) {
                    size = TableSize.TINY;
                } else if (tableSizeButton8.isSelected()) {
                    size = TableSize.SMALL;
                } else if (tableSizeButton10.isSelected()) {
                    size = TableSize.MEDIUM;
                } else {
                    size = TableSize.BIG;
                }

                // check if single mode is selected
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
                    //start a new single game with appropriate parameters
                    ctrl.startSingleGame(level, size, name);

                } else { //multiplayer game is selected

                    if (serverButton.isSelected()) { //server mode
                        // start a new server with selected name
                        String serverName = serverNameField.getText();
                        startButton.setText("Waiting for a client to connect...");
                        //startButton.setEnabled(false);
                        print(getGraphics());
                        ctrl.startServerGame(size, serverName, name);
                    } else { //client mode
 //                       dispose(); //close this window and show the servers in other window
                        ctrl.showServers();
                    }
                }
                break;

            case "Kétszemélyes": //user clicked on the Kétszemélyes button
                //enable/disable buttons corresponding to selected mode
                hardButton.setEnabled(false);
                easyButton.setEnabled(false);
                normalButton.setEnabled(false);
                serverButton.setEnabled(true);//
                clientButton.setEnabled(true);
                if (clientButton.isSelected()) {
                    setSizeBtnsEnabled(false);
                } else {
                    setSizeBtnsEnabled(true);
                    serverNameField.setEnabled(true);
                    ctrl.startNetworkCommunicator(ReversiType.SERVER);
                }
                break;
            case "Egyszemélyes": //user clicked on the Egyszemélyes button
                //enable/disable buttons corresponding to selected mode
                hardButton.setEnabled(true);
                normalButton.setEnabled(true);
                easyButton.setEnabled(true);
                serverButton.setEnabled(false);
                clientButton.setEnabled(false);
                setSizeBtnsEnabled(true);
                ctrl.stopNetworkCommunicator(); // NetworkCommunicator is not necessary
                break;
            case "Kliens"://user clicked on the Kliens button
                //enable/disable buttons, namefield corresponding to selected mode
                setSizeBtnsEnabled(false);
                serverButton.setEnabled(true);
                serverNameField.setEnabled(false);
                break;
            case "Szerver"://user clicked on the Szerver button
                //enable/disable buttons, namefield corresponding to selected mode
                setSizeBtnsEnabled(true);
                serverNameField.setEnabled(true);
                ctrl.startNetworkCommunicator(ReversiType.SERVER); // start new server
                break;
            case "Betöltés"://user clicked on the Betöltés button
                int returnVal = fc.showOpenDialog(GameTypeView.this); //open the file browser windows
                if (returnVal == JFileChooser.APPROVE_OPTION) { //user selected ona file to load
                    File file = fc.getSelectedFile(); //load the selected file
                    System.out.println("Opening: " + file.getName() + ".");
                    dispose(); // close this window
                    //load and start the selected game
                    ctrl.loadGame(file);
                } else { // user canceled the operation
                    System.out.println("Open command cancelled by user.");
                }
                break;
        }
    }

    /*
     * enable/disable buttons corresponding to selected mode
     */
    void setSizeBtnsEnabled(boolean en) {

        tableSizeButton4.setEnabled(en);
        tableSizeButton8.setEnabled(en);
        tableSizeButton10.setEnabled(en);
        tableSizeButton12.setEnabled(en);

    }
}
