package com.bisa.health.ecg.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RawObject implements Parcelable {

	private int position;
	private int marker_type;
	private int event_type;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getMarker_type() {
		return marker_type;
	}

	public void setMarker_type(int marker_type) {
		this.marker_type = marker_type;
	}

	public int getEvent_type() {
		return event_type;
	}

	public void setEvent_type(int event_type) {
		this.event_type = event_type;
	}

	public RawObject(Parcel source) {
		position = source.readInt();
		marker_type = source.readInt();
		event_type = source.readInt();
	}

	public RawObject() {
		super();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(position);
		dest.writeInt(marker_type);
		dest.writeInt(event_type);
	}

	public static final Creator<RawObject> CREATOR = new Creator<RawObject>() {

		@Override
		public RawObject createFromParcel(Parcel source) {
			return new RawObject(source);
		}

		@Override
		public RawObject[] newArray(int size) {
			return new RawObject[size];
		}

	};

}
