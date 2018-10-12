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
			<a class="nav-item nav-link active" href="<c:url value='/contacts'/>">Contacts</a>
			<a class="nav-item nav-link" href="<c:url value='/users'/>">Users</a>
			<a class="nav-item nav-link" href="<c:url value='/roles'/>">Roles</a>
		</nav>

		<div id="contactManagement">
			<div class="jumbotron">
				<h2>Contact Details</h2>
				<hr>
				
				<div class="row mb15">
					<div class="col-12">
						<c:if test="${error == null}">
							<div class="dropdown float-right ml15">
								<a href="#" class="btn btn-sm btn-success dropdown-toggle" role="button" id="actionMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
									Actions
								</a>
								<div class="dropdown-menu" aria-labelledby="actionMenu">
									<a href="<c:url value='/manageContact?c=${contact.contactId}'/>" class="dropdown-item">Edit Contact</a>
									<c:if test="${contactUser == null}">
										<a href="<c:url value='/manageUser?c=${contact.contactId}'/>" class="dropdown-item">Create User</a>
									</c:if>
										<button type="button" class="dropdown-item bg-danger text-white" data-toggle="modal" data-target="#deleteContactConfirmation">
											Delete
										</button>
<!-- 									<form method="POST" action="<c:url value='/deleteContact'/>">
										<input type="text" name="contactId" value="${contact.contactId}" class="d-none">
										<input type="submit" name="submit" value="Delete" class="dropdown-item bg-danger text-white">
									</form> -->
								</div>
							</div>
						</c:if>
						<a href="<c:url value='/contacts'/>" class="float-right btn btn-sm btn-outline-primary ml15">
							Back
						</a>
					</div>
				</div>
				
				<form:form modelAttribute="contact">
					<div class="form-group row">
						<label for="contactFName" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>First Name: </strong></label>
						<div class="col-12 col-md-9 col-lg-10 inputDisplay">
							<form:input id="contactFName" path="firstName" readonly="true" class="form-control-plaintext"/>
						</div>
					</div>
					<div class="form-group row">
						<label for="contactMName" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Middle Name: </strong></label>
						<div class="ccol-12 col-md-9 col-lg-10 inputDisplay">
							<form:input id="contactMName" path="middleName" readonly="true" class="form-control-plaintext"/>
						</div>
					</div>
					<div class="form-group row">
						<label for="contactLName" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>Last Name: </strong></label>
						<div class="col-12 col-md-9 col-lg-10 inputDisplay">
							<form:input id="contactLName" path="lastName" readonly="true" class="form-control-plaintext"/>
						</div>
					</div>
					<div class="form-group row">
						<label for="contactEMail" class="col-12 col-md-3 col-lg-2 col-form-label"><strong>E-Mail: </strong></label>
						<div class="col-12 col-md-9 col-lg-10 inputDisplay">
							<form:input id="contactEMail" path="email" readonly="true" class="form-control-plaintext"/>
						</div>
					</div>
					<form:input path="contactId" class="d-none"/>
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

<!-- Modal -->
<div class="modal fade" id="deleteContactConfirmation" tabindex="-1" role="dialog" aria-labelledby="deleteContactLabel" aria-hidden="true">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="deleteContactLabel">Delete Confirmation</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				Are you sure you want to delete the contact ${contact.firstName} ${contact.lastName}
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
				<form method="POST" action="<c:url value='/deleteContact'/>">
					<input type="text" name="contactId" value="${contact.contactId}" class="d-none">
					<input type="submit" name="submit" value="Delete" class="btn btn-danger">
				</form>
			</div>
		</div>
	</div>
</div>

<c:import url="../dashboard/scriptDefinitions.jsp"/>

<!-- Scripts -->
<script src="<c:url value='/js/securityManagement.js'/>" type="text/javascript" charset="utf-8" async defer></script>

</body>
</html>