function TrollMetric(name, options) {
	this.name = name;
	this.nicknameRegex = options.nickname;
	this.messageRegex = options.message;
	this.nbPostMatching = 0;
}

TrollMetric.prototype.feed = function(post) {
	if ((this.nicknameRegex && this.nicknameRegex.test(post.nickname))
			|| (this.messageRegex && this.messageRegex.test(post.message))) {
		this.nbPostMatching++;
	}
}

function Trollometre(canvasElement) {
	var self = this;
	this.nbPost = 0;
	this.metrics = [
			new TrollMetric(
					"chauve",
					{
						nickname : /\b(single|normal)\b/i,
						message : /\b(single|bald|cheveu|crâne|domi|dégarni|homophobe|lourd|math|peigne|pédale|shampoing|vtt|vélo)\b/i
					}),
			new TrollMetric(
					"sexisme",
					{
						nickname : /\b(adonai|flanagan)\b/i,
						message : /\b(sexisme|adonai|boobs|couple|femelle|féminisme|féministe|genre|jupe|linge|male|ménage|pantalon|transexuel|vaisselle|soumise|patriarcat|matriarcat)\b/i
					}),
			new TrollMetric(
					"politique",
					{
						nickname : /\b(adonai|flanagan|pap)\b/i,
						message : /\b(balladur|banlieue|banque|banquier|capitaliste|chirac|communisme|communiste|europe|facho|hitler|hollande|insurrection|jean marie|jlm|juppé|lepen|libéral|libéralisme|libre échange|macron|marine|melanchon|melancon|meluche|meluchon|nabot|nain|occitanie|pap|patrie|pauvre|politique|prolétaire|prolo|racisme|raciste|réact|réaction|réactionnaire|république|riche|rouge|sarko|sarkozy|socialisme|staline|villepin)\b/i
					}),
			new TrollMetric(
					"linux",
					{
						message : /\b(archlinux|bash|daubian|debian|gcc|gnome|gnu|hurd|kde|linux|microsoft|redcrap|redhat|rms|shell|slackware|stallman|systemd|wayland|x11|xfce|xwindow)\b/i
					}),
			new TrollMetric(
					"hipster",
					{
						nickname : /\b(houplaboom)\b/i,
						message : /\b(apple|barbe|bio|dae|équitable|fixie|graine|hipster|houpla|houplaboom|quinoa|régime|tatouage|tofu|végétarien)\b/i
					}),
			new TrollMetric(
					"cinéma",
					{
						message : /\b(allocine|blockbuster|camera|chrisix|cinema|cinéma|dune|imdb|lynch|nanar|navet|pon|réalisateur|woody\sallen)\b/i
					}),
			new TrollMetric(
					"dev",
					{
						message : /\b(ant|architecte|autotools|C|C\+\+|cloud|cmake|css|dev|fullstack|html|java|javascript|make|maven|perl|php|python|ruby|scalable)\b/i
					}),
			new TrollMetric(
					"coincoin",
					{
						message : /\b(b3|backend|bouchot|c2|coincoin|dlfp|euromussels|gaycoincoin|gcc|gcoincoin|jb3|miaoli|olcc|wmcc)\b/i
					}),
			new TrollMetric(
					"windows",
					{
						message : /\b(windows|gate|surface|windaube|virus|malware|drm|microsoft)\b/i
					}),
			new TrollMetric(
					"alcool",
					{
						nickname : /\b(joalland)\b/i,
						message : /\b(alcool|bar|bière|Clément|diplomatico|joalland|jojo|longueteau|menthe|mojito|pirate|piraterie|rhum|ti'punch|verre|vin|whisky)\b/i
					}) ];
	var data = {
		labels : [],
		datasets : [ {
			backgroundColor : "rgba(255,99,132,0.2)",
			borderColor : "rgba(255,99,132,1)",
			pointBackgroundColor : "rgba(255,99,132,1)",
			pointBorderColor : "#fff",
			pointHoverBackgroundColor : "#fff",
			pointHoverBorderColor : "rgba(255,99,132,1)",
			data : []
		} ]
	};
	this.chart = new Chart(canvasElement, {
		type : 'radar',
		data : data,
		options : {
			legend : {
				display : false
			},
			scale : {
				display : true,
				ticks : {
					display : false
				}
			}
		}
	});
}

Trollometre.prototype.metricToValue = function(metric) {
	if (this.nbPost > 0) {
		return metric.nbPostMatching * 100 / this.nbPost;
	} else {
		return 0;
	}
}

Trollometre.prototype.update = function() {
	var self = this;
	this.metrics.sort(function(a, b) {
		return b.nbPostMatching - a.nbPostMatching;
	});
	var topMetrics = this.metrics.slice(0, 5);
	this.chart.data.labels = topMetrics.map(function(metric) {
		return metric.name;
	})
	this.chart.data.datasets[0].data = topMetrics.map(function(metric) {
		return self.metricToValue(metric);
	});
	this.chart.update();

}
Trollometre.prototype.feed = function(post) {
	this.nbPost++;
	this.metrics.map(function(metric) {
		return metric.feed(post);
	});
}
