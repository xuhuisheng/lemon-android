/*
 * XmlGui application.
 * Written by Frank Ableson for IBM Developerworks
 * June 2010
 * Use the code as you wish -- no warranty of fitness, etc, etc.
 */
package com.mossle.android.form;

import android.content.Context;

import android.text.method.DigitsKeyListener;

import android.util.AttributeSet;

import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.DatePicker;
import java.util.Calendar;  
import android.app.Activity;  
import android.app.DatePickerDialog;  
import android.app.Dialog;  
import android.app.DatePickerDialog.OnDateSetListener;  
import android.os.Bundle;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button;  
import android.widget.DatePicker;  
import android.widget.TextView;  
import com.mossle.android.activity.FormActivity;

public class XmlGuiDatePicker extends LinearLayout {
    TextView label;
    EditText txtBox;
	static final int DATE_DIALOG_ID = 0;

    public XmlGuiDatePicker(final Context context, String labelText, String initialText) {
        super(context);
        label = new TextView(context);
        label.setText(labelText);
        txtBox = new EditText(context);
        // txtBox.setText(initialText);
        txtBox.setCursorVisible(false); //设置输入框中的光标不可见  
        txtBox.setFocusable(false); //无焦点  
        txtBox.setFocusableInTouchMode(false); //触摸时也得不到焦点 
        txtBox.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(label);
        this.addView(txtBox);

		txtBox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				((FormActivity)context).setEditText(txtBox);
				((Activity)context).showDialog(DATE_DIALOG_ID);
			}
		});
    }

    public XmlGuiDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO Auto-generated constructor stub
    }

    // public void makeNumeric() {
    //    DigitsKeyListener dkl = new DigitsKeyListener(true, true);
    //    txtBox.setKeyListener(dkl);
    // }

    public String getValue() {
		return txtBox.getText().toString();
    }

    public void setValue(String v) {
        txtBox.setText(v);
    } 
}
