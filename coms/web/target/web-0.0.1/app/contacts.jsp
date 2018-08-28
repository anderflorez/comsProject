<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:import url="head.jsp" />
<c:import url="sidebar.jsp" />

<div class="page-container">
	<c:import url="header.jsp" />

	<div class="page-content">

		<c:import url="management-nav.jsp" />
		<div class="row new-item">
			<div class="col-12">
				<button id="newContact" type="button" class="float-right btn btn-outline-success"
					data-toggle="collapse" data-target="#addContact"
					aria-expanded="false" aria-controls="#addContact">New Contact</button>
			</div>
			<div id="addContact" class="collapse col-12 topMargin25">
				<div class="container card card-body">
					<form:form modelAttribute="contact">
						<div class="form-group row">
							<label for="newContactFName" class="col-12 col-sm-3 col-lg-2 col-form-label">First Name: </label>
							<div class="col-12 col-sm-9 col-lg-10">
								<form:input id="newContactFName" path="firstName" class="form-control"/>
							</div>
						</div>
						<div class="form-group row">
							<label for="newContactMName" class="col-12 col-sm-3 col-lg-2 col-form-label">Middle Name: </label>
							<div class="col-12 col-sm-9 col-lg-10">
								<form:input id="newContactMName" path="middleName" class="form-control"/>
							</div>
						</div>
						<div class="form-group row">
							<label for="newContactLName" class="col-12 col-sm-3 col-lg-2 col-form-label">Last Name: </label>
							<div class="col-12 col-sm-9 col-lg-10">
								<form:input id="newContactLName" path="lastName" class="form-control"/>
							</div>
						</div>
						<div class="form-group row">
							<label for="newContactEMail" class="col-12 col-sm-3 col-lg-2 col-form-label">E-Mail: </label>
							<div class="col-12 col-sm-9 col-lg-10">
								<form:input id="newContactEMail" path="email" class="form-control"/>
							</div>
						</div>
						<div class="float-right">
							<button type="button" class="btn btn-secondary"
								data-toggle="collapse" data-target="#addContact">Cancel</button>
							<input type="submit" class="btn btn-success" value="Save Contact">
							<input id="newContactId" type="text" name="newContactId" class="d-none"/>
						</div>
					</form:form>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-12 table-responsive">
				<table class="table table-hover">
					<thead>
						<tr class="table-success">
							<th scope="col">First Name</th>
							<th scope="col">Last Name</th>
							<th scope="col">E-Mail</th>
							<th scope="col">Edit</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${contacts}" var="contact">
							<tr>
								<td class="contactName">${contact.firstName}</td>
								<td class="contactLast">${contact.lastName}</td>
								<td class="contactEmail">${contact.email}</td>
								<td class="contactButton">
									<button type="button" data-toggle="collapse" data-target="#addContact" class="contactEditButton" 
											aria-expanded="false" aria-controls="#addContact">
										<i class="ti-pencil-alt"></i>
									</button>
								</td>
								<td class="contactid d-none">${contact.contactId}</td>
								<td class="contactMiddle d-none">${contact.middleName}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
</div>

<c:import url="bottom.jsp" />