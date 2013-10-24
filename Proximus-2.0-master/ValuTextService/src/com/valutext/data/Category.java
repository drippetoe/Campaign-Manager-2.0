package com.valutext.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
	public static final String JSON_CATEGORY = "category";
	public static final String JSON_CATEGORY_ID = "id";
	private String id;
	public static final String JSON_CATEGORY_NAME = "name";
	private String name;
	public static final String JSON_CATEGORY_WEB_NAME = "webSafeName";
	private String webName;

	public Category(String id, String name, String webName) {
		super();
		this.id = id;
		this.name = name;
		this.webName = webName;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the webName
	 */
	public String getWebName() {
		return webName;
	}

	/**
	 * @param webName
	 *            the webName to set
	 */
	public void setWebName(String webName) {
		this.webName = webName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;

	}

	/**
	 * Constructor used by the Parcelable interface
	 * 
	 * @param source
	 */
	private Category(Parcel source) {
		readFromParcel(source);
	}

	public void readFromParcel(Parcel source) {
		this.id = source.readString();
		this.name = source.readString();
		this.webName = source.readString();
	}

	public static final Creator<Category> CREATOR = new Creator<Category>() {

		@Override
		public Category createFromParcel(Parcel source) {
			return new Category(source);
		}

		@Override
		public Category[] newArray(int size) {
			return new Category[size];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(this.id);
		parcel.writeString(this.name);
		parcel.writeString(this.webName);

	}
}
