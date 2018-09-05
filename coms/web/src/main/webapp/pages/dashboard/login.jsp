<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Coms Login</title>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/login.css'/>">
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-12 offset-sm-2 col-sm-8 offset-lg-4 col-lg-4">
				<div class="card">
					<div class="card-header">
						<h3>Sign in</h3>
					</div>
					<div class="card-body">
						<form action="<c:url value='/login'/>" method="POST">
						
							<c:if test="${param.error != null}">
								<p>Invalid username and/or password</p>
							</c:if>
							
							<div class="form-group">
								<input type="text" name="username" class="form-control" placeholder="Username" value="${param.username}">
							</div>
							<div class="form-group">
								<input type="password" name="password" class="form-control" placeholder="Password">
							</div>
							<div class="form-group">
								<input type="submit" name="login" id="login" class="form-control btn btn-lg btn-success btn-block" value="Login">
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>		
	</div>
	
	<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>

</body>
</html>