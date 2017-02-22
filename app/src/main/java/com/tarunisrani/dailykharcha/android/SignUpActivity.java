package com.tarunisrani.dailykharcha.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.listeners.ServerSignupListener;
import com.tarunisrani.dailykharcha.network.LoginSignupNetworkCall;
import com.tarunisrani.dailykharcha.utils.AppUtils;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private EditText name_input;
    private EditText email_input;
    private EditText password_input;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        Button button_submit = (Button) findViewById(R.id.signup_button_submit);
        Button button_login = (Button) findViewById(R.id.login_button);
        name_input = (EditText) findViewById(R.id.signup_username_input);
        email_input = (EditText) findViewById(R.id.signup_email_input);
        password_input = (EditText) findViewById(R.id.signup_password_input);

        button_submit.setOnClickListener(this);
        button_login.setOnClickListener(this);

    }

    private void performSubmitOperation(){
        final String name = name_input.getText().toString();
        final String email = email_input.getText().toString();
        String password = password_input.getText().toString();

        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
//            AppUtils.getService().performSignUp(email, password);
//            AppUtils.getService().performSignIn(email, password);
//            AppUtils.getService().performSignUpByUID();

            new LoginSignupNetworkCall().createNewUserInFirebase(this, name, email, password, new ServerSignupListener(){

                @Override
                public void onSignupCompleted(FirebaseUser user) {

                    AppUtils.getService().performDBCleanOperation();
                    AppUtils.getService().performLoginOperation(user.getUid());
                    AppUtils.getService().startListeners(user.getUid());
                    AppUtils.getService().createDefaultGroup(user.getUid());
                    AppUtils.getService().storeUserDetails(name, email, user.getUid());
                    openDailyExpenseScreen();
                    finish();
                }

                @Override
                public void onSignupFailed() {

                }
            });

        }
    }

    private void openDailyExpenseScreen(){
        startActivity(new Intent(this, ExpenseActivity.class));
        finish();
    }

    private void performScreenChangeOperation(){
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_button_submit:
                performSubmitOperation();
                break;
            case R.id.login_button:
                performScreenChangeOperation();
                break;
        }
    }
}
