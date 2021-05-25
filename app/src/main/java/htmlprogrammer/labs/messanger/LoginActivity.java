package htmlprogrammer.labs.messanger;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
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

import htmlprogrammer.labs.messanger.api.UserActionsAPI;
import htmlprogrammer.labs.messanger.constants.CodeTypes;
import htmlprogrammer.labs.messanger.fragments.CodeInputFragment;
import htmlprogrammer.labs.messanger.interfaces.BaseActivity;
import htmlprogrammer.labs.messanger.models.User;
import htmlprogrammer.labs.messanger.receivers.CodeReceiver;
import htmlprogrammer.labs.messanger.store.MeStore;
import htmlprogrammer.labs.messanger.viewmodels.MeViewModel;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {
    private EditText phone;
    private TextView nextBtn, signBtn, changePhoneBtn, error;
    private AwesomeValidation validation;
    private FrameLayout codeInputContainer;

    private CodeInputFragment codeInputFragment;
    private MeStore meStore = MeStore.getInstance();
    private CodeReceiver receiver;

    private boolean isCodeStep = false;
    private boolean isLoading = false;
    private boolean registeredReceiver = false;

    private static final int PERM_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new CodeReceiver(this::handleCode);

        setContentView(R.layout.activity_login);

        //find elements
        phone = findViewById(R.id.et_phone);
        signBtn = findViewById(R.id.sign);
        changePhoneBtn = findViewById(R.id.changePhone);
        nextBtn = findViewById(R.id.next);
        error = findViewById(R.id.error);
        codeInputContainer = findViewById(R.id.codeInputContainer);

        createCodeInput();
        activateLinks();
        addValidation();
    }

    @Override
    public void onResume() {
        super.onResume();

        //request sms read and receive permission
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int perm1 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
            int perm2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

            if (perm1 == PackageManager.PERMISSION_GRANTED && perm2 == PackageManager.PERMISSION_GRANTED)
                onPermissionGranted();
            else
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, PERM_CODE);
        }
        else
            onPermissionGranted();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(registeredReceiver) {
            unregisterReceiver(this.receiver);
            registeredReceiver = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode != PERM_CODE)
            return;

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            onPermissionGranted();
        }
    }

    private void onPermissionGranted(){
        registerReceiver(this.receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        registeredReceiver = true;
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
            UserActionsAPI.resend(phone.getText().toString(), CodeTypes.LOGIN.getValue(), (e, response) -> {
                runOnUiThread(() -> onResend(e, response));
            });
        });

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.codeInputContainer, codeInputFragment, null)
                .commit();

        codeInputContainer.setVisibility(View.GONE);
    }

    private void activateLinks() {
        signBtn.setOnClickListener((v) -> {
            Intent signIntent = new Intent(this, SignActivity.class);
            startActivity(signIntent);
        });

        changePhoneBtn.setOnClickListener((v) -> {
            Intent changePhoneIntent = new Intent(this, ChangePhoneActivity.class);
            startActivity(changePhoneIntent);
        });
    }

    private void showErrors(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject errorObject = array.getJSONObject(i);

            if (errorObject.getString("param").equals("phone"))
                phone.setError(errorObject.getString("msg"));

            if(errorObject.getString("param").equals("code"))
                codeInputFragment.showError(errorObject.getString("msg"));
        }
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
                runOnUiThread(() -> onLogin(e, response));
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
                runOnUiThread(() -> onConfirmLogin(e, response));
            });
        } else {
            //show error
            error.setText(getString(R.string.errorOccured));
            error.setVisibility(View.VISIBLE);
        }
    }

    private void onResend(Exception e, Response response) {
        if (e == null && response.isSuccessful()) {
            Toast.makeText(this, getString(R.string.newCodeSent), Toast.LENGTH_LONG).show();
        } else {
            //get error
            String errorText = e != null ? e.getMessage() : "";
            errorText = e == null && !response.isSuccessful() ? response.message() : errorText;

            //show error
            error.setText(errorText);
            error.setVisibility(View.VISIBLE);
        }
    }

    private void onLogin(Exception e, Response response) {
        try{
            if (e == null && response.isSuccessful()) {
                //next step
                isCodeStep = true;
                phone.setEnabled(false);
                codeInputContainer.setVisibility(View.VISIBLE);

                error.setVisibility(View.GONE);
                Toast.makeText(this, getString(R.string.newCodeSent), Toast.LENGTH_SHORT).show();
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
        nextBtn.setTextColor(getResources().getColor(R.color.textWhite));
        isLoading = false;
    }

    private void onConfirmLogin(Exception e, Response response) {
        try {
            //parse response
            JSONObject respObj = new JSONObject(response.body().string());

            //no errors
            if (e == null && response.isSuccessful()) {
                //save user
                meStore.setUser(User.fromJSON(respObj.getJSONObject("user")));
                meStore.setToken(respObj.getString("token"));

                //save token to store
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", respObj.getString("token"));
                editor.apply();

                finish();
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

        nextBtn.setTextColor(getResources().getColor(R.color.textWhite));
        isLoading = false;
    }

    private void handleCode(String code){
        if(this.isCodeStep)
            this.codeInputFragment.setCode(code);
    }
}
