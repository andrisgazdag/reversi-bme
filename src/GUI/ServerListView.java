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
 * TODO: what does this do???
 *
 * @author Alex
 */
public class ServerListView extends JFrame implements ActionListener, ItemListener {

    Controller ctrl = null;
    private static final Logger LOGGER = Logger.getLogger("Reversi");
    private JPanel framePanel = new JPanel(new BorderLayout());
    protected JButton startButton;
    protected JButton refreshButton;
    private Choice serverList;
    String name, choosenServer;
    private JPanel serverNameInputPanel;
    private JPanel ButtonsPanel;

    public ServerListView(final Controller ctrl) {
        super("Reversi");
        this.ctrl = ctrl;

        serverList = new Choice();

        LOGGER.log(Level.SEVERE, "Getting the server list...");
        String[] availableServers = ctrl.getAvailableServerList();
        LOGGER.log(Level.SEVERE, "Server list recived");
        if (availableServers.length == 0 || availableServers[0] == null) {
            serverList.add("Nincs elérhető szerver...");
        } else {
            for (String s : availableServers) {
                LOGGER.log(Level.INFO, "Available server: {0}", s);
                serverList.add(s);
            }
        }
        serverList.addItemListener(this);

        serverNameInputPanel = new JPanel();
        serverNameInputPanel.setLayout(new GridLayout(2, 1));
        serverNameInputPanel.add(new Label("Szerverek:"));
        serverNameInputPanel.add(serverList);

        Dimension buttonSize = new Dimension(100, 25);
        {   // start button
            startButton = new JButton("Start");
            startButton.setVerticalTextPosition(AbstractButton.CENTER);
            startButton.setHorizontalTextPosition(AbstractButton.LEADING);
            startButton.setMnemonic(KeyEvent.VK_D);
            startButton.setActionCommand("start");
            startButton.addActionListener(this);
            startButton.setToolTipText("Let the game begin!");
            startButton.setPreferredSize(buttonSize);
        }



        {   // refresh button
            refreshButton = new JButton("Frissítés");
            refreshButton.setVerticalTextPosition(AbstractButton.CENTER);
            refreshButton.setHorizontalTextPosition(AbstractButton.LEADING);
            refreshButton.setMnemonic(KeyEvent.VK_D);
            refreshButton.setActionCommand("refresh");
            refreshButton.addActionListener(this);
            refreshButton.setToolTipText("Refresh the server list!");
        }

        ButtonsPanel = new JPanel(new BorderLayout());
        ButtonsPanel.add(refreshButton, BorderLayout.WEST);
        ButtonsPanel.add(startButton, BorderLayout.EAST);

        framePanel.add(serverNameInputPanel, BorderLayout.NORTH);
        framePanel.add(ButtonsPanel, BorderLayout.SOUTH);

        framePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // az egész ablakon belül 20 pixel keret
        serverNameInputPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        //Create and set up the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(framePanel);

        setPreferredSize(new Dimension(300, 200));

        //Display the window.
        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "refresh":
                serverList.removeAll();
                LOGGER.log(Level.FINEST, "Getting the server list...");
                String[] availableServers = ctrl.getAvailableServerList();
                LOGGER.log(Level.FINEST, "Server list recived");
                if (availableServers.length == 0 || availableServers[0] == null) {
                    serverList.add("Nincs elérhető szerver...");
                } else {
                    for (String s : availableServers) {
                        LOGGER.log(Level.INFO, "Available server: {0}", s);
                        serverList.add(s);
                    }
                }
                choosenServer = serverList.getSelectedItem(); //ez azért kell, mert különben csak akkor állítódik be a kiválasztott szerver, hogy ha rákattint Béla
                break;
            case "start":
                if (choosenServer == null) {
                    JOptionPane.showMessageDialog(this, "Nincs szerver kiválasztva!", "Reversi", JOptionPane.ERROR_MESSAGE);
                } else {
                    dispose();
                    ctrl.startClientGame(name, choosenServer);
                }
                break;
        }

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(serverList)) {
            choosenServer = serverList.getSelectedItem();
        }
    }
}
