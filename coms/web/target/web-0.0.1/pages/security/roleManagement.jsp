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
			<a class="nav-item nav-link" href="<c:url value='/users'/>">Users</a>
			<a class="nav-item nav-link active" href="<c:url value='/roles'/>">Roles</a>
		</nav>

		<div id="roleManagement">
			<c:if test="${role.roleId != null}">
				<h2 id="manageDetailsTitle">Role Details</h2>
				<h2 id="manageEditTitle" class="d-none">Edit Role</h2>
			</c:if>
			
			<c:if test="${role.roleId == null}">
				<h2>Create New Role</h2>
			</c:if>
			<hr>
			
			<div class="row mb15">
				<div class="col-12">
					<button id="editObject" class="float-right btn btn-sm btn-outline-success ml15">
						Edit Role
					</button>
					<a href="<c:url value='/roles'/>" class="float-right btn btn-sm btn-outline-primary ml15">
						Back
					</a>
				</div>
			</div>
			
			<form:form modelAttribute="role">
				<div class="form-group row">
					<label for="roleName" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Role Name: </strong></label>
					<div class="col-12 col-md-9 col-lg-10 inputDisplay">
						<form:input id="roleName" path="roleName" readonly="true" class="form-control-plaintext"/>
					</div>
				</div>

				<form:input id="objectIdIndicator" path="roleId" class="d-none"/>

				<div class="row inputBtn">
					<div class="col-12">
						<input type="submit" class="btn btn-success float-right ml15 d-none" value="Save Role">
						<c:if test="${role.roleId != null}">
							<a href="<c:url value='/manageRole?r=${role.roleId}'/>" class="btn btn-secondary float-right ml15 d-none">Cancel</a>
						</c:if>
						<c:if test="${role.roleId == null}">
							<a href="<c:url value='/roles'/>" class="btn btn-secondary float-right ml15 d-none">Cancel</a>
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