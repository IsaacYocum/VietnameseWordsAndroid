package com.example.vietnamesewordsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);
    }

    // Clears the two textboxes in the main activity
    public void btnClearOnClick(View v) {
        EditText editText_Vietnamese = findViewById(R.id.editText_Vietnamese);
        EditText editText_English = findViewById(R.id.editText_English);

        TableLayout tableLayout = findViewById(R.id.tableLayout_TranslationsList);
        tableLayout.removeAllViews();

        editText_Vietnamese.getText().clear();
        editText_English.getText().clear();
    }

    public void btnAllOnClick(View v) {
        Cursor data = databaseHelper.getAll();
        populateTable(data);
    }

    public void btnAddOnClick(View v) {
        EditText editText_Vietnamese = findViewById(R.id.editText_Vietnamese);
        EditText editText_English = findViewById(R.id.editText_English);

        String vWord = editText_Vietnamese.getText().toString();
        String eWord = editText_English.getText().toString();

        boolean addTranslation = databaseHelper.addTranslation(vWord, eWord);

        if(addTranslation) {
            toastMessage("Successfully added translation");
        } else {
            toastMessage("Failed to add translation");
        }
    }

    private void populateTable(Cursor data) {
        Cursor translationsList = data;
        ArrayList<String> englishListData = new ArrayList<>();
        ArrayList<String> vietnameseListData = new ArrayList<>();
        while(data.moveToNext()) {
            englishListData.add(data.getString(1));
            vietnameseListData.add(data.getString(2));
        }

        // Assign this table layout to the existing one in activity_main.xml
        TableLayout tableLayout = findViewById(R.id.tableLayout_TranslationsList);
        tableLayout.removeAllViews();

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT,1);
        params.setMargins(2, 2, 2, 2);

        // Create a table row for each translations returned from the database
        if (englishListData.size() == vietnameseListData.size()) {
            for (int i = 0; i < englishListData.size(); i++) {
                TableRow tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                EditText englishEditText = new EditText(this);
                englishEditText.setText(englishListData.get(i));
                englishEditText.setLayoutParams(params);
                englishEditText.setMaxWidth(englishEditText.getWidth());
                englishEditText.setBackgroundColor(Color.LTGRAY);

                EditText vietnameseEditText = new EditText(this);
                vietnameseEditText.setText(vietnameseListData.get(i));
                vietnameseEditText.setLayoutParams(params);
                vietnameseEditText.setMaxWidth(vietnameseEditText.getWidth());
                vietnameseEditText.setBackgroundColor(Color.LTGRAY);

                tr.addView(englishEditText);
                tr.addView(vietnameseEditText);
                tableLayout.addView(tr);
            }
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
