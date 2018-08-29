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
		
		<div id="newUser" class="row mb15">
			<div class="col-12">
				<button type="button" class="float-right btn btn-sm btn-outline-success" data-toggle="modal" data-target="#manageUser">
					New User Account
				</button>
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
							<th scope="col">Last Access</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${users}" var="user">
							<tr>
								<td class="userid">${user.userId}</td>
								<td class="contactName">${user.contact.firstName} ${user.contact.lastName}</td>
								<td class="username">${user.username}</td>
								<td class="userStatus">${user.enabled}</td>
								<td class="userAdded">${user.dateAdded}</td>
								<td class="userLastAccess">${user.lastAccess}</td>
								<td class="userEditButton">
									<button type="button" data-toggle="modal" data-target="#manageUser">
										<i class="ti-pencil-alt"></i>
									</button>
								</td>
								<td class="contactId d-none">${user.contact.contactId}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>		
	</div>	
</div>

<!-- Modal -->
<div class="modal fade" id="manageUser" tabindex="-1" role="dialog" aria-labelledby="manageUserLabel" aria-hidden="true">
	<div class="modal-dialog" role="document">
		<div class="modal-content">

			<div class="modal-header">
				<h5 class="modal-title" id="manageUserLabel">Create New User Account</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>

			<div class="modal-body">
				<form:form modelAttribute="userObject">
					<div class="form-group">
						<label for="objectUsername">Username: </label>
						<form:input id="objectUsername" path="username" class="form-control"/>
					</div>
					<div class="form-group">
						<label for="objectPassword1">New Password: </label>
						<input id="objectPassword1" name="objectPassword1" type="password" class="form-control"/>
					</div>
					<div class="form-group">
						<label for="objectPassword2">Repeat Password: </label>
						<input id="objectPassword2" name="objectPassword2" type="password" class="form-control"/>
					</div>
					<div class="form-group">
						<label for="objectStatus">Status: </label>
						<form:select id="objectStatus" path="enabled" class="form-control">
							<form:option value="true" label="Enable"/>
							<form:option value="false" label="Disable"/>
						</form:select>
					</div>
					<div class="modal-footer">
						<div class="float-right">
							<input id="objectId" type="text" name="objectId" class="d-none"/>
							<button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
							<input type="submit" class="btn btn-success" value="Save Contact">
						</div>
					</div>
				</form:form>
			</div>

		</div>
	</div>
</div>

<c:import url="scriptDefinitions.jsp"/>

<!-- Scripts -->
<script src="<c:url value='/js/securityManagement.js'/>" type="text/javascript" charset="utf-8" async defer></script>

</body>
</html>