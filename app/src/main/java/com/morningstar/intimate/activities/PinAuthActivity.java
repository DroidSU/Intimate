/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.morningstar.intimate.R;
import com.morningstar.intimate.adapters.DigitContainerRecyclerAdapter;
import com.morningstar.intimate.managers.ConstantManager;
import com.morningstar.intimate.pojos.eventpojos.PinDigitClickedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PinAuthActivity extends AppCompatActivity {

    @BindView(R.id.editText_pin_first_digit)
    EditText editTextFirstDig;
    @BindView(R.id.editText_pin_second_digit)
    EditText editTextSecondDig;
    @BindView(R.id.editText_pin_third_digit)
    EditText editTextThirdDig;
    @BindView(R.id.editText_pin_fourth_digit)
    EditText editTextFourthDig;
    @BindView(R.id.pin_digit_zero)
    TextView textViewPin_Zero;
    @BindView(R.id.imageViewBackSpace)
    ImageView imageViewBackSpace;
    @BindView(R.id.imageViewSubmitPin)
    ImageView imageViewSubmit;
    @BindView(R.id.recycler_digit_container)
    RecyclerView recyclerViewDigitContainer;

    private DigitContainerRecyclerAdapter digitContainerRecyclerAdapter;
    private String userPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_auth);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setUpRecycler();
    }

    private void setUpRecycler() {
        recyclerViewDigitContainer.setLayoutManager(new GridLayoutManager(this, 3));
        digitContainerRecyclerAdapter = new DigitContainerRecyclerAdapter(this);
        recyclerViewDigitContainer.setAdapter(digitContainerRecyclerAdapter);
    }

    @OnClick(R.id.pin_digit_zero)
    public void zeroClicked() {
        addDigit("0");
    }

    @OnClick(R.id.imageViewBackSpace)
    public void onBackSpaceClicked() {
        if (!isEditTextEmpty(editTextFourthDig))
            editTextFourthDig.setText("");
        else if (!isEditTextEmpty(editTextThirdDig))
            editTextThirdDig.setText("");
        else if (!isEditTextEmpty(editTextSecondDig))
            editTextSecondDig.setText("");
        else if (!isEditTextEmpty(editTextFirstDig))
            editTextFirstDig.setText("");
    }

    @OnClick(R.id.imageViewSubmitPin)
    public void submitPin() {
        if (!isEditTextEmpty(editTextFirstDig) && !isEditTextEmpty(editTextSecondDig)
                && !isEditTextEmpty(editTextThirdDig) && !isEditTextEmpty(editTextFourthDig)) {

            userPin = editTextFirstDig.getText().toString() + editTextSecondDig.getText().toString() + editTextThirdDig.getText().toString() + editTextFourthDig.getText().toString();

            SharedPreferences sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
            boolean isPinSet = sharedPreferences.getBoolean(ConstantManager.IS_PIN_SET, false);
            if (!isPinSet) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(ConstantManager.IS_PIN_SET, true);
                editor.putString(ConstantManager.USER_PIN, userPin);
                editor.apply();
                startActivity(new Intent(PinAuthActivity.this, MainActivity.class));
                finish();
            }
            if (isPinSet) {
                String existingPin = sharedPreferences.getString(ConstantManager.USER_PIN, null);
                if (userPin != null && userPin.equalsIgnoreCase(existingPin)) {
                    startActivity(new Intent(PinAuthActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Wrong Pin Entered", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void addDigit(String s) {
        if (isEditTextEmpty(editTextFirstDig))
            editTextFirstDig.setText(s);
        else if (isEditTextEmpty(editTextSecondDig))
            editTextSecondDig.setText(s);
        else if (isEditTextEmpty(editTextThirdDig))
            editTextThirdDig.setText(s);
        else if (isEditTextEmpty(editTextFourthDig))
            editTextFourthDig.setText(s);
    }

    private boolean isEditTextEmpty(EditText editText) {
        String string = editText.getText().toString();
        return string.matches("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void digitClicked(PinDigitClickedEvent pinDigitClickedEvent) {
        addDigit(pinDigitClickedEvent.getValue());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
