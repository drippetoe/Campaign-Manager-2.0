package com.valutext.data;
/*
 * © 2013 Proximus Mobility LLC.
 */
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents an offer that is served in mobile devices
 * @author Gilberto Gaxiola
 */
public class MobileOffer implements Parcelable {

	
	public static final String JSON_MOBILE_OFFER = "webOffer";
	public static final String JSON_MOBILE_OFFER_ID = "id";
	private String id;
	public static final String JSON_MOBILE_OFFER_NAME = "name";
	private String name;
	public static final String JSON_MOBILE_OFFER_TEXT = "cleanOfferText";
	private String text;
	public static final String JSON_MOBILE_OFFER_PASSBOOK_BARCODE = "passbookBarcode";
	private String passbookBarcode;
	public static final String JSON_MOBILE_OFFER_PASSBOOK_HEADER = "passbookHeader";
	private String passbookHeader;
	public static final String JSON_MOBILE_OFFER_PASSBOOK_SUBHEADER = "passbookSubheader";
	private String passbookSubheader;
	
	
	/**
	 * @param id
	 * @param name
	 * @param text
	 * @param passbookBarcode
	 * @param passbookHeader
	 * @param passbookSubheader
	 */
	public MobileOffer(String id, String name, String text,
			String passbookBarcode, String passbookHeader,
			String passbookSubheader) {
		super();
		this.id = id;
		this.name = name;
		this.text = text;
		this.passbookBarcode = passbookBarcode;
		this.passbookHeader = passbookHeader;
		this.passbookSubheader = passbookSubheader;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the passbookBarcode
	 */
	public String getPassbookBarcode() {
		return passbookBarcode;
	}
	/**
	 * @param passbookBarcode the passbookBarcode to set
	 */
	public void setPassbookBarcode(String passbookBarcode) {
		this.passbookBarcode = passbookBarcode;
	}
	/**
	 * @return the passbookHeader
	 */
	public String getPassbookHeader() {
		return passbookHeader;
	}
	/**
	 * @param passbookHeader the passbookHeader to set
	 */
	public void setPassbookHeader(String passbookHeader) {
		this.passbookHeader = passbookHeader;
	}
	/**
	 * @return the passbookSubheader
	 */
	public String getPassbookSubheader() {
		return passbookSubheader;
	}
	/**
	 * @param passbookSubheader the passbookSubheader to set
	 */
	public void setPassbookSubheader(String passbookSubheader) {
		this.passbookSubheader = passbookSubheader;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/**
	 * Constructor used by the Parcelable interface
	 * @param source
	 */
	private MobileOffer(Parcel source) {
		readFromParcel(source);
	}
	
	
	public void readFromParcel(Parcel source) {
		this.id = source.readString();
		this.name = source.readString();
		this.text = source.readString();
		this.passbookBarcode = source.readString();
		this.passbookHeader = source.readString();
		this.passbookSubheader = source.readString();
	}
	
	

	
	
	public static final Creator<MobileOffer> CREATOR = new Creator<MobileOffer>() {

		@Override
		public MobileOffer createFromParcel(Parcel source) {
			return new MobileOffer(source);
		}

		@Override
		public MobileOffer[] newArray(int size) {
			return new MobileOffer[size];
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
		parcel.writeString(this.text);
		parcel.writeString(this.passbookBarcode);
		parcel.writeString(this.passbookHeader);
		parcel.writeString(this.passbookSubheader);
		
	}

	
}
