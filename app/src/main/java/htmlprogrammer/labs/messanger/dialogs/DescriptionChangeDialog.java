package htmlprogrammer.labs.messanger.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.EditMeAPI;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.viewmodels.MeViewModel;
import okhttp3.Response;

public class DescriptionChangeDialog extends DialogFragment {
    private EditText descriptionEdit;
    private MeStore meStore = MeStore.getInstance();
    private AlertDialog dialog;
    private boolean isLoading;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create input
        descriptionEdit = new EditText(getContext());
        descriptionEdit.setHint(getString(R.string.enterDesc));

        //build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.changeDesc));
        builder.setIcon(R.drawable.logo);
        builder.setView(descriptionEdit);

        //set actions
        builder.setNegativeButton(R.string.cancel, (dlg, which) -> dlg.cancel());
        builder.setPositiveButton(R.string.next, (dlg, which) -> {});

        dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v) -> {
            if(!isLoading){
                //change nick
                String token = MeStore.getInstance().getToken();

                isLoading = true;
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.textGray));
                EditMeAPI.editDesc(token, descriptionEdit.getText().toString(), this::onResponse);
            }
        });

        return dialog;
    }

    private void onResponse(Exception e, Response response){
        try{
            if (e == null && response.isSuccessful()) {
                //parse response
                JSONObject respObj = new JSONObject(response.body().string());

                //change me
                meStore.setUser(User.fromJSON(respObj.getJSONObject("newUser")));

                if(dialog != null)
                    dialog.dismiss();
            } else {
                //show error
                requireActivity().runOnUiThread(() -> {
                    //get error
                    String errorText = e != null ? e.getMessage() : "";
                    errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

                    descriptionEdit.setError(errorText);
                });
            }

            requireActivity().runOnUiThread(() -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.textBlue));
            });
        }
        catch (Exception err){
            //show error
            descriptionEdit.setError(err.getMessage());
        }

        isLoading = false;
    }
}
