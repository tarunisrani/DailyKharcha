package com.tarunisrani.dailykharcha.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.listeners.ServerLoginListener;
import com.tarunisrani.dailykharcha.network.LoginSignupNetworkCall;
import com.tarunisrani.dailykharcha.utils.AppConstant;
import com.tarunisrani.dailykharcha.utils.AppUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private EditText email_input;
    private TextInputLayout email_input_wrapper;
    private EditText password_input;
    private TextInputLayout password_input_wrapper;
    private ProgressBar progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        boolean show_message = false;

        Intent intent = getIntent();
        if(intent!=null){
            show_message = intent.getBooleanExtra(AppConstant.INTENT_KEY_SHOW_MESSAGE, false);
        }


        TextView verification_message_label = (TextView) findViewById(R.id.verification_message_label);
        verification_message_label.setVisibility(show_message?View.VISIBLE:View.GONE);

        findViewById(R.id.forgotpassword_button).setOnClickListener(this);


        Button button_submit = (Button) findViewById(R.id.login_button_submit);
        TextView button_signup = (TextView) findViewById(R.id.signup_button);
        email_input = (EditText) findViewById(R.id.login_email_input);
        email_input_wrapper = (TextInputLayout) findViewById(R.id.login_email_input_wrapper);
        password_input = (EditText) findViewById(R.id.login_password_input);
        password_input_wrapper = (TextInputLayout) findViewById(R.id.login_password_input_wrapper);
        progressbar = (ProgressBar) findViewById(R.id.login_progressbar);


        email_input_wrapper.setHint("Email");
        password_input_wrapper.setHint("Password");

        button_submit.setOnClickListener(this);
        button_signup.setOnClickListener(this);

    }

    private void performSubmitOperation(){
        String email = email_input.getText().toString();
        String password = password_input.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()){
//            AppUtils.getService().performSignUp(email, password);
//            AppUtils.getService().performSignIn(email, password);
//            AppUtils.getService().performSignUpByUID();
            progressbar.setVisibility(View.VISIBLE);
            new LoginSignupNetworkCall().loginUser(email, password, new ServerLoginListener(){

                @Override
                public void onLoginCompleted(FirebaseUser user) {
                    progressbar.setVisibility(View.GONE);
                    if(user.isEmailVerified()) {
                        Toast.makeText(LoginActivity.this, "SignIn Success",
                                Toast.LENGTH_SHORT).show();
                        AppUtils.getService().performLoginOperation(user.getDisplayName(), user.getUid());
                        AppUtils.getService().initializeFirebase(user.getUid());
                        AppUtils.getService().startListeners(user.getUid());
                        openDailyExpenseScreen();
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Account not verified", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onLoginFailed() {
                    progressbar.setVisibility(View.GONE);
                }
            });

        }
    }

    private void openDailyExpenseScreen(){
        AppUtils.openNewScreen(this, ExpenseActivity.class, true, null);
//        startActivity(new Intent(this, ExpenseActivity.class));
//        finish();
    }

    private void performScreenChangeOperation(){
        AppUtils.openNewScreen(this, SignUpActivity.class, true, null);
//        startActivity(new Intent(this, SignUpActivity.class));
//        finish();
    }

    private void performForgotPasswordOperation(){
        String email = email_input.getText().toString();
        if(!email.isEmpty()) {
            AppUtils.getService().performResetPassword(email);
        }else{
            Toast.makeText(this, "Please enter correct email", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button_submit:
                performSubmitOperation();
                break;
            case R.id.signup_button:
                performScreenChangeOperation();
                break;
            case R.id.forgotpassword_button:
                performForgotPasswordOperation();
                break;
        }
    }
}
