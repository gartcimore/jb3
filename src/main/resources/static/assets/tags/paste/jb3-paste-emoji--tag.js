var jb3PasteEmojiConstructor = function () {
    var self = this;
    self.clear = function () {
        self.emojiError = null;
        self.emojiList = null;
        self.pasted = null;
    };
    self.submit = function (event) {
        event.preventDefault();
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function (event) {
            if (xhr.readyState === 4) {
            	self.pasted = "";
                if (xhr.status === 200) {
                    self.emojiList = JSON.parse(xhr.response);
                } else {
                    self.emojiError = 'Error during emoji search';
                    self.emojiList = null;
                }
                self.trigger('paste-content-changed');
                self.update();
            }
        };
        xhr.open("GET", "/api/paste/emoji/search?terms=" + encodeURIComponent(self.terms.value));
        xhr.send();
        return false;
    };
    self.selectEmoji = function (event) {
        self.pasted = "";
        self.emojiList.forEach(function (emoji) {
            if (emoji.name === event.currentTarget.dataset.name) {
                emoji.selected = !emoji.selected;
            }
            if (emoji.selected) {
                self.pasted = self.pasted.concat(emoji.character);
            }
        });
        self.trigger('paste-content-changed');
        self.update();
    };
    self.on('updated', function () {
        self.terms.focus();
    });
};

var jb3PasteEmojiStyles = '\
    .jb3-paste-emoji-selected {\
        background-color: #e5eaec;\
        border-radius: 4px;\
    }\
';

var jb3PasteEmojiTemplate = '\
<form name="pasteEmojiForm" class="c-fieldset" onsubmit="{ submit }">\
        <div class="o-form-element">\
                <input name="terms" class="c-field c-field--label" type="text"></input>\
        </div>\
        <input type="submit" class="c-button c-button--info" value="Search" >\
</form>\
<div if="{ emojiList }"class="o-grid  o-grid--wrap" >\
    <virtual each="{ emojiList }" >\
        <div data-name="{ name }" class="{ o-grid__cell: true, jb3-paste-emoji-selected: selected}" onclick="{ selectEmoji }">\
            <div>{ character }</div>\
            <div>{ name }</div>\
        </div>\
    </virtual>\
</div>\
<div if="{ emojiError }" class="c-card">\
  <div class="c-card__item c-card__item--divider c-card__item--error">Error :-(</div>\
  <div class="c-card__item">\
    <p class="c-paragraph">{ emojiError }</p>\
  </div>\
</div>\
';


riot.tag('jb3-paste-emoji', jb3PasteEmojiTemplate, jb3PasteEmojiStyles, jb3PasteEmojiConstructor);