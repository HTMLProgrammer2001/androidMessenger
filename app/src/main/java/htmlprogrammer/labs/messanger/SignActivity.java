package htmlprogrammer.labs.messanger;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import okhttp3.Response;

public class SignActivity extends BaseActivity {
    private EditText nameEdit, nickEdit, phoneEdit;
    private FrameLayout codeInputContainer;
    private TextView back, error, nextButton;

    private AwesomeValidation validation;
    private boolean isCodeStep = false;
    private CodeInputFragment codeInputFragment;

    private MeStore meStore = MeStore.getInstance();
    private CodeReceiver receiver;

    private boolean isLoading = false;
    private boolean receiverRegistered = false;

    private final int PERM_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        receiver = new CodeReceiver(this::handleCode);

        back = findViewById(R.id.back);
        error = findViewById(R.id.error);
        nameEdit = findViewById(R.id.et_name);
        nickEdit = findViewById(R.id.et_nick);
        phoneEdit = findViewById(R.id.et_phone);
        codeInputContainer = findViewById(R.id.codeInputContainerSign);
        nextButton = findViewById(R.id.next);

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

        if(receiverRegistered) {
            unregisterReceiver(receiver);
            receiverRegistered = false;
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
        receiverRegistered = true;
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
            UserActionsAPI.resend(phoneEdit.getText().toString(), CodeTypes.SIGNIN.getValue(), (e, response) -> {
                runOnUiThread(() -> onResend(e, response));
            });
        });

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.codeInputContainerSign, codeInputFragment, null)
                .commit();

        codeInputContainer.setVisibility(View.GONE);
    }

    private void activateLinks(){
        back.setOnClickListener(e -> finish());
    }

    private void addValidation(){
        validation = new AwesomeValidation(ValidationStyle.BASIC);

        //add required validation
        validation.addValidation(nameEdit, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));
        validation.addValidation(phoneEdit, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));
        validation.addValidation(nickEdit, RegexTemplate.NOT_EMPTY, getString(R.string.requiredField));

        validation.addValidation(phoneEdit, Patterns.PHONE, getString(R.string.invalidPhone));
        validation.addValidation(nameEdit, "[a-zA-Z]\\w{3,31}", getString(R.string.invalidName));
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
                    (e, response) -> runOnUiThread(() -> onSign(e, response)));
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
                runOnUiThread(() -> onConfirmSign(e, response));
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
        nextButton.setTextColor(getResources().getColor(R.color.textWhite));
        isLoading = false;
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

    private void onConfirmSign(Exception e, Response response){
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

                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);

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

        nextButton.setTextColor(getResources().getColor(R.color.textWhite));
        isLoading = false;
    }

    private void handleCode(String code){
        if(isCodeStep)
            this.codeInputFragment.setCode(code);
    }
}
