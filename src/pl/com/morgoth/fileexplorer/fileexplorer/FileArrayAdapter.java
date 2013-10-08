package pl.com.morgoth.fileexplorer.fileexplorer;

import java.util.List;

import pl.com.morgoth.fileexplorer.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileArrayAdapter extends ArrayAdapter<FileListItem> {

	private static final String DRAWABLE = "drawable/";
	private final Context c;
	private final int id;
	private List<FileListItem> items;

	public FileArrayAdapter(Context context, int textViewResourceId,
			List<FileListItem> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items = objects;
	}

	public FileArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		c = context;
		id = textViewResourceId;
	}

	@Override
	public FileListItem getItem(int i) {
		return items.get(i);
	}

	public void setItemsList(List<FileListItem> items) {
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(id, null);
		}

		/* create a new view of my layout and inflate it in the row */
		// convertView = ( RelativeLayout ) inflater.inflate( resource, null );

		final FileListItem o = items.get(position);
		if (o != null) {
			TextView t1 = (TextView) v.findViewById(R.id.fileName);
			TextView t2 = (TextView) v.findViewById(R.id.count);
			TextView t3 = (TextView) v.findViewById(R.id.TextViewDate);
			/* Take the ImageView from layout and set the city's image */
			ImageView imageCity = (ImageView) v.findViewById(R.id.preview);
			int imageResource = c.getResources()
					.getIdentifier(
							new StringBuilder(DRAWABLE).append(o.getImage())
									.toString(), null, c.getPackageName());
			Drawable image = c.getResources().getDrawable(imageResource);
			imageCity.setImageDrawable(image);

			if (t1 != null)
				t1.setText(o.getName());
			if (t2 != null)
				t2.setText(o.getData());
			if (t3 != null)
				t3.setText(o.getLastModificationDate());
		}
		return v;
	}
}