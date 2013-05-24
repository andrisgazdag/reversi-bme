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
    private JPanel framePanel = new JPanel(new BorderLayout());
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
    private TextField nameField;
    private TextField serverNameField;
    private JPanel mainPanel;
    private JPanel levelPanel;
    private JPanel buttonsPanel;
    private JPanel userNameInputPanel;
    private JPanel serverNameInputPanel;
    private JPanel gameButtonsPanel;
    private JPanel functionButtonsPanel;
    private JButton startButton;
    private JButton refreshButton;
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
    private Choice serverList;
    private String name, choosenServer;
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

        //az egész ablakon belül 20 pixel keret
        framePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        {   // start button
            startButton = new JButton("Start");
            startButton.setVerticalTextPosition(AbstractButton.CENTER);
            startButton.setHorizontalTextPosition(AbstractButton.LEADING);
            startButton.setMnemonic(KeyEvent.VK_D);
            startButton.setActionCommand("start");
            startButton.addActionListener(this);
            startButton.setToolTipText("Let the game begin!");
        }

        {   // load game
            loadGameButton = new JButton("Betöltés");
            loadGameButton.setVerticalTextPosition(AbstractButton.CENTER);
            loadGameButton.setHorizontalTextPosition(AbstractButton.LEADING);
            loadGameButton.addActionListener(this);
            loadGameButton.setToolTipText("Mentett játék folytatása.");
        }

        mainPanel.add(serverNameInputPanel, BorderLayout.AFTER_LAST_LINE);

        gameButtonsPanel = new JPanel(new BorderLayout(0, 5)); // start button

        gameButtonsPanel.add(loadGameButton, BorderLayout.NORTH);
        gameButtonsPanel.add(startButton, BorderLayout.SOUTH);

        framePanel.add(mainPanel, BorderLayout.CENTER);
        framePanel.add(gameButtonsPanel, BorderLayout.SOUTH); // adding buttons to the main panel


        serverNameInputPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        //Create and set up the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(framePanel);

        setPreferredSize(new Dimension(400, 390));

        //Display the window.
        pack();
        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "start":
                // ez hívódik meg a startgombra kattintáskor

                dispose();  //jelenlegi ablak becsukása

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

                        dispose();
                        ctrl.showServers();

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
                } else {
                    setSizeBtnsEnabled(true);
                    serverNameField.setEnabled(true);
                    ctrl.startNetworkCommunicator(ReversiType.SERVER);
                }
                break;
            case "Egyszemélyes":
                hardButton.setEnabled(true);
                normalButton.setEnabled(true);
                easyButton.setEnabled(true);
                serverButton.setEnabled(false);
                clientButton.setEnabled(false);
                setSizeBtnsEnabled(true);
                break;
            case "Kliens":
                //TODO... megakadályozzuk Bélát szegény gép kínzásában
                serverButton.setEnabled(false);
                setSizeBtnsEnabled(false);
                serverButton.setEnabled(true);
                serverNameField.setEnabled(false);
                break;
            case "Szerver":
                setSizeBtnsEnabled(true);
                serverNameField.setEnabled(true);
                ctrl.startNetworkCommunicator(ReversiType.SERVER);
                break;
            case "Betöltés":
                int returnVal = fc.showOpenDialog(GameTypeView.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    System.out.println("Opening: " + file.getName() + ".");
                    dispose(); // ablak becsukása
                    //játék betöltése és indítása
                    ctrl.loadGame(file);

                } else { // Béla mégsem akar betölteni semmit, így visszatérünk az előző ablakhoz
                    System.out.println("Open command cancelled by user.");
                }
                break;

        }

    }

    void setSizeBtnsEnabled(boolean en) {

        tableSizeButton4.setEnabled(en);
        tableSizeButton8.setEnabled(en);
        tableSizeButton10.setEnabled(en);
        tableSizeButton12.setEnabled(en);

    }
}
