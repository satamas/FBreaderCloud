package ru.spbau.atamas.FBReaderCloud;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileList extends ArrayAdapter<String>{
		private final Activity context;
		private final String[] titles;
		private final Integer[] imageId;
		
		public FileList(Activity context, String[] titles, Integer[] imageId) {
			super(context, R.layout.file_list, titles);
			this.context = context;
			this.titles = titles;
			this.imageId = imageId;
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.file_list, null, true);
			TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
			txtTitle.setText(titles[position]);
			imageView.setImageResource(imageId[position]);
			return rowView;
		}
}
