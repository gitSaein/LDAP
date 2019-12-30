<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dasan.dis.bean.UserBean" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>  
<% 
	UserBean user = (UserBean)session.getAttribute("user"); 
	String SsoEmail= "";
	if(user != null){
		SsoEmail = user.getEmail();
	}
%>
<!DOCTYPE html>
<html>
<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<meta charset="utf-8" />
		<title>비밀번호 변경</title>

		<meta name="description" content="top menu &amp; navigation" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />

		<!-- bootstrap & fontawesome -->
		<link rel="stylesheet" href="/assets/css/bootstrap.min.css" />
		<link rel="stylesheet" href="/assets/css/font-awesome/4.1.0/css/font-awesome.min.css" />

		<!-- page specific plugin styles -->
        <link rel="stylesheet" href="/assets/css/jquery-ui.min.css" />

		<!-- text fonts -->
		<link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans:400,300" />

		<!-- ace styles -->
		<link rel="stylesheet" href="/assets/css/ace.min.css" id="main-ace-style" />
		<link rel="stylesheet" href="/assets/css/ace-skins.min.css" />
		<link rel="stylesheet" href="/assets/css/ace-rtl.min.css" />
		<script type="text/javascript" src="/Js/passwordEx.js"></script>
		<script src="/assets/js/ace-extra.min.js"></script>
		<script type="text/javascript" src="/assets/js/jquery/2.1.1/jquery.min.js"></script>
		<script src="/assets/js/jquery-ui.min.js"></script>
		
	</head>
	
	<body class="login-layout light-login">
		<div class="main-container">
			<div class="main-content">
				<div class="row">
					<div class="col-sm-10 col-sm-offset-1">
						<div class="login-container">
							<div style="height:200px;vertical-align:middle;"></div>
							<div class="position-relative">
								<div id="login-box" class="login-box visible widget-box no-border">
									<div class="widget-body">
										<div class="widget-main">
											<h4 class="header blue lighter bigger">
												<i class="ace-icon fa fa-info-circle blue"></i>
												<b>비밀번호 변경</b>
											</h4>
											<span>
												※사용자의 계정 이름의 일부를 포함하지 않아야 함
												<br>※최소 8자 이상
												<br>※다음 네 가지 범주 중 세 가지의 문자 포함
												<br>&nbsp;&nbsp;&nbsp;&nbsp;- 영문 대문자(A - Z)
												<br>&nbsp;&nbsp;&nbsp;&nbsp;- 영문 소문자(a - z)
												<br>&nbsp;&nbsp;&nbsp;&nbsp;- 기본 10개 숫자(0 - 9)
												<br>&nbsp;&nbsp;&nbsp;&nbsp;- 특수문자(예: !, $, #, %)<br>
											</span>
											<br>
											<form id="pwdchangeform" >
											    <input type="hidden" name="funcType" value="rAndA"/>
											    <c:set var="SsoEmail" value="<%= SsoEmail %>" scope="page"/>
											    <input type="hidden" name="SsoEmail" value="${SsoEmail}"/>
												<fieldset>
													<label class="block clearfix" >
														<input type="text" class="form-control" id ="reqEmail" name="reqEmail"/>
													</label>
													    <c:if test="${SsoEmail ne null}">
													    	<script type="text/javascript">
													    	 chkAttr('${SsoEmail}','reqEmail');
													    	</script>
														</c:if>													
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="password" class="form-control" placeholder="현재 비밀번호" id="oldPwd" name="oldPwd" />
															<i class="ace-icon fa fa-lock"></i>
														</span>
													</label>												
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="password" class="form-control" placeholder="새 비밀번호" id="newPwd" name="newPwd" />
															<i class="ace-icon fa fa-lock"></i>
														</span>
													</label>
													<label class="block clearfix">
														<span class="block input-icon input-icon-right">
															<input type="password" class="form-control" placeholder="새 비밀번호 확인" id="newPwdChk" name="newPwdChk" />
															<i class="ace-icon fa fa-lock"></i>
														</span>
													</label>													
													<div class="space"></div>
													<div class="clearfix">
														<div class="ui-dialog-buttonset">
															<button type="button" class="btn btn-gray btn-xs ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only pull-right" role="button" onclick="closePop()">
																<span class="ui-button-text">취소</span>
															</button>
															<button type="button" class="btn btn-primary btn-xs ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only pull-right" role="button" onclick="changePwd('pwdchangeform','newPwd','newPwdChk','oldPwd');">
																<span class="ui-button-text">확인</span>
															</button>
														</div>
													</div>	
													<div class="space-4"></div>
												</fieldset>
											</form>
										</div><!-- /.widget-main -->
									</div><!-- /.widget-body -->
								</div><!-- /.login-box -->
							</div><!-- /.position-relative -->
						</div>
					</div><!-- /.col -->
				</div><!-- /.row -->
			</div><!-- /.main-content -->
		</div><!-- /.main-container -->
	</body>
</html>
		
		
		