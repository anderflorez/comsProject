<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<c:import url="head.jsp" />
<body>
	
<c:import url="sidebar.jsp" />

<div class="page-container">
	<c:import url="header.jsp" />

	<!-- Styles -->
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/securityManagement.css'/>">
	
	<div class="page-content">

		<nav class="nav nav-tabs">
			<a class="nav-item nav-link" href="<c:url value='/contacts'/>">Contacts</a>
			<a class="nav-item nav-link active" href="<c:url value='/users'/>">Users</a>
			<a class="nav-item nav-link" href="<c:url value='/roles'/>">Roles</a>
		</nav>
		
		<div class="row new-item">
			<div class="col-12">
				<button id="newItem" type="button" class="float-right btn btn-sm btn-outline-success"
					data-toggle="collapse" data-target="#manageUser"
					aria-expanded="false" aria-controls="#manageUser">New User</button>
			</div>
			<div id="manageUser" class="collapse col-12 topMargin25">
				<div class="container card card-body">
					<form:form modelAttribute="userFormObject">
						<div class="form-group row">
							<label for="objectUsername" class="col-12 col-sm-3 col-lg-2 col-form-label">Username: </label>
							<div class="col-12 col-sm-9 col-lg-10">
								<form:input id="objectUsername" path="username" class="form-control"/>
							</div>
						</div>
						<div class="form-group row">
							<label for="objectPassword1" class="col-12 col-sm-3 col-lg-2 col-form-label">Password: </label>
							<div class="col-12 col-sm-9 col-lg-10">
								<input id="objectPassword1" name="objectPassword1" type="password" class="form-control"/>
							</div>
						</div>
						<div class="form-group row">
							<label for="objectPassword2" class="col-12 col-sm-3 col-lg-2 col-form-label">Repeat Password: </label>
							<div class="col-12 col-sm-9 col-lg-10">
								<input id="objectPassword2" name="objectPassword2" type="password" class="form-control"/>
							</div>
						</div>
						<div class="form-group row">
							<label for="objectStatus" class="col-12 col-sm-3 col-lg-2 col-form-label">E-Mail: </label>
							<div class="col-12 col-sm-9 col-lg-10">
								<form:select path="enabled" id="objectStatus" class="form-control">
									<form:option value="true" label="Enabled"/>
									<form:option value="false" label="Disabled"/>
								</form:select>
							</div>
						</div>
						<div class="float-right">
							<button type="button" class="btn btn-secondary"
								data-toggle="collapse" data-target="#manageUser">Cancel</button>
							<input type="submit" class="btn btn-success" value="Save Contact">
							<input id="objectId" name="objectId" type="text" class="d-none"/>
						</div>
					</form:form>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-12 table-responsive">
				<table class="table table-hover">
					<thead>
						<tr class="table-success">
							<th scope="col">Id</th>
							<th scope="col">Contact Name</th>
							<th scope="col">Username</th>
							<th scope="col">Status</th>
							<th scope="col">Added</th>
							<th scope="col">Last Accessed</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${users}" var="user">
							<tr>
								<td class="userId">${user.userId}</td>
								<td class="contactName">${user.contact.firstName} ${user.contact.lastName}</td>
								<td class="username">${user.username}</td>
								<td class="userStatus">${user.enabled}</td>
								<td class="userAdded">${user.dateAdded}</td>
								<td class="userLastAccess">${user.lastAccess}</td>
								<td class="editButton">
									<button id="userEditButton" type="button" data-toggle="collapse" data-target="#manageUser" 
											aria-expanded="false" aria-controls="#manageUser">
										<i class="ti-pencil-alt"></i>
									</button>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
</div>

<c:import url="scriptDefinitions.jsp"/>

<!-- Scripts -->
<script src="<c:url value='/js/securityManagement.js'/>" type="text/javascript" charset="utf-8" async defer></script>

</body>
</html>