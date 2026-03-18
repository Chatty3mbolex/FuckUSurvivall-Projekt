package client.network;

public class GameClientListener implements Runnable {

    private final GameClientConnection connection;

    public GameClientListener(GameClientConnection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = connection.receive();
                if (msg == null) break;
                System.out.println("SERVER: " + msg);
            }
        } catch (Exception ignored) {}
    }
}
