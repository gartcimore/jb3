riot
		.tag(
				'jb3-paste-form',
				'\
		<form name="pasteTextForm" class="c-fieldset" action="/api/paste/text" method="post">\
			<label class="c-label c-form-element">\
			  Text:\
			  <textarea name="ptext" class="c-label__field" type="text"></textarea>\
			</label>\
			<input type="submit" class="c-button c-button--primary"" >\
		</form>\
		<div if="{ pastedTextUrl }" class="c-card  c-card--success">\
		  <div class="c-card__content c-card__content--divider">Pasted!</div>\
		  <div class="c-card__content">\
		    <p class="c-paragraph"><a class="c-link" href="{ pastedTextUrl }">{ pastedTextUrl }</a></p>\
		  </div>\
		</div>\
		<div if="{ pastedTextError }" class="c-card  c-card--error">\
		  <div class="c-card__content c-card__content--divider">Error :-(</div>\
		  <div class="c-card__content">\
		    <p class="c-paragraph">{ pastedTextError }</p>\
		  </div>\
		</div>\
		<form name="pasteImageForm" class="c-fieldset" action="/api/paste/image" method="post">\
				<label class="c-label c-form-element">\
					Image:\
					<div class="c-form-element">\
						<input name="pimage" type="file"></input>\
					</div>\
				</label>\
				<input type="submit" class="c-button c-button--primary"" >\
		</form>\
		<div if="{ pastedImageUrl }" class="c-card  c-card--success">\
		  <div class="c-card__content c-card__content--divider">Pasted!</div>\
		  <div class="c-card__content">\
		    <p class="c-paragraph"><a class="c-link" href="{ pastedImageUrl }">{ pastedImageUrl }</a></p>\
		  </div>\
		</div>\
		<div if="{ pastedImageError }" class="c-card  c-card--error">\
		  <div class="c-card__content c-card__content--divider">Error :-(</div>\
		  <div class="c-card__content">\
		    <p class="c-paragraph">{ pastedImageError }</p>\
		  </div>\
		</div>\
		<form name="pasteFileForm" class="c-fieldset" action="/api/paste/file" method="post">\
			<label class="c-label c-form-element">\
			  File:\
			  <input name="pfile" class="c-label__field" type="file"></input>\
			</label>\
			<input type="submit" class="c-button c-button--primary"" >\
			 <progress name="pfileprogress" value="0" max="100"></progress> \
		</form>\
		<div if="{ pastedFileUrl }" class="c-card  c-card--success">\
		  <div class="c-card__content c-card__content--divider">Pasted!</div>\
		  <div class="c-card__content">\
		    <p class="c-paragraph"><a class="c-link" href="{ pastedFileUrl }">{ pastedFileUrl }</a></p>\
		  </div>\
		</div>\
		<div if="{ pastedFileError }" class="c-card  c-card--error">\
		  <div class="c-card__content c-card__content--divider">Error :-(</div>\
		  <div class="c-card__content">\
		    <p class="c-paragraph">{ pastedFileError }</p>\
		  </div>\
		</div>\
',
				function(opts) {
					var self = this;
					$(self.pasteTextForm).ajaxForm(
							{
								success : function(data) {
									self.pastedTextError = null;
									self.pastedTextUrl = data.url;
									self.update();
								},
								error: function() {
									self.pastedTextError = 'Error during text upload';
									self.pastedTextUrl = null;
									self.update();
								}
							});
					$(self.pasteFileForm).ajaxForm(
							{
								success : function(data) {
									self.pastedFileError = null;
									self.pastedFileUrl = data.url;
									self.update();
								},
								uploadProgress : function(event, position,
										total, percentComplete) {
									self.pfileprogress.val(percentComplete);
								},
								error: function() {
									self.pastedFileError = 'Error during file upload';
									self.pastedFileUrl = null;
									self.update();
								}
							});
					$(self.pimage).picEdit({
						maxWidth : 'auto',
						formSubmitted : function(xhr) {
							if (xhr.status == 200) {
								var data = JSON.parse(xhr.response);
								self.pastedImageError = null;
								self.pastedImageUrl = data.url;
							} else {
								self.pastedImageError = 'Error during image upload';
								self.pastedImageUrl = null;
							}
							self.update();
						}
					});
				});