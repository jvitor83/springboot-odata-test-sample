package com.hackerrank.projects.controllers;

import java.util.List;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.FeedMetadata;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ODataResponse<T> {
	@JsonInclude(Include.NON_NULL)
	@JsonProperty("@odata.count")
	Long Count;
	
	@JsonProperty("value")
	T Value;

}
