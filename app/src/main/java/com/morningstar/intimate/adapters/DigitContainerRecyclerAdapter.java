/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.morningstar.intimate.R;
import com.morningstar.intimate.pojos.eventpojos.PinDigitClickedEvent;

import org.greenrobot.eventbus.EventBus;

public class DigitContainerRecyclerAdapter extends RecyclerView.Adapter<DigitContainerRecyclerAdapter.DigitViewHolder> {

    private Context context;
    private View view;

    public DigitContainerRecyclerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DigitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.circular_textview, parent, false);
        return new DigitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DigitViewHolder holder, int position) {
        String currentPinNumber = String.valueOf(position + 1);
        holder.textView.setText(currentPinNumber);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PinDigitClickedEvent pinDigitClickedEvent = new PinDigitClickedEvent();
                pinDigitClickedEvent.setValue(currentPinNumber);
                EventBus.getDefault().post(pinDigitClickedEvent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 9;
    }

    class DigitViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        DigitViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_Pin);
        }
    }
}
