package htmlprogrammer.labs.messanger.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.fragments.common.CodeInputFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePhoneFragment extends Fragment {
    private EditText oldPhoneEdit, newPhoneEdit;
    private FrameLayout codeInputContainerOld, codeInputContainerNew;
    private TextView back, error, nextButton;

    private AwesomeValidation validation;
    private boolean isCodeStep = false;
    private CodeInputFragment codeInputFragmentOld, codeInputFragmentNew;

    public ChangePhoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nextButton = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
        error = view.findViewById(R.id.error);
        codeInputContainerNew = view.findViewById(R.id.codeInputNew);
        codeInputContainerOld = view.findViewById(R.id.codeInputOld);
        oldPhoneEdit = view.findViewById(R.id.et_oldPhone);
        newPhoneEdit = view.findViewById(R.id.et_newPhone);

        createCodeInput();
        activateLinks();
        addValidation();
    }

    private void cancelCb(){
        isCodeStep = false;
        codeInputContainerNew.setVisibility(View.GONE);
        codeInputContainerOld.setVisibility(View.GONE);

        oldPhoneEdit.setEnabled(true);
        newPhoneEdit.setEnabled(true);
    }

    private void resendCb(boolean isOld){
        Toast.makeText(getContext(), "Resend " + (isOld ? "old" : "new"), Toast.LENGTH_SHORT).show();
    }

    private void createCodeInput(){
        codeInputFragmentNew = new CodeInputFragment();
        codeInputFragmentOld = new CodeInputFragment();

        codeInputFragmentOld.setCancelCb(this::cancelCb);
        codeInputFragmentNew.setCancelCb(this::cancelCb);

        codeInputFragmentNew.setResendCb(() -> resendCb(false));
        codeInputFragmentOld.setResendCb(() -> resendCb(true));

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.codeInputOld, codeInputFragmentOld, null)
                .addToBackStack("codeInputOld")
                .commit();

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.codeInputNew, codeInputFragmentNew, null)
                .addToBackStack("codeInputNew")
                .commit();

        codeInputContainerNew.setVisibility(View.GONE);
        codeInputContainerOld.setVisibility(View.GONE);
    }

    private void activateLinks(){
        back.setOnClickListener(view -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void addValidation(){
        validation = new AwesomeValidation(ValidationStyle.BASIC);

        //add rules
        validation.addValidation(oldPhoneEdit, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));
        validation.addValidation(newPhoneEdit, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));
        validation.addValidation(oldPhoneEdit, Patterns.PHONE, getString(R.string.invalidPhone));
        validation.addValidation(newPhoneEdit, Patterns.PHONE, getString(R.string.invalidPhone));
        validation.addValidation(newPhoneEdit, s -> !s.equals(oldPhoneEdit.getText().toString()), getString(R.string.notSame));

        nextButton.setOnClickListener(view -> {
            if(!isCodeStep)
                nextStepOne();
            else
                nextStepTwo();
        });
    }

    private void nextStepOne(){
        if(validation.validate()){
            isCodeStep = true;

            oldPhoneEdit.setEnabled(false);
            newPhoneEdit.setEnabled(false);

            error.setVisibility(View.GONE);
            codeInputContainerNew.setVisibility(View.VISIBLE);
            codeInputContainerOld.setVisibility(View.VISIBLE);
        }
        else{
            //show error
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }

    private void nextStepTwo(){
        if(codeInputFragmentNew.validate() && codeInputFragmentOld.validate())
            error.setVisibility(View.GONE);
        else{
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }
}
