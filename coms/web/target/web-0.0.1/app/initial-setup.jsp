<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>Coms Login</title>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/login.css'/>">
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="offset-md-4 col-md-4 offset-sm-3 col-sm-6">
				<div class="card">
					<div class="card-header">
						<h4>Administrator Account Setup</h4>
					</div>
					<div class="card-body">
						<form action="<c:url value="/initialSetup"/>" method="POST">
							<div class="form-group">
								<input class="form-control" type="password" name="password" placeholder="Password"/>
							</div>
							<div class="form-group">
								<input type="submit" name="newAdminAccount" id="newAdminAccount" class="form-control btn btn-lg btn-success btn-block" value="Create Account">
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>		
	</div>
	
	<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" type="text/javascript" charset="utf-8"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" type="text/javascript" charset="utf-8"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" type="text/javascript" charset="utf-8"></script>

</body>
</html>