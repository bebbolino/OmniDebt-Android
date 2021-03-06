package org.omnidebt.client.view.main;

import java.util.List;

import org.omnidebt.client.R;
import org.omnidebt.client.view.main.dashboard.DashboardFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class DebtAdapter extends ArrayAdapter<Debt>{
	Context context;
	int layoutResourceId;    
    List<Debt> data = null;
	
	public DebtAdapter(Context context, int layoutResourceId, List<Debt> data)
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		final int	pos			= position;
		View		row			= convertView;
		DebtHolder	holder		= null;
		Debt 		debt		= data.get(position);

		if(row==null)
		{
			LayoutInflater inflater=((Activity) context).getLayoutInflater();
			row=inflater.inflate(layoutResourceId, parent, false);
			
			holder=new DebtHolder();
			holder.date=(TextView) row.findViewById(R.id.date_debt);
			holder.person=(TextView) row.findViewById(R.id.person_debt);
			holder.value=(TextView) row.findViewById(R.id.value_debt);
			holder.pay=(ImageButton) row.findViewById(R.id.DebtPayment);
			
			row.setTag(holder);
		}
		else
		{
			holder=(DebtHolder) row.getTag();
		}
		
		holder.date.setText(debt.date);
		holder.person.setText(debt.name);
		holder.value.setText(String.format("%.2f", debt.value));
		holder.pay.setOnClickListener(new View.OnClickListener() {

			int savedPos = pos;

			@Override
			public void onClick(View v) {
				DashboardFragment db=(DashboardFragment) ((MainODActivity)v.getContext()).currentFragment;
				
				((MainODActivity)v.getContext()).goToPayDebt(db.theList.get(savedPos).name, db.theList.get(savedPos).value);

			}
		});

		if(debt.owed)
			holder.value.setTextColor(Color.parseColor("#ff0000"));
		else
			holder.value.setTextColor(Color.parseColor("#00ff00"));

		Log.d("debt", debt.name + " " + ( (Boolean) debt.closed ).toString());

		if(!debt.owed || debt.closed)
			( (ImageButton) row.findViewById(R.id.DebtPayment) ).setVisibility(View.INVISIBLE);
		
		return row;
	}
	
	public static class DebtHolder
	{
		public TextView date;
		public TextView person;
		public TextView value;
		public ImageButton pay;
	}
}
