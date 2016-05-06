package edu.gatech.seclass.tccart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

import edu.gatech.seclass.tccart.database.DatabaseTcCart;

/**
 * Created by shawnrajput on 3/19/16.
 */
public class ViewCustomers extends Activity implements OnItemClickListener {
    private ListView lv;
    private String email;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customers);
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popupwindow,null);
        popupWindow = new PopupWindow(popupView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

        lv = (ListView) findViewById(R.id.lstCustomers);
        lv.setOnItemClickListener(this);
        DatabaseTcCart db = new DatabaseTcCart(getApplicationContext());


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                db.getAllCustomers() );
        lv.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_customers, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        email =(String) parent.getItemAtPosition(position);
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popupwindow, parent, false);

        for (int button : new int[]{R.id.btnTrans,R.id.btnEdit,R.id.btnCheckout, R.id.btnDismiss}) 	{
            Button btn = (Button) popupView.findViewById(button);
        }

        popupWindow.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);
    }
    public void popupButtonClick(View v){

        this.popupWindow.dismiss();
        switch (v.getId())  {
            case R.id.btnEdit:
                startNewIntent(EditCustomer.class);
                break;
            case R.id.btnCheckout:
                startNewIntent(RunCreditCard.class);
                break;
            case R.id.btnTrans:
                startNewIntent(ViewCustomerTransactions.class);
                break;
        }
    }

    private void startNewIntent(Class className){
        Intent mainIntent = new Intent(ViewCustomers.this, (className));
        mainIntent.putExtra("email", email);
        startActivity(mainIntent);
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

}
