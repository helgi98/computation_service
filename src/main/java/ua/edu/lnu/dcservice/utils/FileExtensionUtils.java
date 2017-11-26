package ua.edu.lnu.dcservice.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileExtensionUtils {
    private FileExtensionUtils() {
        throw new IllegalArgumentException();
    }

    public static boolean isImage(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        return extension.equals("jpg") || extension.equals("jpeg")
                || extension.equals("mpeg") || extension.equals("png")
                || extension.equals("bmp") || extension.equals("svg")
                || extension.equals("gif") || extension.equals("tiff");
    }

    public static boolean isTextDocument(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        return extension.equals("txt");
    }
}
