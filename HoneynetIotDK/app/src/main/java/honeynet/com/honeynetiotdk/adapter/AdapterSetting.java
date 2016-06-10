package honeynet.com.honeynetiotdk.adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import honeynet.com.honeynetiotdk.R;

/**
 * Created by NPhat on 1/3/2016.
 *
 */
public class AdapterSetting extends ArrayAdapter{
    private Activity context;
    private int resource;
    private ArrayList<String> arrayList;

    public AdapterSetting(Activity context, int resource, ArrayList objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.arrayList = objects;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Get layout
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(R.layout.item_list_setting,null);

        TextView textView = (TextView)convertView.findViewById(R.id.tv_item_setting);
        final EditText editText = (EditText)convertView.findViewById(R.id.ed_item_setting);

        textView.setText("F " + (position +1));
        editText.setText(arrayList.get(position).toString());


        //Save data after edittext changed

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                arrayList.set(position,s.toString());
            }
        });



        return convertView;
    }



}
