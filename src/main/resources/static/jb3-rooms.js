jb3_rooms = {
    init: function () {
        var records = jb3_common.getRooms();
        $('#jb3-rooms-grid').w2grid({
            name: 'Rooms',
            header: 'List of rooms',
            fixedBody: false,
            show: {
                toolbar: true,
                toolbarAdd: true,
                toolbarEdit: true,
                toolbarDelete: true,
                toolbarSave: true,
                footer: true
            },
            columns: [
                {field: 'rname', caption: 'Name', size: '30%', editable: {type: 'text'}}
            ],
            records: records,
            onAdd: function () {
                var recid = 1;
                if (this.records.length > 0) {
                    recid = (Math.max.apply(Math, this.find({}))) + 1;
                }
                this.add({recid: recid, rname: "room_" + recid});
            }
        }).on({type: 'submit', execute: 'after'}, function () {
            localStorage.rooms = JSON.stringify(this.records);
        });


    }
};
jb3_rooms.init();