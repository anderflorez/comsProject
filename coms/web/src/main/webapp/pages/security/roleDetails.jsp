<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<c:import url="../dashboard/head.jsp"/>
<body>
	
<c:import url="../dashboard/sidebar.jsp" />

<div class="page-container">
	<!-- Styles -->
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/securityManagement.css'/>">
	<c:import url="../dashboard/header.jsp" />

	
	<div class="page-content">

		<nav class="nav nav-tabs">
			<a class="nav-item nav-link" href="<c:url value='/contacts'/>">Contacts</a>
			<a class="nav-item nav-link" href="<c:url value='/users'/>">Users</a>
			<a class="nav-item nav-link active" href="<c:url value='/roles'/>">Roles</a>
		</nav>

		<div>
			<div class="jumbotron">
				<h2 id="manageDetailsTitle">Role Details</h2>
				<hr>
				
				<div class="row mb15">
					<div class="col-12">
						<c:if test="${error == null}">
							<div class="dropdown float-right ml15">
								<a href="#" class="btn btn-sm btn-success dropdown-toggle" role="button" id="actionMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
									Actions
								</a>
								<div class="dropdown-menu" aria-labelledby="actionMenu">
									<a href="<c:url value='/manageRole?r=${role.roleId}'/>" class="dropdown-item">Edit Role</a>
									<button type="button" class="dropdown-item" data-toggle="modal" data-target="#deleteRoleConfirmation">
										Delete
									</button>
								</div>
							</div>
						</c:if>
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

					<form:input path="roleId" class="d-none"/>
				</form:form>
				
			</div>
			<div class="jumbotron">
				<div class="row mb25">
					<div class="col-12">
						<h2>Member users</h2>
						<hr>
						<c:if test="${userMembers != null}">
							<div class="row">
								<div class="col-12 table-responsive">
									<table class="table table-hover">
										<thead>
											<tr class="table-success">
												<th scope="col">User</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${userMembers}" var="userMember">
												<tr>
													<td>${userMember.contact.firstName} ${userMember.contact.lastName}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</c:if>
					</div>
				</div>				
			</div>			

		</div>
	</div>	
</div>

<c:import url="../dashboard/scriptDefinitions.jsp"/>

<c:if test="${error != null}">
	<div class="alert alert-danger alert-dismissible fade show" role="alert">
		${error}
		<button type="button" class="close" data-dismiss="alert" aria-label="Close">
			<span aria-hidden="true">&times;</span>
		</button>
	</div>
</c:if>

<!-- Modal -->
<div class="modal fade" id="deleteRoleConfirmation" tabindex="-1" role="dialog" aria-labelledby="deleteRoleLabel" aria-hidden="true">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="deleteRoleLabel">Delete Confirmation</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<div>Are you sure you want to delete the role ${role.roleName}?</div><br>
				<div>Warning: This action will also remove any permissions for users that belong to this role</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
				<form method="POST" action="<c:url value='/deleteRole'/>">
					<input type="text" name="roleId" value="${role.roleId}" class="d-none">
					<input type="submit" name="submit" value="Delete" class="btn btn-danger">
				</form>
			</div>
		</div>
	</div>
</div>

<!-- Scripts -->
<script src="<c:url value='/js/securityManagement.js'/>" type="text/javascript" charset="utf-8" async defer></script>

</body>
</html>