<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<c:import url="../dashboard/head.jsp"/>
<body>
	
<c:import url="../dashboard/sidebar.jsp" />

<div class="page-container">
	<c:import url="../dashboard/header.jsp" />

	<!-- Styles -->
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/securityManagement.css'/>">
	
	<div class="page-content">

		<nav class="nav nav-tabs">
			<a class="nav-item nav-link" href="<c:url value='/contacts'/>">Contacts</a>
			<a class="nav-item nav-link active" href="<c:url value='/users'/>">Users</a>
			<a class="nav-item nav-link" href="<c:url value='/roles'/>">Roles</a>
		</nav>

		<div id="userManagement">
			<c:if test="${userForm.userId != null}">
				<h2 id="manageDetailsTitle">User Details</h2>
				<h2 id="manageEditTitle" class="d-none">Edit User</h2>
			</c:if>
			
			<c:if test="${userForm.userId == null}">
				<h2>Create New User</h2>
			</c:if>
			<hr>
			
			<div class="row mb15">
				<div class="col-12">
					<button id="editObject" class="float-right btn btn-sm btn-outline-success ml15">
						Edit User
					</button>
					<a href="<c:url value='/users'/>" class="float-right btn btn-sm btn-outline-primary ml15">
						Back
					</a>
				</div>
			</div>
			
			<form:form modelAttribute="userForm">
				<div class="form-group row">
					<label for="userUsername" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Username: </strong></label>
					<div class="col-12 col-md-9 col-lg-10 inputDisplay">
						<form:input id="userUsername" path="username" readonly="true" class="form-control-plaintext"/>
					</div>
				</div>
				<c:if test="${userForm.userId != null}">
					<div class="form-group row">
						<label for="userStatus" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Status: </strong></label>
						<div class="ccol-12 col-md-9 col-lg-10 inputDisplay">
							<form:input id="userStatus" path="enabled" readonly="true" class="form-control-plaintext"/>
						</div>
					</div>
					<div class="form-group row">
						<label for="userDateAdded" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Date Added: </strong></label>
						<div class="col-12 col-md-9 col-lg-10">
							<form:input id="userDateAdded" path="dateAdded" readonly="true" class="form-control-plaintext"/>
						</div>
					</div>
					<div class="form-group row">
						<label for="userLastAccess" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Last Access: </strong></label>
						<div class="col-12 col-md-9 col-lg-10">
							<form:input id="userLastAccess" path="lastAccess" readonly="true" class="form-control-plaintext"/>
						</div>
					</div>
				</c:if>

				<c:if test="${userForm.userId == null}">
					<div class="form-group row">
						<label for="pass1" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Password: </strong></label>
						<div class="col-12 col-md-9 col-lg-10">
							<form:input path="password1" type="password" class="form-control" placeholder="Password" />
						</div>
					</div>
					<div class="form-group row">
						<!-- <label for="pass2" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Repeat Password: </strong></label> -->
						<div class="col-12 offset-md-3 col-md-9 offset-lg-2 col-lg-10">
							<form:input path="password2" type="password" class="form-control" placeholder="Password" />
						</div>
					</div>
				</c:if>

				<form:input id="objectIdIndicator" path="userId" class="d-none"/>
				<form:input id="objectIdIndicator" path="contactId" class="d-none"/>
				<div class="form-group row inputBtn">
					<div class="col-12">
						<input type="submit" class="btn btn-success float-right ml15 d-none" value="Save User">
						<c:if test="${userForm.userId != null}">
							<a href="<c:url value='/manageUser?u=${userForm.userId}&c=0'/>" class="btn btn-secondary float-right ml15 d-none">Cancel</a>
						</c:if>
						<c:if test="${userForm.userId == null}">
							<a href="<c:url value='/manageContact?c=${userForm.contactId}'/>" class="btn btn-secondary float-right ml15 d-none">Cancel</a>
						</c:if>
					</div>
				</div>
			</form:form>
		</div>
	</div>	
</div>

<c:import url="../dashboard/scriptDefinitions.jsp"/>

<!-- Scripts -->
<script src="<c:url value='/js/securityManagement.js'/>" type="text/javascript" charset="utf-8" async defer></script>

</body>
</html>