package com.example.max.myapplication;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity {

    public TextView out;
    public Button button;

    class clickLsitener implements View.OnClickListener{

        @Override
        public void onClick(View v){

            try {
                setConfVer();
                out.append("\n" + getConfVer()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        public void setConfVer() throws IOException {
            //Pattern pattern = Pattern.compile("[A-La-l]\\d{2}\\.\\w{3}\\.\\w+.(\\d+)");
            //Pattern pattern = Pattern.compile("\\w+\\.\\w+\\.\\w+.(\\d+)");
            Pattern pattern = Pattern.compile("(\\w+\\.){3}(\\d+)");
            String test = "B16.ABC.DEF.25";
            Matcher m = pattern.matcher(test);
            out.append("\nCorrect " + m.matches());

            String campainNumber = m.group(2);
            out.append("Campain number " + campainNumber);

            FileOutputStream confver = new FileOutputStream(getApplicationContext().getFilesDir().getAbsoluteFile() +
                                                            File.separator + "confver",false);
            confver.write(campainNumber.toString().getBytes());
        }

        public String getConfVer() throws IOException, ParseException {
            File path = getApplication().getFilesDir();
            File confver = new File(path.getAbsolutePath(),"confver");
            String campainNumber = "";
            if (!confver.exists())
                campainNumber = "0";
            else {
                FileInputStream confVerFos = new FileInputStream(path + File.separator + "confver");
                byte[] buffer = new byte[5];
                confVerFos.read(buffer);
                campainNumber = new String(buffer);
            }

            FileInputStream proc_ver = new FileInputStream("/proc/version");
            byte[] buffer = new byte[255];
            int res = proc_ver.read(buffer);
            String kernelVer = new String(buffer);
            out.append(kernelVer);

            Pattern p = Pattern.compile(".*(\\w{3}) \\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2} \\w{3} \\d{2}(\\d{2}).*",Pattern.MULTILINE);
            Matcher m = p.matcher(kernelVer);
            boolean b = m.find();
            out.append(""+b + "\n");
            String month = "";
            String year = "";
            if (b) {
                month = m.group(1);
                year = m.group(2);
            }
            String model = Build.MODEL;
            String man = Build.MANUFACTURER;

            out.append("Original manufacturer " + man + "\n");
            if (man.contains("Verizon"))
                man = "VZW";
            else
                man = man.substring(0,3);
            out.append("Processed manufacturer " + man + "\n");
            out.append("Original model " +model + "\n");
            model = model.replaceAll("[^\\w\\d]","");
            out.append("Processed model " + model + "\n");

            Date date = new SimpleDateFormat("MMM", Locale.US).parse(month);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int monthPosition =cal.get(Calendar.MONTH);
            char monthLetter = (char)('A' + monthPosition);
            out.append(monthLetter+"\n");
            String result = String.format("%s%s.%s.%s.%s",monthLetter,year,man,model,campainNumber);
            return result;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        out =(TextView) findViewById(R.id.Out);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new clickLsitener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
