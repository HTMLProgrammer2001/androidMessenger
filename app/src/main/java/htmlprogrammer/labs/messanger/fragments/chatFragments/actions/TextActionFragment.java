package htmlprogrammer.labs.messanger.fragments.chatFragments.actions;


import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.MessageAPI;
import htmlprogrammer.labs.messanger.constants.DialogStatus;
import htmlprogrammer.labs.messanger.constants.MessageTypes;
import htmlprogrammer.labs.messanger.helpers.FileHelper;
import htmlprogrammer.labs.messanger.models.Dialog;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.store.chat.ChatMessagesStore;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import htmlprogrammer.labs.messanger.store.chat.SelectedMessagesStore;
import htmlprogrammer.labs.messanger.store.chat.SendMessagesStore;
import htmlprogrammer.labs.messanger.websockets.Websocket;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextActionFragment extends Fragment {
    private ImageView send;
    private ImageView attachment;
    private EditText input;
    private ConstraintLayout fileSelect;
    private ConstraintLayout editMessage;
    private TextView editMessageText;
    private ImageView editMessageCancel;

    private FrameLayout document;
    private FrameLayout image;
    private FrameLayout audio;
    private FrameLayout video;

    private Timer sendFileTimer;
    private boolean fileShown = false;
    private MessageTypes type;
    private ChatStore chatStore = ChatStore.getInstance();
    private SendMessagesStore sendMessagesStore = SendMessagesStore.getInstance();
    private SelectedMessagesStore selectedMessagesStore = SelectedMessagesStore.getInstance();
    private Message editMessageObj;

    private final int FILE_REQUEST_CODE = 9000;
    private final int PERM_CODE = 101;

    public TextActionFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //find elements
        send = view.findViewById(R.id.send);
        input = view.findViewById(R.id.input);
        attachment = view.findViewById(R.id.attachment);
        fileSelect = view.findViewById(R.id.fileSelect);
        editMessage = view.findViewById(R.id.editMessage);
        editMessageText = view.findViewById(R.id.message);
        editMessageCancel = view.findViewById(R.id.cancel);

        audio = view.findViewById(R.id.audio);
        video = view.findViewById(R.id.video);
        document = view.findViewById(R.id.document);
        image = view.findViewById(R.id.image);

        send.setColorFilter(getResources().getColor(R.color.textGray), PorterDuff.Mode.SRC_IN);
        fileSelect.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        fileSelect.setVisibility(View.GONE);

        sendFileTimer = new Timer();

        initEdit();
        addHandlers();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(data == null)
                Toast.makeText(requireContext(), getString(R.string.errorOccured), Toast.LENGTH_SHORT).show();

            try {
                //get file
                Uri uri = data.getData();
                String filePath = FileHelper.getRealPathFromUri(requireActivity(), uri);
                File file = new File(filePath);

                //create message object for show sending state
                String url = "file://" + Uri.fromFile(file).toString();
                Message messageObj = new Message();
                messageObj.setAuthor(MeStore.getInstance().getUser());
                messageObj.setUrl(url);
                messageObj.setDialog(ChatStore.getInstance().getDialog());
                messageObj.setSending(true);
                messageObj.setTime(new Date());
                messageObj.setType(type);
                messageObj.setMessage(file.getName());
                messageObj.setSize(file.length());
                messageObj.setId(String.valueOf(new Random().nextInt()));

                //add it to store
                sendMessagesStore.addMessage(messageObj);

                //make api call
                sendFileMessage(messageObj, file);

                //change ws
                Websocket.sendStatus(ChatStore.getInstance().getDialog().getId(), DialogStatus.fromMessageType(type));

                sendFileTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Websocket.sendStatus(ChatStore.getInstance().getDialog().getId(), DialogStatus.fromMessageType(type));
                    }
                }, 0, 1000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void initEdit(){
        editMessageObj = (Message) getArguments().getSerializable("editMessage");

        //hide if no edit message
        if(editMessageObj == null) {
            editMessage.setVisibility(View.GONE);
            return;
        }

        //show edit message data
        editMessageText.setText(editMessageObj.getMessage());

        if(editMessageObj.getType().equals(MessageTypes.TEXT))
            input.setText(editMessageObj.getMessage());
    }

    public static TextActionFragment getInstance(Message msg){
        TextActionFragment fragment = new TextActionFragment();
        Bundle args = new Bundle();

        args.putSerializable("editMessage", msg);
        fragment.setArguments(args);

        return fragment;
    }

    private void addHandlers(){
        audio.setOnClickListener(v -> openFile("audio/*", MessageTypes.AUDIO));
        video.setOnClickListener(v -> openFile("video/*", MessageTypes.VIDEO));
        image.setOnClickListener(v -> openFile("image/*", MessageTypes.IMAGE));
        document.setOnClickListener(v -> openFile("*/*", MessageTypes.DOCUMENT));

        editMessageCancel.setOnClickListener(v -> selectedMessagesStore.reset());

        attachment.setOnClickListener(v -> {
            //toggle ui
            if(!fileShown)
                fileSelect.setVisibility(View.VISIBLE);

            fileSelect.getLayoutParams().height = fileShown ? 1 : ConstraintLayout.LayoutParams.WRAP_CONTENT;
            fileSelect.requestLayout();

            fileShown = !fileShown;
        });

        send.setOnClickListener(v -> {
            selectedMessagesStore.reset();
            String messageText = input.getText().toString();

            if(messageText.isEmpty())
                return;

            //create message object for show sending state
            Message messageObj = new Message();
            messageObj.setAuthor(MeStore.getInstance().getUser());
            messageObj.setMessage(messageText);
            messageObj.setDialog(ChatStore.getInstance().getDialog());
            messageObj.setSending(true);
            messageObj.setTime(new Date());
            messageObj.setType(MessageTypes.TEXT);
            messageObj.setId(String.valueOf(new Random().nextInt()));

            //add it to store
            sendMessagesStore.addMessage(messageObj);

            //make api call
            sendTextMessage(messageObj);

            input.setText("");
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //change color
                if(input.getText().toString().isEmpty())
                    send.setColorFilter(getResources().getColor(R.color.textGray), PorterDuff.Mode.SRC_IN);
                else
                    send.setColorFilter(getResources().getColor(R.color.textBlue), PorterDuff.Mode.SRC_IN);

                //change size
                int lines = s.toString().split("\n").length;
                input.setLines(Math.min(lines, 3));

                //change dialog status
                Websocket.sendStatus(chatStore.getDialog().getId(), DialogStatus.MESSAGE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void sendTextMessage(Message msg){
        if(editMessageObj == null) {
            //send new text message
            MessageAPI.sendTextMessage(
                    MeStore.getInstance().getToken(),
                    chatStore.getDialog().getId(),
                    msg.getMessage(), msg.getId(),
                    (err, resp) -> {
                        sendMessagesStore.deleteMessage(msg);
                        onSent(err, resp);
                    }
            );
        }
        else{
            //edit text message
            MessageAPI.editTextMessage(
                    MeStore.getInstance().getToken(),
                    editMessageObj.getId(),
                    msg.getMessage(), msg.getId(),
                    (err, resp) -> {
                        sendMessagesStore.deleteMessage(msg);
                        onSent(err, resp);
                    }
            );
        }
    }

    private void sendFileMessage(Message message, File file){
        if(editMessageObj == null){
            MessageAPI.sendFileMessage(
                    MeStore.getInstance().getToken(),
                    chatStore.getDialog().getId(),
                    type, file, message.getId(),
                    (err, resp) -> {
                        sendMessagesStore.deleteMessage(message);
                        onSent(err, resp);
                    }
            );
        }
        else{
            MessageAPI.editFileMessage(
                    MeStore.getInstance().getToken(),
                    editMessageObj.getId(),
                    message.getType(),
                    file,
                    message.getId(),
                    (err, resp) -> {
                        sendMessagesStore.deleteMessage(message);
                        onSent(err, resp);
                    }
            );
        }
    }

    private void openFile(String strType, MessageTypes msgType){
        this.type = msgType;

        //request read storage
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int perm = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

            if (perm == PackageManager.PERMISSION_GRANTED)
                onPermissionGranted(strType);
            else
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_CODE);
        }
        else
            onPermissionGranted(strType);
    }

    private void onPermissionGranted(String strType){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(strType);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    private void onSent(Exception e, Response response){
        if(getActivity() == null)
            return;

        //stop loading
        chatStore.stopLoading();
        sendFileTimer.cancel();

        if(e != null || !response.isSuccessful()){
            requireActivity().runOnUiThread(() -> {
                //get error
                String errorText = e != null ? e.getMessage() : "";
                errorText = e == null && !response.isSuccessful() ? response.message() : errorText;
                Toast.makeText(requireActivity(), errorText, Toast.LENGTH_SHORT).show();
            });
        }
        else{
            try {
                //parse new message
                JSONObject object = new JSONObject(response.body().string());
                Message newMessage = Message.fromJSON(object.getJSONObject("newMessage"));

                //add it to state
                ChatMessagesStore.getInstance().addMessage(newMessage);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
