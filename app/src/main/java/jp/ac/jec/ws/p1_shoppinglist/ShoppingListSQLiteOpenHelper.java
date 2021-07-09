package jp.ac.jec.ws.p1_shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class ShoppingListSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "SHOPPING_DB";

    public ShoppingListSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         * LISTS : shoppingList
         * ITEMS : product detail
         */
        String mainSql = "CREATE TABLE LISTS(_ID INTEGER PRIMARY KEY AUTOINCREMENT,ITEMS_ID INTEGER, ADD_DATE TEXT, BOUGHT_DATE TEXT)";

        String editSql = "CREATE TABLE ITEMS(ITEMS_ID INTEGER PRIMARY KEY AUTOINCREMENT,PRODUCT_NAME TEXT, FOREIGN KEY (ITEMS_ID) REFERENCES LISTS(ITEMS_ID))";

        db.execSQL(editSql);
        db.execSQL(mainSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertDate(ListItem item, String dbTable){
        ContentValues values = new ContentValues();

        if (dbTable.equals("LISTS")){
            values.put("ITEMS_ID", item.getItemID());
            values.put("ADD_DATE", item.getInputData());
        } else {
            values.put("PRODUCT_NAME",item.getProductName());

        }

        SQLiteDatabase db = getWritableDatabase();
        long ret = -1;
        try {
            ret = db.insert(dbTable,"",values);
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            db.close();
        }

        return ret > 0;
    }


}
