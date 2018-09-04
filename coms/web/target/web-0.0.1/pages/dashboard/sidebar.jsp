<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="sidebar">
	<div id="sidebar-head">
		<a href="<c:url value='/'/>">
			<div id="sidebar-head-logo">
				<img src="<c:url value='/images/UEC BULB-64x64.png'/>"/>
			</div>
			<div id="sidebar-head-text">
				<h5>App Name</h5>
			</div>
		</a>
		<div id="menu-close">
			<button type="button">
				<i class="fas fa-chevron-left"></i>
			</button>
		</div>
	</div>
	<div id="sidebar-menu">
		<ul>
			<li>
				<a href="<c:url value='/'/>">
					<div class="menu-icon">
						<i class="fas fa-home"></i>
					</div>
					<div class="menu-title">Dashboard</div>
				</a>
			</li>
		</ul>
	</div>
</div>