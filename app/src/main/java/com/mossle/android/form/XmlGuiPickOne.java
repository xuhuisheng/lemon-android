/*
 * XmlGui application.
 * Written by Frank Ableson for IBM Developerworks
 * June 2010
 * Use the code as you wish -- no warranty of fitness, etc, etc.
 */
package com.mossle.android.form;

import android.content.Context;

import android.util.AttributeSet;

import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class XmlGuiPickOne extends LinearLayout {
    String tag = XmlGuiPickOne.class.getName();
    TextView label;
    ArrayAdapter<String> aa;
    Spinner spinner;

    public XmlGuiPickOne(Context context, String labelText, String options,
            String value) {
        super(context);
        label = new TextView(context);
        label.setText(labelText);
        spinner = new Spinner(context);

        String[] opts = options.split("\\|");
        int index = 0;

        if ((opts != null) && (value != null)) {
            for (int i = 0; i < opts.length; i++) {
                String opt = opts[i];

                if (value.equals(opt)) {
                    index = i;

                    break;
                }
            }
        }

        aa = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, opts);
        spinner.setAdapter(aa);
        spinner.setSelection(index);
        this.addView(label);
        this.addView(spinner);
    }

    public XmlGuiPickOne(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO Auto-generated constructor stub
    }

    public String getValue() {
        return (String) spinner.getSelectedItem().toString();
    }
}
