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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.constants.CodeTypes;
import htmlprogrammer.labs.messanger.fragments.common.CodeInputFragment;
import htmlprogrammer.labs.messanger.models.User;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePhoneFragment extends Fragment {
    private EditText oldPhoneEdit, newPhoneEdit;
    private FrameLayout codeInputContainerOld, codeInputContainerNew;
    private TextView back, error, nextButton;

    private AwesomeValidation validation;
    private CodeInputFragment codeInputFragmentOld, codeInputFragmentNew;

    private boolean isCodeStep = false;
    private boolean isLoading = false;

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

    private void cancelCb() {
        isCodeStep = false;
        codeInputContainerNew.setVisibility(View.GONE);
        codeInputContainerOld.setVisibility(View.GONE);

        oldPhoneEdit.setEnabled(true);
        newPhoneEdit.setEnabled(true);
    }

    private void resendCb(boolean isOld) {
        String phone = isOld ? oldPhoneEdit.getText().toString() : newPhoneEdit.getText().toString();
        UserActionsAPI.resend(phone, CodeTypes.CHANGE_PHONE.getValue(), (e, response) -> {
            requireActivity().runOnUiThread(() -> onResend(e, response));
        });
    }

    private void createCodeInput() {
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
                .commit();

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.codeInputNew, codeInputFragmentNew, null)
                .commit();

        codeInputContainerNew.setVisibility(View.GONE);
        codeInputContainerOld.setVisibility(View.GONE);
    }

    private void activateLinks() {
        back.setOnClickListener(view -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void addValidation() {
        validation = new AwesomeValidation(ValidationStyle.BASIC);

        //add rules
        validation.addValidation(oldPhoneEdit, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));
        validation.addValidation(newPhoneEdit, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));
        validation.addValidation(oldPhoneEdit, Patterns.PHONE, getString(R.string.invalidPhone));
        validation.addValidation(newPhoneEdit, Patterns.PHONE, getString(R.string.invalidPhone));
        validation.addValidation(newPhoneEdit, s -> !s.equals(oldPhoneEdit.getText().toString()), getString(R.string.notSame));

        nextButton.setOnClickListener(view -> {
            if (isLoading)
                return;

            error.setVisibility(View.GONE);

            if (!isCodeStep)
                nextStepOne();
            else
                nextStepTwo();
        });
    }

    private void nextStepOne() {
        if (validation.validate()) {
            isLoading = true;
            nextButton.setTextColor(getResources().getColor(R.color.textGray));

            UserActionsAPI.changePhone(
                    oldPhoneEdit.getText().toString(),
                    newPhoneEdit.getText().toString(),
                    (e, response) -> requireActivity().runOnUiThread(() -> onChange(e, response))
            );
        } else {
            //show error
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }

    private void nextStepTwo() {
        if (codeInputFragmentNew.validate() && codeInputFragmentOld.validate()) {
            isLoading = true;
            nextButton.setTextColor(getResources().getColor(R.color.textGray));

            UserActionsAPI.confirmChangePhone(
                    codeInputFragmentOld.getCode(),
                    codeInputFragmentNew.getCode(),
                    (e, response) -> requireActivity().runOnUiThread(() -> onConfirmChange(e, response))
            );
        } else {
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }

    private void showErrors(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject errorObject = array.getJSONObject(i);

            if (errorObject.getString("param").equals("oldPhone"))
                oldPhoneEdit.setError(errorObject.getString("msg"));

            if(errorObject.getString("param").equals("newPhone"))
                newPhoneEdit.setError(errorObject.getString("msg"));

            if(errorObject.getString("param").equals("oldCode"))
                codeInputFragmentOld.showError(errorObject.getString("msg"));

            if(errorObject.getString("param").equals("newCode"))
                codeInputFragmentNew.showError(errorObject.getString("msg"));
        }
    }

    private void onChange(Exception e, Response response) {
        try{
            if (e == null && response.isSuccessful()) {
                //next step
                isCodeStep = true;

                oldPhoneEdit.setEnabled(false);
                newPhoneEdit.setEnabled(false);

                codeInputContainerOld.setVisibility(View.VISIBLE);
                codeInputContainerNew.setVisibility(View.VISIBLE);

                error.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), getString(R.string.newCodeSent), Toast.LENGTH_SHORT).show();
            } else {
                //get error
                String errorText = e != null ? e.getMessage() : "";
                errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

                //show error
                error.setText(errorText);
                error.setVisibility(View.VISIBLE);

                if(response != null)
                    showErrors(new JSONObject(response.body().string()).getJSONArray("errors"));
            }
        }
        catch (Exception err){
            //show error
            error.setText(err.getMessage());
            error.setVisibility(View.VISIBLE);
        }

        //stop loading
        nextButton.setTextColor(getResources().getColor(R.color.textWhite));
        isLoading = false;
    }

    private void onConfirmChange(Exception e, Response response){
        try {
            //parse response
            JSONObject respObj = new JSONObject(response.body().string());

            //no errors
            if (e == null && response.isSuccessful()) {
                Toast.makeText(requireActivity(), getString(R.string.phoneChanged), Toast.LENGTH_SHORT).show();

                //clear form
                codeInputContainerOld.setVisibility(View.GONE);
                codeInputContainerNew.setVisibility(View.GONE);

                newPhoneEdit.setEnabled(true);
                oldPhoneEdit.setEnabled(true);
                newPhoneEdit.setText("");
                oldPhoneEdit.setText("");
            } else {
                //get error
                String errorText = e != null ? e.getMessage() : "";
                errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

                //show error
                error.setText(errorText);
                error.setVisibility(View.VISIBLE);

                showErrors(respObj.getJSONArray("errors"));
            }
        } catch (Exception err) {
            error.setText(err.getMessage());
            error.setVisibility(View.VISIBLE);
        }

        nextButton.setTextColor(getResources().getColor(R.color.textWhite));
        isLoading = false;
    }

    private void onResend(Exception e, Response response){
        if (e == null && response.isSuccessful()) {
            Toast.makeText(requireActivity(), getString(R.string.newCodeSent), Toast.LENGTH_LONG).show();
        } else {
            //get error
            String errorText = e != null ? e.getMessage() : "";
            errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

            //show error
            error.setText(errorText);
            error.setVisibility(View.VISIBLE);
        }
    }
}
