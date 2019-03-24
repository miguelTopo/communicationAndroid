package co.edu.udistrital.communicationapp.model;

import java.util.Date;

public class Message {

    public String id;
    public User senderUser;
    public User receiverUser;
    public String messageBody;
    public String receiverUserId;
    public String senderUserId;
    public Date creationDate;
    public String parentMessageId;
}
