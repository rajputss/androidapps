package edu.gatech.seclass.tccart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

import edu.gatech.seclass.tccart.database.DatabaseTcCart;
import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Transaction;

/**
 * Created by shawnrajput on 3/19/16.
 */
public class TransactionSummary extends Activity {

    private String email;
    private Transaction transaction;
    private Customer customer;

    private TextView customerName, transactionAmount, todaysDate, message, originalAmount;
    DecimalFormat precision = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_summary);

        Bundle b 			= getIntent().getExtras();
        this.email			= b.getString("email");

        DatabaseTcCart db 	= new DatabaseTcCart(getApplicationContext());
        this.transaction	= db.getLatestTransaction(email);
        this.customer 		= db.getCustomer(email);

		/*
		 * initialize all text boxes in this screen
		 */
        customerName 		= (TextView) findViewById(R.id.customerName);
        transactionAmount	= (TextView) findViewById(R.id.transactionAmount);
        todaysDate 			= (TextView) findViewById(R.id.todaysDate);
        message 			= (TextView) findViewById(R.id.tvMsg);
        originalAmount		= (TextView) findViewById(R.id.tvOriginal);


        if (transaction!=null) {
            customerName.setText(Html.fromHtml("<html><strong>Customer: </strong></html>" + customer.getSzFirstName() + " " + customer.getSzLastName()));
            transactionAmount.setText(Html.fromHtml("<html><strong>Amount: $</strong></html>" + (precision.format(Double.parseDouble(String.valueOf(transaction.getdAmount()))))));
            todaysDate.setText(Html.fromHtml("<html><strong>Date: </strong></html> " +  transaction.getSzDate()));
            message.setText(Html.fromHtml("<Html><p>Congratulations!</p> <p>You successfully processed this customer&apos;s transaction!</p>"));
            originalAmount.setText(Html.fromHtml("<html><strong>You saved: $</strong></html> " +  precision.format(Double.parseDouble(String.valueOf(transaction.getdVipDiscount() + transaction.getdCashDiscount())))));

        }
        db.close();
    }

    public void returnToMainMenu(View v) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
