/**
 * 
 */

/*
 * window.sessionStorage.setItem("key","value")
 * window.sessionStorage.getItem("key")
 */
/*
 * 記錄使用者已勾選的項目，用來當視窗大小改變時觸發DataTable reload重畫後(勾選狀態會reset)，能夠再將使用者先前勾選的項目還原
 * 也用來避免若使用者已開啟Modal視窗，將可能導致抓取不到DataTable勾選的項目
 */
var _DATATABLE_CHECKED_ITEM_ = "DATATABLE_CHECKED_ITEM";
var _DELIVERY_SCRIPT_INFO_ID_ = "DELIVERY_SCRIPT_INFO_ID";
var _DELIVERY_SCRIPT_SYSTEM_VERSION_ = "DELIVERY_SCRIPT_SYSTEM_VERSION";
var _DELIVERY_SCRIPT_DEVICE_MODEL_ = "DELIVERY_SCRIPT_DEVICE_MODEL";
var _DELIVERY_SCRIPT_CODE_ = "DELIVERY_SCRIPT_CODE";
var _DELIVERY_SCRIPT_NAME_ = "DELIVERY_SCRIPT_NAME";
var _DELIVERY_DEVICE_MENU_JSON_STR_ = "DELIVERY_DEVICE_MENU_JSON_STR";
var _DELIVERY_GROUP_ID_ = "DELIVERY_GROUP_ID";
var _DELIVERY_DEVICE_ID_ = "DELIVERY_DEVICE_ID";
var _DELIVERY_DEVICE_GROUP_NAME_ = "DELIVERY_DEVICE_GROUP_NAME";
var _DELIVERY_DEVICE_NAME_ = "DELIVERY_DEVICE_NAME";
var _DELIVERY_REASON_ = "DELIVERY_REASON";
var _DELIVERY_VAR_KEY_ = "DELIVERY_VAR_KEY";
var _DELIVERY_VAR_VALUE_ = "DELIVERY_VAR_VALUE";

var openWindow = null;

$(window).on('load', function() {
    $(".loader").fadeOut("slow");
    
    if(!Modernizr.inputtypes.date) {
    	$("input[type=date]").datepicker({ dateFormat: 'yy-mm-dd' });
    }
});

function closeTabAndGo(_uri) {
	if (openWindow != null) {
		openWindow.close();
	}
	
	window.location = _uri;
}

function clearDialog() {
	$(".ui-dialog").hide()
	$(".ui-widget-overlay").hide()
}

function alert(msg) {
	$('<div class="alert font" />').html(msg).dialog({
		title: "提示訊息",
		show: { effect: "fadeIn", duration: 300 },
		hide: { effect: "fadeOut", duration: 500 },
		modal: true,
		resizable: false,
		open : function() {
			$(".ui-dialog").css('z-index', 9999);
			$(".ui-widget-overlay").css('z-index', 9998);
			$(".ui-button.ui-corner-all.ui-widget.ui-button-icon-only.ui-dialog-titlebar-close").hide();
			$(".ui-button.ui-corner-all.ui-widget").css("font-family", "微軟正黑體");
		},
		buttons : {
			"關閉" : function() {
	            $(this).dialog("close");
	          }
		}
	});
}

function confirm(msg, callback) {
	$('<div class="confirm font" />').html(msg).dialog({
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
	        	  
	        	  var fn = window[callback];
	        	  if (typeof fn === "function") {
	        		  fn();
	        	  }
	          },
	          "取消" : function() {
	            $(this).dialog("close");
	          }
	        }
	});
}

function showProcessing() {
	$(".mask").show();
	$(".processing").show();
}

function hideProcessing() {
	$(".mask").hide();
	$(".processing").hide();
}

function isEmpty(obj) {
    for(var key in obj) {
        if(obj.hasOwnProperty(key))
            return false;
    }
    return true;
}


function bindTrEventForSpecifyTableRadio(dataTable_className, dataTable_radioId) {
	var dataTable = '.'+dataTable_className+' tbody tr';
	var radioBox = '#'+dataTable_radioId;
	
	$(dataTable).click(function(event) {
        if (event.target.tagName !== 'A' && event.target.type !== 'checkbox' && event.target.type !== 'radio') {
        	$(radioBox, this).prop('checked', ($(radioBox, this).is(':checked') ? false : true)).change();
            
            if ($(radioBox, this).attr('type') === 'radio') {
          	  changeTrBgColor($(radioBox, this).get(0));
            }
        }
    });
}

function htmlSpecialChars(ch) {
	if (ch===null) return '';
	ch = ch.replace(/&/g, "&amp;");
	ch = ch.replace(/\"/g, "&quot;");
	ch = ch.replace(/\'/g, "&#039;");
	ch = ch.replace(/</g, "&lt;");
	ch = ch.replace(/>/g, "&gt;");
	return ch;
}


function unHtmlSpecialChars(ch) {
	if (ch===null) return '';
	ch = ch.replace("&amp;", "&");
	ch = ch.replace("&quot;", "\"");
	ch = ch.replace("&#039; ","\'");
	ch = ch.replace("&lt;", "<");
	ch = ch.replace("&gt;", ">");
	return ch;
} 

function paddingLeft(str,lenght){
	if(str.length >= lenght)
	return str;
	else
	return paddingLeft("0" +str,lenght);
}
function paddingRight(str,lenght){
	if(str.length >= lenght)
	return str;
	else
	return paddingRight(str+"0",lenght);
}

