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

		<div>
			<div class="jumbotron">
				<div id="newContact" class="row mb15">
					<div class="col-12">
						<button type="button" class="float-right btn btn-sm btn-outline-success clickable" data-href="<c:url value='/manageContact?c=0'/>">
							New Contact
						</button>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 table-responsive">
						<table class="table table-hover">
							<thead>
								<tr class="table-success">
									<th scope="col">Contact Name</th>
									<th scope="col">E-Mail</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${contacts}" var="contact">
									<tr class="clickable" data-href="<c:url value='/manageContact?c=${contact.contactId}'/>">
										<td>${contact.firstName} ${contact.lastName}</td>
										<td>${contact.email}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>			
		</div>
	</div>
</div>

<div class="alert alert-danger userAlert">
Be sure to have your pages set up with the latest design and development standards. That means using an HTML5 doctype and including a viewport meta tag for proper responsive behaviors. Put it all together and your pages should look like this: Bootstrap employs a handful of important global styles and settings that you’ll need to be aware of when using it, all of which are almost exclusively geared towards the normalization of cross browser styles. Let’s dive in. For more straightforward sizing in CSS, we switch the global box-sizing value from content-box to border-box. This ensures padding does not affect the final computed width of an element, but it can cause problems with some third party software like Google Maps and Google Custom Search Engine.
</div>

<c:import url="../dashboard/scriptDefinitions.jsp"/>

<!-- Scripts -->
<script src="<c:url value='/js/securityManagement.js'/>" type="text/javascript" charset="utf-8" async defer></script>

</body>
</html>