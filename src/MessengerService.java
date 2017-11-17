import com.sun.istack.internal.Nullable;

/**
 * Creates and manages a messenger session.
 */
public abstract class MessengerService {
    private String sessionID;

    @Nullable
    private MessengerServiceDelegate delegate;

    public MessengerService(String sessionID) {
        this.sessionID = sessionID;
        openSession(sessionID);
    }

    public String getSessionID() {
        return sessionID;
    }

    public MessengerServiceDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(MessengerServiceDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Converts given raw input string to binary data, encodes it and sends to a connected session.
     * @param inputString Raw message input provided by the user.
     */
    abstract void sendMessage(String inputString);

    /**
     * Initializes a socket session with given id.
     * @param identifier Id of a session to initialize.
     */
    abstract void openSession(String identifier);

    abstract void closeSession();

    abstract void connectToOtherSession(String identifier);
}
