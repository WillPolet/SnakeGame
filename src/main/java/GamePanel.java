import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;

// To add a screen at the beginning : Need to put running to false, and make a level selector
// Running is to start the movement of the snake so I need another variable.

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE; // 600*600 / 25 == 14 400
    static final int DELAY = 75;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int [GAME_UNITS]; // An array of 14 400 lenght
    final int[] wallx = new int[576];
    final int[] wally = new int[576];
    ArrayList<Integer> wallX = new ArrayList<>();
    ArrayList<Integer> wallY = new ArrayList<>();
    int bodyParts = 6;
    int applesEaten; // Same as define as 0
    int appleX;
    int appleY;
    char direction = 'R'; // Right Left Up Down
    boolean running = true;
    boolean selectLevel = false;
    Timer timer;
    Random random;
    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        // Initialize the snake at the center of the screen
        for (int i = 0; i<bodyParts;i++){
            x[i] = SCREEN_WIDTH/2 - i*UNIT_SIZE; // x[0] = 300 x[1] = 275 etc
            y[i]= SCREEN_WIDTH/2;
        }

        newApple();
//        running = true;
        timer = new Timer(DELAY,this); // Pass delay value to change the speed of game.
        timer.start();
    }

    // Need to add screens there ? It's a little special for end screen bc it's a conditional screen
    // I can do a first one there, and draw others only when first one is done (at the end)
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        levelSelect(g,0);
    }

    private void levelSelect(Graphics g, int level) {
        if (selectLevel){
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free",Font.BOLD,30));
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            g.drawString("Select level : ", (SCREEN_WIDTH - metrics1.stringWidth("Select level :"))/2,g.getFont().getSize());

//            levelSelect(g, 1);


        }
        else {
            super.paintComponent(g);
            wall(g, 1);
            draw(g);
        }
    }

    public void draw(Graphics g){
        if(running) {
            g.setColor(Color.darkGray);
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) { // To make a grid, i is from 0 to 23
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT); // take four parameters, coordinate to draw line between A & B
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE); // Same but with horizontal lines
            }
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); //Puting a cricle at a random place

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 188, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free",Font.BOLD,30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score : " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score : " + applesEaten))/2,g.getFont().getSize());

        }
        else{
            gameOver(g);
        }
    }

    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
        // Condition to do not make appears an apple under the snake
        for (int i = 0 ; i< bodyParts ; i++){
            if (x[i] == appleX && y[i] == appleY){
                newApple();
            }
        }
        // Condition to not make apple appears inside a wall sometimes doesn't work ??
        for (int i = 0; i < wallX.size(); i++){
            if (wallX.get(i) == appleX && wallY.get(i) == appleY){
                System.out.println("An apple in a wall");
                newApple();
            }
        }

    }
    public void move() { // To move the snake
        for(int i = bodyParts; i>0; i--){
            x[i] = x[i-1];
            y[i] = y[i -1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void wall(Graphics g, int level){
        if(level == 1){
            g.setColor(Color.gray);
            for (int i = 0; i < SCREEN_WIDTH ; i+=SCREEN_WIDTH/UNIT_SIZE){ // Wall on first line
                g.fillRect(i,0,UNIT_SIZE,UNIT_SIZE);
                // Adding walls to coordinates of walls in X and Y, need to add i/24
                wallX.add(i+i/24);
                wallY.add(0);
            }
            for (int i = 0; i < SCREEN_WIDTH ; i+=SCREEN_WIDTH/UNIT_SIZE){ // Wall on last line
                g.fillRect(i,SCREEN_HEIGHT - UNIT_SIZE,UNIT_SIZE,UNIT_SIZE);
                wallX.add(i+i/24);
                wallY.add(SCREEN_HEIGHT - UNIT_SIZE);
            }
        }

    }
    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions(){
        // Checks if head collides with body
        for(int i = 1;i<bodyParts;i++){
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        // Check if there's collision with walls needs to put number in unit size at walls creation || Need to use .size instead of .length bc it's an arrayList
        for (int i = 0; i < wallX.size() ; i++){ // loop over the numbers of blocks and if head is at same position than one block game over
//            System.out.println("Head coordonates : (" + x[0] + " ; " + y[0] + ")");
//            if((x[0] == wallx[i]) && (y[0] == wally[i])){
            if((x[0] == wallX.get(i)) && (y[0] == wallY.get(i))){  //Need to use .get bc arrayList
                running = false;
                break;
            }
        }
        // Check if head touches left border
        if(x[0] < 0){
            x[0] = SCREEN_WIDTH - UNIT_SIZE;
        }
        // Check if head touches right border
        if (x[0]>SCREEN_WIDTH - UNIT_SIZE){
            x[0] = 0;
        }
        if(y[0]<0){
            y[0] = SCREEN_HEIGHT - UNIT_SIZE;
        }
        if(y[0]>SCREEN_HEIGHT - UNIT_SIZE){
            y[0] = 0;
        }
        if (!running){
            timer.stop();
        }
    }

    public void gameOver(Graphics g){
        // Score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free",Font.BOLD,30));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score : " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score : " + applesEaten))/2,g.getFont().getSize());
        //GameOver text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free",Font.BOLD,75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2,SCREEN_HEIGHT/2);
        // New game
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free",Font.BOLD,30));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("For New Game Press Enter",(SCREEN_WIDTH - metrics3.stringWidth("For New Game Press Enter"))/2,SCREEN_HEIGHT - g.getFont().getSize());
    }

    public void actionPerformed(ActionEvent e){
        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') {
                        direction = 'L';
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') {
                        direction = 'R';
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') {
                        direction = 'U';
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') {
                        direction = 'D';
                    }
                }
                case KeyEvent.VK_P -> {
                    if (timer.isRunning()){
                        timer.stop();
                    }
                    else {
                        timer.start();
                    }
                }
                case KeyEvent.VK_ENTER -> {
                    if(!running){
                    final Window parentWindow = SwingUtilities.getWindowAncestor(GamePanel.this);
                    parentWindow.dispose();
                     new GameFrame();
                    }
                }
                case KeyEvent.VK_1 -> {

                }
            }

        }
    }
}
