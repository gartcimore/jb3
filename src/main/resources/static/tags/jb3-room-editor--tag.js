riot.tag(
				'jb3-room-editor',
'<fieldset class="c-fieldset">\
		<label class="c-label c-form-element">\
		  Name:\
		  <input name="rname" class="c-label__field" type="text">\
		</label>\
		<label class="c-label c-form-element">\
		  Login:\
		  <input name="rlogin" class="c-label__field" type="text">\
		</label>\
		<label class="c-label c-form-element">\
		  Password / Cookie / ... :\
		  <input name="rauth" class="c-label__field" type="text">\
		</label>\
		<button class="c-button c-button--primary"" onclick="{ save }">Save</button>\
		<button class="c-button c-button--secondary" onclick="{ del }">Delete</button>\
</fieldset>\
',
				function(opts) {
					this.on('edit-room', function(room) {
						this.rname.value = room.rname;
						this.rlogin.value = room.rlogin || '';
						this.rauth.value = room.rauth || '';
					});
					this.save = function(e) {
						this.trigger('save-room', {
							rname : this.rname.value,
							rlogin : this.rlogin.value,
							rauth : this.rauth.value
						});
					}
					this.del = function(e) {
						this.trigger('delete-room', {
							rname : this.rname.value,
						});
						this.rname.value = '';
						this.rlogin.value = '';
						this.rauth.value = '';
					}
				});