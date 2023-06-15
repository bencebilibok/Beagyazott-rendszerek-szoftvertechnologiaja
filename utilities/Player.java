package utilities;

import java.awt.*;


public class Player {
    private final int ALL_DOTS = 1024;
    private final int x[] = new int[ALL_DOTS];     //vektorok a Player-ek testreszeinek x es y koordinatainak tarolasara
    private final int y[] = new int[ALL_DOTS];


    private int dots;
    private Image head;
    private Image body;
    private int Counter = 0;

    public int getCounter() {
        return Counter;
    }

    public void setCounter(int counter) {
        Counter = counter;
    }

    public Player() {
        this.dots = 3;
    }

    public int[] getX() {
        return x;
    }

    public int[] getY() {
        return y;
    }

    public void setX(int res, int i) {
        x[i] = res;
    }

    public void setY(int res, int i) {
        x[i] = res;
    }

    public int getDots() {
        return dots;
    }

    public void setDots(int dots) {
        this.dots = dots;
    }

    public Image getHead() {
        return head;
    }

    public void setHead(Image head) {
        this.head = head;
    }

    public Image getBody() {
        return body;
    }

    public void setBody(Image body) {
        this.body = body;
    }
}
