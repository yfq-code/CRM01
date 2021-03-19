<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

	<script>
		$(function(){
			if(window.top!=window){
				window.top.location=window.location;
			}
			//页面加载完毕后，将用户文本框中的内容清空
			$("#loginAct").val("");
			//页面加载完毕后，让用户的文本框自动获得焦点
			$("#loginAct").focus();
			//验证用户登录，这里是点击事件
			$("#submitBtn").click(function(){
				login();
			})

			//为当前登录也窗口绑定敲键盘事件
			//event:这个参数可以取得我们敲的是哪个键
			$(window).keydown(function (event) {

				//如果取得的键位的码值为13，表示敲的是回车键
				if(event.keyCode==13){
					login();
				}
			})
		})

		/*普通登录方法，写在$(function(){}  外面*/
		function login(){
			//验证账号密码不能为空
			//取得账号密码
			//将文本中的左右空格去掉，使用$.trim(文本)
			var loginAct = $.trim($("#loginAct").val());
			var loginPwd = $.trim($("#loginPwd").val());
			if(loginAct=="" || loginPwd==""){
				$("#msg").html("账号或者密码不能为空");
				return;
			}
			/*接下来就要发送ajax请求，利用后台验证登录了*/
			$.ajax({
				url:"settings/user/login.do",
				/* name:value 对的形式*/
				data:{
					"loginAct":loginAct,
					"loginPwd":loginPwd
				},
				type:"post",
				dataType:"json",
				success:function (data) {
					/*返回对象包括两个属性
					* "success":true/false
					* "massage":错误信息
					* */
					//如果登陆成功了,就转跳页面
					if(data.success){
						window.location.href = "workbench/index.jsp";
					}
					//否则输出错误信息
					else{
						$("#msg").html(data.massage);
					}
				}
			})
		}

	</script>


</head>
<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2021&nbsp;富强企业</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
			<form action="workbench/index.jsp" class="form-horizontal" role="form">
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" id="loginAct" type="text" placeholder="用户名">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" id="loginPwd"  type="password" placeholder="密码">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						
							<span id="msg"></span>
						
					</div>
					<%--
						把这里的按钮设置为button而不是submit，然后设置事件方法
						但是需要重新定义回车提交的功能
					--%>
					<button type="button" id="submitBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>