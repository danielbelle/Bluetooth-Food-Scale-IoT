package com.example.danie.medikcal;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import maes.tech.intentanim.CustomIntent;


public class AlimentoListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private static final String NOME_ESCOLHIDO = "SALVAR NOME";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimento_list);


        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("alimentos");
    }

    private void  firebaseSearch(String searchText){
        String query = searchText.toLowerCase();
        final Query firebaseSearchQuery = mRef.orderByChild("minusculaNome").startAt(query).endAt(query+"\uf8ff");

        FirebaseRecyclerAdapter<Alimento, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Alimento, ViewHolder>(
                        Alimento.class,
                        R.layout.activity_row,
                        ViewHolder.class,
                        firebaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Alimento model, int position) {
                        viewHolder.setDetails(getApplicationContext(), model.getNomeAlimento(), model.getValorKcal(), model.getValorProt(), model.getValorCarb(), model.getValorGord());
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                TextView mNomeAlimento = view.findViewById(R.id.showAlimentoRow);


                                String nomeAl = mNomeAlimento.getText().toString();

                                SharedPreferences preferences = getSharedPreferences(NOME_ESCOLHIDO,0);
                                SharedPreferences.Editor editor = preferences.edit();

                                editor.putString("nomeAl",nomeAl);
                                editor.apply();
                                finish();
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                                //TODO LONG CLICK

                            }
                        });
                        return viewHolder;

                    }



                };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Alimento, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Alimento, ViewHolder>(
                        Alimento.class,
                        R.layout.activity_row,
                        ViewHolder.class,
                        mRef
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Alimento model, int position) {

                        viewHolder.setDetails(getApplicationContext(), model.getNomeAlimento(), model.getValorKcal(), model.getValorProt(), model.getValorCarb(), model.getValorGord());
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                TextView mNomeAlimento = view.findViewById(R.id.showAlimentoRow);

                                String nomeAl = mNomeAlimento.getText().toString();

                                SharedPreferences preferences = getSharedPreferences(NOME_ESCOLHIDO,0);
                                SharedPreferences.Editor editor = preferences.edit();

                                editor.putString("nomeAl",nomeAl);
                                editor.apply();
                                finish();
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                                //TODO LONG CLICK

                            }
                        });
                        return viewHolder;

                    }

                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id== R.id.action_settings){
            //TODO
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this,"fadein-to-fadeout");
    }


}
