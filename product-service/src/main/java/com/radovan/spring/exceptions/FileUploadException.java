package com.radovan.spring.exceptions;

import javax.management.RuntimeErrorException;

public class FileUploadException extends RuntimeErrorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileUploadException(Error e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

}
