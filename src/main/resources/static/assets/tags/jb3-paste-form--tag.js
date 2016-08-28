riot
		.tag(
				'jb3-paste-form',
				'\
		<div class="c-tabs">\
		    <div class="c-tabs__headings">\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'text\' }" data-tab="text" onclick="{ selectTab }">Text</div>\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'image\' }" data-tab="image" onclick="{ selectTab }">Image</div>\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'file\' }" data-tab="file" onclick="{ selectTab }">File</div>\
		     </div>\
			<div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'text\' }">\
				<form name="pasteTextForm" class="c-fieldset" action="/api/paste/text" method="post">\
					<div class="c-form-element">\
						<textarea name="ptext" class="c-field" type="text"></textarea>\
					</div>\
					<input type="submit" class="c-button c-button--primary"" >\
				</form>\
				<div if="{ pastedTextUrl }" class="c-card  c-card--success">\
				  <div class="c-card__content c-card__content--divider">Pasted!</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph"><a class="c-link jb3-pasted-url" href="{ pastedTextUrl }">{ pastedTextUrl }</a></p>\
				  </div>\
				</div>\
				<div if="{ pastedTextError }" class="c-card  c-card--error">\
				  <div class="c-card__content c-card__content--divider">Error :-(</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph">{ pastedTextError }</p>\
				  </div>\
				</div>\
			</div>\
			<div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'image\' }">\
				<form name="pasteImageForm" class="c-fieldset" action="/api/paste/image" method="post">\
					<div class="c-form-element">\
						<input name="pimage" type="file"></input>\
					</div>\
					<input type="submit" class="c-button c-button--primary"" >\
				</form>\
				<div if="{ pastedImageUrl }" class="c-card  c-card--success">\
				  <div class="c-card__content c-card__content--divider">Pasted!</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedImageUrl }">{ pastedImageUrl }</a></p>\
				  </div>\
				</div>\
				<div if="{ pastedImageError }" class="c-card  c-card--error">\
				  <div class="c-card__content c-card__content--divider">Error :-(</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph">{ pastedImageError }</p>\
				  </div>\
				</div>\
			</div>\
			<div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'file\' }">\
				<form name="pasteFileForm" class="c-fieldset" action="/api/paste/file" method="post">\
					<div class="c-form-element">\
						<input name="pfile" class="c-label__field" type="file"></input>\
					</div>\
					<input type="submit" class="c-button c-button--primary" >\
					<progress value="0" max="100"></progress>\
				</form>\
				<div if="{ pastedFileUrl }" class="c-card  c-card--success">\
				  <div class="c-card__content c-card__content--divider">Pasted!</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedFileUrl }">{ pastedFileUrl }</a></p>\
				  </div>\
				</div>\
				<div if="{ pastedFileError }" class="c-card  c-card--error">\
				  <div class="c-card__content c-card__content--divider">Error :-(</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph">{ pastedFileError }</p>\
				  </div>\
				</div>\
			</div>\
		</div>\
',
				function(opts) {
					var self = this;
					this.selectedTab = 'text';
					self.selectTab = function(e) {
						self.selectedTab = e.target.dataset.tab
					};
					self.clear = function() {
						self.pastedTextError = null;
						self.pastedTextUrl = null;
						self.pastedImageError = null;
						self.pastedImageUrl = null;
						self.pastedFileError = null;
						self.pastedFileUrl = null;
						this.update();
					}
					$(self.pasteTextForm).ajaxForm({
						success : function(data) {
							self.pastedTextError = null;
							self.pastedTextUrl = data.url;
							self.update();
						},
						error : function() {
							self.pastedTextError = 'Error during text upload';
							self.pastedTextUrl = null;
							self.update();
						}
					});
					$(self.pasteFileForm)
							.ajaxForm(
									{
										success : function(data) {
											self.pastedFileError = null;
											self.pastedFileUrl = data.url;
											self.update();
										},
										uploadProgress : function(event,
												position, total,
												percentComplete) {
											$(self.pasteFileForm).find('progress')
													.val(percentComplete);
										},
										error : function() {
											self.pastedFileError = 'Error during file upload';
											self.pastedFileUrl = null;
											self.update();
										}
									});
					$(self.pimage)
							.picEdit(
									{
										maxWidth : 'auto',
										formSubmitted : function(xhr) {
											if (xhr.status == 200) {
												var data = JSON
														.parse(xhr.response);
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