package crv.manolin.events.entities;

public class JoinEvent extends ChatEvent{
    private String roomId;
    private String username;
    private String password;
    public JoinEvent( String roomId, String username, String password) {
        super(ChatEventType.USER_JOINED);
        this.roomId = roomId;
        this.username = username;
        this.password = password;
    }
}
