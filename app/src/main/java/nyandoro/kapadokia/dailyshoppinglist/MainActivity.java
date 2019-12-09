package nyandoro.kapadokia.dailyshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //declarations
    private EditText email, password;
    private TextView signup;
    private Button loginbtn;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inits
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        signup = findViewById(R.id.signup_txt);
        loginbtn = findViewById(R.id.login_btn);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        //check if the user exists
        if (mAuth.getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(), Home.class));
        }
        //making the login button clickable
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //first add some string variables
                String mEmail = email.getText().toString().trim();
                String mPass  = password.getText().toString().trim();

                //checking if fields are empty
                if (TextUtils.isEmpty(mEmail)){
                    email.setError("Required field...");
                    return;
                }
                if (TextUtils.isEmpty(mPass)){
                    password.setError("Required field...");
                    return;
                }

                //process sign in information
                progressDialog.setMessage("signing in...");
                mAuth.signInWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if the task was successful
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "authentication error", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        //making sign up btn clickable
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent that redirects us to our registration page
                startActivity(new Intent(getApplicationContext(), Registration.class));
            }
        });
    }
}
