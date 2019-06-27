/**
 * 
 */

var timer, startTime, timer_start, timer_end;

$(document).ready(function() {
	if ($("#isAll").val() === "Y") {
		initMenuStatus("toggleMenu_prtg", "toggleMenu_prtg_items", "mp_netFlowCurrentRanking_all");
		
	} else {
		initMenuStatus("toggleMenu_prtg", "toggleMenu_prtg_items", "mp_netFlowCurrentRanking");
	}
	
});
