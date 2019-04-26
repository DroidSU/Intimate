/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.morningstar.intimate.R;
import com.morningstar.intimate.adapters.DigitContainerRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PinAuthActivity extends AppCompatActivity {

    @BindView(R.id.editText_pin_first_digit)
    EditText editTextFirstDig;
    @BindView(R.id.editText_pin_second_digit)
    EditText editTextSecondDig;
    @BindView(R.id.editText_pin_third_digit)
    EditText editTextThirdDig;
    @BindView(R.id.editText_pin_fourth_digit)
    EditText editTextFourthDig;
    @BindView(R.id.imageViewBackSpace)
    ImageView imageViewBackSpace;
    @BindView(R.id.imageViewSubmitPin)
    ImageView imageViewSubmit;
    @BindView(R.id.recycler_digit_container)
    RecyclerView recyclerViewDigitContainer;

    private DigitContainerRecyclerAdapter digitContainerRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_auth);
        ButterKnife.bind(this);

        setUpRecycler();
    }

    private void setUpRecycler() {
        recyclerViewDigitContainer.setLayoutManager(new GridLayoutManager(this, 3));
        digitContainerRecyclerAdapter = new DigitContainerRecyclerAdapter(this);
        recyclerViewDigitContainer.setAdapter(digitContainerRecyclerAdapter);
    }
}
