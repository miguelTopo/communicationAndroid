package co.edu.udistrital.communicationapp.rest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import co.edu.udistrital.communicationapp.TalkActivity;
import co.edu.udistrital.communicationapp.adapter.MessageListAdapter;
import co.edu.udistrital.communicationapp.application.CommunicationApplication;
import co.edu.udistrital.communicationapp.model.Conversation;
import co.edu.udistrital.communicationapp.model.Message;
import co.edu.udistrital.communicationapp.util.JSONUtil;
import co.edu.udistrital.communicationapp.values.FieldName;

public class MessageService {

    private static final String MESSAGE_SEND_TEXT = "/message/sendText";
    private static final String MESSAGE_SEND_AUDIO = "/message/sendAudio";
    private static final String MESSAGE_SEND_VIDEO = "/message/sendVideo";
    private static final String CONVERSATION_USER_TO_USER = "/conversation/userToUser";

    private RecyclerView recyclerView;

    public MessageService() {
    }

    public void sendTextMessage(final Message message) {
        if (message == null || message.senderUser == null || message.receiverUser == null)
            return;
        AsyncTask<Message, Void, String> task = new AsyncTask<Message, Void, String>() {
            @Override
            protected String doInBackground(Message... messages) {
                String json = ApplicationService.executePost(MESSAGE_SEND_TEXT, JSONUtil.parseToJson(message));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    boolean succesResponse = jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                    if (!succesResponse)
                        return null;
                    return jsonObject.getString("entity");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String message) {
                super.onPostExecute(message);
                try {
                    Context context = CommunicationApplication.getAppContext();
                    Intent intent = new Intent(context, TalkActivity.class);
                    intent.putExtra(FieldName.JSON_RESPONSE, message);
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
        AsyncTask<Message, Void, Boolean> task = new AsyncTask<Message, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Message... messages) {
                String json = ApplicationService.executeMultipartTest(MESSAGE_SEND_AUDIO, file, JSONUtil.parseToJson(message));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    return jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                System.out.println(success);
            }


        };
        task.execute(message);

    }

    public void sendVideoMessage(File file, Message message) {
        if (file == null || message == null)
            return;
        AsyncTask<Message, Void, Boolean> task = new AsyncTask<Message, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Message... messages) {
                String json = ApplicationService.executeMultipartTest(MESSAGE_SEND_VIDEO, file, JSONUtil.parseToJson(message));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    return jsonObject.getBoolean(FieldName.BOOLEAN_RESPONSE);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                System.out.println(success);
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
                        /*if (conversation.messageList != null)
                            conversation.messageLinkedList.addAll(conversation.messageList);*/
                        MessageListAdapter adapter = new MessageListAdapter(context, conversation);
                        //Connect the adapter with the RecyclerView
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute();
    }


}
