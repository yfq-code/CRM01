<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<%--bootstrap--%>
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<%--日历插件--%>
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<%--分页插件--%>
<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

	<script type="text/javascript">

	$(function(){
		//为创建按钮绑定事件，打开添加操作的模态窗口

		/*
			操作模态窗口的方式：
				需要操作的模态窗口的jquery对象，调用modal方法，为该方法传递参数 show:打开模态窗口   hide：关闭模态窗口
		*/
		$("#addBtn").click(function(){
            /*
            先清空表单
            注意：
                我们拿到了form表单的jquery对象
                对于表单的jquery对象，提供了submit()方法让我们提交表单
                但是表单的jquery对象，没有为我们提供reset()方法让我们重置表单（坑：idea为我们提示了有reset()方法）

                虽然jquery对象没有为我们提供reset方法，但是原生js为我们提供了reset方法
                所以我们要将jquery对象转换为原生dom对象

                jquery对象转换为dom对象：
                    jquery对象[下标]

                dom对象转换为jquery对象：
                    $(dom)
             */
            $("#activityAddForm")[0].reset();

			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
			//走后台，目的是为了取得用户信息列表，为所有者下拉框铺值
			//用ajax请求
			$.ajax({
				url : "workbench/Activity/getUserList.do",
				type : "get",
				dataType : "json",
				success : function (data) {
					//拿到的data是json格式的[{user1}、{2}...]
					let html = "";
					//遍历json数组
					$.each(data,function(i,n){
						html += "<option id='"+n.id+"'>"+n.name+"</option>"
					})
					//赋给select对象
					$("#create-marketActivityOwner").html(html);

					//将当前登录的用户，设置为下拉框默认的选项
					let name = "${user.name}";
					$("#create-marketActivityOwner").val(name);

					//所有者下拉框处理完毕后，展现模态窗口
					$("#createActivityModal").modal("show");
				}
			})
		})

		//为保存按钮绑定事件，执行添加操作
		$("#saveBtn").click(function(){
			$.ajax({
				url : "workbench/Activity/save.do",
				data : {
					"owner" : $.trim($("#create-marketActivityOwner").val()),
					"name" : $.trim($("#create-marketActivityName").val()),
					"startDate" : $.trim($("#create-startTime").val()),
					"endDate" : $.trim($("#create-endTime").val()),
					"cost" : $.trim($("#create-cost").val()),
					"description" : $.trim($("#create-describe").val())
				},
				type : "post",
				dataType : "json",
				success : function (data) {
					/*data:{"success":true/false}*/
					if(data.success) {
						//添加成功后,刷新市场活动信息列表（局部刷新）
						//关闭
						$("#createActivityModal").modal("hide");
						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
					}else{
						alert("fail save");
					}
				}
			})
		})

		/*页面加载完毕后触发一个方法，刷新页面*/
		pageList(1,2);
		/*为查询按钮绑定事件，调用的是pagelist方法*/
		$("#searchBtn").click(function(){
			/*在查询前先将关键词存入隐藏域*/
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startTime").val()));
			$("#hidden-endDate").val($.trim($("#search-endTime").val()));
			pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
		})

		//为全选的复选框绑定事件，触发全选操作
		$("#all").click(function () {
			/*name是choose的所有input对象，选定状态要和全选框保持一致*/
			$("input[name=choose]").prop("checked",this.checked);
		})
		/*反向操作，所有的选择框都选上了，全选自动选上，有一个取消则全选取消
		但是要注意，选择框是动态生成的，所以不能直接操作。正确语法如下：
			$(需要绑定元素的有效的外层元素).on(绑定事件的方式,需要绑定的元素的jquery对象,回调函数)
		*/
		$("#activityBody").on("click",$("input[name=choose]"),function(){
			//判断选择框选择了的数量是不是和总数据量是否相等，作为是否全选的标志
			$("#all").prop("checked",$("input[name=choose]").length==$("input[name=choose]:checked").length);
		})

		//为删除市场活动绑定按钮
		$("#deleteBtn").click(function () {
			//1、找到打钩的市场活动的jquery对象
			let $choose = $("input[name=choose]:checked");
			if($choose.length==0){
				alert("请选择需要删除的记录");
				//肯定选了，而且有可能是1条，有可能是多条
			}else{
				if(confirm("确定删除所选中的记录吗？")){
					//请求方式：url:workbench/activity/delete.do?id=xxx&id=xxx&id=xxx
					//拼接请求参数，即id=xx&id=xx...
					var param = "";
					//将$choose中的每一个dom对象遍历出来，取其value值，就相当于取得了需要删除的记录的id
					for(let i=0;i<$choose.length;i++){
						param += "id="+$($choose[i]).val();
						//如果不是最后一个元素，需要在后面追加一个&符
						if(i!=$choose.length-1) param += "&";
					}
					//ajax发送请求
					$.ajax({
						url : "workbench/Activity/delete.do",
						data: param,
						type : "post",
						dataType : "json",
						success : function (data) {
							if(data.success){
								//重新获取页面
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
								alert("删除成功");
							}
							else alert("删除失败");
						}
					})
				}
			}

		})
		//为修改市场活动绑定按钮
		$("#editBtn").click(function(){
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
			//1、找到打钩的市场活动的jquery对象
			let $choose = $("input[name=choose]:checked");
			if($choose.length==0){
				alert("请选择需要修改的对象");
			}else if($choose.length>1){
				alert("只能选择一条记录进行修改");
				//肯定只选了一条
			}else{
				let id = $choose.val();
				$.ajax({
					url : "workbench/Activity/getUserListAndActivity.do",
					data : {
						"id" : id
					},
					type : "get",
					dataType : "json",
					success : function (data) {
						/*
							data
								用户列表
								市场活动对象
								{"uList":[{用户1},{2},{3}],"a":{市场活动对象}}
						 */
						//拼字符串，做下拉列表的
						let html = "";
						$.each(data.uList,function (i,n) {
							html += "<option id='"+n.id+"'>"+n.name+"</option>"
						})
						$("#edit-owner").html(html);

						//显示其他信息
						$("#edit-id").val(data.a.id);  /*很重要，但是隐藏起来*/
						$("#edit-name").val(data.a.name);
						$("#edit-owner").val(data.a.owner);
						$("#edit-startDate").val(data.a.startDate);
						$("#edit-endDate").val(data.a.endDate);
						$("#edit-cost").val(data.a.cost);
						$("#edit-description").val(data.a.description);

						//所有的值都填写好之后，打开修改操作的模态窗口
						$("#editActivityModal").modal("show");
					}
				})
			}
		})
		//为更新市场活动绑定按钮
		$("#updateBtn").click(function(){
			$.ajax({
				url : "workbench/Activity/update.do",
				data : {
					"id" : $.trim($("#edit-id").val()),
					"owner" : $.trim($("#edit-owner").val()),
					"name" : $.trim($("#edit-name").val()),
					"startDate" : $.trim($("#edit-startDate").val()),
					"endDate" : $.trim($("#edit-endDate").val()),
					"cost" : $.trim($("#edit-cost").val()),
					"description" : $.trim($("#edit-description").val())
				},
				type : "post",
				dataType : "json",
				success : function (data) {
					/*data:{"success":true/false}*/
					if(data.success) {
						//修改操作后，应该维持在当前页，维持每页展现的记录数，用的是插件的内容
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//关闭
						$("#editActivityModal").modal("hide");
					}else{
						alert("fail save");
					}
				}
			})
		})
	})

	/**
	 * pageNo：页面的编号
	 * pageSize：一页展示的数据行数
	 * 当"访问市场活动"、"数据更新"、"查询操作"后就要调用一次该方法对页面进行刷新
	 */
	function pageList(pageNo,pageSize) {
		//查询前，将隐藏域中保存的信息取出来，重新赋予到搜索框中,这样就保证了页面显示的就是最近一次的查询结果
		$("#search-name").val($("#hidden-name").val());
		$("#search-owner").val($("#hidden-owner").val());
		$("#search-startTime").val($("#hidden-startDate").val());
		$("#search-endTime").val($("#hidden-endDate").val());

		$.ajax({
			url : "workbench/Activity/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				/*前两个是分页查询，后两个是用作条件查询*/
				"name" : $.trim($("#search-name").val()),
				"owner" : $.trim($("#search-owner").val()),
				"startDate" : $.trim($("#search-startTime").val()),
				"endDate" : $.trim($("#search-endTime").val())
			},
			type : "get",
			dataType : "json",
			success : function (data) {
				/*
                我们需要的是：市场活动信息列表 json数组
                    [{市场活动1},{2},{3}] List<Activity> dataList
                    分页插件需要的是：查询出来的总记录数
                    {"total":100} int total
                    组合一下：
                    {"total":100,"dataList":[{市场活动1},{2},{3}]}
                */
				//遍历返回的市场活动数组dataList组串
				let html = "";
				$.each(data.dataList,function(i,n){
					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="choose" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/Activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>'
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.startDate+'</td>';
					html += '<td>'+n.endDate+'</td>'
				})
				$("#activityBody").html(html);

				//计算总页数
				let totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;
				//数据处理完毕后，结合分页查询，对前端展现分页信息
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片
					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					//该回调函数时在，点击分页组件的时候触发的
					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
			}
		})
	}
	
</script>
</head>
<body>
	<%--隐藏域，存储上一次搜索的关键词--%>
	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startTime" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endTime" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<%--data-dismiss="modal":关闭动态窗口，不涉及提交数据
						但是，保存数据需要发送请求,需要给个id
					--%>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" >
						<%--需要给这个市场活动一个id--%>
						<input type="hidden" id="edit-id"/>
						<div class="form-group">
							<label for="edit-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">

								</select>
							</div>
                            <label for="edit-name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate">
							</div>
							<label for="edit-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<%--添加表单域标签里面的内容用的是 val() 方法--%>
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startTime" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endTime">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="all" /></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
						<%--表体，展示查询信息--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<%--分页号码展示区--%>
				<div id="activityPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>