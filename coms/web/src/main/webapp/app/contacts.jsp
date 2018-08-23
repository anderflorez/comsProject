<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:import url="head.jsp" />
<c:import url="sidebar.jsp" />

<div class="page-container">
	<c:import url="header.jsp" />

	<div class="page-content">

		<c:import url="management-nav.jsp" />
		<div class="row">
			<div class="col-12">
				<button type="button" class="float-right btn btn-outline-success"
					data-toggle="collapse" data-target="#addContact"
					aria-expanded="false" aria-controls="#addContact">New Contact</button>
			</div>
			<div id="addContact" class="collapse col-12 topMargin25">
				<div class="container card card-body">
					<div class="row">
						<div class="col-12">
							<form:form modelAttribute="userSetup">
								<label>Username: </label><form:input path="username"/>
								<label>Password: </label><form:input path="password"/>
								<input type="submit" value="Create New Account">
							</form:form>
						</div>
						<div class="col-12">
							<div class="float-right">
								<button type="button" class="btn btn-secondary"
									data-toggle="collapse" data-target="#addContact">Cancel</button>
								<button type="button" class="btn btn-success">Save Contact</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

</div>

<c:import url="bottom.jsp" />