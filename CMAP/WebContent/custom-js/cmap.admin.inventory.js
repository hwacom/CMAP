
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
	
	$("#btnImport_web").click(function() {
		uncheckAll();
		initModal();
		
		$("#inventoryDataImportModal").modal({
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
		if($('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(4).text().indexOf(" > ") > -1){
			var groupName = $('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(4).text().split(" > ");
			document.getElementById('addGroup').value=groupName[0];
			for(var i=1;i<groupName.length;i++){
				divAddInputText(groupName[i]);
			}			
		}else{
			document.getElementById('addGroup').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(4).text();
		}
		showValue();
		
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
//		var modifyGroup = $("input[name='inputAddGroup']").map(function() {
//		         	return $(this).val();
//		         }).get(0);
		var modifyGroup = "";
		
		$("input[name='inputAddGroup']").map(function() {
			if ($(this).val().trim() != ''){
				if(modifyGroup != "") modifyGroup = modifyGroup + " > ";
				modifyGroup = modifyGroup +  $(this).val();
			}
	     });

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
		var remark = $("input[name='inputAddRemark']").map(function() {
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
		obj.remark = remark;
		
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
					d.queryModifyOnly = document.getElementById("queryModifyOnly").checked,
					d.queryBrand = $("#queryBrand").val(),
					d.queryModel = $("#queryModel").val(),
					d.queryGroupName = $("#queryGroupName").val();
					
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
				},
				{
					"targets" : [5],
					"className" : "left",
					"searchable": true,
					"orderable": true,
					"render" : function(data, type, row) {
									var html = '<a href="#" onclick="viewCellDetail(\''+row.deviceId+'\')">'+row.deviceName+'</a>';
									return html;
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
		dataObj.queryGroup = $("#queryGroup").val(),
		dataObj.queryDevice = $("#queryDevice").val()
	
	} else if ($('#queryFrom').val() == 'MOBILE') {
		dataObj.queryGroup = $("#queryGroup_mobile").val(),
		dataObj.queryDevice = $("#queryDevice_mobile").val()
	}
	
	dataObj.queryProbe = $("#queryProbe").val(),
	dataObj.queryDeviceName = $("#queryDeviceName").val(),
	dataObj.queryDeviceType = $("#queryDeviceType").val(),
	dataObj.queryModifyOnly = document.getElementById("queryModifyOnly").checked,
	dataObj.queryBrand = $("#queryBrand").val(),
	dataObj.queryModel = $("#queryModel").val(),
	dataObj.queryGroupName = $("#queryGroupName").val();
	
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

function checkData(file){
     
	if(!file.name.endsWith('.csv')){
		alert('請選擇csv類型檔案');
		return ;
	}
	
	var reader = new FileReader();
	reader.readAsText(file,'big5');

   // here we tell the reader what to do when it's done reading...
   reader.onload = readerEvent => {
      var content = readerEvent.target.result; // this is the content!
      
      if(content.match(/\r?\n|\r/g).length < 2){
  		alert('檔案內容無資料資訊，請重新選擇');
  		return ;
  	  }

      $('<div class="confirm font" />').html("請確認是否匯入").dialog({
		 title: "確認訊息",
	     modal: true,
	     show: { effect: "fadeIn", duration: 300 },
	     resizable: false,
	     open : function() {
				$(".ui-dialog").css('z-index', 2000);
				$(".ui-widget-overlay").css('z-index', 1999);
				$(".ui-button.ui-corner-all.ui-widget.ui-button-icon-only.ui-dialog-titlebar-close").hide();
				$(".ui-button.ui-corner-all.ui-widget").css("font-family", "微軟正黑體");
			},
	     buttons : {
	          "確認" : function() {
	        	  $(this).dialog("close");
	        	  importData(content);
	          },
	          "取消" : function() {
	            $(this).dialog("close");
	          }
	        }
      });
      
//     console.log( "JSON = " +JSON.stringify(content) );
//     
//     confirm("請確認是否匯入", "doDeleteActionAjax");
   }
   
}

function importData(content){
	
	var result = csvJSON(content);
	
	if(result.length < 1){
		alert('檔案內容無資料資訊或資料格式錯誤，請重新選擇');
  		return ;
	}
	
	$.ajax({
		url : _ctx + '/admin/inventory/importData',
		data : JSON.stringify(result),
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
				$("#importFileName").val('');
				setTimeout(function(){
					$('#inventoryDataImportModal').modal('hide');
					
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

function csvJSON(csv){

  var lines=csv.split(/\r\n|\r|\n/);

  var result = [];

//  var headers=lines[0].split(",");
  var headers= ['probe', 'groupName', 'groupName2', 'groupName3', 'groupName4', 'groupName5', 'deviceName', 'deviceIp', 'deviceType', 'brand', 'model', 'systemVersion', 'serialNumber', 'manufactureDate', 'remark'];

  for(var i=1;i<=lines.length;i++){

	  var obj = {};
	  if(lines[i] != undefined && lines[i].indexOf(",") > -1){
//		  console.log("currentline["+i+"]/"+lines.length+" ="+ lines[i] );
		  var currentline=lines[i].split(",");

		  if(currentline.length == headers.length){
			  var groupName = "";
			  for(var j=0;j<headers.length;j++){
				  
				  if(j > 0 && j <= 5){//groupName
					  if (currentline[j].trim() != ''){
						  if(groupName != "") groupName = groupName + " > ";
						  groupName = groupName +  currentline[j].trim();
					  }
					  
					  if(j == 5){
						  obj[headers[1]] = groupName;
					  }
				  } else{
					  obj[headers[j]] = currentline[j];
				  }
			  }			  
			  result.push(obj);
		  }
	  }
  }
  
//  console.log("result ="+ result.length );
  return result;
}

function divAddInputText(val) {
	if($("input[name='inputAddGroup']").length == 5) return;
	var x = document.createElement("input");
	x.setAttribute("type", "text");
	x.setAttribute("name", "inputAddGroup");
	x.setAttribute("class", "form-control form-control-sm");
	x.setAttribute("value", val);
	x.setAttribute("onchange", "showValue()");
	var parent = document.getElementById("inputAddGroupDiv");
	parent.appendChild(x);
}

function showValue() {
	var result = "";
	
	$("input[name='inputAddGroup']").map(function() {
		if ($(this).val().trim() != ''){
			if(result != "") result = result + " > ";
			result = result +  $(this).val();
		}
     });
	$("#showGroupName").text(result);
	console.log("value = " + result);
}


function viewCellDetail(deviceId) {
	var obj = new Object();
	obj.deviceId = deviceId;
	
	$.ajax({
		url : _ctx + '/admin/inventory/getInvCellDetailData.json',
		data : JSON.stringify(obj),
		headers: {
		    'Accept': 'application/json',
		    'Content-Type': 'application/json'
		},
		type : "POST",
		async: true,
		beforeSend : function() {
			showProcessing();
		},
		complete : function() {
			hideProcessing();
		},
		success : function(resp) {
			if (resp.code == '200') {
				$('#viewCellDetailModal_frequencyBand').parent().show();
				$('#viewCellDetailModal_amfIpAddress').parent().show();
				$('#viewCellDetailModal_enodebType').parent().show();
				$('#viewCellDetailModal_gnbId').parent().show();
				$('#viewCellDetailModal_cellIdentify').parent().show();
				$('#viewCellDetailModal_physicalCellGroupId').parent().show();
				$('#viewCellDetailModal_physicalCellId').parent().show();
				$('#viewCellDetailModal_plmn').parent().show();
				$('#viewCellDetailModal_arfcn').parent().show();
				$('#viewCellDetailModal_bandWidth').parent().show();
				$('#viewCellDetailModal_currentTxPower').parent().show();
				
				$('#viewCellDetailModal_frequencyBand').html(resp.data.frequencyBand);
				$('#viewCellDetailModal_amfIpAddress').html(resp.data.amfIpAddress);
				$('#viewCellDetailModal_enodebType').html(resp.data.enodebType);
				$('#viewCellDetailModal_gnbId').html(resp.data.gnbId);
				$('#viewCellDetailModal_cellIdentify').html(resp.data.cellIdentify);
				$('#viewCellDetailModal_physicalCellGroupId').html(resp.data.physicalCellGroupId);
				$('#viewCellDetailModal_physicalCellId').html(resp.data.physicalCellId);
				$('#viewCellDetailModal_plmn').html(resp.data.plmn);
				$('#viewCellDetailModal_arfcn').html(resp.data.arfcn);
				$('#viewCellDetailModal_bandWidth').html(resp.data.bandWidth);
				$('#viewCellDetailModal_currentTxPower').html(resp.data.currentTxPower);
				
				$('#viewCellDetailModal').modal('show');
				
			} else {
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}