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
			<h1>${contact.firstName} ${contact.lastName} Contact Details</h1>
			<hr>
			<div class="row mb15">
				<div class="col-12">
					<a href="<c:url value='/manageContact?c=${contact.contactId}'/>" class="float-right btn btn-sm btn-outline-success ml15">
						Edit Contact
					</a>
					<a href="<c:url value='/contacts'/>" class="float-right btn btn-sm btn-outline-primary ml15">
						Back
					</a>
				</div>
			</div>
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
		</div>

	</div>	
</div>

<c:import url="../dashboard/scriptDefinitions.jsp"/>

<!-- Scripts -->
<script src="<c:url value='/js/securityManagement.js'/>" type="text/javascript" charset="utf-8" async defer></script>

</body>
</html>