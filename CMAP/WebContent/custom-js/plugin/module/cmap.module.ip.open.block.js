/**
 * 
 */
var resultTable_blockIpRecord;

$(document).ready(function() {
	
});



//查詢按鈕動作
function findData(from) {
	$(".myTableSection").show();
	
	if (typeof resultTable_blockIpRecord !== "undefined") {
		
	} else {
		resultTable_blockIpRecord = $('#resultTable_blockIpRecord').DataTable({
			"autoWidth" 	: true,
			"paging" 		: true,
			"bFilter" 		: true,
			"ordering" 		: true,
			"info" 			: true,
			"serverSide" 	: false,
			"bLengthChange" : true,
			"pagingType" 	: "full",
			"processing" 	: true,
			"scrollX"		: true,
			"scrollY"		: dataTableHeight,
			"scrollCollapse": true,
			"pageLength"	: 100,
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
		});
	}
}