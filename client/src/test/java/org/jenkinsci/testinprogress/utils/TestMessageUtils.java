package org.jenkinsci.testinprogress.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

public class TestMessageUtils {

	public static List<JSONObject> getTestMessagesMatching(JSONObject[] messages, JSONObject expectedMessage, JSONCompareMode jsonCompareMode)  {
		List<JSONObject> result = new ArrayList<JSONObject>();
		
		for (JSONObject message : messages) {
			JSONCompareResult jsonResult = JSONCompare.compareJSON(expectedMessage, message, jsonCompareMode);
			if(jsonResult.passed()){
				result.add(message);
			}
		}
		return result;
	}
	
	public static void assertTestMessageMatches(JSONObject[] messages, JSONObject expectedMessage, JSONCompareMode jsonCompareMode){
		assertEquals(1, getTestMessagesMatching(messages, expectedMessage, jsonCompareMode).size());
	}
	
	public static void printTestMessages(JSONObject[] messages) {
		for (JSONObject message : messages) {
			System.out.println(message.toString());
		}
	}
	
	
}
