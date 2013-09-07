package com.jaje.cloudfullsync.fileexplorer;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("DefaultLocale")
public class FileListItem implements Comparable<FileListItem>, Parcelable {

	private String name, path, data, lastModificationDate, preview;

	public FileListItem() {
	}

	public FileListItem(String name, String path, String data,
			String lastModificationDate, String image) {
		this.name = name;
		this.path = path;
		this.data = data;
		this.lastModificationDate = lastModificationDate;
		preview = image;
	}

	public FileListItem(Parcel parcel) {
		name = parcel.readString();
		path = parcel.readString();
		data = parcel.readString();
		lastModificationDate = parcel.readString();
		preview = parcel.readString();
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getData() {
		return data;
	}

	public String getLastModificationDate() {
		return lastModificationDate;
	}

	public String getImage() {
		return preview;
	}

	public FileListItem set(String name, String path, String data,
			String lastModificationDate, String image) {
		this.name = name;
		this.path = path;
		this.data = data;
		this.lastModificationDate = lastModificationDate;
		preview = image;
		return this;
	}

	public FileListItem setName(String name) {
		this.name = name;
		return this;
	}

	public FileListItem setPath(String path) {
		this.path = path;
		return this;
	}

	public FileListItem setData(String data) {
		this.data = data;
		return this;
	}

	public FileListItem setLastModificationDate(String lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
		return this;
	}

	public FileListItem setImage(String string) {
		this.preview = string;
		return this;
	}

	@Override
	public int compareTo(FileListItem arg0) {
		if (name != null) {
			return name.toLowerCase().compareTo(arg0.getName().toLowerCase());
		} else {
			throw new IllegalArgumentException();
		}
	}

	// from Parcelable interface
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(path);
		dest.writeString(data);
		dest.writeString(lastModificationDate);
		dest.writeString(preview);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public FileListItem createFromParcel(Parcel in) {
			return new FileListItem(in);
		}

		public FileListItem[] newArray(int size) {
			return new FileListItem[size];
		}
	};

}
