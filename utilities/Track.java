package utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Track extends JPanel implements ActionListener {

    private final int B_WIDTH = 800;                //valtozok deklaralasa es inicializalasa a JPanel es Player mereteivel
    private final int B_HEIGHT = 800;
    private final int DOT_SIZE = 25;


    private Player trump;                                   //Jatekosok inicializasa
    private Player biden;
    private Collisions collisions;
    private int vote_x;
    private int vote_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean ADirection = false;
    private boolean DDirection = true;
    private boolean WDirection = false;
    private boolean SDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image vote;

    public Track() {            //a palya felepitese
        initTrack();
    }

    private void initTrack() {

        this.biden = new Player();
        this.trump = new Player();
        this.collisions = new Collisions();
        addKeyListener(new TAdapter());                 //action listener hozzaadasa
        setBackground(Color.black);
        setFocusable(true);                             //a fokuszba allitani a kodot, ezaltal elerhetoek az action Listenerek

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();

        /*Collisions collisions = new Collisions();
        inGame = collisions.getCondition();*/
    }

    private void loadImages() {

        ImageIcon bodyBIm = new ImageIcon("D:/SnakeMulti/src/resources/rsz_blue_dot_25.png");
        biden.setBody(bodyBIm.getImage());

        ImageIcon bodyTIm = new ImageIcon("D:/SnakeMulti/src/resources/rsz_red_dot_25.png");
        trump.setBody(bodyTIm.getImage());

        ImageIcon voteIm = new ImageIcon("D:/SnakeMulti/src/resources/rsz_vote_25.png");
        vote = voteIm.getImage();

        ImageIcon headTIm = new ImageIcon("D:/SnakeMulti/src/resources/rsz_trump_25.png");
        trump.setHead(headTIm.getImage());

        ImageIcon headBIm = new ImageIcon("D:/SnakeMulti/src/resources/rsz_biden_25.png");
        biden.setHead(headBIm.getImage());
    }

    private void initGame() {

        int dots = 3;

        for (int i = 0; i < dots; i++) {
            trump.setX(175 - i * DOT_SIZE,i);
            trump.setY(175, i);
            biden.setX(325 - i * DOT_SIZE, i);
            biden.setY(325, i);
        }

        locateBadge();

        int DELAY = 180;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {


        if (inGame) {
            g.drawImage(vote, vote_x, vote_y, this);

            for (int i = 0; i < trump.getDots(); i++) {
                if (i == 0) {
                    g.drawImage(trump.getHead(), trump.getX()[i], trump.getY()[i], this);

                } else {
                    g.drawImage(trump.getBody(), trump.getX()[i], trump.getY()[i], this);
                }
            }

            for (int i = 0; i < biden.getDots(); i++) {
                if (i == 0) {

                    g.drawImage(biden.getHead(), biden.getX()[i], biden.getY()[i], this);
                } else {

                    g.drawImage(biden.getBody(), biden.getX()[i], biden.getY()[i], this);
                }
            }

        } else {

            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {
        String winner;
        Font small;

        if(trump.getCounter() > biden.getCounter()) {
            winner = "The new-old President is TRUMP!";
            small = new Font("Courier", Font.BOLD, 28);
            this.setBackground(Color.red);
        }
        else {
            if(trump.getCounter() < biden.getCounter()) {
                winner = "The new President is BIDEN!";
                small = new Font("Courier", Font.BOLD, 28);
                this.setBackground(Color.blue);
            }
            else{
                winner = "It's a TIE!";
                small = new Font("Courier", Font.BOLD, 40);
            }
        }


        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(winner, (B_WIDTH - metr.stringWidth(winner)) / 2, B_HEIGHT / 2);
    }

    private void checkVote() {
        if (trump.getX()[0] == vote_x && trump.getY()[0] == vote_y)
        {
            trump.setDots(trump.getDots() + 1);
            trump.setCounter(trump.getCounter() + 1);
            locateBadge();
        }

        if (biden.getX()[0] == vote_x && biden.getY()[0] == vote_y)
        {
            biden.setDots(biden.getDots() + 1);
            biden.setCounter(biden.getCounter() + 1);
            locateBadge();
        }

    }

    private void movements() {

        for (int i = biden.getDots(); i > 0; i--) {
            biden.getX()[i] = biden.getX()[(i - 1)];
            biden.getY()[i] = biden.getY()[(i - 1)];
        }

        for (int i = trump.getDots(); i > 0; i--) {
            trump.getX()[i] = trump.getX()[(i - 1)];
            trump.getY()[i] = trump.getY()[(i - 1)];
        }

        if (leftDirection) {
            trump.getX()[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            trump.getX()[0] += DOT_SIZE;
        }

        if (upDirection) {
            trump.getY()[0] -= DOT_SIZE;
        }

        if (downDirection) {
            trump.getY()[0] += DOT_SIZE;
        }

        if (ADirection) {
            biden.getX()[0] -= DOT_SIZE;
        }

        if (DDirection) {
            biden.getX()[0] += DOT_SIZE;
        }

        if (WDirection) {
            biden.getY()[0] -= DOT_SIZE;
        }

        if (SDirection) {
            biden.getY()[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {

        for (int i = 0; i < trump.getDots(); i++) {

            if ((i > 4) && (trump.getX()[0] == trump.getX()[i]
                    && (trump.getY()[0] == trump.getY()[i]))) {                           //itt lehetett volna a ket if()-et egybe is irni
                inGame = false;                                       //de az atlathatosag kedveert igy irtam, kulon a Biden
            }                                                         //es kulon a Trump koordinataira, a fejere es a vegere

            for(int j = 0; j < biden.getDots(); j++) {
                if(trump.getX()[j] == biden.getX()[i]
                        && trump.getY()[j] == biden.getY()[i]) {
                    inGame = false;
                   /* if(biden.getX()[i] == B_HEIGHT || ) {
                        biden.setCounter(biden.getCounter() - 3);
                    }*/
                }

            }
        }

        for (int i = 0; i < biden.getDots(); i++) {
            if ((i > 4) && (biden.getX()[0] == biden.getX()[i])
                    && (biden.getY()[0] == biden.getY()[i])) {
                inGame = false;
            }

            for(int j = 0; j < trump.getDots(); j++) {
                if(trump.getX()[i] == biden.getX()[j]
                        && trump.getY()[i] == biden.getY()[j])
                    inGame = false;
                /*if(i == 0) {
                    trump.setCounter(trump.getCounter() - 3);
                }*/
            }
        }

        if (trump.getY()[0] >= B_HEIGHT) {
            inGame = false;
            trump.setCounter(trump.getCounter() - 3);
        }

        if (trump.getY()[0] < 0) {
            inGame = false;
            trump.setCounter(trump.getCounter() - 3);
        }

        if (trump.getX()[0] >= B_WIDTH) {
            inGame = false;
            trump.setCounter(trump.getCounter() - 3);
        }

        if (trump.getX()[0] < 0) {
            inGame = false;
            trump.setCounter(trump.getCounter() - 3);
        }

        if (biden.getY()[0] >= B_HEIGHT) {
            inGame = false;
            biden.setCounter(biden.getCounter() - 3);
        }

        if (biden.getY()[0] < 0) {
            inGame = false;
            biden.setCounter(biden.getCounter() - 3);
        }

        if (biden.getX()[0] >= B_WIDTH) {
            inGame = false;
            biden.setCounter(biden.getCounter() - 3);
        }

        if (biden.getX()[0] < 0) {
            inGame = false;
            biden.setCounter(biden.getCounter() - 3);
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void locateBadge() {

        int RAND_POS = 29;
        int rand = (int) (Math.random()*RAND_POS) ;
        if(rand < 30) {
            vote_x = ((rand * DOT_SIZE));
        }

        rand = (int) (Math.random()*RAND_POS);
        if(rand < 20) {
            vote_y = ((rand * DOT_SIZE));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkVote();
            checkCollision();
            movements();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_A) && (!DDirection)) {
                ADirection = true;
                WDirection = false;
                SDirection = false;
            }

            if ((key == KeyEvent.VK_D) && (!ADirection)) {
                DDirection = true;
                WDirection = false;
                SDirection = false;
            }

            if ((key == KeyEvent.VK_W) && (!SDirection)) {
                WDirection = true;
                DDirection = false;
                ADirection = false;
            }

            if ((key == KeyEvent.VK_S) && (!WDirection)) {
                SDirection = true;
                DDirection = false;
                ADirection = false;
            }
        }
    }
}
