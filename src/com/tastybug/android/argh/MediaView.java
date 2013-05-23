package com.tastybug.android.argh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tastybug.android.test.R;

public class MediaView extends ListView {
	
	static final String[] COUNTRIES = new String[] {
	    "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
	    "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
	    "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
	    "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
	    "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
	    "Bosnia and Herzegovina", "Botswana"};
	
    
    public MediaView (Context context) {
    	super(context);
		setAdapter(new MyCustomAdapter(context, R.layout.row, COUNTRIES));
		
		setTextFilterEnabled(true);
		setFastScrollEnabled(false);
		setSmoothScrollbarEnabled(true);
		setAnimationCacheEnabled(false);
		setDividerHeight(0);
    }

//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		String selection = l.getItemAtPosition(position).toString();
//		Toast.makeText(this, selection, Toast.LENGTH_LONG).show();
//	}
    
    public class MyCustomAdapter extends ArrayAdapter<String> {
    	
    	Context context;
    	
		public MyCustomAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.country);
			label.setText(COUNTRIES[position]);
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			ImageView icon2 = (ImageView) row.findViewById(R.id.icon2);

			if (COUNTRIES[position] == "Algeria") {
				icon.setImageResource(R.drawable.icon);
				icon2.setImageResource(R.drawable.icon);
			} else {
				icon.setImageResource(R.drawable.icongray);
				icon2.setImageResource(R.drawable.icongray);
			}

			return row;
		}
	}
}