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
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class GamePlayView extends JFrame {

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

    public GamePlayView(TableSize size, Controller c) {
        super("Reversi");
        scoreBlue = 0;
        scoreRed = 0;
        ctrl = c;
        
        if(size == null)
        {
            tableSize = TableSize.BIG;
        }
        else
        {
            tableSize = size;
        }
        
        width = tableSize.getSize() * cellSize + 2 * BORDER_SIZE;//+2*BORDER_SIZE;
        height = width;

        fc = new JFileChooser();  //fájl betöltéshez kell
        //setSize(700, 750);
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
                // System.out.println("X:" + e.getX() + " Y:" + e.getY());
                // ctrl.sendClick(new Point(e.getX(), e.getY()));
                //itt meghatározzuk, hogy mely négyzetrácsba kattintottunk majd
                //ennek megfelelően rakjuk be a pontokat tároló listába a pontot
                int Y = ((e.getY() - BORDER_SIZE) / cellSize);
                int X = ((e.getX() - BORDER_SIZE) / cellSize);
                //le kell csekkolni, hogy a keretre kattintást ne érzékelje
//                if (X < tableSize && Y < tableSize && e.getX() > BORDER_SIZE && e.getY() > BORDER_SIZE) {
//                    addPoint(new Point(X * cellSize + BORDER_SIZE, Y * cellSize + BORDER_SIZE),  1);
//                }
                if (e.isMetaDown()) //jobb egér klikk
                {
                    if (X < tableSize.getSize() && Y < tableSize.getSize() && e.getX() > BORDER_SIZE && e.getY() > BORDER_SIZE) {
                        addPoint(new Point(X * cellSize + BORDER_SIZE, Y * cellSize + BORDER_SIZE), 1);
                    }
                } else //bal egér klikk
                {

                    if (X < tableSize.getSize() && Y < tableSize.getSize() && e.getX() > BORDER_SIZE && e.getY() > BORDER_SIZE) {
//                        addPoint(new Point(X * cellSize + BORDER_SIZE, Y * cellSize + BORDER_SIZE), 0);
                        ctrl.iteration(X, Y);
                    }

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

        //setLocation(400, 0);

        setVisible(true);  //ablak megjelenítése
        setResizable(false); // ne akarja senki átméretezni az ablakot!

//        for (int i = 0; i < 8; ++i) {
//            addPoint(new Point(i * cellSize + BORDER_SIZE, i * cellSize + BORDER_SIZE), i % 2);
//        }
        
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawPanel.repaint();
    }

    private void addPoint(Point p, int Color) {
        if (Color == 1) {
            drawPanel.pointsBlue.add(p);
        } else {
            drawPanel.pointsRed.add(p);
        }
        drawPanel.repaint();
    }
    
    public void reDraw(Field[][] table, int[] score)
    {
        drawPanel.pointsBlue.clear();
        drawPanel.pointsRed.clear();
        for(int i=0; i<tableSize.getSize();++i)
        {
           for(int j=0; j<tableSize.getSize();++j) 
           {
               if(table[i][j] == Field.BLUE)
               {
                   addPoint(new Point(i * cellSize + BORDER_SIZE+(cellSize-CircleSize)/2, j * cellSize + BORDER_SIZE +(cellSize-CircleSize)/2), 1);
               }
               else if(table[i][j] == Field.RED)
               {
                   addPoint(new Point(i * cellSize + BORDER_SIZE+(cellSize-CircleSize)/2, j * cellSize + BORDER_SIZE+(cellSize-CircleSize)/2), 0);
               }
           }
        }
    }

    private class DrawPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        ArrayList<Point> pointsBlue = new ArrayList<>();
        ArrayList<Point> pointsRed = new ArrayList<>();

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

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


            scoreBlue = 0;
            scoreRed = 0;
            //pöttyök kirajzolása
            g.setColor(Color.red);
            for (Point p : pointsRed) {
                g.fillOval(p.x, p.y, CircleSize, CircleSize);
                scoreRed++;
            }

            g.setColor(Color.blue);
            for (Point p : pointsBlue) {
                g.fillOval(p.x, p.y, CircleSize, CircleSize);
                scoreBlue++;
            }
            ScoreBlue.setText(Integer.toString(scoreBlue)); //pontszámok megjelenítése
            ScoreRed.setText(Integer.toString(scoreRed));
        }
    }

    private class MenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand().equals("Kilépés")) {
                //System.exit(0);
                showUserWin();
                showUserLoose();

            }
            if (e.getActionCommand().equals("Mentés")) {

                //felugró ablak megjelenítése
                int returnVal = fc.showSaveDialog(GamePlayView.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
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
    }

    public void showUserLoose() {
        JOptionPane.showMessageDialog(this, "Vesztettél!", "Reversi", JOptionPane.INFORMATION_MESSAGE);
    }

}
