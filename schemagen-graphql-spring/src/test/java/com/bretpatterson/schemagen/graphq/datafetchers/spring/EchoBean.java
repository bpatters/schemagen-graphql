package com.bretpatterson.schemagen.graphq.datafetchers.spring;

/**
 * Created by bpatterson on 3/1/16.
 */
public class EchoBean {
	public String echo(Object... params) {
		StringBuilder sb = new StringBuilder();
		for (Object param : params) {
			sb.append(":");
			sb.append(param.toString());
		}

		return sb.toString();
	}
}
