var user = null;

var errorFunction = function() {
  $('#warning').show();
}

var redirectPage = function(  ) {
	$.ajax( 'api/user', {  type : 'GET',  
		success:function( user ){
			console.log( user.username != null );
			if( user.username != null ) {  
				$('#warning').hide();
				window.location.replace('/user');
				}
			} , 
		error : errorFunction() } );
}

var login = function() {
  var usrname = $('#username').val();
  var psword = $('#password').val();
   var body = { username : usrname, password : psword};
   var url = '/login';
   $.ajax( url, {  type : 'POST',  data : body, success: redirectPage, error : errorFunction } );
}

var updateUser = function( ) {
   $('#profile').text( user.username );
}

var validateDate = function( inputDate ) {
  var date1 = inputDate.replace(/[ ]/g,"");
   var dtRegex = new RegExp(/\b\d{1,2}[\/-]\d{1,2}[\/-]\d{4}\b/);
  if( date1.length!=10  ||  !dtRegex.test(date1) )
    return false;

   var SplitValue = date1.split("/");
        var retVal = true;

        if ( !SplitValue[1].length == 2  ||  !SplitValue[0].length == 2  ||  !SplitValue[2].length == 4) {
            return false;
        }
        
        if (retVal) {
            var Day = parseInt(SplitValue[1], 10);
            var Month = parseInt(SplitValue[0], 10);
            var Year = parseInt(SplitValue[2], 10);
 
            if (retVal = ((Year > 1900) && (Year < 3000))) {
                if (retVal = (Month <= 12 && Month > 0)) {

                    var LeapYear = (((Year % 4) == 0) && ((Year % 100) != 0) || ((Year % 400) == 0));   
                    if(retVal = Day > 0)
                    {
                        if (Month == 2) {  
                            retVal = LeapYear ? Day <= 29 : Day <= 28;
                        } 
                        else {
                            if ((Month == 4) || (Month == 6) || (Month == 9) || (Month == 11)) {
                                retVal = Day <= 30;
                            }
                            else {
                                retVal = Day <= 31;
                            }
                        }
                    }
                }
            }
        }
        return retVal;
}


var createTask = function( ) {
  var description = $('#description').val();
  var colorCode = $('#colorCode').val();
  var dueDate = $('#dueDate').val();
 
   if( !validateDate(dueDate) || dueDate.indexOf(" ") !=-1 ){
      $('#dueDate').val("");
      alert("Please enter date with correct format!");
    } else {
      $('#description').val("");
      $('#dueDate').val("");
      $('#colorCode').val("#000000");
      checkContent();
     var body = { description : description, dueDate : dueDate, color : colorCode };
     var url = '/tasker/users/' + user.id +'/tasks';
     $.ajax( url, {  type : 'POST',  data : body, success : getAll } );

    $('#keyword').val("");
    $('#overDueOnly').removeAttr( "checked" , false );
    $('#incompleteOnly').removeAttr( "checked" , false );
 }
}

function checkContent (event) {
    if( $('#description').val()!=''  && $('#dueDate').val()!=''){
      $('#submitBtn').removeAttr("disabled");
      $('#submitBtn').attr("onclick","createTask()");
    } else{
      $('#submitBtn').attr("disabled","disabled");
      $('#submitBtn').removeAttr("onclick");
    }
}

var listenInput = function( clickedItem ){
  var parentTR = $(clickedItem).parents("tr");
  var tid = $(clickedItem).parents("tr").attr("id");
  var description =  $(parentTR).find('td:eq(0)').children("input").val() ;
  var colorCode = $(parentTR).find('td:eq(1)').children("input").val();
  var dueDate = $(parentTR).find('td:eq(2)').children("input").val();
  var completed = clickedItem.checked;

  if( description == "" ){
    alert("Task description cannot be empety!");
  } else if( validateDate(dueDate) ) {
    // Update task
     var body = { description : description, color : colorCode, dueDate : dueDate, completed: completed};
     var url = '/tasker/users/' + user.id +'/tasks/' + tid;
     $.ajax( url, {  type : 'PUT',  data : body, success: setDefaultselect } );

  }
}

var listenDelete = function(clickedItem) {
  var tid = $(clickedItem).parents("tr").attr("id");
  var url = '/tasker/users/' + user.id +'/tasks/' + tid;
  $.ajax( url, {  type : 'DELETE',  success : getAll, error : redirectToLogin } );
}

var listenSearch = function(event) {
  var key = $('#keyword').val().toLowerCase();

  var table = $('#resultTable');
  table.find('tr').hide();
  table.find('tr:eq(0)').show();

   for(var i = 1 ; i < table.find('tr').length ; i++)
   {    var tmpTR = table.find('tr:eq(' + i + ')');
        var tmpDESC = tmpTR.children("td:eq(0)").children("input").val();
        if( tmpDESC.toLowerCase().indexOf(key) !=-1 ) {
           table.find('tr:eq(' + i + ')').show();
        }
      
   }

}

var redirectToLogin = function( xhr, msg, err ) {
   window.location.replace('/');   
}

var getPrincipal = function( ) {
   $.ajax( '/api/user',
           { type : 'GET',
             success : function( apiUser ) { user = apiUser; updateUser();getAll();   },
//             error : redirectToLogin
           } );
   
}

var updateResult = function( result ) {
  $('#resultTable').text("");
 $('#resultTable').append("<thead style = \" font-weight:bold;\" ><td>Description</td><td>Color</td>  <td>Due</td><td>Completed</td><td></td></thead> <tbody>");
  for(var i=0 ; i < result.length; i++){

	  var dateArr = result[i].due.split("-");
	  var newDate = dateArr[1]+"/"+dateArr[2]+"/"+dateArr[0] ;

	    if( Boolean(result[i].completed) ){
	        $('#resultTable').append(
	            "<tr id=\"" + result[i].id + "\"> <td> <input type=\"text\" value=\"" + result[i].description + "\" style=\"border:none;\" onblur=\"listenInput(this)\"></input></td>" +
	            "<td><input type=\"color\" class=\"form-control\" id=\"colorInput\" value=\""+ result[i].color + "\" style=\"width:70px\" onchange=\"listenInput(this)\" ></td>" +
	            "<td> <input type=\"text\" value=\"" + newDate + "\"style=\"border:none;\"  onblur=\"listenInput(this)\"></input></td>" +
	            "<td><input type=\"checkbox\" onchange=\"listenInput(this)\" checked ></td>" +
	            "<td><button class=\"btn btn-danger\" type=\"button\"onclick=\"listenDelete(this)\"><span class=\"glyphicon glyphicon-trash\"></span></button></td> </tr>" 
	         );    
	      }else {
	        $('#resultTable').append(
	            "<tr id=\"" + result[i].id + "\"><td> <input type=\"text\" value=\"" + result[i].description + "\" style=\"border:none;\"  onblur=\"listenInput(this)\"></input></td>" +
	            "<td><input type=\"color\" class=\"form-control\" id=\"colorInput\" value=\""+ result[i].color + "\" style=\"width:70px\" onchange=\"listenInput(this)\"></td>" +
	            "<td><input type=\"text\" value=\"" + newDate + "\" style=\"border:none;\" onblur=\"listenInput(this)\" ></input></td>" +
	            "<td><input type=\"checkbox\" onchange=\"listenInput(this)\"></td>" +
	            "<td><button class=\"btn btn-danger\" type=\"button\"onclick=\"listenDelete(this)\"><span class=\"glyphicon glyphicon-trash\"  ></span></button></td> </tr>" 
	        ) ;
	      }
  }
  $('#resultTable').append("</tbody>");
  // After updating result, check search field
  listenSearch();
}

var listenComplete = function (event) {
   $('#resultTable').text("");

   // incomplete is YES  overdue is NO
   if( document.getElementById("incompleteOnly").checked){
    $('#overDueOnly').removeAttr( "checked" , false);
    $('#incompleteOnly').attr( "checked" , true);
    var url = '/tasker/users/' + user.id + '/tasks?incomplete=yes&overdue=no' ;
   $.ajax( url, {  type : 'GET',  success : updateResult } );
   } 
   else if( !document.getElementById("incompleteOnly").checked ) {

    $('#incompleteOnly').removeAttr( "checked" , false );
    var url = '/tasker/users/' + user.id + '/tasks' ;
   $.ajax( url, {  type : 'GET',  success : updateResult } );
   }
}

var listenOverdue = function (event) {
   $('#resultTable').text("");
   // overdue is NO
   if( !document.getElementById("overDueOnly").checked ){
    $('#overDueOnly').removeAttr( "checked" , false );
     var url = '/tasker/users/' + user.id + '/tasks' ;
   $.ajax( url, {  type : 'GET',  success : updateResult } );
   } 
   // overdue is YES
   else if( document.getElementById("overDueOnly").checked ) {

    $('#overDueOnly').attr( "checked" , true); 
    $('#incompleteOnly').removeAttr( "checked" , false);

    var url = '/tasker/users/' + user.id + '/tasks?incomplete=no&overdue=yes' ;
   $.ajax( url, {  type : 'GET',  success : updateResult } );
    }
}

var setDefaultselect = function(){
    $('#keyword').text("");
    $('#overDueOnly').removeAttr( "checked" , false); 
    $('#incompleteOnly').removeAttr( "checked" , false);
    getAll();
}

var getAll = function() {
   var url = 'tasker/users/' + user.id + '/tasks';
   $.ajax( url, {  type : 'GET',  success : updateResult } );
}

var logout = function() {
   user = null;
   var url = '/logout';
   $.ajax( url, { type : 'GET', success : redirectToLogin, error : redirectToLogin } );
}

