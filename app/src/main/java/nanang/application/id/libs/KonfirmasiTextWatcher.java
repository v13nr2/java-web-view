package nanang.application.id.libs;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class KonfirmasiTextWatcher implements TextWatcher {
    
    private DecimalFormat df;
    private DecimalFormat dfnd;
    private boolean hasFractionalPart;

    private EditText[] et;
    private EditText jt;
    private CheckBox cb;

    public KonfirmasiTextWatcher(EditText[] et, EditText jt, CheckBox cb)
    {
    	DecimalFormat format = new DecimalFormat("#,###");
		DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		format.setDecimalFormatSymbols(symbols);
		
        df = format;
        //df.setDecimalSeparatorAlwaysShown(true);
        
        DecimalFormat format_ = new DecimalFormat("#,###");
		DecimalFormatSymbols symbols_ = format.getDecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		format.setDecimalFormatSymbols(symbols_);
		
        dfnd = format_;
        this.et = et;
        this.cb = cb;
        this.jt = jt;
        hasFractionalPart = false;
    }
    
    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";

    //@Override
    public void afterTextChanged(Editable s)
    {
        et[0].removeTextChangedListener(this);
        
        try {
            int inilen, endlen;
            inilen = et[0].getText().length(); //==0?1:et.getText().length();
            
            String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            v = v.length()==0?"0":v;
            Number n = df.parse(v);
            int cp = et[0].getSelectionStart();
            if (hasFractionalPart) {
                et[0].setText(df.format(n));
            } else {
                et[0].setText(dfnd.format(n));
            }
            endlen = et[0].getText().length();
            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= et[0].getText().length()) {
                et[0].setSelection(sel);
            } else {
                // place cursor at the end?
                et[0].setSelection(et[0].getText().length() - 1);
            }

            if(cb!=null && !cb.isChecked() && Integer.valueOf(v)>0) {
                cb.setChecked(true);
            }

            if(jt!=null) {
                int total = 0;
                for(EditText edt: et) {
                    String subttl = edt.getText().toString().replace(",", "").trim();
                    int nominal = Integer.parseInt(subttl.equalsIgnoreCase("")?"0":subttl);
                    total+=nominal;
                }

                jt.setText(df.format(total));
            }

        } catch (NumberFormatException nfe) {
            // do nothing?
        } catch (ParseException e) {
            // do nothing?
        }
        
        et[0].addTextChangedListener(this);



    }

    //@Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    //@Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator())))
        {
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
    }

}