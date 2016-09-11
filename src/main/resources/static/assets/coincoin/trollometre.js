function ChauveMetric() {
	this.name = 'chauve';
	this.nbPostMatching = 0;
	this.nbPostNotMatching = 0;
}
ChauveMetric.prototype.feed = function(post) {
	if (/(single|normal)/i.test(post.nickname)
			|| /(chauve|dégarni|bald|cheveu|crâne|shampoing|peigne|domi)/i
					.test(post.message)) {
		this.nbPostMatching++;
	} else {
		this.nbPostNotMatching++;
	}
}

function SexismeMetric() {
	this.name = 'sexisme';
	this.nbPostMatching = 0;
	this.nbPostNotMatching = 0;
}
SexismeMetric.prototype.feed = function(post) {
	if (/(femme|femelle|fille|gonzesse|mâle|bitch|pute|salope|jupe|connasse)/i
			.test(post.message)) {
		this.nbPostMatching++;
	} else {
		this.nbPostNotMatching++;
	}
}

function PolitiqueMetric() {
	this.name = 'politique';
	this.nbPostMatching = 0;
	this.nbPostNotMatching = 0;
}
PolitiqueMetric.prototype.feed = function(post) {
	if (/(politique|mélanchon|méluchon|jlm|sarko|juppé|droite|gauche)/i
			.test(post.message)) {
		this.nbPostMatching++;
	} else {
		this.nbPostNotMatching++;
	}
}

function LinuxMetric() {
	this.name = 'linux';
	this.nbPostMatching = 0;
	this.nbPostNotMatching = 0;
}
LinuxMetric.prototype.feed = function(post) {
	if (/(linux|unix|wayland|systemd)/i.test(post.message)) {
		this.nbPostMatching++;
	} else {
		this.nbPostNotMatching++;
	}
}

function HipsterMetric() {
	this.name = 'hipster';
	this.nbPostMatching = 0;
	this.nbPostNotMatching = 0;
}
HipsterMetric.prototype.feed = function(post) {
	if (/(hipster|barbe|fixie|houpla)/i.test(post.message)) {
		this.nbPostMatching++;
	} else {
		this.nbPostNotMatching++;
	}
}

function CinemaMetric() {
	this.name = 'cinéma';
	this.nbPostMatching = 0;
	this.nbPostNotMatching = 0;
}
CinemaMetric.prototype.feed = function(post) {
	if (/(ciné|pon|dune)/i.test(post.message)) {
		this.nbPostMatching++;
	} else {
		this.nbPostNotMatching++;
	}
}

 function Trollometre(canvasElement) {
 var self = this;
 this.metrics = [ new ChauveMetric(), new SexismeMetric(),
 new PolitiqueMetric(), new LinuxMetric(), new HipsterMetric(),
 new CinemaMetric() ];
 var data = {
 labels : this.metrics.map(function(metric) {
 return metric.name;
 }),
 datasets : [ {
 backgroundColor : "rgba(255,99,132,0.2)",
 borderColor : "rgba(255,99,132,1)",
 pointBackgroundColor : "rgba(255,99,132,1)",
 pointBorderColor : "#fff",
 pointHoverBackgroundColor : "#fff",
 pointHoverBorderColor : "rgba(255,99,132,1)",
 data : this.metrics
 .map(function(metric) {
 return self.metricToValue(metric);
 })
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
	if (metric.nbPostMatching > 0) {
		var totalPosts = metric.nbPostMatching + metric.nbPostNotMatching;
		return metric.nbPostMatching * 100 / totalPosts;
	} else {
		return 0;
	}
}

Trollometre.prototype.update = function() {
	var self = this;
	this.chart.data.datasets[0].data = this.metrics.map(function(metric) {
		return self.metricToValue(metric);
	});
	this.chart.update();

}
Trollometre.prototype.feed = function(post) {

	this.metrics.map(function(metric) {
		return metric.feed(post);
	});
}
