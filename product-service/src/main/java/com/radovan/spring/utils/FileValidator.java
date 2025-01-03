package com.radovan.spring.utils;

import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.radovan.spring.exceptions.DataNotValidatedException;

@Component
public class FileValidator {

	public Boolean validateFile(MultipartFile file) {

		Boolean returnValue = false;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());

		if (isSupportedExtension(extension)) {
			returnValue = true;
		} else {
			Error error = new Error("The file is not valid!");
			throw new DataNotValidatedException(error);
		}

		return returnValue;
	}

	private Boolean isSupportedExtension(String extension) {
		Boolean returnValue = false;
		Optional<String> extensionOptional = Optional.ofNullable(extension);
		if (extensionOptional.isPresent()) {
			if (extension.equals("png") || extension.equals("jpeg") || extension.equals("jpg")) {
				returnValue = true;
			}
		}
		return returnValue;
	}
}
