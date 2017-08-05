var GraphModel = function(params) {
	
	var self = this;
	
	self.containerId = params.view+"_graph";
	
	self.colors = ko.observable().publishOn(params.view+"_colors", true, function(val, old){return false;});
	self.colors.extend({ notify: 'always' });
	self.graph = ko.observable().publishOn(params.view+"_graph", true, function(val, old){return false;});
	self.graph.extend({ notify: 'always' });

	
	self.hasStructuralChange = ko.observable().subscribeTo(params.view+"_hasStructuralChange", true);
	ko.postbox.subscribe(params.view+"_system", function(system) {
		self.loadGraph(system);
	});
	
	self.loadGraph = function(system) {
		if (!self.hasStructuralChange()) {
			// no need to reload graph!
			return ;
		}
		$.ajax({
			  url: "api/system/graph",
			  type: "POST",
			  data: system,
			  contentType: "application/json",
			  success: function(graph){
				  
				  self.colors(self.generateColors(graph.products));
				  self.graph(graph);
				  self.createGraph();
			  },
			  error: function(xhr){
				  MainModel.displayErrorAlert(xhr.status == 400 ? xhr.responseText : "An unexpected error has occurred. Could not load graph.");
			  }
		});
	};
	
	self.generateColors = function(products) {
		var colorGen = new RColor;
		var colors = [];
		for (var i=0; i < products.length; i++) {
			colors[products[i]] = colorGen.get(true);
		}
		return colors;
	};
	
	self.show = function() {
		$('#'+self.containerId).collapse('show');
	};
	
	self.remove = function() {
		$('#'+self.containerId+' svg').remove();
	};
		
	self.graphConfig = {
		width : 1000,
		height : 400,
		distance : function(d) {return d.internal ? "175" : "205"},
		charge : -500,
		gravity : 0.01,
		linkClass : function(d) {return d.fromReceptor ? "receptor" : "effector"},
		linkStyle : function(d) {
				var color = "#799FF3";
    			if (d.fromReceptor) {
    				color = "#F38279;";
    			} else if (self.colors()[d.label] != null) {
    				color = self.colors()[d.label]; 
    			}
    			return "stroke: "+color;
		},
		linkMarker : function(d) {return d.fromReceptor ? "url(#receptor-marker)" : "url(#effector-marker)"},
		markerClass : function(d) {return d == "receptor-marker" ? "receptor" : "effector"},
		nodeImage : function(d) {return d.source ? "images/source.svg" : d.receptor ? "images/receptor.svg" : "images/effector.svg"},
		nodeClass : function(d) {return "node"},
		nodeLabel : function(d) {
			if (d.source || !d.receptor) {
				return "";
			}
			return (d.name.indexOf("-receptor") == -1 ? d.name : d.name.substring(0,d.name.indexOf("-receptor")));
		}
	};
	
	self.createGraph = function() {
		
		var graph = self.graph();
		var colors = self.colors();
		var config = self.graphConfig;
		
		self.show();
		self.remove();

		var svg = d3.select("#"+self.containerId)
			.append("svg")
		    	.attr("width", config.width)
		    	.attr("height", config.height);
		
		self.createMarkerDefinitions(svg, config);
	
		var force = d3.layout.force()
	    	.nodes(d3.values(graph.nodes))
	    	.links(graph.links)
	    	.size([config.width, config.height])
		    .linkDistance(config.distance)
		    .charge(config.charge)
		    .gravity(config.gravity)
		    .on("tick", tick)
		    .start();

		var drag = force.drag().on("dragstart", dragstart);
	
		var path = svg.append("g").selectAll("path")
		    .data(force.links()).enter()
		    	.append("path")
		    		.attr("id", function(d, i) {return params.view+"_id-"+i;})
		    		.attr("class", config.linkClass)
		    		.attr("style", config.linkStyle)
				    .attr("marker-end", config.linkMarker);
		
		var linktext = svg.append("g").selectAll("g.linklabelholder").data(force.links());
		linktext.enter()
				.append("g")
					.attr("class", "linklabelholder")
					.append("text")
						.attr("dx", "1")
						.attr("dy", "-5")
						.attr("text-anchor", "start")
							.append("textPath")
								.attr("xlink:href",function(d,i) { return "#"+params.view+"_id-" + i;})
								.text(function(d) {return d.label;});

		var node = svg.selectAll(".node")
	    	.data(force.nodes()).enter()
	    		.append("g")
	    		.on("dblclick", dblclick)
	    		.call(drag);

		node.append("image")
		    .attr("xlink:href", config.nodeImage)
		    .attr("class", config.nodeClass)
		    .attr("x", -25)
		    .attr("y", -25)
		    .attr("width", 50)
		    .attr("height", 50);
	
	
		var text = svg.append("g").selectAll("text")
		    .data(force.nodes()).enter()
		    	.append("text")
				    .attr("x", 0)
				    .attr("y", -21)
				    .text(config.nodeLabel);
	
		function tick() {
			
			node.each(function(d, i) {
				d.x = Math.max(30, Math.min(config.width - 30, d.x));
				d.y = Math.max(30, Math.min(config.height - 30, d.y));
			});
			
			linktext.select("text").attr("dx", linkStart);
			
			path.attr("d", linkArc);
			node.attr("transform", transform);
			text.attr("transform", transform);
		}
		
		function linkStart(d) {
			var dx = d.target.x - d.source.x,
		    dy = d.target.y - d.source.y,
		    dr = Math.sqrt(dx * dx + dy * dy);
			return dr/3;
		}
	
		function dblclick(d) {
			d3.select(this).classed("fixed", d.fixed = false);
		}

		function dragstart(d) {
			d3.select(this).classed("fixed", d.fixed = true);
		}
		
		function linkArc(d) {
			var dx = d.target.x - d.source.x,
			    dy = d.target.y - d.source.y,
			    dr = Math.sqrt(dx * dx + dy * dy);
			return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
		}

		function transform(d) {
			return "translate(" + d.x + "," + d.y + ")";
		}
	};
	
	self.createMarkerDefinitions = function(svg, config) {
		svg.append("defs").selectAll("marker")
	    	.data(["receptor-marker", "effector-marker"]).enter()
	    		.append("marker")
		    		.attr("id", function(d) {return d;})
		    		.attr("refX", 35)
		    		.attr("refY", 2)
		    		.attr("markerWidth", 10)
		    		.attr("markerHeight", 10)
		    		.attr("orient", "auto")
		    		.attr("class", config.markerClass)
	    		.append("path")
	    			.attr("d", "M0,0 L10,5 0,10");
	};
};

$(document).ready(function() {
	ko.components.register('graph', {
	    template : {require: 'text!components/shared/graph/template.html'},
	    viewModel : GraphModel
	});
});