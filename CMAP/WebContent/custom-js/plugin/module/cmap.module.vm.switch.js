/**
 * 
 */
var _ctx = $("meta[name='ctx']").attr("content");
var source = null;

$(document).ready(function() {
	
	$("#btnGo").click(function() {
		confirm("請再次確認是否要開始執行切換?", "startSwitch");;
	});
	
	$("#btnClose").click(function() {
		$("#btnGo").attr("disabled", true);
		hideProcessing2();
		stopSSE();
		window.location.replace(_ctx + '/plugin/module/vmswitch/result');
	});
	
	stopSSE();
	
});

function hideProcessing2() {
	$(".mask").hide();
	$(".processing2").hide();
}

function showProcessing2() {
	$(".mask").show();
	$(".processing2").show();
}

function startSSE() {
	if (!!window.EventSource) {
        source = new EventSource('vmswitch/push'); //为http://localhost:8080/testSpringMVC/push
        s = '';
        
        $('#msg_from_server').show();
        $('#msg_from_server').scrollTop = $('#msg_from_server').scrollHeight;
        
        source.addEventListener('message', function (e) {
        	console.log("e.data: " + e.data);
        	var data = JSON.parse(e.data);
        	var time = data.time;
        	var step = data.step;
        	var result = data.result;
        	var msg = data.msg;
        	console.log("step: " + step + " >> result: " + result + " >> msg: " + msg);
        	
        	if (step == '<CLOSE>') {
        		source.close();
        		
        	} else if (step != '<NONE>') {
        		
        		if (step == '<PROCESS_END>') {
        			msg = "======================== [ End ] ========================";
        			source.close();
        			
        			$(".processing2").css("background", "none");
        			$("#btnClose").show();
        			
        		} else if (step == '<STEP_RESULT>') {
        			msg = " >> " + msg + "\r\n";
        			
        		} else {
            		msg = "[" + time + "] " + step + " >> " + msg;
            		
            		/*
            		s += e.data + "<br/>"
                    $("#msg_from_server").html(s);
                    */
            	}
        		
        		$('#msg_from_server').val(function(i, text) {
        		    return text + msg;
        		});
        	}
        });

        source.addEventListener('open', function (e) {
            console.log("連線開啟");
        }, false);

        source.addEventListener('error', function (e) {
            if (e.readyState == EventSource.CLOSED) {
                console.log("連線關閉");
                
            } else if (e.readyState != undefined) {
                console.log(e.readyState);
            }
        }, false);
        
    } else {
        console.log("瀏覽器不支援SSE!!");
    }
}

function stopSSE() {
	if (source != null) {
		source.close();
		console.log("關閉SSE");
	}
}

function startSwitch() {
	$('#msg_from_server').val('====================== [ Processing ] ======================');
	var obj = new Object();
	
	$.ajax({
		url : _ctx + '/plugin/module/vmswitch/go',
		data : JSON.stringify(obj),
		headers: {
		    'Accept': 'application/json',
		    'Content-Type': 'application/json'
		},
		type : "POST",
		dataType : 'json',
		async: true,
		beforeSend : function(xhr) {
			showProcessing2();
			startSSE();
		},
		complete : function() {
			//hideProcessing();
		},
		success : function(resp) {
			/*
			if (resp.code == '200') {
				alert(resp.message);
				
			} else {
				alert(resp.message);
			}
			*/
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}

function ajaxErrorHandler() {
	alert('連線逾時，頁面將重新導向');
	location.reload();
}