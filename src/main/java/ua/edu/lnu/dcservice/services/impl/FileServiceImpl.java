package ua.edu.lnu.dcservice.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.lnu.dcservice.exceptions.ServiceException;
import ua.edu.lnu.dcservice.services.FileService;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final String PATH_CONCAT_TEMPLATE = "%s/%s";

    @Value("${file.storage.path}")
    private String path;

    @Override
    public String saveFile(String directoryName, MultipartFile multipartFile) {
        String fileName = createNewFileName(multipartFile.getOriginalFilename());

        return saveFile(directoryName, fileName, multipartFile);
    }

    @Override
    public String saveFile(String directoryName, String fileName, MultipartFile multipartFile) {
        File directory = new File(String.format(PATH_CONCAT_TEMPLATE, path, directoryName));

        if (!directory.exists() && !directory.mkdir()) {
            log.debug("Directory {} doesn't exist and cannot be created", directory.getAbsolutePath());
            throw new ServiceException("Directory doesn't exist");
        }

        String relativePath = String.format(PATH_CONCAT_TEMPLATE, directoryName, fileName);
        String fullPath = String.format(PATH_CONCAT_TEMPLATE, path, relativePath);

        try {
            multipartFile.transferTo(new File(fullPath));
        } catch (IOException ioex) {
            log.error("Couldn't save file to {}.", directory.getAbsolutePath());
            log.error("{}", ioex);
            throw new ServiceException("Cannot save file");
        }

        return relativePath;
    }

    @Override
    public File getFile(String directoryName, String fileName) {
        return new File(String.format(
                PATH_CONCAT_TEMPLATE,
                String.format(PATH_CONCAT_TEMPLATE, path, directoryName),
                fileName));
    }

    @Override
    public File createFile(String directoryName, String fileName) {
        File directory = new File(String.format(PATH_CONCAT_TEMPLATE, path, directoryName));

        if (!directory.exists() && !directory.mkdir()) {
            log.debug("Directory {} doesn't exist and cannot be created", directory.getAbsolutePath());
            throw new ServiceException("Directory doesn't exist");
        }

        String relativePath = String.format(PATH_CONCAT_TEMPLATE, directoryName, fileName);
        File file = new File(String.format(PATH_CONCAT_TEMPLATE, path, relativePath));

        try {
            if (!file.createNewFile()) {
                log.debug("File {}/{} already exsits", directoryName, fileName);
                throw new ServiceException("File with such taskName already exists");
            }
        } catch (IOException ioex) {
            log.error("Failed to create new file, {}", ioex);
            throw new ServiceException("Failed to create new file");
        }

        return file;
    }

    private String createNewFileName(String originalName) {
        return String.format("%d_%s", System.currentTimeMillis(), originalName);
    }
}
