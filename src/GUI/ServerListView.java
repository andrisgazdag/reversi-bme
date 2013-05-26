package GUI;

import Reversi.Controller;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 
 *
 * @author Gabor Kovacs
 */
public class ServerListView extends JFrame implements ActionListener, ItemListener {

    Controller ctrl = null;
    private static final Logger LOGGER = Logger.getLogger("Reversi");
    private JPanel framePanel = new JPanel(new BorderLayout()); //mainpanel of the window
    //buttons:
    protected JButton startButton;
    protected JButton refreshButton;
    //pop-up menu of choices for servers
    private Choice serverList;
    //user name, choosen servername
    String name, choosenServer;
    //panels:
    private JPanel serverNameInputPanel;
    private JPanel ButtonsPanel;

    public ServerListView(final Controller ctrl) {
        super("Reversi");
        this.ctrl = ctrl;

        //create pop-up menu of choices for servers
        serverList = new Choice();

        LOGGER.log(Level.SEVERE, "Getting the server list...");
        //at the beginning get the available server list with timout 
        int timeoutCtr = 50; // 1 sec max
        int timeout = 20; // msec
        
        String[] availableServers = ctrl.getAvailableServerList(); // get available server list
        while (timeoutCtr-- > 0 && (availableServers.length == 0 || availableServers[0] == null) ) {
            try {
                Thread.sleep(timeout); //if the list is empty retry after 20 msec
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerListView.class.getName()).log(Level.SEVERE, null, ex);
            }
            availableServers = ctrl.getAvailableServerList();  // get available server list
        }
        
        LOGGER.log(Level.SEVERE, "Server list recived");
        
        //add servers to popup menu
        if (availableServers.length == 0 || availableServers[0] == null) {
            serverList.add("Nincs elérhető szerver..."); //there are no available servers
        } else {
            for (String s : availableServers) {
                LOGGER.log(Level.INFO, "Available server: {0}", s);
                serverList.add(s);
            }
            choosenServer = serverList.getSelectedItem(); //set the selected server
        }
        serverList.addItemListener(this); //add listener to popup menu

        serverNameInputPanel = new JPanel(); //create new panel for popup menu
        serverNameInputPanel.setLayout(new GridLayout(2, 1)); //setup layout
        serverNameInputPanel.add(new Label("Szerverek:")); //add label to menu
        serverNameInputPanel.add(serverList); //add server list

        Dimension buttonSize = new Dimension(100, 25); //set dimension for buttons
        {   // create start button
            startButton = new JButton("Start");
            startButton.setVerticalTextPosition(AbstractButton.CENTER);
            startButton.setHorizontalTextPosition(AbstractButton.LEADING);
            startButton.setActionCommand("start");
            startButton.addActionListener(this);
            startButton.setToolTipText("Let the game begin!");
            startButton.setPreferredSize(buttonSize);
        }

        {   // create refresh button
            refreshButton = new JButton("Frissítés");
            refreshButton.setVerticalTextPosition(AbstractButton.CENTER);
            refreshButton.setHorizontalTextPosition(AbstractButton.LEADING);
            refreshButton.setActionCommand("refresh");
            refreshButton.addActionListener(this);
            refreshButton.setToolTipText("Refresh the server list!");
        }

        //create new panel for buttons
        ButtonsPanel = new JPanel(new BorderLayout()); 
        ButtonsPanel.add(refreshButton, BorderLayout.WEST); //add buttons to panel
        ButtonsPanel.add(startButton, BorderLayout.EAST);

        //add panels to window
        framePanel.add(serverNameInputPanel, BorderLayout.NORTH);
        framePanel.add(ButtonsPanel, BorderLayout.SOUTH);

        //create an empty border inside of the window
        framePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        serverNameInputPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        //Create and set up the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(framePanel);
        setPreferredSize(new Dimension(300, 200)); //set window size

        //Display the window.
        pack();
        setResizable(false); //do not resize the window!
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) { //buttons handler

        switch (e.getActionCommand()) {
            case "refresh": //refresh button clicked
                serverList.removeAll();//remove the elements
                LOGGER.log(Level.FINEST, "Getting the server list...");
                //get the new list
                String[] availableServers = ctrl.getAvailableServerList();
                LOGGER.log(Level.FINEST, "Server list recived");
                if (availableServers.length == 0 || availableServers[0] == null) { //no servers
                    serverList.add("Nincs elérhető szerver...");
                } else {
                    for (String s : availableServers) {
                        LOGGER.log(Level.INFO, "Available server: {0}", s);
                        serverList.add(s); //add new servern to list
                    }
                }
                choosenServer = serverList.getSelectedItem();//set the choosen server
                break;
            case "start": //start button clicked
                if (choosenServer == null) { //error handling: no choosen  server
                    //popup error message dialog
                    JOptionPane.showMessageDialog(this, "Nincs szerver kiválasztva!", "Reversi", JOptionPane.ERROR_MESSAGE); 
                } else {
                    dispose(); //close this window
                    ctrl.startClientGame(name, choosenServer); //start the game
                }
                break;
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) { //popup-menu interaction handler
        if (e.getSource().equals(serverList)) {
            choosenServer = serverList.getSelectedItem(); //set the choosen server
        }
    }
}
