package com.jaje.cloudfullsync.binding;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaje.cloudfullsync.R;

public class BindListAdapter extends ArrayAdapter<Binding> {

	private Context c;
	private int resId;
	private Binding[] items;

	public BindListAdapter(Context context, int textViewResourceId,
			List<Binding> objects) {
		super(context, textViewResourceId, objects.toArray(new Binding[0]));

		c = context;
		resId = textViewResourceId;
		items = objects.toArray(new Binding[0]);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(resId, null);
		}

		final Binding o = items[position];
		if (o != null) {
			TextView localPathTextView = (TextView) v
					.findViewById(R.bindItemLayout.bind_local_path);
			TextView cloudPathTextView = (TextView) v
					.findViewById(R.bindItemLayout.bind_cloud_path);
			ImageView imageSource = (ImageView) v
					.findViewById(R.bindItemLayout.source_icon);
			ImageView imageDirection = (ImageView) v
					.findViewById(R.bindItemLayout.direct_sync_icon);

			localPathTextView.setText(o.localPath);
			cloudPathTextView.setText(o.cloudPath);
			imageSource.setImageDrawable(o.sourceIcon);
			imageDirection.setImageDrawable(o.syncDirectIcon);
		}
		return v;
	}

}
