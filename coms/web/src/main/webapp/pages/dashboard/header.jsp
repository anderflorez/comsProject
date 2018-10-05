<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="header">
	<div id="nav-menu">
		<button type="button">
			<i class="fas fa-bars"></i>
		</button>
	</div>
	<div id="nav-right">
		<button>
			<i class="fas fa-tasks"></i>
		</button>
		<div id="user-menu" class="dropdown">
			<button type="button" data-toggle="dropdown" class="dropdown-toggle">
				<span>${user.contact.firstName} ${user.contact.lastName}</span>
			</button>
			<ul class="dropdown-menu">
				<li><a href="#"><i class="ti-settings"></i>Settings</a></li>
				<li><a href="#"><i class="ti-user"></i>Profile</a></li>
				<li><a id="management" href="<c:url value='/contacts'/>"><i class="fas fa-users-cog"></i>Management</a></li>
				<li class="divider"></li>
				<li><a href="<c:url value='logout'/>"><i class="ti-power-off"></i>Logout</a></li>
			</ul>					
		</div>
	</div>
</div>
