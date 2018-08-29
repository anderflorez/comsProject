"use strict"

$(document).ready(function() {
	
	windowDashboardSized();
	$(window).on("resize", function () {
		windowDashboardSized();
	});

	$("#nav-menu").find("button").on("click", function() {
		sidebarSize(2, 400);
	});

	$("#menu-close").find("button").on("click", function() {
		if ($(window).width() < 900) {
			sidebarSize(0, 400);
		}
		else {
			sidebarSize(1, 400);
		}
	});

	function windowDashboardSized() {
		let pagewidth = $(window).width();
		if (pagewidth > 1400) {
			sidebarSize(2, 1);
		}
		else if (pagewidth > 900) {
			sidebarSize(1, 1);
		}
		else {
			sidebarSize(0, 1);
		}
		return;
	}

	function sidebarSize(side, speed) {
		if (side === 0) {
			$("#sidebar").animate({left: '-280px'}, speed);
			$(".page-container").animate({left: '0'}, speed);
			$("#nav-menu").find("button").show();
			$("#menu-close").show();
			$("#sidebar-head-text").show();
			$(".menu-title").show();
			$("#sidebar-head-logo").find("img").css("padding", "15px");
			$("#sidebar").css("width", "280px");
			$("#nav-right").css("display", "inline-flex");
		}
		else if (side === 1) {
			$("#sidebar").animate({left: 0}, speed);
			$("#nav-menu").find("button").show();
			$("#menu-close").hide();
			$("#sidebar-head-text").fadeOut(speed);
			$(".menu-title").fadeOut(speed);
			$("#sidebar-head-logo").find("img").css("padding", "15px 0");
			$("#sidebar").animate({width: "80px"}, speed);
			$(".page-container").animate({left: "80px"}, speed);
			$("#nav-right").css("display", "inline-flex");
		}
		else {
			$("#sidebar").css("left", "-280px");
			$("#menu-close").show();
			$("#sidebar-head-text").show();
			$(".menu-title").show();
			$("#sidebar-head-logo").find("img").css("padding", "15px");
			$("#sidebar").css("width", "280px");
			$("#nav-menu").find("button").hide();
			$("#sidebar").animate({left: '0'}, speed);
			$(".page-container").animate({left: '280'}, speed);

			if ($(window).width() < 492) {
				$("#nav-right").css("display", "none");
			}
		}
		return;
	}

});