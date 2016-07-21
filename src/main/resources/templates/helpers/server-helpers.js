Handlebars.registerHelper('taiste', function() {
  return 'Ceci est un taiste!';
});

Handlebars.registerHelper('format-message', function(message) {
	  return new Handlebars.SafeString(jb3_post_to_html.parse(message));
});