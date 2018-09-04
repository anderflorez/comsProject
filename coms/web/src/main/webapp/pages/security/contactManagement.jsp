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
		
		<div class="jumbotron">
			<h1>Edit ${contactObject.firstName} ${contactObject.lastName} Contact</h1>
			<hr>
			<form:form modelAttribute="contactObject">
				<div class="form-group">
					<label for="contactId">First Name: </label>
					<form:input id="contactId" path="contactId" class="form-control"/>
				</div>
				<div class="form-group">
					<label for="contactFName">First Name: </label>
					<form:input id="contactFName" path="firstName" class="form-control"/>
				</div>
				<div class="form-group">
					<label for="contactMName">Middle Name: </label>
					<form:input id="contactMName" path="middleName" class="form-control"/>
				</div>
				<div class="form-group">
					<label for="contactLName">Last Name: </label>
					<form:input id="contactLName" path="lastName" class="form-control"/>
				</div>
				<div class="form-group">
					<label for="contactEMail">E-Mail: </label>
					<form:input id="contactEMail" path="email" class="form-control"/>
				</div>
				<div class="float-right">
					<!-- <input id="contactId" name="contactId" type="text" class="d-none" value="${contactObject.contactId}" /> -->
					<a href="<c:url value='/contactDetails?objectId=${contactObject.contactId}'/>" class="btn btn-secondary">Cancel</a>
					<input type="submit" class="btn btn-success" value="Save Contact">
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