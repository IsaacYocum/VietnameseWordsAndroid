package com.example.vietnamesewordsandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);

        final EditText vietnameseEditText = findViewById(R.id.editText_Vietnamese);
        vietnameseEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = String.valueOf(vietnameseEditText.getText());
                Cursor data = databaseHelper.search("vietnamese", searchText);
                populateTable(data, "vietnamese", searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        final EditText englishEditText = findViewById(R.id.editText_English);
        englishEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = String.valueOf(englishEditText.getText());
                Cursor data = databaseHelper.search("english", searchText);
                populateTable(data, "english", searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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

    // Retrieves all translations from the database and displays them on the screen
    public void btnAllOnClick(View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Cursor data = databaseHelper.getAll();
                populateTable(data, null, null);
            }
        });
    }

    // Selects and displays a random row form the database
    public void btnRandomOnClick(View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Cursor data = databaseHelper.getRandom();
                populateTable(data, null, null);
            }
        });
    }

    // Adds the entries in the edit texts to the database
    // TODO: Prevent adding when edit texts are empty
    public void btnAddOnClick(View v) {
        EditText editText_Vietnamese = findViewById(R.id.editText_Vietnamese);
        EditText editText_English = findViewById(R.id.editText_English);

        String vWord = editText_Vietnamese.getText().toString();
        String eWord = editText_English.getText().toString();

        final long lastId = databaseHelper.add(vWord, eWord);

        if (lastId > -1) {
            toastMessage("Successfully added translation");
        } else {
            toastMessage("Failed to add translation");
            return;
        }

        editText_Vietnamese.getText().clear();
        editText_English.getText().clear();

        editText_Vietnamese.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText_Vietnamese, InputMethodManager.SHOW_IMPLICIT);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Cursor data = databaseHelper.getId(lastId);
                populateTable(data, null, null);
            }
        });
    }

    public void btnDeleteOnClick(View v) {
        final TableLayout tl = findViewById(R.id.tableLayout_TranslationsList);
        final ImageButton btnDelete = findViewById(R.id.btn_Delete);
        final ImageButton btnCancel = findViewById(R.id.imageButton_CancelDelete);

        // If the cancel delete button is not visible, make it visible
        // else, submit deletions
        if (btnCancel.getVisibility() == View.GONE) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnDelete.setImageResource(R.drawable.ic_check_black_24dp);
                    btnCancel.setVisibility(View.VISIBLE);

                    for (int i = 0; i < tl.getChildCount(); i++) {
                        TableRow tr = (TableRow)tl.getChildAt(i);
                        CheckBox cb = (CheckBox)tr.getChildAt(0);
                        cb.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else { // If the
            ArrayList<Integer> idList = new ArrayList<>();
            for (int i = 0; i < tl.getChildCount(); i++) {
                TableRow tr = (TableRow)tl.getChildAt(i);
                CheckBox cb = (CheckBox)tr.getChildAt(0);
                if (cb.isChecked()) {
                    idList.add(tr.getId());
                }
            }

            if (idList.size() != 0) {
                // For some reason if i try to delete the rows in the previous loop it fails. So I am
                // doing it down here instead.
                for (int i = 0; i < idList.size(); i++) {
                    databaseHelper.delete(idList.get(i));
                    TableRow tr = findViewById(idList.get(i));
                    tl.removeView(tr);
                }
                toastMessage("Successfully deleted!");
            } else {
                toastMessage("Nothing to delete.");
            }

            cancelDeleteOptions();
        }
    }

    public void btnCancelDeleteOnClick(View v) {
        cancelDeleteOptions();
    }

    public void cancelDeleteOptions() {
        TableLayout tl = findViewById(R.id.tableLayout_TranslationsList);
        ImageButton btnDelete = findViewById(R.id.btn_Delete);
        btnDelete.setImageResource(R.drawable.ic_delete_black_24dp);
        ImageButton btnCancel = findViewById(R.id.imageButton_CancelDelete);
        btnCancel.setVisibility(View.GONE);

        for (int i = 0; i < tl.getChildCount(); i++) {
            TableRow tr = (TableRow)tl.getChildAt(i);
            CheckBox cb = (CheckBox)tr.getChildAt(0);
            cb.setChecked(false);
            cb.setVisibility(View.GONE);
        }
    }

    // Hides the second row of buttons when clicked
    // This is here to save space on the screen
    public void btnOptionsOnClick(View v) {
        LinearLayout ll = findViewById(R.id.linearLayout_Options);

        if (ll.getVisibility() == View.VISIBLE) {
            ll.setVisibility(View.GONE);
        } else {
            ll.setVisibility(View.VISIBLE);
        }
    }

    // Displays passed in data on the screen
    private void populateTable(Cursor data, String language, String searchedText) {
        cancelDeleteOptions();
        if (data != null) {
            // Assign this table layout to the existing one in activity_main.xml
            TableLayout tableLayout = findViewById(R.id.tableLayout_TranslationsList);
            tableLayout.removeAllViews();

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT,1);
            params.setMargins(5, 2, 2, 2);

            // Create a table row for each translations returned from the database
            while (data.moveToNext()) {
                final TableRow tr = new TableRow(this);
                tr.setId(data.getInt(0));
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                final CheckBox checkBox = new CheckBox(this);
                checkBox.setVisibility(View.GONE);

                final EditText vietnameseEditText = new EditText(this);
                if (language != null && language.equals("vietnamese") && data.getString(1).toLowerCase().contains(searchedText.toLowerCase())) {
                    SpannableStringBuilder highlightedText = highlight(data.getString(1), searchedText);
                    vietnameseEditText.setText(highlightedText);
                } else {
                    vietnameseEditText.setText(data.getString(1));
                }
                vietnameseEditText.setLayoutParams(params);
                vietnameseEditText.setMaxWidth(vietnameseEditText.getWidth());
                vietnameseEditText.setBackgroundColor(getResources().getColor(R.color.aliceBlue));
                vietnameseEditText.setPadding(5, 0, 2, 0);

                final EditText englishEditText = new EditText(this);
                if (language != null && language.equals("english") && data.getString(2).toLowerCase().contains(searchedText.toLowerCase())) {
                    SpannableStringBuilder highlightedText = highlight(data.getString(2), searchedText);
                    englishEditText.setText(highlightedText);
                } else {
                    englishEditText.setText(data.getString(2));
                }
                englishEditText.setLayoutParams(params);
                englishEditText.setMaxWidth(englishEditText.getWidth());
                englishEditText.setBackgroundColor(getResources().getColor(R.color.aliceBlue));
                englishEditText.setPadding(8, 0, 0, 0);

                vietnameseEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String newText = vietnameseEditText.getText().toString();
                        databaseHelper.update(tr.getId(),"vWord", newText);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                englishEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String newText = englishEditText.getText().toString();
//                            toastMessage(tr.getId() + " e " + newText);
                        databaseHelper.update(tr.getId(),"eWord", newText);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                tr.addView(checkBox);
                tr.addView(vietnameseEditText);
                tr.addView(englishEditText);
                tableLayout.addView(tr);
            }
        }
    }

    // Handles highlighting the search results
    private SpannableStringBuilder highlight(String original, String textToHighlight) {
        SpannableStringBuilder sb = new SpannableStringBuilder(original);
        Pattern p = Pattern.compile(textToHighlight, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(original);

        while (m.find()){
            sb.setSpan(new BackgroundColorSpan(Color.parseColor("#add8e6")), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return sb;
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
