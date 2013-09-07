package com.jaje.cloudfullsync.binding;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.jaje.cloudfullsync.CloudDrive;

public class Binding implements Parcelable {

	public String localPath;
	public String cloudPath;
	public Drawable sourceIcon;
	public Drawable syncDirectIcon;

	private String frequency = "1";

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private final CloudDrive source;
	private final SyncDirect direct;
	private boolean active;

	public Binding(CloudDrive source, String localPath, String cloudPath,
			SyncDirect direct, boolean active, String frequency) {
		this.source = source;
		this.localPath = localPath;
		this.cloudPath = cloudPath;
		this.direct = direct;
		sourceIcon = source.getIcon();
		syncDirectIcon = direct.getIcon();
		this.active = active;
		this.frequency = frequency;
	}

	public Binding(Parcel in) {
		source = CloudDrive.valueOf(in.readString());
		localPath = in.readString();
		cloudPath = in.readString();
		direct = SyncDirect.valueOf(in.readString());
		sourceIcon = source.getIcon();
		syncDirectIcon = direct.getIcon();
	}

	public SyncDirect getDirect() {
		return direct;
	}

	public CloudDrive getSource() {
		return source;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(source.name());
		dest.writeString(localPath);
		dest.writeString(cloudPath);
		dest.writeString(direct.name());
	}

	public static final Parcelable.Creator<Binding> CREATOR = new Parcelable.Creator<Binding>() {
		@Override
		public Binding createFromParcel(Parcel in) {
			return new Binding(in);
		}

		@Override
		public Binding[] newArray(int size) {
			return new Binding[size];
		}
	};

	public boolean isDeactive() {
		return !active;
	}

	public boolean isActive() {
		return active;
	}

	public void swith() {
		active = !active;
	}

}
