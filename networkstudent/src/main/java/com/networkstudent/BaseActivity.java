package com.networkstudent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.networkstudent.utils.ReusableClass;

/**
 * Created by anirban on 4/10/16.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        String[] a = new String[]{"pear", "amleth", "dormitory", "tinsel", "dirty room", "hamlet", "listen", "silent"};

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (i != j) {
                    String value = a[i];
                    if (check(a[i].replaceAll("[^\\p{Alnum}]", "").toLowerCase(), a[j].replaceAll("[^\\p{Alnum}]", "").toLowerCase())) {
                        value = value + ", " + a[j];
                    }
                    if (!value.equalsIgnoreCase(a[i]))
                        System.out.println(value);
                }
            }
        }

    }

    public boolean check(String first, String second) {
        if (first.length() != second.length())
            return false;

        int value = 0;
        for (int i = 0; i < first.length(); i++) {
            value += ((int) first.charAt(i)) ^ 2;
            value -= ((int) second.charAt(i)) ^ 2;
        }
        return value == 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorite: {
                Intent i = new Intent(this, FavoritesActivity.class);
                startActivity(i);
                return true;
            }

            case R.id.action_logout: {
                ReusableClass.saveInPreference("session", "", this);
                finish();
                return true;
            }
//            case R.id.action_product_archives: {
//                Intent i = new Intent(this, ArchivedProductListActivity.class);
//                startActivity(i);
//                return true;
//            }
            case R.id.action_my_order: {
//                Intent i = new Intent(this, OrderedProductListActivity.class);
//                startActivity(i);
//                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
