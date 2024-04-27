import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int frameWidth = 360;
    int frameHeight = 640;

    //image attributes
    Image backgroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;
    // Player
    int playerStartPosX = frameWidth / 8;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;
    Player player;

    //pipe attribut
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    ArrayList<Pipe> pipes;

    //game logic
    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;

    //constructor
    public FlappyBird(){
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setFocusable(true);
        addKeyListener(this);

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(scoreLabel);

        //load images
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        //pipes cooldown
        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        pipesCooldown.start();

        gameLoop = new Timer(1000/60,this);
        gameLoop.start();
    }

    private JLabel scoreLabel;
    private int score = 0;

    public void updateScore() {
        score++; // Tambah skor
        scoreLabel.setText("Score: " + score); // Perbarui teks JLabel skor
    }

    private boolean gameOver = false;

    public void gameOver() {
        gameOver = true; // Set status game over
        gameLoop.stop(); // Hentikan permainan
        pipesCooldown.stop(); // Hentikan penambahan pipa
        JOptionPane.showMessageDialog(this, "Game Over", "Flappy Bird", JOptionPane.PLAIN_MESSAGE);
    }

    public boolean checkOutOfBounds() {
        return player.getPosY() >= frameHeight; // True jika pemain jatuh ke bawah JFrame
    }

    public void restartGame() {
        player.setPosY(playerStartPosY); // Set posisi pemain ke posisi awal
        pipes.clear(); // Hapus semua pipa
        gameOver = false; // Set status game over ke false
        gameLoop.start(); // Mulai permainan kembali
        pipesCooldown.start(); // Mulai penambahan pipa kembali
    }

    public void move() {
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipe.getVelocityY());
            if (pipe.getPosX() + pipe.getWidth() < player.getPosX() && !pipe.isPassed()) {
                pipe.setPassed(true);
                updateScore(); // Pembaruan skor saat pemain melewati pipa
            }

        }

        if (checkCollision() || checkOutOfBounds()) {
            gameOver();
        }

    }


    public void placePipes(){
        int randomPipePosY = (int)(pipeStartPosY - pipeHeight/4 - Math.random() * (pipeHeight/2));
        int openingSpace = frameHeight/4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPipePosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPipePosY + pipeHeight + openingSpace, pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(backgroundImage, 0,0, frameWidth, frameHeight, null);

        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);

        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(),null);
        }
    }
//    public void move(){
//        player.setVelocityY(player.getVelocityY() + gravity);
//        player.setPosY(player.getPosY() + player.getVelocityY());
//        player.setPosY(Math.max(player.getPosY(), 0));
//
//        for(int i = 0; i < pipes.size(); i++){
//            Pipe pipe = pipes.get(i);
//            pipe.setPosX(pipe.getPosX() + pipe.getVelocityY());
//        }
//    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            player.setVelocityY(-10);
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restartGame();
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void startGame() {
        gameLoop.start();
        pipesCooldown.start();
    }

    public boolean checkCollision() {
        Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight());
        for (Pipe pipe : pipes) {
            Rectangle pipeRect = new Rectangle(pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight());
            if (playerRect.intersects(pipeRect)) {
                return true; // Ada tabrakan
            }
        }
        return false; // Tidak ada tabrakan
    }



}
