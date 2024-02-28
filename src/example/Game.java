package example;

import java.awt.*;
import java.awt.event.*;
import static java.lang.System.exit;
import java.net.URL;
import javax.swing.*;

import example.Terminator.GameTerminatedException;
import example.Terminator.ReturnToMainMenuException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/* start */
public class Game extends JFrame {

    // flags
    static boolean bgmPlaying = false;

    // First framew
    static JFrame mainWindow;
    static Container tempWindow;
    static Container con;

    // Play Frame
    JFrame windowplay;
    Container conplay;

    // Logo
    JPanel titlePanel;
    JLabel titleLabel;

    // Logo Mechanics
    static JPanel mechslogoPanel;
    static JLabel mechslogoLabel;

    // Logo Mechanics paper
    static JPanel mechanicspanPanel;
    static JLabel mechanicspanLabel;

    // playbutton Game()
    JPanel playPanel;
    JButton playLabel;

    // mechanicsbutton Game()
    JPanel mechsPanel;
    JButton mechsLabel;

    // exitbutton
    static JPanel exitPanel;
    static JButton exitLabel;

    // backbutton
    static JPanel backPanel;
    static JButton backLabel;

    // playagainbutton
    static JPanel playagainPanel;
    static JButton playagainLabel;

    // playbutton play()
    static JFrame window1;
    static Container con1;

    // mechanicbutton mechanic()
    static JFrame window2;
    static Container con2;

    static JLabel blockPanel1 = new JLabel();

    // white panel 1
    static JPanel whitePanel1 = new JPanel();

    // white panel 2
    static JPanel whitePanel2 = new JPanel();

    // white panel 3
    static JPanel whitePanel3 = new JPanel();

    // Logo Mechanics
    static JPanel tetrislogoPanel;
    static JLabel tetrislogoLabel;

    public static GameArea ga;

    JLabel timerLabel, scoreLabel;

    // SwingWorker
    private static SwingWorker<Void, String> worker;

    // BGM
    AudioInputStream zoneStream;
    AudioInputStream bgmStream;
    Clip clip;

    // thread
    public static Thread thread;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Game();
            }
        });
    }

    public Game() {

        // swing worker
        worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    zoneStream = AudioSystem.getAudioInputStream(getClass().getResource("korobeinki_cover.wav"));
                    clip = AudioSystem.getClip();
                    clip.open(zoneStream);
                    clip.setFramePosition(0);
                    clip.loop(-1);
                    clip.start();
                    bgmPlaying = true;
                } catch (Exception err) {
                }
                return null;
            }
        };

        // title and background
        mainWindow = new JFrame("Tetris Twist");
        mainWindow.setSize(800, 800);
        mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        java.net.URL background1 = getClass().getResource("assets/background.jpg");
        mainWindow.setContentPane(new JLabel(new ImageIcon(background1)));
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setResizable(false);
        con = mainWindow.getContentPane();

        // logo
        titlePanel = new JPanel();
        titlePanel.setBounds(150, 70, 500, 500);
        titlePanel.setBackground(Color.blue);
        java.net.URL logo = getClass().getResource("assets/logo.png");
        titleLabel = new JLabel(new ImageIcon(logo));
        titlePanel.add(titleLabel);
        titlePanel.setOpaque(false);
        con.add(titlePanel);

        // playbutton
        playLabel = new JButton("PLAY");
        playPanel = new JPanel();
        playPanel.setBounds(150, 360, 500, 500);
        playPanel.setBackground(Color.blue);
        playLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        playLabel.setBackground(new Color(3, 252, 24));
        playPanel.setOpaque(false);
        playLabel.setBorderPainted(false);
        playLabel.setFocusPainted(false);
        playPanel.add(playLabel);
        con.add(playPanel);
        playLabel.addActionListener(new Action());

        // mechsbutton
        mechsLabel = new JButton("Mechanics");
        mechsPanel = new JPanel();
        mechsPanel.setBounds(150, 460, 500, 500);
        mechsPanel.setBackground(Color.blue);
        mechsLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        mechsLabel.setBackground(new Color(3, 252, 24));
        mechsPanel.setOpaque(false);
        mechsLabel.setBorderPainted(false);
        mechsLabel.setFocusPainted(false);
        mechsPanel.add(mechsLabel);
        con.add(mechsPanel);
        mechsLabel.addActionListener(new mechanics());

        // exitbutton
        exitLabel = new JButton("Exit");
        exitPanel = new JPanel();
        exitPanel.setBounds(150, 560, 500, 500);
        exitPanel.setBackground(Color.blue);
        exitLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        exitLabel.setBackground(new Color(3, 252, 24));
        exitPanel.setOpaque(false);
        exitLabel.setBorderPainted(false);
        exitLabel.setFocusPainted(false);
        exitPanel.add(exitLabel);
        con.add(exitPanel);
        exitLabel.addActionListener(new exit());

        mainWindow.setVisible(true);

    }

    public void startGame() {
        thread = new GameThread(ga);
        thread.start();
    }

    private void initControls() {
        System.out.println("x");
    }

    public void play() {
        window1 = new JFrame("PLAY");
        window1.setSize(800, 800);
        window1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        java.net.URL background2 = getClass().getResource("assets/background234.png");
        window1.setContentPane(new JLabel(new ImageIcon(background2)));
        window1.setLocationRelativeTo(null);
        window1.setResizable(false);
        con1 = window1.getContentPane();

        // panel
        window1.add(whitePanel1);
        window1.add(blockPanel1);
        blockPanel1.setBackground(Color.GRAY);
        blockPanel1.setBounds(570, 200, 200, 200);
        blockPanel1.setBorder(BorderFactory.createEtchedBorder(1));
        whitePanel1.setBackground(Color.GRAY);
        whitePanel1.setBounds(280, 140, 275, 550);
        whitePanel1.setBorder(BorderFactory.createEtchedBorder(1));

        // Instance GameArea
        timerLabel = new JLabel("Timer: ");
        timerLabel.setBounds(570, 470, 100, 30);
        timerLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        scoreLabel = new JLabel("Score: ");
        scoreLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        scoreLabel.setBounds(570, 520, 150, 30);
        con1.add(timerLabel);
        con1.add(scoreLabel);
        con1.setSize(300, 300);
        con1.setLayout(null);
        con1.setVisible(true);
        ga = new GameArea(whitePanel1, 10, blockPanel1, scoreLabel, timerLabel, mainWindow);

        tempWindow = mainWindow.getContentPane();
        mainWindow.setContentPane(con1);
        mainWindow.revalidate();

        // Thread

        // Controls
        initControls();

        // GameArea
        window1.add(ga);

        // backbutton
        backLabel = new JButton("Main Menu");
        backPanel = new JPanel();
        backPanel.setBounds(20, 150, 270, 260);
        backPanel.setBackground(Color.blue);
        backLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        backLabel.setBackground(new Color(252, 136, 3));
        backPanel.setOpaque(false);
        backLabel.setBorderPainted(false);
        backLabel.setFocusPainted(false);
        backPanel.add(backLabel);
        con1.add(backPanel);
        backLabel.addActionListener(new MainMenu());

        // logo mechanic
        tetrislogoPanel = new JPanel();
        tetrislogoPanel.setBounds(100, 1, 600, 500);
        tetrislogoPanel.setBackground(Color.blue);
        java.net.URL logo2 = getClass().getResource("assets/logo3.png");
        tetrislogoLabel = new JLabel(new ImageIcon(logo2));
        tetrislogoPanel.add(tetrislogoLabel);
        tetrislogoPanel.setOpaque(false);
        con1.add(tetrislogoPanel);
        BGM();

    }

    private void BGM() {
        if (!bgmPlaying)
            worker.execute();
    }

    public void mechanic() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        window2 = new JFrame("Mechanics");
        window2.setSize(800, 800);
        window2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        java.net.URL background3 = getClass().getResource("assets/background3.jpg");
        window2.setContentPane(new JLabel(new ImageIcon(background3)));
        window2.setLocationRelativeTo(null);
        window2.setVisible(true);
        window2.setResizable(false);
        con2 = window2.getContentPane();

        // logo mechanic
        mechslogoPanel = new JPanel();
        mechslogoPanel.setBounds(170, 70, 500, 500);
        mechslogoPanel.setBackground(Color.blue);
        java.net.URL logo3 = getClass().getResource("assets/logo2.png");
        mechslogoLabel = new JLabel(new ImageIcon(logo3));
        mechslogoPanel.add(mechslogoLabel);
        mechslogoPanel.setOpaque(false);
        con2.add(mechslogoPanel);

        // mechanics
        mechanicspanPanel = new JPanel();
        mechanicspanPanel.setBounds(170, 170, 500, 500);
        mechanicspanPanel.setBackground(Color.blue);
        java.net.URL mechanics = getClass().getResource("assets/mechanics12.png");
        mechanicspanLabel = new JLabel(new ImageIcon(mechanics));
        mechanicspanPanel.add(mechanicspanLabel);
        mechanicspanPanel.setOpaque(false);
        con2.add(mechanicspanPanel);

        // backbutton
        backLabel = new JButton("Back");
        backPanel = new JPanel();
        backPanel.setBounds(100, 660, 250, 250);
        backPanel.setBackground(Color.blue);
        backLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        backLabel.setBackground(new Color(98, 116, 172));
        backPanel.setOpaque(false);
        backLabel.setBorderPainted(false);
        backLabel.setFocusPainted(false);
        backPanel.add(backLabel);
        con2.add(backPanel);
        backLabel.addActionListener(new back());
    }

    class Action implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            play();

        }
    }

    class mechanics implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            mechanic();
            mainWindow.dispose();

        }
    }

    static class exit implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            exit(0);

        }
    }

    static class back implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Game game = new Game();
            window2.dispose();
            // window.dispose();
        }
    }

    static class MainMenu implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            mainWindow.setContentPane(tempWindow);
            mainWindow.revalidate();
            ga.stop();
        }
    }

}
// * end */