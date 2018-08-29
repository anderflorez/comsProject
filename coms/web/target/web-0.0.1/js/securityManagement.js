"use strict"

$(document).ready(function() {
	
	$(".contactEditButton").on("click", function() {
		let fname = $(this).parent().parent().find(".contactName").text();
		let mname = $(this).parent().parent().find(".contactMiddle").text();
		let lname = $(this).parent().parent().find(".contactLast").text();
		let ename = $(this).parent().parent().find(".contactEmail").text();
		let id = $(this).parent().parent().find(".contactid").text();
		
		$("#objectFName").val(fname);
		$("#objectMName").val(mname);
		$("#objectLName").val(lname);
		$("#objectEMail").val(ename);
		$("#objectId").val(id);
	});
	
	$("#newContact").find("button").on("click", function() {
		$("#objectFName").val("");
		$("#objectMName").val("");
		$("#objectLName").val("");
		$("#objectEMail").val("");
		$("#objectId").val("");
	});

});