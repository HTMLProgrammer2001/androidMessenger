package htmlprogrammer.labs.messanger.fragments.chatFragments.actions;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import htmlprogrammer.labs.messanger.App;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.MessageAPI;
import htmlprogrammer.labs.messanger.dialogs.DeleteMessagesDialog;
import htmlprogrammer.labs.messanger.models.Message;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.store.chat.SelectedMessagesStore;
import htmlprogrammer.labs.messanger.viewmodels.chat.SelectedMessagesViewModel;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageActionFragment extends Fragment {
    private Button cancel;
    private Button resend;
    private Button delete;
    private Button edit;

    private MeStore meStore = MeStore.getInstance();
    private SelectedMessagesStore selectedMessagesStore = SelectedMessagesStore.getInstance();
    private SelectedMessagesViewModel selectedMessagesVM;

    public MessageActionFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedMessagesVM = ViewModelProviders.of(this).get(SelectedMessagesViewModel.class);

        //find elements
        cancel = view.findViewById(R.id.cancel);
        delete = view.findViewById(R.id.delete);
        edit = view.findViewById(R.id.edit);
        resend = view.findViewById(R.id.resend);

        addHandlers();
    }

    private void addHandlers() {
        selectedMessagesVM.getSelectedMessages().observe(this, messages -> {
            //show or hide edit button
            if (messages.size() == 1 && messages.get(0).getAuthor().getId().equals(meStore.getUser().getId())) {
                edit.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.GONE);
            }
        });

        cancel.setOnClickListener(this::onCancel);
        delete.setOnClickListener(this::onDelete);
        edit.setOnClickListener(this::onEdit);
    }

    private void onCancel(View v) {
        selectedMessagesStore.reset();
    }

    private void onEdit(View v){
        selectedMessagesStore.setEditMessage(selectedMessagesStore.getSelectedMessages().get(0));
    }

    private void onDelete(View v) {
        //check if all messages belongs to current user
        boolean isMyMessages = true;

        for (Message msg : selectedMessagesStore.getSelectedMessages()) {
            if (!msg.getAuthor().getId().equals(meStore.getUser().getId())) {
                isMyMessages = false;
                break;
            }
        }

        //show dialog if belongs
        if (isMyMessages) {
            DeleteMessagesDialog deleteMessagesDialog = new DeleteMessagesDialog();
            deleteMessagesDialog.show(requireFragmentManager(), "delete");
            deleteMessagesDialog.setHandler(this::deleteMessages);
        } else {
            deleteMessages(false);
        }
    }

    public void deleteMessages(boolean forOther) {
        String[] ids = new String[selectedMessagesStore.getSelectedMessages().size()];

        for (int i = 0; i < selectedMessagesStore.getSelectedMessages().size(); i++) {
            ids[i] = selectedMessagesStore.getSelectedMessages().get(i).getId();
        }

        MessageAPI.deleteMessages(
                meStore.getToken(),
                forOther,
                ids,
                this::onDeleteResponse
        );

        selectedMessagesStore.reset();
    }

    private void onDeleteResponse(Exception e, Response response) {
        if (e != null || !response.isSuccessful()) {
            new Handler(Looper.getMainLooper()).post(() -> {
                //get error
                String errorText = e != null ? e.getMessage() : "";
                errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

                Toast.makeText(App.getContext(), errorText, Toast.LENGTH_SHORT).show();
            });
        } else {
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(App.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
