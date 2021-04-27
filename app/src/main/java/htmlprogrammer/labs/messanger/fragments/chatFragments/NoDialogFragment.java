package htmlprogrammer.labs.messanger.fragments.chatFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.ChatActivity;
import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.ChatAPI;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoDialogFragment extends Fragment {
    private TextView start;

    private boolean isLoading = false;
    private String userNick;
    private String userID;

    public NoDialogFragment() {}

    public static NoDialogFragment getInstance(String userID, String userNick){
        Bundle arguments = new Bundle();
        arguments.putString("userID", userID);
        arguments.putString("userNick", userNick);

        NoDialogFragment fragment = new NoDialogFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        start = view.findViewById(R.id.start);
        userID = getArguments().getString("userID");
        userNick = getArguments().getString("userNick");

        addHandlers();
    }

    private void addHandlers(){
        start.setOnClickListener(v -> {
            if(isLoading)
                return;

            start.setTextColor(getResources().getColor(R.color.textGray));
            isLoading = true;
            ChatAPI.createPersonal(MeStore.getInstance().getToken(), userID, this::onDialogCreated);
        });
    }

    private void onDialogCreated(Exception err, Response response){
        requireActivity().runOnUiThread(() -> start.setTextColor(getResources().getColor(R.color.textBlue)));
        isLoading = false;

        if(err != null || !response.isSuccessful()){
            requireActivity().runOnUiThread(() -> {
                //get error
                String errorText = err != null ? err.getMessage() : "";
                errorText = err == null && !response.isSuccessful() ? response.message() : errorText;
                Toast.makeText(requireContext(), errorText, Toast.LENGTH_SHORT).show();
            });
        }
        else{
            //show new activity
            Intent intent = new Intent(requireActivity(), ChatActivity.class);
            intent.putExtra("nick", userNick);
            startActivity(intent);

            requireActivity().finish();
        }
    }
}
