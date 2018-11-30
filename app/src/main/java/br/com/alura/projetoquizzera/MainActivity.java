package br.com.alura.projetoquizzera;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import br.com.alura.projetoquizzera.Model.User;

public class MainActivity extends AppCompatActivity {
    MaterialEditText edtNewUser, edtNewPassword, edtNewEmail; //para Sing Up;
    MaterialEditText edtUser, edtPassword; //para Sing in;

    Button btnSignUp, btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        edtUser = (MaterialEditText)findViewById(R.id.edtUser);
        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);

        btnSignIn = (Button)findViewById(R.id.btn_sign_in);
        btnSignUp = (Button)findViewById(R.id.btn_sign_up);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignUpDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(edtUser.getText().toString(),edtPassword.getText().toString());
            }

            private void signIn(final String user, final String pwd) {
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(user).exists())
                        {
                            if(!user.isEmpty())
                            {
                                User login = dataSnapshot.child(user).getValue(User.class);
                                if(login.getPassword().equals(pwd))
                                {
                                    Intent homeActivity = new Intent(MainActivity.this,Home.class);
                                    startActivity(homeActivity);
                                    finish();
                                }
                                else
                                    Toast.makeText(MainActivity.this,"Senha errada!",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"Por favor, insira seu nome de usuário",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                            Toast.makeText(MainActivity.this,"Usuário não existente!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void showSignUpDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Criar Conta");
        alertDialog.setMessage("Por favor, preencha todas as informações");

        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout,null);

        edtNewUser = (MaterialEditText)sign_up_layout.findViewById(R.id.edtNewUsername);
        edtNewEmail = (MaterialEditText)sign_up_layout.findViewById(R.id.edtNewEmail);
        edtNewPassword = (MaterialEditText)sign_up_layout.findViewById(R.id.edtNewPassword);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final User user = new User(edtNewUser.getText().toString(),
                        edtNewPassword.getText().toString(),
                        edtNewEmail.getText().toString());

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(user.getUserName()).exists())
                            Toast.makeText(MainActivity.this,"Este usuário já é registrado!",Toast.LENGTH_SHORT).show();
                        else
                        {
                            users.child(user.getUserName())
                                    .setValue(user);
                            Toast.makeText(MainActivity.this,"Usuário registrado com sucesso",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }
}
