package com.example.danie.medikcal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class ViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public ViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
        });

    }

    public void setDetails(Context ctx, String nomeAlimento, Float valorKcal, float valorProt, float valorCarb, float valorGord){
        TextView mNomeAlimento = mView.findViewById(R.id.showAlimentoRow);
        TextView mQuantKcal = mView.findViewById(R.id.showKcalRow);

        TextView mQuantProt = mView.findViewById(R.id.showProtRow);
        TextView mQuantCarb = mView.findViewById(R.id.showCarbRow);
        TextView mQuantGord = mView.findViewById(R.id.showGordRow);



        mNomeAlimento.setText(nomeAlimento);
        mQuantKcal.setText(String.valueOf(valorKcal*100));

        mQuantProt.setText(String.valueOf(valorProt*100));
        mQuantCarb.setText(String.valueOf(valorCarb*100));
        mQuantGord.setText(String.valueOf(valorGord*100));

    }

    private ViewHolder.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }


    public void setOnClickListener(ViewHolder.ClickListener clickListener){
        mClickListener = clickListener;

    }





}


