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

		<div id="roleManagement">
			<div class="jumbotron">
				<c:if test="${role.roleId != null}">
					<h2 id="manageEditTitle">Edit Role</h2>
				</c:if>
				
				<c:if test="${role.roleId == null}">
					<h2>Create New Role</h2>
				</c:if>
				<hr>
				
				<form:form modelAttribute="role">
					<div class="form-group row">
						<label for="roleName" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Role Name: </strong></label>
						<div class="col-12 col-md-9 col-lg-10 inputDisplay">
							<form:input id="roleName" path="roleName" class="form-control"/>
						</div>
					</div>

					<form:input path="roleId" class="d-none"/>

					<div class="row">
						<div class="col-12">
							<input type="submit" class="btn btn-success float-right ml15" value="Save Role">
							<c:if test="${role.roleId != null}">
								<a href="<c:url value='/roleDetail?r=${role.roleId}'/>" class="btn btn-secondary float-right ml15">Cancel</a>
							</c:if>
							<c:if test="${role.roleId == null}">
								<a href="<c:url value='/roles'/>" class="btn btn-secondary float-right ml15">Cancel</a>
							</c:if>
						</div>
					</div>
				</form:form>
				
			</div>
			<div class="jumbotron">
				<div class="row mb25">
					<div class="col-12">
						<h2>Member users</h2>
						<hr>
						<c:if test="${memberContacts != null}">
							<div class="row">
								<div class="col-12 table-responsive">
									<table class="table table-hover">
										<thead>
											<tr class="table-success">
												<th scope="col">User</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${memberContacts}" var="memberContact">
												<tr>
													<td>${memberContact.firstName} ${memberContact.lastName}</td>
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

<!-- Scripts -->
<script src="<c:url value='/js/securityManagement.js'/>" type="text/javascript" charset="utf-8" async defer></script>

</body>
</html>