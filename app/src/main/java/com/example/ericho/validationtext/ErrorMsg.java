package com.example.ericho.validationtext;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

class ErrorMsg extends RelativeLayout {

    private TextView errMsg;
    private ValidationText validationEditText;

    private STATE state = STATE.ERROR_OFF;

    public ErrorMsg(Context context, AttributeSet attr) {
        super(context, attr);
        initializeViews(context);
    }

    public ErrorMsg(Context context) {
        super(context);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.myview_err_msg, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        errMsg = (TextView) findViewById(R.id.error_msg);
    }

    public void showError(String msg) {
        if (!isErrorOn()) {
            if (validationEditText != null) {
                validationEditText.onError();
            }
            setVisibility(VISIBLE);
            errMsg.setText(msg);
            setState(STATE.ERROR_ON);
        } else {
            Log.d(getClass().getName(), "Invalid call: showError");
        }
    }

    public void showError(int resId) {
        showError(getResources().getString(resId));
    }

    public void hideError() {
        if (isErrorOn()) {
            if (validationEditText != null) {
                validationEditText.onErrorCancel();
            }
            setVisibility(INVISIBLE);
            errMsg.setText("");
            setState(STATE.ERROR_OFF);
        } else {
            Log.d(getClass().getName(), "Invalid call: hideError");
        }
    }

    public void bindValidationText(ValidationText validationEditText) {
        this.validationEditText = validationEditText;
        validationEditText.setErrorMsg(this);
    }

    public boolean isErrorOn() {
        return state.equals(STATE.ERROR_ON);
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public enum STATE {
        ERROR_ON(0), ERROR_OFF(1);
        private final int state;

        STATE(int i) {
            this.state = i;
        }

        int getState() {
            return state;
        }
    }

}