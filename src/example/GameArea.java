package example;

import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import static java.lang.System.exit;
import javax.swing.*;

import example.Terminator.GameOverException;
import example.Terminator.GameTerminatedException;

import java.util.*;
import java.util.Timer;
import java.lang.Math;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GameArea extends JPanel {

  /* start */
  private String zoneSFX = "ZaWarudo.wav";
  private int gridRows;
  private int gridColumns;
  private int gridCellSize;
  private Color[][] background;
  private int actionCooldown;
  private int dropTimer = 50;
  private int difficultyTimer;
  private int clearedLines;
  private boolean inZone;
  private boolean isStarted = true;

  private Tetris.Shape block;
  private Tetris.Shape nextShape;
  private int xpos;
  private int ypos;
  public Timer timer;
  private TimerTask task;
  boolean isFalling = false;
  private Tetris.Block[] board;
  private float zoneMeter = 0;
  private boolean specialBlockNext = false;
  private int specialBlockCountdown = 0;
  private JLabel nextBlock;
  private JLabel scoreLabel;
  private JLabel timerLabel;

  public int score;

  JFrame window;
  private int clears;
  AudioInputStream zoneStream;
  AudioInputStream bgmStream;
  Clip clip;

  public GameArea(JPanel placeholder, int columns, JLabel nextBlock, JLabel scoreLabel, JLabel timerLabel,
      JFrame parent) {
    this.scoreLabel = scoreLabel;
    this.timerLabel = timerLabel;
    this.nextBlock = nextBlock;
    placeholder.setVisible(false);
    this.setBounds(placeholder.getBounds());
    this.setBackground(placeholder.getBackground());
    this.setBorder(placeholder.getBorder());
    block = new Tetris.Shape();

    gridColumns = columns;
    gridCellSize = this.getBounds().width / gridColumns;
    gridRows = columns * 2;
    board = new Tetris.Block[gridColumns * gridRows];
    background = new Color[gridRows][gridColumns];
    timer = new Timer();
    task = new TimerTask() {
      @Override
      public void run() {
        updateFrame();
      }
    };
    clearBoard();
    timer.scheduleAtFixedRate(task, new Date(), 400);

    InputMap im = new ComponentInputMap(this);
    ActionMap am = new ActionMap();

    im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
    im.put(KeyStroke.getKeyStroke("LEFT"), "left");
    im.put(KeyStroke.getKeyStroke("UP"), "up");
    im.put(KeyStroke.getKeyStroke("DOWN"), "down");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "hard");
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), "zone");

    am.put("zone", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        if (zoneMeter > 0 && !inZone) {
          inZone = true;
        } else {
          inZone = false;
        }
        cueSFX("zone", inZone);
      }
    });

    am.put("right", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        checkPosition(block, xpos + 1, ypos);
      }
    });

    am.put("left", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        checkPosition(block, xpos - 1, ypos);
      }
    });

    am.put("up", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        checkPosition(block.rotateLeft(), xpos, ypos);
      }
    });

    am.put("down", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        checkPosition(block.rotateRight(), xpos, ypos);
      }
    });

    am.put("hard", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        hardDrop();
      }
    });

    addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent me) {
        if (me.isPopupTrigger()) {
          checkPosition(block.rotateRight(), xpos, ypos);
        } else {
          checkPosition(block.rotateLeft(), xpos, ypos);
        }
      }

      @Override
      public void mouseEntered(MouseEvent me) {
      }

      @Override
      public void mouseExited(MouseEvent me) {
      }

      @Override
      public void mousePressed(MouseEvent me) {
      }

      @Override
      public void mouseReleased(MouseEvent me) {
      }
    });
    setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, im);
    setActionMap(am);
    setFocusable(true);
    parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private void cueSFX(String name, boolean value) {
    Clip clip;
    if (value) {
      try {
        AudioInputStream sound = AudioSystem.getAudioInputStream(getClass().getResource("ZaWarudo.wav"));
        clip = AudioSystem.getClip();
        clip.open(sound);
        clip.setFramePosition(0);
        clip.start();
      } catch (Exception err) {
        err.printStackTrace();
      }
    }
  }

  private void cueSFX(String name) {
    Clip clip;
    if (name == "lineBomb") {
      try {
        AudioInputStream sound = AudioSystem.getAudioInputStream(getClass().getResource("laser.wav"));
        clip = AudioSystem.getClip();
        clip.open(sound);
        clip.setFramePosition(0);
        clip.start();
      } catch (Exception err) {
        err.printStackTrace();
      }
    } else if (name == "bomb") {
      try {
        AudioInputStream sound = AudioSystem.getAudioInputStream(getClass().getResource("ooh.wav"));
        clip = AudioSystem.getClip();
        clip.open(sound);
        clip.setFramePosition(0);
        clip.start();
      } catch (Exception err) {
        err.printStackTrace();
      }
    }
  }

  private void updateFrame() {
    System.out.println(zoneMeter);
    timerLabel.setText("Timer: " + dropTimer);

    if (difficultyTimer < 10)
      difficultyTimer = 10;
    else
      difficultyTimer = 50 - (clears / 2);

    if (!inZone) {
      if (zoneMeter >= 0 && zoneMeter <= 30)
        zoneMeter++;
    }

    if (zoneMeter <= 0)
      inZone = false;

    if (dropTimer <= 0 && isFalling && !inZone) {
      hardDrop();
      dropTimer = difficultyTimer;
    }

    else if (inZone && isFalling) {
      zoneMeter--;
      return;
    }

    else if (isFalling) {
      if (!checkPosition(block, xpos, ypos - 1)) {
        pieceDropped();
      } else {
        dropTimer--;
      }
    }

    else {
      spawn();
      dropTimer = difficultyTimer;
    }
  }

  private void spawn() {
    double number = Math.random();
    if (number >= 0.95) {
      specialBlockNext = true;
      specialBlockCountdown = 20;
    }
    if (nextShape != null) {
      block = nextShape;
      nextBlock();
    } else {
      nextShape = new Tetris.Shape();
      nextBlock();
      System.out.println("No shape");
      block.setRandomShape();
    }
    try {
      xpos = gridColumns / 2 + 1;
      ypos = gridRows - 1 + block.minY();
      isFalling = true;
      if (!checkPosition(block, xpos, ypos - 1) && isStarted) {
        block.setShape(Tetris.Block.None);
        JOptionPane.showMessageDialog(null, "Game over. Your final score is " + score);
        stop();
        throw new GameTerminatedException("Line limit exceeded.");
      }
    } catch (Exception err) {
      stop();
      throw new GameTerminatedException("Line limit exceeded.");
    }

    specialBlockNext = false;
  }

  private void nextBlock() {

    Tetris.Shape newShape = new Tetris.Shape();
    ImageIcon icon;

    if (!specialBlockNext) {
      newShape.setRandomShape();

    } else {
      newShape.setRandomShape(true);
    }
    nextShape = newShape;
    if (nextShape.getShape() == Tetris.Block.SShape)
      icon = new ImageIcon(getClass().getResource("s.png"));
    else if (nextShape.getShape() == Tetris.Block.ZShape)
      icon = new ImageIcon(getClass().getResource("z.png"));
    else if (nextShape.getShape() == Tetris.Block.TShape)
      icon = new ImageIcon(getClass().getResource("T.png"));
    else if (nextShape.getShape() == Tetris.Block.SquareShape)
      icon = new ImageIcon(getClass().getResource("square.png"));
    else if (nextShape.getShape() == Tetris.Block.LineShape)
      icon = new ImageIcon(getClass().getResource("i.png"));
    else if (nextShape.getShape() == Tetris.Block.LShape)
      icon = new ImageIcon(getClass().getResource("L.png"));
    else if (nextShape.getShape() == Tetris.Block.MirroredLShape)
      icon = new ImageIcon(getClass().getResource("mirror_L.png"));
    else {
      icon = new ImageIcon(getClass().getResource("special.png"));
    }

    Image orig_icon = icon.getImage();
    Image newimg = orig_icon.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH);
    ImageIcon output = new ImageIcon(newimg);

    nextBlock.setIcon(output);
    nextBlock.setHorizontalAlignment(SwingConstants.CENTER);
  }

  private void bomb(int x, int y) {

    int startX = xpos + block.x(3);
    int startY = ypos - block.y(3);

    // board[9 * 20] maximum value
    if (block.getShape() == Tetris.Block.Bomb) {
      cueSFX("bomb");
      board[(startY) * gridColumns + (startX)] = Tetris.Block.None;
      board[(startY) * gridColumns + (startX - 1)] = Tetris.Block.None;
      board[(startY + 1) * gridColumns + (startX)] = Tetris.Block.None;
      board[(startY + 1) * gridColumns + (startX - 1)] = Tetris.Block.None;
      if (startX > 1) {
        board[(startY) * gridColumns + (startX - 2)] = Tetris.Block.None;
        board[(startY + 1) * gridColumns + (startX - 2)] = Tetris.Block.None;
      }

      if (startX < 9) {
        board[startY * gridColumns + (startX + 1)] = Tetris.Block.None;
        board[(startY + 1) * gridColumns + (startX + 1)] = Tetris.Block.None;
      }

      if (startY > 0) {
        board[(startY - 1) * gridColumns + (startX - 1)] = Tetris.Block.None;
        board[(startY - 1) * gridColumns + (startX)] = Tetris.Block.None;
      }
      board[(startY + 2) * gridColumns + (startX - 1)] = Tetris.Block.None;
      board[(startY + 2) * gridColumns + (startX)] = Tetris.Block.None;

      score += clearedLines * (51 - difficultyTimer * 0.5) * 2;
    } else if (block.getShape() == Tetris.Block.LineBomb) {
      cueSFX("lineBomb");
      for (int i = 0; i < gridColumns; i++) {
        board[(startY + 3) * gridColumns + i] = Tetris.Block.None;
        board[(startY + 2) * gridColumns + i] = Tetris.Block.None;
        board[(startY + 1) * gridColumns + i] = Tetris.Block.None;
        board[(startY) * gridColumns + i] = Tetris.Block.None;
      }
      score += clearedLines * (51 - difficultyTimer * 0.5) * 4;
    }
  }

  private void pieceDropped() {
    for (int i = 0; i < 4; i++) {
      int x = xpos + block.x(i);
      int y = ypos - block.y(i);
      board[y * gridColumns + x] = block.getShape();
    }
    if (block.getShape() == Tetris.Block.Bomb || block.getShape() == Tetris.Block.LineBomb)
      bomb(0, 0);
    checkForClears();
    isFalling = false;
  }

  private void hardDrop() {
    int newY = ypos;

    while (newY > 0) {
      if (!checkPosition(block, xpos, ypos - 1))
        break;

      --newY;
    }

    pieceDropped();
  }

  private void checkForClears() {
    int numFullLines = 0;

    for (int i = gridRows - 1; i >= 0; --i) {
      boolean lineIsFull = true;

      for (int j = 0; j < gridColumns; ++j) {
        if (shapeAt(j, i) == Tetris.Block.None) {
          lineIsFull = false;
          break;
        }
      }

      if (lineIsFull) {
        ++numFullLines;

        for (int k = i; k < gridRows - 1; ++k) {
          for (int j = 0; j < gridColumns; ++j) {
            board[k * gridColumns + j] = shapeAt(j, k + 1);
          }
        }
      }

      if (numFullLines > 0) {
        isFalling = false;
        block.setShape(Tetris.Block.None);
        repaint();
        clearedLines += numFullLines;
        clears++;
        score += clearedLines * (51 - difficultyTimer * 0.5);
        scoreLabel.setText("Score: " + score);
      }
    }
    dropTimer = difficultyTimer;
    specialBlockCountdown = clears;
  }

  public void startGame() {
    block.setRandomShape();
    difficultyTimer = 50;
    dropTimer = 50;
    zoneMeter = 30;
    isStarted = true;
  }

  public void stop() {
    timer.cancel();
    throw new Terminator.GameTerminatedException("Game Cancelled.");
  }

  /* end */

  private boolean checkPosition(Tetris.Shape newPiece, int newX, int newY) {
    for (int i = 0; i < 4; ++i) {
      int x = newX + newPiece.x(i);
      int y = newY - newPiece.y(i);

      if (x < 0 || x >= gridColumns || y < 0 || y >= gridRows)
        return false;

      if (shapeAt(x, y) != Tetris.Block.None)
        return false;
    }

    block = newPiece;
    xpos = newX;
    ypos = newY;
    repaint();

    return true;
  }

  public Tetris.Block shapeAt(int x, int y) {
    return board[y * gridColumns + x];
  }

  public int squareWidth() {
    return (int) getSize().getWidth() / gridColumns;
  }

  public int squareHeight() {
    return (int) getSize().getHeight() / gridRows;
  }

  private void drawSquare(Graphics g, int x, int y, Tetris.Block shape) {
    Color color = shape.color;
    g.setColor(color);
    g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
    g.setColor(color.brighter());
    g.drawLine(x, y + squareHeight() - 1, x, y);
    g.drawLine(x, y, x + squareWidth() - 1, y);
    g.setColor(color.darker());
    g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
    g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
  }

  private void clearBoard() {
    for (int i = 0; i < gridRows * gridColumns; i++) {
      board[i] = Tetris.Block.None;
    }
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    Dimension size = getSize();
    int boardTop = (int) size.getHeight() - gridRows * squareHeight();

    for (int i = 0; i < gridRows; i++) {
      for (int j = 0; j < gridColumns; ++j) {
        Tetris.Block shape = shapeAt(j, gridRows - i - 1);

        if (shape != Tetris.Block.None) {
          drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
        }
      }
    }

    if (block.getShape() != Tetris.Block.None) {
      for (int i = 0; i < 4; ++i) {
        int x = xpos + block.x(i);
        int y = ypos - block.y(i);
        drawSquare(g, x * squareWidth(), boardTop + (gridRows - y - 1) * squareHeight(), block.getShape());
      }
    }
  }
}
