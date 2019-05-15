package com.example.lzmfour;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DetailDialog extends Dialog implements  View.OnClickListener{
    private TextView textView;
    private Button button;
    private  String msg;
    public DetailDialog(Activity a, String message) {
        super(a);
        msg=message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_dialog);
        button=(Button) findViewById(R.id.btn_ok);
        textView=(TextView)findViewById(R.id.tv_msg);
        button.setOnClickListener(this);
        textView.setText(msg);
    }

    @Override
    public void onClick(View view) {
      switch (view.getId()) {
          case R.id.btn_ok:
              dismiss();
              break;
              default:
                  break;
      }
    }
}
