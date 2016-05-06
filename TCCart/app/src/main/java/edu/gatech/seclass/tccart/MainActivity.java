package edu.gatech.seclass.tccart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import edu.gatech.seclass.tccart.database.DatabaseTcCart;

public class MainActivity extends Activity {

    private int[] buttons = {R.id.btnManagerCustomer, R.id.btnAddCustomer};
    private DatabaseTcCart db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int button : buttons) {
            Button btn = (Button) findViewById(button);
        }

        db = new DatabaseTcCart(getApplicationContext());

        db.closeDataBase();
    }

    public void btnClick(View view) {
        Button button = (Button) view;
        switch (button.getId()) {
            case R.id.btnAddCustomer:
                Intent mainIntent = new Intent(this, AddCustomer.class);
                startActivity(mainIntent);
                break;
            case R.id.btnManagerCustomer:
                mainIntent = new Intent(this, ViewCustomers.class);
                startActivity(mainIntent);
                break;
            default:
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

}
