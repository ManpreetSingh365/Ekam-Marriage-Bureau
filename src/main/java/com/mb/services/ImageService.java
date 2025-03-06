package com.mb.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

//    String uploadImage(MultipartFile contactImage, String filename);

    String getUrlFromPublicId(String publicId);
    
	public List<String> uploadImages(List<MultipartFile> userImages, String filename);

	List<String> getImagesByPublicIds(List<String> imagesList);

    
}
