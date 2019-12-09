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

public class Registration extends AppCompatActivity {
    //declarations
    private EditText email, password;
    private TextView signin;
    private Button regbtn;
    //an object of firebase auth
    private FirebaseAuth mAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

    //inits
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        email = findViewById(R.id.email_reg);
        password = findViewById(R.id.password_reg);
        signin = findViewById(R.id.signin_txt);
        regbtn = findViewById(R.id.reg_btn);

        //set regbtn to onlick listener
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();

                //checking if fields are empty
                if (TextUtils.isEmpty(mEmail)){
                    email.setError("Required field..");
                    return;
                }
                if (TextUtils.isEmpty(mPassword)){
                    password.setError("Required field..");
                    return;
                }

                //creating user with email and password
                progressDialog.setMessage("in progress ...");
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //show success state
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(Registration.this, "registered successfully", Toast.LENGTH_SHORT).show();
                            //redirecting intent to home page
                            startActivity(new Intent(getApplicationContext(),Home.class));
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(Registration.this, "error during registration", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //setting the sign in button to an o click listener
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
