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

public class XmlGuiEditBox extends LinearLayout {
    TextView label;
    EditText txtBox;

    public XmlGuiEditBox(Context context, String labelText, String initialText) {
        super(context);
        label = new TextView(context);
        label.setText(labelText);
        txtBox = new EditText(context);
        txtBox.setText(initialText);
        txtBox.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(label);
        this.addView(txtBox);
    }

    public XmlGuiEditBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO Auto-generated constructor stub
    }

    public void makeNumeric() {
        DigitsKeyListener dkl = new DigitsKeyListener(true, true);
        txtBox.setKeyListener(dkl);
    }

    public String getValue() {
        return txtBox.getText().toString();
    }

    public void setValue(String v) {
        txtBox.setText(v);
    }
}
