package GUI;

import Enums.Field;
import Enums.TableSize;
import Reversi.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GamePlayView extends JFrame implements Runnable {

    private static final Logger LOGGER = Logger.getLogger("Reversi");
    private Controller ctrl;
    //create a drawPanel to draw on it the game
    private DrawPanel drawPanel = new DrawPanel();
    private final static int BORDER_SIZE = 10; // size of the border of the tabel (in px)
    private int width; // width of table
    private int height; // height of table
    private int cellSize = 50; //size of one cell on the table (in pixel)
    private TableSize tableSize;  //size of table tableSize x tableSize (8x8,10x10,12x12)
    private int CircleSize = 34; //size of filled circles in pixel
    private JLabel ScoreBlue, ScoreRed; //Score labels
    private JPanel inputPanel = new JPanel(); // create an input panel to detect user actions
    private JFileChooser fc; // in single mode to load/save a file
    private boolean keepRedrawing = true;
    private int scoreBlue, scoreRed;
    private Field[][] localTable = null;
    private Field[][] gameTable = null;

    public GamePlayView(TableSize size, Controller c) {

        super("Reversi");
        //set window title corresponding to game mode
        if (c.getNetworkCommunicator() != null) {
            setTitle("Reversi: " + c.getNetworkCommunicator().gameType.toString());
        }
        //initial values
        scoreBlue = 0;
        scoreRed = 0;
        ctrl = c;

        if (size == null) {
            tableSize = TableSize.BIG;
        } else {
            tableSize = size;
        }
        //calculate the size of the table
        width = tableSize.getSize() * cellSize + 2 * BORDER_SIZE;
        height = width;

        fc = new JFileChooser();  // for load/save a file

        //set window size
        setSize(width + tableSize.getSize() + 0, height + tableSize.getSize() + 90);  
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); //set the layout manager

        //create menubar
        JMenuBar menuBar = new JMenuBar();

        //load/save is available in single mode
        if (c.getNetworkCommunicator() == null) {
            JMenuItem menuItem = new JMenuItem("Mentés"); //add new menu item "Mentés"
            menuItem.addActionListener(new MenuListener()); //add listener
            menuBar.add(menuItem); //add this item to menubar
            
            menuItem = new JMenuItem("Betöltés");
            menuItem.addActionListener(new MenuListener());
            menuBar.add(menuItem);

            menuItem = new JMenuItem("Új játék");
            menuItem.addActionListener(new MenuListener());
            menuBar.add(menuItem);
        } else {
            JMenuItem menuItem = new JMenuItem("Új játék");
            menuItem.addActionListener(new MenuListener());
            menuBar.add(menuItem);
        }

        setJMenuBar(menuBar);
        add(inputPanel); //add the input panel to window to detect user actions
        drawPanel.setBounds(0, 0, width, height); //set bounds of the input panel

        add(drawPanel); //add drawPanel to window
        inputPanel.setBounds(0, 0, width, height);
        inputPanel.addMouseListener(new MouseAdapter() { //add listener to inputpanel
            @Override
            public void mousePressed(MouseEvent e) { // mouse click handler

                int Y = ((e.getY() - BORDER_SIZE) / cellSize); //calculate on which cell clicked the user
                int X = ((e.getX() - BORDER_SIZE) / cellSize);
                //check the result, is it in valid area?
                if (X < tableSize.getSize() && Y < tableSize.getSize() && e.getX() > BORDER_SIZE && e.getY() > BORDER_SIZE) {
                    ctrl.iteration(X, Y);  //send the coordinates to controller
                    LOGGER.log(Level.FINEST, "Mouse Click at: {0} {1}", new Object[]{X, Y});
                }
            }
        });

        // show scores
        ScoreBlue = new JLabel("2"); //default value is 2
        ScoreBlue.setForeground(Color.blue); //set color
        ScoreBlue.setFont(new Font("Dialog", Font.BOLD, 30)); //set font, size
        ScoreRed = new JLabel("2");
        ScoreRed.setForeground(Color.red);
        ScoreRed.setFont(new Font("Dialog", Font.BOLD, 30));

        //create new panel for scores
        JPanel status = new JPanel();
        status.setLayout(new BorderLayout());
        status.add(ScoreBlue, BorderLayout.WEST); //add scores to panel
        status.add(ScoreRed, BorderLayout.EAST);
        add(status, BorderLayout.SOUTH); //add this panel to window
        status.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); //separate from drawPanel

        setVisible(true);  //show window
        setResizable(false); //do not resize the window!

        localTable = new Field[ctrl.getGameState().length][ctrl.getGameState().length];
        gameTable = ctrl.getGameState();

    }

    @Override
    public void run() { //this GUI runs in a separat thread

        while (keepRedrawing) { //in the future might be useful  (for exit point)

            // check if something has changed
            boolean somethingChanged = false;

            for (int i = 0; i < localTable.length && !somethingChanged; i++) {
                for (int j = 0; j < localTable.length && !somethingChanged; j++) {
                    if (gameTable[i][j] != localTable[i][j]) {
                        somethingChanged = true;
                        LOGGER.log(Level.FINER, "Difference was detected!");
                    }
                }
            }

            // repaint if something has changed
            if (somethingChanged) {
                for (int i = 0; i < localTable.length; i++) {
                    System.arraycopy(gameTable[i], 0, localTable[i], 0, localTable.length);
                }
                drawPanel.repaint();
            }

            try { //sleep this thread for 40 msec
                Thread.sleep(40);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override 
    public void paint(Graphics g) {

        super.paint(g);
        drawPanel.repaint();

    }

    public void updateGamePlayView() { // view update
        LOGGER.log(Level.FINE, "Notifying view about update...");
        gameTable = ctrl.getGameState(); //get the new state of the game
    }

    private class DrawPanel extends JPanel { // inner class for drawpanel

        @Override
        protected void paintComponent(Graphics g) { // paint the components of the game
            super.paintComponent(g);

            LOGGER.log(Level.FINER, "Repainting the game...");

            //draw Border lines
            g.setColor(Color.darkGray);
            for (int i = 0; i < BORDER_SIZE; i++) {
                g.drawRect(i, i, width - 2 * i - 1, height - 2 * i - 1);
            }

            //draw backround
            g.setColor(Color.lightGray);
            for (int i = 0; i < tableSize.getSize(); i++) {
                for (int j = 0 + i % 2; j < tableSize.getSize(); j += 2) {
                    g.fillRect(BORDER_SIZE + j * cellSize, BORDER_SIZE + i * cellSize, cellSize, cellSize);
                }
            }

            //draw isolator lines  
            g.setColor(Color.black);
            for (int i = 1; i < tableSize.getSize(); ++i) // horizontal lines
            {
                g.drawLine(BORDER_SIZE, BORDER_SIZE + i * cellSize,
                        width - BORDER_SIZE, i * cellSize + BORDER_SIZE);
            }

            for (int i = 1; i < tableSize.getSize(); ++i) // vertical lines
            {
                g.drawLine(BORDER_SIZE + i * cellSize, BORDER_SIZE,
                        BORDER_SIZE + i * cellSize, height - BORDER_SIZE);
            }

            // draw circles
            int[] score = ctrl.getScores(); //get games scores
            for (int i = 0; i < tableSize.getSize(); ++i) {
                for (int j = 0; j < tableSize.getSize(); ++j) {
                    if (localTable[i][j] == Field.BLUE) {
                        g.setColor(Color.blue); //draw blue filled circles
                        g.fillOval(i * cellSize + BORDER_SIZE + (cellSize - CircleSize) / 2,
                                j * cellSize + BORDER_SIZE + (cellSize - CircleSize) / 2, CircleSize, CircleSize);
                    } else if (localTable[i][j] == Field.RED) {
                        g.setColor(Color.red);//draw red filled circles
                        g.fillOval(i * cellSize + BORDER_SIZE + (cellSize - CircleSize) / 2,
                                j * cellSize + BORDER_SIZE + (cellSize - CircleSize) / 2, CircleSize, CircleSize);
                    }
                }
            }
            // score update
            ScoreBlue.setText(Integer.toString(score[1])); 
            ScoreRed.setText(Integer.toString(score[0]));
        }
    }

    private class MenuListener implements ActionListener {  //listener for menu

        @Override
        public void actionPerformed(ActionEvent e) { //user action detected

            if (e.getActionCommand().equals("Új játék")) { //start new game
                ctrl.stopNetworkCommunicator(); //stop NetworkCommunicator
                dispose(); //close this window
                ctrl.startReversi(); //start new start setup window
            }
            if (e.getActionCommand().equals("Mentés")) { //save the game

                int returnVal = fc.showSaveDialog(GamePlayView.this); //popup new file browser 
                if (returnVal == JFileChooser.APPROVE_OPTION) { //game saving
                    File file = fc.getSelectedFile(); //get file for save
                    ctrl.saveGame(file); //save the game
                    System.out.println("Saving: " + file.getName() + "." + '\n');
                } else {
                    System.out.println("Open command cancelled by user.");
                }

            }
            if (e.getActionCommand().equals("Betöltés")) { //user wants to load a game
                int returnVal = fc.showOpenDialog(GamePlayView.this); //popup new file browser
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile(); //open selected file
                    System.out.println("Opening: " + file.getName() + ".");
                    keepRedrawing=false;
                    dispose();
                    ctrl.loadGame(file); //load the game
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        }
    }

    public void showUserWin() { //popup user won
        JOptionPane.showMessageDialog(this, "Győztél!", "Reversi", JOptionPane.INFORMATION_MESSAGE);
        setTitle("Reversi - Győztél");
    }

    public void showUserEven() { //popup even state
        JOptionPane.showMessageDialog(this, "Döntetlen!", "Reversi", JOptionPane.INFORMATION_MESSAGE);
        setTitle("Reversi - Döntetlen!");
    }

    public void showUserLoose() { //popup user loosed
        JOptionPane.showMessageDialog(this, "Vesztettél!", "Reversi", JOptionPane.INFORMATION_MESSAGE);
        setTitle("Reversi - Vesztettél!");
    }
}
