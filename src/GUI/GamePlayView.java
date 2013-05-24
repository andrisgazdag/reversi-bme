package GUI;

import Enums.Field;
import Enums.TableSize;
import Reversi.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
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

    private Controller ctrl;
    private DrawPanel drawPanel = new DrawPanel();
    private final static int BORDER_SIZE = 10;
    private int width;
    private int height;
    private int cellSize = 50;
    private int offsetX = 0;
    private int offsetY = 0;
    private TableSize tableSize;  //a tábla mérete tableSize x tableSize
    private int CircleSize = 34;
    static JLabel ScoreBlue, ScoreRed;
    int scoreBlue, scoreRed;
    JPanel inputPanel = new JPanel();
    JFileChooser fc;
    private boolean keepRedrawing = true;
    private static final Logger LOGGER = Logger.getLogger("Reversi");
    private boolean firstPaintFrame = false;
    Field[][] localTable = null;
    Field[][] gameTable = null;

    public GamePlayView(TableSize size, Controller c) {

        super("Reversi");
        scoreBlue = 0;
        scoreRed = 0;
        ctrl = c;

        if (size == null) {
            tableSize = TableSize.BIG;
        } else {
            tableSize = size;
        }

        width = tableSize.getSize() * cellSize + 2 * BORDER_SIZE;//+2*BORDER_SIZE;
        height = width;

        fc = new JFileChooser();  //fájl betöltéshez kell

        setSize(width + tableSize.getSize() + 0, height + tableSize.getSize() + 90);  //az ablak mérete
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //menük:
        JMenuBar menuBar = new JMenuBar();

        JMenuItem menuItem = new JMenuItem("Mentés");
        menuItem.addActionListener(new MenuListener());
        menuBar.add(menuItem);

        menuItem = new JMenuItem("Betöltés");
        menuItem.addActionListener(new MenuListener());
        menuBar.add(menuItem);

        menuItem = new JMenuItem("Kilépés");
        menuItem.addActionListener(new MenuListener());
        menuBar.add(menuItem);

        setJMenuBar(menuBar);


        add(inputPanel);
        drawPanel.setBounds(offsetX, offsetY, width, height);//(30, 30, 600, 600);//(230, 30, 200, 200);
        //drawPanel.setBorder(BorderFactory.createTitledBorder("Draw"));

        add(drawPanel);
        inputPanel.setBounds(offsetX, offsetY, width, height);
        // inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
        inputPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                //itt meghatározzuk, hogy mely négyzetrácsba kattintottunk majd
                //ennek megfelelően rakjuk be a pontokat tároló listába a pontot
                int Y = ((e.getY() - BORDER_SIZE) / cellSize);
                int X = ((e.getX() - BORDER_SIZE) / cellSize);

                if (X < tableSize.getSize() && Y < tableSize.getSize() && e.getX() > BORDER_SIZE && e.getY() > BORDER_SIZE) {
                    ctrl.iteration(X, Y);
                    LOGGER.log(Level.FINEST, "Mouse Click at: {0} {1}", new Object[]{X, Y});
                }

            }
        });

        //pontszámok kiírása
        ScoreBlue = new JLabel("2");
        ScoreBlue.setForeground(Color.blue);
        ScoreBlue.setFont(new Font("Dialog", Font.BOLD, 30));
        ScoreRed = new JLabel("2");
        ScoreRed.setForeground(Color.red);
        ScoreRed.setFont(new Font("Dialog", Font.BOLD, 30));

        JPanel status = new JPanel();
        status.setLayout(new BorderLayout());
        status.add(ScoreBlue, BorderLayout.WEST);
        status.add(ScoreRed, BorderLayout.EAST);
        add(status, BorderLayout.SOUTH);
        status.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        setVisible(true);  //ablak megjelenítése
        setResizable(false); // ne akarja senki átméretezni az ablakot!

        localTable = new Field[ctrl.getGameState().length][ctrl.getGameState().length];
        gameTable = ctrl.getGameState();

    }

    @Override
    public void run() {
        while (keepRedrawing) {

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

            try {
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

    public void updateGamePlayView() {
        LOGGER.log(Level.FINE, "Notifying view about update...");
        gameTable = ctrl.getGameState();
    }

    private class DrawPanel extends JPanel {

        private boolean firstPaintPanel = true;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            LOGGER.log(Level.FINER, "Repainting the game...");

            //KERET KIRAJZOLÁSA
            g.setColor(Color.darkGray);
            for (int i = 0; i < BORDER_SIZE; i++) {
                g.drawRect(i, i, width - 2 * i - 1, height - 2 * i - 1);
            }

            //háttér  (sakktábla)
            g.setColor(Color.lightGray);
            for (int i = 0; i < tableSize.getSize(); i++) {
                for (int j = 0 + i % 2; j < tableSize.getSize(); j += 2) {
                    g.fillRect(BORDER_SIZE + j * cellSize, BORDER_SIZE + i * cellSize, cellSize, cellSize);
                }
            }

            //négyzetrács  kirajzolása  
            g.setColor(Color.black);
            for (int i = 1; i < tableSize.getSize(); ++i)//vízszintes vonalak
            {
                g.drawLine(BORDER_SIZE, BORDER_SIZE + i * cellSize,
                        width - BORDER_SIZE, i * cellSize + BORDER_SIZE);
            }

            for (int i = 1; i < tableSize.getSize(); ++i) //FÜGGŐLEGES VOtableSizeALAK
            {
                g.drawLine(BORDER_SIZE + i * cellSize, BORDER_SIZE,
                        BORDER_SIZE + i * cellSize, height - BORDER_SIZE);
            }

            // körök
            //Field[][] localTable = ctrl.getGameState();
            int[] score = ctrl.getScores();
            for (int i = 0; i < tableSize.getSize(); ++i) {
                for (int j = 0; j < tableSize.getSize(); ++j) {
                    if (localTable[i][j] == Field.BLUE) {
                        g.setColor(Color.blue);
                        g.fillOval(i * cellSize + BORDER_SIZE + (cellSize - CircleSize) / 2, j * cellSize + BORDER_SIZE + (cellSize - CircleSize) / 2, CircleSize, CircleSize);
                    } else if (localTable[i][j] == Field.RED) {
                        g.setColor(Color.red);
                        g.fillOval(i * cellSize + BORDER_SIZE + (cellSize - CircleSize) / 2, j * cellSize + BORDER_SIZE + (cellSize - CircleSize) / 2, CircleSize, CircleSize);
                    }
                }
            }

            // score-ok
            ScoreBlue.setText(Integer.toString(score[1])); //pontszámok megjelenítése
            ScoreRed.setText(Integer.toString(score[0]));
        }
    }

    private class MenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand().equals("Kilépés")) {
                //felmerül a kérdés h itt nem kéne-e itten törölni dolgokat
                dispose();
                ctrl.startReversi();
            }
            if (e.getActionCommand().equals("Mentés")) {

                //felugró ablak megjelenítése
                int returnVal = fc.showSaveDialog(GamePlayView.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    ctrl.saveGame(file);
                    System.out.println("Saving: " + file.getName() + "." + '\n');
                } else {
                    System.out.println("Open command cancelled by user.");
                }

            }
            if (e.getActionCommand().equals("Betöltés")) {
                //felugró ablak megjelenítése
                int returnVal = fc.showOpenDialog(GamePlayView.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    System.out.println("Opening: " + file.getName() + ".");
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        }
    }

    public void showUserWin() {
        // JOptionPane.showMessageDialog(GamePlayView.this, "Győztél!");
        JOptionPane.showMessageDialog(this, "Győztél!", "Reversi", JOptionPane.INFORMATION_MESSAGE);
        setTitle("Reversi - Győztél");
    }

    public void showUserEven() {
        JOptionPane.showMessageDialog(this, "Döntetlen!", "Reversi", JOptionPane.INFORMATION_MESSAGE);
        setTitle("Reversi - Döntetlen!");
    }

    public void showUserLoose() {
        JOptionPane.showMessageDialog(this, "Vesztettél!", "Reversi", JOptionPane.INFORMATION_MESSAGE);
        setTitle("Reversi - Vesztettél!");
    }
}
