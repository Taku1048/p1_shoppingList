package jp.ac.jec.ws.p1_shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class EditListActivity extends AppCompatActivity {

    private Boolean exist = false;
    private static int itemIDCount = 1;

    ListView inputtedShoppingList;
    private Button inputUserProductBtn;
    private EditText inputUserProductEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);


        inputtedShoppingList = findViewById(R.id.inputtedProductList);
        inputUserProductBtn = findViewById(R.id.insertListBtn);
        inputUserProductEdit = findViewById(R.id.inputUserProduct);

    }

    @Override
    protected void onResume() {
        super.onResume();

        final ArrayList<String> productNames = new ArrayList<>();
        ArrayList<ListItem> listItems = new ArrayList<>();
        ArrayList<ArrayList<String>> userDateInDB = new ArrayList<>();

        ShoppingListSQLiteOpenHelper helper = new ShoppingListSQLiteOpenHelper(this);

        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            String sql = "SELECT DISTINCT PRODUCT_NAME FROM LISTS LEFT JOIN ITEMS ON LISTS.ITEMS_ID = ITEMS.ITEMS_ID";
            Cursor cr = db.rawQuery(sql, null);
            cr.moveToFirst();

            for (int i = 0; i < cr.getCount(); i++) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (int j = 0; j < 1; j++) {
                    arrayList.add(cr.getString(j));
                }
                userDateInDB.add(arrayList);
                cr.moveToNext();
            }

        } catch (SQLiteException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        for (int i = 0; i < userDateInDB.size(); i++) {
            ListItem item = new ListItem(userDateInDB.get(i).get(0));
            listItems.add(item);
        }

        for (ListItem tmp : listItems){
            productNames.add(tmp.getProductName());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,productNames);
        inputtedShoppingList.setAdapter(adapter);

        inputUserProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inputUserProductEdit.getText().toString().equals("")){
                    Toast.makeText(EditListActivity.this, "入力するかリストから選択してください", Toast.LENGTH_SHORT).show();
                } else {
                    for (String tmp: productNames){
                        if (tmp.equals(inputUserProductEdit.getText().toString())) {
                            exist = true;
                        }
                    }
                    if (exist) {
                        Toast.makeText(EditListActivity.this, "リストから選んでください", Toast.LENGTH_SHORT).show();
                        exist = false;
                    } else {
                        String productEditName = inputUserProductEdit.getText().toString();

                        insertDB("LISTS", productEditName, itemIDCount, getDate());
                        insertDB("ITEMS", productEditName, 0, "");
                        itemIDCount++;
                        finish();
                    }
                }

            }
        });

        inputtedShoppingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String productName = (String) parent.getItemAtPosition(position);

                insertDB("LISTS", productName, itemIDCount,getDate());
                insertDB("ITEMS", productName, 0,"");
                itemIDCount++;
                finish();
            }
        });


    }

    private void insertDB(String dbTable, String productName, int itemIDCount, String addDate) {

        ListItem tmp = new ListItem();
        if (dbTable.equals("LISTS")) {
            tmp.setItemID(itemIDCount);
            tmp.setInputData(addDate);
        } else if (dbTable.equals("ITEMS")) {
            tmp.setProductName(productName);
        }

        ShoppingListSQLiteOpenHelper helper = new ShoppingListSQLiteOpenHelper(this);
        if (helper.insertDate(tmp, dbTable)) {
            Toast.makeText(this, "登録完了", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "登録失敗", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDate(){
        DateFormat df = new SimpleDateFormat("MM月dd日");
        Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }
}
