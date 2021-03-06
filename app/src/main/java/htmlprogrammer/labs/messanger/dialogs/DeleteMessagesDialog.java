package htmlprogrammer.labs.messanger.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.interfaces.IDeleteHandler;


public class DeleteMessagesDialog extends DialogFragment {
    private boolean isChecked = false;
    private IDeleteHandler handler = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.deleteForOther);
        builder.setMultiChoiceItems(new String[]{getString(R.string.deleteForOther)}, new boolean[]{isChecked}, this::onSelect);

        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.next), (dialog, which) -> {
            if(handler != null)
                handler.delete(isChecked);
        });

        return builder.create();
    }

    private void onSelect(DialogInterface dlg, int which, boolean isChecked){
        this.isChecked = isChecked;
    }

    public void setHandler(IDeleteHandler handler){
        this.handler = handler;
    }
}
