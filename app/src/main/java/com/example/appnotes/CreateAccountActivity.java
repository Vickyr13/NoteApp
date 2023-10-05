package com.example.appnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button btnCreateAccount;
    ProgressBar progressBar;
    TextView loginBtnView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //variables que permite unir lo grafico con lo logico
        emailEditText = findViewById(R.id.txtEmail);
        passwordEditText = findViewById(R.id.txtPassword);
        confirmPasswordEditText = findViewById(R.id.txtConfirmPassword);
        loginBtnView = findViewById(R.id.txtLoginBtn);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        progressBar = findViewById(R.id.progressBar);
        //crear evento que permite la funcionalidad de crear cuenta
        btnCreateAccount.setOnClickListener(v->createAccount());
        //Crear evento que me dirigira al activity login por si ya se tiene usuario
        loginBtnView.setOnClickListener(v->startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class)));

    }
    void createAccount(){
        //variables que almacenaran el contenido que el usuario ingrese desde la vista
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        //validar si los campos se encuentran vacios
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(CreateAccountActivity.this, "Los campos estan vacios", Toast.LENGTH_SHORT).show();
        }else {
            boolean isvalidated = validateData(email, password, confirmPassword);
            if (!isvalidated) {
                return;
            }
            //Si los datos han pasado la validación, esta línea llama a una función llamada createAccountInFirebase con los parámetros email y password
            createAccountInFirebase(email, password);
        }
    }

    void createAccountInFirebase(String email,String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            //creating acc is done
                            Toast.makeText(CreateAccountActivity.this, "Cuenta creada Existosamente, Verificar Email", Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else{
                            //failure
                            Toast.makeText(CreateAccountActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );



    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            btnCreateAccount.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            btnCreateAccount.setVisibility(View.VISIBLE);
        }
    }


    boolean validateData(String email, String password, String confirmPassword) {
        //validacion de los datos ingresados dentro de cada input/EditText
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("El correo ingresado es invalido");
            return  false;
        }
        if(password.length() <6){
            passwordEditText.setError("Debe ingresar mas de 6 caracteres");
            return false;
        }
        if(!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Las contraseñas no son iguales");
            return false;
        }
        return true;
    }
}