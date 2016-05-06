package edu.gatech.seclass.tccart;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import edu.gatech.seclass.tccart.database.DatabaseTcCart;
import edu.gatech.seclass.tccart.model.Customer;

/**
 * Created by shawnrajput on 3/19/16.
 */
public class ViewCustomerTransactions extends Activity {
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer_transactions);
        Bundle b = getIntent().getExtras();
        this.email = b.getString("email");
        TextView txt = (TextView) findViewById(R.id.txtEmail);
        Customer cust = new DatabaseTcCart(getApplicationContext()).getCustomer(this.email);
        txt.setText(MessageFormat.format("View {0} {1}'s Transactions", cust.getSzFirstName(), cust.getSzLastName()));

        ListView lv =  (ListView) findViewById(R.id.lstTransactions);

        DatabaseTcCart db = new DatabaseTcCart(getApplicationContext());

        List<Map<String, String>> list = db.getCustTransFormatted(email);

        SimpleAdapter adapter = new SimpleAdapter(
                this, list, android.R.layout.simple_list_item_2,
                new String[] {"Amount", "Date"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_customer_transactions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
