package htmlprogrammer.labs.messanger.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import htmlprogrammer.labs.messanger.R;

public class InfoDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setIcon(R.drawable.logo)
                .setTitle("Info")
                .setMessage("Messenger app");

        return builder.create();
    }
}
