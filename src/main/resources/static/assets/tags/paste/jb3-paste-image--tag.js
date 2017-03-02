var jb3PasteImageConstructor = function () {
    var self = this;
    self.clear = function () {
        self.pastedImageError = null;
        self.pastedImageUrl = null;
    };
    $(self.pimage).picEdit({
        maxWidth: 'auto',
        formSubmitted: function (xhr) {
            if (xhr.status === 200) {
                var data = JSON.parse(xhr.response);
                self.pastedImageError = null;
                self.pastedImageUrl = data.url;
            } else {
                self.pastedImageError = 'Error during image upload';
                self.pastedImageUrl = null;
            }
            self.update();
            if (self.pastedResult && self.pastedResult.scrollIntoView) {
                self.pastedResult.scrollIntoView();
            }
        }
    });
};

var jb3PasteImageStyles = '\
';

var jb3PasteImageTemplate = '\
<form class="c-fieldset" action="/api/paste/image" method="post">\
    <div class="o-form-element">\
            <input name="pimage" type="file"></input>\
    </div>\
    <input type="submit" class="c-button c-button--info"" >\
</form>\
<div name="pastedResult">\
    <div if="{ pastedImageUrl }" class="c-card">\
        <div class="c-card__item c-card__item--divider c-card__item--success">Pasted!</div>\
        <div class="c-card__item">\
        <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedImageUrl }" target="_blank">{ pastedImageUrl }</a></p>\
    </div>\
    <div if="{ pastedImageError }" class="c-card">\
        <div class="c-card__item c-card__item--divider c-card__item--error">Error :-(</div>\
        <div class="c-card__item">\
        <p class="c-paragraph">{ pastedImageError }</p>\
    </div>\
</div>\
';

riot.tag('jb3-paste-image', jb3PasteImageTemplate, jb3PasteImageStyles, jb3PasteImageConstructor);