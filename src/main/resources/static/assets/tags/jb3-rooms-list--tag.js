riot.tag('jb3-rooms-list',
'<ul class="c-card c-card--menu u-high">\
		  <li class="c-card__item c-card__item--divider">Rooms</li>\
		<li each="{ store.rooms }" onclick="{ parent.select }" class="c-card__item">{ rname }</li>\
</ul>\
',
 function(opts) {
	this.store = opts.store;
	this.select = function(e) {
		$(e.target).addClass('c-card__item--active').siblings().removeClass('c-card__item--active');
		this.parent.trigger('select-room', e.item);
	};
}
);