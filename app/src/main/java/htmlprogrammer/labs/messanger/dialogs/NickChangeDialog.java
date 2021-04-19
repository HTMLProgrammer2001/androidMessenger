package htmlprogrammer.labs.messanger.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.EditMeAPI;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeStore;
import okhttp3.Response;

public class NickChangeDialog extends DialogFragment {
    private EditText nickEdit;
    private AwesomeValidation validation;
    private MeStore meStore = MeStore.getInstance();
    private AlertDialog dialog;
    private boolean isLoading;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create input
        nickEdit = new EditText(getContext());
        nickEdit.setHint(R.string.enterNick);
        createValidation();

        //build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.changeNick));
        builder.setIcon(R.drawable.logo);
        builder.setView(nickEdit);

        //set actions
        builder.setNegativeButton(R.string.cancel, (dlg, which) -> dlg.cancel());
        builder.setPositiveButton(R.string.next, (dlg, which) -> {});

        dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v) -> {
            if(validation.validate() && !isLoading){
                //change nick
                String token = requireActivity().getSharedPreferences("store", 0)
                        .getString("token", "");

                isLoading = true;
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.textGray));
                EditMeAPI.editNick(token, nickEdit.getText().toString(), this::onResponse);
            }
        });

        return dialog;
    }

    private void createValidation(){
        validation = new AwesomeValidation(ValidationStyle.BASIC);
        validation.addValidation(nickEdit, "[a-zA-Z]\\w{3,31}", getString(R.string.invalidNick));
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

                    nickEdit.setError(errorText);
                });
            }

            requireActivity().runOnUiThread(() -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.textBlue));
            });
        }
        catch (Exception err){
            //show error
            nickEdit.setError(err.getMessage());
        }

        isLoading = false;
    }
}
