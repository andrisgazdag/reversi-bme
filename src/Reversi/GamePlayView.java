/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reversi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author Alex
 */
public class GamePlayView extends JFrame {

    private Controller ctrl;
    private DrawPanel drawPanel = new DrawPanel();
    private final static int BORDER_SIZE = 10;
    private int width;
    private int height;
    private int cellSize = 50;
    private int offsetX = 0;
    private int offsetY = 0;
    private int tableSize = 12;  //a tábla mérete tableSizextableSize
    private int CircleSize = cellSize;
    JPanel inputPanel = new JPanel();

    GamePlayView(int size) {
        super(/*"Reversi"*/);
        //ctrl = c;
        if(size==8 || size==10 || size== 12)
            setTableSize(size);
        width = tableSize * cellSize + 2 * BORDER_SIZE;//+2*BORDER_SIZE;
        height = width;


        //setSize(700, 750);
        setSize(width + tableSize + 20, height + tableSize + 70);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Save Game");

        JMenuItem menuItem = new JMenuItem("Client");
        menuItem.addActionListener(new MenuListener());
        menu.add(menuItem);

        menuItem = new JMenuItem("Server");
        menuItem.addActionListener(new MenuListener());
        menu.add(menuItem);

        menuBar.add(menu);

        menuItem = new JMenuItem("Load");
        menuItem.addActionListener(new MenuListener());
        menuBar.add(menuItem);

        menuItem = new JMenuItem("Exit");
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
                System.out.println("X:" + e.getX() + " Y:" + e.getY());
               // ctrl.sendClick(new Point(e.getX(), e.getY()));
                //itt meghatározzuk, hogy mely négyzetrácsba kattintottunk majd
                //enenk megfelelően rakjuk be a pontokat tároló listába a pontot
                int Y = ((e.getY() - BORDER_SIZE) / cellSize);
                int X = ((e.getX() - BORDER_SIZE) / cellSize);
                //le kell csekkolni, hogy a keretre kattintást ne érzékelje
//                if (X < tableSize && Y < tableSize && e.getX() > BORDER_SIZE && e.getY() > BORDER_SIZE) {
//                    addPoint(new Point(X * cellSize + BORDER_SIZE, Y * cellSize + BORDER_SIZE),  1);
//                }
                if(e.isMetaDown()) //jobb egér klikk
                {
                    if (X < tableSize && Y < tableSize && e.getX() > BORDER_SIZE && e.getY() > BORDER_SIZE) {
                    addPoint(new Point(X * cellSize + BORDER_SIZE, Y * cellSize + BORDER_SIZE),  1);
                }
                }
                else  //bal egér klikk
                {
                    if (X < tableSize && Y < tableSize && e.getX() > BORDER_SIZE && e.getY() > BORDER_SIZE) {
                    addPoint(new Point(X * cellSize + BORDER_SIZE, Y * cellSize + BORDER_SIZE),  0);
                }
                }
            }
        });
        
        setVisible(true);
        drawPanel.repaint();  //nem mukodik
        for(int i=0;i<8;++i)
        {
            addPoint(new Point(i * cellSize + BORDER_SIZE, i * cellSize + BORDER_SIZE), i%2);
        }
            
    }

 public void setController(Controller c)
    {
        ctrl = c;
    }
void setTableSize(int t)
{
    tableSize=t;
}
    void addPoint(Point p, int Color) {
        if(Color==1)
        {
            drawPanel.pointsBlue.add(p);
        }
        else 
        {
            drawPanel.pointsRed.add(p);
        }
        drawPanel.repaint();
    }

    private class DrawPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        ArrayList<Point> pointsBlue = new ArrayList<Point>();
        ArrayList<Point> pointsRed = new ArrayList<Point>();

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
            for (int i = 0; i < tableSize; i++) {
                for (int j = 0 + i % 2; j < tableSize; j += 2) {
                    g.fillRect(BORDER_SIZE + j * cellSize, BORDER_SIZE + i * cellSize, cellSize, cellSize);
                }
            }

            //négyzetrács  kirajzolása  
            g.setColor(Color.black);
            for (int i = 1; i < tableSize; ++i)//vízszintes vonalak
            {
                g.drawLine(BORDER_SIZE, BORDER_SIZE + i * cellSize,
                        width - BORDER_SIZE, i * cellSize + BORDER_SIZE);
            }

            for (int i = 1; i < tableSize; ++i) //FÜGGŐLEGES VOtableSizeALAK
            {
                g.drawLine(BORDER_SIZE + i * cellSize, BORDER_SIZE,
                        BORDER_SIZE + i * cellSize, height - BORDER_SIZE);
            }

            //pöttyök kirajzolása
            g.setColor(Color.red);
            for (Point p : pointsRed) {
                g.fillOval(p.x, p.y, CircleSize, CircleSize);
            }
            
            g.setColor(Color.blue);
            for (Point p : pointsBlue) {
                g.fillOval(p.x, p.y, CircleSize, CircleSize);
            }
        }
    }

    private class MenuListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
           /* if (e.getActionCommand().equals("Clear")) {
                drawPanel.points.clear();
                drawPanel.repaint();
            }
            if (e.getActionCommand().equals("Exit")) {
                System.exit(0);
            }
            if (e.getActionCommand().equals("Server")) {
               // ctrl.startServer();
            }
            if (e.getActionCommand().equals("Client")) {
                //ctrl.startClient();
            }*/
        }
    }
}
