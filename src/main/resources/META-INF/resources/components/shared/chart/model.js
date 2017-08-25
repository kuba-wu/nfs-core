var ChartModel = function(params) {
	
	var self = this;
	
	self.containerId = params.view+"_chart";
	
	self.chartNumber = 0;
	self.currentData = {};
	self.charts = ko.observableArray();
	
	self.system = ko.observable().subscribeTo(params.view+"_system");
	self.colors = ko.observable().subscribeTo(params.view+"_colors");
	self.currentSimRun = null;
	
	ko.postbox.subscribe(params.view+"_simRun", function(simRun) {
		
		self.currentSimRun = simRun;
		
		if (!self.system()) {
			MainModel.displayErrorAlert("Please select a task/exercise/solution first.");
			return ;
		}
		
		var simulation = {
			environment : simRun.environment,
			system : JSON.parse(self.system())
		};
		self.loadData(self.colors(), self.system(), JSON.stringify(simulation));
	});
	
	self.show = function() {
		$('#'+self.containerId).collapse('show');
	};
	
	self.mergeChartData = function(chartData) {
		var mergedData = [];
		var firstSerie = chartData[0].values;
		for (var i=0; i < firstSerie.length; i++) {
			var oneEntry = {};
			oneEntry.time = firstSerie[i].time+"";
			for (var j=0; j < chartData.length; j++) {
				oneEntry[j+""] = chartData[j].values[i].product;
			}
			if (!(i % self.currentSimRun.environment.timeScale)) {
				oneEntry.bullet = "round";
			}
			mergedData.push(oneEntry);
		}
		return mergedData;
	};
	
	self.createGraphs = function(chartData, colors) {
		
		var graphs = [];
		for (var i=0; i < chartData.length; i++) {		
			var product = chartData[i].product;
			graphs.push(self.createGraph(i, product, colors[product]));
		}
		return graphs;
	};
	
	self.createGraph = function(field, name, color) {
			var graph = {};
			
			graph.type = "smoothedLine";
			graph.valueField = field;
			graph.title = name+" concentration";
			
			graph.bulletField = "bullet";
			graph.bulletBorderAlpha =  1;
			graph.bulletSize = 0;
			graph.bulletColor = "#FFFFFF";
			graph.bulletBorderThickness = 1;
			graph.useLineColorForBulletBorder = true;
			
			graph.lineColor = color;
			graph.lineThickness = 2;
			
			return graph;
	},
	
	self.createExportFileName = function() {
		var runTimeString = "[runTime-"+self.currentSimRun.environment.runTime+"]";
		var timeScaleString = "[timeScale-"+self.currentSimRun.environment.timeScale+"]";
		var system = JSON.parse(self.system());
		var names = ["nfs", system.name || "default", "system", runTimeString, timeScaleString];
		return names.join("_");
	};
	
	self.getExportConfig = function(dataSource) {
		return {
		    menuTop: '53px',
		    menuLeft: 'auto',
		    menuRight: '9px',
		    menuBottom: 'auto',
		    backgroundColor: "#efefef",
		    menuItemStyle	: {
                backgroundColor			: '#ffffff',
                rollOverBackgroundColor	: '#DDDDDD'
            },
		    menuItems: [{
		        textAlign: 'center',
		        onclick: function(a) {
		            return false;
		        },
		        icon: 'webjars/amcharts/3.4.7/images/export.png',
		        iconTitle: 'Save chart',
		        items: [{
		            title: 'PNG',
		            format: 'png',
		            fileName: dataSource.createExportFileName()
		        }, {
		            title: 'SVG',
		            format: 'svg',
		            fileName: dataSource.createExportFileName()
		        },{
		            title: 'TXT',
		            format: 'txt',
		            fileName: dataSource.createExportFileName()
		        }]
		    }],
		    rawData: function() {return self.createOutputData(self.currentData);}
		};
	};
	
	self.createOutputData = function(chartData) {
		
		var result = "";
		for (var i=0; i < chartData.length; i++) {
			var data = chartData[i];
			result += data.product+" concentration: ";
			var output = [];
			for (var j=0, n=data.values.length;j < n; j++) {
				output[j] = data.values[j].product;
			}
			result += output.join(', ')+"\r\n\r\n";
		}
		return result;
	};
	
	self.removeAll = function() {
	    self.charts.removeAll();
	}
	
	self.remove = function(chart) {
		self.charts.remove(chart);
	};
	
	self.getDataUrl = function() {
		return ("api/system/simulation");
	};
	
	self.loadData = function(colors, systemJs, simulationRun) {
		
		var url = self.getDataUrl();
		$.ajax(url, {
			data : simulationRun,
			success : function(chartData) {

				self.show();
				
				var mergedData = self.mergeChartData(chartData);
				var graphs = self.createGraphs(chartData, colors);
				var legend = new AmCharts.AmLegend();
                legend.valueWidth = 100;
				
				var innerId = params.view+"chartDiv"+self.chartNumber;
				var outerId = params.view+"chartOuterDiv"+self.chartNumber;
				self.chartNumber+=1;
		
				var systemModel = JSON.parse(systemJs);
				
				self.charts.push({
					innerId : innerId, 
					outerId : outerId,
					system : systemModel});
				
				AmCharts.makeChart(innerId, {
					type : "serial",
					theme : "none",
					marginLeft : 20,
					pathToImages : "webjars/amcharts/3.4.7/images/",
					autoMarginOffset : 5,
					marginTop : 0,
					marginRight : 10,
					zoomOutButton : {
						backgroundColor : '#000000',
						backgroundAlpha : 0.15
					},
					dataProvider : mergedData,
					categoryField : "time",
					// AXES	
					categoryAxis : {
						dashLength : 1,
						gridAlpha : 0.15,
						axisColor : "#DADADA",
						title : "time [step]"
					},
					// value                
					valueAxes : [{
						axisColor : "#DADADA",
						dashLength : 0,
						labelsEnabled : true,
						title : "concentration [molecules/volume]"
					}],
					// graphs
					graphs : graphs,
					// export
					exportConfig : self.getExportConfig(self),
					
					// legend
					legend : legend,
					
					// CURSOR
					chartCursor : {
						cursorPosition : "mouse"
					},
					// SCROLLBAR
					chartScrollbar : {}
				});
				self.currentData = chartData;
				MainModel.goToElement(outerId);
			},
			contentType : 'application/json',
			type : 'POST'
		}).fail(function(xhr) {
			MainModel.displayErrorAlert(xhr.responseText);
		});
	}
};

$(document).ready(function() {
	ko.components.register('chart', {
	    template: {require: 'text!components/shared/chart/template.html'},
	    viewModel: ChartModel
	});
});