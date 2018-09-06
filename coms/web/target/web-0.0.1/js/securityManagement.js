"use strict"

$(document).ready(function() {
	
	$(".clickable").click(function() {
		window.location = $(this).data("href");
	});

	if ($("#objectIdIndicator").val() == "" || $("#objectIdIndicator").val() == null) {
		editOrAddObjectView();
	}

	$("#editObject").click(function() {
		editOrAddObjectView();
	});

	function editOrAddObjectView() {
		$("#manageDetailsTitle").addClass("d-none");
		$("#manageEditTitle").removeClass("d-none");
		$(".inputDisplay").find("input").removeAttr("readonly");
		$(".inputDisplay").find("input").removeClass("form-control-plaintext");
		$(".inputDisplay").find("input").addClass("form-control");
		$("#editObject").parent().parent().hide();
		$(".restoreRW").find("a").removeClass("d-none");
		$(".restoreRW").find("input").removeClass("d-none");
	}
});