package htmlprogrammer.labs.messanger.fragments.chatFragments.actions;


import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.MessageAPI;
import htmlprogrammer.labs.messanger.constants.MessageTypes;
import htmlprogrammer.labs.messanger.helpers.FileHelper;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.store.chat.ChatStore;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextActionFragment extends Fragment {
    private ImageView send;
    private ImageView attachment;
    private EditText input;
    private ConstraintLayout fileSelect;

    private FrameLayout document;
    private FrameLayout image;
    private FrameLayout audio;
    private FrameLayout video;

    private boolean fileShown = false;
    private MessageTypes type;
    private ChatStore chatStore = ChatStore.getInstance();

    private final int FILE_REQUEST_CODE = 9000;

    public TextActionFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_action, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //find elements
        send = view.findViewById(R.id.send);
        input = view.findViewById(R.id.input);
        attachment = view.findViewById(R.id.attachment);
        fileSelect = view.findViewById(R.id.fileSelect);

        audio = view.findViewById(R.id.audio);
        video = view.findViewById(R.id.video);
        document = view.findViewById(R.id.document);
        image = view.findViewById(R.id.image);

        send.setColorFilter(getResources().getColor(R.color.textGray), PorterDuff.Mode.SRC_IN);
        fileSelect.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        fileSelect.setVisibility(View.GONE);

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

                //make api call
                MessageAPI.sendFileMessage(
                        MeStore.getInstance().getToken(),
                        chatStore.getDialog().getId(),
                        type,
                        file,
                        this::onSent
                );
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void addHandlers(){
        audio.setOnClickListener(v -> openFile("audio/*", MessageTypes.AUDIO));
        video.setOnClickListener(v -> openFile("video/*", MessageTypes.VIDEO));
        image.setOnClickListener(v -> openFile("image/*", MessageTypes.IMAGE));
        document.setOnClickListener(v -> openFile("*/*", MessageTypes.DOCUMENT));

        attachment.setOnClickListener(v -> {
            //toggle ui
            if(!fileShown)
                fileSelect.setVisibility(View.VISIBLE);

            fileSelect.getLayoutParams().height = fileShown ? 1 : ConstraintLayout.LayoutParams.WRAP_CONTENT;
            fileSelect.requestLayout();

            fileShown = !fileShown;
        });

        send.setOnClickListener(v -> {
            String message = input.getText().toString();

            if(message.isEmpty())
                return;

            //make api call
            MessageAPI.sendTextMessage(
                    MeStore.getInstance().getToken(),
                    chatStore.getDialog().getId(),
                    message,
                    this::onSent
            );

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
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void openFile(String type, MessageTypes msgType){
        this.type = msgType;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    private void onSent(Exception err, Response response){
        if(getActivity() == null)
            return;

        requireActivity().runOnUiThread(() -> {
            try {
                Toast.makeText(requireContext(), response.body().string(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
