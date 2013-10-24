var serverBaseURL = 'https://secure.proximusmobility.com/ProximusTomorrow-war/api/';
var map_base_url = 'http://maps.googleapis.com/maps/api/staticmap';

var createStaticMap = function(offer, options) {

	if (typeof (offer) === "undefined") {
		offer = retrieveActiveOffer();
	}
	if ((typeof options === "undefined")) {
		options = {
			'width' : 640,
			'height' : 300,
			'zoom' : 17
		};
	}
	var markers = "&markers=color:63982c%7C" + offer.latitude + ","
			+ offer.longitude;

	var url = map_base_url + "?";
	url += "center=" + offer.latitude + "," + offer.longitude;
	if ((typeof options.width !== "undefined")
			&& (typeof options.height !== "undefined")) {
		url += "&size=" + options.width + "x" + options.height;
	} else {
		url += "&size=320x320";
	}
	if (typeof options.zoom !== "undefined") {
		url += "&zoom=" + options.zoom;
	} else {
		url += "&zoom=15";
	}
	url += "&sensor=false"
	url += markers;

	$("#static_map").attr('src', url);
	$('#static_map').css('cursor', 'pointer');
	$("#static_map").click(function() {
		if (navigator.userAgent.toLowerCase().indexOf("android") != -1) {
			getMapChooser();
		} else {
			var url = 'http://maps.google.com/maps?';
			url += "q=" + offer.latitude + "+" + offer.longitude;
			url += '&z=18';
			loadExternalUrl(url);
		}
	});

	$(".offer-bottom-list").listview('refresh');

	return url;

}

function getCategoriesforAjax() {
	var categories = localStorage.categories;
	if (categories == null) {
		return "";
	}
	categories = JSON.parse(categories);
	var tempArray = new Array();
	$.each(categories, function(key, value) {
		if (value == true) {
			tempArray.push(key);
		}
	});
	var result = tempArray.join(',');
	return result;

}

function getOfferList(getOfferSuccessMethod, getOfferFailedMethod) {
	$.mobile.utils.showWaitBox("a", "Loading");
	var the_data = {
		username : "api@valutext.com",
		token : "B542676572F03957891A606E9E4E6A23",
		latitude : (window.localStorage.use_my_location == 'true') ? window.latitude
				: "",
		longitude : (window.localStorage.use_my_location == 'true') ? window.longitude
				: "",
		zipcode : window.localStorage.zipcode || "",
		maxDistance : window.maxDistance,
		mac : window.macAddress,
		msisdn : window.msisdn,
		uuid : getDeviceUUID(),
		category : getCategoriesforAjax(),
		locale : (window.locale) ? window.locale : "en"
	}

	$.ajax({
		type : 'POST',
		contentType : "application/x-www-form-urlencoded; charset=utf-8",
		url : serverBaseURL + 'closestpropertyoffers',
		dataType : 'json',
		data : the_data,
		success : getOfferSuccessMethod,
		error : getOfferFailedMethod
	}); // end ajax
}

function onOfferListSuccess(data) {

	var list = JSONToOfferList(data);
	$('#offerList').empty();

	for (i in list) {
		var liHtml = OfferLiTemplate(list[i]);
		$offerLi = $(liHtml);
		$offerLi.bind("click", {
			offer : list[i]
		}, offerClickHandler);
		$('#offerList').append($offerLi);

	}
	if (list.length == 0) {
		$('#offerList').append(
				"<h1>No Offers Found within: " + window.maxDistance
						+ " miles</h1>");
	}
	$('#offerList').listview('refresh');
	$.mobile.utils.hideWaitBox();

}

function onOfferListFailure(data) {
	alert("You may have an invalid internet connection!");
	$('#offerList').listview('refresh');
	$.mobile.utils.hideWaitBox();

}

function JSONToOfferList(response) {
	var list = new Array();
	var appOffers = response.appOffers;
	for (i in appOffers) {
		var newOffer = appOffers[i];
		var text = newOffer.cleanOfferText;
		text = text.replace("ValuText: ", "");
		text = text.replace("MORE OFFERS AT: http://vtext.me", "");
		newOffer.cleanOfferText = text;
		if (newOffer.offerImage === null) {
			newOffer.offerImage = "./img/icon.png";

		}
		list.push(newOffer);
	}
	return list;
}

function storeActiveOffer(offer) {
	if (typeof (Storage) !== "undefined") {
		localStorage.activeOffer = JSON.stringify(offer);
	} else {
		console.log("Store: No storage support!");
	}
}

function retrieveActiveOffer() {
	if (typeof (Storage) !== "undefined") {
		window.activeOffer = JSON.parse(localStorage.activeOffer);
		return window.activeOffer;
	} else {
		console.log("retrieve: No storage support!");
	}
}

function offerClickHandler(event) {
	var currentOffer = event.data.offer;
	window.activeOffer = currentOffer;
	storeActiveOffer(currentOffer);
}

function renderMap(offer) {
	if (typeof (offer) === "undefined") {
		offer = retrieveActiveOffer();
	}
	var latlng = new google.maps.LatLng(offer.latitude, offer.longitude);
	// alert("Setting center to lat:" + latlng);
	var options = {
		'center' : latlng,
		'zoom' : 17
	};
	$('#map_canvas').gmap('clear', 'markers');
	$('#map_canvas').gmap('get', 'map').setOptions(options);
	$('#map_canvas').gmap('addMarker', {
		'position' : latlng,
		'bounds' : false
	});

}

/**
 * Offer Listview Template
 * 
 * @param offer
 * @returns {String}
 */

function OfferLiTemplate(offer) {

	var liTemplate = "<li data-corners=\"false\" data-shadow=\"false\" data-iconshadow=\"true\">";
	liTemplate += "<a href=\"#offer\" data-transition=\"slide\" id=\""
			+ offer.offer_id + "\">";
	liTemplate += "<img src=\"" + offer.offerImage
			+ "\" class=\"ui-li-thumb\"/>";

	liTemplate += "<h2>" + offer.retailerName + "</h2>";
	liTemplate += "<p>" + offer.cleanOfferText + "</p>";
	liTemplate += "<p class='ui-li-aside'><b>" + offer.distance.toFixed(2)
			+ "</b> miles</p>";
	liTemplate += "<span class=\"ui-icon ui-icon-arrow-r ui-icon-shadow\"></span>";
	liTemplate += "</a>";
	liTemplate += "</li>";
	return liTemplate;
}

function buildOfferPageTemplate(pageId, offer) {
	var d = new Date();
	var n = d.toLocaleDateString();
	$(".offer-bottom-list").empty();
	$.each(offer, function(key, value) {
        console.log(key + ': ' + value);
		var id = ".offer-" + key;
		if (typeof (value) === "object") {
           if (value !== null) {
           
           var twocolumns = new Array();
           var onecolumn = new Array();
           console.log('value: ' + value);
           $.each(value, function(k, category) {
                  console.log(k + 'category: ' + category);
                  if (typeof (category.name) !== 'undefined' && category.name && category.name !== '') {
                        //alert('twocolumns: ' + twocolumns);
                        if (category.name.length > 23) {
                            onecolumn.push(''+category.name);
                        }
                        else {
                            //console.log(k + 'twocolumns.push: ' + category.name);
                            //alert('Adding to two columns');
                            twocolumns.push(''+category.name);
                        }
                  }
			});
           console.log('building table');
           var table = "<tr>";
           var i = 0;
           for (data in twocolumns) {
                table += "<td><b>" + twocolumns[data] + "</b></td>";
                i++;
                if (i % 2 === 0) {
                    table += "</tr><tr>";
                }
           }
           if (twocolumns.length % 2 === 1) {
                table += "</tr>";
           }
           for (data in onecolumn) {
                table += "<tr><td colspan='2'><b>" + onecolumn[data] + "</b></td></tr>";
           }
           console.log('categoriesHTML: ' + table);
            $("#categoryTable").append(table);
           }
           else {
                console.log('no categories');
           }

		} else {
			if (key === "expirationDate") {
				d = new Date(value);
				n = d.toLocaleDateString();
				$(id).text("Expires: " + n);
			} else if (key === "offerImage") {
				if (value !== "img/icon.png") {
					$(id).attr('src', value);
					$(id).attr('alt', offer.retailerName);
					$(id).attr('title', offer.retailerName);
				}
			} else {
				$(id).text(value);
			}
		}
	});
    // Set passbook Download URL
	var passbookUrl = 'https://secure.proximusmobility.com/ProximusTomorrow-war/api/v1/retrievePass/'
    + offer.offerId.replace("-", "/");
	$("#passbookDownloadButton").attr("href", passbookUrl);
    
	$(".offer-bottom-list").listview('refresh');
}

function removeCategories() {
    $("#categoryTable").empty();
}




