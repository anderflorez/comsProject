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
			<div class="jumbotron">
				<h2 id="manageDetailsTitle">User Details</h2>
				<hr>
				
				<div class="row mb15">
					<div class="col-12">
						<a href="<c:url value='/manageUser?u=${userDetails.userId}'/>" class="float-right btn btn-sm btn-outline-success ml15">
							Edit User
						</a>
						<a href="<c:url value='/users'/>" class="float-right btn btn-sm btn-outline-primary ml15">
							Back
						</a>
					</div>
				</div>
				
				<form:form modelAttribute="userDetails">
					<div class="form-group row">
						<label for="userUsername" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Username: </strong></label>
						<div class="col-12 col-md-9 col-lg-10">
							<form:input id="userUsername" path="username" readonly="true" class="form-control-plaintext"/>
						</div>
					</div>
					<div class="form-group row">
						<label for="userStatus" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Status: </strong></label>
						<div class="col-12 col-md-9 col-lg-10">
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

					<form:input path="userId" class="d-none"/>
				</form:form>
			</div>			
		</div>
	</div>	
</div>

<c:if test="${error != null}">
	<div class="alert alert-danger alert-dismissible fade show" role="alert">
		${error}
		<button type="button" class="close" data-dismiss="alert" aria-label="Close">
			<span aria-hidden="true">&times;</span>
		</button>
	</div>
</c:if>

<c:import url="../dashboard/scriptDefinitions.jsp"/>

<!-- Scripts -->
<script src="<c:url value='/js/securityManagement.js'/>" type="text/javascript" charset="utf-8" async defer></script>

</body>
</html>