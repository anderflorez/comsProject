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
			<h2>Create New Contact</h2>	
			<hr>
			<div class="row mb15">
				<div class="col-12">
					<button id="editContact" class="float-right btn btn-sm btn-outline-success ml15">
						Edit Contact
					</button>
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
				<form:input id="contactIdIndicator" path="contactId" class="d-none"/>
				<div class="form-group row inputSubmit">
					<div class="col-12">
						<input type="submit" class="btn btn-success float-right ml15 d-none" value="Save Contact">
						<c:if test="${contact.contactId != null}">
							<a href="<c:url value='/manageContact?c=${contact.contactId}'/>" class="btn btn-secondary float-right ml15 d-none">Cancel</a>
						</c:if>
						<c:if test="${contact.contactId == null}">
							<a href="<c:url value='/contacts'/>" class="btn btn-secondary float-right ml15 d-none">Cancel</a>
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



<!-- 				<div id="contactDetails">
					<h1>${contact.firstName} ${contact.lastName} Contact Details</h1>
					<hr>

					<table id="objectDetailsTable">
						<tbody>
							<tr>
								<th>First Name: </th>
								<td>${contact.firstName}</td>
							</tr>
							<tr>
								<th>Middle Name: </th>
								<td>${contact.middleName}</td>
							</tr>
							<tr>
								<th>Last Name: </th>
								<td>${contact.lastName}</td>
							</tr>
							<tr>
								<th>Email: </th>
								<td>${contact.email}</td>
							</tr>
						</tbody>
					</table>
				</div> -->