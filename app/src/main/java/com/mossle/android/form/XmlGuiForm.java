/*
 * XmlGui application.
 * Written by Frank Ableson for IBM Developerworks
 * June 2010
 * Use the code as you wish -- no warranty of fitness, etc, etc.
 */
package com.mossle.android.form;

import java.net.URLEncoder;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

public class XmlGuiForm {
    private String formNumber;
    private String formName;
    private String submitTo;
	private boolean readOnly;
    public Vector<XmlGuiFormField> fields;

    public XmlGuiForm() {
        this.fields = new Vector<XmlGuiFormField>();
        formNumber = "";
        formName = "";
        submitTo = "loopback"; // ie, do nothing but display the results
		readOnly = false;
    }

    // getters & setters
    public String getFormNumber() {
        return formNumber;
    }

    public void setFormNumber(String formNumber) {
        this.formNumber = formNumber;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getSubmitTo() {
        return submitTo;
    }

    public void setSubmitTo(String submitTo) {
        this.submitTo = submitTo;
    }

    public Vector<XmlGuiFormField> getFields() {
        return fields;
    }

    public void setFields(Vector<XmlGuiFormField> fields) {
        this.fields = fields;
    }

	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("XmlGuiForm:\n");
        sb.append("Form Number: " + this.formNumber + "\n");
        sb.append("Form Name: " + this.formName + "\n");
        sb.append("Submit To: " + this.submitTo + "\n");
		sb.append("ReadOnly: " + this.readOnly + "\n");

        if (this.fields == null) {
            return sb.toString();
        }

        ListIterator<XmlGuiFormField> li = this.fields.listIterator();

        while (li.hasNext()) {
            sb.append(li.next().toString());
        }

        return sb.toString();
    }

    public String getFormattedResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("Results:\n");

        if (this.fields == null) {
            return sb.toString();
        }

        ListIterator<XmlGuiFormField> li = this.fields.listIterator();

        while (li.hasNext()) {
            sb.append(li.next().getFormattedResult() + "\n");
        }

        return sb.toString();
    }

    public String getFormEncodedData() {
        try {
            int i = 0;
            StringBuilder sb = new StringBuilder();
            sb.append("Results:\n");

            if (this.fields == null) {
                return sb.toString();
            }

            ListIterator<XmlGuiFormField> li = this.fields.listIterator();

            while (li.hasNext()) {
                if (i != 0) {
                    sb.append("&");
                }

                XmlGuiFormField thisField = li.next();
                sb.append(thisField.name + "=");

                String encstring = new String();
                URLEncoder.encode((String) thisField.getData(), encstring);
                sb.append(encstring);
            }

            return sb.toString();
        } catch (Exception e) {
            return "ErrorEncoding";
        }
    }

    public Map<String, Object> getMapData() {
        Map<String, Object> map = new HashMap<String, Object>();

        for (XmlGuiFormField item : this.fields) {
			if (item.isReadOnly()) {
				continue;
			}
            map.put(item.name, item.getData());
        }

        return map;
    }
}
