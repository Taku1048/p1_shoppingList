package jp.ac.jec.ws.p1_shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView shoppingList;
    private ShoppingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shoppingList = findViewById(R.id.shoppingList);
        Button moveEditActivityBtn = findViewById(R.id.moveActivity);

        moveEditActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<ListItem> listItems = new ArrayList<>();
        ArrayList<ArrayList<String>> userDateInDB = new ArrayList<>();

        ShoppingListSQLiteOpenHelper helper = new ShoppingListSQLiteOpenHelper(this);

        try (SQLiteDatabase db = helper.getReadableDatabase()){
            String sql = "SELECT ADD_DATE, PRODUCT_NAME, LISTS.ITEMS_ID FROM LISTS LEFT JOIN ITEMS ON LISTS.ITEMS_ID = ITEMS.ITEMS_ID WHERE BOUGHT_DATE is NULL ";
            Cursor cr = db.rawQuery(sql,null);
            cr.moveToFirst();

            for (int i = 0; i < cr.getCount();i++){
                ArrayList<String> arrayList = new ArrayList<>();
                for (int j = 0; j < 3; j++){
                    arrayList.add(cr.getString(j));
                }
                userDateInDB.add(arrayList);
                cr.moveToNext();
            }

        } catch (SQLiteException e){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        for (int i = 0; i < userDateInDB.size();i++){
            ListItem item = new ListItem(userDateInDB.get(i).get(0),userDateInDB.get(i).get(1),String.valueOf(userDateInDB.get(i).get(2)));
            listItems.add(item);
        }

        adapter = new ShoppingListAdapter(this,R.layout.list_custom_cell,listItems);
        shoppingList.setAdapter(adapter);
    }

    /**
     * shoppingListAdapterのInnerClass
     */
    class ShoppingListAdapter extends ArrayAdapter<ListItem> {

        private int resource;
        private List<ListItem> items;
        private LayoutInflater inflater;

        public ShoppingListAdapter(Context context, int resource, List<ListItem> objects) {
            super(context, resource, objects);

            this.resource = resource;
            items = objects;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {

            final View view;

            if (convertView != null){
                view = convertView;
            } else {
                view = inflater.inflate(resource,null);

            }
            final ListItem item = items.get(position);
            final TextView inputData = view.findViewById(R.id.inputData);
            TextView inputProductName = view.findViewById(R.id.inputProductName);
            Button deleteCell = view.findViewById(R.id.deleteBtn);


            inputData.setText(item.getInputData());
            inputProductName.setText(item.getProductName());

            deleteCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ShoppingListSQLiteOpenHelper helper = new ShoppingListSQLiteOpenHelper(getContext());

                    try(SQLiteDatabase db = helper.getReadableDatabase()){

                        ContentValues values = new ContentValues();
                        values.put("BOUGHT_DATE",getDate());
                        db.update("LISTS",values,"ITEMS_ID = ?",new String[]{String.valueOf(item.getItemID())});

                    } catch (SQLiteException e){
                        e.printStackTrace();
                    }

                    items.remove(item);
                    adapter.notifyDataSetChanged();
                }
            });
            return view;
        }

        private String getDate(){
            DateFormat df = new SimpleDateFormat("MM月dd日");
            Date date = new Date(System.currentTimeMillis());
            return df.format(date);
        }
    }

}
