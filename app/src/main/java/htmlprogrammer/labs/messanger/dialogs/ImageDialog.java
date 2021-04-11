package htmlprogrammer.labs.messanger.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Constraints;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import htmlprogrammer.labs.messanger.R;

public class ImageDialog extends DialogFragment {
    private static String URL_ARG = "IMAGE_URL";

    public static ImageDialog getInstance(String url){
        ImageDialog dlg = new ImageDialog();
        Bundle arguments = new Bundle();

        arguments.putString(URL_ARG, url);
        dlg.setArguments(arguments);

        return dlg;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Image preview");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_image, null);
        ImageView imageView = view.findViewById(R.id.imgView);
        Picasso.get().load(getArguments().getString(URL_ARG)).into(imageView);

        builder.setView(view);
        return builder.create();
    }
}
