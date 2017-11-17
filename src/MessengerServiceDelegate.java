/**
 * A delegate used to pass events from `MessengerService` to any other class, implementing this interface.
 */
public interface MessengerServiceDelegate {
    public void messengerDidGetMessage(MessengerService service, byte[] messageData);
}
