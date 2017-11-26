package ua.edu.lnu.dcservice.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileService {
    String saveFile(String directory, MultipartFile multipartFile);

    String saveFile(String directoryName, String fileName, MultipartFile multipartFile);

    File getFile(String directoryName, String fileName);

    File createFile(String directoryName, String fileName);
}
