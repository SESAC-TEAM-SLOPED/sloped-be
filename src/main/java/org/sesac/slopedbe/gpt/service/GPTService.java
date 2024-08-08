package org.sesac.slopedbe.gpt.service;

import java.io.IOException;

public interface GPTService {

	String sendMessage(String message) throws IOException;

	/**
	 * Sends an image file and a message to an external API.
	 *
	 * @param imageFile The image file to be sent.
	 * @param message The message to be sent along with the image.
	 * @return The response from the API.
	 * @throws IOException If there is an error reading the file or making the request.
	 */
	String sendImageWithMessage(String imageUrl, String message) throws IOException;
}
