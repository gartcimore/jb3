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

	$('.jb3-posts').on(
			{
				mouseenter : function(event) {
					var postId = $(event.target).data('ref');
					var post = $('#' + postId);
					post.addClass("jb3-highlight");
					$(".jb3-cite[data-ref='" + post.attr('id') + "']")
							.addClass("jb3-highlight");
				},
				mouseleave : function(event) {
					var postId = $(event.target).data('ref');
					var post = $('#' + postId);
					post.removeClass("jb3-highlight");
					$(".jb3-cite[data-ref='" + post.attr('id') + "']")
							.removeClass("jb3-highlight");
				},
				click : function(event) {
					var postContainer = $('html');
					var quoted = $('#' + $(event.target).data('ref'));
					if (quoted.length > 0) {
						postContainer.scrollTop(quoted[0].offsetTop
								- event.clientY + postContainer.offset().top
								+ 10);
					}
				}
			}, ".jb3-cite");
}
new Jb3Archives();
