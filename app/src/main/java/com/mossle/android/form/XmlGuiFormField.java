/*
 * XmlGui application.
 * Written by Frank Ableson for IBM Developerworks
 * June 2010
 * Use the code as you wish -- no warranty of fitness, etc, etc.
 */
package com.mossle.android.form;

// class to handle each individual form
public class XmlGuiFormField {
    String name;
    String label;
    String type;
    boolean required;
    String options;
    String value;
    public Object obj; // holds the ui implementation , i.e. the EditText for example
	boolean readOnly;

    // getters & setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Field Name: " + this.name + "\n");
        sb.append("Field Label: " + this.label + "\n");
        sb.append("Field Type: " + this.type + "\n");
        sb.append("Required? : " + this.required + "\n");
        sb.append("Options : " + this.options + "\n");
        sb.append("Value : " + (String) this.getData() + "\n");

        return sb.toString();
    }

    public String getFormattedResult() {
        return this.name + "= [" + (String) this.getData() + "]";
    }

    public Object getData() {
        if (type.equals("text") || type.equals("numeric")) {
            if (obj != null) {
                XmlGuiEditBox b = (XmlGuiEditBox) obj;

                return b.getValue();
            }
        }

        if (type.equals("choice")) {
            if (obj != null) {
                XmlGuiPickOne po = (XmlGuiPickOne) obj;

                return po.getValue();
            }
        }

		if (type.equals("datepicker")) {
            if (obj != null) {
                XmlGuiDatePicker b = (XmlGuiDatePicker) obj;

                return b.getValue();
            }
		}

        // todo, add other UI elements here
        return null;
    }

	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
}
