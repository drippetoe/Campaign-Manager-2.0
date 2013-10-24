package com.valutext.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Response implements Parcelable {

	public static final String JSON_RESPONSE_STATUS = "status";
	private String status;
	public static final String JSON_RESPONSE_STATUS_MESSAGES = "status_message";
	private String statusMessage;
	public static final String JSON_RESPONSE_PROPERTIES_ARRAY = "propertyOffers";
	private List<Property> properties;
	private List<Category> categories;

	public Response(String status, String statusMessage,
			List<Property> properties, List<Category> categories) {
		super();
		this.status = status;
		this.statusMessage = statusMessage;
		this.properties = properties;
		this.categories = categories;
	}

	private Response(Parcel source) {
		readFromParcel(source);
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * @param statusMessage
	 *            the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	/**
	 * @return the properties
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	/**
	 * @return the categories
	 */
	public List<Category> getCategories() {
		return categories;
	}

	/**
	 * @param categories
	 *            the categories to set
	 */
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String res = this.statusMessage;

		for (Property p : this.properties) {
			res += "\n\t" + p.toString();
		}

		return res;
	}

	public static final Creator<Response> CREATOR = new Creator<Response>() {

		@Override
		public Response createFromParcel(Parcel source) {
			return new Response(source);
		}

		@Override
		public Response[] newArray(int size) {
			return new Response[size];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	public void readFromParcel(Parcel source) {
		this.status = source.readString();
		this.statusMessage = source.readString();
		this.properties = new ArrayList<Property>();
		this.categories = new ArrayList<Category>();
		source.readTypedList(properties, Property.CREATOR);
		source.readTypedList(categories, Category.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(this.status);
		parcel.writeString(this.statusMessage);
		parcel.writeTypedList(this.properties);
		parcel.writeTypedList(this.categories);
	}

}
