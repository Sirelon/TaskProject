package com.khpi.diplom.taskproject;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created on 30/05/2017 23:34.
 */

class BaseActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    protected void showProgress(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    protected void hideProgress(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.hide();
        }
    }
}
