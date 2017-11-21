import com.sun.istack.internal.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Creates and manages a messenger session.
 */
public class MessengerService {
    private String sessionID;

    @Nullable
    private MessengerServiceDelegate delegate;

    DataInputStream is;
    DataOutputStream os;


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
    void sendMessage(String inputString) {

    }

    /**
     * Initializes a socket session with given id.
     * @param identifier Id of a session to initialize.
     */
    void openSession(String identifier) {

    }

    void closeSession() {

    }

    void connectToOtherSession(String identifier) {

    }
}
