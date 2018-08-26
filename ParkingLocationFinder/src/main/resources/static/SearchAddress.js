/**
 * 
 */

$(document).ready(function() {
	$("#bookaspot").hide();
	$("#view").hide();
	$("#find-address").submit(function(event) {
		event.preventDefault();
		$("#search-result").empty();
		$("#registration-form").remove();
		var address = $('input[name=find-address]').val();
		var distance = $('input[name=search-distance]').val(); 
		
		var uri = "/customers?" + "address=" + address + "&radius=" + distance ;
		 
		console.log(uri);
		 
		 $.get(uri , function(data, status ) { 
			 // Handle no data here
			 
			 if (data.length == 0){
				 alert("No Parking available in the requested location");
				 $("#search-result").empty();
				 $("#find-id").val('');
			 } else {
			 var result = "<table class=\"table\">" +
		 		"<thead>" +
		 		"<tr>" +
		 		"<th> </th>" +
		 		"<th>First Name</th>" +
		 		"<th>Last Name</th>" +
		 		"<th>location Latitude</th>" +
		 		"<th>location Longitude</th>" +
		 		"<th>Address</th>" +
		 		"</tr>" +
		 		"</thead>";
	    			for(var i=0;i<data.length;i++){
	    				var item = data[i];
	    				result += "<tr>" +
	    						  "<td>"+"<input type=\"radio\" " + 
	    						  			"name=\"optradio\" " + 
	    						  			"value="+ item.custId +">" +
	    						  "</td>" +
	    						  "<td>"+item.firstName + "</td>" +
	    						  "<td>"+item.lastName + "</td>" +
	    						  "<td>"+item.locationLatitude + "</td>" +
	    						  "<td>"+item.locationLongitude + "</td>" +
	    						  "<td>"+item.address + "</td>" +
	    						  "</tr>"
	    			}
	    			 result += "</table>"
	    			
	    	         $("#search-result").append(result);	
	    				 
	    			 $("#bookaspot").show();
	    		}
	    			 
		 });
	
	});
	
	
	/*
	 * var item = data[i];
	 * var row = `<td> <input type="radio" value=${i} </td> 
	 *             <td>${item.firstName}</td>`
	 *             <td>
	 * 
	 */
	
	 $("#reserve").submit(function(event) { 
		 
		 var id ;
	
		 var radioButtons = document.getElementsByName("optradio");
		 
		 for (var x = 0; x < radioButtons.length; x ++) {
		      if (radioButtons[x].checked) {
		       id = radioButtons[x].value;
		        break;
		      }
		  }
		 
		 var request = {"CustId":id};
		 // process the form
	        $.ajax({
	        	headers: { 
	                'Accept': 'application/json',
	                'Content-Type': 'application/json' 
	            },
	            type        : 'PUT', // define the type of HTTP verb we want to use (POST for our form)
	            url         : '/reserve', // the url where we want to POST
	            data        : JSON.stringify(request), // our data object
	            dataType    : 'json', // what type of data do we expect back from the server
	            encode          : true
	        })
	            // using the done promise callback
	            .always(function(data) {

	                // log data to the console so we can see
	                console.log(data); 
	                if(data.status == 200) {
	                	alert("Successfully Booked");
	                	$("#view").show();
	                	//window.location.href = "index.html";
	                } else {
	                	$("#bookaspot").hide();
	                	$("#view").hide();
	                	alert("Failed. Please retry.");
	                }
	           
	            });
	        
	        event.preventDefault();
	 });
	 
	 $("#viewBooking").submit(function(event) {
		 
		window.location.href = "map.html";
		
		  event.preventDefault();
	 });
});