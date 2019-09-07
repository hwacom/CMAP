/**
 * 
 */

$(document).ready(function() {
	initMenuStatus("toggleMenu_plugin", "toggleMenu_plugin_items", "cm_portBlockedRecord");
	
	changeDeviceMenu("queryDevice", $("#queryGroup").val());

	$("#btnSearch_record_web").click(function(e) {
		$('#queryFrom').val("WEB");
		findBlockedPortRecordData();
	});
	
	$("#btnSearch_record_mobile").click(function(e) {
		$('#queryFrom').val("MOBILE");
		$('#collapseExample').collapse('hide');
		findBlockedPortRecordData();
	});
});
