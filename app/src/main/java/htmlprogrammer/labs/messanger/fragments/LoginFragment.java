package htmlprogrammer.labs.messanger.fragments;


import android.arch.lifecycle.ViewModelProviders;
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

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.fragments.common.CodeInputFragment;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.store.MeState;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private EditText phone;
    private TextView nextBtn, signBtn, changePhoneBtn, error;
    private AwesomeValidation validation;
    private FrameLayout codeInputContainer;

    private CodeInputFragment codeInputFragment;
    private MeState meState;

    private boolean isCodeStep = false;
    private boolean isLoading = false;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        meState = ViewModelProviders.of(requireActivity()).get(MeState.class);

        phone = view.findViewById(R.id.et_phone);
        signBtn = view.findViewById(R.id.sign);
        changePhoneBtn = view.findViewById(R.id.changePhone);
        nextBtn = view.findViewById(R.id.next);
        error = view.findViewById(R.id.error);
        codeInputContainer = view.findViewById(R.id.codeInputContainer);

        createCodeInput();
        activateLinks();
        addValidation();
    }

    private void createCodeInput() {
        //create code input
        codeInputFragment = new CodeInputFragment();

        codeInputFragment.setCancelCb(() -> {
            isCodeStep = false;
            codeInputContainer.setVisibility(View.GONE);
            phone.setEnabled(true);
        });

        codeInputFragment.setResendCb(() -> {
            Toast.makeText(getContext(), "Resend", Toast.LENGTH_SHORT).show();
        });

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.codeInputContainer, codeInputFragment, null)
                .commit();

        codeInputContainer.setVisibility(View.GONE);
    }

    private void activateLinks() {
        signBtn.setOnClickListener((v) -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.container, new SignFragment(), null)
                    .addToBackStack("sign")
                    .commit();
        });

        changePhoneBtn.setOnClickListener((v) -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.container, new ChangePhoneFragment(), null)
                    .addToBackStack("changePhone")
                    .commit();
        });
    }

    private void addValidation() {
        //init validation
        validation = new AwesomeValidation(ValidationStyle.BASIC);

        //create validation rules
        validation.addValidation(phone, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));
        validation.addValidation(phone, Patterns.PHONE, getString(R.string.invalidPhone));

        nextBtn.setOnClickListener(view -> {
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
            nextBtn.setTextColor(getResources().getColor(R.color.textGray));

            UserActionsAPI.login(phone.getText().toString(), (e, response) -> {
                requireActivity().runOnUiThread(() -> {
                    if(e == null && response.isSuccessful()){
                        isCodeStep = true;
                        phone.setEnabled(false);
                        codeInputContainer.setVisibility(View.VISIBLE);

                        error.setVisibility(View.GONE);
                    }
                    else{
                        String errorText = e != null ? e.getMessage() : "";
                        errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

                        error.setText(errorText);
                        error.setVisibility(View.VISIBLE);
                    }

                    nextBtn.setTextColor(getResources().getColor(R.color.textWhite));
                    isLoading = false;
                });
            });
        } else {
            //show error
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }

    private void nextStepTwo() {
        if (codeInputFragment.validate()) {
            isLoading = true;
            nextBtn.setTextColor(getResources().getColor(R.color.textGray));

            UserActionsAPI.confirmLogin(codeInputFragment.getCode(), (e, response) -> {
                requireActivity().runOnUiThread(() -> {
                    if(e == null && response.isSuccessful()){
                        try {
                            JSONObject respObj = new JSONObject(response.body().string());
                            meState.setUser(User.fromJSON(respObj.getJSONObject("user")));
                        }
                        catch (Exception err){}
                    }
                    else{
                        String errorText = e != null ? e.getMessage() : "";
                        errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

                        error.setText(errorText);
                        error.setVisibility(View.VISIBLE);
                    }

                    nextBtn.setTextColor(getResources().getColor(R.color.textWhite));
                    isLoading = false;
                });
            });
        } else {
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }
}
