package htmlprogrammer.labs.messanger.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.fragments.common.CodeInputFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignFragment extends Fragment {
    private EditText nameEdit, nickEdit, phoneEdit;
    private FrameLayout codeInputContainer;
    private TextView back, error, nextButton;

    private AwesomeValidation validation;
    private boolean isCodeStep = false;
    private CodeInputFragment codeInputFragment;

    private FragmentManager manager;

    public SignFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        manager = requireActivity().getSupportFragmentManager();

        back = view.findViewById(R.id.back);
        error = view.findViewById(R.id.error);
        nameEdit = view.findViewById(R.id.et_name);
        nickEdit = view.findViewById(R.id.et_nick);
        phoneEdit = view.findViewById(R.id.et_phone);
        codeInputContainer = view.findViewById(R.id.codeInputContainerSign);
        nextButton = view.findViewById(R.id.next);

        createCodeInput();
        activateLinks();
        addValidation();
    }

    private void createCodeInput(){
        //create code input
        codeInputFragment = new CodeInputFragment();

        codeInputFragment.setCancelCb(() -> {
            isCodeStep = false;
            codeInputContainer.setVisibility(View.GONE);

            phoneEdit.setEnabled(true);
            nameEdit.setEnabled(true);
            nickEdit.setEnabled(true);
        });

        codeInputFragment.setResendCb(() -> {
            Toast.makeText(getContext(), "Resend", Toast.LENGTH_SHORT).show();
        });

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.codeInputContainerSign, codeInputFragment, null)
                .commit();

        codeInputContainer.setVisibility(View.GONE);
    }

    private void activateLinks(){
        back.setOnClickListener(e -> manager.popBackStack());
    }

    private void addValidation(){
        validation = new AwesomeValidation(ValidationStyle.BASIC);

        //add required validation
        validation.addValidation(nameEdit, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));
        validation.addValidation(phoneEdit, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));
        validation.addValidation(nickEdit, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));

        validation.addValidation(phoneEdit, Patterns.PHONE, getString(R.string.invalidPhone));
        validation.addValidation(nickEdit, "[a-zA-Z]\\w{3,31}", getString(R.string.invalidNick));

        nextButton.setOnClickListener(view -> {
            if(!isCodeStep)
                nextStepOne();
            else
                nextStepTwo();
        });
    }

    private void nextStepOne(){
        if(validation.validate()) {
            //TODO: make api call
            isCodeStep = true;

            phoneEdit.setEnabled(false);
            nameEdit.setEnabled(false);
            nickEdit.setEnabled(false);

            codeInputContainer.setVisibility(View.VISIBLE);
            error.setVisibility(View.GONE);
        }
        else{
            //show error
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }

    private void nextStepTwo(){
        if(codeInputFragment.validate()){
            //TODO: make api call
            error.setVisibility(View.GONE);
        }
        else {
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }
}
