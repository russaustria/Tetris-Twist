package example;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

import example.Terminator.GameTerminatedException;

/*start*/

public class GameThread extends Thread {

    public GameArea ga;
    private boolean interrupted;

    public GameThread(GameArea ga) {
        this.ga = ga;
    }

    @Override
    public void run() {
        try {
            ga.startGame();
        } catch (GameTerminatedException err) {
            ga.clip.stop();
            ga.stop();
            end();
        }
    }

    public void end() {
        try {
            this.finalize();
        } catch (Throwable err) {

        }
    }
}
/* end */