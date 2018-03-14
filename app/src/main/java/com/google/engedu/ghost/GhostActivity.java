/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private static final String COMPUTER_WIN = "Computer Wins!";
    private static final String USER_WIN = "You Win!";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    FastDictionary obj;
    TextView txtWord, label;
    Button challenger,reset;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        txtWord = (TextView)findViewById(R.id.ghostText);
        label = (TextView) findViewById(R.id.gameStatus);
        challenger = (Button)findViewById(R.id.challenge);
        reset = (Button)findViewById(R.id.reset);
        try {
            obj = new FastDictionary(assetManager.open("words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        onStart(null);
        challenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = txtWord.getText().toString();
                challenger.setEnabled(false);
                if(word.length()>=dictionary.MIN_WORD_LENGTH && obj.isWord(word))
                {
                    label.setText(USER_WIN);
                    challenger.setEnabled(false);
                    return;
                }
                String new_letter = obj.getAnyWordStartingWith(txtWord.getText().toString());
                if(new_letter==null)
                    label.setText(USER_WIN);
                else
                {
                    txtWord.setText(obj.getAnyWordStartingWith(txtWord.getText().toString()));
                    label.setText(COMPUTER_WIN);
                }

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challenger.setEnabled(true);
                onStart(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        txtWord.setText("");
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {

        // Do computer turn stuff then make it the user's turn again
        String word = txtWord.getText().toString();
        int index = txtWord.getText().toString().length();
        if(word.length()>=4 && obj.isWord(word))
        {
            Log.e("getAnyWord1", obj.getAnyWordStartingWith(word));
            label.setText(COMPUTER_WIN);
            challenger.setEnabled(false);
            return;
        }
        //String new_letter = obj.getAnyWordStartingWith(word);
        if(obj.getAnyWordStartingWith(word)==null)
        {
//            Log.e("getAnyWord2", obj.getAnyWordStartingWith(word));
            label.setText(COMPUTER_WIN);
            challenger.setEnabled(false);
            return;
        }
        else
        {
            txtWord.setText(txtWord.getText().toString() + obj.getAnyWordStartingWith(word).charAt(index));
        }
        userTurn = true;
        label.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char letter = (char)event.getUnicodeChar();
        if((letter>='a'&&letter<='z')||(letter>='A'&&letter<='Z'))
        {
            txtWord.setText(txtWord.getText().toString()+letter);
            userTurn = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    computerTurn();
                }
            },1500);
            label.setText(COMPUTER_TURN);
            return true;
        }
        else
        {
            Toast.makeText(this, "Enter a valid character", Toast.LENGTH_SHORT).show();
            return super.onKeyUp(keyCode, event);
        }
    }
}
