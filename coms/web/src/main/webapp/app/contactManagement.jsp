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
			<a class="nav-item nav-link active" href="<c:url value='/contacts'/>">Contacts</a>
			<a class="nav-item nav-link" href="<c:url value='/users'/>">Users</a>
			<a class="nav-item nav-link" href="<c:url value='/roles'/>">Roles</a>
		</nav>
		
		<div id="newContact" class="row mb15">
			<div class="col-12">
				<button type="button" class="float-right btn btn-sm btn-outline-success" data-toggle="modal" data-target="#manageContact">
					New Contact
				</button>
			</div>
		</div>
		
		<div class="row">
			<div class="col-12 table-responsive">
				<table class="table table-hover">
					<thead>
						<tr class="table-success">
							<th scope="col">Id</th>
							<th scope="col">First Name</th>
							<th scope="col">Last Name</th>
							<th scope="col">E-Mail</th>
							<th scope="col">Edit</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${contacts}" var="contact">
							<tr>
								<td class="contactid">${contact.contactId}</td>
								<td class="contactName">${contact.firstName}</td>
								<td class="contactLast">${contact.lastName}</td>
								<td class="contactEmail">${contact.email}</td>
								<td class="contactButton">
									<button type="button" data-toggle="modal" data-target="#manageContact" class="contactEditButton">
										<i class="ti-pencil-alt"></i>
									</button>
								</td>
								<td class="contactMiddle d-none">${contact.middleName}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>		
	</div>	
</div>

<!-- Modal -->
<div class="modal fade" id="manageContact" tabindex="-1" role="dialog" aria-labelledby="manageContactLabel" aria-hidden="true">
	<div class="modal-dialog" role="document">
		<div class="modal-content">

			<div class="modal-header">
				<h5 class="modal-title" id="manageContactLabel">Create New Contact</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>

			<div class="modal-body">
				<form:form modelAttribute="contactObject">
					<div class="form-group">
						<label for="objectFName">First Name: </label>
						<form:input id="objectFName" path="firstName" class="form-control"/>
					</div>
					<div class="form-group">
						<label for="objectMName">Middle Name: </label>
						<form:input id="objectMName" path="middleName" class="form-control"/>
					</div>
					<div class="form-group">
						<label for="objectLName">Last Name: </label>
						<form:input id="objectLName" path="lastName" class="form-control"/>
					</div>
					<div class="form-group">
						<label for="objectEMail">E-Mail: </label>
						<form:input id="objectEMail" path="email" class="form-control"/>
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