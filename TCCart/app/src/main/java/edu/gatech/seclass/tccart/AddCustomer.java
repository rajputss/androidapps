package edu.gatech.seclass.tccart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.gatech.seclass.tccart.database.DatabaseTcCart;
import edu.gatech.seclass.tccart.model.Customer;

/**
 * Created by shawnrajput on 3/19/16.
 */
public class AddCustomer extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        int[] textBoxes = {R.id.etEmail, R.id.etFirstName,
                R.id.etLastName};

        for (int text : textBoxes) {
            EditText btn = (EditText) findViewById(text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    public void addCustomer(View v) {
        Button btn = (Button) v;
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etFirstName = (EditText) findViewById(R.id.etFirstName);
        EditText etLastName = (EditText) findViewById(R.id.etLastName);

        if (!etEmail.getText().equals("")) {
            if (btn.getId() == R.id.btnSubmit) {
                new DatabaseTcCart(getApplicationContext()).createCustomer(new Customer(
                        String.valueOf(etEmail.getText()),
                        String.valueOf(etFirstName.getText()),
                        String.valueOf(etLastName.getText()),
                        false,
                        false)
                );
                Toast.makeText(this, "Your customer has been added!", Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(this, RunCreditCard.class);
                mainIntent.putExtra("email", String.valueOf(etEmail.getText()));
                mainIntent.putExtra("firstName", String.valueOf(etFirstName.getText()));
                mainIntent.putExtra("lastName", String.valueOf(etLastName.getText()));
                startActivity(mainIntent);

                finish();
            }
        }
    }
}
