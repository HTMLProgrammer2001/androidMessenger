package htmlprogrammer.labs.messanger.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.Timer;
import java.util.TimerTask;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.interfaces.ICallable;

/**
 * A simple {@link Fragment} subclass.
 */

public class CodeInputFragment extends Fragment {
    private TextView cancelBtn, resendBtn;
    private EditText codeInput;

    private AwesomeValidation validation;
    private ICallable resendCb, cancelCb;

    private Timer resendTimer;
    private boolean isResendActive = true;

    public CodeInputFragment(){ }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_code_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resendTimer = new Timer();

        //find
        codeInput = view.findViewById(R.id.codeInput);
        cancelBtn = view.findViewById(R.id.cancel);
        resendBtn = view.findViewById(R.id.resend);

        addValidation();
        addHandlers();
    }

    public boolean validate(){
        return validation.validate();
    }

    private void addValidation(){
        //init validation
        validation = new AwesomeValidation(ValidationStyle.BASIC);

        //add rules
        validation.addValidation(codeInput, "\\d{8}", getString(R.string.invalidCode));
    }

    private void addHandlers(){
        resendBtn.setOnClickListener(v -> {
            if(!isResendActive)
                return;

            resendCb.call();
            isResendActive = false;
            resendTimer.schedule(new ResendTask(), 5000);
            resendBtn.setTextColor(getResources().getColor(R.color.textGray));
        });

        cancelBtn.setOnClickListener(v -> cancelCb.call());
    }

    public void setCancelCb(ICallable cb){
        cancelCb = cb;
    }

    public void setResendCb(ICallable cb){
        resendCb = cb;
    }

    public String getCode(){
        return codeInput.getText().toString();
    }

    public void setCode(String code){
        codeInput.setText(code);
    }

    public void showError(String error){
        codeInput.setError(error);
    }

    class ResendTask extends TimerTask{
        @Override
        public void run() {
            isResendActive = true;

            getActivity().runOnUiThread(() -> {
                resendBtn.setTextColor(getResources().getColor(R.color.textBlue));
            });
        }
    }
}
