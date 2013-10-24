package com.valutext.data;
/*
 * © 2013 Proximus Mobility LLC.
 */
import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the physical location where MobileOffers can be redeem 
 * @author Gilberto Gaxiola
 */
public class Property implements Parcelable {

	public static final String JSON_PROPERTY = "property";
	public static final String JSON_PROPERTY_ID = "id";
	private String id;
	public static final String JSON_PROPERTY_NAME = "name";
	private String name;
	public static final String JSON_PROPERTY_ADDRESS = "address";
	private String address;
	public static final String JSON_PROPERTY_CITY = "city";
	private String city;
	public static final String JSON_PROPERTY_STATE_PROVINCE = "stateProvince";
	private String stateProvince;
	public static final String JSON_PROPERTY_COUNTRY = "country";
	private String country;
	public static final String JSON_PROPERTY_ZIPCODE = "zipcode";
	private String zipcode;
	public static final String JSON_PROPERTY_DISTANCE = "distance";
	private String distance;
	public static final String JSON_PROPERTY_LOCATION = "geoPoint";
	private Location location;
	public static final String JSON_PROPERTY_OFFERS_ARRAY = "offers";
	private List<MobileOffer> offers;

	/**
	 * @param id
	 * @param name
	 * @param address
	 * @param city
	 * @param stateProvince
	 * @param country
	 * @param zipcode
	 * @param location
	 * @param offers
	 */
	public Property(String id, String name, String address, String city,
			String stateProvince, String country, String zipcode,
			String distance, Location location, List<MobileOffer> offers) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.city = city;
		this.stateProvince = stateProvince;
		this.country = country;
		this.zipcode = zipcode;
		this.distance = distance;
		this.location = location;
		this.offers = offers;
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
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the stateProvince
	 */
	public String getStateProvince() {
		return stateProvince;
	}

	/**
	 * @param stateProvince
	 *            the stateProvince to set
	 */
	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the zipcode
	 */
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * @param zipcode
	 *            the zipcode to set
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * @return the distance
	 */
	public String getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the offers
	 */
	public List<MobileOffer> getOffers() {
		return offers;
	}

	/**
	 * @param offers
	 *            the offers to set
	 */
	public void setOffers(List<MobileOffer> offers) {
		this.offers = offers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String res = this.name + " (" + this.location.getLatitude() + ","
				+ this.location.getLongitude() + ")";
		for (MobileOffer o : this.offers) {
			res += "\n\t\t" + o.toString();
		}
		return res;
	}

	/**
	 * Constructor used by the Parcelable interface
	 * 
	 * @param source
	 */
	private Property(Parcel source) {
		readFromParcel(source);
	}

	/**
	 * Initalize the Object from a Parcel
	 * 
	 * @param source
	 *            the Android Parcel Object
	 */
	public void readFromParcel(Parcel source) {

		this.id = source.readString();
		this.name = source.readString();
		this.address = source.readString();
		this.city = source.readString();
		this.stateProvince = source.readString();
		this.country = source.readString();
		this.zipcode = source.readString();
		this.location = source.readParcelable(Location.class.getClassLoader());
		// offers must be initialized to an empty list before using
		// .readTypedList();
		this.offers = new ArrayList<MobileOffer>();
		source.readTypedList(offers, MobileOffer.CREATOR);
	}

	public static final Creator<Property> CREATOR = new Creator<Property>() {

		@Override
		public Property createFromParcel(Parcel source) {
			return new Property(source);
		}

		@Override
		public Property[] newArray(int size) {
			return new Property[size];
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
		parcel.writeString(this.address);
		parcel.writeString(this.city);
		parcel.writeString(this.stateProvince);
		parcel.writeString(this.country);
		parcel.writeString(this.zipcode);
		parcel.writeParcelable(this.location, flags);
		parcel.writeTypedList(offers);
	}

}
