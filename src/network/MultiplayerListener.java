package network;

/**
 * Callback interface used by {@link NetworkClient} to deliver
 * incoming network messages to the game logic.
 *
 * <p>Classes implementing this interface can decide how to interpret
 * and process received data such as player movement, bomb placement,
 * or synchronization packets.</p>
 */
public interface MultiplayerListener {

    /**
     * Called whenever a new line of text is received from the server.
     *
     * @param msg the message received from the network connection
     */
    void onNetworkMessage(String msg);
}
