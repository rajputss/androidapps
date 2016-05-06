package edu.gatech.seclass.tccart;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.gatech.seclass.services.PrintingService;
import edu.gatech.seclass.services.QRCodeService;
import edu.gatech.seclass.tccart.database.DatabaseTcCart;
import edu.gatech.seclass.tccart.model.Customer;

/**
 * Created by shawnrajput on 3/19/16.
 */
public class EditCustomer extends Activity {

    private String pFirstName, pLastName, pQrCode;
    private String email;
    private Customer customer;
    private EditText etFirst, etLast, etEmail, etQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        Bundle bundle = getIntent().getExtras();
        this.email = bundle.getString("email");

        DatabaseTcCart databaseTcCart = new DatabaseTcCart(getApplicationContext());
        this.customer = databaseTcCart.getCustomer(email);

        TextView textEmail = (TextView) findViewById(R.id.txtEmail);
        etFirst = (EditText) findViewById(R.id.etEditFirstName);
        etLast = (EditText) findViewById(R.id.etEditLastName);
        etQrCode = (EditText) findViewById(R.id.etEditQrCode);

        Button btn = (Button) findViewById(R.id.btnEditCustomer);

        if (customer != null) {
            textEmail.setText(email);
            etFirst.setText(customer.getSzFirstName());
            etLast.setText(customer.getSzLastName());
            etQrCode.setText(customer.getSzQrCode());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void editCustomer(View v) {
        Button btn = (Button) v;
        if (btn.getId() == R.id.btnEditCustomer) {
            if (!email.equals("")) {
                Customer cust = new Customer(
                        String.valueOf(email),
                        String.valueOf(etFirst.getText()),
                        String.valueOf(etLast.getText()),
                        String.valueOf(etQrCode.getText()),
                        this.customer.isbVipStatus(),
                        this.customer.isRewardStatus()
                );
                new DatabaseTcCart(getApplicationContext()).updateCustomer(cust, email);
                Toast.makeText(this, "Your changes were successful!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        if (btn.getId() == R.id.btnPrintCard) {
            if (!email.equals("")) {
                Customer cust = new Customer(
                        String.valueOf(email),
                        String.valueOf(etFirst.getText()),
                        String.valueOf(etLast.getText()),
                        String.valueOf(etQrCode.getText())
                );
                new DatabaseTcCart(getApplicationContext()).getCustomerByEmail(this.email);
                if (cust != null) {
                    pFirstName = cust.getSzFirstName();
                    pLastName = cust.getSzLastName();
                    pQrCode = QRCodeService.scanQRCode();/* cust.getHexDecimalId(); */
                }
                if (cardSuccessfullyPrinted()) {
                    Toast.makeText(this, "Your card has been printed successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public boolean cardSuccessfullyPrinted() {
        return PrintingService.printCard(pFirstName, pLastName, pQrCode);
    }
}
