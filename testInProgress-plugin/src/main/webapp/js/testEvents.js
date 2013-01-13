var TestRun = Class.create();
TestRun.prototype = {
	initialize : function(elementId, runId) {
		this.runId = runId;
		this.treeEvents = [];
		this.elementId = elementId;
		this.testCount = 0;
		this.testStarted = 0;
		this.testIgnored = 0;
		this.errors = 0;
		this.failures = 0;
		var testMessageId = "testMessage-" + this.runId;
		var progressId = "progress-" + this.runId;
		var treeId = "tree-" + this.runId;
		var runsId = "runs-" + this.runId;
		var errorsId = "errors-" + this.runId;
		var failuresId = "failures-" + this.runId;
		var panelStackTraceId = "panel-stackTrace-" + this.runId;
		var stackTraceId = "stackTrace-" + this.runId;
		var panelTreeId = "panel-tree-" + this.runId;
		var element = document.getElementById(this.elementId);
		element.innerHTML += "<div class='testpanel'>" + "<fieldset>"
				+ "<fieldset>" + "<div id='"
				+ testMessageId
				+ "'></div>"
				+ "Runs : <span class='stat' id='"
				+ runsId
				+ "'></span>"
				+ "Errors: <span class='stat' id='"
				+ errorsId
				+ "'></span>"
				+ "Failures : <span class='stat' id='"
				+ failuresId
				+ "'></span><div id='"
				+ progressId
				+ "'></div></fieldset>"
				+ "<fieldset class='stacktrace' id='"
				+ panelStackTraceId
				+ "'><legend>Stacktrace</legend><div id='"
				+ stackTraceId
				+ "'></div></fieldset>"
				+ "<fieldset id='"
				+ panelTreeId
				+ "'><div id='"
				+ treeId
				+ "'></div></fieldset></fieldset></div>";
	},
	handleTestEvents : function(events) {
		for ( var i = 0; i < events.length; i++) {
			this.handleTestEvent(events[i]);
		}
	},
	handleTestEvent : function(event) {
		switch (event.type) {
		case "TESTC":
			this.handleRunStartEvent(event);
			break;
		case "TSTTREE":
			this.treeEvents.push(event);
			break;
		case "FAILED":
			this.handleTestFailedEvent(event);
			break;
		case "ERROR":
			this.handleTestErrorEvent(event);
			break;
		case "TESTS":
			this.handleTestStartEvent(event);
			break;
		case "TESTE":
			this.handleTestEndEvent(event);
			break;
		case "RUNTIME":
			this.handleRunStopEvent(event);
			break;
		default:
			break;
		}
		this.updateStats();
	},
	updateStats : function() {
		var runs = this.testStarted + "/" + this.testCount;
		if (this.testIgnored > 0) {
			runs += " (" + this.testIgnored + " ignored)";
		}
		document.getElementById("runs-" + this.runId).innerHTML = runs;
		document.getElementById("errors-" + this.runId).innerHTML = this.errors;
		document.getElementById("failures-" + this.runId).innerHTML = this.failures;
	},
	setMessage : function(message) {
		document.getElementById("testMessage-" + this.runId).innerHTML = message;
	},
	handleRunStartEvent : function(event) {
		this.testCount = event.testCount;
		this.progressBar = new YAHOO.widget.ProgressBar({
			value : 0,
			maxValue : event.testCount,
			width : "500px"
		});
		YAHOO.util.Dom.setStyle(this.progressBar.get('barEl'),
				'backgroundColor', 'green');
		YAHOO.util.Dom.setStyle(this.progressBar.get('barEl'),
				'backgroundImage', 'none');
		this.progressBar.render("progress-" + this.runId);
	},
	handleRunStopEvent : function(event) {
		this.setMessage("Finished after "
				+ (event.elapsedTime / 1000).toFixed(2) + " seconds");
	},
	handleTestStartEvent : function(event) {
		this.testStarted++;
		if (event.ignored) {
			this.testIgnored++;
		}
		if (this.treeView == null) {
			this.createTreeView();
		}
		var node = this.getNodeByTestId(event.testId);
		this.setContentStyle(node, "testRunNode");
		this.updateParentNode(node);
		this.expandParent(node);
	},
	expandParent : function(node) {
		var parent = node.parent;
		while (parent != null) {
			parent.expand();
			parent = parent.parent;
		}
	},
	collapseParentIfPassed : function(node) {
		var parent = node.parent;
		while (parent != null) {
			if (parent.contentStyle == "testSuitePassedNode") {
				parent.collapse();
			} else {
				break;
			}
			parent = parent.parent;
		}
	},
	handleTestFailedEvent : function(event) {
		this.failures++;
		var node = this.getNodeByTestId(event.testId);
		this.setContentStyle(node, "testFailedNode");
		node.trace = event.trace;
		YAHOO.util.Dom.setStyle(this.progressBar.get('barEl'),
				'backgroundColor', 'darkred');
	},
	handleTestErrorEvent : function(event) {
		this.errors++;
		var node = this.getNodeByTestId(event.testId);
		this.setContentStyle(node, "testErrorNode");
		node.trace = event.trace;
		YAHOO.util.Dom.setStyle(this.progressBar.get('barEl'),
				'backgroundColor', 'darkred');
	},
	handleTestEndEvent : function(event) {
		var node = this.getNodeByTestId(event.testId);
		if (node.contentStyle == "testRunNode") {
			if (event.ignored) {
				this.setContentStyle(node, "testIgnoredNode");
			} else {
				this.setContentStyle(node, "testPassedNode");
			}
		}
		this.progressBar.set('value', this.progressBar.get('value') + 1);
		this.updateParentNode(node);
		this.collapseParentIfPassed(node);
	},
	updateParentNode : function(childNode) {
		var parentNode = childNode.parent;
		if (parentNode == null) {
			return;
		}
		var parentStyle = "testSuiteNode";
		for ( var i = 0; i < parentNode.children.length; i++) {
			var childNode = parentNode.children[i];
			if (childNode.contentStyle == "testRunNode"
					|| childNode.contentStyle == "testSuiteRunNode") {
				parentStyle = "testSuiteRunNode";
				break;
			}
			if (parentStyle == "testSuiteNode") {
				if (childNode.contentStyle == "testIgnoredNode"
						|| childNode.contentStyle == "testPassedNode"
						|| childNode.contentStyle == "testSuitePassedNode") {
					parentStyle = "testSuitePassedNode";
				}
			}
			if (childNode.contentStyle == "testFailedNode"
					|| childNode.contentStyle == "testErrorNode"
					|| childNode.contentStyle == "testSuiteFailedNode") {
				parentStyle = "testSuiteFailedNode";
			}
		}
		this.setContentStyle(parentNode, parentStyle);
		this.updateParentNode(parentNode);
	},
	createTreeView : function() {
		this.treeView = new YAHOO.widget.TreeView("tree-" + this.runId);
		if (this.treeEvents.length > 0) {
			this.createTreeElement(this.treeView.getRoot(), 0);
		}
		var $this = this;
		this.treeView
				.subscribe(
						"clickEvent",
						function(event) {
							var panelStackTrace = document
									.getElementById("panel-stackTrace-"
											+ $this.runId);
							panelStackTrace.style.display = "block";
							// panelStackTrace.style.top =
							// YAHOO.util.Dom.getY(document.getElementById(event.node.contentElId));
							var panelTree = document
									.getElementById("panel-tree-" + $this.runId);
							panelStackTrace.style.left = YAHOO.util.Dom
									.getX(panelTree)
									+ panelTree.offsetWidth / 2;
							var left = YAHOO.util.Dom.getX(panelTree)
									+ panelTree.offsetWidth / 2;
							document
									.getElementById("stackTrace-" + $this.runId).innerHTML = event.node.trace;

							var myAnim = new YAHOO.util.Anim(
									"panel-stackTrace-" + $this.runId,
									{
										top : {
											to : YAHOO.util.Dom
													.getY(document
															.getElementById(event.node.contentElId))
										}
									}, 1, YAHOO.util.Easing.easeOut);
							myAnim.animate();

						});
		this.treeView.render();
	},
	createTreeElement : function(parent, eventIndex) {
		var event = this.treeEvents[eventIndex];
		if (event.suite == false) {
			var testName = this.getShortTestName(event);
			var node = new YAHOO.widget.HTMLNode({
				html : "<span class='htmlnodelabel'>" + testName + "</span>",
				expanded : false
			}, parent);
			node.testId = event.testId;
			node.contentStyle = "testNode";
			eventIndex++;
			return eventIndex;
		} else {
			var node = new YAHOO.widget.HTMLNode({
				html : "<span class='htmlnodelabel'>" + event.testName
						+ "</span>",
				expanded : false
			}, parent);
			node.testId = event.testId;
			node.contentStyle = "testSuiteNode";
			eventIndex++;
			for ( var i = 0; i < event.testCount; i++) {
				eventIndex = this.createTreeElement(node, eventIndex);
			}
			return eventIndex;
		}
	},
	getNodeByTestId : function(testId) {
		var node = this.treeView.getNodeByProperty("testId", testId);
		return node;
	},
	getShortTestName : function(event) {
		var index = event.testName.indexOf('(');
		if (index == -1) {
			return;
		}
		return event.testName.slice(0, index);
	},
	setContentStyle : function(node, contentStyle) {
		node.contentStyle = contentStyle;
		var el = node.getContentEl();
		if (el) {
			el.className = "ygtvcell " + node.contentStyle + " ygtvcontent";
		}
	}
};

var TestRuns = Class.create();

TestRuns.prototype = {
	initialize : function(elementId) {
		this.elementId = elementId;
		this.eventsCount = 0;
		this.testRuns = [];
	},
	start : function(event) {
		var $this = this;
		(function() {
			$this.handleNextTestEvents();
			// call the function again in 2 seconds
			setTimeout(arguments.callee, 2000);
		})();
	},
	handleNextTestEvents : function() {
		var $this = this;
		remoteAction.getTestEvents(this.eventsCount, function(t) {
			var testEvents = t.responseObject();
			$this.handleTestEvents(testEvents);
			$this.eventsCount += testEvents.length;
		});
	},
	handleTestEvents : function(events) {
		var runIdToEvents = {};
		for ( var i = 0; i < events.length; i++) {
			var event = events[i];
			if (runIdToEvents[event.runId] == null) {
				runIdToEvents[event.runId] = [];
			}
			runIdToEvents[event.runId].push(event.runTestEvent);
		}
		for ( var runId in runIdToEvents) {
			var testRun = this.testRuns[runId];
			if (testRun == null) {
				document.getElementById(this.elementId).innerHTML += "<div id='runId-"
						+ runId + "'></div>";
				testRun = new TestRun("runId-" + runId, runId);
				this.testRuns[runId] = testRun;
			}
			testRun.handleTestEvents(runIdToEvents[runId]);
		}
	}
}
