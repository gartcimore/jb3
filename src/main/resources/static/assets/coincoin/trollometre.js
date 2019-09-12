class TrollMetric {
	constructor(name, options) {
	    this.name = name;
	    this.nicknameRegex = options.nickname;
	    this.messageRegex = options.message;
	    this.nbPostMatching = 0;
	}
	
	feed(post) {
	    if ((this.nicknameRegex && this.nicknameRegex.test(post.nickname))
	            || (this.messageRegex && this.messageRegex.test(post.message))) {
	        this.nbPostMatching++;
	    }
	}
}

class Trollometre {
	constructor(canvasElement) {
	    this.nbPost = 0;
	    this.metricsByRoom = {};
	    var data = {
	        labels: [],
	        datasets: [{
	                backgroundColor: "rgba(255,99,132,0.2)",
	                borderColor: "rgba(255,99,132,1)",
	                pointBackgroundColor: "rgba(255,99,132,1)",
	                pointBorderColor: "#fff",
	                pointHoverBackgroundColor: "#fff",
	                pointHoverBorderColor: "rgba(255,99,132,1)",
	                data: []
	            }]
	    };
	    this.chart = new Chart(canvasElement, {
	        type: 'radar',
	        data: data,
	        options: {
	            legend: {
	                display: false
	            },
	            scale: {
	                display: true,
	                ticks: {
	                    display: false
	                }
	            }
	        }
	    });
	}

	getMetricsForRoom(room) {
	    return this.metricsByRoom[room] = this.metricsByRoom[room] || [
	        new TrollMetric(
	                "chauve",
	                {
	                    nickname: /\b(single|normal)\b/i,
	                    message: /\b(single|bald|cheveux?|crânes?|domi|dégarnis?|homophobes?|lourds?|maths?|peignes?|pédales?|shampoo?ings?|vtts?|vélos?)\b/i
	                }),
	        new TrollMetric(
	                "sexisme",
	                {
	                    nickname: /\b(adonai|flanagan)\b/i,
	                    message: /\b(sexismes?|adonai|boobs|couples?|femelles?|féminismes?|féministes?|genres?|jupes?|linges?|males?|ménages?|pantalons?|transexuels?|vaisselles?|soumises?|patriarcats?|matriarcats?)\b/i
	                }),
	        new TrollMetric(
	                "politique",
	                {
	                    nickname: /\b(adonai|flanagan|pap)\b/i,
	                    message: /\b(balladur|banlieues?|banques?|banquiers?|capitalistes?|classes?|chirac|communismes?|communistes?|coppé|europes?|fachos?|hitler|hollande|insurrections?|jean marie|jlm|juppé|lepen|libérals?|libéralismes?|libre échange|macron|marine|mélenchon|melanchon|melanchon|melancon|meluche|meluchon|nabots?|nains?|occitanie|pap|patries?|pauvres?|politiques?|prolétaires?|prolos?|racismes?|racistes?|réacts?|réactions?|réactionnaires?|républiques?|riches?|rouges?|sarko|sarkozy|socialismes?|staline|totalitaires?|uniformes?|villepin)\b/i
	                }),
	        new TrollMetric(
	                "linux",
	                {
	                    message: /\b(archlinux|bash|daubian|debian|gcc|gnome|gnu|hurd|kde|linux|microsoft|redcrap|redhat|rms|shell|slackware|stallman|systemd|wayland|x11|xfce|xwindow)\b/i
	                }),
	        new TrollMetric(
	                "hipster",
	                {
	                    nickname: /\b(houplaboom)\b/i,
	                    message: /\b(apple|barbes?|bio|dae|équitables?|fixie|graines?|hipsters?|houpla(boom)?|quinoa|régimes?|tatouages?|tofu|végans?|végétariens?)\b/i
	                }),
	        new TrollMetric(
	                "cinéma",
	                {
	                    message: /\b(allocine|blockbusters?|caméras?|chrisix|cinemas?|cinémas?|dune|films?|flims?|imdb|lynch|nanars?|navets?|pon|réalisateur?|woody\sallen)\b/i
	                }),
	        new TrollMetric(
	                "dev",
	                {
	                    message: /\b(ant|architectes?|autotools|C\+\+|cloud|cmake|css|dev|fullstack|html|java|javascript|make|maven|perl|php|python|ruby|scalable|sysadmins?)\b/i
	                }),
	        new TrollMetric(
	                "coincoin",
	                {
	                    message: /\b(b[3³]|backends?|bouchots?|c[2²]|coincoins?|dlfp|batavie|euromussels|gaycoincoin|gcc|gcoincoin|jb3|miaoli|olcc|wmcc|quteqoin|taab|BML)\b/i
	                }),
	        new TrollMetric(
	                "windows",
	                {
	                    message: /\b(windows|gate|surface|windaube|virus|malware|drm|microsoft|explorer)\b/i
	                }),
	        new TrollMetric(
	                "alcool",
	                {
	                    nickname: /\b(joalland)\b/i,
	                    message: /\b(alcools?|bars?|bières?|Clément|diplomatico|joalland|jojo|longueteau|menthes?|mojitos?|pirates?|pirateries?|rhums?|punch|verres?|vins?|whisky)\b/i
	                })];
	}

	metricToValue(metric) {
	    if (this.nbPost > 0) {
	        return metric.nbPostMatching * 100 / this.nbPost;
	    } else {
	        return 0;
	    }
	}

	update(room) {
	    var metrics = this.getMetricsForRoom(room);
	    metrics.sort((a, b) => {
	        return b.nbPostMatching - a.nbPostMatching;
	    });
	    var topMetrics = metrics.slice(0, 5);
	    this.chart.data.labels = topMetrics.map((metric) => {
	        return metric.name;
	    });
	    this.chart.data.datasets[0].data = topMetrics.map((metric) => {
	        return this.metricToValue(metric);
	    });
	    this.chart.update();
	
	}

	feed(post) {
	    this.nbPost++;
	    var metrics = this.getMetricsForRoom(post.room);
	    metrics.map((metric) => {
	        return metric.feed(post);
	    });
	}

}
