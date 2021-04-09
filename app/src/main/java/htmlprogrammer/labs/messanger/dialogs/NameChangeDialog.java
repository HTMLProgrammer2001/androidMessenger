package htmlprogrammer.labs.messanger.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
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
import htmlprogrammer.labs.messanger.store.MeState;
import okhttp3.Response;

public class NameChangeDialog extends DialogFragment {
    private EditText nameEdit;
    private AwesomeValidation validation;
    private MeState meState;
    private AlertDialog dialog;
    private boolean isLoading;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        meState = ViewModelProviders.of(requireActivity()).get(MeState.class);

        //create input
        nameEdit = new EditText(getContext());
        nameEdit.setHint(R.string.enterName);
        createValidation();

        //build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.changeName));
        builder.setIcon(R.drawable.logo);
        builder.setView(nameEdit);

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
                EditMeAPI.editName(token, nameEdit.getText().toString(), this::onResponse);
            }
        });

        return dialog;
    }

    private void createValidation(){
        validation = new AwesomeValidation(ValidationStyle.BASIC);
        validation.addValidation(nameEdit, "[a-zA-Z]\\w{3,31}", getString(R.string.invalidName));
    }

    private void onResponse(Exception e, Response response){
        try{
            if (e == null && response.isSuccessful()) {
                //parse response
                JSONObject respObj = new JSONObject(response.body().string());

                //change me
                meState.setUser(User.fromJSON(respObj.getJSONObject("newUser")));

                if(dialog != null)
                    dialog.dismiss();
            } else {
                //show error
                requireActivity().runOnUiThread(() -> {
                    //get error
                    String errorText = e != null ? e.getMessage() : "";
                    errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

                    nameEdit.setError(errorText);
                });
            }

            requireActivity().runOnUiThread(() -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.textBlue));
            });
        }
        catch (Exception err){
            //show error
            nameEdit.setError(err.getMessage());
        }

        isLoading = false;
    }
}
