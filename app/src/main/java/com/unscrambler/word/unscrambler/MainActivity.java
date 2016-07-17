package com.unscrambler.word.unscrambler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.XmlRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button hitButton;
    EditText inputBox;
    String userOriginalWord = null;
    String userAlphaWord;

    String item;
    private TextView myText = null;
    private ArrayAdapter<String> listAdapter;
//    private TextView myText = (TextView)findViewById(R.id.textView);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#51c2f2")));
        ab.setLogo(R.mipmap.ic_launcher);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);

        try {
//            item = getItemFromXML(this);
            item = getItemsFromText(this);
//        } catch (XmlPullParserException e) {
        } catch (IOException e) {
        }
        final String[] items = item.split("\n");

        hitButton = (Button)findViewById(R.id.button);
        hitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                unscrambleIt(items);
            }
        });

        inputBox = (EditText)findViewById(R.id.userInput);
        inputBox.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            unscrambleIt(items);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });



//        setListAdapter(adapter);
//        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
//        RelativeLayout lView = new RelativeLayout(this);
//        LinearLayout lView = (LinearLayout)findViewById(R.id.linLayout);
//        myText = new TextView(this);
//        myText.setText(items[0]);
//        lView.addView(myText);
//        setContentView(lView);

    }

    public void unscrambleIt(String[] items){
        ArrayList<String> possibleWords = new ArrayList<String>();
        TextView heading = (TextView)findViewById(R.id.heading);
        inputBox = (EditText)findViewById(R.id.userInput);
        userOriginalWord = inputBox.getText().toString();
        boolean isEntryValid = validWord(userOriginalWord);
        if(!isEntryValid){
//                  possibleWords = null;
            heading.setText("");
            heading.setText("You can only enter letters");
//                    heading.setText("You entered: " + userOriginalWord);

        }else{
            if(userOriginalWord.length() < 3){
                heading.setText("");
                heading.setText("Please enter at least three letters");
            }else {
                userOriginalWord = userOriginalWord.toLowerCase();
                heading.setText("");
                userAlphaWord = alphaIt(userOriginalWord);
//              final ArrayList<String> possibleWords = new ArrayList<String>();
                for (int i = 0; i < items.length; i++) {
                    if (items[i].length() == userAlphaWord.length()) {
                        String fileAlphaWord = alphaIt(items[i]);
                        if (fileAlphaWord.equals(userAlphaWord)) {
                            possibleWords.add(items[i]);
                        }
                    }
                }
                if (possibleWords.size() == 0) {
                    heading.setText("Sorry, I can't unscramble this word");
                } else if (possibleWords.size() == 1) {
                    heading.setText(possibleWords.size() + " match found");
                } else {
                    heading.setText(possibleWords.size() + " matches found");
                }
            }
        }
        updateList(possibleWords);


    }

    public void updateList(ArrayList<String> possibleWords){
        ArrayAdapter<String> adapter;
        listAdapter = new CustomListAdapter(this, R.layout.custom_list,possibleWords);
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, possibleWords);
        adapter = new ArrayAdapter<String>(this, R.layout.custom_list, possibleWords);
        final ListView listView = (ListView)findViewById(R.id.listview);
//        listView.setAdapter(adapter);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Touch & Hold for definition", Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object listItem = listView.getItemAtPosition(i);
                String wordToDefine = listItem.toString();
                Intent definition = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.dictionary.com/browse/" + wordToDefine + "?s=t"));
                startActivity(definition);
                return false;
            }
        });


    }


    public String alphaIt(String word){
        char chars[] = word.toCharArray();
        Arrays.sort(chars);
        String alphaWord = new String(chars);
        return alphaWord;

    }

    public static boolean validWord(String word) {
        if(word.equals(null) || word.equals("")){
            return true;
        }
        return word.matches("[a-zA-Z]+");
    }

    public String getItemsFromText(Activity activity) throws IOException{
        StringBuilder buf=new StringBuilder();
        InputStream text=getAssets().open("items.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(text, "UTF-8"));
        String str;
        while ((str=in.readLine()) != null) {
            buf.append(str);
            buf.append("\n");
        }
        in.close();
        return buf.toString();

    }

    public void setListAdapter(ArrayAdapter<String> listAdapter) {
        this.listAdapter = listAdapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
//                Toast.makeText(getApplicationContext(), "Info icon is selected", Toast.LENGTH_SHORT).show();
                Intent info = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(info);
        }
//        return super.onOptionsItemSelected(item);
        return true;
    }



/*public String getItemFromXML(Activity activity) throws XmlPullParserException, IOException{
        StringBuffer stringBuffer = new StringBuffer();
        Resources res = activity.getResources();
        XmlResourceParser xpp = res.getXml(R.xml.items);
        xpp.next();

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT){
            if (eventType == XmlPullParser.START_TAG){
                if (xpp.getName().equals("Item")){
                    stringBuffer.append(xpp.getAttributeValue(null, "ItemNumber") + "\n");
                }
            }
            eventType = xpp.next();
        }
        return stringBuffer.toString();

    }*/

}

