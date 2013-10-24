var serverBaseURL = 'https://secure.proximusmobility.com/ProximusTomorrow-war/api/';

function getCategories(getCategorySuccessMethod, getCategoryFailedMethod) {
	var lastCheck = localStorage.getItem("lastCheck");
	if (lastCheck != null) {
		lastCheck = JSON.parse(lastCheck);
	}
	var now = new Date().getTime();
	if (lastCheck == null || (now - lastCheck.timestamp) > 86400000
			|| typeof (window.categoryList) == "undefined") {
		lastCheck = {
			timestamp : new Date().getTime()
		};
		localStorage.setItem("lastCheck", JSON.stringify(lastCheck));
		ajaxGetCategories(getCategorySuccessMethod, getCategoryFailedMethod);
	} else {
		loadUserPreferences();
	}
}

function ajaxGetCategories(getCategorySuccessMethod, getCategoryFailedMethod) {
	$.ajax({
		type : 'POST',
		contentType : "application/x-www-form-urlencoded; charset=utf-8",
		url : serverBaseURL + 'categorylist',
		dataType : 'json',
		data : {
			username : "api@valutext.com",
			token : "825C8CDCB96D8C30422401B800C5A648",
			locale : window.locale
		},
		success : getCategorySuccessMethod,
		error : getCategoryFailedMethod
	}); // end ajax
}

function categoryRetrievalSuccess(data) {
	var status = data.status;
	var status_message = data.status_message;
	var categories = data.category;
	window.categoryList = new Object();
	window.categoryList.count = 0;
	window.categoryList.list = Array();

	$.each(categories, function(key, val) {
		var id = val.id;
		var name = val.name;
		var webSafeName = val.webSafeName;

		// store it by key
		window.categoryList[webSafeName] = {
			"name" : name,
			"id" : id,
			"webSafeName" : webSafeName
		};
		// and also store in a list so we can iterate
		window.categoryList.list.push(window.categoryList[webSafeName]);
	});
	buildAndReplaceCategoryList();
	loadUserPreferences();
}

/*
 * Because we are building two columns, we build an even side and an odd side At
 * the end we combine them into HTML and replace the one we have
 */
function buildAndReplaceCategoryList() {
	var categoryHtmlEvenSide = "";
	var categoryHtmlOddSide = "";

	$
			.each(
					window.categoryList.list,
					function(key, val) {
						// For now, one column, Two did not fit
						var isEven = true; // ( (key % 2) == 0 );

						if (isEven == true) {
							categoryHtmlEvenSide += '<label><input data-mini type="checkbox" name="category" value="'
									+ val.name
									+ '" id="'
									+ val.webSafeName
									+ '"/>' + val.name + '</label>';

						} else {
							categoryHtmlOddSide += '<label><input data-mini type="checkbox" name="category" value="'
									+ val.name
									+ '" id="checkCategory'
									+ val.id
									+ '" checked />' + val.name + '</label>';
						}
					});

	$("#settingsCategoryBlock-a").empty();
	$(categoryHtmlEvenSide).appendTo("#settingsCategoryBlock-a").trigger(
			"create");
}

function categoryRetrievalFailed() {
	alert("Failed in Retrieving Categories, you may have an internet connection issue!");
}