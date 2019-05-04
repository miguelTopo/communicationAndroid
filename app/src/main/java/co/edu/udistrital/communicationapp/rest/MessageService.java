package co.edu.udistrital.communicationapp.rest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.R;
import co.edu.udistrital.communicationapp.TalkActivity;
import co.edu.udistrital.communicationapp.adapter.MessageListAdapter;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;
import co.edu.udistrital.communicationapp.model.Conversation;
import co.edu.udistrital.communicationapp.model.Message;
import co.edu.udistrital.communicationapp.util.JSONUtil;
import co.edu.udistrital.communicationapp.util.ToastMessage;
import co.edu.udistrital.communicationapp.values.FieldName;

public class MessageService {

    private static final String MESSAGE_SEND_TEXT = "/message/sendText";
    private static final String MESSAGE_SEND_AUDIO = "/message/sendAudio";
    private static final String MESSAGE_SEND_VIDEO = "/message/sendVideo";
    private static final String CONVERSATION_USER_TO_USER = "/conversation/userToUser";

    private RecyclerView recyclerView;

    public MessageService() {
    }

    public MessageService(RecyclerView rv) {
        recyclerView = rv;
    }

    public void sendTextMessage(final Message message) {
        if (message == null || message.senderUser == null || message.receiverUser == null)
            return;
        AsyncTask<Message, Void, Message> task = new AsyncTask<Message, Void, Message>() {
            @Override
            protected Message doInBackground(Message... messages) {
                String json = ApplicationService.executePost(MESSAGE_SEND_TEXT, JSONUtil.parseToJson(message));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean succes = jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                    return succes ? message : null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Message message) {
                super.onPostExecute(message);
                try {
                    Context context = CommunicationApplication.getAppContext();
                    Intent intent = new Intent(context, TalkActivity.class);
                    intent.putExtra(FieldName.CONTACT_SELECTED, message.receiverUser.id);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute(message);
    }

    public void sendAudioMessage(File file, Message message) {
        if (file == null || message == null)
            return;
        AsyncTask<Message, Void, Message> task = new AsyncTask<Message, Void, Message>() {
            @Override
            protected Message doInBackground(Message... messages) {
                String json = ApplicationService.executeMultipartTest(MESSAGE_SEND_AUDIO, file, JSONUtil.parseToJson(message));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean succes = jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                    return succes ? message : null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Message message) {
                super.onPostExecute(message);
                if (message != null) {
                    Context context = CommunicationApplication.getAppContext();
                    Intent intent = new Intent(context, TalkActivity.class);
                    intent.putExtra(FieldName.CONTACT_SELECTED, message.receiverUser.id);
                    context.startActivity(intent);
                    ToastMessage.addSuccess(R.string.message_success_send);
                } else
                    ToastMessage.addError(R.string.core_error_response);
            }
        };
        task.execute(message);

    }

    public void sendVideoMessage(File file, Message message) {
        if (file == null || message == null)
            return;
        AsyncTask<Message, Void, Message> task = new AsyncTask<Message, Void, Message>() {
            @Override
            protected Message doInBackground(Message... messages) {
                String json = ApplicationService.executeMultipartTest(MESSAGE_SEND_VIDEO, file, JSONUtil.parseToJson(message));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean success = jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                    return success ? message : null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Message message) {
                super.onPostExecute(message);
                if (message != null) {
                    Context context = CommunicationApplication.getAppContext();
                    Intent intent = new Intent(context, TalkActivity.class);
                    intent.putExtra(FieldName.CONTACT_SELECTED, message.receiverUser.id);
                    context.startActivity(intent);
                    ToastMessage.addSuccess(R.string.message_success_send);
                } else
                    ToastMessage.addError(R.string.core_error_response);
            }
        };
        task.execute(message);
    }

    public void loadConversation(String userId, String contactUserId) {
        if (userId == null || userId.trim().isEmpty() || contactUserId == null || contactUserId.trim().isEmpty())
            return;
        AsyncTask<Void, Void, Conversation> task = new AsyncTask<Void, Void, Conversation>() {
            @Override
            protected Conversation doInBackground(Void... voids) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userId", userId);
                map.put("contactUserId", contactUserId);
                String json = ApplicationService.executePost(CONVERSATION_USER_TO_USER, map);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean succesResponse = jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                    if (!succesResponse)
                        return null;
                    return (Conversation) JSONUtil.parseToObject(jsonObject.getString("entity"), Conversation.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Conversation conversation) {
                super.onPostExecute(conversation);
                try {
                    Context context = CommunicationApplication.getAppContext();
                    if (conversation != null) {
                        MessageListAdapter adapter = new MessageListAdapter(context, conversation);
                        //Connect the adapter with the RecyclerView
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute();
    }


}
