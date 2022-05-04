package com.example.danie.medikcal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.UUID;
import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;

    Button btnAddNew;
    Button btnEscolher;
    public String a;
    public TextView showAlimento;
    public TextView showKcal;
    public TextView showProt;
    public TextView showCarb;
    public TextView showGord;
    //public TextView showFibras;
    private static final String NOME_ESCOLHIDO = "SALVAR NOME";

    /// BLUETOOTH
    int pesoFinalInt;
    float pesofinal;
    Button btnConexao;
    Button btnTare;
    Button btnLogIn;
    BluetoothAdapter meuBluetoothAdapter = null;
    BluetoothDevice meuDevice = null;
    BluetoothSocket meuSocket = null;
    ConnectedThread connectedThread;
    Handler mHandler;
    StringBuilder dadosBluetooth = new StringBuilder();
    private static final int SOLICITA_ATIVACAO=1;
    private static final int SOLICITA_CONEXAO=2;
    private static final int MESSAGE_READ=3;
    private static String MAC = null;
    boolean conexao = false;
    boolean first = true;
    UUID MEU_UUAI = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    /// BLUETOOTH

    //Firebase
    private DatabaseReference dbF;
    //Firebase

    public ArcProgress arc;
    TextView textItemTwoOne;
    TextView textItemTwoTwo;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_um);

        arc = (ArcProgress) findViewById(R.id.medidor);
        textItemTwoOne = (TextView) findViewById(R.id.textItemTwoOne);
        textItemTwoTwo = (TextView) findViewById(R.id.textItemTwoTwo);


        btnLogIn= (Button) findViewById(R.id.btnLogIn);
        btnEscolher = (Button) findViewById(R.id.btnAdd);
        Alimento alimento = new Alimento();


        String nomeAlimentoSearch = alimento.getNomeAlimento();


        showKcal = (TextView) findViewById(R.id.showKcal);
        showProt = (TextView) findViewById(R.id.showProt);
        showCarb = (TextView) findViewById(R.id.showCarb);
        showGord = (TextView) findViewById(R.id.showGord);
        //showFibras = (TextView) findViewById(R.id.showFibras);
        showAlimento = (TextView) findViewById(R.id.showAlimento);

        showAlimento.setText(nomeAlimentoSearch);

        a=showAlimento.getText().toString();

        //FIREBASE

        dbF = FirebaseDatabase.getInstance().getReference("alimentos");


        Query query1 = FirebaseDatabase.getInstance().getReference("alimentos").orderByChild("nomeAlimento")
                .startAt(a).endAt("\uf8ff");

        query1.addListenerForSingleValueEvent(valueEventListener);



        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogIn();
            }
        });

        btnEscolher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEscolher();
            }
        });


        //Firebase


        btnAddNew= (Button) findViewById(R.id.btnAddNew);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNovoAlimento();
            }
        });


        btnConexao= (Button) findViewById(R.id.btnConexao);
        btnTare = (Button) findViewById(R.id.btnTare);
        meuBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        btnTare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(conexao){
                    connectedThread.enviar("t");
                }else{
                    Toast.makeText(getApplicationContext(),"Bluetooth não Conectado!", Toast.LENGTH_LONG).show();
                }
            }
        });


        if(meuBluetoothAdapter.isEnabled()){
            //btnConexao.setText("Parear");
            textItemTwoOne.setText(" ");
            textItemTwoTwo.setText(" Parear");

        }


        btnConexao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(meuBluetoothAdapter==null){
                    Toast.makeText(getApplicationContext(),"Seu dispositivo não possui Bluetooth", Toast.LENGTH_LONG).show();

                }else if(!meuBluetoothAdapter.isEnabled()){
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, SOLICITA_ATIVACAO);
                }




                if(conexao){
                    //desconectar
                    try{
                        meuSocket.close();
                        conexao =false;
                        //btnConexao.setText("Conectar");
                        textItemTwoOne.setText(" ");
                        textItemTwoTwo.setText(" Conectar");
                        meuBluetoothAdapter.disable();
                        Toast.makeText(getApplicationContext(),"Bluetooth Desconectado", Toast.LENGTH_LONG).show();
                    }catch(IOException erro){

                        Toast.makeText(getApplicationContext(),"Aconteceu um Erro1", Toast.LENGTH_LONG).show();
                    }

                }else if (meuBluetoothAdapter.isEnabled()){
                    //conectar
                    Intent abreLista = new Intent(MainActivity.this, ListaDispositivos.class);
                    startActivityForResult(abreLista,SOLICITA_CONEXAO);
                }




            }
        });


        mHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {

                if(msg.what == MESSAGE_READ){

                    String recebidos = (String) msg.obj;

                    dadosBluetooth.append(recebidos);

                    int fimInformacao = dadosBluetooth.indexOf("}");

                    if(fimInformacao>0){

                        String dadosCompletos = dadosBluetooth.substring(0,fimInformacao);

                        int tamanhoInformacao = dadosCompletos.length();

                        if(dadosBluetooth.charAt(0) == '{'){


                            String dadosFinais = dadosBluetooth.substring(1,tamanhoInformacao); // PESO TOTAL FINAL


                            //TextView mostraPeso = (TextView) findViewById(R.id.textView);
                            pesofinal=Float.parseFloat(dadosFinais);

                            //pesoFinalInt=Math.round(pesofinal);

                            Query query1 = FirebaseDatabase.getInstance().getReference("alimentos").orderByChild("nomeAlimento")
                                    .equalTo(a);

                            query1.addListenerForSingleValueEvent(valueEventListener);


                            float pesoFinalFloat = pesofinal*1000;
                            pesoFinalInt = Math.round(pesoFinalFloat);


                            //Log.i("PORRA","pesofinal: "+pesoFinalInt);
                            //mostraPeso.setText(dadosFinais); Integer.valueOf(dadosFinais)
                            arc.setProgress(pesoFinalInt);


                        }
                        dadosBluetooth.delete(0,dadosBluetooth.length());


                    }

                }


            }//
        };


    }// fim do OnCreate

    //Firebase
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Alimento alimentoS = snapshot.getValue(Alimento.class);
                    assert alimentoS != null;
                    float kcal,prot,carb,gord,fibras;
                    float gain=1000;
                    kcal=gain*pesofinal*alimentoS.getValorKcal();
                    prot=gain*pesofinal*alimentoS.getValorProt();
                    carb=gain*pesofinal*alimentoS.getValorCarb();
                    gord=gain*pesofinal*alimentoS.getValorGord();
                    //fibras=gain*pesofinal*alimentoS.getValorFibras();


                    showKcal.setText(String.valueOf(kcal));
                    showProt.setText(String.valueOf(prot));
                    showCarb.setText(String.valueOf(carb));
                    showGord.setText(String.valueOf(gord));
                    //showFibras.setText(String.valueOf(fibras));

                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    //firebase


    public void openAddNovoAlimento(){
        Intent intent = new Intent(this, AddNovoAlimento.class);
        startActivity(intent);
        CustomIntent.customType(this,"right-to-left");
    }

    public void openLogIn(){
        Intent intent = new Intent(this, LogInGoogle.class);
        startActivity(intent);
        CustomIntent.customType(this,"fadein-to-fadeout");
    }

    public void openEscolher(){
        Intent intent = new Intent(this, AlimentoListActivity.class);
        startActivity(intent);
        CustomIntent.customType(this,"fadein-to-fadeout");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case SOLICITA_ATIVACAO:
                if(resultCode== Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(),"O Bluetooth foi ativado", Toast.LENGTH_LONG).show();
                    //btnConexao.setText("Parear"); // INICIO
                    textItemTwoOne.setText(" ");
                    textItemTwoTwo.setText("   Parear");
                }else{
                    Toast.makeText(getApplicationContext(),"O Bluetooth não foi ativado", Toast.LENGTH_LONG).show();

                }
                break;

            case SOLICITA_CONEXAO:
                if(resultCode == Activity.RESULT_OK){

                    MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);

                    //Toast.makeText(getApplicationContext(),"MAC FINAL: " + MAC, Toast.LENGTH_LONG).show();
                    meuDevice = meuBluetoothAdapter.getRemoteDevice(MAC);


                    try {
                        meuSocket = meuDevice.createRfcommSocketToServiceRecord(MEU_UUAI);

                        meuSocket.connect();

                        conexao = true;

                        connectedThread = new ConnectedThread(meuSocket);
                        connectedThread.start();

                        //btnConexao.setText("Desconectar");
                        textItemTwoOne.setText(" ");
                        textItemTwoTwo.setText(" Desconectar");

                        Toast.makeText(getApplicationContext(),"Você foi Conectado com: " + MAC, Toast.LENGTH_LONG).show();

                    } catch (IOException erro){
                        conexao = false;

                        Toast.makeText(getApplicationContext(),"Ligue a Balança! hehehe", Toast.LENGTH_LONG).show();
                    }




                }else{

                    Toast.makeText(getApplicationContext(),"Falha ao obter o MAC3", Toast.LENGTH_LONG).show();

                }

        }



    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            //Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    String dadosBt = new String(buffer,0,bytes);

                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, dadosBt).sendToTarget();


                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void enviar(String dadosEnviar) {
            byte[] msgBuffer = dadosEnviar.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(NOME_ESCOLHIDO, 0);
        String qnomeAl = preferences.getString("nomeAl", "");


        showAlimento.setText(qnomeAl);
        a=showAlimento.getText().toString();
        dbF = FirebaseDatabase.getInstance().getReference("alimentos");


        Query query1 = FirebaseDatabase.getInstance().getReference("alimentos").orderByChild("nomeAlimento")
                .startAt(a).endAt("\uf8ff");

        query1.addListenerForSingleValueEvent(valueEventListener);


    }
}
