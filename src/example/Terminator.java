package example;

public class Terminator {
    static class GameTerminatedException extends RuntimeException {
        public GameTerminatedException(String error) {
        }
    }

    static class GameOverException extends RuntimeException {
        public GameOverException(String error) {
        }
    }

    static class ReturnToMainMenuException extends RuntimeException {
        public ReturnToMainMenuException(String error) {
        }
    }
}
