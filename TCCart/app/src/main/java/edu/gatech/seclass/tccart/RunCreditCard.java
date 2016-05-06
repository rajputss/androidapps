package edu.gatech.seclass.tccart;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.gatech.seclass.services.CreditCardService;
import edu.gatech.seclass.services.EmailService;
import edu.gatech.seclass.services.PaymentService;

import edu.gatech.seclass.tccart.database.DatabaseTcCart;
import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Reward;
import edu.gatech.seclass.tccart.model.Transaction;
import edu.gatech.seclass.tccart.model.VipStatusDiscount;

/**
 * Created by shawnrajput on 3/19/16.
 */
public class RunCreditCard extends Activity {

    DecimalFormat precision = new DecimalFormat("0.00");
    private String email;
    private String sFirstName = "";
    private String sLastName = "";
    private String sAccountNum = "";
    private String sExpiration = "";
    private String sSecurityCode = "";
    private double dAmount = 0.0;
    private double dVipAmountOff = 0.0;

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            Button btnSwipe = (Button) findViewById(R.id.btnSwipeCard);

            if (s.toString().trim().length() < 1) {
                btnSwipe.setEnabled(false);
            } else {
                btnSwipe.setEnabled(true);
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }
    };

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_credit_card);

        TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
        Bundle b = getIntent().getExtras();
        this.email = b.getString("email");

        txtEmail.setText(email);

        EditText etAmount = (EditText) findViewById(R.id.etAmount);

        etAmount.addTextChangedListener(textWatcher);

        Button btnSwipe = (Button) findViewById(R.id.btnSwipeCard);
        Button btnProcess = (Button) findViewById(R.id.btnCheckoutCust);

        btnSwipe.setEnabled(false);
        btnProcess.setEnabled(false);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.run_credit_card, menu);

        return true;
    }

    public void btnSwipeCard(View v) {
        String cardInfo = CreditCardService.readCreditCard();

        TextView tvCCnum = (TextView) findViewById(R.id.etCCnum);
        TextView tvSecCode = (TextView) findViewById(R.id.etSecCode);
        TextView tvExpDate = (TextView) findViewById(R.id.etExpDate);
        TextView tvName = (TextView) findViewById(R.id.etName);
        EditText etAmount = (EditText) findViewById(R.id.etAmount);
        Button btnProcess = (Button) findViewById(R.id.btnCheckoutCust);

        if (cardInfo == "Error") {
            Toast.makeText(this,
                    "There was an error getting your card info, please swipe again",
                    Toast.LENGTH_SHORT).show();

            tvCCnum.setText("");
            tvSecCode.setText("");
            tvExpDate.setText("");
            tvName.setText("");

            btnProcess.setEnabled(false);

        } else {
            String[] info = cardInfo.split("#");

            sFirstName = info[0];
            sLastName = info[1];
            sAccountNum = info[2];
            sExpiration = info[3];
            sSecurityCode = info[4];


            dAmount = Double.parseDouble(precision.format(Double.parseDouble(String.valueOf(etAmount.getText()))));

            etAmount.setText(Double.toString(dAmount));
            tvCCnum.setText(sAccountNum);
            tvSecCode.setText(sSecurityCode);
            tvExpDate.setText(sExpiration);
            tvName.setText(sFirstName + " " + sLastName);

            btnProcess.setEnabled(true);

        }

    }

    private void calculateCustomerDiscount() {
        Customer customer = new DatabaseTcCart(getApplicationContext()).getCustomer(this.email);

        if (customer.isbVipStatus()) {
            this.dVipAmountOff = this.dAmount * VipStatusDiscount.PERCENT_OFF;
            this.dAmount *= (1 - VipStatusDiscount.PERCENT_OFF);
        }

        if (customer.isbVipStatus())
            this.dAmount -= Reward.AMOUNT_OFF;
    }

    public boolean cardSuccessfullyProcessed(View v) {
        Date sDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            sDate = formatter.parse(sExpiration);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return PaymentService.processPayment(sFirstName, sLastName,
                sAccountNum, sDate, sSecurityCode, dAmount);
    }

    private void sendRewardEmail() {
        String subject = "You have recieved a reward!";
        String body = "Congratulations! You have received a reward! You may use it next time you shop here.";

        EmailService.sendEMail(email, subject, body);
    }

    private void sendVipStatusEmail() {
        String subject = "You are a VIP member!";
        String body = "Congratulations! You have reached VIP status! You will now get 5% off when you shop here for life!";

        EmailService.sendEMail(email, subject, body);
    }

    private void addCustomerTransactionToDB() {
        DatabaseTcCart db = new DatabaseTcCart(getApplicationContext());

        Customer customer = db.getCustomer(this.email);
        Transaction transaction = new Transaction();

        if (customer.isRewardStatus()) {
            transaction.setdCashDiscount(Reward.AMOUNT_OFF);
            customer.setRewardStatus(false);
        } else {
            transaction.setdCashDiscount(0);
        }

        if (this.dAmount >= Reward.AMOUNT_TO_EARN_REWARD) {
            customer.setRewardStatus(true);
            sendRewardEmail();
        } else {
            customer.setRewardStatus(false);
        }

        if (customer.isbVipStatus()) {
            transaction.setdVipDiscount(this.dVipAmountOff);
        } else {
            transaction.setdVipDiscount(0);
        }

        if (db.getCustomerTotalYTD(this.email) + this.dAmount >= VipStatusDiscount.AMOUNT_TO_EARN_VIP_STATUS)
            customer.setbVipStatus(true);
        sendVipStatusEmail();

        transaction.setSzEmailID(this.email);
        transaction.setdAmount(this.dAmount);
        transaction.setSzDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));

        db.updateCustomer(customer, this.email);
        db.createTransaction(transaction);
        db.close();
    }

    public void btnProcessCard(View v) {
        if (cardSuccessfullyProcessed(v)) {
            calculateCustomerDiscount();
            addCustomerTransactionToDB();
            Intent mainIntent = new Intent(this, TransactionSummary.class);
            mainIntent.putExtra("email", this.email);
            startActivity(mainIntent);

            Toast.makeText(this, "Card Processed Successfully!!!",
                    Toast.LENGTH_SHORT).show();
            finish();

        } else {
            Toast.makeText(
                    this,
                    "There was an error processing your card, please try again",
                    Toast.LENGTH_SHORT).show();

            this.dAmount = Double.parseDouble(precision.format(Double.parseDouble(String.valueOf(((EditText) findViewById(R.id.etAmount)).getText()))));
            this.dVipAmountOff = 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "RunCreditCard Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://edu.gatech.seclass.tccart/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "RunCreditCard Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://edu.gatech.seclass.tccart/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
