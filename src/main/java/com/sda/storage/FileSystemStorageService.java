package com.sda.storage;

import com.sda.entity.Picture;
import com.sda.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class  FileSystemStorageService implements StorageService {


	@Autowired
	private PictureService pictureService;

	private final Path rootLocation;
	private String nameLastFile;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}


	@Override
	public void store(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		if (file.isEmpty()) {
			throw new StorageException("Failed to store empty file " + filename);
		}
		if (filename.contains("..")) {
			// This is a security check
			throw new StorageException(
					"Cannot store file with relative path outside current directory "
							+ filename);
		}

		//Save File -------------------
		try (InputStream inputStream = file.getInputStream()) {
			Picture picture = new Picture();
			picture.setFileName(filename);
			pictureService.savePicture(picture);
			Files.copy(inputStream, this.rootLocation.resolve(picture.getId()+"_"+filename), //filename
				StandardCopyOption.REPLACE_EXISTING);
			picture.setFileName(picture.getId()+"_"+picture.getFileName());
			pictureService.savePicture(picture);

			FtpService ftpService = null;
			ftpService = new FtpService();
			System.out.println("Upload start!");
			ftpService.uploadFileToFTP(new File(String.valueOf(rootLocation.resolve(picture.getId()+"_"+filename))), picture);
			System.out.println("Upload done!");

			nameLastFile=picture.getFileName();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getNameLastStorageFile(){
		return nameLastFile;
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);

			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);
			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
