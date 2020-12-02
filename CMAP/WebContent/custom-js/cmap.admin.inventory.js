
var startNum, pageLength;
var isModify = false;

$(document).ready(function() {
	initMenuStatus("toggleMenu_admin", "toggleMenu_admin_items", "bk_inventory");
	
	startNum = 0;
	pageLength = Number($("#pageLength").val());	

	$("#btnAdd").click(function() {
		uncheckAll();
		initModal();
		
		$("#addModifyModal").modal({
			backdrop : 'static'
		});
	});
	
	$("#btnModify").click(function() {
		var checkedItem = $('input[name=chkbox]:checked');
		
		if (checkedItem.length == 0) {
			alert('請先勾選欲修改的項目');
			return;
		}
		
		if (checkedItem.length > 1) {
			alert('請僅勾選1個項目');
			return;
		}
		
		document.getElementById('addProbe').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(3).text(); 
		document.getElementById('addGroup').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(4).text();
		document.getElementById('addDeviceName').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(5).text();
		document.getElementById('addDeviceIp').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(6).text();
		document.getElementById('addDeviceType').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(7).text();
		document.getElementById('addBrand').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(8).text(); 
		document.getElementById('addModel').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(9).text();
		document.getElementById('addSystemVersion').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(10).text();
		document.getElementById('addSerialNumber').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(11).text();
		document.getElementById('addManufactureDate').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(12).text();
		
		$("#addModifyModal").modal({
			backdrop : 'static'
		});
	});
	
	$("#btnDelete").click(function() {
		envAction('delete');
	});
	
	$("#btnSave").click(function() {
		envAction('save');
	});
});

function envAction(action) {
	var obj = new Object();
	
	var id = $("input[name='chkbox']:checked").map(function() {
     	return $(this).val();
     }).get(0);
	
	obj.deviceId = id;
	
	if (action == "delete") {
		var checkedItem = $('input[name=chkbox]:checked');
		
		if (checkedItem.length == 0) {
			alert('請先勾選欲刪除的項目');
			return;
		}
		
		confirm("請確認是否刪除", "doDeleteActionAjax")
	} else if (action == "save") {
		var checkFlag = true;
		var checkFields = document.getElementsByClassName("checkRequired");
		for(var i= 0 ; i < checkFields.length; i++){
			var val = checkFields[i].value;
			if(!val || /^\s*$/.test(val)){
				alert(checkFields[i].placeholder + "不可為空白");
				return ;
			}
		}
		
		var modifyProbe = $("input[name='inputAddProbe']").map(function() {
		        	return $(this).val();
		        }).get(0);
		var modifyGroup = $("input[name='inputAddGroup']").map(function() {
		         	return $(this).val();
		         }).get(0);
		var modifyDeviceName = $("input[name='inputAddDeviceName']").map(function() {
		         	 return $(this).val();
		          }).get(0);
		var modifyDeviceIp = $("input[name='inputAddDeviceIp']").map(function() {
        	 return $(this).val();
         }).get(0);
		var modifyDeviceType = $("input[name='inputAddDeviceType']").map(function() {
        	 return $(this).val();
         }).get(0);
		var modifyBrand = $("input[name='inputAddBrand']").map(function() {
        	 return $(this).val();
         }).get(0);
		var modifyModel = $("input[name='inputAddModel']").map(function() {
        	 return $(this).val();
         }).get(0);
		var modifySystemVersion = $("input[name='inputAddSystemVersion']").map(function() {
        	 return $(this).val();
         }).get(0);
		var modifySerialNumber = $("input[name='inputAddSerialNumber']").map(function() {
        	 return $(this).val();
         }).get(0);
		var modifyManufactureDate = $("input[name='inputAddManufactureDate']").map(function() {
        	 return $(this).val();
         }).get(0);
		
		obj.modifyProbe = modifyProbe;
		obj.modifyGroup = modifyGroup;
		obj.modifyDeviceName = modifyDeviceName;
		obj.modifyDeviceIp = modifyDeviceIp;
		obj.modifyDeviceType = modifyDeviceType;
		obj.modifyBrand = modifyBrand;
		obj.modifyModel = modifyModel;
		obj.modifySystemVersion = modifySystemVersion;
		obj.modifySerialNumber = modifySerialNumber;
		obj.modifyManufactureDate = modifyManufactureDate;
		
		doActionAjax(obj, "save");
	}
	
}

function doDeleteActionAjax() {
	var obj = new Object();
	
	var ids = $("input[name='chkbox']:checked").map(function() {
     	return $(this).val();
     }).get();
	
	obj.ids = ids;
	
	doActionAjax(obj, "delete");
}

function doActionAjax(obj, action) {
	$.ajax({
		url : _ctx + '/admin/inventory/'+action,
		data : JSON.stringify(obj),
		headers: {
		    'Accept': 'application/json',
		    'Content-Type': 'application/json'
		},
		type : "POST",
		dataType : 'json',
		async: true,
		beforeSend : function() {
			showProcessing();
		},
		complete : function() {
			hideProcessing();
		},
		success : function(resp) {
			if (resp.code == '200') {								
				setTimeout(function(){
					$('#addModifyModal').modal('hide');
					
				}, 500);
				alert(resp.message);
				
				findData($("#queryFrom").val());
			} else {
				alert('envAction > success > else :: resp.code: '+resp.code);
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}

//查詢按鈕動作
function findData(from) {
	$('#queryFrom').val(from);
	
	if (from == 'MOBILE') {
		$('#collapseExample').collapse('hide');
	}
	
	startNum = 0;
	if (typeof resultTable !== "undefined") {
		$(".dataTables_scrollBody").scrollTop(0);
		resultTable.ajax.reload();
		$(".myTableSection").show();
		
	} else {
		$(".myTableSection").show();
		
		resultTable = $('#resultTable').DataTable(
		{
			"autoWidth" 	: true,
			"paging" 		: false,
			"bFilter" 		: false,
			"ordering" 		: true,
			"info" 			: true,
			"serverSide" 	: true,
			"bLengthChange" : true,
			"pagingType" 	: "full",
			"processing" 	: true,
			"scrollX"		: true,
			"scrollY"		: dataTableHeight,
			"scrollCollapse": true,
			"pageLength"	: pageLength,
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
	        "createdRow": function( row, data, dataIndex ) {
	        },
			"ajax" : {
				"url" : _ctx + '/admin/inventory/getInventoryInfoData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryGroup = $("#queryGroup").val(),
						d.queryDevice = $("#queryDevice").val()
					
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryGroup = $("#queryGroup_mobile").val(),
						d.queryDevice = $("#queryDevice_mobile").val()
					}
					
					d.queryProbe = $("#queryProbe").val(),
					d.queryDeviceName = $("#queryDeviceName").val(),
					d.queryDeviceType = $("#queryDeviceType").val(),
					d.queryBrand = $("#queryBrand").val(),
					d.queryModel = $("#queryModel").val();
					d.start = 0; //初始查詢一律從第0筆開始
					d.length = pageLength;
					return d;
				},
				beforeSend : function() {
					countDown('START');
				},
				complete : function() {
					countDown('STOP');
				},
				"error" : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				},
				"timeout" : parseInt(_timeout) * 1000 //設定60秒Timeout
			},
			"order": [[6 , 'acs' ]],
			"initComplete": function(settings, json) {
				if (json.msg != null) {
					$(".myTableSection").hide();
					alert(json.msg);
				}
				bindScrollEvent();
          },
			"drawCallback" : function(settings) {
				//搜尋
				$("div.dataTables_filter").parent().removeClass('col-sm-12');
				$("div.dataTables_filter").parent().addClass('col-sm-6');
				
				//資料筆數
				$("div.dataTables_info").parent().removeClass('col-sm-12');
				$("div.dataTables_info").parent().addClass('col-sm-6');
				
				startNum = pageLength; //初始查詢完成後startNum固定為pageLength大小
				lastScrollYPos = $(".dataTables_scrollBody").prop("scrollTop");
				$("#resultTable_filter").find("input").prop("placeholder","(模糊查詢速度較慢)");
				bindTrEvent();
			},
			"columns" : [
				{},{},
				{ "data" : "deviceId" , "orderable" : false },
				{ "data" : "probe" , "orderable" : false },
				{ "data" : "groupName" , "orderable" : false },
				{ "data" : "deviceName" , "orderable" : false },
				{ "data" : "deviceIp" , "orderable" : false },
				{ "data" : "deviceType" , "orderable" : false },
				{ "data" : "brand" , "orderable" : false },
				{ "data" : "model" , "orderable" : false },
				{ "data" : "systemVersion" , "orderable" : false },
				{ "data" : "serialNumber" , "orderable" : false },
				{ "data" : "manufactureDate" , "orderable" : false }
			],
			"columnDefs" : [
				{
					"targets" : [0],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render" : function(data, type, row) {
								 var html = '<input type="checkbox" id="chkbox" name="chkbox" onclick="changeTrBgColor(this)" value="'+row.deviceId+'">';
								 return html;
							 }
				},
				{
					"targets" : [1],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render": function (data, type, row, meta) {
						       	return meta.row + meta.settings._iDisplayStart + 1;
						   	}
				}
			],
		});
	}
}


function countDown(status) {
	if (status == 'START') {
		startTime = parseInt(_timeout) - 1;
		
		timer_start = performance.now();
		timer = setInterval(function () {
			$("#timeoutMsg").val("Timeout倒數 : " + startTime + " 秒");
			startTime--;
			
	    }, 1000);
		
	} else {
		timer_end = performance.now();
		var spent = (parseInt(timer_end) - parseInt(timer_start)) / 1000;
		$("#timeoutMsg").val("查詢花費時間 : " + spent + " 秒");
		
		clearInterval(timer);
	}
}


function bindScrollEvent() {
	$(".dataTables_scrollBody").scroll(function(e) {
		if ($(".dataTables_empty").length == 0) {
			var rowCount = $("#resultTable > tBody > tr").length;
			var scrollTop = $(this).prop("scrollTop");
			// 改用 scrollHeight + clientHeight 以支援大眾瀏覽器
			var scrollTopMax = $(this).prop("scrollHeight") - $(this).prop("clientHeight");
			
			if (scrollTop > lastScrollYPos) { //移動Y軸時才作動
				lastScrollYPos = scrollTop;

				if (rowCount >= pageLength) { //查詢結果筆數有超過分頁筆數才作動
					//if (scrollTop > (scrollTopMax - (scrollTopMax*0.3))) {
					//捲到最底才查找下一批資料
					if (scrollTop >= ( scrollTopMax - 100 )) { // scrollTopMax - 100 確保解析度問題導致 scrollTop 達不到 scrollTopMax 位置
						if (!waitForNextData) {
							waitForNextData = true;
							findNextData();
						}
					}
				}
			}
		}
	});
}

//[資料匯出]Modal >> 匯出確認按鈕事件 (由cmap.main.js呼叫)
function doDataExport(exportRecordCount) {
	var dataObj = new Object();
	if ($('#queryFrom').val() == 'WEB') {
		d.queryGroup = $("#queryGroup").val(),
		d.queryDevice = $("#queryDevice").val()
	
	} else if ($('#queryFrom').val() == 'MOBILE') {
		d.queryGroup = $("#queryGroup_mobile").val(),
		d.queryDevice = $("#queryDevice_mobile").val()
	}
	
	d.queryProbe = $("#queryProbe").val(),
	d.queryDeviceName = $("#queryDeviceName").val(),
	d.queryDeviceType = $("#queryDeviceType").val(),
	d.queryBrand = $("#queryBrand").val(),
	d.queryModel = $("#queryModel").val();
	
	dataObj.start = 0; //初始查詢一律從第0筆開始
	dataObj.length = pageLength;
	dataObj.exportRecordCount = exportRecordCount;
	
	$.ajax({
		url : _ctx + '/admin/inventory/dataExport.json',
		data : dataObj,
		type : "POST",
		dataType : 'json',
		async: true,
		beforeSend : function(xhr) {
			showProcessing();
		},
		complete : function() {
			hideProcessing();
		},
		initComplete : function(settings, json) {
     },
		success : function(resp) {
			if (resp.code == 200) {
				const fileId = resp.data.fileId;
				const url = getResourceDownloadLink(fileId);
				// 彈出下載視窗
				location.href = url;
				// 關閉Modal視窗
				closeExportPanel();
				
			} else {
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}