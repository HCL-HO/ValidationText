package com.example.ericho.validationtext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

public class ValidationText extends RelativeLayout {

    public static final int DEFAULT_BTN_SIZE = 28;
    public static final int DEFAULT_TXT_SIZE = 49;
    public static final int NUMBER = 2;
    public static final int PASSWORD = 128;

    private Integer BUTTONSIZE;
    private Integer TEXTSIZE;
    private Integer HINTSIZE;
    private String HINT;
    private Integer INPUTTYPE;
    private Integer IMEOPTION;
    private Integer CURSOR = R.drawable.textcursor;
    Context context;
    EditText editText;
    ImageView deleteBtn;

    private ErrorMsg errMsgView;
    private STATE state = STATE.ERROR_OFF;

    public ValidationText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialAttr(attrs);
        initializeViews(context);
    }

    private void initialAttr(AttributeSet set) {
        if (set == null) {
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(set, R.styleable.ValidationEditText);
        BUTTONSIZE = typedArray.getInt(R.styleable.ValidationEditText_deleteIconSize, DEFAULT_BTN_SIZE);
        TEXTSIZE = typedArray.getDimensionPixelSize(R.styleable.ValidationEditText_edittextSize, DEFAULT_TXT_SIZE);
        HINT = typedArray.getString(R.styleable.ValidationEditText_hint);
        INPUTTYPE = typedArray.getInt(R.styleable.ValidationEditText_inputType, InputType.TYPE_CLASS_TEXT);
        IMEOPTION = typedArray.getInt(R.styleable.ValidationEditText_imeOption, EditorInfo.IME_ACTION_NONE);
        CURSOR = typedArray.getInt(R.styleable.ValidationEditText_cursorDrawable, CURSOR);
        typedArray.recycle();
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.myview_validation_edittext, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        editText = (EditText) findViewById(R.id.edittext);
        deleteBtn = (ImageView) findViewById(R.id.deletebutton);
        setCursorDrawable(editText, CURSOR);

        if (BUTTONSIZE != null) {
            deleteBtn.getLayoutParams().width = DisplayUtil.getPxByDp(context, BUTTONSIZE);
            deleteBtn.getLayoutParams().height = DisplayUtil.getPxByDp(context, BUTTONSIZE);
            deleteBtn.requestLayout();
        }
        if (TEXTSIZE != null) {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXTSIZE);
            editText.requestLayout();
        }

        if (HINT != null) {
            editText.setHint(HINT);
        }

        if (IMEOPTION != null) {
            editText.setImeOptions(IMEOPTION);
        }

        editText.setSingleLine(true);
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        if (INPUTTYPE != null) {
            if (INPUTTYPE == PASSWORD) {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                editText.setRawInputType(INPUTTYPE);
            }
            if (INPUTTYPE == NUMBER) {
                editText.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
            }
        }

        setOnDeleteClickListener(onDeleteClickListener);
    }

    public void setOnDeleteClickListener(final OnDeleteClickListener onClickListener) {
        this.onDeleteClickListener = new OnDeleteClickListener() {
            @Override
            public void onClick() {
                defaultDeleteClick();
                onClickListener.onClick();
            }
        };
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteClickListener.onClick();
            }
        });
    }

    public void setInputType(int i) {
        editText.setInputType(i);
    }

    public void setHint(String s) {
        editText.setHint(s);
    }

    private void setCursorDrawable(EditText editText, int drawableId) {
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, drawableId);
        } catch (final Throwable ignored) {
        }
    }

    public Editable getText() {
        return editText.getText();
    }

    public EditText getEditText() {
        return editText;
    }

    //Actions

    public void onError() {
        if (!isErrorOn()) {
            editText.addTextChangedListener(onAmendError);
            editText.setBackground(getResources().getDrawable(R.drawable.background_rounded_corner_dark_red_border));
            deleteBtn.setVisibility(VISIBLE);
            setState(STATE.ERROR_ON);
        } else {
            Log.d(getClass().getName(), "Invalid call: onError");
        }
    }

    public void onErrorCancel() {
        if (isErrorOn()) {
            editText.removeTextChangedListener(onAmendError);
            editText.setBackground(getResources().getDrawable(R.drawable.background_rounded_corner_dark));
            deleteBtn.setVisibility(GONE);
            setState(STATE.ERROR_OFF);
        } else {
            Log.d(getClass().getName(), "Invalid call: onErrorCancel");
        }
    }

    public void setErrorMsg(ErrorMsg errorMsg) {
        this.errMsgView = errorMsg;
    }

    public interface OnDeleteClickListener {
        void onClick();
    }

    private TextWatcher onAmendError = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            hideErrorText();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private OnDeleteClickListener onDeleteClickListener = new OnDeleteClickListener() {
        @Override
        public void onClick() {
            defaultDeleteClick();
        }
    };

    private void defaultDeleteClick() {
        hideErrorText();
        editText.setText("");
    }

    private void hideErrorText() {
        if (errMsgView != null) {
            errMsgView.hideError();
        } else {
            onErrorCancel();
        }
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