package htmlprogrammer.labs.messanger.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.helpers.LanguageHelper;

public class LanguageDialog extends DialogFragment {
    private int language_index;
    private List<String> languageCodes;
    private SharedPreferences pref;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        languageCodes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.languageCodes)));
        pref = requireActivity().getSharedPreferences("store", 0);

        //get current language
        String currentLanguage = pref.getString("language", "en");

        //get data
        this.language_index = languageCodes.indexOf(currentLanguage);

        //create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose language");
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.languages), this.language_index, this::onSelect);

        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.next), this::setLanguage);

        return builder.create();
    }

    private void onSelect(DialogInterface dlg, int which){
        this.language_index = which;
    }

    private void setLanguage(DialogInterface dlg, int which){
        //save new language
        String language = this.languageCodes.get(this.language_index);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("language", language);
        editor.apply();

        //change app language
        LanguageHelper.setLocale(requireActivity(), language);

        //show message
        Toast.makeText(requireContext(), getString(R.string.reload), Toast.LENGTH_SHORT).show();
    }
}
