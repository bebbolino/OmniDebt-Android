package org.omnidebt.client.view.main;

import java.util.List;

import org.omnidebt.client.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContactAdapter extends ArrayAdapter<Contact> {
	
	Context				cContext	= null;
	int					iLayout		= 0;
	List<Contact>		lcData		= null;
	SparseBooleanArray	baSelected	= null;

	public ContactAdapter(Context context, int resource, List<Contact> objects, SparseBooleanArray baArray) {
		super(context, resource, objects);
		cContext	= context;
		iLayout		= resource;
		lcData		= objects;
		baSelected	= baArray;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View			rowView = convertView;
		ContactHolder	holder	= null;
		
		if(rowView == null)
		{
			LayoutInflater inflater=((Activity) cContext).getLayoutInflater();
			rowView=inflater.inflate(iLayout, parent, false);
			
			holder = new ContactHolder();
			holder.tvName		=(TextView)	rowView.findViewById(R.id.contact_name);
			holder.tvBalance	=(TextView)	rowView.findViewById(R.id.contact_balance);
			holder.tvPositive	=(TextView)	rowView.findViewById(R.id.contact_positive);
			holder.tvNegative	=(TextView)	rowView.findViewById(R.id.contact_negative);
			
			rowView.setTag(holder);
		}
		else
		{
			holder=(ContactHolder) rowView.getTag();
		}
		
		Contact contact = lcData.get(position);
		holder.tvName.setText(contact.name);
		holder.tvBalance.setText(String.format("%.2f", contact.balance));
		holder.tvPositive.setText(String.format("%.2f", contact.pos));
		holder.tvNegative.setText(String.format("%.2f", contact.neg));

		if(baSelected.get(position))
			rowView.setBackgroundColor(Color.parseColor("#424242"));
		else
			rowView.setBackgroundColor(Color.parseColor("#ffffff"));

		return rowView;
		
	}

	static class ContactHolder
	{
		TextView tvName;
		TextView tvBalance;
		TextView tvPositive;
		TextView tvNegative;
	}

}
