riot.tag('jb3-rooms-list',
'<ul class="c-menu c-menu--high">\
		  <li class="c-menu__item c-menu__item--divider">Rooms</li>\
		<li each="{ store.rooms }" onclick="{ parent.select }" class="c-menu__item">{ rname }</li>\
</ul>\
',
 function(opts) {
	this.store = opts.store;
	this.select = function(e) {
		$(e.target).addClass('c-menu__item--active').siblings().removeClass('c-menu__item--active');
		this.parent.trigger('select-room', e.item);
	};
}
);