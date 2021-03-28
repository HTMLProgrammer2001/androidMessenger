package htmlprogrammer.labs.messanger.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.constants.CodeTypes;
import htmlprogrammer.labs.messanger.fragments.common.CodeInputFragment;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeState;
import okhttp3.Response;

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

    private MeState meState;

    private FragmentManager manager;
    private boolean isLoading = false;

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
        meState = ViewModelProviders.of(requireActivity()).get(MeState.class);

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
            UserActionsAPI.resend(phoneEdit.getText().toString(), CodeTypes.SIGNIN, (e, response) -> {
                requireActivity().runOnUiThread(() -> onResend(e, response));
            });
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
        validation.addValidation(nameEdit, "[a-zA-Z]\\w{3,31}", getString(R.string.invalidNick));
        validation.addValidation(nickEdit, "[a-zA-Z]\\w{3,31}", getString(R.string.invalidNick));

        nextButton.setOnClickListener(view -> {
            if(isLoading)
                return;

            error.setVisibility(View.GONE);

            if(!isCodeStep)
                nextStepOne();
            else
                nextStepTwo();
        });
    }

    private void nextStepOne(){
        if(validation.validate()) {
            isLoading = true;
            nextButton.setTextColor(getResources().getColor(R.color.textGray));

            UserActionsAPI.signIn(
                    nameEdit.getText().toString(),
                    phoneEdit.getText().toString(),
                    nickEdit.getText().toString(),
                    (e, response) -> requireActivity().runOnUiThread(() -> onSign(e, response)));
        }
        else{
            //show error
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }

    private void nextStepTwo(){
        if(codeInputFragment.validate()){
            UserActionsAPI.confirmSignIn(codeInputFragment.getCode(), (e, response) -> {
                requireActivity().runOnUiThread(() -> onConfirmSign(e, response));
            });

            error.setVisibility(View.GONE);
        }
        else {
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }

    private void showErrors(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject errorObject = array.getJSONObject(i);

            if (errorObject.getString("param").equals("phone"))
                phoneEdit.setError(errorObject.getString("msg"));

            if(errorObject.getString("param").equals("nickname"))
                nickEdit.setError(errorObject.getString("msg"));

            if(errorObject.getString("param").equals("name"))
                nameEdit.setError(errorObject.getString("msg"));

            if(errorObject.getString("param").equals("code"))
                codeInputFragment.showError(errorObject.getString("msg"));
        }
    }

    private void onSign(Exception e, Response response){
        try{
            if (e == null && response.isSuccessful()) {
                //next step
                isCodeStep = true;
                phoneEdit.setEnabled(false);
                nameEdit.setEnabled(false);
                nickEdit.setEnabled(false);

                codeInputContainer.setVisibility(View.VISIBLE);

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

    private void onResend(Exception e, Response response) {
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

    private void onConfirmSign(Exception e, Response response){
        try {
            //parse response
            JSONObject respObj = new JSONObject(response.body().string());

            //no errors
            if (e == null && response.isSuccessful()) {
                //save user
                meState.setUser(User.fromJSON(respObj.getJSONObject("user")));

                //save token to store
                SharedPreferences.Editor editor = requireActivity().getSharedPreferences("store", 0).edit();
                editor.putString("token", respObj.getString("token"));
                editor.apply();
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
}
