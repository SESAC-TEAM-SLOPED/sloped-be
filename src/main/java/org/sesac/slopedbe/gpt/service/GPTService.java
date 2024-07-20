package org.sesac.slopedbe.gpt.service;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

public interface GPTService {
	/**
	 * Sends an image file and a message to an external API.
	 *
	 * @param imageFile The image file to be sent.
	 * @param message The message to be sent along with the image.
	 * @return The response from the API.
	 * @throws IOException If there is an error reading the file or making the request.
	 */
	JsonNode sendImageWithMessage(String imageUrl, String message) throws IOException;
}
