/**
 * 
 */

$(document).ready(function() {

    // process the form
    $('#registration-form').submit(function(event) {

        // get the form data
        // there are many ways to get this data using jQuery (you can use the class or id also)
        var formData = {
            "firstName": $('input[name=f-name]').val(),
            "lastName": $('input[name=l-name]').val(),
           /* 'email' : $('input[name=email]').val(),
           */
            "address": $('input[name=address]').val()
        };
        
        // process the form
        $.ajax({
        	headers: { 
                'Accept': 'application/json',
                'Content-Type': 'application/json' 
            },
            type        : 'POST', // define the type of HTTP verb we want to use (POST for our form)
            url         : '/addcustomer', // the url where we want to POST
            data        : JSON.stringify(formData), // our data object
            dataType    : 'json', // what type of data do we expect back from the server
            encode          : true
        })
            // using the done promise callback
            .always(function(data) {

                // log data to the console so we can see
                console.log(data); 
                if(data.status == 200) {
                	alert("Registration successful");
                	window.location.href = "index.html";
                } else {
                	alert("Registration failure");
                }
                

                // here we will handle errors and validation messages
            });
        

        // stop the form from submitting the normal way and refreshing the page
        event.preventDefault();
    });
    
/*   $("#show-records").click(function(event){
    	$.get( "/customers/all", function( data, status ) {
    		if(status == "success") {
    			var result = "<table class=\"table\"><thead><tr><th>First Name</th><th>Last Name</th><tr></thead>";
    			for(var i=0;i<data.length;i++){
    				var item = data[i];
    				result += "<tr>" +
    						  "<td>"+item.firstName + "</td>" +
    						  "<td>"+item.lastName + "</td>" +
    						  "</tr>"
    			}
    			 result += "</table>"
    	         $("#records-container").append(result);
    		}
    		  
    		});
    });*/
});