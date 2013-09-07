package com.jaje.cloudfullsync.binding;

import java.util.ArrayList;
import java.util.List;

import com.jaje.cloudfullsync.CloudDrive;

public class Bindings {

	public List<Binding> bindingsList;

	public Bindings(List<Binding> list) {
		bindingsList = list;
	}

	public Bindings() {
		bindingsList = new ArrayList<Binding>(10);
	}

	public void add(Binding bind) {
		bindingsList.add(bind);
	}

	public void add(CloudDrive source, String localPath, String virtualPath,
			SyncDirect direct, boolean active, String frequency) {
		add(new Binding(source, localPath, virtualPath, direct, active,
				frequency));
	}

	public int getIndexOf(Binding bind) {
		return bindingsList.indexOf(bind);
	}

	public void removeBinding(Binding binding) {
		bindingsList.remove(binding);
	}

	// public Bindings(Parcel in) {
	// bindingsList = new ArrayList<Binding>(10);
	// in.readTypedList(bindingsList, null);
	// }
	//
	// @Override
	// public int describeContents() {
	// return 0;
	// }
	//
	// @Override
	// public void writeToParcel(Parcel dest, int flags) {
	// dest.writeTypedList(bindingsList);
	// }
	//
	// public static final Parcelable.Creator<Bindings> CREATOR = new
	// Parcelable.Creator<Bindings>() {
	// public Bindings createFromParcel(Parcel in) {
	// return new Bindings(in);
	// }
	//
	// public Bindings[] newArray(int size) {
	// return new Bindings[size];
	// }
	// };

}
