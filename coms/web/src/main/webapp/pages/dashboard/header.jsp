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
		<div class="dropdown">
			<a href="" id="user-menu" data-toggle="dropdown" class="dropdown-toggle" role="button" aria-haspopup="true" aria-expanded="false">
				<span>${user.contact.firstName} ${user.contact.lastName}</span>
			</a>
			<div class="dropdown-menu" aria-labelledby="user-menu">
				<a href="#" class="dropdown-item"><i class="ti-settings"></i>Settings</a>
				<a href="#" class="dropdown-item"><i class="ti-user"></i>Profile</a>
				<a href="<c:url value='/contacts'/>" class="dropdown-item"><i class="fas fa-users-cog"></i>Management</a>
				<a href="<c:url value='logout'/>" class="dropdown-item"><i class="ti-power-off"></i>Logout</a>
			</div>
		</div>
	</div>
</div>
