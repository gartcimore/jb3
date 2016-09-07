riot.tag(
				'jb3-room-editor',
'<fieldset class="c-fieldset">\
		<label class="c-label c-form-element" if="{ !rname.value }">\
		  Name:\
		  <input name="rname" class="c-label__field" type="text">\
		</label>\
		<label class="c-label c-form-element" if="{ rname.value }">\
		  Login:\
		  <input name="rlogin" class="c-label__field" type="text">\
		</label>\
		<label class="c-label c-form-element" if="{ rname.value }">\
		  Password / Cookie / ... :\
		  <input name="rauth" class="c-label__field" type="text">\
		</label>\
	    <button if="{ !rname.value }" class="c-button c-button--primary" onclick="{ save }">Create new room</button>\
		<button if="{ rname.value }" class="c-button c-button--primary" onclick="{ save }">Save</button>\
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