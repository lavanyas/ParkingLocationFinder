/**
 * 
 */

$(document).ready(function() {
	$("#bookaspot").hide();
	$("#find-address").submit(function(event) {
		event.preventDefault();
		$("#search-result").empty();
		$("#registration-form").remove();
		var address = $('input[name=find-address]').val();
		 
		var uri = "/customers?" + "address=" + address;
		 
		console.log(uri);
		 
		 $.get(uri , function(data, status ) { 
			 // Handle no data here
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
	    				 if ($('#search-result').is(':empty')){
	    				 alert("No Parking available in the choosen location");
	    				} else {
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
	            url         : 'http://localhost:8080/reserve', // the url where we want to POST
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
	                } else {
	                	alert("Failed. Please retry.");
	                }
	           
	            });
	 });
});