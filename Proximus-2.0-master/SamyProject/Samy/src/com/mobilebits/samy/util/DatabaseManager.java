package com.mobilebits.samy.util;

import java.sql.SQLException;
import java.util.List;

import net.aixum.webservice.CompanySummary;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DatabaseManager {

	private static final String TAG = DatabaseManager.class.getSimpleName();
	static private DatabaseManager instance;

	static public void init(Context ctx) {
		if (null == instance) {
			instance = new DatabaseManager(ctx);
		}
	}

	static public DatabaseManager getInstance() {
		return instance;
	}

	private DatabaseHelper helper;

	private DatabaseManager(Context ctx) {
		if (helper == null) {
			helper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
	    }	    
	}

	private DatabaseHelper getHelper() {
		return helper;
	}

	public void updateCompany(CompanySummary c) throws SQLException {

		getHelper().getCompanyDao().createOrUpdate(c);

	}
	


	public List<CompanySummary> getAllCompanies() {
		List<CompanySummary> wishLists = null;
		try {
			wishLists = getHelper().getCompanyDao().queryForAll();
		} catch (SQLException e) {
			Log.e(TAG, "Unable to get All CompanySummaries: " + e.getMessage());
		}
		return wishLists;
	}

	public void createCompany(CompanySummary c) {

		try {
			getHelper().getCompanyDao().create(c);
		} catch (SQLException e) {
			Log.e(TAG, "Unable to create CompanySummary: " + c.toString()
					+ ", " + e.getMessage());
		}
	}

}
