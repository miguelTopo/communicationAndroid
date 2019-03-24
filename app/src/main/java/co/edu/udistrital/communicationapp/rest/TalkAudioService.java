package co.edu.udistrital.communicationapp.rest;

import java.io.File;

import co.edu.udistrital.communicationapp.model.Message;
import co.edu.udistrital.communicationapp.model.User;

public class TalkAudioService {

    private MessageService messageService;

    public void messageAudioSend(String senderId, String receiverId, String filePath) {
        try {
            if (senderId == null || receiverId == null || filePath == null)
                return;
            Message message = new Message();
            message.receiverUser = new User(receiverId);
            message.senderUser = new User(senderId);
            getMessageService().sendAudioMessage(new File(filePath), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MessageService getMessageService() {
        if (this.messageService == null)
            this.messageService = new MessageService();
        return this.messageService;
    }
}
