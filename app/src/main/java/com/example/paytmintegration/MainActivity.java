package com.example.paytmintegration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText amtEditTxt;
    private RelativeLayout payNowBtn;
    private ProgressBar progressBar;
    private Integer ActivityRequestCode = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        amtEditTxt = findViewById(R.id.amt_txt);
        payNowBtn = findViewById(R.id.pay_btn);
        payNowBtn.setOnClickListener(MainActivity.this);
        progressBar = findViewById(R.id.progress);
    }

    public void btnProcessEvent (){

        progressBar.setVisibility(View.VISIBLE);
        String txnAmountString = amtEditTxt.getText().toString();
        String midString = "Partht87312038884258";
        String orderIdString = "ORDERID_01234567891";
        String txnTokenString = "6bab99a8c6e549319cb9d6b6228f64251619082062903";

        String host = "https://securegw-stage.paytm.in/";
        host = "https://securegw.paytm.in/";

        String errors = "";

        if(midString.equalsIgnoreCase("")){
            errors +="Enter valid MID here\n";
        }
        if(orderIdString.equalsIgnoreCase("")){
            errors +="Enter valid Order ID here\n";
        }
        if(txnTokenString.equalsIgnoreCase("")){
            errors +="Enter valid Txn Token here\n";
        }
        if(txnAmountString.equalsIgnoreCase("")){
            errors +="Enter valid Amount here\n";
        }
        Toast.makeText(this, errors, Toast.LENGTH_SHORT).show();


        progressBar.setVisibility(View.GONE);
        if(errors.equalsIgnoreCase("")){
            String orderDetails = "MID: " + midString + ", OrderId: " + orderIdString + ", TxnToken: " + txnTokenString + ", Amount: " + txnAmountString;
            Toast.makeText(this, orderDetails, Toast.LENGTH_SHORT).show();



            String callBackUrl = host + "theia/paytmCallback?ORDER_ID="+orderIdString;
            PaytmOrder paytmOrder = new PaytmOrder(orderIdString, midString, txnTokenString, txnAmountString, callBackUrl);
            TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback(){

                @Override
                public void onTransactionResponse(Bundle bundle) {
                    Toast.makeText(MainActivity.this, "Response (onTransactionResponse) : "+bundle.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("xxxBundle",bundle+" ..");
                }

                @Override
                public void networkNotAvailable() {
                    Log.d("xxx","Network not available");
                }

                @Override
                public void onErrorProceed(String s) {
                    Log.d("xxxonErrorProceed",s);
                }

                @Override
                public void clientAuthenticationFailed(String s) {
                    Log.d("xxxclientAuthenticatio",s);
                }

                @Override
                public void someUIErrorOccurred(String s) {
                    Log.d("xxxsomeUIErrorOccurred",s);
                }

                @Override
                public void onErrorLoadingWebPage(int i, String s, String s1) {
                    Log.d("xxxonErrorLoadinWP",s+" "+i+" "+s1);
                }

                @Override
                public void onBackPressedCancelTransaction() {
                    Log.d("xxxOBPTC","transaction cancelled...");
                }

                @Override
                public void onTransactionCancel(String s, Bundle bundle) {
                    Log.d("xxxTC",s+"..."+bundle);
                    Toast.makeText(MainActivity.this,bundle+"",Toast.LENGTH_SHORT).show();
                }
            });

            transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
            transactionManager.setAppInvokeEnabled(false);
            transactionManager.startTransaction(this, ActivityRequestCode);
        }



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode && data != null) {
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pay_btn :
                btnProcessEvent();
                break;
        }
    }
}