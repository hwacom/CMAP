/**
 * 
 */

$(document).ready(function() {
	initMenuStatus("toggleMenu_setting", "toggleMenu_setting_items", "st_emailUpdate");

	adjustHeight();
	
	$(window).resize(_.debounce(function() {
		adjustHeight();
		
	}, 1000));
	
	/*getLoginUri();*/
	closeOpenWindow();
	getPrtgUri(URI_EMAIL_UPDATE, OPEN_METHOD_IFRAME);
});
