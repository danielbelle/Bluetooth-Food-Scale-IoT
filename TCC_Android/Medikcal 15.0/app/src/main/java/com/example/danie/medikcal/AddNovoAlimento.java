package com.example.danie.medikcal;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import maes.tech.intentanim.CustomIntent;

public class AddNovoAlimento extends AppCompatActivity {



    Button btnAddAlimentoBD;
    private EditText mAlimento;
    private EditText mGramas;
    private EditText mKcal;
    private EditText mProt;
    private EditText mCarb;
    private EditText mGord;
    private EditText mFibras;
    public float bdKcal;
    public float bdProt;
    public float bdCarb;
    public float bdGord;
    public float bdFibras;

    //Firebase
    private final DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    DatabaseReference referenciaAlimentos1 = referencia.child("alimentos");
    private DatabaseReference dbF;
    public String a;
    final Alimento alimento = new Alimento();
    public boolean c=true;
    //Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_dois);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        final DatabaseReference referenciaAlimentos = referencia.child("alimentos");





        btnAddAlimentoBD = (Button) findViewById(R.id.btnAddAlimentoBD);
        mAlimento = (EditText) findViewById(R.id.nomenomeAlimento);
        mGramas = (EditText) findViewById(R.id.nomepesoPorcao);
        mKcal = (EditText) findViewById(R.id.nomequantKcal);
        mProt = (EditText) findViewById(R.id.nomequantProt);
        mCarb = (EditText) findViewById(R.id.nomequantCarb);
        mGord = (EditText) findViewById(R.id.nomequantGord);
        //mFibras = (EditText) findViewById(R.id.nomequantFibras);

        //FIREBASE

        dbF = FirebaseDatabase.getInstance().getReference("alimentos");
        //Firebase




        btnAddAlimentoBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float valGramas = Float.parseFloat(mGramas.getText().toString());
                float valKcal = Float.parseFloat(mKcal.getText().toString());
                float valProt = Float.parseFloat(mProt.getText().toString());
                float valCarb = Float.parseFloat(mCarb.getText().toString());
                float valGord = Float.parseFloat(mGord.getText().toString());
                //float valFibras = Float.parseFloat(mFibras.getText().toString());

                bdKcal = valKcal / valGramas;
                bdProt = valProt / valGramas;
                bdCarb = valCarb / valGramas;
                bdGord = valGord / valGramas;
                bdFibras = 0;

                resolveIsso();


            }
        });


    }


    //firebase


    private void resolveIsso(){


        alimento.setNomeAlimento(mAlimento.getText().toString());
        alimento.setValorKcal(bdKcal);
        alimento.setValorProt(bdProt);
        alimento.setValorCarb(bdCarb);
        alimento.setValorGord(bdGord);
        alimento.setValorFibras(bdFibras);
        alimento.setMinusculaNome(mAlimento.getText().toString().toLowerCase());
        a = mAlimento.getText().toString();

        Log.i("PORRA","aa :"+a);
        Query query1 = FirebaseDatabase.getInstance().getReference("alimentos").orderByChild("nomeAlimento");
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("PORRA","ONDATACHANCE: ");


                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Log.i("PORRA","FORRRR: ");
                    Alimento alimentoS1 = snapshot.getValue(Alimento.class);
                    assert alimentoS1 != null;
                    String b = alimentoS1.getNomeAlimento();

                    Log.i("PORRA","User nome   b: "+b);
                    Log.i("PORRA","User nome   a: "+a);

                    if(a.equals(b)){
                        c=true;

                        Log.i("PORRA","IGUALLLL!");
                        break;
                    }else{
                        c=false;
                        Log.i("PORRA","NAO IGUAL ");

                    }

                }
                Log.i("PORRA", String.valueOf(c));

                if(c){
                    Log.i("PORRA","ja existe esse krl da poha: ");
                    Toast.makeText(getApplicationContext(), "Esse alimento já existe, por favor adicione outro nome Ex: Feijão1", Toast.LENGTH_LONG).show();

                }else{
                    Log.i("PORRA","ele cria um novo alimento par esse capeta: ");
                    referenciaAlimentos1.push().setValue(alimento);

                    Toast.makeText(getApplicationContext(), "Alimento Adicionado", Toast.LENGTH_LONG).show();
                    finish();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("PORRA","ERRO");
            }



        });

    }


    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this,"left-to-right");
    }


}
