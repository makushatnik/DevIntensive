package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

/**
 * Created by Ageev Evgeny on 25.08.2016.
 */
public class BillingActivity extends BaseActivity {
    private EditText mSumEdit;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSumEdit = (EditText) findViewById(R.id.sum_et);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_btn);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //showToast("Cecked " + checkedId);
            }
        });
    }

    public void sendPaymentInfo(View v) {
        RadioButton bankBtn = (RadioButton) mRadioGroup.findViewById(R.id.bank_card_btn);
        RadioButton yandBtn = (RadioButton) mRadioGroup.findViewById(R.id.yandex_btn);
        if (bankBtn.isChecked()) {
            showToast("Bank card selected");
        } else if (yandBtn.isChecked()) {
            showToast("Yandex selected");
        }
        String receiver = "41001276984788";

        Intent i = new Intent();
        i.putExtra("sum", mSumEdit.getText().toString());
        setResult(ConstantManager.REQUEST_PAYMENT, i);
        finish();
    }
}
