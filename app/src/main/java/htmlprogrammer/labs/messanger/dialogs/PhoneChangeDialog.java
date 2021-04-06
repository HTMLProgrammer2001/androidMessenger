package htmlprogrammer.labs.messanger.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import htmlprogrammer.labs.messanger.R;

public class PhoneChangeDialog extends DialogFragment {
    private EditText phoneEdit;
    private AwesomeValidation validation;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create input
        phoneEdit = new EditText(getContext());
        createValidation();

        //build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.changePhone));
        builder.setIcon(R.drawable.logo);
        builder.setView(phoneEdit);

        //set actions
        builder.setNegativeButton(R.string.cancel, (dlg, which) -> dlg.cancel());
        builder.setPositiveButton(R.string.next, (dlg, which) -> {
            if(validation.validate())
                Toast.makeText(getActivity(), phoneEdit.getText().toString(), Toast.LENGTH_SHORT).show();
        });

        return builder.create();
    }

    private void createValidation(){
        validation = new AwesomeValidation(ValidationStyle.BASIC);
        validation.addValidation(phoneEdit, Patterns.PHONE, getString(R.string.invalidPhone));
    }
}
