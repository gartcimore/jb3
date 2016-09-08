riot.tag(
				'jb3-room-editor',
'<fieldset class="c-fieldset">\
		<h2 class="c-heading c-heading--small" if="{ rname.value }">{ rname.value }</h2>\
		<label class="c-label c-form-element" if="{ rname.value == \'dlfp\' }">\
		  Login:\
		  <input name="rlogin" class="c-label__field" type="text">\
		</label>\
		<label class="c-label c-form-element" if="{ rname.value == \'dlfp\' }">\
		  Password / Cookie / ... :\
		  <input name="rauth" class="c-label__field" type="text">\
		</label>\
		<div class="c-input-group" if="{ !rname.value }">\
				<input name="rname" class="c-field" type="text" placeholder="Type new room\'s name">\
			    <button if="{ !rname.value }" class="c-button c-button--primary" onclick="{ save }">Create new room</button>\
		</div>\
		<div class="c-form-element" if="{ !rname.value }" >\
				<button class="c-button c-button--secondary" onclick="{ resetAll }">Reset all rooms</button>\
		</div>\
		<button if="{ rname.value == \'dlfp\' }" class="c-button c-button--primary" onclick="{ save }">Save</button>\
		<button if="{ rname.value }" class="c-button" onclick="{ cancel }">Cancel</button>\
		<button if="{ rname.value }" class="c-button c-button--secondary" onclick="{ del }">Delete</button>\
</fieldset>\
',
				function(opts) {
					var self = this;
					this.on('edit-room', function(room) {
						this.rname.value = room.rname;
						this.rlogin.value = room.rlogin || '';
						this.rauth.value = room.rauth || '';
						this.update();
					});
					this.save = function(e) {
						if(this.rname.value) {
							this.trigger('save-room', {
								rname : this.rname.value,
								rlogin : this.rlogin.value,
								rauth : this.rauth.value
							});
							this.rname.value = '';
							this.rlogin.value = '';
							this.rauth.value = '';
							this.update();
						}
					}
					this.cancel = function(e) {
						this.trigger('cancel-edit-room');
						this.rname.value = '';
						this.rlogin.value = '';
						this.rauth.value = '';
						this.update();
					}
					this.resetAll = function(e) {
						this.trigger('reset-all-rooms');
						this.rname.value = '';
						this.rlogin.value = '';
						this.rauth.value = '';
						this.update();
					}
					this.del = function(e) {
						this.trigger('delete-room', {
							rname : this.rname.value,
						});
						this.rname.value = '';
						this.rlogin.value = '';
						this.rauth.value = '';
						this.update();
					}
				});