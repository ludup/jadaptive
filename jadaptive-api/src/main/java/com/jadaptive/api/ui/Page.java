package com.jadaptive.api.ui;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pf4j.ExtensionPoint;

public interface Page extends ExtensionPoint {

	default void doGet(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException {
		throw new UnsupportedOperationException();
	}

	default void doPost(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException {
		throw new UnsupportedOperationException();
	}

	String getUri();

	String getResource();
}