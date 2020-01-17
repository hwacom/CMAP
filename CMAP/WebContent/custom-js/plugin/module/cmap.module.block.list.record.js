/**
 * 
 */

$(document).ready(function() {
	initMenuStatus("toggleMenu_plugin", "toggleMenu_plugin_items", "cm_blockedListRecord");

	$("#btnSearch_record_web").click(function(e) {
		$('#queryFrom').val("WEB");
		findBlockedPortRecordData('B');
		findBlockedIpRecordData('B');
		findBlockedMacRecordData('B');
	});
	
	$("#btnSearch_record_mobile").click(function(e) {
		$('#queryFrom').val("MOBILE");
		$('#collapseExample').collapse('hide');
		findBlockedPortRecordData('B');
		findBlockedIpRecordData('B');
		findBlockedMacRecordData('B');
	});
});
