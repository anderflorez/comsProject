"use strict"

$(document).ready(function() {
	
	$(".contactEditButton").on("click", function() {
		let fname = $(this).parent().parent().find(".contactName").text();
		let mname = $(this).parent().parent().find(".contactMiddle").text();
		let lname = $(this).parent().parent().find(".contactLast").text();
		let ename = $(this).parent().parent().find(".contactEmail").text();
		let id = $(this).parent().parent().find(".contactid").text();
		
		$("#contactFName").val(fname);
		$("#contactMName").val(mname);
		$("#contactLName").val(lname);
		$("#contactEMail").val(ename);
		$("#contactId").val(id);
	});
	
	$("#newContact").find("button").on("click", function() {
		$("#contactFName").val("");
		$("#contactMName").val("");
		$("#contactLName").val("");
		$("#contactEMail").val("");
		$("#contactId").val("");
	});

//	$("tbody").find("tr").on("click", function() {
//		let id = $(this).find(".contactId").text();
//	});
	
	$(".clickable-row").click(function() {
		window.location = $(this).data("href");
	});

});