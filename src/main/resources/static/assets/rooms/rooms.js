function Jb3Rooms() {
	var self = this;
	self.rooms = jb3_common.getRooms();
	riot.observable(self);
	var roomList = riot.mount('jb3-rooms-list', {store: self})[0];
	var roomEditor = riot.mount('jb3-room-editor')[0];

	roomList.on('select-room', function(room) {
		roomEditor.trigger('edit-room', room);
	});

	roomEditor.on('save-room', function(savedRoom) {
		self.rooms = jb3_common.getRooms();
		var existingIndex = self.rooms.findIndex(function(r) {
			return r.rname == savedRoom.rname;
		});
		if(existingIndex >= 0) {
			self.rooms[existingIndex] = savedRoom;
		} else {
			self.rooms.push(savedRoom);
		}
		self.rooms.sort(function(a, b) {
			return a.rname.localeCompare(b.rname);
		});
		localStorage.rooms = JSON.stringify(self.rooms);
		roomList.update();
		roomEditor.update();
	});
	
	roomEditor.on('cancel-edit-room', function() {
		self.rooms = jb3_common.getRooms();
		roomList.update();
	});
	
	roomEditor.on('delete-room', function(roomToDelete) {
		self.rooms = jb3_common.getRooms().filter(function(r) {
			return r.rname != roomToDelete.rname;
		});
		localStorage.rooms = JSON.stringify(self.rooms);
		roomList.update();
		roomEditor.update();
	});
	
	roomEditor.on('reset-all-rooms', function(roomToDelete) {
		self.rooms = jb3_common.getDefaultRooms();
		localStorage.rooms = JSON.stringify(self.rooms);
		roomList.update();
		roomEditor.update();
	});
	
}
new Jb3Rooms();
