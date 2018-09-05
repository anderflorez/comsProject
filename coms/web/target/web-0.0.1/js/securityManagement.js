"use strict"

$(document).ready(function() {
	
	$(".clickable").click(function() {
		window.location = $(this).data("href");
	});

	if ($("#contactIdIndicator").val() == "") {
		editOrAddContactView();
	}

	$("#editContact").click(function() {
		editOrAddContactView();
	});

	function editOrAddContactView() {
		$(".inputDisplay").find("input").removeAttr("readonly");
		$(".inputDisplay").find("input").removeClass("form-control-plaintext");
		$(".inputDisplay").find("input").addClass("form-control");
		$("#editContact").parent().parent().hide();
		$(".inputSubmit").find("a").removeClass("d-none");
		$(".inputSubmit").find("input").removeClass("d-none");
	}
});