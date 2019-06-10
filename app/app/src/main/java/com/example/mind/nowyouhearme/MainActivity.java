package com.example.mind.nowyouhearme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button settingBtn, literatioBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingBtn = (Button) findViewById(R.id.settingbtn);
        literatioBtn = (Button) findViewById(R.id.literationbtn);

        settingBtn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View view){
                        Intent intent = new Intent(getApplicationContext(),setting.class);
                        startActivity(intent);//액티비티 띄우기
                    }
                }
        );
        literatioBtn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View view){
                        Intent intent = new Intent(getApplicationContext(),literation.class);
                        startActivity(intent);//액티비티 띄우기
                    }
                }
        );
    }
}
