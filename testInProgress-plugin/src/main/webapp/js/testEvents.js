var TestRun = (function($) {

	TestRun.index = 0;

	function TestRun(elementId, runId) {
		console.log("Test run : " + runId);
		TestRun.index++;
		this.tree = null;
		this.runId = runId;
		this.treeEvents = [];
		this.elementId = elementId;
		this.testCount = 0;
		this.testStarted = 0;
		this.testIgnored = 0;
		this.testEnded = 0;
		this.errors = 0;
		this.failures = 0;
		this.testMessageId = "testMessage-" + TestRun.index;
		this.progressId = "progress-" + TestRun.index;
		this.treeId = "tree-" + TestRun.index;
		this.runsId = "runs-" + TestRun.index;
		this.errorsId = "errors-" + TestRun.index;
		this.failuresId = "failures-" + TestRun.index;
		this.panelStackTraceId = "panel-stackTrace-" + TestRun.index;
		this.stackTraceId = "stackTrace-" + TestRun.index;
		$('#' + this.elementId).html(
				"<div class='testpanel'>" + "<fieldset><legend>" + runId
						+ "</legend>" + "<fieldset>" + "<div id='"
						+ this.testMessageId + "'></div>"
						+ "Runs : <span class='stat' id='" + this.runsId
						+ "'></span>" + "Errors: <span class='stat' id='"
						+ this.errorsId + "'></span>"
						+ "Failures : <span class='stat' id='"
						+ this.failuresId + "'></span><div id='"
						+ this.progressId + "'></div></fieldset>"
						+ "<fieldset class='stacktrace' id='"
						+ this.panelStackTraceId
						+ "'><legend>Stacktrace</legend><div id='"
						+ this.stackTraceId + "'></div></fieldset>"
						+ "<div id='" + this.treeId
						+ "' class='ztree'></div></fieldset></div>");
	}

	TestRun.prototype = {

		handleTestEvents : function(events) {
			if (this.tree == null) {
				this.treeWillBeRefreshed = true;
			} else {
				this.treeWillBeRefreshed = false;
			}
			for ( var i = 0; i < events.length; i++) {
				this.handleTestEvent(events[i]);
			}
			if (this.tree != null && this.treeWillBeRefreshed == true) {
				this.tree.refresh();
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
			$("#" + this.runsId).text(runs);
			$("#" + this.errorsId).text(this.errors);
			$("#" + this.failuresId).text(this.failures);
		},
		setMessage : function(message) {
			$("#" + this.testMessageId).text(message);
		},
		handleRunStartEvent : function(event) {
			this.testCount = event.testCount;
			$("#" + this.progressId).progressbar({
				value : 0,
				max : this.testCount
			});
			$("#" + this.progressId + " > div").css({
				'background' : 'green'
			});
		},
		handleRunStopEvent : function(event) {
			this.setMessage("Finished after "
					+ (event.elapsedTime / 1000).toFixed(2) + " seconds");
			if (this.errors == 0 && this.failures == 0) {
				$('#' + this.elementId+ "> .testpanel > fieldset legend").addClass("successSuite");
			}
		},
		handleTestStartEvent : function(event) {
			this.testStarted++;
			if (event.ignored) {
				this.testIgnored++;
			}
			if (this.tree == null) {
				this.createTreeView();
			}
			var node = this.getNodeByTestId(event.testId);
			this.updateNodeIcon(node, "testRun");
			this.updateParentNode(node);
			this.expandParent(node);
		},
		updateNodeIcon : function(node, iconSkin) {
			node.iconSkin = iconSkin;
			if (this.treeWillBeRefreshed == false) {
				this.tree.updateNode(node);
			}
		},
		expandParent : function(node) {
			var parent = node.getParentNode();
			while (parent != null && parent.open == false) {
				if (this.treeWillBeRefreshed == true) {
					parent.open = true;
				} else {
					this.tree.expandNode(parent, true, false, false, false);
				}
				parent = parent.getParentNode();
			}
		},
		collapseParentIfPassed : function(node) {
			var parent = node.getParentNode();
			while (parent != null) {
				if (parent.iconSkin == "testSuitePassed") {
					if (this.treeWillBeRefreshed == true) {
						parent.open = false;
					} else {
						this.tree
								.expandNode(parent, false, false, false, false);
					}
				} else {
					break;
				}
				parent = parent.getParentNode();
			}
		},
		handleTestFailedEvent : function(event) {
			this.failures++;
			var node = this.getNodeByTestId(event.testId);
			this.updateNodeIcon(node, "testFailed");
			// node.trace = event.trace;
			$("#" + this.progressId + " > div").css({
				'background' : 'darkred'
			});
			$('#' + this.elementId+ "> .testpanel > fieldset legend").addClass("errorSuite");
		},
		handleTestErrorEvent : function(event) {
			this.errors++;
			var node = this.getNodeByTestId(event.testId);
			this.updateNodeIcon(node, "testError");
			// node.trace = event.trace;
			$("#" + this.progressId + " > div").css({
				'background' : 'darkred'
			});
			$('#' + this.elementId+ "> .testpanel > fieldset legend").addClass("errorSuite");
		},
		handleTestEndEvent : function(event) {
			this.testEnded++;
			var node = this.getNodeByTestId(event.testId);
			if (node.iconSkin == "testRun") {
				if (event.ignored) {
					this.updateNodeIcon(node, "testIgnored");
				} else {
					this.updateNodeIcon(node, "testPassed");
				}
			}
			$("#" + this.progressId).progressbar("value", this.testEnded);
			this.updateParentNode(node);
			this.collapseParentIfPassed(node);
		},
		updateParentNode : function(childNode) {
			var parentNode = childNode.getParentNode();
			if (parentNode == null) {
				return;
			}
			var parentStyle = "testSuite";
			for ( var i = 0; i < parentNode.children.length; i++) {
				var childNode = parentNode.children[i];
				if (childNode.iconSkin == "testRun"
						|| childNode.iconSkin == "testSuiteRun") {
					parentStyle = "testSuiteRun";
					break;
				}
				if (parentStyle == "testSuite") {
					if (childNode.iconSkin == "testIgnored"
							|| childNode.iconSkin == "testPassed"
							|| childNode.iconSkin == "testSuitePassed") {
						parentStyle = "testSuitePassed";
					}
				}
				if (childNode.iconSkin == "testFailed"
						|| childNode.iconSkin == "testError"
						|| childNode.iconSkin == "testSuiteFailed") {
					parentStyle = "testSuiteFailed";
				}
			}
			this.updateNodeIcon(parentNode, parentStyle);
			this.updateParentNode(parentNode);
		},
		createTreeNodes : function(treeEvents) {
			var eventIndex = 0;
			var testRun = this;
			function createTreeNode() {
				var event = treeEvents[eventIndex];
				if (event.suite == false) {
					var testName = testRun.getShortTestName(event);
					var newNode = {
						id : event.testId,
						name : testName,
						iconSkin : "test"
					};
					eventIndex++;
					return newNode;
				} else {
					var newNode = {
						id : event.testId,
						name : event.testName,
						iconSkin : "testSuite",
						children : []
					};
					eventIndex++;
					for ( var i = 0; i < event.testCount; i++) {
						newNode.children.push(createTreeNode());
					}
					return newNode;
				}
			}
			return [ createTreeNode() ];
		},
		createTreeView : function() {
			var treeNodes = this.createTreeNodes(this.treeEvents);
			$.fn.zTree.init($("#" + this.treeId), {}, treeNodes);
			this.tree = $.fn.zTree.getZTreeObj(this.treeId);
		},
		getNodeByTestId : function(testId) {
			var node = this.tree.getNodeByParam("id", testId, null);
			return node;
		},
		getShortTestName : function(event) {
			var index = event.testName.indexOf('(');
			if (index == -1) {
				return;
			}
			return event.testName.slice(0, index);
		}
	};

	return TestRun;
}(jQuery));

var TestRuns = (function($) {

	function TestRuns(elementId) {
		this.elementId = elementId;
		this.eventsCount = 0;
		this.testRuns = [];
		this.index = 0;
	}

	TestRuns.prototype = {
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
		handleTestEvents : function(buildEvents) {
			var runIdToRunEvents = {};
			for ( var i = 0; i < buildEvents.length; i++) {
				var buildEvent = buildEvents[i];
				if (runIdToRunEvents[buildEvent.runId] == null) {
					runIdToRunEvents[buildEvent.runId] = [];
				}
				runIdToRunEvents[buildEvent.runId]
						.push(buildEvent.runTestEvent);
			}
			for ( var runId in runIdToRunEvents) {
				var testRun = this.testRuns[runId];

				if (testRun == null) {
					this.index++;
					$('#' + this.elementId).append(
							"<div id='runId-" + this.index + "'></div>");
					testRun = new TestRun("runId-" + this.index, runId);
					this.testRuns[runId] = testRun;
				}
				testRun.handleTestEvents(runIdToRunEvents[runId]);
			}
		}
	};

	return TestRuns;
}(jQuery));