package edu.gatech.seclass.tccart.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.gatech.seclass.tccart.model.Customer;
import edu.gatech.seclass.tccart.model.Item;
import edu.gatech.seclass.tccart.model.Transaction;

/**
 * Created by shawnrajput on 3/19/16.
 */
public class DatabaseTcCart extends SQLiteOpenHelper {

    private static final String LOG = DatabaseTcCart.class.getName();

    /* Database Version */
    private static final int DATABASE_VERSION = 1;

    /* Database Name */
    private static final String DATABASE_NAME = "TCCartManagerDB";

    /* Error Codes for Customer */
    private static final int CUSTOMER_UPDATE_SUCCESS       = 1;
    private static final int CUSTOMER_UPDATE_ERROR         = 0;

    /* Error Codes for Items */
    private static final int ITEM_UPDATE_SUCCESS           = 1;
    private static final int ITEM_UPDATE_ERROR             = 0;

    /* Table Names */
    private static final String TABLE_TRANSACTION          = "trans";
    private static final String TABLE_CUSTOMER             = "customer";
    private static final String TABLE_ITEM                 = "item";

    /* Common column names */
    private static final String KEY_ID                     = "szEmailID";

    /* Transaction Table - column names */
    private static final String KEY_EMAILID                = "szEmailID";
    private static final String KEY_DATE                   = "szDate";
    private static final String KEY_AMOUNT                 = "dAmount";
    private static final String KEY_CASHDISCOUNT           = "dCashDiscount";
    private static final String KEY_VIPDISCOUNT 		   = "dVipDiscount";

    /* Item Table - column names */
    private static final String KEY_ITEMID                 = "szItemID";
    private static final String KEY_ITEMNAME               = "szItemName";
    private static final String KEY_ITEMTYPE               = "szItemType";
    private static final String KEY_ITEMPRICE              = "szItemPrice";

    /* Customer Table - column names */
    private static final String KEY_FIRSTNAME              = "szFirstName";
    private static final String KEY_LASTNAME               = "szLastName";
    private static final String KEY_QRCODE                 = "szQrCode";
    private static final String KEY_UNIQUECODE             = "szUniqueCode";
    private static final String KEY_VIPSTATUS              = "bVipStatus";
    private static final String KEY_REWARDSTATUS		   = "bRewardStatus";

    private static final String KEY_TOTAL_YTD 			   = "totalYTD";

    /* Table Create Statements */
    /* Transaction table create statement */
    private static final String CREATE_TABLE_ITEM = "CREATE TABLE "
            + TABLE_ITEM + "(" + KEY_ITEMID + " TEXT PRIMARY KEY,"
            + KEY_ITEMNAME + " TEXT," + KEY_ITEMTYPE + " TEXT,"
            + KEY_ITEMPRICE + " DOUBLE " + ")";

    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE "
            + TABLE_TRANSACTION + "(" + KEY_EMAILID + " TEXT, " + KEY_DATE
            + " TEXT, " + KEY_AMOUNT + " DOUBLE, " + KEY_CASHDISCOUNT
            + " DOUBLE, " +  KEY_VIPDISCOUNT + " DOUBLE " + ")";

    private static final String CREATE_TABLE_CUSTOMER = "CREATE TABLE "
            + TABLE_CUSTOMER + "(" + KEY_ID + " TEXT PRIMARY KEY,"
            + KEY_FIRSTNAME	+ " TEXT," + KEY_LASTNAME+ " TEXT,"
            + KEY_QRCODE + " TEXT,"  + KEY_VIPSTATUS + " INTEGER,"
            + KEY_UNIQUECODE + " TEXT," + KEY_REWARDSTATUS + " INTEGER" + ")";

    public DatabaseTcCart(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* creating required tables */
        db.execSQL(CREATE_TABLE_CUSTOMER);
        db.execSQL(CREATE_TABLE_TRANSACTION);
        db.execSQL(CREATE_TABLE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);

        // create new tables
        onCreate(db);
    }

    /* ------------------------ "TRANSACTION" ---------------- */

    /**
     * Create a transaction
     * @param transaction
     * @return transaction id
     */
    public long createTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAILID, transaction.getSzEmailID());
        values.put(KEY_DATE, transaction.getSzDate());
        values.put(KEY_AMOUNT, transaction.getdAmount());
        values.put(KEY_CASHDISCOUNT, transaction.getdCashDiscount());
        values.put(KEY_VIPDISCOUNT, transaction.getdVipDiscount());

        // insert row
        long transaction_id = db.insert(TABLE_TRANSACTION, "", values);

        return transaction_id;
    }

    /**
     * Get latest transaction for a user.
     * @param emailID
     * @return the customer's transaction with the most recent timestamp
     */
    public Transaction getLatestTransaction(String emailID) {
        Transaction tx = new Transaction();
        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTION
                + " WHERE " + KEY_EMAILID + " = \"" + emailID + "\" AND "
                + KEY_DATE + " = (SELECT MAX(" + KEY_DATE + ") FROM " + TABLE_TRANSACTION
                + " WHERE " + KEY_EMAILID + " = \"" + emailID + "\")";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            tx.setSzEmailID(c.getString((c.getColumnIndex(KEY_EMAILID))));
            tx.setSzDate((c.getString(c.getColumnIndex(KEY_DATE))));
            tx.setdAmount(c.getDouble(c.getColumnIndex(KEY_AMOUNT)));
            tx.setdCashDiscount(c.getDouble(c.getColumnIndex(KEY_CASHDISCOUNT)));
            tx.setdVipDiscount(c.getDouble(c.getColumnIndex(KEY_VIPDISCOUNT)));
        }

        return tx;
    }

    /**
     *  Get all previous transactions for a user.
     * @param emailID
     * @return empty array if no records found. Otherwise an array of transactions.
     */
    public List<Transaction> getAllTransactions(String emailID) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_EMAILID + " = \"" + emailID +"\"";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c != null && c.moveToFirst()) {
            do {
                Transaction tx = new Transaction();
                tx.setSzEmailID(c.getString((c.getColumnIndex(KEY_EMAILID))));
                tx.setSzDate((c.getString(c.getColumnIndex(KEY_DATE))));
                tx.setdAmount(c.getDouble(c.getColumnIndex(KEY_AMOUNT)));
                tx.setdCashDiscount(c.getDouble(c.getColumnIndex(KEY_CASHDISCOUNT)));
                tx.setdVipDiscount(c.getDouble(c.getColumnIndex(KEY_VIPDISCOUNT)));
                transactions.add(tx);
            } while (c.moveToNext() && c != null);
        }

        return transactions;
    }

    /* ------------------------ "CUSTOMER" ---------------- */

    /** Creating a Customer
     *
     * @param customer
     * @return 1 for success and 0 for failure.
     */
    public long createCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, customer.getSzEmailID());
        values.put(KEY_FIRSTNAME, customer.getSzFirstName());
        values.put(KEY_LASTNAME, customer.getSzLastName());
        values.put(KEY_QRCODE, customer.getSzQrCode());
        values.put(KEY_VIPSTATUS, customer.isbVipStatus());
        values.put(KEY_REWARDSTATUS, customer.isRewardStatus());

        // insert row
        try{
            db.insert(TABLE_CUSTOMER, null, values);
        }catch (Exception e) {
            return CUSTOMER_UPDATE_ERROR;
        }

        return CUSTOMER_UPDATE_SUCCESS;
    }

    /** Getting a Customer
     *
     * @param emailID
     * @return an empty customer object if not found. Otherwise, details of a particular customer
     */

    public Customer getCustomer(String emailID) {

        String selectQuery = "SELECT  * FROM " + TABLE_CUSTOMER + " WHERE " + KEY_ID + " = \'" + emailID + "\'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Customer cust = new Customer();

        if (c != null)
        {
            c.moveToFirst();

            cust.setSzEmailID(c.getString((c.getColumnIndex(KEY_ID))));
            cust.setSzFirstName((c.getString(c.getColumnIndex(KEY_FIRSTNAME))));
            cust.setSzLastName(c.getString(c.getColumnIndex(KEY_LASTNAME)));
            cust.setSzQrCode(c.getString(c.getColumnIndex(KEY_QRCODE)));
            cust.setbVipStatus(c.getInt(c.getColumnIndex(KEY_VIPSTATUS)) != 0);
            cust.setRewardStatus(c.getInt(c.getColumnIndex(KEY_REWARDSTATUS)) != 0);
        }
        return cust;
    }

    /** Getting a Customer To Print Card
     *
     * @param emailID
     * @return an empty customer object if not found. Otherwise, details of a particular customer
     */

    public Customer getCustomerByEmail(String emailID) {

        String selectQuery = "SELECT  * FROM " + TABLE_CUSTOMER + " WHERE " + KEY_ID + " = \'" + emailID + "\'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Customer cust = new Customer();

        if (c != null)
        {
            c.moveToFirst();

            cust.setSzFirstName((c.getString(c.getColumnIndex(KEY_FIRSTNAME))));
            cust.setSzLastName(c.getString(c.getColumnIndex(KEY_LASTNAME)));
            cust.setSzQrCode(c.getString(c.getColumnIndex(KEY_QRCODE)));
        }
        return cust;
    }

    /** Editing a Customer
     *
     * @param customer
     * @param szOrigEmailID
     * @return 1 for success. 0 for failure
     */

    public int updateCustomer(Customer customer, String szOrigEmailID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, customer.getSzEmailID());
        values.put(KEY_FIRSTNAME, customer.getSzFirstName());
        values.put(KEY_LASTNAME, customer.getSzLastName());
        values.put(KEY_QRCODE, customer.getSzQrCode());
        values.put(KEY_VIPSTATUS, customer.isbVipStatus() ? 1 : 0);
        values.put(KEY_REWARDSTATUS, customer.isRewardStatus() ? 1 : 0);

        // updating row
        try{
            db.update(TABLE_CUSTOMER, values, KEY_ID + " = ?",
                    new String[] { String.valueOf(szOrigEmailID) });
        }catch (Exception e) {
            return CUSTOMER_UPDATE_ERROR;
        }

        if (!szOrigEmailID.equals(customer.getSzEmailID()))
        {
            ContentValues values_tran = new ContentValues();
            values_tran.put(KEY_EMAILID, customer.getSzEmailID());

            db.update(TABLE_TRANSACTION, values_tran, KEY_EMAILID + " = ?",
                    new String[] { String.valueOf(szOrigEmailID) });
        }

        return CUSTOMER_UPDATE_SUCCESS;
    }
    //return all customer emails
    public List<String> getAllCustomers() {

        List<String> List = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  "+ KEY_ID + " FROM " + TABLE_CUSTOMER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                List.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return List;
    }


    //return a map of amount and date of each transaction
    public List<Map<String, String>> getCustTransFormatted(String email) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        String selectQuery = "SELECT  " + KEY_DATE  + ","  + KEY_AMOUNT + "," + KEY_CASHDISCOUNT + "," + KEY_VIPDISCOUNT
                + " FROM " + TABLE_TRANSACTION + " WHERE "
                + KEY_EMAILID + " = '" + email + "' order by 1 desc";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        DecimalFormat precision = new DecimalFormat("0.00");

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String amount 	= "$" + String.valueOf(precision.format(Double.parseDouble(String.valueOf(cursor.getString(1))))) + " Spent";
                String reward = "";
                if (Double.valueOf(cursor.getString(2)) > 0)
                    reward = "\n$" + String.valueOf(precision.format(Double.parseDouble(String.valueOf(cursor.getString(2))))) + " Reward";
                String vip = "";
                if (Double.valueOf(cursor.getString(3)) > 0)
                    vip = "\n$" + String.valueOf(precision.format(Double.parseDouble(String.valueOf(cursor.getString(3))))) + " VIP Discount";

                Map<String, String> datum = new HashMap<>(2);
                datum.put("Date", cursor.getString(0));
                datum.put("Amount", amount + reward + vip);
                list.add(datum);

            } while (cursor.moveToNext());
        }

        return list;
    }

    // ------------------------ "ITEM" ----------------//

    /** Creating an item
     *
     * @param item
     * @return 1 for success and 0 for failure.
     */
    public long createItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEMID, item.getSzItemID());
        values.put(KEY_ITEMNAME, item.getSzItemName());
        values.put(KEY_ITEMTYPE, item.getSzItemType());
        values.put(KEY_ITEMPRICE, item.getSzItemPrice());

        // insert row
        try{
            db.insert(TABLE_ITEM, null, values);
        }catch (Exception e) {
            return ITEM_UPDATE_ERROR;
        }

        return ITEM_UPDATE_SUCCESS;
    }

    /** Getting an Item
     *
     * @param itemId
     * @return an empty customer object if not found. Otherwise, details of a particular customer
     */

    public Item getItem(String itemId) {

        String selectQuery = "SELECT  * FROM " + TABLE_ITEM + " WHERE " + KEY_ITEMID + " = \'" + itemId + "\'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Item item = new Item();

        if (c != null)
        {
            c.moveToFirst();

            item.setSzItemID(c.getString((c.getColumnIndex(KEY_ITEMID))));
            item.setSzItemName((c.getString(c.getColumnIndex(KEY_ITEMNAME))));
            item.setSzItemType(c.getString(c.getColumnIndex(KEY_ITEMTYPE)));
            item.setSzItemPrice(c.getDouble(c.getColumnIndex(KEY_ITEMPRICE)));
        }
        return item;
    }

//    /** Editing an Item
//     *
//     * @param item
//     * @param szItemID
//     * @return 1 for success. 0 for failure
//     */
//
//    public int updateItem(Item item, String szItemID) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//
//        values.put(KEY_ITEMID, item.getSzItemID());
//        values.put(KEY_ITEMNAME, item.getSzItemName());
//        values.put(KEY_ITEMPRICE, item.getSzItemPrice());
//        values.put(KEY_ITEMTYPE, item.getSzItemType());
//
//        // updating row
//        try{
//            db.update(TABLE_ITEM, values, KEY_ITEMID + " = ?",
//                    new String[] { String.valueOf(szItemID) });
//        }catch (Exception e) {
//            return CUSTOMER_UPDATE_ERROR;
//        }
//
//        if (!szItemID.equals(item.getSzItemID()))
//        {
//            ContentValues values_tran = new ContentValues();
//            values_tran.put(KEY_ITEMID, item.getSzItemID());
//
//            db.update(TABLE_TRANSACTION, values_tran, KEY_ITEMID + " = ?",
//                    new String[] { String.valueOf(szItemID) });
//        }
//
//        return CUSTOMER_UPDATE_SUCCESS;
//    }
    //return all customer emails
//    public List<String> getAllCustomers() {
//
//        List<String> List = new ArrayList<>();
//
//        // Select All Query
//        String selectQuery = "SELECT  "+ KEY_ID + " FROM " + TABLE_CUSTOMER;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                List.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//
//        return List;
//    }

    public double getCustomerTotalYTD(String email) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        cal.set(year, 0, 1, 0, 0, 0);

        String begOfYear = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());

        String selectQuery = "SELECT SUM(" + KEY_AMOUNT + ") AS " + KEY_TOTAL_YTD + " FROM " + TABLE_TRANSACTION
                + " WHERE " + KEY_DATE + " > '" + begOfYear + "' AND " + KEY_EMAILID + " = '" + email + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        double ytdTotal = 0;
        // looping through all rows and adding to list
        if (c != null && c.moveToFirst()) {
            do {
                ytdTotal = c.getDouble(c.getColumnIndex(KEY_TOTAL_YTD));
            } while (c.moveToNext() && c != null);
        }

        return ytdTotal;
    }

    /* Close database */
    public void closeDataBase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
