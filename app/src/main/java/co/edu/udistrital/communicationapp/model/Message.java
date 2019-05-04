package co.edu.udistrital.communicationapp.model;

import java.util.Date;

import co.edu.udistrital.communicationapp.enums.MessageType;

public class Message {

    public String id;
    public User senderUser;
    public User receiverUser;
    public String messageBody;
    public String receiverUserId;
    public String senderUserId;
    public String file;
    public Date creationDate;
    public String parentMessageId;
    public MessageType messageType;
}
