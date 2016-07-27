function Jb3Archives() {
	$('#button-next-page').click(function() {
		var page = $("#search-form input[name='page']");
		page.val(parseInt(page.val()) + 1);
		$('#search-form').submit();
	});
	$('#button-previous-page').click(function() {
		var page = $("#search-form input[name='page']");
		page.val(parseInt(page.val()) - 1);
		$('#search-form').submit();
	});
	jb3_common.initHighlight();
    jb3_common.initTotozLazyLoading();
}
new Jb3Archives();
