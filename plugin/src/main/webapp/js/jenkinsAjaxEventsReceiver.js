/*
 * Test Events Receiver
 * 
 */
var JenkinsAjaxEventsReceiver = (function($) {

	function JenkinsAjaxEventsReceiver(testRuns) {
		this.testRuns = testRuns;
		this.index = 0;
		this.time1 = 0;
		this.time2 = 0;
		this.time3 = 0;
		this.building = true;
	}

	JenkinsAjaxEventsReceiver.prototype = {
		start : function() {
			this.handlePreviousBuildTestEvents($.proxy(this.handleCurrentBuildTestEvents,this));
		},
		handlePreviousBuildTestEvents: function(previousBuildTestEventsHandled) {
			var handler = $.proxy(function(t) {
				this.time2 = (new Date()).getTime();
				console.log("Time to get events : " + (this.time2 - this.time1));
				var buildTestEventsList = t.responseObject();
				this.testRuns.handlePreviousBuildTestEvents(buildTestEventsList.buildTestEvents);
				this.time3 = (new Date()).getTime();
				console.log("Time to handle events : " + (this.time3 - this.time2));
				previousBuildTestEventsHandled();
			}, this);
			var callFunction = $.proxy(function() {
				this.time1 = (new Date()).getTime();
				remoteAction.getPreviousTestEvents(handler);
			}, this);
			callFunction();
		},
		handleCurrentBuildTestEvents: function() {
			var handler = $.proxy(function(t) {
				this.time2 = (new Date()).getTime();
				console.log("Time to get events : " + (this.time2 - this.time1));
				var buildTestEventsList = t.responseObject();
				this.building = buildTestEventsList.building;
				this.testRuns.handleTestEvents(buildTestEventsList.buildTestEvents);
				this.index += buildTestEventsList.buildTestEvents.length;
				this.time3 = (new Date()).getTime();
				console.log("Time to handle events : " + (this.time3 - this.time2));
				// call the function again in 2 seconds
				if (this.building) {
					setTimeout(callFunction, 2000);
				}
			}, this);
			var callFunction = $.proxy(function() {
				this.time1 = (new Date()).getTime();
				remoteAction.getTestEvents(this.index,handler);
			}, this);
			callFunction();
		}
	};

	return JenkinsAjaxEventsReceiver;
}(jQuery));